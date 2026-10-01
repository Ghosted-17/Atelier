package com.example.ui.screens

import android.annotation.SuppressLint
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.example.model.BodySize
import com.example.model.BrandComboRecommendation
import com.example.model.BrandProfile
import com.example.model.ChatMessage
import com.example.model.ClothingCategory
import com.example.model.ClothingItem
import com.example.model.FeedItem
import com.example.model.FitRatingBreakdown
import com.example.model.Gender
import com.example.model.SavedFit
import com.example.model.SkinTone
import com.example.model.UserFitCheck
import com.example.ui.theme.MatteGold
import com.example.viewmodel.AtelierViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

// ==========================================
// 1. FIT CHECK (LANDING PAGE)
// ==========================================
@Composable
fun FitCheckScreen(
  viewModel: AtelierViewModel,
  onNavigateToStudio: () -> Unit,
  onNavigateToStylist: () -> Unit,
  modifier: Modifier = Modifier
) {
  val savedFits by viewModel.savedFits.collectAsState()
  val feedItems by viewModel.feedItems.collectAsState()
  val currentTop by viewModel.selectedTop.collectAsState()
  val currentBottom by viewModel.selectedBottom.collectAsState()
  val isDark by viewModel.isDarkMode.collectAsState()

  val activeUserFitCheck by viewModel.activeUserFitCheck.collectAsState()
  val isAnalyzingFit by viewModel.isAnalyzingFit.collectAsState()

  // Camera & Gallery Launchers
  val takePictureLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.TakePicturePreview()
  ) { bitmap ->
    if (bitmap != null) {
      viewModel.submitFitPhoto(uri = null, bitmap = bitmap)
    }
  }

  val pickMediaLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { uri ->
    if (uri != null) {
      viewModel.submitFitPhoto(uri = uri, bitmap = null)
    }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
      .padding(horizontal = 20.dp, vertical = 16.dp),
    verticalArrangement = Arrangement.spacedBy(20.dp)
  ) {
    // Editorial Header
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = "ATELIER",
          style = MaterialTheme.typography.displayMedium,
          fontWeight = FontWeight.Bold,
          letterSpacing = 2.sp,
          color = MaterialTheme.colorScheme.onBackground
        )
        Text(
          text = "SARTORIAL LOOKBOOK • EDITION 2026",
          style = MaterialTheme.typography.labelSmall,
          letterSpacing = 1.2.sp,
          color = MatteGold
        )
      }
      Surface(
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, MatteGold),
        color = MaterialTheme.colorScheme.surface
      ) {
        Text(
          text = "PARIS / MILAN",
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
          style = MaterialTheme.typography.labelSmall,
          fontWeight = FontWeight.SemiBold,
          color = MaterialTheme.colorScheme.onSurface
        )
      }
    }

    // Editorial Quote
    Surface(
      shape = RoundedCornerShape(12.dp),
      color = MaterialTheme.colorScheme.surfaceVariant,
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Text(
          text = "“Elegance is not standing out, but being remembered.”",
          style = MaterialTheme.typography.bodyMedium,
          fontFamily = FontFamily.Serif,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "— GIORGIO ARMANI",
          style = MaterialTheme.typography.labelSmall,
          fontWeight = FontWeight.Bold,
          color = MatteGold
        )
      }
    }

    // ==========================================
    // SARTORIAL FIT RATER SECTION
    // ==========================================
    when {
      isAnalyzingFit -> {
        FitAnalyzingCard()
      }
      activeUserFitCheck != null -> {
        FitRaterResultCard(
          fitCheck = activeUserFitCheck!!,
          onSaveToVault = { title -> viewModel.saveUserFitToVault(title) },
          onInjectPaletteToStudio = { palette ->
            viewModel.applyExtractedPaletteToStudio(palette)
            onNavigateToStudio()
          },
          onReset = { viewModel.clearCurrentFitCheck() }
        )
      }
      else -> {
        FitCheckUploadCard(
          onSnapFit = { takePictureLauncher.launch(null) },
          onUploadFit = {
            pickMediaLauncher.launch(
              PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
          }
        )
      }
    }

    // Featured Fit of the Day Card
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(16.dp))
        .border(1.dp, MatteGold.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
        .testTag("featured_fit_card"),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
      Column {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
        ) {
          AsyncImage(
            model = "https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?auto=format&fit=crop&w=1000&q=80",
            contentDescription = "Featured Fit of the Day",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
          )
          // Gradient Scrim
          Box(
            modifier = Modifier
              .fillMaxSize()
              .background(
                Brush.verticalGradient(
                  colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f)),
                  startY = 180f
                )
              )
          )
          // Runway Badge
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = MatteGold,
            modifier = Modifier
              .padding(14.dp)
              .align(Alignment.TopStart)
          ) {
            Text(
              text = "RUNWAY OF THE DAY",
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold,
              color = Color.Black
            )
          }
          // Title & Details on Image
          Column(
            modifier = Modifier
              .align(Alignment.BottomStart)
              .padding(16.dp)
          ) {
            Text(
              text = "The Double-Faced Noir Trench",
              style = MaterialTheme.typography.titleLarge,
              color = Color.White,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "Maison de L'Ombre • Autumn Capsule",
              style = MaterialTheme.typography.bodySmall,
              color = Color.LightGray
            )
          }
        }

        // Action row inside card
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("#111111", "#F5F5F0", "#D4AF37").forEach { hex ->
              Box(
                modifier = Modifier
                  .size(20.dp)
                  .clip(CircleShape)
                  .background(Color(android.graphics.Color.parseColor(hex)))
                  .border(1.dp, MatteGold, CircleShape)
              )
            }
          }
          Button(
            onClick = onNavigateToStudio,
            colors = ButtonDefaults.buttonColors(
              containerColor = MatteGold,
              contentColor = Color.Black
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.testTag("fit_check_studio_button")
          ) {
            Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Inspect in 3D", style = MaterialTheme.typography.labelMedium)
          }
        }
      }
    }

    // Quick Action Launchers
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      Card(
        modifier = Modifier
          .weight(1f)
          .clickable { onNavigateToStudio() }
          .testTag("quick_action_colour_studio"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Icon(
            Icons.Default.Palette,
            contentDescription = null,
            tint = MatteGold,
            modifier = Modifier.size(28.dp)
          )
          Spacer(modifier = Modifier.height(10.dp))
          Text(
            text = "3D Fitting Room",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "Swap gender, sizes & fabrics live",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      Card(
        modifier = Modifier
          .weight(1f)
          .clickable { onNavigateToStylist() }
          .testTag("quick_action_stylist_ai"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Icon(
            Icons.Default.AutoAwesome,
            contentDescription = null,
            tint = MatteGold,
            modifier = Modifier.size(28.dp)
          )
          Spacer(modifier = Modifier.height(10.dp))
          Text(
            text = "Stylist AI",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "Couture critique & color harmony",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    }

    // ==========================================
    // LUXURY BRAND DIRECTORY & COMBOS SEARCH SPACE
    // ==========================================
    BrandSearchSpaceSection(
      viewModel = viewModel,
      onNavigateToStudio = onNavigateToStudio,
      onNavigateToStylist = onNavigateToStylist
    )

    // Quick Atelier Telemetry & Metrics
    Text(
      text = "STUDIO ARCHIVE OVERVIEW",
      style = MaterialTheme.typography.labelMedium,
      letterSpacing = 1.sp,
      color = MatteGold
    )
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      MetricCard(label = "Vault Fits", value = "${savedFits.size}", modifier = Modifier.weight(1f))
      MetricCard(label = "Lookbook Feed", value = "${feedItems.size}", modifier = Modifier.weight(1f))
      MetricCard(label = "Active Top", value = currentTop.name.take(10), modifier = Modifier.weight(1f))
    }

    Spacer(modifier = Modifier.height(24.dp))
  }
}

@Composable
private fun MetricCard(label: String, value: String, modifier: Modifier = Modifier) {
  Card(
    modifier = modifier,
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Text(
        text = value,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = MatteGold,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
      Text(
        text = label,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }
  }
}

// ==========================================
// FIT CHECK RATER COMPONENTS
// ==========================================

@Composable
private fun FitCheckUploadCard(
  onSnapFit: () -> Unit,
  onUploadFit: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .border(1.dp, MatteGold.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
      .testTag("fit_check_upload_card"),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(modifier = Modifier.padding(20.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          shape = RoundedCornerShape(20.dp),
          color = MatteGold.copy(alpha = 0.15f),
          border = BorderStroke(1.dp, MatteGold)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Box(
              modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(MatteGold)
            )
            Text(
              text = "HAUTE FIT RATER",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold,
              color = MatteGold
            )
          }
        }
        Icon(
          Icons.Default.CameraAlt,
          contentDescription = null,
          tint = MatteGold,
          modifier = Modifier.size(20.dp)
        )
      }

      Spacer(modifier = Modifier.height(14.dp))

      Text(
        text = "Rate Today's Silhouette",
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold
      )

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = "Snap or upload a photo of your outfit. Atelier AI breaks down your color harmony, silhouette proportions, and provides tailored styling critique.",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )

      Spacer(modifier = Modifier.height(20.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Button(
          onClick = onSnapFit,
          modifier = Modifier
            .weight(1f)
            .height(48.dp)
            .testTag("snap_fit_button"),
          colors = ButtonDefaults.buttonColors(
            containerColor = MatteGold,
            contentColor = Color.Black
          ),
          shape = RoundedCornerShape(10.dp)
        ) {
          Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text("Snap Fit", fontWeight = FontWeight.Bold)
        }

        OutlinedButton(
          onClick = onUploadFit,
          modifier = Modifier
            .weight(1f)
            .height(48.dp)
            .testTag("upload_fit_button"),
          colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.onSurface
          ),
          border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
          shape = RoundedCornerShape(10.dp)
        ) {
          Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(18.dp), tint = MatteGold)
          Spacer(modifier = Modifier.width(8.dp))
          Text("Upload Photo", fontWeight = FontWeight.Medium)
        }
      }
    }
  }
}

