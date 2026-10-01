package com.example.model

enum class Gender(val label: String) {
  MALE("Male"),
  FEMALE("Female")
}

enum class BodyType(val label: String, val description: String) {
  SLIM("Slim", "Tailored & Linear"),
  ATHLETIC("Athletic", "Sculpted & Toned"),
  BROAD("Broad", "Structured & Bold"),
  CURVY("Curvy", "Full & Voluminous")
}

// Backward compatibility alias for BodySize
typealias BodySize = BodyType

enum class ClothingCategory(val title: String) {
  HEADWEAR("Caps & Hats"),
  TOP("Tops & Outerwear"),
  BOTTOM("Pants & Bottoms"),
  FOOTWEAR("Kicks & Boots"),
  ACCESSORIES("Accessories")
}

data class ClothingItem(
  val id: String,
  val name: String,
  val category: ClothingCategory,
  val meshNodeName: String,
  val defaultHex: String,
  val colorHex: String = defaultHex,
  val subTitle: String = "Bitmoji Style",
  val material: String = "Matte Fabric"
)

data class SkinTone(
  val id: String,
  val name: String,
  val hex: String,
  val undertone: String,
  val description: String,
  val recommendedPalettes: List<String> = emptyList()
) {
  companion object {
    val DEFAULT_PRESETS = listOf(
      SkinTone(
        id = "alabaster",
        name = "Alabaster Porcelain",
        hex = "#F7EBE1",
        undertone = "Cool Rosy Alabaster",
        description = "Luminous fair porcelain with delicate rosy undertones.",
        recommendedPalettes = listOf("#111111", "#1B263B", "#5B1424", "#D4AF37")
      ),
      SkinTone(
        id = "ivory",
        name = "Warm Ivory",
        hex = "#EEDBC8",
        undertone = "Warm Golden Peach",
        description = "Fair to light complexion with gentle golden warmth.",
        recommendedPalettes = listOf("#1B263B", "#C19A6B", "#4B5320", "#333333")
      ),
      SkinTone(
        id = "bisque",
        name = "Golden Bisque",
        hex = "#E2C7A8",
        undertone = "Neutral Buff Sand",
        description = "Light-medium sunlit complexion with balanced neutral undertones.",
        recommendedPalettes = listOf("#111111", "#F5F5F0", "#5B1424", "#C19A6B")
      ),
      SkinTone(
        id = "olive",
        name = "Honey Olive",
        hex = "#CBA17B",
        undertone = "Golden Mediterranean",
        description = "Medium golden olive with sun-kissed warmth.",
        recommendedPalettes = listOf("#1B263B", "#F5F5F0", "#D4AF37", "#A0522D")
      ),
      SkinTone(
        id = "caramel",
        name = "Warm Caramel",
        hex = "#A8754D",
        undertone = "Rich Amber Bronze",
        description = "Medium-deep radiant bronze with warm amber depth.",
        recommendedPalettes = listOf("#F5F5F0", "#D4AF37", "#1B263B", "#8A9A86")
      ),
      SkinTone(
        id = "chestnut",
        name = "Spiced Chestnut",
        hex = "#865434",
        undertone = "Warm Terracotta Chestnut",
        description = "Rich chestnut complexion with earthy warm depth.",
        recommendedPalettes = listOf("#D4AF37", "#C19A6B", "#F5F5F0", "#111111")
      ),
      SkinTone(
        id = "espresso",
        name = "Deep Espresso",
        hex = "#5C3621",
        undertone = "Rich Cocoa Undertone",
        description = "Deep espresso with opulent chocolate undertones.",
        recommendedPalettes = listOf("#D4AF37", "#F5F5F0", "#C19A6B", "#8A9A86")
      ),
      SkinTone(
        id = "ebony",
        name = "Midnight Obsidian",
        hex = "#382018",
        undertone = "Deep Neutral Ebony",
        description = "Deepest rich obsidian with regal cool depth.",
        recommendedPalettes = listOf("#D4AF37", "#F5F5F0", "#A0522D", "#C19A6B")
      )
    )
  }
}

data class SavedFit(
  val id: String,
  val name: String,
  val date: String,
  val gender: Gender,
  val bodyType: BodyType,
  val headwear: ClothingItem?,
  val top: ClothingItem,
  val bottom: ClothingItem,
  val footwear: ClothingItem,
  val accessories: ClothingItem?,
  val notes: String = "",
  val colorPalette: List<String> = emptyList(),
  val skinToneHex: String = "#EEDBC8"
) {
  // Compatibility getter for code expecting bodySize
  val bodySize: BodyType get() = bodyType
}

data class FeedItem(
  val id: String,
  val title: String,
  val designer: String,
  val collection: String,
  val imageUrl: String,
  val tags: List<String>,
  val likes: Int,
  val isLiked: Boolean = false,
  val description: String,
  val dominantColorHex: String,
  val gender: Gender = Gender.FEMALE
)

data class ChatMessage(
  val id: String,
  val text: String,
  val isFromUser: Boolean,
  val timestamp: String,
  val outfitRecommendation: SavedFit? = null,
  val suggestedColors: List<String> = emptyList()
)

data class ColorOption(
  val name: String,
  val hex: String
)

data class FitRatingBreakdown(
  val overallScore: Float,
  val gradeTitle: String,
  val verdict: String,
  val colorHarmonyScore: Float,
  val colorCritique: String,
  val silhouetteScore: Float,
  val silhouetteCritique: String,
  val stylingScore: Float,
  val stylingCritique: String,
  val occasionScore: Float,
  val occasionCritique: String,
  val stylistTips: List<String>,
  val extractedPalette: List<String>
)

data class UserFitCheck(
  val id: String,
  val imageUri: android.net.Uri? = null,
  val imageBitmap: android.graphics.Bitmap? = null,
  val capturedAt: String,
  val rating: FitRatingBreakdown
)

data class BrandComboRecommendation(
  val title: String,
  val description: String,
  val topPiece: String,
  val bottomPiece: String,
  val footwearPiece: String,
  val accessoriesPiece: String,
  val palette: List<String>,
  val stylingRule: String
)

data class BrandProfile(
  val id: String,
  val name: String,
  val tier: String,
  val tierCategory: String,
  val rating: Float,
  val reviewsCount: Int,
  val priceTier: String,
  val origin: String,
  val foundedYear: Int,
  val signatureVibe: String,
  val keyStrengths: List<String>,
  val comboRecommendation: BrandComboRecommendation,
  val flagshipAddress: String = "",
  val websiteUrl: String = "",
  val storePhone: String = "",
  val mapsQuery: String = ""
)
