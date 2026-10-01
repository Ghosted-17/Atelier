package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
  primary = CrispWhite, // #FAFAFA
  onPrimary = DeepObsidian,
  primaryContainer = ElevatedCharcoal, // #1A1A1A
  onPrimaryContainer = CrispWhite,
  secondary = MatteGold, // #D4AF37 Accent
  onSecondary = DeepObsidian,
  secondaryContainer = CharcoalSurfaceVariant,
  onSecondaryContainer = MatteGold,
  tertiary = MatteGold, // #D4AF37 Accent
  onTertiary = DeepObsidian,
  background = DeepObsidian, // #111111
  onBackground = CrispWhite,
  surface = ElevatedCharcoal, // #1A1A1A Cards
  onSurface = CrispWhite,
  surfaceVariant = CharcoalSurfaceVariant,
  onSurfaceVariant = MutedTextDark,
  outline = SubtleBorderDark,
  outlineVariant = SubtleBorderDark,
)

private val LightColorScheme = lightColorScheme(
  primary = SharpBlack, // #111111
  onPrimary = PureWhite,
  primaryContainer = PureWhite, // #FFFFFF
  onPrimaryContainer = SharpBlack,
  secondary = MatteGold, // #D4AF37 Accent
  onSecondary = SharpBlack,
  secondaryContainer = StudioWhite,
  onSecondaryContainer = MatteGold,
  tertiary = MatteGold, // #D4AF37 Accent
  onTertiary = SharpBlack,
  background = StudioWhite, // #FAFAFA
  onBackground = SharpBlack,
  surface = PureWhite, // #FFFFFF Cards
  onSurface = SharpBlack,
  surfaceVariant = StudioWhite,
  onSurfaceVariant = MutedTextLight,
  outline = SubtleBorderLight,
  outlineVariant = SubtleBorderLight,
)

@Composable
fun AtelierTheme(
  darkTheme: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
