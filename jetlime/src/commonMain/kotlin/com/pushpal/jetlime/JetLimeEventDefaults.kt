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

/**
 * Provides default values and utility functions for [JetLimeEvent] styling.
 *
 * This object contains default values and composable functions for creating event styles and point animations in [JetLimeColumn] or [JetLimeRow] components.
 * It offers a convenient way to access standard styling options and animations for JetLime events.
 */
object JetLimeEventDefaults {
  private val PointType: EventPointType = EventPointType.filled(0.5f)
  private val PointRadius: Dp = 12.dp
  private val PointStrokeWidth: Dp = 2.dp
  private val PointAnimation: InfiniteRepeatableSpec<Float> = infiniteRepeatable(
    animation = tween(800, easing = FastOutLinearInEasing),
    repeatMode = Reverse,
  )

  /**
   * Maximum width allowed to for the additional content composable used in [JetLimeExtendedEvent],
   * that will be drawn on the left side of the timeline. As the content is thought to have more
   * preference than additional content, it is assumed that additional content will be used for
   * drawing ui that will consume relatively lesser space.
   */
  internal val AdditionalContentMaxWidth = 72.dp

  /**
   * Creates a default [JetLimeEventStyle] object with specified parameters.
   *
   * @param position The position of the event relative to the timeline.
   * @param pointType The type of point used in the event. Defaults to a filled point.
   * @param pointColor The color of the point. Defaults to the 'onPrimary' color from MaterialTheme's color scheme.
   * @param pointFillColor The fill color of the point. Defaults to the primary color from MaterialTheme's color scheme.
   * @param pointRadius The radius of the point. Defaults to [PointRadius].
   * @param pointAnimation The animation for the point, if any.
   * @param pointStrokeWidth The stroke width of the point. Defaults to [PointStrokeWidth].
   * @param pointStrokeColor The stroke color of the point. Defaults to the primary color from MaterialTheme's color scheme.
   * @return A [JetLimeEventStyle] object configured with the given parameters.
   */
  @Composable
  fun eventStyle(
    position: EventPosition,
    pointType: EventPointType = PointType,
    pointColor: Color = MaterialTheme.colorScheme.onPrimary,
    pointFillColor: Color = MaterialTheme.colorScheme.primary,
    pointRadius: Dp = PointRadius,
    pointAnimation: EventPointAnimation? = null,
    pointStrokeWidth: Dp = PointStrokeWidth,
    pointStrokeColor: Color = MaterialTheme.colorScheme.primary,
  ): JetLimeEventStyle = JetLimeEventStyle(
    pointType = pointType,
    pointColor = pointColor,
    pointFillColor = pointFillColor,
    pointRadius = pointRadius,
    pointAnimation = pointAnimation,
    pointStrokeWidth = pointStrokeWidth,
    pointStrokeColor = pointStrokeColor,
  ).apply {
    this.position = position
  }

  /**
   * Creates an [EventPointAnimation] object to define animations for event points.
   *
   * @param initialValue The initial value of the animation. Defaults to 1.0f.
   * @param targetValue The target value of the animation. Defaults to 1.2f.
   * @param animationSpec The specification for the animation. Defaults to [PointAnimation].
   * @return An [EventPointAnimation] object configured with the given parameters.
   */
  @Composable
  fun pointAnimation(
    initialValue: Float = 1.0f,
    targetValue: Float = 1.2f,
    animationSpec: InfiniteRepeatableSpec<Float> = PointAnimation,
  ): EventPointAnimation = EventPointAnimation(
    initialValue = initialValue,
    targetValue = targetValue,
    animationSpec = animationSpec,
  )
}
