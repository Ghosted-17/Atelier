package com.example.ui.screens

import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.model.BodyType
import com.example.model.ClothingCategory
import com.example.model.ClothingItem
import com.example.model.Gender
import com.example.model.SkinTone
import com.example.ui.theme.MatteGold
import com.example.viewmodel.AtelierViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ColourStudioScreen(
  viewModel: AtelierViewModel,
  modifier: Modifier = Modifier
) {
  val gender by viewModel.gender.collectAsState()
  val bodyType by viewModel.bodyType.collectAsState()
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
  var selectedTabCategory by remember { mutableIntStateOf(0) }
  val categories = listOf("Complexion", "Caps", "Tops", "Bottoms", "Kicks", "Accs")

  // Observe Javascript command dispatch
  LaunchedEffect(Unit) {
    viewModel.jsCommandFlow.collectLatest { command ->
      webViewRef?.post {
        webViewRef?.evaluateJavascript(command, null)
      }
    }
  }

  // Synchronize when gender or bodyType changes
  LaunchedEffect(gender, bodyType) {
    val g = gender.name.lowercase()
    val t = bodyType.name.lowercase()
    val cmd = "window.setGender && window.setGender('$g'); window.setBodyType && window.setBodyType('$t'); window.loadModel && window.loadModel('$g', '$t');"
    webViewRef?.evaluateJavascript(cmd, null)
  }

  // Synchronize garment cuts and colors
  LaunchedEffect(
    selectedTop.colorHex,
    selectedTop.meshNodeName,
    selectedBottom.colorHex,
    selectedBottom.meshNodeName,
    selectedFootwear.colorHex,
    selectedFootwear.meshNodeName,
    selectedHeadwear?.colorHex,
    selectedHeadwear?.meshNodeName,
    selectedAccessories?.colorHex,
    selectedAccessories?.meshNodeName,
    selectedSkinTone.hex
  ) {
    val topHex = selectedTop.colorHex
    val botHex = selectedBottom.colorHex
    val shoeHex = selectedFootwear.colorHex
    val headHex = selectedHeadwear?.colorHex ?: "#111111"
    val accHex = selectedAccessories?.colorHex ?: "#D4AF37"
    val skinHex = selectedSkinTone.hex

    val topMesh = selectedTop.meshNodeName
    val botMesh = selectedBottom.meshNodeName
    val shoeMesh = selectedFootwear.meshNodeName
    val headMesh = selectedHeadwear?.meshNodeName ?: "none"
    val accMesh = selectedAccessories?.meshNodeName ?: "none"

    val script = buildString {
      append("window.updateColors && window.updateColors('$topHex', '$botHex', '$shoeHex', '$headHex', '$accHex', '$skinHex'); ")
      append("window.setSkinTone && window.setSkinTone('$skinHex'); ")
      append("window.setGarment && window.setGarment('top', '$topMesh'); ")
      append("window.setGarmentColor && window.setGarmentColor('top', '$topHex'); ")
      append("window.setGarment && window.setGarment('bottom', '$botMesh'); ")
      append("window.setGarmentColor && window.setGarmentColor('bottom', '$botHex'); ")
      append("window.setGarment && window.setGarment('footwear', '$shoeMesh'); ")
      append("window.setGarmentColor && window.setGarmentColor('footwear', '$shoeHex'); ")
      append("window.setGarment && window.setGarment('headwear', '$headMesh'); ")
      append("window.setGarmentColor && window.setGarmentColor('headwear', '$headHex'); ")
      append("window.setGarment && window.setGarment('accessories', '$accMesh'); ")
      append("window.setGarmentColor && window.setGarmentColor('accessories', '$accHex');")
    }
    webViewRef?.evaluateJavascript(script, null)
  }

  LaunchedEffect(isDark) {
    webViewRef?.evaluateJavascript("window.setTheme && window.setTheme($isDark);", null)
  }

  Column(modifier = modifier.fillMaxSize()) {
    // 1. Header Controls: Gender Selector & Save Lookbook
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 6.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Gender Segmented Control Pill
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

      // Save Look to Vault Button
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
        Text("Save Look", style = MaterialTheme.typography.labelMedium)
      }
    }

    // 2. Body Type Morph Selector (SLIM, ATHLETIC, BROAD, CURVY)
    LazyRow(
      contentPadding = PaddingValues(horizontal = 16.dp, vertical = 2.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      items(BodyType.values()) { type ->
        val isSelected = bodyType == type
        FilterChip(
          selected = isSelected,
          onClick = { viewModel.setBodyType(type) },
          label = {
            Text(
              text = "${type.label} (${type.description.take(8)})",
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
          modifier = Modifier.testTag("body_type_chip_${type.name.lowercase()}")
        )
      }
    }

    // 3. Quick-Access Skin Complexion Swatch Strip
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

    // 4. Main 3D Avatar Runway Stage (AndroidView WebView)
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

      // Turntable Rotation Helper Floating on Bottom Right
      Row(
        modifier = Modifier
          .align(Alignment.BottomEnd)
          .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          shape = CircleShape,
          color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
          border = BorderStroke(1.dp, MatteGold),
          shadowElevation = 4.dp
        ) {
          IconButton(
            onClick = { viewModel.setRotationDegrees(rotationDegrees + 45f) },
            modifier = Modifier
              .size(42.dp)
              .testTag("quick_rotate_button")
          ) {
            Icon(
              imageVector = Icons.Default.RotateRight,
              contentDescription = "Rotate 360",
              tint = MatteGold,
              modifier = Modifier.size(20.dp)
            )
          }
        }
      }
    }

    // 5. Wardrobe Drawer (Bottom)
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
          0 -> StudioComplexionView(
            selectedSkinTone = selectedSkinTone,
            skinTones = viewModel.skinTonePresets,
            onSelectSkinTone = { viewModel.selectSkinTone(it) },
            onApplyPaletteColor = { viewModel.updateBottomColor(it) }
          )
          1 -> WardrobeCategoryDrawer(
            items = viewModel.catalogHeadwear,
            selectedItem = selectedHeadwear,
            onSelectItem = { viewModel.selectHeadwear(it) },
            onUpdateColor = { viewModel.updateHeadwearColor(it) },
            palettePresets = viewModel.colorPalettePresets
          )
          2 -> WardrobeCategoryDrawer(
            items = viewModel.catalogTops,
            selectedItem = selectedTop,
            onSelectItem = { viewModel.selectTop(it) },
            onUpdateColor = { viewModel.updateTopColor(it) },
            palettePresets = viewModel.colorPalettePresets
          )
          3 -> WardrobeCategoryDrawer(
            items = viewModel.catalogBottoms,
            selectedItem = selectedBottom,
            onSelectItem = { viewModel.selectBottom(it) },
            onUpdateColor = { viewModel.updateBottomColor(it) },
            palettePresets = viewModel.colorPalettePresets
          )
          4 -> WardrobeCategoryDrawer(
            items = viewModel.catalogFootwear,
            selectedItem = selectedFootwear,
            onSelectItem = { viewModel.selectFootwear(it) },
            onUpdateColor = { viewModel.updateFootwearColor(it) },
            palettePresets = viewModel.colorPalettePresets
          )
          5 -> WardrobeCategoryDrawer(
            items = viewModel.catalogAccessories,
            selectedItem = selectedAccessories,
            onSelectItem = { viewModel.selectAccessories(it) },
            onUpdateColor = { /* metallic sheen */ },
            palettePresets = viewModel.colorPalettePresets
          )
        }
      }
    }
  }

  // Save Look Dialog
  if (showSaveDialog) {
    AlertDialog(
      onDismissRequest = { showSaveDialog = false },
      title = {
        Text("Save Look to FitVault", style = MaterialTheme.typography.titleLarge)
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text(
            "Archive this 3D avatar configuration with a custom title and styling notes.",
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
            placeholder = { Text("e.g. Oversized drop hoodie with matte gold sneakers") },
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
          Text("Archive Look")
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
private fun StudioComplexionView(
  selectedSkinTone: SkinTone,
  skinTones: List<SkinTone>,
  onSelectSkinTone: (SkinTone) -> Unit,
  onApplyPaletteColor: (String) -> Unit
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 4.dp)
      .testTag("skin_tone_customizer_view")
  ) {
    // Active Complexion Spotlight Card
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

    // Inclusive Complexion Spectrum Carousel
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

    // Recommended Fabric Harmony
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
          text = "Tap to dress trunks",
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
private fun WardrobeCategoryDrawer(
  items: List<ClothingItem>,
  selectedItem: ClothingItem?,
  onSelectItem: (ClothingItem) -> Unit,
  onUpdateColor: (String) -> Unit,
  palettePresets: List<com.example.model.ColorOption>
) {
  Column(modifier = Modifier.fillMaxWidth()) {
    // Horizontal Swatch Palette Row (Color Wheel / Presets)
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState())
        .padding(horizontal = 16.dp, vertical = 4.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "MATTE PALETTE:",
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

    // Stylized Modular Garment Cuts Carousel
    LazyRow(
      contentPadding = PaddingValues(horizontal = 16.dp),
      horizontalArrangement = Arrangement.spacedBy(10.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      items(items) { item ->
        val isSelected = selectedItem?.id == item.id
        Card(
          modifier = Modifier
            .width(175.dp)
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
                  text = "SNAP ON",
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
              text = "${item.subTitle} • ${item.material}",
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
