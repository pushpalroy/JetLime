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

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pushpal.jetlime.HorizontalAlignment.BOTTOM
import com.pushpal.jetlime.HorizontalAlignment.TOP
import com.pushpal.jetlime.VerticalAlignment.LEFT
import com.pushpal.jetlime.VerticalAlignment.RIGHT
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * Provides default values and utility functions for [JetLimeColumn] or [JetLimeRow] styling.
 *
 * This object contains default values and composable functions that create different types of brushes and a default [JetLimeStyle].
 * It acts as a utility provider for the [JetLimeColumn] or [JetLimeRow] component styling, allowing for consistent default styling across the application.
 */
object JetLimeDefaults {
  private val LineThickness: Dp = 4.dp
  private val ContentDistance: Dp = 16.dp
  private val ItemSpacing: Dp = 8.dp

  /**
   * Creates a linear gradient brush for lines in [JetLimeColumn] or [JetLimeRow] components.
   *
   * @param colors The colors to be used in the gradient. Defaults to primary, secondary, and tertiary colors from MaterialTheme's color scheme.
   * @param start The start offset for the gradient.
   * @param end The end offset for the gradient.
   * @param tileMode The tile mode for the gradient.
   * @return A [Brush] object representing a linear gradient.
   */
  @Composable
  fun lineGradientBrush(
    colors: ImmutableList<Color> = persistentListOf(
      MaterialTheme.colorScheme.primary,
      MaterialTheme.colorScheme.secondary,
      MaterialTheme.colorScheme.tertiary,
    ),
    start: Offset = Offset.Zero,
    end: Offset = Offset.Infinite,
    tileMode: TileMode = TileMode.Clamp,
  ): Brush = Brush.linearGradient(
    colors = colors,
    start = start,
    end = end,
    tileMode = tileMode,
  )

  /**
   * Creates a solid color brush for lines in [JetLimeColumn] or [JetLimeRow] components.
   *
   * @param color The color to be used for the brush. Defaults to the primary color from MaterialTheme's color scheme.
   * @return A [Brush] object representing a solid color.
   */
  @Composable
  fun lineSolidBrush(color: Color = MaterialTheme.colorScheme.primary): Brush = SolidColor(color)

  /**
   * Creates a default [JetLimeStyle] object with specified parameters.
   *
   * @param contentDistance The distance of content from the start of the JetLime component.
   * @param itemSpacing The spacing between items in the JetLime component.
   * @param lineThickness The thickness of the line in the JetLime component.
   * @param lineBrush The brush used for the line in the JetLime component.
   * @param pathEffect the effect applied to the geometry of the timeline to obtain a dashed pattern.
   * @param lineHorizontalAlignment The horizontal alignment of the line in the JetLime component.
   * @param lineVerticalAlignment The vertical alignment of the line in the JetLime component.
   * @return A [JetLimeStyle] object configured with the given parameters.
   */
  @Composable
  internal fun jetLimeStyle(
    contentDistance: Dp,
    itemSpacing: Dp,
    lineThickness: Dp,
    lineBrush: Brush,
    pathEffect: PathEffect?,
    lineHorizontalAlignment: HorizontalAlignment = TOP,
    lineVerticalAlignment: VerticalAlignment = LEFT,
  ): JetLimeStyle = JetLimeStyle(
    contentDistance = contentDistance,
    itemSpacing = itemSpacing,
    lineThickness = lineThickness,
    lineBrush = lineBrush,
    pathEffect = pathEffect,
    lineHorizontalAlignment = lineHorizontalAlignment,
    lineVerticalAlignment = lineVerticalAlignment,
  )

  /**
   * Creates a column style configuration for [JetLimeColumn].
   *
   * @param contentDistance The distance of content from the JetLime component's start.
   * @param itemSpacing The spacing between items in the JetLime component.
   * @param lineThickness The thickness of the line in the JetLime component.
   * @param lineBrush The brush used for the line in the JetLime component.
   * @param pathEffect the effect applied to the geometry of the timeline to obtain a dashed pattern.
   * @param lineVerticalAlignment The vertical alignment of the line: [LEFT] or [RIGHT]
   * @return A [JetLimeStyle] instance configured for column arrangement.
   */
  @Composable
  fun columnStyle(
    contentDistance: Dp = ContentDistance,
    itemSpacing: Dp = ItemSpacing,
    lineThickness: Dp = LineThickness,
    lineBrush: Brush = lineSolidBrush(),
    pathEffect: PathEffect? = null,
    lineVerticalAlignment: VerticalAlignment = LEFT,
  ) = jetLimeStyle(
    contentDistance,
    itemSpacing,
    lineThickness,
    lineBrush,
    pathEffect,
    lineVerticalAlignment = lineVerticalAlignment,
  )

  /**
   * Creates a row style configuration for [JetLimeRow].
   *
   * @param contentDistance The distance of content from the JetLime component's start.
   * @param itemSpacing The spacing between items in the JetLime component.
   * @param lineThickness The thickness of the line in the JetLime component.
   * @param lineBrush The brush used for the line in the JetLime component.
   * @param pathEffect the effect applied to the geometry of the timeline to obtain a dashed pattern.
   * @param lineHorizontalAlignment The horizontal alignment of the line: [TOP] or [BOTTOM]
   * @return A [JetLimeStyle] instance configured for row arrangement.
   */
  @Composable
  fun rowStyle(
    contentDistance: Dp = ContentDistance,
    itemSpacing: Dp = ItemSpacing,
    lineThickness: Dp = LineThickness,
    lineBrush: Brush = lineSolidBrush(),
    pathEffect: PathEffect? = null,
    lineHorizontalAlignment: HorizontalAlignment = TOP,
  ) = jetLimeStyle(
    contentDistance,
    itemSpacing,
    lineThickness,
    lineBrush,
    pathEffect,
    lineHorizontalAlignment = lineHorizontalAlignment,
  )
}
