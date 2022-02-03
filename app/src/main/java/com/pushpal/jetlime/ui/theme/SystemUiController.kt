package com.pushpal.jetlime.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.luminance

interface SystemUiController {
  fun setStatusBarColor(
    color: Color,
    darkIcons: Boolean = color.luminance() > 0.5f,
    transformColorForLightContent: (Color) -> Color = BlackScrimmed
  )

  fun setNavigationBarColor(
    color: Color,
    darkIcons: Boolean = color.luminance() > 0.5f,
    transformColorForLightContent: (Color) -> Color = BlackScrimmed
  )

  fun setSystemBarsColor(
    color: Color,
    darkIcons: Boolean = color.luminance() > 0.5f,
    transformColorForLightContent: (Color) -> Color = BlackScrimmed
  )
}

/**
 * An [androidx.compose.runtime.CompositionLocalProvider] holding the current [LocalSysUiController]. Defaults to a
 * no-op controller; consumers should [provide][androidx.compose.runtime.CompositionLocalProvider] a real one.
 */
val LocalSysUiController = staticCompositionLocalOf<SystemUiController> {
  FakeSystemUiController
}

private val BlackScrim = Color(0f, 0f, 0f, 0.2f) // 20% opaque black
private val BlackScrimmed: (Color) -> Color = { original ->
  BlackScrim.compositeOver(original)
}

/**
 * A fake implementation, useful as a default or used in Previews.
 */
private object FakeSystemUiController : SystemUiController {
  override fun setStatusBarColor(
    color: Color,
    darkIcons: Boolean,
    transformColorForLightContent: (Color) -> Color
  ) = Unit

  override fun setNavigationBarColor(
    color: Color,
    darkIcons: Boolean,
    transformColorForLightContent: (Color) -> Color
  ) = Unit

  override fun setSystemBarsColor(
    color: Color,
    darkIcons: Boolean,
    transformColorForLightContent: (Color) -> Color
  ) = Unit
}
