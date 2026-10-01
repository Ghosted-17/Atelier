package com.example.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material.icons.outlined.Checkroom
import androidx.compose.material.icons.outlined.Collections
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.BrandSearchModalBottomSheet
import com.example.ui.screens.ColourStudioScreen
import com.example.ui.screens.FitCheckScreen
import com.example.ui.screens.FitVaultScreen
import com.example.ui.screens.StyleFeedScreen
import com.example.ui.screens.StylistAIScreen
import com.example.ui.theme.AtelierTheme
import com.example.ui.theme.MatteGold
import com.example.viewmodel.AtelierViewModel

enum class AtelierDestination(
  val route: String,
  val label: String,
  val selectedIcon: ImageVector,
  val unselectedIcon: ImageVector
) {
  FIT_CHECK("fit_check", "Fit Check", Icons.Filled.Checkroom, Icons.Outlined.Checkroom),
  STUDIO("studio", "Colour Studio", Icons.Filled.Palette, Icons.Outlined.Palette),
  FEED("feed", "Style Feed", Icons.Filled.Collections, Icons.Outlined.Collections),
  VAULT("vault", "FitVault", Icons.Filled.Bookmarks, Icons.Outlined.Bookmarks),
  STYLIST("stylist", "Stylist AI", Icons.Filled.AutoAwesome, Icons.Outlined.AutoAwesome)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AtelierApp(viewModel: AtelierViewModel) {
  val isDark by viewModel.isDarkMode.collectAsState()
  val navController = rememberNavController()
  val navBackStackEntry by navController.currentBackStackEntryAsState()
  val currentRoute = navBackStackEntry?.destination?.route ?: AtelierDestination.FIT_CHECK.route

  var showBrandSearchSheet by remember { mutableStateOf(false) }

  AtelierTheme(darkTheme = isDark) {
    Scaffold(
      contentWindowInsets = WindowInsets.safeDrawing,
      modifier = Modifier.fillMaxSize(),
      topBar = {
        CenterAlignedTopAppBar(
          title = {
            Text(
              text = "ATELIER",
              style = MaterialTheme.typography.titleLarge,
              fontFamily = FontFamily.Serif,
              fontWeight = FontWeight.Bold,
              letterSpacing = 3.sp,
              color = MaterialTheme.colorScheme.onBackground
            )
          },
          actions = {
            // Brand Search Space Launcher Button
            Surface(
              shape = CircleShape,
              color = MaterialTheme.colorScheme.surfaceVariant,
              border = BorderStroke(1.dp, MatteGold.copy(alpha = 0.5f)),
              modifier = Modifier.padding(end = 4.dp)
            ) {
              IconButton(
                onClick = { showBrandSearchSheet = true },
                modifier = Modifier
                  .size(38.dp)
                  .testTag("topbar_brand_search_button")
              ) {
                Icon(
                  imageVector = Icons.Default.Search,
                  contentDescription = "Search Luxury Brands",
                  tint = MatteGold,
                  modifier = Modifier.size(18.dp)
                )
              }
            }

            // Theme Light / Dark Mode Toggle
            Surface(
              shape = CircleShape,
              color = MaterialTheme.colorScheme.surfaceVariant,
              border = BorderStroke(1.dp, MatteGold.copy(alpha = 0.5f)),
              modifier = Modifier.padding(end = 8.dp)
            ) {
              IconButton(
                onClick = { viewModel.toggleDarkMode() },
                modifier = Modifier
                  .size(38.dp)
                  .testTag("theme_toggle_button")
              ) {
                AnimatedContent(
                  targetState = isDark,
                  transitionSpec = { fadeIn() togetherWith fadeOut() },
                  label = "ThemeToggleAnimation"
                ) { targetDark ->
                  Icon(
                    imageVector = if (targetDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = if (targetDark) "Switch to Light Mode" else "Switch to Dark Mode",
                    tint = MatteGold,
                    modifier = Modifier.size(18.dp)
                  )
                }
              }
            }
          },
          colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            scrolledContainerColor = MaterialTheme.colorScheme.surface
          )
        )
      },
      bottomBar = {
        NavigationBar(
          containerColor = MaterialTheme.colorScheme.surface,
          tonalElevation = 8.dp,
          modifier = Modifier.testTag("main_navigation_bar")
        ) {
          AtelierDestination.values().forEach { destination ->
            val isSelected = currentRoute == destination.route
            NavigationBarItem(
              selected = isSelected,
              onClick = {
                if (currentRoute != destination.route) {
                  navController.navigate(destination.route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                      saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                  }
                }
              },
              icon = {
                Icon(
                  imageVector = if (isSelected) destination.selectedIcon else destination.unselectedIcon,
                  contentDescription = destination.label
                )
              },
              label = {
                Text(
                  text = destination.label,
                  style = MaterialTheme.typography.labelSmall,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
              },
              colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.background,
                selectedTextColor = MatteGold,
                indicatorColor = MatteGold,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
              ),
              modifier = Modifier.testTag("nav_item_${destination.route}")
            )
          }
        }
      }
    ) { innerPadding ->
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(innerPadding)
      ) {
        NavHost(
          navController = navController,
          startDestination = AtelierDestination.FIT_CHECK.route,
          modifier = Modifier.fillMaxSize()
        ) {
          composable(AtelierDestination.FIT_CHECK.route) {
            FitCheckScreen(
              viewModel = viewModel,
              onNavigateToStudio = {
                navController.navigate(AtelierDestination.STUDIO.route) {
                  popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                  launchSingleTop = true
                  restoreState = true
                }
              },
              onNavigateToStylist = {
                navController.navigate(AtelierDestination.STYLIST.route) {
                  popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                  launchSingleTop = true
                  restoreState = true
                }
              }
            )
          }

          composable(AtelierDestination.STUDIO.route) {
            ColourStudioScreen(viewModel = viewModel)
          }

          composable(AtelierDestination.FEED.route) {
            StyleFeedScreen(
              viewModel = viewModel,
              onInspectLook = {
                navController.navigate(AtelierDestination.STUDIO.route) {
                  popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                  launchSingleTop = true
                  restoreState = true
                }
              }
            )
          }

          composable(AtelierDestination.VAULT.route) {
            FitVaultScreen(
              viewModel = viewModel,
              onLoadIntoStudio = {
                navController.navigate(AtelierDestination.STUDIO.route) {
                  popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                  launchSingleTop = true
                  restoreState = true
                }
              }
            )
          }

          composable(AtelierDestination.STYLIST.route) {
            StylistAIScreen(
              viewModel = viewModel,
              onApplyRecommendation = {
                navController.navigate(AtelierDestination.STUDIO.route) {
                  popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                  launchSingleTop = true
                  restoreState = true
                }
              }
            )
          }
        }
      }
    }

    // TopAppBar Brand Search Bottom Sheet
    if (showBrandSearchSheet) {
      BrandSearchModalBottomSheet(
        viewModel = viewModel,
        onDismissRequest = { showBrandSearchSheet = false },
        onNavigateToStudio = {
          showBrandSearchSheet = false
          navController.navigate(AtelierDestination.STUDIO.route) {
            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
            launchSingleTop = true
            restoreState = true
          }
        },
        onNavigateToStylist = {
          showBrandSearchSheet = false
          navController.navigate(AtelierDestination.STYLIST.route) {
            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
            launchSingleTop = true
            restoreState = true
          }
        }
      )
    }
  }
}