@Composable
private fun FitAnalyzingCard() {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .border(1.dp, MatteGold, RoundedCornerShape(16.dp))
      .testTag("fit_analyzing_card"),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      CircularProgressIndicator(
        color = MatteGold,
        strokeWidth = 3.dp,
        modifier = Modifier.size(44.dp)
      )

      Text(
        text = "ANALYZING SARTORIAL SILHOUETTE",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        color = MatteGold
      )

      Text(
        text = "Measuring volume balance • Extracting fabric palette • Curating bespoke tailoring advice...",
        style = MaterialTheme.typography.bodySmall,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )

      LinearProgressIndicator(
        color = MatteGold,
        trackColor = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier
          .fillMaxWidth(0.85f)
          .height(4.dp)
          .clip(RoundedCornerShape(2.dp))
      )
    }
  }
}

@Composable
private fun FitRaterResultCard(
  fitCheck: UserFitCheck,
  onSaveToVault: (String) -> Unit,
  onInjectPaletteToStudio: (List<String>) -> Unit,
  onReset: () -> Unit
) {
  var isSaved by remember { mutableStateOf(false) }
  val rating = fitCheck.rating

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .border(1.5.dp, MatteGold, RoundedCornerShape(16.dp))
      .testTag("fit_rater_result_card"),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
  ) {
    Column(modifier = Modifier.fillMaxWidth()) {
      // Photo Header with Badges
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(260.dp)
      ) {
        val imgModel = fitCheck.imageBitmap ?: fitCheck.imageUri
        if (imgModel != null) {
          AsyncImage(
            model = imgModel,
            contentDescription = "User Captured Fit",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
          )
        } else {
          Box(
            modifier = Modifier
              .fillMaxSize()
              .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.CameraAlt, contentDescription = null, tint = MatteGold, modifier = Modifier.size(48.dp))
          }
        }

        // Dark gradient scrim
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(
              Brush.verticalGradient(
                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f)),
                startY = 140f
              )
            )
        )

        // Top Header Row on Image
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.Black.copy(alpha = 0.75f),
            border = BorderStroke(1.dp, MatteGold)
          ) {
            Text(
              text = fitCheck.capturedAt,
              style = MaterialTheme.typography.labelSmall,
              color = Color.White,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
            )
          }

          IconButton(
            onClick = onReset,
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(Color.Black.copy(alpha = 0.65f))
              .testTag("reset_fit_rater_button")
          ) {
            Icon(Icons.Default.Refresh, contentDescription = "Rate new fit", tint = Color.White, modifier = Modifier.size(18.dp))
          }
        }

        // Overall Score Overlay at Bottom of Image
        Row(
          modifier = Modifier
            .align(Alignment.BottomStart)
            .padding(16.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = MatteGold,
            modifier = Modifier.size(62.dp)
          ) {
            Column(
              modifier = Modifier.fillMaxSize(),
              verticalArrangement = Arrangement.Center,
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text(
                text = "${rating.overallScore}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.Black
              )
              Text(
                text = "/ 10",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Black.copy(alpha = 0.7f)
              )
            }
          }

          Column {
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = Color.Black.copy(alpha = 0.8f),
              border = BorderStroke(1.dp, MatteGold)
            ) {
              Text(
                text = rating.gradeTitle.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MatteGold,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
              )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
              repeat(5) {
                Icon(Icons.Default.Star, contentDescription = null, tint = MatteGold, modifier = Modifier.size(16.dp))
              }
            }
          }
        }
      }

      // Detailed Rating Body
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        // Executive Verdict
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = MaterialTheme.colorScheme.surfaceVariant,
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Text(
              text = "ATELIER COUTURE BOARD VERDICT",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold,
              color = MatteGold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "“${rating.verdict}”",
              style = MaterialTheme.typography.bodyMedium,
              fontFamily = FontFamily.Serif
            )
          }
        }

        // 4 Core Category Metrics
        Text(
          text = "SARTORIAL PILLARS BREAKDOWN",
          style = MaterialTheme.typography.labelMedium,
          fontWeight = FontWeight.Bold,
          letterSpacing = 1.sp,
          color = MatteGold
        )

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
          MetricRatingRow(
            label = "Color Harmony & Contrast",
            score = rating.colorHarmonyScore,
            critique = rating.colorCritique
          )
          MetricRatingRow(
            label = "Silhouette & Proportions",
            score = rating.silhouetteScore,
            critique = rating.silhouetteCritique
          )
          MetricRatingRow(
            label = "Styling, Hardware & Layers",
            score = rating.stylingScore,
            critique = rating.stylingCritique
          )
          MetricRatingRow(
            label = "Occasion Versatility",
            score = rating.occasionScore,
            critique = rating.occasionCritique
          )
        }

        // Extracted Palette Row
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = MaterialTheme.colorScheme.surfaceVariant,
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "EXTRACTED PALETTE",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MatteGold
              )
              TextButton(
                onClick = { onInjectPaletteToStudio(rating.extractedPalette) },
                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp),
                modifier = Modifier.testTag("inject_palette_studio_button")
              ) {
                Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(14.dp), tint = MatteGold)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Inject into 3D Studio →", style = MaterialTheme.typography.labelSmall, color = MatteGold)
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
              rating.extractedPalette.forEach { hex ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                  Box(
                    modifier = Modifier
                      .size(32.dp)
                      .clip(CircleShape)
                      .background(Color(android.graphics.Color.parseColor(hex)))
                      .border(1.dp, MatteGold, CircleShape)
                  )
                  Spacer(modifier = Modifier.height(4.dp))
                  Text(text = hex, style = MaterialTheme.typography.labelSmall, fontSize = 9.sp)
                }
              }
            }
          }
        }

        // Bespoke Stylist Tips
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = MaterialTheme.colorScheme.surfaceVariant,
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Text(
              text = "TAILORING & ELEVATION TIPS",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold,
              color = MatteGold
            )
            Spacer(modifier = Modifier.height(8.dp))
            rating.stylistTips.forEach { tip ->
              Row(
                modifier = Modifier.padding(vertical = 3.dp),
                verticalAlignment = Alignment.Top
              ) {
                Text(text = "◆", style = MaterialTheme.typography.labelSmall, color = MatteGold, modifier = Modifier.padding(end = 8.dp, top = 2.dp))
                Text(text = tip, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
              }
            }
          }
        }

        // Action Buttons Row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Button(
            onClick = {
              if (!isSaved) {
                onSaveToVault("Rated Look: ${rating.gradeTitle}")
                isSaved = true
              }
            },
            modifier = Modifier
              .weight(1f)
              .height(46.dp)
              .testTag("save_rated_fit_button"),
            colors = ButtonDefaults.buttonColors(
              containerColor = if (isSaved) Color(0xFF2E7D32) else MatteGold,
              contentColor = if (isSaved) Color.White else Color.Black
            ),
            shape = RoundedCornerShape(8.dp)
          ) {
            Icon(
              if (isSaved) Icons.Default.Check else Icons.Outlined.BookmarkBorder,
              contentDescription = null,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(if (isSaved) "Archived to Vault" else "Save to FitVault", style = MaterialTheme.typography.labelMedium)
          }

          OutlinedButton(
            onClick = onReset,
            modifier = Modifier
              .weight(1f)
              .height(46.dp),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
          ) {
            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Rate New Fit", style = MaterialTheme.typography.labelMedium)
          }
        }
      }
    }
  }
}

