/*
* MIT License
*
* Copyright (c) 2024 Pushpal Roy
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in all
* copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
*
*/
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

private val LightColorPalette =
  JetLimeColorPalette(
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
    buttonTextColor = White,
  )

private val DarkColorPalette =
  JetLimeColorPalette(
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
    buttonTextColor = Ocean2,
  )

@Composable
fun JetLimeTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
  val colors = if (darkTheme) DarkColorPalette else LightColorPalette

  val sysUiController = LocalSysUiController.current
  SideEffect {
    sysUiController.setSystemBarsColor(
      color = colors.uiBackground.copy(alpha = ALPHA_NEAR_OPAQUE),
    )
  }

  ProvideJetLimeColors(colors) {
    MaterialTheme(
      shapes = JetLimeShapes,
      content = content,
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
  buttonTextColor: Color,
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
fun ProvideJetLimeColors(colors: JetLimeColorPalette, content: @Composable () -> Unit) {
  val colorPalette = remember { colors }
  colorPalette.update(colors)
  CompositionLocalProvider(LocalJetLimeColor provides colorPalette, content = content)
}

private val LocalJetLimeColor =
  staticCompositionLocalOf<JetLimeColorPalette> {
    error("No JetLimeColorPalette provided")
  }
