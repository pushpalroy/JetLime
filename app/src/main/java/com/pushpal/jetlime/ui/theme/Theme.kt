package com.pushpal.jetlime.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

private val LightColorPalette = JetLimeColorPalette(
  brand = White,
  accent = JetLimeColor,
  uiBackground = Neutral0,
  uiBorder = VeryLightGrey,
  uiFloated = FunctionalRed,
  textPrimary = TextPrimary,
  textSecondary = TextSecondary,
  textSecondaryDark = TextSecondaryDark,
  error = FunctionalRed,
  isDark = false,
  buttonTextColor = White
)

private val DarkColorPalette = JetLimeColorPalette(
  brand = Shadow1,
  accent = Ocean2,
  uiBackground = GreyBg,
  uiBorder = GreyBgDark,
  uiFloated = Ocean2,
  textPrimary = Shadow1,
  textSecondary = Neutral0,
  textSecondaryDark = Neutral0,
  error = FunctionalRedDark,
  isDark = true,
  buttonTextColor = Ocean2
)

@Composable
fun JetLimeTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit
) {
  val colors = if (darkTheme) DarkColorPalette else LightColorPalette

  val sysUiController = LocalSysUiController.current
  SideEffect {
    sysUiController.setSystemBarsColor(
      color = colors.uiBackground.copy(alpha = AlphaNearOpaque)
    )
  }

  ProvideJetLimeColors(colors) {
    MaterialTheme(
      shapes = JetLimeShapes,
      content = content
    )
  }
}

object JetLimeTheme {
  val colors: JetLimeColorPalette
    @Composable
    get() = LocalJetLimeColor.current
}

/**
 * JetLime custom Color Palette
 */
@Stable
class JetLimeColorPalette(
  brand: Color,
  accent: Color,
  uiBackground: Color,
  uiBorder: Color,
  uiFloated: Color,
  textPrimary: Color = brand,
  textSecondaryDark: Color,
  textSecondary: Color,
  error: Color,
  isDark: Boolean,
  buttonTextColor: Color
) {
  var accent by mutableStateOf(accent)
    private set
  var uiBackground by mutableStateOf(uiBackground)
    private set
  var uiBorder by mutableStateOf(uiBorder)
    private set
  var uiFloated by mutableStateOf(uiFloated)
    private set
  var textPrimary by mutableStateOf(textPrimary)
    private set
  var textSecondary by mutableStateOf(textSecondary)
    private set
  var textSecondaryDark by mutableStateOf(textSecondaryDark)
    private set
  var error by mutableStateOf(error)
    private set
  var isDark by mutableStateOf(isDark)
    private set

  var buttonTextColor by mutableStateOf(buttonTextColor)
    private set

  fun update(other: JetLimeColorPalette) {
    uiBackground = other.uiBackground
    uiBorder = other.uiBorder
    uiFloated = other.uiFloated
    textPrimary = other.textPrimary
    textSecondary = other.textSecondary
    error = other.error
    isDark = other.isDark
    buttonTextColor = other.buttonTextColor
  }
}

@Composable
fun ProvideJetLimeColors(
  colors: JetLimeColorPalette,
  content: @Composable () -> Unit
) {
  val colorPalette = remember { colors }
  colorPalette.update(colors)
  CompositionLocalProvider(LocalJetLimeColor provides colorPalette, content = content)
}

private val LocalJetLimeColor = staticCompositionLocalOf<JetLimeColorPalette> {
  error("No JetLimeColorPalette provided")
}