@Composable
private fun MetricRatingRow(
  label: String,
  score: Float,
  critique: String
) {
  Column(modifier = Modifier.fillMaxWidth()) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = label,
        style = MaterialTheme.typography.bodySmall,
        fontWeight = FontWeight.SemiBold
      )
      Text(
        text = "$score / 10",
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
        color = MatteGold
      )
    }

    Spacer(modifier = Modifier.height(4.dp))

    LinearProgressIndicator(
      progress = { (score / 10f).coerceIn(0f, 1f) },
      modifier = Modifier
        .fillMaxWidth()
        .height(6.dp)
        .clip(RoundedCornerShape(3.dp)),
      color = MatteGold,
      trackColor = MaterialTheme.colorScheme.surfaceVariant
    )

    Spacer(modifier = Modifier.height(4.dp))

    Text(
      text = critique,
      style = MaterialTheme.typography.bodySmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )
  }
}

// ==========================================
// LUXURY BRAND SEARCH & FASHION COMBOS COMPONENTS
// ==========================================

@Composable
fun BrandSearchSpaceSection(
  viewModel: AtelierViewModel,
  onNavigateToStudio: () -> Unit,
  onNavigateToStylist: () -> Unit,
  modifier: Modifier = Modifier
) {
  val searchQuery by viewModel.brandSearchQuery.collectAsState()
  val selectedTier by viewModel.selectedBrandTier.collectAsState()
  val filteredBrands by viewModel.filteredBrands.collectAsState()

  val tiers = listOf("All", "High-Street", "Heritage & Prep", "Luxury Fashion", "Quiet Luxury")
  val quickJumpBrands = listOf("Zara", "Tommy Hilfiger", "Ralph Lauren", "COS", "Gucci", "Prada", "Massimo Dutti")

  Column(
    modifier = modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Header
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = "BRAND DIRECTORY & RATINGS",
          style = MaterialTheme.typography.labelMedium,
          letterSpacing = 1.sp,
          color = MatteGold,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "Tailored Luxury Fashion Combos",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold
        )
      }
      Surface(
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MatteGold),
        color = MaterialTheme.colorScheme.surface
      ) {
        Text(
          text = "${filteredBrands.size} LABELS",
          modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
          style = MaterialTheme.typography.labelSmall,
          fontWeight = FontWeight.Bold,
          color = MatteGold
        )
      }
    }

    // Search Input Bar
    OutlinedTextField(
      value = searchQuery,
      onValueChange = { viewModel.setBrandSearchQuery(it) },
      modifier = Modifier
        .fillMaxWidth()
        .testTag("brand_search_input"),
      placeholder = { Text("Search Zara, Tommy Hilfiger, Gucci, Prada...") },
      leadingIcon = {
        Icon(Icons.Default.Search, contentDescription = "Search", tint = MatteGold)
      },
      trailingIcon = {
        if (searchQuery.isNotEmpty()) {
          IconButton(
            onClick = { viewModel.setBrandSearchQuery("") },
            modifier = Modifier.testTag("clear_brand_search_button")
          ) {
            Icon(Icons.Default.Close, contentDescription = "Clear search", modifier = Modifier.size(18.dp))
          }
        }
      },
      singleLine = true,
      shape = RoundedCornerShape(24.dp),
      colors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MatteGold,
        unfocusedBorderColor = MaterialTheme.colorScheme.outline
      )
    )

    // Tier Filter Chips
    LazyRow(
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      items(tiers) { tier ->
        val isSelected = selectedTier == tier
        FilterChip(
          selected = isSelected,
          onClick = { viewModel.setSelectedBrandTier(tier) },
          label = {
            Text(
              text = tier,
              style = MaterialTheme.typography.labelSmall,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
          },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MatteGold,
            selectedLabelColor = Color.Black,
            containerColor = MaterialTheme.colorScheme.surface,
            labelColor = MaterialTheme.colorScheme.onSurface
          ),
          border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = isSelected,
            borderColor = if (isSelected) MatteGold else MaterialTheme.colorScheme.outline
          ),
          modifier = Modifier.testTag("brand_tier_${tier.lowercase().replace(" ", "_")}")
        )
      }
    }

    // Quick Jump Buttons (when search is empty)
    if (searchQuery.isBlank()) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "POPULAR:",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          fontWeight = FontWeight.Bold
        )
        quickJumpBrands.forEach { bName ->
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.clickable { viewModel.setBrandSearchQuery(bName) }
          ) {
            Text(
              text = bName,
              style = MaterialTheme.typography.labelSmall,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
              color = MaterialTheme.colorScheme.onSurface
            )
          }
        }
      }
    }

    // Brand Profile Cards List
    if (filteredBrands.isEmpty()) {
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Icon(Icons.Default.Search, contentDescription = null, tint = MatteGold, modifier = Modifier.size(36.dp))
          Text("No luxury brands match “$searchQuery”", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
          Text("Try searching for Zara, Tommy Hilfiger, Ralph Lauren, COS, Gucci, Prada...", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
          TextButton(onClick = { viewModel.setBrandSearchQuery("") }) {
            Text("Clear Search Filter", color = MatteGold)
          }
        }
      }
    } else {
      Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        filteredBrands.forEach { brand ->
          BrandProfileCard(
            brand = brand,
            onApplyToStudio = {
              viewModel.applyBrandComboToStudio(brand.comboRecommendation)
              onNavigateToStudio()
            },
            onSaveToVault = {
              viewModel.saveBrandComboToVault(brand.name, brand.comboRecommendation)
            },
            onConsultStylist = {
              viewModel.sendUserMessage("How can I best elevate my ${brand.name} look with accessories and tailoring?")
              onNavigateToStylist()
            }
          )
        }
      }
    }
  }
}

