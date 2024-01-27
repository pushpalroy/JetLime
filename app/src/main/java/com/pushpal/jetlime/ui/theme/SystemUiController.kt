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

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.luminance

interface SystemUiController {
  fun setStatusBarColor(
    color: Color,
    darkIcons: Boolean = color.luminance() > 0.5f,
    transformColorForLightContent: (Color) -> Color = BlackScrimmed,
  )

  fun setNavigationBarColor(
    color: Color,
    darkIcons: Boolean = color.luminance() > 0.5f,
    transformColorForLightContent: (Color) -> Color = BlackScrimmed,
  )

  fun setSystemBarsColor(
    color: Color,
    darkIcons: Boolean = color.luminance() > 0.5f,
    transformColorForLightContent: (Color) -> Color = BlackScrimmed,
  )
}

/**
 * An [androidx.compose.runtime.CompositionLocalProvider] holding the current [LocalSysUiController]. Defaults to a
 * no-op controller; consumers should [provide][androidx.compose.runtime.CompositionLocalProvider] a real one.
 */
val LocalSysUiController =
  staticCompositionLocalOf<SystemUiController> {
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
    transformColorForLightContent: (Color) -> Color,
  ) = Unit

  override fun setNavigationBarColor(
    color: Color,
    darkIcons: Boolean,
    transformColorForLightContent: (Color) -> Color,
  ) = Unit

  override fun setSystemBarsColor(
    color: Color,
    darkIcons: Boolean,
    transformColorForLightContent: (Color) -> Color,
  ) = Unit
}
