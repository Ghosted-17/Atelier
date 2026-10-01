package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.BodySize
import com.example.model.ClothingCategory
import com.example.model.Gender
import com.example.viewmodel.AtelierViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `verify app name resource is Atelier`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Atelier", appName)
  }

  @Test
  fun `verify initial atelier viewmodel state`() {
    val viewModel = AtelierViewModel()
    assertTrue(viewModel.isDarkMode.value)
    assertEquals(Gender.MALE, viewModel.gender.value)
    assertEquals(BodySize.SLIM, viewModel.bodySize.value)
    assertEquals(0f, viewModel.rotationDegrees.value)
    assertNotNull(viewModel.selectedTop.value)
    assertNotNull(viewModel.selectedBottom.value)
    assertNotNull(viewModel.selectedFootwear.value)
    assertFalse(viewModel.savedFits.value.isEmpty())
    assertFalse(viewModel.feedItems.value.isEmpty())
    assertFalse(viewModel.chatMessages.value.isEmpty())
  }

  @Test
  fun `test gender and body size transitions`() {
    val viewModel = AtelierViewModel()
    viewModel.setGender(Gender.FEMALE)
    assertEquals(Gender.FEMALE, viewModel.gender.value)

    viewModel.setBodySize(BodySize.ATHLETIC)
    assertEquals(BodySize.ATHLETIC, viewModel.bodySize.value)

    viewModel.setRotationDegrees(90f)
    assertEquals(90f, viewModel.rotationDegrees.value)
  }

  @Test
  fun `test clothing selection and color updates`() {
    val viewModel = AtelierViewModel()
    val newTop = viewModel.catalogTops.first { it.id == "t2" }
    viewModel.selectTop(newTop)
    assertEquals("t2", viewModel.selectedTop.value.id)

    viewModel.updateTopColor("#D4AF37")
    assertEquals("#D4AF37", viewModel.selectedTop.value.colorHex)
  }

  @Test
  fun `test saving fit into vault and deletion`() {
    val viewModel = AtelierViewModel()
    val initialCount = viewModel.savedFits.value.size
    viewModel.saveCurrentFit(name = "Milan Test Fit", notes = "Silk and matte gold")

    val updatedFits = viewModel.savedFits.value
    assertEquals(initialCount + 1, updatedFits.size)
    val saved = updatedFits.first()
    assertEquals("Milan Test Fit", saved.name)
    assertEquals("Silk and matte gold", saved.notes)

    viewModel.deleteSavedFit(saved.id)
    assertEquals(initialCount, viewModel.savedFits.value.size)
  }

  @Test
  fun `test style feed like toggle`() {
    val viewModel = AtelierViewModel()
    val firstItem = viewModel.feedItems.value.first()
    val initialLikes = firstItem.likes
    val initialLikedState = firstItem.isLiked

    viewModel.toggleFeedLike(firstItem.id)
    val toggledItem = viewModel.feedItems.value.first { it.id == firstItem.id }
    assertEquals(!initialLikedState, toggledItem.isLiked)
    assertEquals(if (toggledItem.isLiked) initialLikes + 1 else initialLikes - 1, toggledItem.likes)
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun `test stylist ai message exchange`() = runTest {
    val viewModel = AtelierViewModel()
    val initialCount = viewModel.chatMessages.value.size

    viewModel.sendUserMessage("Suggest an evening gala look")
    val currentMessages = viewModel.chatMessages.value
    assertTrue(currentMessages.size > initialCount)
    assertTrue(currentMessages.any { it.isFromUser && it.text.contains("evening gala") })
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun `test fit rater photo submission and evaluation`() = runTest {
    val viewModel = AtelierViewModel()
    assertFalse(viewModel.isAnalyzingFit.value)
    assertEquals(null, viewModel.activeUserFitCheck.value)

    viewModel.submitFitPhoto(uri = null, bitmap = null, skipDelay = true)
    val check = viewModel.activeUserFitCheck.value
    assertNotNull(check)
    assertTrue(check!!.rating.overallScore in 8.0f..10.0f)
    assertFalse(check.rating.gradeTitle.isEmpty())
    assertEquals(4, check.rating.extractedPalette.size)
    assertFalse(check.rating.stylistTips.isEmpty())

    // Test saving rated fit to vault
    val initialVaultSize = viewModel.savedFits.value.size
    viewModel.saveUserFitToVault(title = "Paris Street Fit")
    assertEquals(initialVaultSize + 1, viewModel.savedFits.value.size)
    assertEquals("Paris Street Fit", viewModel.savedFits.value.first().name)

    // Test applying extracted palette to 3D studio
    viewModel.applyExtractedPaletteToStudio(check.rating.extractedPalette)
    assertEquals(check.rating.extractedPalette[0], viewModel.selectedTop.value.colorHex)
    assertEquals(check.rating.extractedPalette[1], viewModel.selectedBottom.value.colorHex)

    // Test clearing fit check
    viewModel.clearCurrentFitCheck()
    assertEquals(null, viewModel.activeUserFitCheck.value)
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun `test luxury brand directory search and ratings`() = runTest {
    val viewModel = AtelierViewModel()

    // Verify presence of Zara and Tommy Hilfiger
    val zara = viewModel.brandDirectory.firstOrNull { it.name.equals("Zara", ignoreCase = true) }
    assertNotNull(zara)
    assertEquals(8.6f, zara!!.rating)
    assertFalse(zara.comboRecommendation.title.isEmpty())
    assertFalse(zara.comboRecommendation.palette.isEmpty())
    assertFalse(zara.flagshipAddress.isEmpty())
    assertTrue(zara.flagshipAddress.contains("Madrid"))
    assertTrue(zara.websiteUrl.contains("zara.com"))
    assertFalse(zara.storePhone.isEmpty())

    val tommy = viewModel.brandDirectory.firstOrNull { it.name.contains("Tommy", ignoreCase = true) }
    assertNotNull(tommy)
    assertEquals(8.7f, tommy!!.rating)
    assertEquals("American Heritage Prep", tommy.tier)
    assertFalse(tommy.comboRecommendation.stylingRule.isEmpty())
    assertFalse(tommy.flagshipAddress.isEmpty())
    assertTrue(tommy.flagshipAddress.contains("New York"))
    assertTrue(tommy.websiteUrl.contains("tommy.com"))

    // Test Search query filter by name
    viewModel.setBrandSearchQuery("zara")
    testScheduler.advanceUntilIdle()
    val zaraSearchResults = viewModel.filteredBrands.value
    assertTrue(zaraSearchResults.any { it.name.equals("Zara", ignoreCase = true) })

    viewModel.setBrandSearchQuery("tommy")
    testScheduler.advanceUntilIdle()
    val tommySearchResults = viewModel.filteredBrands.value
    assertTrue(tommySearchResults.any { it.name.contains("Tommy", ignoreCase = true) })

    // Test Search query filter by Flagship Address
    viewModel.setBrandSearchQuery("Madrid")
    testScheduler.advanceUntilIdle()
    val addressSearchResults = viewModel.filteredBrands.value
    assertTrue(addressSearchResults.any { it.name == "Zara" })

    // Test Tier filtering
    viewModel.setBrandSearchQuery("")
    viewModel.setSelectedBrandTier("High-Street")
    testScheduler.advanceUntilIdle()
    val highStreetBrands = viewModel.filteredBrands.value
    assertTrue(highStreetBrands.any { it.name == "Zara" })

    // Test Applying Fashion Combo to Studio
    viewModel.applyBrandComboToStudio(zara.comboRecommendation)
    assertEquals(zara.comboRecommendation.topPiece, viewModel.selectedTop.value.name)
    assertEquals(zara.comboRecommendation.palette[0], viewModel.selectedTop.value.colorHex)

    // Test Saving Brand Combo to Vault
    val vaultCount = viewModel.savedFits.value.size
    viewModel.saveBrandComboToVault(tommy.name, tommy.comboRecommendation)
    assertEquals(vaultCount + 1, viewModel.savedFits.value.size)
    val latestFit = viewModel.savedFits.value.first()
    assertTrue(latestFit.name.contains("Tommy Hilfiger"))
  }

  @Test
  fun `test skin tone selector presets and customization`() {
    val viewModel = AtelierViewModel()

    // 1. Verify 8 inclusive presets exist covering Fitzpatrick I-VI
    assertEquals(8, viewModel.skinTonePresets.size)
    val alabaster = viewModel.skinTonePresets.first { it.id == "alabaster" }
    val ebony = viewModel.skinTonePresets.first { it.id == "ebony" }
    assertEquals("#F7EBE1", alabaster.hex)
    assertEquals("#382018", ebony.hex)

    // 2. Verify initial selected skin tone is Warm Ivory (#EEDBC8)
    assertEquals("ivory", viewModel.selectedSkinTone.value.id)
    assertEquals("#EEDBC8", viewModel.selectedSkinTone.value.hex)

    // 3. Test selecting a new skin tone (Deep Espresso)
    val espresso = viewModel.skinTonePresets.first { it.id == "espresso" }
    viewModel.selectSkinTone(espresso)
    assertEquals("espresso", viewModel.selectedSkinTone.value.id)
    assertEquals("#5C3621", viewModel.selectedSkinTone.value.hex)
    assertFalse(viewModel.selectedSkinTone.value.recommendedPalettes.isEmpty())

    // 4. Test selecting custom skin tone by hex
    viewModel.selectSkinToneByHex("#CBA17B")
    assertEquals("olive", viewModel.selectedSkinTone.value.id)

    viewModel.selectSkinToneByHex("#7A4526")
    assertEquals("#7A4526", viewModel.selectedSkinTone.value.hex)

    // 5. Test saving fit persists the custom skin tone and loading it restores the skin tone
    viewModel.selectSkinTone(ebony)
    viewModel.saveCurrentFit(name = "Obsidian Gala Look")
    val savedLook = viewModel.savedFits.value.first { it.name == "Obsidian Gala Look" }
    assertEquals("#382018", savedLook.skinToneHex)

    // Switch away to alabaster
    viewModel.selectSkinTone(alabaster)
    assertEquals("alabaster", viewModel.selectedSkinTone.value.id)

    // Restore saved look and assert skin tone restored
    viewModel.loadFitIntoStudio(savedLook)
    assertEquals("ebony", viewModel.selectedSkinTone.value.id)
    assertEquals("#382018", viewModel.selectedSkinTone.value.hex)
  }
}