@Composable
private fun BrandProfileCard(
  brand: BrandProfile,
  onApplyToStudio: () -> Unit,
  onSaveToVault: () -> Unit,
  onConsultStylist: () -> Unit
) {
  val context = LocalContext.current
  var isSaved by remember { mutableStateOf(false) }
  val combo = brand.comboRecommendation

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
      .testTag("brand_card_${brand.id}"),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
      // Brand Header: Initial, Name, Tier, Ratings
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Row(
          horizontalArrangement = Arrangement.spacedBy(12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceVariant,
            border = BorderStroke(1.5.dp, MatteGold),
            modifier = Modifier.size(46.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Text(
                text = brand.name.take(1),
                style = MaterialTheme.typography.titleLarge,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                color = MatteGold
              )
            }
          }

          Column {
            Text(
              text = brand.name,
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "${brand.origin} • Est. ${brand.foundedYear}",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        // Rating Badge and Price Tier
        Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = MatteGold,
            contentColor = Color.Black
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(13.dp))
              Text(
                text = "${brand.rating} / 10",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold
              )
            }
          }

          Surface(
            shape = RoundedCornerShape(6.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline)
          ) {
            Text(
              text = brand.priceTier,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold,
              color = MatteGold
            )
          }
        }
      }

      // Tier Category Badge & Signature Vibe
      Surface(
        shape = RoundedCornerShape(6.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(1.dp, MatteGold.copy(alpha = 0.4f))
      ) {
        Text(
          text = brand.tier.uppercase(),
          modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
          style = MaterialTheme.typography.labelSmall,
          fontWeight = FontWeight.Bold,
          color = MatteGold
        )
      }

      Text(
        text = brand.signatureVibe,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface
      )

      // Key Strengths Chips
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        brand.keyStrengths.forEach { strength ->
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline)
          ) {
            Text(
              text = strength,
              style = MaterialTheme.typography.labelSmall,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }

      // ==========================================
      // FLAGSHIP BOUTIQUE ADDRESS & STORE LOCATOR
      // ==========================================
      if (brand.flagshipAddress.isNotBlank()) {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
          border = BorderStroke(1.dp, MatteGold.copy(alpha = 0.45f))
        ) {
          Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Icon(
                  Icons.Default.Place,
                  contentDescription = null,
                  tint = MatteGold,
                  modifier = Modifier.size(16.dp)
                )
                Text(
                  text = "FLAGSHIP BOUTIQUE ADDRESS",
                  style = MaterialTheme.typography.labelSmall,
                  fontWeight = FontWeight.Bold,
                  color = MatteGold,
                  letterSpacing = 0.8.sp
                )
              }
              Surface(
                shape = RoundedCornerShape(4.dp),
                color = MaterialTheme.colorScheme.surface
              ) {
                Text(
                  text = "VERIFIED STORE",
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                  style = MaterialTheme.typography.labelSmall,
                  fontSize = 9.sp,
                  color = MatteGold
                )
              }
            }

            Text(
              text = brand.flagshipAddress,
              style = MaterialTheme.typography.bodyMedium,
              fontWeight = FontWeight.Medium,
              color = MaterialTheme.colorScheme.onSurface
            )

            if (brand.storePhone.isNotBlank()) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Icon(
                  Icons.Default.Phone,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.onSurfaceVariant,
                  modifier = Modifier.size(13.dp)
                )
                Text(
                  text = "Concierge: ${brand.storePhone}",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }

            // Interactive Navigation & Contact Actions
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              // Directions / Google Maps button
              Button(
                onClick = {
                  try {
                    val mapUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=" + Uri.encode(brand.mapsQuery))
                    val mapIntent = Intent(Intent.ACTION_VIEW, mapUri)
                    context.startActivity(mapIntent)
                  } catch (e: Exception) {
                    Toast.makeText(context, "Opening maps: ${brand.flagshipAddress}", Toast.LENGTH_SHORT).show()
                  }
                },
                modifier = Modifier
                  .weight(1.3f)
                  .height(38.dp)
                  .testTag("brand_maps_button_${brand.id}"),
                colors = ButtonDefaults.buttonColors(
                  containerColor = MatteGold,
                  contentColor = Color.Black
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 8.dp)
              ) {
                Icon(Icons.Default.Navigation, contentDescription = null, modifier = Modifier.size(13.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Get Directions", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
              }

              // Official Web Store button
              OutlinedButton(
                onClick = {
                  try {
                    val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(brand.websiteUrl))
                    context.startActivity(webIntent)
                  } catch (e: Exception) {
                    Toast.makeText(context, "Navigating to ${brand.websiteUrl}", Toast.LENGTH_SHORT).show()
                  }
                },
                modifier = Modifier
                  .weight(1f)
                  .height(38.dp)
                  .testTag("brand_website_button_${brand.id}"),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                contentPadding = PaddingValues(horizontal = 6.dp)
              ) {
                Icon(Icons.Default.Language, contentDescription = null, modifier = Modifier.size(13.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Visit Store", style = MaterialTheme.typography.labelSmall)
              }

              // Copy Address button
              IconButton(
                onClick = {
                  val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                  val clip = ClipData.newPlainText("Boutique Address", "${brand.name} Flagship: ${brand.flagshipAddress}")
                  clipboard.setPrimaryClip(clip)
                  Toast.makeText(context, "${brand.name} address copied to clipboard", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                  .size(38.dp)
                  .clip(RoundedCornerShape(8.dp))
                  .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                  .testTag("brand_copy_address_button_${brand.id}")
              ) {
                Icon(
                  Icons.Default.ContentCopy,
                  contentDescription = "Copy Address",
                  tint = MatteGold,
                  modifier = Modifier.size(15.dp)
                )
              }
            }
          }
        }
      }

      HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))

      // ==========================================
      // SUITABLE FASHION COMBO RECOMMENDATION
      // ==========================================
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = BorderStroke(1.dp, MatteGold.copy(alpha = 0.6f))
      ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MatteGold, modifier = Modifier.size(16.dp))
              Text(
                text = "RECOMMENDED FASHION COMBO",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MatteGold
              )
            }
          }

          Text(
            text = combo.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )

          Text(
            text = combo.description,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          // Pieces List
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
              Text(text = "• Top: ${combo.topPiece}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
              Text(text = "• Bottom: ${combo.bottomPiece}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
              Text(text = "• Footwear: ${combo.footwearPiece}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
              Text(text = "• Accents: ${combo.accessoriesPiece}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
            }
          }

          // Recommended Palette Swatches
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Text(
              text = "Combo Palette:",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold,
              color = MatteGold
            )
            combo.palette.forEach { hex ->
              Box(
                modifier = Modifier
                  .size(18.dp)
                  .clip(CircleShape)
                  .background(Color(android.graphics.Color.parseColor(hex)))
                  .border(1.dp, MatteGold.copy(alpha = 0.5f), CircleShape)
              )
            }
          }

          // Styling Rule Quote
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
            border = BorderStroke(0.5.dp, MatteGold.copy(alpha = 0.3f)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(8.dp),
              verticalAlignment = Alignment.Top,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Icon(Icons.Default.FormatQuote, contentDescription = null, tint = MatteGold, modifier = Modifier.size(16.dp))
              Text(
                text = combo.stylingRule,
                style = MaterialTheme.typography.bodySmall,
                fontFamily = FontFamily.Serif,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          // Action Buttons
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Button(
              onClick = onApplyToStudio,
              modifier = Modifier
                .weight(1.3f)
                .height(42.dp)
                .testTag("apply_brand_combo_${brand.id}"),
              colors = ButtonDefaults.buttonColors(
                containerColor = MatteGold,
                contentColor = Color.Black
              ),
              shape = RoundedCornerShape(8.dp)
            ) {
              Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Try in 3D", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
              onClick = {
                if (!isSaved) {
                  onSaveToVault()
                  isSaved = true
                }
              },
              modifier = Modifier
                .weight(1f)
                .height(42.dp)
                .testTag("save_brand_combo_${brand.id}"),
              shape = RoundedCornerShape(8.dp),
              border = BorderStroke(1.dp, if (isSaved) Color(0xFF2E7D32) else MaterialTheme.colorScheme.outline),
              colors = ButtonDefaults.outlinedButtonColors(
                contentColor = if (isSaved) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurface
              )
            ) {
              Icon(
                if (isSaved) Icons.Default.Check else Icons.Outlined.BookmarkBorder,
                contentDescription = null,
                modifier = Modifier.size(14.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(if (isSaved) "Archived" else "Save Fit", style = MaterialTheme.typography.labelSmall)
            }

            IconButton(
              onClick = onConsultStylist,
              modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
            ) {
              Icon(Icons.Default.AutoAwesome, contentDescription = "Ask Stylist", tint = MatteGold, modifier = Modifier.size(18.dp))
            }
          }
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrandSearchModalBottomSheet(
  viewModel: AtelierViewModel,
  onDismissRequest: () -> Unit,
  onNavigateToStudio: () -> Unit,
  onNavigateToStylist: () -> Unit
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  ModalBottomSheet(
    onDismissRequest = onDismissRequest,
    sheetState = sheetState,
    containerColor = MaterialTheme.colorScheme.surface,
    contentColor = MaterialTheme.colorScheme.onSurface,
    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 20.dp, vertical = 8.dp)
        .padding(bottom = 36.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "BRAND SEARCH & DIRECTORY",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MatteGold,
            letterSpacing = 1.sp
          )
          Text(
            text = "Browse Zara, Tommy Hilfiger, Prada & tailored combos",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
        IconButton(onClick = onDismissRequest) {
          Icon(Icons.Default.Close, contentDescription = "Close sheet")
        }
      }

      BrandSearchSpaceSection(
        viewModel = viewModel,
        onNavigateToStudio = {
          onDismissRequest()
          onNavigateToStudio()
        },
        onNavigateToStylist = {
          onDismissRequest()
          onNavigateToStylist()
        }
      )
    }
  }
}

// ==========================================
// 2. COLOUR STUDIO (THE 3D FITTING ROOM)
// ==========================================
@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun LegacyColourStudioScreen(
  viewModel: AtelierViewModel,
  modifier: Modifier = Modifier
) {
  val gender by viewModel.gender.collectAsState()
  val bodySize by viewModel.bodySize.collectAsState()
  val rotationDegrees by viewModel.rotationDegrees.collectAsState()
  val isDark by viewModel.isDarkMode.collectAsState()

  val selectedHeadwear by viewModel.selectedHeadwear.collectAsState()
  val selectedTop by viewModel.selectedTop.collectAsState()
  val selectedBottom by viewModel.selectedBottom.collectAsState()
  val selectedFootwear by viewModel.selectedFootwear.collectAsState()
  val selectedAccessories by viewModel.selectedAccessories.collectAsState()
  val selectedSkinTone by viewModel.selectedSkinTone.collectAsState()

  var webViewRef by remember { mutableStateOf<WebView?>(null) }
  var showSaveDialog by remember { mutableStateOf(false) }
  var newFitName by remember { mutableStateOf("") }
  var newFitNotes by remember { mutableStateOf("") }

  // Drawer Wardrobe Category Tabs - Complexion first for immediate customization
  var selectedTabCategory by remember { mutableIntStateOf(0) } // 0: Complexion, 1: Boxers, 2: Tops, 3: Kicks, 4: Acc, 5: Caps
  val categories = listOf("Complexion", "Boxers", "Tops", "Kicks", "Acc", "Caps")

  // Observe Javascript command dispatch
  LaunchedEffect(Unit) {
    viewModel.jsCommandFlow.collectLatest { command ->
      webViewRef?.post {
        webViewRef?.evaluateJavascript(command, null)
      }
    }
  }

  // Synchronize when gender, bodySize, or colors change
  LaunchedEffect(gender, bodySize) {
    val cmd = "window.loadModel && window.loadModel('${gender.name}', '${bodySize.name}');"
    webViewRef?.evaluateJavascript(cmd, null)
  }

  LaunchedEffect(
    selectedTop.colorHex,
    selectedBottom.colorHex,
    selectedFootwear.colorHex,
    selectedHeadwear?.colorHex,
    selectedAccessories?.colorHex,
    selectedSkinTone.hex
  ) {
    val topHex = selectedTop.colorHex
    val botHex = selectedBottom.colorHex
    val shoeHex = selectedFootwear.colorHex
    val headHex = selectedHeadwear?.colorHex ?: "#111111"
    val accHex = selectedAccessories?.colorHex ?: "#D4AF37"
    val skinHex = selectedSkinTone.hex
    val cmd = "window.updateColors && window.updateColors('$topHex', '$botHex', '$shoeHex', '$headHex', '$accHex', '$skinHex'); window.setSkinTone && window.setSkinTone('$skinHex');"
    webViewRef?.evaluateJavascript(cmd, null)
  }

  LaunchedEffect(isDark) {
    webViewRef?.evaluateJavascript("window.setTheme && window.setTheme($isDark);", null)
  }

  Column(modifier = modifier.fillMaxSize()) {
    // Top Bar: Gender Toggle & Save Action
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 6.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Gender Segmented Pill
      Surface(
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
      ) {
        Row(modifier = Modifier.padding(3.dp)) {
          Gender.values().forEach { g ->
            val isSelected = gender == g
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(if (isSelected) MatteGold else Color.Transparent)
                .clickable { viewModel.setGender(g) }
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .testTag("gender_toggle_${g.name.lowercase()}"),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = g.label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurface
              )
            }
          }
        }
      }

      // Save Fit to Vault Button
      Button(
        onClick = { showSaveDialog = true },
        colors = ButtonDefaults.buttonColors(
          containerColor = MaterialTheme.colorScheme.surface,
          contentColor = MatteGold
        ),
        border = BorderStroke(1.dp, MatteGold),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.testTag("save_fit_button")
      ) {
        Icon(Icons.Outlined.BookmarkBorder, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("Save Fit", style = MaterialTheme.typography.labelMedium)
      }
    }

    // Size Selector Chips (Horizontal Scroll)
    LazyRow(
      contentPadding = PaddingValues(horizontal = 16.dp, vertical = 2.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      items(BodySize.values()) { size ->
        val isSelected = bodySize == size
        FilterChip(
          selected = isSelected,
          onClick = { viewModel.setBodySize(size) },
          label = {
            Text(
              text = "${size.label} (${size.description.take(8)})",
              style = MaterialTheme.typography.labelSmall
            )
          },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MatteGold,
            selectedLabelColor = Color.Black,
            containerColor = MaterialTheme.colorScheme.surface,
            labelColor = MaterialTheme.colorScheme.onSurface
          ),
          border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = isSelected,
            borderColor = if (isSelected) MatteGold else MaterialTheme.colorScheme.outline
          ),
          modifier = Modifier.testTag("size_chip_${size.name.lowercase()}")
        )
      }
    }

    // Quick-Access Complexion & Skin Tone Selector Strip
    Surface(
      color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
      border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
      shape = RoundedCornerShape(12.dp),
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 4.dp)
        .testTag("skin_tone_selector_bar")
    ) {
      Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Box(
              modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(Color(android.graphics.Color.parseColor(selectedSkinTone.hex)))
                .border(1.dp, MatteGold, CircleShape)
            )
            Text(
              text = "SKIN COMPLEXION",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.sp,
              color = MatteGold
            )
          }
          Text(
            text = "${selectedSkinTone.name} • ${selectedSkinTone.undertone}",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Horizontal Swatches for All 8 Inclusive Complexion Presets
        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(10.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          items(viewModel.skinTonePresets) { tone ->
            val isSelected = selectedSkinTone.id == tone.id
            val color = Color(android.graphics.Color.parseColor(tone.hex))
            Box(
              modifier = Modifier
                .size(if (isSelected) 34.dp else 28.dp)
                .clip(CircleShape)
                .background(color)
                .border(
                  width = if (isSelected) 2.5.dp else 1.dp,
                  color = if (isSelected) MatteGold else MaterialTheme.colorScheme.outline.copy(alpha = 0.6f),
                  shape = CircleShape
                )
                .clickable { viewModel.selectSkinTone(tone) }
                .testTag("skin_tone_swatch_${tone.id}"),
              contentAlignment = Alignment.Center
            ) {
              if (isSelected) {
                Icon(
                  Icons.Default.Check,
                  contentDescription = "${tone.name} selected",
                  tint = if (tone.hex.equals("#F7EBE1", true) || tone.hex.equals("#EEDBC8", true)) Color.Black else Color.White,
                  modifier = Modifier.size(14.dp)
                )
              }
            }
          }
        }
      }
    }

    // Main Stage: 3D Mannequin Canvas (AndroidView WebView)
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f)
        .testTag("3d_mannequin_stage")
    ) {
      AndroidView(
        factory = { context ->
          WebView(context).apply {
            layoutParams = android.view.ViewGroup.LayoutParams(
              android.view.ViewGroup.LayoutParams.MATCH_PARENT,
              android.view.ViewGroup.LayoutParams.MATCH_PARENT
            )
            isNestedScrollingEnabled = false
            overScrollMode = android.view.View.OVER_SCROLL_NEVER
            setLayerType(android.view.View.LAYER_TYPE_HARDWARE, null)
            setBackgroundColor(if (isDark) 0xFF111111.toInt() else 0xFFFAFAFA.toInt())
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.allowFileAccess = true
            settings.allowContentAccess = true
            settings.databaseEnabled = true
            settings.mediaPlaybackRequiresUserGesture = false
            settings.loadsImagesAutomatically = true
            webChromeClient = WebChromeClient()
            webViewClient = object : WebViewClient() {
              override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                viewModel.refresh3DMannequin()
              }
            }
            loadUrl("file:///android_asset/mannequin.html")
            webViewRef = this
          }
        },
        update = { webView ->
          webView.setBackgroundColor(if (isDark) 0xFF111111.toInt() else 0xFFFAFAFA.toInt())
          webViewRef = webView
        },
        modifier = Modifier.fillMaxSize()
      )

      // Quick Rotation Control Float on Bottom Right
      Row(
        modifier = Modifier
          .align(Alignment.BottomEnd)
          .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          shape = CircleShape,
          color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
          border = BorderStroke(1.dp, MatteGold.copy(alpha = 0.5f))
        ) {
          IconButton(
            onClick = {
              viewModel.setRotationDegrees(rotationDegrees + 45f)
            },
            modifier = Modifier.size(40.dp)
          ) {
            Icon(
              Icons.Default.RotateRight,
              contentDescription = "Rotate 3D model",
              tint = MatteGold
            )
          }
        }
      }
    }

    // Wardrobe Drawer (Bottom)
    Surface(
      shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 6.dp,
      border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(bottom = 12.dp)) {
        // Drawer Category Tabs
        ScrollableTabRow(
          selectedTabIndex = selectedTabCategory,
          containerColor = Color.Transparent,
          contentColor = MatteGold,
          edgePadding = 16.dp,
          indicator = { tabPositions: List<TabPosition> ->
            if (selectedTabCategory < tabPositions.size) {
              TabRowDefaults.SecondaryIndicator(
                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabCategory]),
                color = MatteGold
              )
            }
          }
        ) {
          categories.forEachIndexed { index, title ->
            Tab(
              selected = selectedTabCategory == index,
              onClick = { selectedTabCategory = index },
              text = {
                Text(
                  text = title,
                  style = MaterialTheme.typography.labelMedium,
                  fontWeight = if (selectedTabCategory == index) FontWeight.Bold else FontWeight.Normal
                )
              }
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Wardrobe Items / Complexion Customizer for Selected Category
        when (selectedTabCategory) {
          0 -> SkinToneCustomizerView(
            selectedSkinTone = selectedSkinTone,
            skinTones = viewModel.skinTonePresets,
            onSelectSkinTone = { viewModel.selectSkinTone(it) },
            onApplyPaletteColor = { viewModel.updateBottomColor(it) }
          )
          1 -> WardrobeItemList(
            items = viewModel.catalogBottoms,
            selectedItem = selectedBottom,
            onSelectItem = { viewModel.selectBottom(it) },
            onUpdateColor = { viewModel.updateBottomColor(it) },
            palettePresets = viewModel.colorPalettePresets
          )
          2 -> WardrobeItemList(
            items = viewModel.catalogTops,
            selectedItem = selectedTop,
            onSelectItem = { viewModel.selectTop(it) },
            onUpdateColor = { viewModel.updateTopColor(it) },
            palettePresets = viewModel.colorPalettePresets
          )
          3 -> WardrobeItemList(
            items = viewModel.catalogFootwear,
            selectedItem = selectedFootwear,
            onSelectItem = { viewModel.selectFootwear(it) },
            onUpdateColor = { viewModel.updateFootwearColor(it) },
            palettePresets = viewModel.colorPalettePresets
          )
          4 -> WardrobeItemList(
            items = viewModel.catalogAccessories,
            selectedItem = selectedAccessories,
            onSelectItem = { viewModel.selectAccessories(it) },
            onUpdateColor = { /* accessories stay metallic */ },
            palettePresets = viewModel.colorPalettePresets
          )
          5 -> WardrobeItemList(
            items = viewModel.catalogHeadwear,
            selectedItem = selectedHeadwear,
            onSelectItem = { viewModel.selectHeadwear(it) },
            onUpdateColor = { viewModel.updateHeadwearColor(it) },
            palettePresets = viewModel.colorPalettePresets
          )
        }
      }
    }
  }

  // Save Fit Dialog
  if (showSaveDialog) {
    AlertDialog(
      onDismissRequest = { showSaveDialog = false },
      title = {
        Text("Save Fit to FitVault", style = MaterialTheme.typography.titleLarge)
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text(
            "Archive this mannequin configuration with custom title and styling notes.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          OutlinedTextField(
            value = newFitName,
            onValueChange = { newFitName = it },
            label = { Text("Lookbook Title") },
            placeholder = { Text("e.g. Milan Gala Monochrome") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("fit_name_input")
          )
          OutlinedTextField(
            value = newFitNotes,
            onValueChange = { newFitNotes = it },
            label = { Text("Styling Notes") },
            placeholder = { Text("e.g. Silk lapel with matte gold footwear") },
            modifier = Modifier.fillMaxWidth().testTag("fit_notes_input")
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            viewModel.saveCurrentFit(newFitName, newFitNotes)
            newFitName = ""
            newFitNotes = ""
            showSaveDialog = false
          },
          colors = ButtonDefaults.buttonColors(containerColor = MatteGold, contentColor = Color.Black),
          modifier = Modifier.testTag("confirm_save_fit_button")
        ) {
          Text("Archive Fit")
        }
      },
      dismissButton = {
        TextButton(onClick = { showSaveDialog = false }) {
          Text("Cancel", color = MaterialTheme.colorScheme.onSurface)
        }
      }
    )
  }
}

