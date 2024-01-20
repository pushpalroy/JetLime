package com.pushpal.jetlime.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.google.accompanist.insets.ProvideWindowInsets
import com.pushpal.jetlime.ui.theme.JetLimeTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // Displaying edge-to-edge
    WindowCompat.setDecorFitsSystemWindows(window, false)

    setContent {
      JetLimeTheme(darkTheme = true) {
        ProvideWindowInsets {
          HomeScreen()
        }
      }
    }
  }
}