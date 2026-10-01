package com.example.viewmodel

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.BodySize
import com.example.model.BodyType
import com.example.model.BrandComboRecommendation
import com.example.model.BrandProfile
import com.example.model.ChatMessage
import com.example.model.ClothingCategory
import com.example.model.ClothingItem
import com.example.model.ColorOption
import com.example.model.FeedItem
import com.example.model.FitRatingBreakdown
import com.example.model.Gender
import com.example.model.SavedFit
import com.example.model.SkinTone
import com.example.model.UserFitCheck
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class AtelierViewModel : ViewModel() {

  // Theme state
  private val _isDarkMode = MutableStateFlow(true)
  val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

  // 3D Stylized Bitmoji Fitting Room State
  private val _gender = MutableStateFlow(Gender.MALE)
  val gender: StateFlow<Gender> = _gender.asStateFlow()

  private val _bodyType = MutableStateFlow(BodyType.SLIM)
  val bodyType: StateFlow<BodyType> = _bodyType.asStateFlow()
  val bodySize: StateFlow<BodySize> get() = _bodyType.asStateFlow()

  private val _rotationDegrees = MutableStateFlow(0f)
  val rotationDegrees: StateFlow<Float> = _rotationDegrees.asStateFlow()

  // Javascript evaluation commands channel for the 3D WebView
  private val _jsCommandFlow = MutableSharedFlow<String>(extraBufferCapacity = 16)
  val jsCommandFlow: SharedFlow<String> = _jsCommandFlow.asSharedFlow()

  // Available Color Presets
  val colorPalettePresets = listOf(
    ColorOption("Noir Onyx", "#111111"),
    ColorOption("Pure Alabaster", "#F5F5F0"),
    ColorOption("Matte Gold", "#D4AF37"),
    ColorOption("Midnight Navy", "#1B263B"),
    ColorOption("Rich Camel", "#C19A6B"),
    ColorOption("Terracotta", "#A0522D"),
    ColorOption("Bordeaux Wine", "#5B1424"),
    ColorOption("Olive Drab", "#4B5320"),
    ColorOption("Sage Celadon", "#8A9A86"),
    ColorOption("Charcoal Slate", "#333333")
  )

  // Catalog of Stylized Snap-On Wardrobe Items
  val catalogHeadwear = listOf(
    ClothingItem("h1", "Gilded Atelier Snapback", ClothingCategory.HEADWEAR, "cap_baseball", "#111111", "#111111", "Snapback Cap", "Structured Twill"),
    ClothingItem("h2", "Minimalist Knit Beanie", ClothingCategory.HEADWEAR, "beanie_knit", "#F5F5F0", "#F5F5F0", "Knit Cap", "Merino Rib"),
    ClothingItem("h3", "Structured Wool Fedora", ClothingCategory.HEADWEAR, "fedora_felt", "#111111", "#111111", "Wide Brim", "100% Felt"),
    ClothingItem("h4", "Sculptural Leather Beret", ClothingCategory.HEADWEAR, "beret_leather", "#5B1424", "#5B1424", "Rue St-Honoré", "Calfskin"),
    ClothingItem("h0", "No Headwear (Bare)", ClothingCategory.HEADWEAR, "none", "#000000", "#000000", "Natural Hair", "Bare Silhouette")
  )

  val catalogTops = listOf(
    ClothingItem("t1", "Oversized Street Hoodie", ClothingCategory.TOP, "hoodie_oversized", "#111111", "#111111", "Drop Shoulder", "Heavy French Terry"),
    ClothingItem("t2", "Tailored Wool Blazer", ClothingCategory.TOP, "blazer_tailored", "#1B263B", "#1B263B", "Sartorial Suiting", "Double-Breasted Twill"),
    ClothingItem("t3", "Boxy Minimalist Tee", ClothingCategory.TOP, "tee_boxy", "#F5F5F0", "#F5F5F0", "Streetwear Essential", "280gsm Cotton"),
    ClothingItem("t4", "Varsity Bomber Jacket", ClothingCategory.TOP, "jacket_varsity", "#D4AF37", "#D4AF37", "Letterman Cut", "Satin & Leather"),
    ClothingItem("t5", "Cashmere Polo", ClothingCategory.TOP, "polo_knit", "#C19A6B", "#C19A6B", "Clean Preppy", "12-Gauge Knit"),
    ClothingItem("t6", "Ribbed Athletic Tank", ClothingCategory.TOP, "tank_ribbed", "#A0522D", "#A0522D", "Sculpted Fit", "Organic Cotton")
  )

  val catalogBottoms = listOf(
    ClothingItem("b1", "Baggy Denim Jeans", ClothingCategory.BOTTOM, "jeans_baggy", "#1B263B", "#1B263B", "90s Skate Fit", "14oz Selvedge"),
    ClothingItem("b2", "Pleated Wide-Leg Trousers", ClothingCategory.BOTTOM, "trousers_wide", "#F5F5F0", "#F5F5F0", "High-Waist Drape", "Heavy Crepe"),
    ClothingItem("b3", "Utility Cargo Pants", ClothingCategory.BOTTOM, "cargo_pants", "#4B5320", "#4B5320", "Architectural", "Ripstop Cotton"),
    ClothingItem("b4", "Designer Boxer Trunks", ClothingCategory.BOTTOM, "shorts_boxer", "#18181C", "#18181C", "Lounge Fit", "Mercerized Jersey"),
    ClothingItem("b5", "Relaxed Street Joggers", ClothingCategory.BOTTOM, "joggers_sport", "#111111", "#111111", "Cuffed Hem", "French Terry")
  )

  val catalogFootwear = listOf(
    ClothingItem("f1", "Chunky Retro Sneakers", ClothingCategory.FOOTWEAR, "sneakers_chunky", "#F5F5F0", "#F5F5F0", "Dad Shoe Profile", "Leather & Mesh"),
    ClothingItem("f2", "Platform Leather Loafers", ClothingCategory.FOOTWEAR, "loafers_platform", "#111111", "#111111", "Lug Sole", "Polished Calfskin"),
    ClothingItem("f3", "Gilded Sole Derbies", ClothingCategory.FOOTWEAR, "derby_shoes", "#D4AF37", "#D4AF37", "Formal Sartorial", "Patent Leather"),
    ClothingItem("f4", "Chelsea Suede Boots", ClothingCategory.FOOTWEAR, "boots_chelsea", "#C19A6B", "#C19A6B", "Artisanal", "Waxy Suede"),
    ClothingItem("f5", "Bare Sculpted Feet", ClothingCategory.FOOTWEAR, "bare_feet", "#EEDBC8", "#EEDBC8", "Natural Stance", "Runway")
  )

  val catalogAccessories = listOf(
    ClothingItem("a1", "24K Matte Gold Cuban Chain", ClothingCategory.ACCESSORIES, "chain_gold", "#D4AF37", "#D4AF37", "Atelier Jewelry", "Gold-Plated Brass"),
    ClothingItem("a2", "Leather Crossbody Pouch", ClothingCategory.ACCESSORIES, "bag_crossbody", "#111111", "#111111", "Leather Goods", "Grained Leather"),
    ClothingItem("a3", "Acetate Fashion Shades", ClothingCategory.ACCESSORIES, "glasses_shades", "#111111", "#111111", "UV400 Polarized", "Black Acetate")
  )

  // Currently Selected Wardrobe Configuration
  private val _selectedHeadwear = MutableStateFlow<ClothingItem?>(catalogHeadwear[0])
  val selectedHeadwear: StateFlow<ClothingItem?> = _selectedHeadwear.asStateFlow()

  private val _selectedTop = MutableStateFlow(catalogTops[0])
  val selectedTop: StateFlow<ClothingItem> = _selectedTop.asStateFlow()

  private val _selectedBottom = MutableStateFlow(catalogBottoms[0])
  val selectedBottom: StateFlow<ClothingItem> = _selectedBottom.asStateFlow()

  private val _selectedFootwear = MutableStateFlow(catalogFootwear[0])
  val selectedFootwear: StateFlow<ClothingItem> = _selectedFootwear.asStateFlow()

  private val _selectedAccessories = MutableStateFlow<ClothingItem?>(catalogAccessories[0])
  val selectedAccessories: StateFlow<ClothingItem?> = _selectedAccessories.asStateFlow()

  // Available Skin Tone Presets & Selection
  val skinTonePresets: List<SkinTone> = SkinTone.DEFAULT_PRESETS

  private val _selectedSkinTone = MutableStateFlow(SkinTone.DEFAULT_PRESETS[1]) // Default: Warm Ivory
  val selectedSkinTone: StateFlow<SkinTone> = _selectedSkinTone.asStateFlow()

  // FitVault: Saved Fits List
  private val _savedFits = MutableStateFlow<List<SavedFit>>(emptyList())
  val savedFits: StateFlow<List<SavedFit>> = _savedFits.asStateFlow()

  // Style Feed Items
  private val _feedCategory = MutableStateFlow("All")
  val feedCategory: StateFlow<String> = _feedCategory.asStateFlow()

  private val _feedItems = MutableStateFlow<List<FeedItem>>(emptyList())
  val feedItems: StateFlow<List<FeedItem>> = _feedItems.asStateFlow()

  // Stylist AI Messages
  private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
  val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

  private val _isChatLoading = MutableStateFlow(false)
  val isChatLoading: StateFlow<Boolean> = _isChatLoading.asStateFlow()

  // FitCheck Rater State
  private val _activeUserFitCheck = MutableStateFlow<UserFitCheck?>(null)
  val activeUserFitCheck: StateFlow<UserFitCheck?> = _activeUserFitCheck.asStateFlow()

  private val _isAnalyzingFit = MutableStateFlow(false)
  val isAnalyzingFit: StateFlow<Boolean> = _isAnalyzingFit.asStateFlow()

  // ==========================================
  // BRAND SEARCH SPACE & RECOMMENDATION STATE
  // ==========================================
  val brandDirectory: List<BrandProfile> = listOf(
    BrandProfile(
      id = "brand-zara",
      name = "Zara",
      tier = "Contemporary High-Street",
      tierCategory = "High-Street",
      rating = 8.6f,
      reviewsCount = 18450,
      priceTier = "$$",
      origin = "Spain (Arteixo)",
      foundedYear = 1975,
      signatureVibe = "Rapid runway translation with sleek European tailoring and clean structural shapes.",
      keyStrengths = listOf("Oversized Double-Breasted Blazers", "High-Rise Pleated Slacks", "Modern Trend Agility"),
      comboRecommendation = BrandComboRecommendation(
        title = "The High-Low Sartorial Contrast",
        description = "Anchor a Zara oversized structured wool-blend blazer with liquid drape alabaster trousers, contrasting accessible fast-fashion agility with quiet luxury footwear.",
        topPiece = "Zara Relaxed Double-Breasted Blazer (Noir)",
        bottomPiece = "Pleated Fluid Barathea Trousers (Pure Alabaster)",
        footwearPiece = "Polished Spazzolato Chunky Loafers (Matte Gold Welt)",
        accessoriesPiece = "Brushed Matte Gold Signet Ring & Leather Crossbody Pouch",
        palette = listOf("#111111", "#F5F5F0", "#D4AF37", "#333333"),
        stylingRule = "Roll blazer cuffs slightly to reveal tailored wrists; avoid loud branding to cultivate effortless high-fashion intrigue."
      ),
      flagshipAddress = "Paseo de la Castellana 79, Nuevos Ministerios, 28046 Madrid, Spain",
      websiteUrl = "https://www.zara.com",
      storePhone = "+34 915 55 93 00",
      mapsQuery = "Zara Paseo de la Castellana 79 Madrid Spain"
    ),
    BrandProfile(
      id = "brand-tommy",
      name = "Tommy Hilfiger",
      tier = "American Heritage Prep",
      tierCategory = "Heritage & Prep",
      rating = 8.7f,
      reviewsCount = 12900,
      priceTier = "$$$",
      origin = "USA (New York)",
      foundedYear = 1985,
      signatureVibe = "Classic American Cool with bold collegiate color-blocking and nautical tailoring.",
      keyStrengths = listOf("Heavyweight Oxford Shirts", "Varsity Bombers", "Structured Linen Chinos"),
      comboRecommendation = BrandComboRecommendation(
        title = "Modern Ivy League Architecture",
        description = "Reinterpret Tommy Hilfiger's collegiate DNA by pairing a midnight navy relaxed Oxford shirt with rich camel pleated chinos and clean alabaster minimal kicks.",
        topPiece = "Tommy Hilfiger Relaxed Heavyweight Oxford (Midnight Navy)",
        bottomPiece = "Tailored High-Rise Linen Chinos (Rich Camel)",
        footwearPiece = "Minimalist Full-Grain Nappa Low-Tops (Pure Alabaster)",
        accessoriesPiece = "24K Matte Gold Link Watch & Tortoiseshell Frames",
        palette = listOf("#1B263B", "#C19A6B", "#F5F5F0", "#D4AF37"),
        stylingRule = "Unbutton the top two collar buttons and half-tuck into high-waisted pleated chinos for modern sprezzatura."
      ),
      flagshipAddress = "681 5th Ave, Midtown Manhattan, New York, NY 10022, USA",
      websiteUrl = "https://usa.tommy.com",
      storePhone = "+1 (212) 750-0055",
      mapsQuery = "Tommy Hilfiger 681 5th Ave New York NY"
    ),
    BrandProfile(
      id = "brand-ralph",
      name = "Ralph Lauren",
      tier = "Old Money Sartorial & Purple Label",
      tierCategory = "Heritage & Prep",
      rating = 9.4f,
      reviewsCount = 16800,
      priceTier = "$$$$",
      origin = "USA (New York)",
      foundedYear = 1967,
      signatureVibe = "Unapologetic heritage luxury, equestrian grace, and peerless bespoke suiting.",
      keyStrengths = listOf("Cable-Knit Cashmere Sweaters", "Double-Faced Flannel Suiting", "Penny Loafers"),
      comboRecommendation = BrandComboRecommendation(
        title = "The Hamptons Equestrian Quiet Luxury",
        description = "A buttery camel cashmere knit layered under an unconstructed navy blazer, paired with tailored alabaster trousers and burnished leather derby footwear.",
        topPiece = "Ralph Lauren Cashmere Cable-Knit Polo (Rich Camel)",
        bottomPiece = "Super 130s Flannel Pleated Slacks (Noir Onyx)",
        footwearPiece = "Burnished Calfskin Penny Loafers (Gilded Welt)",
        accessoriesPiece = "Silk Pocket Square & Matte Gold Equestrian Buckle Belt",
        palette = listOf("#C19A6B", "#111111", "#D4AF37", "#F5F5F0"),
        stylingRule = "Embrace tactile knit texture against flannel; let the camel tone ground the sharp monochrome bottom."
      ),
      flagshipAddress = "888 Madison Ave (Rhinelander Mansion), New York, NY 10021, USA",
      websiteUrl = "https://www.ralphlauren.com",
      storePhone = "+1 (212) 434-8000",
      mapsQuery = "Ralph Lauren Rhinelander Mansion 888 Madison Ave New York"
    ),
    BrandProfile(
      id = "brand-cos",
      name = "COS",
      tier = "Architectural Minimalist",
      tierCategory = "Quiet Luxury",
      rating = 8.9f,
      reviewsCount = 8900,
      priceTier = "$$",
      origin = "Sweden / UK (London)",
      foundedYear = 2007,
      signatureVibe = "Sculptural forms, reinvented wardrobe classics, and uncompromising fabric integrity.",
      keyStrengths = listOf("Boxy Poplin Overshirts", "Origami Fold Pleat Trousers", "Cashmere Mock Necks"),
      comboRecommendation = BrandComboRecommendation(
        title = "Nordic Brutalist Monolith",
        description = "A boxy minimalist poplin overshirt in charcoal slate paired with high-volume pleated wide-leg trousers and architectural square-toe derby shoes.",
        topPiece = "COS Crisp Organic Poplin Boxy Shirt (Charcoal Slate)",
        bottomPiece = "Architectural Wide-Leg Pleated Pants (Noir Onyx)",
        footwearPiece = "Square-Toe Architectural Derby Shoes (Matte Gold Accent)",
        accessoriesPiece = "Minimalist Leather Crossbody Camera Pouch",
        palette = listOf("#333333", "#111111", "#F5F5F0", "#D4AF37"),
        stylingRule = "Strictly adhere to clean geometry. Keep jewelry limited to one matte gold chain or architectural cuff."
      ),
      flagshipAddress = "222 Regent St, Mayfair, London W1B 5BD, United Kingdom",
      websiteUrl = "https://www.cos.com",
      storePhone = "+44 20 7478 0400",
      mapsQuery = "COS 222 Regent St London W1B 5BD"
    ),
    BrandProfile(
      id = "brand-massimo",
      name = "Massimo Dutti",
      tier = "Elevated European Tailoring",
      tierCategory = "Quiet Luxury",
      rating = 9.0f,
      reviewsCount = 11200,
      priceTier = "$$$",
      origin = "Spain (Barcelona)",
      foundedYear = 1985,
      signatureVibe = "Sophisticated Mediterranean elegance, unconstructed soft tailoring, and noble natural fibers.",
      keyStrengths = listOf("Unstructured Linen-Silk Blazers", "Tonal Knit Polos", "Suede Chelsea Boots"),
      comboRecommendation = BrandComboRecommendation(
        title = "Riviera Sartorial Flâneur",
        description = "An unconstructed midnight navy blazer draped casually over an alabaster silk-cotton polo, grounded with rich camel linen slacks and waxy suede Chelsea boots.",
        topPiece = "Massimo Dutti Soft Tailored Blazer (Midnight Navy)",
        bottomPiece = "Irish Linen High-Rise Chinos (Rich Camel)",
        footwearPiece = "Artisanal Waxy Suede Chelsea Boots (Burnished Tan)",
        accessoriesPiece = "Braided Leather Belt & Gold-Rimmed Acetate Sunglasses",
        palette = listOf("#1B263B", "#C19A6B", "#F5F5F0", "#D4AF37"),
        stylingRule = "Opt for unlined shoulders and rolled trouser hems with a subtle ankle break to highlight suede footwear."
      ),
      flagshipAddress = "Passeig de Gràcia 96, Eixample, 08008 Barcelona, Spain",
      websiteUrl = "https://www.massimodutti.com",
      storePhone = "+34 934 87 63 60",
      mapsQuery = "Massimo Dutti Passeig de Gracia 96 Barcelona Spain"
    ),
    BrandProfile(
      id = "brand-gucci",
      name = "Gucci",
      tier = "Florentine Runway Maximalist",
      tierCategory = "Luxury Fashion",
      rating = 9.5f,
      reviewsCount = 24100,
      priceTier = "$$$$$",
      origin = "Italy (Florence)",
      foundedYear = 1921,
      signatureVibe = "Exuberant Italian glamour, archival equestrian horsebit motifs, and theatrical tailoring.",
      keyStrengths = listOf("Embroidered Velvet Smoking Jackets", "Archival Horsebit Loafers", "Silk Jacquard Scarves"),
      comboRecommendation = BrandComboRecommendation(
        title = "Neo-Romantic Runway Opulence",
        description = "A deep bordeaux velvet dinner jacket paired with fluid tailored cigarette slacks, gilded sole derby shoes, and an ornate matte gold choker.",
        topPiece = "Gucci Embroidered Evening Tuxedo (Bordeaux Wine)",
        bottomPiece = "Tailored High-Waist Wool Slacks (Noir Onyx)",
        footwearPiece = "Gilded Hardware Horsebit Loafers (Matte Gold Welt)",
        accessoriesPiece = "24K Matte Gold Link Chain & Archival Brooch",
        palette = listOf("#5B1424", "#111111", "#D4AF37", "#F5F5F0"),
        stylingRule = "Balance velvet’s rich sheen by keeping trousers matte and razor-sharp; let the footwear hardware gleam."
      ),
      flagshipAddress = "Via de' Tornabuoni 73/r, 50123 Firenze FI, Italy",
      websiteUrl = "https://www.gucci.com",
      storePhone = "+39 055 264011",
      mapsQuery = "Gucci Via de Tornabuoni 73 Florence Italy"
    ),
    BrandProfile(
      id = "brand-prada",
      name = "Prada",
      tier = "Intellectual Post-Industrial Luxury",
      tierCategory = "Luxury Fashion",
      rating = 9.8f,
      reviewsCount = 28900,
      priceTier = "$$$$$",
      origin = "Italy (Milan)",
      foundedYear = 1913,
      signatureVibe = "Subversive minimalism, industrial Re-Nylon ingenuity, and stark conceptual beauty.",
      keyStrengths = listOf("Re-Nylon Overshirts", "Monolith Spazzolato Footwear", "Razor-Sharp Sarto Slacks"),
      comboRecommendation = BrandComboRecommendation(
        title = "Subversive Industrial Chic",
        description = "A sharp Re-Nylon cropped jacket styled with high-waisted cigarette wool slacks and iconic chunky lug-sole platform loafers.",
        topPiece = "Prada Structured Re-Nylon Zip Jacket (Noir Onyx)",
        bottomPiece = "Precision-Cut Cigarette Slacks (Noir Onyx)",
        footwearPiece = "Chunky Architectural Spazzolato Loafers",
        accessoriesPiece = "Brushed Enamel Triangle Clip & Gilded Link Cuff",
        palette = listOf("#111111", "#F5F5F0", "#D4AF37", "#1B263B"),
        stylingRule = "Embrace pure monochrome noir; contrast technical sheen against matte virgin wool for intellectual depth."
      ),
      flagshipAddress = "Galleria Vittorio Emanuele II 63/65, 20121 Milano MI, Italy",
      websiteUrl = "https://www.prada.com",
      storePhone = "+39 02 876979",
      mapsQuery = "Prada Galleria Vittorio Emanuele II Milan Italy"
    ),
    BrandProfile(
      id = "brand-ysl",
      name = "Saint Laurent",
      tier = "Parisian Rock-Chic Couture",
      tierCategory = "Luxury Fashion",
      rating = 9.6f,
      reviewsCount = 21500,
      priceTier = "$$$$$",
      origin = "France (Paris)",
      foundedYear = 1961,
      signatureVibe = "Sleek nocturnal tailoring, razor-sharp peak lapels, and androgynous rock-and-roll decadence.",
      keyStrengths = listOf("Le Smoking Tuxedo Jackets", "Silk Lavallière Blouses", "Wyatt Harness Boots"),
      comboRecommendation = BrandComboRecommendation(
        title = "Le Smoking Nocturne",
        description = "An ultra-sharp six-button peak lapel tuxedo jacket worn over an open silk crepe tunic, anchored by skin-tight tailored barathea trousers and pointed boots.",
        topPiece = "Saint Laurent Peak-Lapel Grain de Poudre Smoking (Noir)",
        bottomPiece = "Skinny Barathea Tailored Trousers (Noir Onyx)",
        footwearPiece = "Pointed Leather Wyatt Chelsea Boots (Noir)",
        accessoriesPiece = "Silk Crepe Pocket Square & Fine Gold Collar Pin",
        palette = listOf("#111111", "#D4AF37", "#FAFAFA", "#333333"),
        stylingRule = "Keep the silhouette ultra-lean and elongated. Minimalist jewelry with sharp metallic sheen only."
      ),
      flagshipAddress = "213 Rue Saint-Honoré, 75001 Paris, France",
      websiteUrl = "https://www.ysl.com",
      storePhone = "+33 1 42 61 74 58",
      mapsQuery = "Saint Laurent 213 Rue Saint Honore Paris France"
    ),
    BrandProfile(
      id = "brand-bottega",
      name = "Bottega Veneta",
      tier = "Sensual Leather Craft & Quiet Luxury",
      tierCategory = "Quiet Luxury",
      rating = 9.7f,
      reviewsCount = 19300,
      priceTier = "$$$$$",
      origin = "Italy (Vicenza)",
      foundedYear = 1966,
      signatureVibe = "Tactile supremacy, monumental fluid leather silhouettes, and no visible logos.",
      keyStrengths = listOf("Intrecciato Woven Leather Goods", "Puddle Wool Trousers", "Tire Combat Boots"),
      comboRecommendation = BrandComboRecommendation(
        title = "Monumental Tactile Drapery",
        description = "An oversized olive drab buttery leather overshirt matched with puddle-hem fluid wool slacks and bold architectural platform footwear.",
        topPiece = "Bottega Veneta Sculptural Nappa Overshirt (Olive Drab)",
        bottomPiece = "Puddle Wide-Leg Wool Slacks (Pure Alabaster)",
        footwearPiece = "Architectural Tire Sole Footwear (Matte Gold Accent)",
        accessoriesPiece = "Intrecciato Leather Pouch & Sculpted Gold Ring",
        palette = listOf("#4B5320", "#F5F5F0", "#D4AF37", "#111111"),
        stylingRule = "Allow the trouser hem to break generously over the chunky boot welt to celebrate fluid volume."
      ),
      flagshipAddress = "Via Montenapoleone 27/A, 20121 Milano MI, Italy",
      websiteUrl = "https://www.bottegaveneta.com",
      storePhone = "+39 02 7602 4495",
      mapsQuery = "Bottega Veneta Via Montenapoleone 27 Milan Italy"
    ),
    BrandProfile(
      id = "brand-jacquemus",
      name = "Jacquemus",
      tier = "Provençal Sun-Drenched Avant-Garde",
      tierCategory = "Luxury Fashion",
      rating = 9.3f,
      reviewsCount = 15700,
      priceTier = "$$$$",
      origin = "France (Salon-de-Provence)",
      foundedYear = 2009,
      signatureVibe = "Sun-bleached linen poetry, playful geometric asymmetry, and warm Mediterranean sensuality.",
      keyStrengths = listOf("La Chemise Asymmetric Tops", "Linen Draped Bermudas", "Micro-Chiquito Bags"),
      comboRecommendation = BrandComboRecommendation(
        title = "Provençal Sunburst Solstice",
        description = "An asymmetric drape terracotta linen shirt styled with fluid high-waisted alabaster slacks and minimal leather strappy mules.",
        topPiece = "Jacquemus Draped Asymmetric Linen Shirt (Terracotta Rust)",
        bottomPiece = "Pleated Wide-Leg Trousers (Pure Alabaster)",
        footwearPiece = "Minimalist Architectural Leather Slides (Matte Gold)",
        accessoriesPiece = "Micro Le Chiquito Crossbody & Gilded Sunflower Earring",
        palette = listOf("#A0522D", "#F5F5F0", "#D4AF37", "#C19A6B"),
        stylingRule = "Tuck one side of the linen shirt loosely while letting the terracotta fabric billow naturally in movement."
      ),
      flagshipAddress = "58 Avenue Montaigne, 75008 Paris, France",
      websiteUrl = "https://www.jacquemus.com",
      storePhone = "+33 1 42 68 00 24",
      mapsQuery = "Jacquemus 58 Avenue Montaigne Paris France"
    )
  )

  private val _brandSearchQuery = MutableStateFlow("")
  val brandSearchQuery: StateFlow<String> = _brandSearchQuery.asStateFlow()

  private val _selectedBrandTier = MutableStateFlow("All")
  val selectedBrandTier: StateFlow<String> = _selectedBrandTier.asStateFlow()

  val filteredBrands: StateFlow<List<BrandProfile>> = combine(
    _brandSearchQuery,
    _selectedBrandTier
  ) { query, tier ->
    brandDirectory.filter { brand ->
      val matchesTier = tier == "All" || brand.tierCategory.equals(tier, ignoreCase = true)
      val matchesQuery = query.isBlank() ||
          brand.name.contains(query, ignoreCase = true) ||
          brand.tier.contains(query, ignoreCase = true) ||
          brand.signatureVibe.contains(query, ignoreCase = true) ||
          brand.origin.contains(query, ignoreCase = true) ||
          brand.flagshipAddress.contains(query, ignoreCase = true) ||
          brand.keyStrengths.any { it.contains(query, ignoreCase = true) } ||
          brand.comboRecommendation.title.contains(query, ignoreCase = true)
      matchesTier && matchesQuery
    }
  }.stateIn(viewModelScope, SharingStarted.Eagerly, brandDirectory)

  fun setBrandSearchQuery(query: String) {
    _brandSearchQuery.value = query
  }

  fun setSelectedBrandTier(tier: String) {
    _selectedBrandTier.value = tier
  }

  fun applyBrandComboToStudio(combo: BrandComboRecommendation) {
    if (combo.palette.isNotEmpty()) {
      applyExtractedPaletteToStudio(combo.palette)
    }
    _selectedTop.update { it.copy(name = combo.topPiece, colorHex = combo.palette.getOrElse(0) { "#111111" }) }
    _selectedBottom.update { it.copy(name = combo.bottomPiece, colorHex = combo.palette.getOrElse(1) { "#F5F5F0" }) }
    _selectedFootwear.update { it.copy(name = combo.footwearPiece, colorHex = combo.palette.getOrElse(2) { "#D4AF37" }) }
    refresh3DMannequin()
  }

  fun saveBrandComboToVault(brandName: String, combo: BrandComboRecommendation) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val newFit = SavedFit(
      id = UUID.randomUUID().toString(),
      name = "$brandName • ${combo.title}",
      date = dateFormat.format(Date()),
      gender = _gender.value,
      bodyType = _bodyType.value,
      headwear = _selectedHeadwear.value,
      top = _selectedTop.value.copy(name = combo.topPiece, colorHex = combo.palette.getOrElse(0) { "#111111" }),
      bottom = _selectedBottom.value.copy(name = combo.bottomPiece, colorHex = combo.palette.getOrElse(1) { "#F5F5F0" }),
      footwear = _selectedFootwear.value.copy(name = combo.footwearPiece, colorHex = combo.palette.getOrElse(2) { "#D4AF37" }),
      accessories = _selectedAccessories.value?.copy(name = combo.accessoriesPiece),
      notes = "${combo.description} (Styling Rule: ${combo.stylingRule})",
      colorPalette = combo.palette
    )
    _savedFits.update { listOf(newFit) + it }
  }

  init {
    initializeSampleFeed()
    initializeSampleVault()
    initializeChat()
  }

  fun toggleDarkMode() {
    val newMode = !_isDarkMode.value
    _isDarkMode.value = newMode
    evaluateJavascript("window.setTheme && window.setTheme($newMode);")
  }

  fun setGender(newGender: Gender) {
    _gender.value = newGender
    dispatchModelSwap()
  }

  fun setBodyType(newType: BodyType) {
    _bodyType.value = newType
    dispatchModelSwap()
  }

  fun setBodySize(newSize: BodySize) {
    setBodyType(newSize)
  }

  fun setRotationDegrees(deg: Float) {
    val normalized = (deg % 360f + 360f) % 360f
    _rotationDegrees.value = normalized
    evaluateJavascript("window.setRotation && window.setRotation(${normalized.toInt()});")
  }

  fun selectGarment(item: ClothingItem) {
    when (item.category) {
      ClothingCategory.HEADWEAR -> _selectedHeadwear.value = item
      ClothingCategory.TOP -> _selectedTop.value = item
      ClothingCategory.BOTTOM -> _selectedBottom.value = item
      ClothingCategory.FOOTWEAR -> _selectedFootwear.value = item
      ClothingCategory.ACCESSORIES -> _selectedAccessories.value = item
    }
    evaluateJavascript("window.setGarment && window.setGarment('${item.category.name.lowercase()}', '${item.meshNodeName}');")
    evaluateJavascript("window.setGarmentColor && window.setGarmentColor('${item.category.name.lowercase()}', '${item.colorHex}');")
    dispatchColorUpdate()
  }

  fun selectHeadwear(item: ClothingItem?) {
    _selectedHeadwear.value = item
    if (item != null) {
      evaluateJavascript("window.setGarment && window.setGarment('headwear', '${item.meshNodeName}');")
    } else {
      evaluateJavascript("window.setGarment && window.setGarment('headwear', 'none');")
    }
    dispatchColorUpdate()
  }

  fun selectTop(item: ClothingItem) {
    _selectedTop.value = item
    evaluateJavascript("window.setGarment && window.setGarment('top', '${item.meshNodeName}');")
    dispatchColorUpdate()
  }

  fun selectBottom(item: ClothingItem) {
    _selectedBottom.value = item
    evaluateJavascript("window.setGarment && window.setGarment('bottom', '${item.meshNodeName}');")
    dispatchColorUpdate()
  }

  fun selectFootwear(item: ClothingItem) {
    _selectedFootwear.value = item
    evaluateJavascript("window.setGarment && window.setGarment('footwear', '${item.meshNodeName}');")
    dispatchColorUpdate()
  }

  fun selectAccessories(item: ClothingItem?) {
    _selectedAccessories.value = item
    if (item != null) {
      evaluateJavascript("window.setGarment && window.setGarment('accessories', '${item.meshNodeName}');")
    } else {
      evaluateJavascript("window.setGarment && window.setGarment('accessories', 'none');")
    }
    dispatchColorUpdate()
  }

  fun updateTopColor(hex: String) {
    _selectedTop.update { it.copy(colorHex = hex) }
    evaluateJavascript("window.setGarmentColor && window.setGarmentColor('top', '$hex');")
    dispatchColorUpdate()
  }

  fun updateBottomColor(hex: String) {
    _selectedBottom.update { it.copy(colorHex = hex) }
    evaluateJavascript("window.setGarmentColor && window.setGarmentColor('bottom', '$hex');")
    dispatchColorUpdate()
  }

  fun updateFootwearColor(hex: String) {
    _selectedFootwear.update { it.copy(colorHex = hex) }
    evaluateJavascript("window.setGarmentColor && window.setGarmentColor('footwear', '$hex');")
    dispatchColorUpdate()
  }

  fun updateHeadwearColor(hex: String) {
    _selectedHeadwear.update { it?.copy(colorHex = hex) }
    evaluateJavascript("window.setGarmentColor && window.setGarmentColor('headwear', '$hex');")
    dispatchColorUpdate()
  }

  fun selectSkinTone(skinTone: SkinTone) {
    _selectedSkinTone.value = skinTone
    evaluateJavascript("window.setSkinTone && window.setSkinTone('${skinTone.hex}');")
    dispatchColorUpdate()
  }

  fun selectSkinToneByHex(hex: String) {
    val matched = skinTonePresets.firstOrNull { it.hex.equals(hex, ignoreCase = true) }
      ?: SkinTone(id = "custom", name = "Custom Shade", hex = hex, undertone = "Custom Undertone", description = "User customized skin complexion.")
    selectSkinTone(matched)
  }

  fun refresh3DMannequin() {
    dispatchModelSwap()
    dispatchColorUpdate()
    evaluateJavascript("window.setSkinTone && window.setSkinTone('${_selectedSkinTone.value.hex}');")
    evaluateJavascript("window.setTheme && window.setTheme(${_isDarkMode.value});")
  }

  private fun dispatchModelSwap() {
    val g = _gender.value.name.lowercase()
    val s = _bodyType.value.name.lowercase()
    evaluateJavascript("window.loadModel && window.loadModel('$g', '$s');")
    evaluateJavascript("window.setGender && window.setGender('$g');")
    evaluateJavascript("window.setBodyType && window.setBodyType('$s');")
  }

  private fun dispatchColorUpdate() {
    val topHex = _selectedTop.value.colorHex
    val bottomHex = _selectedBottom.value.colorHex
    val shoesHex = _selectedFootwear.value.colorHex
    val headHex = _selectedHeadwear.value?.colorHex ?: "#111111"
    val accHex = _selectedAccessories.value?.colorHex ?: "#D4AF37"
    val skinHex = _selectedSkinTone.value.hex

    evaluateJavascript(
      "window.updateColors && window.updateColors('$topHex', '$bottomHex', '$shoesHex', '$headHex', '$accHex', '$skinHex');"
    )
    evaluateJavascript("window.setSkinTone && window.setSkinTone('$skinHex');")
  }

  fun evaluateJavascript(script: String) {
    viewModelScope.launch {
      _jsCommandFlow.emit(script)
    }
  }

  // FitVault Operations
  fun saveCurrentFit(name: String, notes: String = "") {
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val newFit = SavedFit(
      id = UUID.randomUUID().toString(),
      name = name.ifBlank { "Lookbook #${_savedFits.value.size + 1}" },
      date = dateFormat.format(Date()),
      gender = _gender.value,
      bodyType = _bodyType.value,
      headwear = _selectedHeadwear.value,
      top = _selectedTop.value,
      bottom = _selectedBottom.value,
      footwear = _selectedFootwear.value,
      accessories = _selectedAccessories.value,
      notes = notes,
      colorPalette = listOf(
        _selectedTop.value.colorHex,
        _selectedBottom.value.colorHex,
        _selectedFootwear.value.colorHex,
        _selectedHeadwear.value?.colorHex ?: "#111111"
      ),
      skinToneHex = _selectedSkinTone.value.hex
    )
    _savedFits.update { listOf(newFit) + it }
  }

  fun deleteSavedFit(fitId: String) {
    _savedFits.update { list -> list.filterNot { it.id == fitId } }
  }

  fun loadFitIntoStudio(fit: SavedFit) {
    _gender.value = fit.gender
    _bodyType.value = fit.bodyType
    _selectedHeadwear.value = fit.headwear
    _selectedTop.value = fit.top
    _selectedBottom.value = fit.bottom
    _selectedFootwear.value = fit.footwear
    _selectedAccessories.value = fit.accessories
    val matchingSkin = skinTonePresets.firstOrNull { it.hex.equals(fit.skinToneHex, ignoreCase = true) }
      ?: skinTonePresets[1]
    _selectedSkinTone.value = matchingSkin
    refresh3DMannequin()
  }

  // ==========================================
  // FITCHECK CAMERA / UPLOAD RATER OPERATIONS
  // ==========================================
  fun submitFitPhoto(uri: Uri?, bitmap: Bitmap?, skipDelay: Boolean = false) {
    viewModelScope.launch {
      _isAnalyzingFit.value = true
      if (!skipDelay) {
        kotlinx.coroutines.delay(1200) // Aesthetic cognitive scan delay
      }

      val rating = generateFitRating()
      val fitCheck = UserFitCheck(
        id = UUID.randomUUID().toString(),
        imageUri = uri,
        imageBitmap = bitmap,
        capturedAt = SimpleDateFormat("dd MMM yyyy • HH:mm", Locale.getDefault()).format(Date()),
        rating = rating
      )
      _activeUserFitCheck.value = fitCheck
      _isAnalyzingFit.value = false
    }
  }

  fun clearCurrentFitCheck() {
    _activeUserFitCheck.value = null
  }

  fun saveUserFitToVault(title: String, notes: String = "") {
    val current = _activeUserFitCheck.value ?: return
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val newFit = SavedFit(
      id = UUID.randomUUID().toString(),
      name = title.ifBlank { "Rated Fit (${current.rating.gradeTitle})" },
      date = dateFormat.format(Date()),
      gender = _gender.value,
      bodyType = _bodyType.value,
      headwear = _selectedHeadwear.value,
      top = _selectedTop.value.copy(name = "Analyzed Silhouette Top", colorHex = current.rating.extractedPalette.getOrElse(0) { "#111111" }),
      bottom = _selectedBottom.value.copy(name = "Analyzed Drape Bottom", colorHex = current.rating.extractedPalette.getOrElse(1) { "#F5F5F0" }),
      footwear = _selectedFootwear.value.copy(name = "Analyzed Footwear", colorHex = current.rating.extractedPalette.getOrElse(2) { "#D4AF37" }),
      accessories = _selectedAccessories.value,
      notes = notes.ifBlank { current.rating.verdict },
      colorPalette = current.rating.extractedPalette
    )
    _savedFits.update { listOf(newFit) + it }
  }

  fun applyExtractedPaletteToStudio(palette: List<String>) {
    if (palette.isNotEmpty()) updateTopColor(palette[0])
    if (palette.size > 1) updateBottomColor(palette[1])
    if (palette.size > 2) updateFootwearColor(palette[2])
    if (palette.size > 3) updateHeadwearColor(palette[3])
  }

  private fun generateFitRating(): FitRatingBreakdown {
    val templates = listOf(
      FitRatingBreakdown(
        overallScore = 9.4f,
        gradeTitle = "Haute Distinction",
        verdict = "Impeccable structural silhouette with striking monochromatic discipline. The contrast between volume and proportion establishes commanding editorial poise.",
        colorHarmonyScore = 9.6f,
        colorCritique = "Sublime tonal discipline. The interplay of obsidian depths and warm metallic accents creates quiet luxury without visual noise.",
        silhouetteScore = 9.2f,
        silhouetteCritique = "Architectural shoulder line balances effortlessly with a fluid drape through the lower silhouette.",
        stylingScore = 9.5f,
        stylingCritique = "Accessories feel intentional and refined. The jewelry accents punctuate the clean line work.",
        occasionScore = 9.3f,
        occasionCritique = "Ready for Paris gallery vernissages, cocktail soirees, or contemporary fashion salon presentations.",
        stylistTips = listOf(
          "Consider breaking the ankle hem with a half-break architectural loafer to elongate the stride.",
          "A matte gold signet ring or micro-chain will tie into your hardware beautifully.",
          "Experiment with a silk scarf tied inside the collar for subtle tonal depth."
        ),
        extractedPalette = listOf("#111111", "#D4AF37", "#F5F5F0", "#333333")
      ),
      FitRatingBreakdown(
        overallScore = 9.7f,
        gradeTitle = "Sartorial Mastery",
        verdict = "Flawless textural juxtaposition. The dialogue between soft drape and tailored rigidity yields an effortless, commanding high-fashion statement.",
        colorHarmonyScore = 9.8f,
        colorCritique = "Harmonious palette with sublime light reflection. The chromatic balance flatters posture and framing.",
        silhouetteScore = 9.6f,
        silhouetteCritique = "Golden-ratio proportions: structured torso anchored by fluid wide-leg silhouette.",
        stylingScore = 9.7f,
        stylingCritique = "Minimalist perfection. Every piece serves an architectural purpose.",
        occasionScore = 9.6f,
        occasionCritique = "A couture masterclass suited for Milan Fashion Week, evening galas, or curated editorial photography.",
        stylistTips = listOf(
          "A structured leather crossbody worn high on the chest adds tactical luxury appeal.",
          "Keep footwear polished to maintain high-sheen contrast against matte fabrics.",
          "Try unbuttoning the lower sleeve cuff for a nonchalant sprezzatura touch."
        ),
        extractedPalette = listOf("#1B263B", "#C19A6B", "#D4AF37", "#F5F5F0")
      ),
      FitRatingBreakdown(
        overallScore = 9.1f,
        gradeTitle = "Contemporary Avant-Garde",
        verdict = "Bold, forward-thinking ensemble with daring geometric balance. Shows fearless confidence in modern layering and proportion.",
        colorHarmonyScore = 9.0f,
        colorCritique = "Daring chromatic mood with rich earthy undertones that radiate warmth and modern edge.",
        silhouetteScore = 9.3f,
        silhouetteCritique = "Dramatic volume contrast that challenges traditional tailoring in a captivating way.",
        stylingScore = 9.2f,
        stylingCritique = "Expressive piece curation. The layering creates dynamic movement in motion.",
        occasionScore = 9.0f,
        occasionCritique = "Ideal for creative summits, runway front-rows, and high-concept evening dinners.",
        stylistTips = listOf(
          "Cinch slightly at the natural waist if you desire a more pronounced hourglass taper.",
          "Incorporate a monochrome leather beret to frame the facial silhouette.",
          "A neutral camel or bordeaux accent piece will elevate the color hierarchy."
        ),
        extractedPalette = listOf("#A0522D", "#111111", "#D4AF37", "#8A9A86")
      )
    )
    return templates.random()
  }

  // Style Feed Operations
  fun setFeedCategory(cat: String) {
    _feedCategory.value = cat
  }

  fun toggleFeedLike(itemId: String) {
    _feedItems.update { list ->
      list.map { item ->
        if (item.id == itemId) {
          val nextLiked = !item.isLiked
          item.copy(
            isLiked = nextLiked,
            likes = if (nextLiked) item.likes + 1 else item.likes - 1
          )
        } else {
          item
        }
      }
    }
  }

  // Stylist AI Operations
  fun sendUserMessage(text: String) {
    if (text.isBlank()) return
    val userMsg = ChatMessage(
      id = UUID.randomUUID().toString(),
      text = text.trim(),
      isFromUser = true,
      timestamp = getCurrentTimeString()
    )
    _chatMessages.update { it + userMsg }

    // Generate intelligent haute-couture response
    viewModelScope.launch {
      _isChatLoading.value = true
      kotlinx.coroutines.delay(650) // simulate natural cognitive curation

      val response = generateStylistAdvice(userMsg.text)
      _chatMessages.update { it + response }
      _isChatLoading.value = false
    }
  }

  private fun generateStylistAdvice(input: String): ChatMessage {
    val lower = input.lowercase()
    val id = UUID.randomUUID().toString()
    val time = getCurrentTimeString()

    return when {
      lower.contains("evening") || lower.contains("gala") || lower.contains("cocktail") || lower.contains("formal") -> {
        ChatMessage(
          id = id,
          text = "For an evening gala, restraint and sculptural silhouette command the room. I recommend a sharp Noir Onyx tailored smoking jacket paired with high-rise drape trousers, anchored by our 24K Matte Gold footwear hardware.",
          isFromUser = false,
          timestamp = time,
          suggestedColors = listOf("#111111", "#D4AF37", "#F5F5F0"),
          outfitRecommendation = SavedFit(
            id = "rec-gala",
            name = "Milan Gala Monochrome",
            date = "Atelier Curated",
            gender = _gender.value,
            bodyType = _bodyType.value,
            headwear = catalogHeadwear[2],
            top = catalogTops[4], // Gilded Evening Smoking Jacket
            bottom = catalogBottoms[1], // Tailored Cigarette Slacks
            footwear = catalogFootwear[0], // Gilded Sole Derby Shoes
            accessories = catalogAccessories[0]
          )
        )
      }
      lower.contains("monochrome") || lower.contains("minimal") || lower.contains("clean") -> {
        ChatMessage(
          id = id,
          text = "Minimalism in luxury fashion relies on tactile texture variation rather than clashing colors. Contrast matte wool with liquid silk gabardine, balancing Studio White and Pure Alabaster with deep Obsidian accents.",
          isFromUser = false,
          timestamp = time,
          suggestedColors = listOf("#FAFAFA", "#F5F5F0", "#111111", "#C19A6B"),
          outfitRecommendation = SavedFit(
            id = "rec-minimal",
            name = "Architectural Alabaster",
            date = "Atelier Curated",
            gender = _gender.value,
            bodyType = _bodyType.value,
            headwear = catalogHeadwear[1],
            top = catalogTops[0],
            bottom = catalogBottoms[0],
            footwear = catalogFootwear[2],
            accessories = catalogAccessories[1]
          )
        )
      }
      lower.contains("gold") || lower.contains("color") || lower.contains("palette") -> {
        ChatMessage(
          id = id,
          text = "Matte Gold (#D4AF37) acts as a warm luminescent anchor. It pairs magnificently with Midnight Navy (#1B263B) for regal contrast, or with Rich Camel (#C19A6B) for tonal quiet luxury.",
          isFromUser = false,
          timestamp = time,
          suggestedColors = listOf("#D4AF37", "#1B263B", "#C19A6B", "#111111")
        )
      }
      lower.contains("street") || lower.contains("casual") || lower.contains("oversized") -> {
        ChatMessage(
          id = id,
          text = "For elevated streetwear, play with volume asymmetry: pair an oversized boxy silk trench with architectural wide-leg pleated slacks and structured chunky loafers. This creates effortless high-fashion proportion.",
          isFromUser = false,
          timestamp = time,
          suggestedColors = listOf("#111111", "#4B5320", "#F5F5F0"),
          outfitRecommendation = SavedFit(
            id = "rec-street",
            name = "Urban Nomad Couture",
            date = "Atelier Curated",
            gender = _gender.value,
            bodyType = _bodyType.value,
            headwear = catalogHeadwear[0],
            top = catalogTops[0],
            bottom = catalogBottoms[2],
            footwear = catalogFootwear[1],
            accessories = catalogAccessories[1]
          )
        )
      }
      else -> {
        ChatMessage(
          id = id,
          text = "A sartorial balance is key: combine structured tailored shoulders with fluid bottom drape. Tap any suggested swatch below to inject new harmony into your 3D mannequin, or ask me for specific gala, travel, or seasonal palettes.",
          isFromUser = false,
          timestamp = time,
          suggestedColors = listOf("#111111", "#D4AF37", "#F5F5F0", "#1B263B")
        )
      }
    }
  }

  private fun getCurrentTimeString(): String {
    return SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
  }

  private fun initializeSampleFeed() {
    _feedItems.value = listOf(
      FeedItem(
        id = "feed-1",
        title = "Architectural Minimalism",
        designer = "Maison de L'Ombre",
        collection = "Autumn/Winter 2026",
        imageUrl = "https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?auto=format&fit=crop&w=800&q=80",
        tags = listOf("Minimalist", "Monochrome", "Wool"),
        likes = 1420,
        isLiked = true,
        description = "Dramatic double-faced cashmere coat with razor-sharp lapels and fluid wide-leg drape.",
        dominantColorHex = "#111111"
      ),
      FeedItem(
        id = "feed-2",
        title = "Gilded Sunburst Evening",
        designer = "Auric Studio",
        collection = "Haute Couture Salon",
        imageUrl = "https://images.unsplash.com/photo-1490481651871-ab68de25d43d?auto=format&fit=crop&w=800&q=80",
        tags = listOf("Runway", "Matte Gold", "Silk"),
        likes = 2890,
        isLiked = false,
        description = "Metallic thread hand-embroidered bustier over tailored floor-grazing culottes.",
        dominantColorHex = "#D4AF37"
      ),
      FeedItem(
        id = "feed-3",
        title = "Sartorial Navy Silhouette",
        designer = "Savile & Co.",
        collection = "Permanent Archive",
        imageUrl = "https://images.unsplash.com/photo-1509631179647-0177331693ae?auto=format&fit=crop&w=800&q=80",
        tags = listOf("Sartorial", "Midnight Navy", "Tailoring"),
        likes = 945,
        isLiked = false,
        description = "Six-button peak lapel suit cut from custom British flannel with gold cuff detailing.",
        dominantColorHex = "#1B263B"
      ),
      FeedItem(
        id = "feed-4",
        title = "Alabaster Fluidity",
        designer = "Komorebi Atelier",
        collection = "Spring Solstice",
        imageUrl = "https://images.unsplash.com/photo-1539109136881-3be0616acf4b?auto=format&fit=crop&w=800&q=80",
        tags = listOf("Minimalist", "Pure Alabaster", "Linen"),
        likes = 1730,
        isLiked = true,
        description = "Layered crêpe de chine tunic paired with origami pleated relaxed trousers.",
        dominantColorHex = "#F5F5F0"
      ),
      FeedItem(
        id = "feed-5",
        title = "Terracotta Leather Trench",
        designer = "Atelier Brutus",
        collection = "Urban Avant-Garde",
        imageUrl = "https://images.unsplash.com/photo-1483985988355-763728e1935b?auto=format&fit=crop&w=800&q=80",
        tags = listOf("Avant-Garde", "Terracotta", "Leather"),
        likes = 3110,
        isLiked = false,
        description = "Sculpted lambskin trench with storm flap and brushed matte gold hardware.",
        dominantColorHex = "#A0522D"
      ),
      FeedItem(
        id = "feed-6",
        title = "Clandestine Velvet Smoking",
        designer = "Vesper Noir",
        collection = "Nocturne Gala",
        imageUrl = "https://images.unsplash.com/photo-1558769132-cb1aea458c5e?auto=format&fit=crop&w=800&q=80",
        tags = listOf("Runway", "Bordeaux", "Velvet"),
        likes = 2154,
        isLiked = true,
        description = "Deep bordeaux velvet dinner jacket styled with high-waist silk barathea slacks.",
        dominantColorHex = "#5B1424"
      )
    )
  }

  private fun initializeSampleVault() {
    _savedFits.value = listOf(
      SavedFit(
        id = "vault-1",
        name = "Paris Fashion Week Finale",
        date = "28 Sep 2026",
        gender = Gender.MALE,
        bodyType = BodyType.SLIM,
        headwear = catalogHeadwear[0],
        top = catalogTops[0],
        bottom = catalogBottoms[0],
        footwear = catalogFootwear[0],
        accessories = catalogAccessories[0],
        notes = "Dramatic contrast with noir outerwear and fluid alabaster drape.",
        colorPalette = listOf("#111111", "#F5F5F0", "#D4AF37", "#111111")
      ),
      SavedFit(
        id = "vault-2",
        name = "Auric Gala Evening",
        date = "24 Sep 2026",
        gender = Gender.FEMALE,
        bodyType = BodyType.ATHLETIC,
        headwear = catalogHeadwear[2],
        top = catalogTops[4],
        bottom = catalogBottoms[1],
        footwear = catalogFootwear[0],
        accessories = catalogAccessories[0],
        notes = "Metallic brocade top catching the studio rim light.",
        colorPalette = listOf("#D4AF37", "#111111", "#D4AF37", "#5B1424")
      ),
      SavedFit(
        id = "vault-3",
        name = "High-Sartorial Mediterranean",
        date = "15 Sep 2026",
        gender = Gender.MALE,
        bodyType = BodyType.BROAD,
        headwear = catalogHeadwear[1],
        top = catalogTops[1],
        bottom = catalogBottoms[3],
        footwear = catalogFootwear[3],
        accessories = catalogAccessories[2],
        notes = "Rich navy blazer over camel linen chinos.",
        colorPalette = listOf("#1B263B", "#C19A6B", "#C19A6B", "#F5F5F0")
      )
    )
  }

  private fun initializeChat() {
    _chatMessages.value = listOf(
      ChatMessage(
        id = "welcome-1",
        text = "Bienvenue to Atelier. I am your Haute Couture Stylist. How may I refine your silhouette today? Ask me about color harmony, event dressing, or silhouette balancing.",
        isFromUser = false,
        timestamp = getCurrentTimeString(),
        suggestedColors = listOf("#111111", "#D4AF37", "#F5F5F0", "#1B263B")
      )
    )
  }
}