@Composable
private fun SkinToneCustomizerView(
  selectedSkinTone: SkinTone,
  skinTones: List<SkinTone>,
  onSelectSkinTone: (SkinTone) -> Unit,
  onApplyPaletteColor: (String) -> Unit
) {
  var warmthAdjustment by remember(selectedSkinTone.id) { mutableStateOf(0f) }

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 4.dp)
      .testTag("skin_tone_customizer_view")
  ) {
    // 1. ACTIVE COMPLEXION HIGHLIGHT CARD
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .testTag("active_complexion_card"),
      colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
      ),
      border = BorderStroke(1.dp, MatteGold.copy(alpha = 0.6f)),
      shape = RoundedCornerShape(12.dp)
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Large circular skin tone preview
        Box(
          modifier = Modifier
            .size(52.dp)
            .clip(CircleShape)
            .background(Color(android.graphics.Color.parseColor(selectedSkinTone.hex)))
            .border(2.5.dp, MatteGold, CircleShape)
            .testTag("active_skin_swatch_large"),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            Icons.Default.Check,
            contentDescription = null,
            tint = if (selectedSkinTone.hex.equals("#F7EBE1", true) || selectedSkinTone.hex.equals("#EEDBC8", true)) Color.Black else Color.White,
            modifier = Modifier.size(20.dp)
          )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Text(
              text = selectedSkinTone.name,
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = MatteGold.copy(alpha = 0.2f),
              border = BorderStroke(0.5.dp, MatteGold)
            ) {
              Text(
                text = selectedSkinTone.hex.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = MatteGold,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }

          Text(
            text = selectedSkinTone.undertone,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = MatteGold
          )

          Spacer(modifier = Modifier.height(2.dp))

          Text(
            text = selectedSkinTone.description,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // 2. INCLUSIVE COMPLEXION SPECTRUM CAROUSEL
    Text(
      text = "COMPLEXION SPECTRUM (FITZPATRICK I - VI):",
      style = MaterialTheme.typography.labelSmall,
      fontWeight = FontWeight.Bold,
      letterSpacing = 0.8.sp,
      color = MatteGold
    )

    Spacer(modifier = Modifier.height(6.dp))

    LazyRow(
      horizontalArrangement = Arrangement.spacedBy(10.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      items(skinTones) { tone ->
        val isSelected = selectedSkinTone.id == tone.id
        Card(
          modifier = Modifier
            .width(130.dp)
            .clickable { onSelectSkinTone(tone) }
            .testTag("skin_tone_card_${tone.id}"),
          colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface
          ),
          border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) MatteGold else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
          ),
          shape = RoundedCornerShape(10.dp)
        ) {
          Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Box(
              modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(Color(android.graphics.Color.parseColor(tone.hex)))
                .border(
                  width = if (isSelected) 2.dp else 1.dp,
                  color = if (isSelected) MatteGold else Color.Gray.copy(alpha = 0.5f),
                  shape = CircleShape
                ),
              contentAlignment = Alignment.Center
            ) {
              if (isSelected) {
                Icon(
                  Icons.Default.Check,
                  contentDescription = null,
                  tint = if (tone.hex.equals("#F7EBE1", true) || tone.hex.equals("#EEDBC8", true)) Color.Black else Color.White,
                  modifier = Modifier.size(16.dp)
                )
              }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
              text = tone.name,
              style = MaterialTheme.typography.labelSmall,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
              textAlign = TextAlign.Center,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )

            Text(
              text = tone.hex.uppercase(),
              style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // 3. COLOR HARMONY & FABRIC PAIRINGS FOR THIS COMPLEXION
    if (selectedSkinTone.recommendedPalettes.isNotEmpty()) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "RECOMMENDED FABRIC HARMONY:",
          style = MaterialTheme.typography.labelSmall,
          fontWeight = FontWeight.Bold,
          letterSpacing = 0.8.sp,
          color = MatteGold
        )
        Text(
          text = "Tap to style trunks",
          style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      Spacer(modifier = Modifier.height(6.dp))

      Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState())
      ) {
        selectedSkinTone.recommendedPalettes.forEach { hex ->
          val color = Color(android.graphics.Color.parseColor(hex))
          Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
            modifier = Modifier
              .clickable { onApplyPaletteColor(hex) }
              .testTag("apply_harmony_${hex.replace("#", "")}")
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(16.dp)
                  .clip(CircleShape)
                  .background(color)
                  .border(0.5.dp, Color.White.copy(alpha = 0.6f), CircleShape)
              )
              Text(
                text = hex.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun WardrobeItemList(
  items: List<ClothingItem>,
  selectedItem: ClothingItem?,
  onSelectItem: (ClothingItem) -> Unit,
  onUpdateColor: (String) -> Unit,
  palettePresets: List<com.example.model.ColorOption>
) {
  Column(modifier = Modifier.fillMaxWidth()) {
    // Horizontal Swatch Palette Row
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState())
        .padding(horizontal = 16.dp, vertical = 4.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "FABRIC PALETTE:",
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        color = MatteGold
      )
      palettePresets.forEach { opt ->
        val itemColor = selectedItem?.colorHex ?: ""
        val isCurrent = itemColor.equals(opt.hex, ignoreCase = true)
        Box(
          modifier = Modifier
            .size(28.dp)
            .clip(CircleShape)
            .background(Color(android.graphics.Color.parseColor(opt.hex)))
            .border(
              width = if (isCurrent) 2.5.dp else 1.dp,
              color = if (isCurrent) MatteGold else Color.Gray.copy(alpha = 0.5f),
              shape = CircleShape
            )
            .clickable { onUpdateColor(opt.hex) }
            .testTag("swatch_${opt.name.lowercase().replace(" ", "_")}"),
          contentAlignment = Alignment.Center
        ) {
          if (isCurrent) {
            Icon(
              Icons.Default.Check,
              contentDescription = null,
              tint = if (opt.hex == "#FAFAFA" || opt.hex == "#F5F5F0") Color.Black else Color.White,
              modifier = Modifier.size(14.dp)
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(6.dp))

    // Items Horizontal Carousel
    LazyRow(
      contentPadding = PaddingValues(horizontal = 16.dp),
      horizontalArrangement = Arrangement.spacedBy(10.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      items(items) { item ->
        val isSelected = selectedItem?.id == item.id
        Card(
          modifier = Modifier
            .width(170.dp)
            .clickable { onSelectItem(item) }
            .testTag("wardrobe_item_${item.id}"),
          colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface
          ),
          border = BorderStroke(
            width = if (isSelected) 1.5.dp else 1.dp,
            color = if (isSelected) MatteGold else MaterialTheme.colorScheme.outline
          ),
          shape = RoundedCornerShape(10.dp)
        ) {
          Column(modifier = Modifier.padding(10.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(14.dp)
                  .clip(CircleShape)
                  .background(Color(android.graphics.Color.parseColor(item.colorHex)))
                  .border(1.dp, MatteGold, CircleShape)
              )
              if (isSelected) {
                Text(
                  text = "ACTIVE",
                  style = MaterialTheme.typography.labelSmall,
                  fontWeight = FontWeight.Bold,
                  color = MatteGold
                )
              }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = item.name,
              style = MaterialTheme.typography.labelLarge,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
            Text(
              text = item.material,
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }
        }
      }
    }
  }
}

// ==========================================
// 3. STYLE FEED (STAGGERED INSPIRATION GRID)
// ==========================================
@Composable
fun StyleFeedScreen(
  viewModel: AtelierViewModel,
  onInspectLook: () -> Unit,
  modifier: Modifier = Modifier
) {
  val feedCategory by viewModel.feedCategory.collectAsState()
  val feedItems by viewModel.feedItems.collectAsState()
  val categories = listOf("All", "Runway", "Minimalist", "Sartorial", "Avant-Garde")

  val filteredItems = remember(feedCategory, feedItems) {
    if (feedCategory == "All") feedItems
    else feedItems.filter { it.tags.any { tag -> tag.contains(feedCategory, ignoreCase = true) } }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(top = 12.dp)
  ) {
    // Header
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = "STYLE FEED",
          style = MaterialTheme.typography.headlineMedium,
          fontWeight = FontWeight.Bold,
          letterSpacing = 1.sp
        )
        Text(
          text = "GLOBAL HAUTE COUTURE INSPIRATION",
          style = MaterialTheme.typography.labelSmall,
          color = MatteGold
        )
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Filter Chips Row
    LazyRow(
      contentPadding = PaddingValues(horizontal = 20.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      items(categories) { cat ->
        val isSelected = feedCategory == cat
        FilterChip(
          selected = isSelected,
          onClick = { viewModel.setFeedCategory(cat) },
          label = { Text(cat, style = MaterialTheme.typography.labelSmall) },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MatteGold,
            selectedLabelColor = Color.Black
          ),
          border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = isSelected,
            borderColor = if (isSelected) MatteGold else MaterialTheme.colorScheme.outline
          ),
          modifier = Modifier.testTag("feed_filter_$cat")
        )
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Vertical Grid (Pinterest-style lookbook)
    LazyVerticalGrid(
      columns = GridCells.Fixed(2),
      contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp, top = 8.dp),
      horizontalArrangement = Arrangement.spacedBy(12.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp),
      modifier = Modifier.fillMaxSize().testTag("style_feed_grid")
    ) {
      items(filteredItems, key = { it.id }) { item ->
        StyleFeedCard(
          item = item,
          onLikeToggle = { viewModel.toggleFeedLike(item.id) },
          onInspect = onInspectLook
        )
      }
    }
  }
}

@Composable
private fun StyleFeedCard(
  item: FeedItem,
  onLikeToggle: () -> Unit,
  onInspect: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp)),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
  ) {
    Column {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(210.dp)
      ) {
        AsyncImage(
          model = item.imageUrl,
          contentDescription = item.title,
          contentScale = ContentScale.Crop,
          modifier = Modifier.fillMaxSize()
        )

        // Floating Like Heart Button
        Surface(
          shape = CircleShape,
          color = Color.Black.copy(alpha = 0.55f),
          modifier = Modifier
            .padding(8.dp)
            .align(Alignment.TopEnd)
        ) {
          IconButton(
            onClick = onLikeToggle,
            modifier = Modifier
              .size(34.dp)
              .testTag("like_button_${item.id}")
          ) {
            Icon(
              imageVector = if (item.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
              contentDescription = "Like item",
              tint = if (item.isLiked) MatteGold else Color.White,
              modifier = Modifier.size(18.dp)
            )
          }
        }

        // Dominant Color Dot Badge
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = Color.Black.copy(alpha = 0.7f),
          modifier = Modifier
            .padding(8.dp)
            .align(Alignment.BottomStart)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(Color(android.graphics.Color.parseColor(item.dominantColorHex)))
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "${item.likes}",
              style = MaterialTheme.typography.labelSmall,
              color = Color.White
            )
          }
        }
      }

      // Card Content
      Column(modifier = Modifier.padding(10.dp)) {
        Text(
          text = item.title,
          style = MaterialTheme.typography.titleSmall,
          fontWeight = FontWeight.Bold,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
        Text(
          text = item.designer,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = item.description,
          style = MaterialTheme.typography.bodySmall,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis,
          color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End
        ) {
          TextButton(
            onClick = onInspect,
            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
          ) {
            Text(
              "Style in 3D →",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold,
              color = MatteGold
            )
          }
        }
      }
    }
  }
}

// ==========================================
// 4. FITVAULT (SAVED OUTFITS ARCHIVE)
// ==========================================
@Composable
fun FitVaultScreen(
  viewModel: AtelierViewModel,
  onLoadIntoStudio: () -> Unit,
  modifier: Modifier = Modifier
) {
  val savedFits by viewModel.savedFits.collectAsState()

  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp, vertical = 12.dp)
  ) {
    // Header
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = "FITVAULT",
          style = MaterialTheme.typography.headlineMedium,
          fontWeight = FontWeight.Bold,
          letterSpacing = 1.sp
        )
        Text(
          text = "YOUR CURATED SARTORIAL ARCHIVE",
          style = MaterialTheme.typography.labelSmall,
          color = MatteGold
        )
      }
      Surface(
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, MatteGold),
        color = MaterialTheme.colorScheme.surface
      ) {
        Text(
          text = "${savedFits.size} SAVED",
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
          style = MaterialTheme.typography.labelSmall,
          fontWeight = FontWeight.Bold,
          color = MatteGold
        )
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    if (savedFits.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f),
        contentAlignment = Alignment.Center
      ) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Icon(
            Icons.Outlined.BookmarkBorder,
            contentDescription = null,
            tint = MatteGold,
            modifier = Modifier.size(48.dp)
          )
          Text(
            text = "Your FitVault is Empty",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "Create and save custom outfits in the 3D Colour Studio.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    } else {
      LazyVerticalGrid(
        columns = GridCells.Fixed(1),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 24.dp),
        modifier = Modifier.fillMaxSize().testTag("fitvault_list")
      ) {
        items(savedFits, key = { it.id }) { fit ->
          SavedFitCard(
            fit = fit,
            onLoad = {
              viewModel.loadFitIntoStudio(fit)
              onLoadIntoStudio()
            },
            onDelete = { viewModel.deleteSavedFit(fit.id) }
          )
        }
      }
    }
  }
}

