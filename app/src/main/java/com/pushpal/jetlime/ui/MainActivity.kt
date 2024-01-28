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
package com.pushpal.jetlime.ui

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.pushpal.jetlime.ui.theme.DarkColorPalette
import com.pushpal.jetlime.ui.theme.JetLimeTheme
import com.pushpal.jetlime.ui.theme.LightColorPalette

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContent {
      var darkTheme by remember { mutableStateOf(true) }
      JetLimeTheme(darkTheme = darkTheme) {
        UiControllerEffect(darkTheme)
        HomeScreen(
          isDarkTheme = darkTheme,
          onThemeChange = {
            darkTheme = it
          },
        )
      }
    }
  }
}

@Composable
fun UiControllerEffect(isDarkTheme: Boolean) {
  val view = LocalView.current
  val colors = if (isDarkTheme) {
    DarkColorPalette
  } else {
    LightColorPalette
  }
  if (!view.isInEditMode) {
    SideEffect {
      val window = (view.context as Activity).window.apply {
        statusBarColor = colors.background.toArgb()
        navigationBarColor = colors.background.toArgb()
      }
      WindowCompat.getInsetsController(window, view).apply {
        isAppearanceLightStatusBars = isDarkTheme
        isAppearanceLightNavigationBars = isDarkTheme
      }
    }
  }
}
