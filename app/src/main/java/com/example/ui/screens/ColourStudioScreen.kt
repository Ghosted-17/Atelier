package com.example.ui.screens

import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Face
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.model.ClothingItem
import com.example.model.Gender
import com.example.model.SkinTone
import com.example.ui.theme.MatteGold
import com.example.viewmodel.AtelierViewModel
import kotlinx.coroutines.flow.collectLatest

enum class StudioDrawerSection(val label: String) {
    COMPLEXION("Skin"),
    CAPS("Head"),
    TOPS("Top"),
    BOTTOMS("Bottom"),
    GOWNS("Full Fit"),
    KICKS("Shoes"),
    ACCS("Accs")
}

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

    // Active bottom tray menu (null = closed/full stage visible)
    var activeSection by remember { mutableStateOf<StudioDrawerSection?>(null) }

    LaunchedEffect(Unit) {
        viewModel.jsCommandFlow.collectLatest { command ->
            webViewRef?.post {
                webViewRef?.evaluateJavascript(command, null)
            }
        }
    }

    LaunchedEffect(gender, bodyType) {
        val g = gender.name.lowercase()
        val t = bodyType.name.lowercase()
        val cmd = "window.setGender && window.setGender('$g'); window.setBodyType && window.setBodyType('$t');"
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

        val script = "window.updateColors && window.updateColors('$topHex', '$botHex', '$shoeHex', '$headHex', '$accHex', '$skinHex');"
        webViewRef?.evaluateJavascript(script, null)
    }

    LaunchedEffect(isDark) {
        webViewRef?.evaluateJavascript("window.setTheme && window.setTheme($isDark);", null)
    }

    Box(modifier = modifier.fillMaxSize().background(if (isDark) Color(0xFF111111) else Color(0xFFFAFAFA))) {
        // ==========================================
        // 1. FULL-SCREEN 3D WEBVIEW AVATAR STAGE
        // ==========================================
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
                    settings.allowFileAccessFromFileURLs = true
                    settings.allowUniversalAccessFromFileURLs = true
                    settings.databaseEnabled = true
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

        // ==========================================
        // 2. TOP FLOATING CONTROLS (Gender, Body Type, Save)
        // ==========================================
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(top = 10.dp, start = 16.dp, end = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Gender Segmented Pill
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = Color.Black.copy(alpha = 0.65f),
                    border = BorderStroke(1.dp, MatteGold.copy(alpha = 0.5f))
                ) {
                    Row(modifier = Modifier.padding(3.dp)) {
                        Gender.values().forEach { g ->
                            val isSelected = gender == g
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(if (isSelected) MatteGold else Color.Transparent)
                                    .clickable { viewModel.setGender(g) }
                                    .padding(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = g.label,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.Black else Color.White
                                )
                            }
                        }
                    }
                }

                // Save Look Floating Button
                Button(
                    onClick = { showSaveDialog = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black.copy(alpha = 0.65f),
                        contentColor = MatteGold
                    ),
                    border = BorderStroke(1.dp, MatteGold.copy(alpha = 0.8f)),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Outlined.BookmarkBorder, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Save", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Body Type Chips Row
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(BodyType.values()) { type ->
                    val isSelected = bodyType == type
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setBodyType(type) },
                        label = {
                            Text(type.label, style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp))
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MatteGold,
                            selectedLabelColor = Color.Black,
                            containerColor = Color.Black.copy(alpha = 0.5f),
                            labelColor = Color.White
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) MatteGold else Color.White.copy(alpha = 0.25f)
                        )
                    )
                }
            }
        }

        // Quick 360 Rotation Button Floating on Right
        Surface(
            shape = CircleShape,
            color = Color.Black.copy(alpha = 0.6f),
            border = BorderStroke(1.dp, MatteGold.copy(alpha = 0.7f)),
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 14.dp)
        ) {
            IconButton(
                onClick = { viewModel.setRotationDegrees(rotationDegrees + 45f) },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(Icons.Default.RotateRight, contentDescription = "Rotate 360", tint = MatteGold, modifier = Modifier.size(20.dp))
            }
        }

        // ==========================================
        // 3. SNAPCHAT-STYLE FOOTER FLOATING SYSTEM
        // ==========================================
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            // Expandable Selection Drawer (Slides up over footer when an icon is tapped)
            AnimatedVisibility(
                visible = activeSection != null,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                Surface(
                    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                    color = Color(0xEE161616),
                    border = BorderStroke(1.dp, MatteGold.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = activeSection?.label?.uppercase() ?: "",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MatteGold,
                                letterSpacing = 1.sp
                            )
                            IconButton(onClick = { activeSection = null }, modifier = Modifier.size(26.dp)) {
                                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.LightGray, modifier = Modifier.size(16.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        when (activeSection) {
                            StudioDrawerSection.COMPLEXION -> {
                                StudioComplexionView(
                                    selectedSkinTone = selectedSkinTone,
                                    skinTones = viewModel.skinTonePresets,
                                    onSelectSkinTone = { viewModel.selectSkinTone(it) },
                                    onApplyPaletteColor = { viewModel.updateBottomColor(it) }
                                )
                            }
                            StudioDrawerSection.TOPS -> {
                                WardrobeCategoryDrawer(
                                    items = viewModel.catalogTops,
                                    selectedItem = selectedTop,
                                    onSelectItem = { viewModel.selectTop(it) },
                                    onUpdateColor = { viewModel.updateTopColor(it) },
                                    palettePresets = viewModel.colorPalettePresets
                                )
                            }
                            StudioDrawerSection.BOTTOMS -> {
                                WardrobeCategoryDrawer(
                                    items = viewModel.catalogBottoms,
                                    selectedItem = selectedBottom,
                                    onSelectItem = { viewModel.selectBottom(it) },
                                    onUpdateColor = { viewModel.updateBottomColor(it) },
                                    palettePresets = viewModel.colorPalettePresets
                                )
                            }
                            StudioDrawerSection.GOWNS -> {
                                // Full Gown / Full-body fit section
                                WardrobeCategoryDrawer(
                                    items = viewModel.catalogTops,
                                    selectedItem = selectedTop,
                                    onSelectItem = { viewModel.selectTop(it) },
                                    onUpdateColor = { viewModel.updateTopColor(it) },
                                    palettePresets = viewModel.colorPalettePresets
                                )
                            }
                            StudioDrawerSection.KICKS -> {
                                WardrobeCategoryDrawer(
                                    items = viewModel.catalogFootwear,
                                    selectedItem = selectedFootwear,
                                    onSelectItem = { viewModel.selectFootwear(it) },
                                    onUpdateColor = { viewModel.updateFootwearColor(it) },
                                    palettePresets = viewModel.colorPalettePresets
                                )
                            }
                            StudioDrawerSection.CAPS -> {
                                WardrobeCategoryDrawer(
                                    items = viewModel.catalogHeadwear,
                                    selectedItem = selectedHeadwear,
                                    onSelectItem = { viewModel.selectHeadwear(it) },
                                    onUpdateColor = { viewModel.updateHeadwearColor(it) },
                                    palettePresets = viewModel.colorPalettePresets
                                )
                            }
                            StudioDrawerSection.ACCS -> {
                                WardrobeCategoryDrawer(
                                    items = viewModel.catalogAccessories,
                                    selectedItem = selectedAccessories,
                                    onSelectItem = { viewModel.selectAccessories(it) },
                                    onUpdateColor = { },
                                    palettePresets = viewModel.colorPalettePresets
                                )
                            }
                            null -> {}
                        }
                    }
                }
            }

            // Snapchat-Style Bottom Icon Bar
            Surface(
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                color = Color(0xF20F0F0F),
                border = BorderStroke(0.5.dp, Color.White.copy(alpha = 0.12f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val isFemale = gender == Gender.FEMALE

                    SnapIconItem("Skin", "✨", activeSection == StudioDrawerSection.COMPLEXION) {
                        activeSection = if (activeSection == StudioDrawerSection.COMPLEXION) null else StudioDrawerSection.COMPLEXION
                    }
                    SnapIconItem("Tops", "👕", activeSection == StudioDrawerSection.TOPS) {
                        activeSection = if (activeSection == StudioDrawerSection.TOPS) null else StudioDrawerSection.TOPS
                    }
                    SnapIconItem("Pants", "👖", activeSection == StudioDrawerSection.BOTTOMS) {
                        activeSection = if (activeSection == StudioDrawerSection.BOTTOMS) null else StudioDrawerSection.BOTTOMS
                    }

                    if (isFemale) {
                        SnapIconItem("Gowns", "👗", activeSection == StudioDrawerSection.GOWNS) {
                            activeSection = if (activeSection == StudioDrawerSection.GOWNS) null else StudioDrawerSection.GOWNS
                        }
                    }

                    SnapIconItem("Kicks", "👟", activeSection == StudioDrawerSection.KICKS) {
                        activeSection = if (activeSection == StudioDrawerSection.KICKS) null else StudioDrawerSection.KICKS
                    }
                    SnapIconItem("Caps", "🧢", activeSection == StudioDrawerSection.CAPS) {
                        activeSection = if (activeSection == StudioDrawerSection.CAPS) null else StudioDrawerSection.CAPS
                    }
                    SnapIconItem("Accs", "🕶️", activeSection == StudioDrawerSection.ACCS) {
                        activeSection = if (activeSection == StudioDrawerSection.ACCS) null else StudioDrawerSection.ACCS
                    }
                }
            }
        }
    }

    // Save Look Dialog
    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = { Text("Save Look to FitVault", style = MaterialTheme.typography.titleLarge) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newFitName,
                        onValueChange = { newFitName = it },
                        label = { Text("Lookbook Title") },
                        placeholder = { Text("e.g. Milan Gala Monochrome") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newFitNotes,
                        onValueChange = { newFitNotes = it },
                        label = { Text("Styling Notes") },
                        placeholder = { Text("e.g. Oversized hoodie with chunky sneakers") },
                        modifier = Modifier.fillMaxWidth()
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
                    colors = ButtonDefaults.buttonColors(containerColor = MatteGold, contentColor = Color.Black)
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
private fun SnapIconItem(
    title: String,
    emoji: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(if (isSelected) MatteGold else Color.White.copy(alpha = 0.08f))
                .border(
                    width = 1.dp,
                    color = if (isSelected) MatteGold else Color.Transparent,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(text = emoji, fontSize = 20.sp)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) MatteGold else Color.LightGray
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
    Column(modifier = Modifier.fillMaxWidth()) {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(skinTones) { tone ->
                val isSelected = selectedSkinTone.id == tone.id
                val color = Color(android.graphics.Color.parseColor(tone.hex))
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(color)
                        .border(
                            width = if (isSelected) 2.5.dp else 1.dp,
                            color = if (isSelected) MatteGold else Color.Gray.copy(alpha = 0.4f),
                            shape = CircleShape
                        )
                        .clickable { onSelectSkinTone(tone) },
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
        // Color row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
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
                        .clickable { onUpdateColor(opt.hex) },
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

        Spacer(modifier = Modifier.height(8.dp))

        // Garment Cuts Carousel
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(items) { item ->
                val isSelected = selectedItem?.id == item.id
                Card(
                    modifier = Modifier
                        .width(140.dp)
                        .clickable { onSelectItem(item) },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) Color(0xFF242424) else Color(0xFF191919)
                    ),
                    border = BorderStroke(
                        width = if (isSelected) 1.5.dp else 0.5.dp,
                        color = if (isSelected) MatteGold else Color.White.copy(alpha = 0.15f)
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(
                            text = item.name,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) MatteGold else Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = item.subTitle,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                            color = Color.LightGray,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}