@Composable
private fun SavedFitCard(
  fit: SavedFit,
  onLoad: () -> Unit,
  onDelete: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
      .testTag("saved_fit_card_${fit.id}"),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = fit.name,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "${fit.date} • ${fit.gender.label} (${fit.bodySize.label})",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        IconButton(onClick = onDelete) {
          Icon(
            Icons.Default.DeleteOutline,
            contentDescription = "Delete fit",
            tint = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Color Palette Row
      Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text("Palette:", style = MaterialTheme.typography.labelSmall, color = MatteGold)
        fit.colorPalette.forEach { hex ->
          Box(
            modifier = Modifier
              .size(16.dp)
              .clip(CircleShape)
              .background(Color(android.graphics.Color.parseColor(hex)))
              .border(1.dp, MatteGold.copy(alpha = 0.5f), CircleShape)
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Garments Summary
      Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(10.dp)) {
          Text(
            text = "• Top: ${fit.top.name}",
            style = MaterialTheme.typography.bodySmall
          )
          Text(
            text = "• Bottom: ${fit.bottom.name}",
            style = MaterialTheme.typography.bodySmall
          )
          Text(
            text = "• Shoes: ${fit.footwear.name}",
            style = MaterialTheme.typography.bodySmall
          )
          if (fit.headwear != null) {
            Text(
              text = "• Headwear: ${fit.headwear.name}",
              style = MaterialTheme.typography.bodySmall
            )
          }
        }
      }

      if (fit.notes.isNotBlank()) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = "“${fit.notes}”",
          style = MaterialTheme.typography.bodySmall,
          fontFamily = FontFamily.Serif,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Action Button
      Button(
        onClick = onLoad,
        modifier = Modifier.fillMaxWidth().testTag("load_fit_${fit.id}"),
        colors = ButtonDefaults.buttonColors(
          containerColor = MatteGold,
          contentColor = Color.Black
        ),
        shape = RoundedCornerShape(8.dp)
      ) {
        Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text("Load into 3D Fitting Room", fontWeight = FontWeight.Bold)
      }
    }
  }
}

