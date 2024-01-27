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
package com.pushpal.jetlime

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.RepeatMode.Reverse
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object JetLimeEventDefaults {
  private val PointType: EventPointType = EventPointType.filled(0.5f)
  private val PointRadius: Dp = 12.dp
  private val PointStrokeWidth: Dp = 2.dp
  private val PointAnimation: InfiniteRepeatableSpec<Float> = infiniteRepeatable(
    animation = tween(800, easing = FastOutLinearInEasing),
    repeatMode = Reverse,
  )

  @Composable
  fun eventStyle(
    pointType: EventPointType = PointType,
    pointColor: Color = MaterialTheme.colorScheme.onPrimary,
    pointFillColor: Color = MaterialTheme.colorScheme.primary,
    pointRadius: Dp = PointRadius,
    pointAnimation: EventPointAnimation? = null,
    pointStrokeWidth: Dp = PointStrokeWidth,
    pointStrokeColor: Color = MaterialTheme.colorScheme.primary,
  ): JetLimeEventStyle {
    return JetLimeEventStyle(
      pointType = pointType,
      pointColor = pointColor,
      pointFillColor = pointFillColor,
      pointRadius = pointRadius,
      pointAnimation = pointAnimation,
      pointStrokeWidth = pointStrokeWidth,
      pointStrokeColor = pointStrokeColor,
    )
  }

  @Composable
  fun pointAnimation(
    initialValue: Float = 1.0f,
    targetValue: Float = 1.2f,
    animationSpec: InfiniteRepeatableSpec<Float> = PointAnimation,
  ): EventPointAnimation {
    return EventPointAnimation(
      initialValue = initialValue,
      targetValue = targetValue,
      animationSpec = animationSpec,
    )
  }
}