// ==========================================
// 5. STYLIST AI (CHAT INTERFACE)
// ==========================================
@Composable
fun StylistAIScreen(
  viewModel: AtelierViewModel,
  onApplyRecommendation: (SavedFit) -> Unit,
  modifier: Modifier = Modifier
) {
  val chatMessages by viewModel.chatMessages.collectAsState()
  val isLoading by viewModel.isChatLoading.collectAsState()
  var inputQuery by remember { mutableStateOf("") }
  val listState = rememberLazyListState()
  val scope = rememberCoroutineScope()

  val promptChips = listOf(
    "Suggest an evening gala look",
    "Monochrome minimalist styling",
    "Palettes for Matte Gold",
    "High-fashion streetwear tips"
  )

  LaunchedEffect(chatMessages.size) {
    if (chatMessages.isNotEmpty()) {
      listState.animateScrollToItem(chatMessages.size - 1)
    }
  }

  Column(modifier = modifier.fillMaxSize()) {
    // Stylist AI Top Header
    Surface(
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 2.dp,
      border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline)
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Surface(
            shape = CircleShape,
            color = MatteGold,
            modifier = Modifier.size(36.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Text(
                text = "A",
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                fontSize = 18.sp,
                color = Color.Black
              )
            }
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = "ATELIER COUTURE AI",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(6.dp)
                  .clip(CircleShape)
                  .background(MatteGold)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "HAUTE SARTORIAL INTELLIGENCE",
                style = MaterialTheme.typography.labelSmall,
                color = MatteGold
              )
            }
          }
        }
      }
    }

    // Quick Inspiration Prompt Chips
    LazyRow(
      contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      items(promptChips) { chip ->
        Surface(
          shape = RoundedCornerShape(16.dp),
          color = MaterialTheme.colorScheme.surfaceVariant,
          border = BorderStroke(1.dp, MatteGold.copy(alpha = 0.4f)),
          modifier = Modifier.clickable {
            viewModel.sendUserMessage(chip)
          }
        ) {
          Text(
            text = chip,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface
          )
        }
      }
    }

    // Scrollable Chat Message List
    LazyColumn(
      state = listState,
      modifier = Modifier
        .weight(1f)
        .fillMaxWidth()
        .padding(horizontal = 16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp),
      contentPadding = PaddingValues(vertical = 12.dp)
    ) {
      items(chatMessages, key = { it.id }) { msg ->
        ChatBubble(
          message = msg,
          onApplyFit = {
            msg.outfitRecommendation?.let { fit ->
              viewModel.loadFitIntoStudio(fit)
              onApplyRecommendation(fit)
            }
          },
          onApplyColor = { hex ->
            viewModel.updateTopColor(hex)
          }
        )
      }

      if (isLoading) {
        item {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(start = 12.dp, top = 4.dp)
          ) {
            CircularProgressIndicator(
              modifier = Modifier.size(16.dp),
              color = MatteGold,
              strokeWidth = 2.dp
            )
            Text(
              text = "Curating sartorial recommendations...",
              style = MaterialTheme.typography.labelSmall,
              color = MatteGold
            )
          }
        }
      }
    }

    // Bottom Input Bar
    Surface(
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 6.dp,
      border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        OutlinedTextField(
          value = inputQuery,
          onValueChange = { inputQuery = it },
          placeholder = { Text("Consult stylist on fabrics, fits, galas...") },
          modifier = Modifier
            .weight(1f)
            .testTag("stylist_chat_input"),
          maxLines = 3,
          shape = RoundedCornerShape(24.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MatteGold,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline
          )
        )

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(
          onClick = {
            if (inputQuery.isNotBlank()) {
              viewModel.sendUserMessage(inputQuery)
              inputQuery = ""
            }
          },
          modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(MatteGold)
            .testTag("send_chat_button")
        ) {
          Icon(
            Icons.AutoMirrored.Filled.Send,
            contentDescription = "Send",
            tint = Color.Black,
            modifier = Modifier.size(20.dp)
          )
        }
      }
    }
  }
}

@Composable
private fun ChatBubble(
  message: ChatMessage,
  onApplyFit: () -> Unit,
  onApplyColor: (String) -> Unit
) {
  val isUser = message.isFromUser

  Column(
    modifier = Modifier.fillMaxWidth(),
    horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
  ) {
    Row(
      horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
      verticalAlignment = Alignment.Bottom,
      modifier = Modifier.fillMaxWidth(0.92f)
    ) {
      if (!isUser) {
        Surface(
          shape = CircleShape,
          color = MatteGold,
          modifier = Modifier
            .size(26.dp)
            .padding(bottom = 2.dp)
        ) {
          Box(contentAlignment = Alignment.Center) {
            Text("A", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color.Black)
          }
        }
        Spacer(modifier = Modifier.width(8.dp))
      }

      Surface(
        shape = RoundedCornerShape(
          topStart = 16.dp,
          topEnd = 16.dp,
          bottomStart = if (isUser) 16.dp else 2.dp,
          bottomEnd = if (isUser) 2.dp else 16.dp
        ),
        color = if (isUser) {
          MaterialTheme.colorScheme.primaryContainer
        } else {
          MaterialTheme.colorScheme.surface
        },
        border = BorderStroke(
          width = 1.dp,
          color = if (isUser) MaterialTheme.colorScheme.outline else MatteGold.copy(alpha = 0.5f)
        ),
        tonalElevation = 2.dp
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text(
            text = message.text,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isUser) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
          )

          // Palette suggestions chips
          if (message.suggestedColors.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            Text("Recommended Swatches:", style = MaterialTheme.typography.labelSmall, color = MatteGold)
            Spacer(modifier = Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              message.suggestedColors.forEach { hex ->
                Box(
                  modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(Color(android.graphics.Color.parseColor(hex)))
                    .border(1.dp, MatteGold, CircleShape)
                    .clickable { onApplyColor(hex) }
                )
              }
            }
          }

          // Embedded Outfit Recommendation Card
          message.outfitRecommendation?.let { rec ->
            Spacer(modifier = Modifier.height(10.dp))
            Card(
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
              border = BorderStroke(1.dp, MatteGold),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(10.dp)) {
                Text(
                  text = "Lookbook Recommendation:",
                  style = MaterialTheme.typography.labelSmall,
                  fontWeight = FontWeight.Bold,
                  color = MatteGold
                )
                Text(
                  text = rec.name,
                  style = MaterialTheme.typography.titleSmall,
                  fontWeight = FontWeight.Bold
                )
                Text(
                  text = "Top: ${rec.top.name} • Pants: ${rec.bottom.name}",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                  onClick = onApplyFit,
                  colors = ButtonDefaults.buttonColors(containerColor = MatteGold, contentColor = Color.Black),
                  shape = RoundedCornerShape(6.dp),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Text("Apply to 3D Mannequin", style = MaterialTheme.typography.labelSmall)
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = message.timestamp,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            modifier = Modifier.align(Alignment.End)
          )
        }
      }
    }
  }
}
