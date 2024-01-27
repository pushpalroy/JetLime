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

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.pushpal.jetlime.Arrangement.VERTICAL
import com.pushpal.jetlime.HorizontalAlignment.TOP
import com.pushpal.jetlime.VerticalAlignment.LEFT

/**
 * Represents the style configuration for JetLime.
 * This class defines the styling parameters for JetLime components, such as background color, content distance, item spacing, and line properties.
 *
 * @property backgroundColor The background color of the JetLime component.
 * @property contentDistance The distance of content from the JetLime component's start.
 * @property itemSpacing The spacing between items in the JetLime component.
 * @property lineColor The color of the line in the JetLime component.
 * @property lineThickness The thickness of the line in the JetLime component.
 * @property lineBrush The brush used for the line in the JetLime component.
 * @property pointStartFactor The factor determining the start position of the point in the line.
 * @property lineHorizontalAlignment The horizontal alignment of the line.
 * @property lineVerticalAlignment The vertical alignment of the line.
 */
@Immutable
class JetLimeStyle(
  val backgroundColor: Color = JetLimeDefaults.BackgroundColor,
  val contentDistance: Dp = JetLimeDefaults.ContentDistance,
  val itemSpacing: Dp = JetLimeDefaults.ItemSpacing,
  val lineColor: Color = JetLimeDefaults.LineColor,
  val lineThickness: Dp = JetLimeDefaults.LineThickness,
  val lineBrush: Brush = JetLimeDefaults.lineSolidBrush(),
  val pointStartFactor: Float = 1.1f,
  val lineHorizontalAlignment: HorizontalAlignment = TOP,
  val lineVerticalAlignment: VerticalAlignment = LEFT,
) {

  var arrangement: Arrangement = VERTICAL

  companion object {
    /**
     * Creates a column style configuration for JetLime.
     *
     * @param backgroundColor The background color of the JetLime component.
     * @param contentDistance The distance of content from the JetLime component's start.
     * @param itemSpacing The spacing between items in the JetLime component.
     * @param lineColor The color of the line in the JetLime component.
     * @param lineThickness The thickness of the line in the JetLime component.
     * @param lineBrush The brush used for the line in the JetLime component.
     * @param pointStartFactor The factor determining the start position of the point in the line.
     * @param lineVerticalAlignment The vertical alignment of the line.
     * @return A [JetLimeStyle] instance configured for column arrangement.
     */
    @Stable
    fun columnStyle(
      backgroundColor: Color = JetLimeDefaults.BackgroundColor,
      contentDistance: Dp = JetLimeDefaults.ContentDistance,
      itemSpacing: Dp = JetLimeDefaults.ItemSpacing,
      lineColor: Color = JetLimeDefaults.LineColor,
      lineThickness: Dp = JetLimeDefaults.LineThickness,
      lineBrush: Brush = JetLimeDefaults.lineSolidBrush(),
      pointStartFactor: Float = 1.1f,
      lineVerticalAlignment: VerticalAlignment = LEFT,
    ) = JetLimeStyle(
      backgroundColor,
      contentDistance,
      itemSpacing,
      lineColor,
      lineThickness,
      lineBrush,
      pointStartFactor,
      lineVerticalAlignment = lineVerticalAlignment,
    )

    /**
     * Creates a row style configuration for JetLime.
     *
     * @param backgroundColor The background color of the JetLime component.
     * @param contentDistance The distance of content from the JetLime component's start.
     * @param itemSpacing The spacing between items in the JetLime component.
     * @param lineColor The color of the line in the JetLime component.
     * @param lineThickness The thickness of the line in the JetLime component.
     * @param lineBrush The brush used for the line in the JetLime component.
     * @param pointStartFactor The factor determining the start position of the point in the line.
     * @param lineHorizontalAlignment The horizontal alignment of the line.
     * @return A [JetLimeStyle] instance configured for row arrangement.
     */
    @Stable
    fun rowStyle(
      backgroundColor: Color = JetLimeDefaults.BackgroundColor,
      contentDistance: Dp = JetLimeDefaults.ContentDistance,
      itemSpacing: Dp = JetLimeDefaults.ItemSpacing,
      lineColor: Color = JetLimeDefaults.LineColor,
      lineThickness: Dp = JetLimeDefaults.LineThickness,
      lineBrush: Brush = JetLimeDefaults.lineSolidBrush(),
      pointStartFactor: Float = 1.1f,
      lineHorizontalAlignment: HorizontalAlignment = TOP,
    ) = JetLimeStyle(
      backgroundColor,
      contentDistance,
      itemSpacing,
      lineColor,
      lineThickness,
      lineBrush,
      pointStartFactor,
      lineHorizontalAlignment = lineHorizontalAlignment,
    )
  }

  /**
   * Sets the alignment of the JetLime component.
   *
   * @param arrangement The desired arrangement (either VERTICAL or HORIZONTAL).
   * @return A [JetLimeStyle] instance with the updated arrangement.
   */
  @Stable
  fun alignment(arrangement: Arrangement = VERTICAL): JetLimeStyle {
    return this.apply {
      this.arrangement = arrangement
    }
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is JetLimeStyle) return false
    if (backgroundColor != other.backgroundColor) return false
    if (contentDistance != other.contentDistance) return false
    if (itemSpacing != other.itemSpacing) return false
    if (lineColor != other.lineColor) return false
    if (lineThickness != other.lineThickness) return false
    if (lineBrush != other.lineBrush) return false
    if (lineHorizontalAlignment != other.lineHorizontalAlignment) return false
    if (lineVerticalAlignment != other.lineVerticalAlignment) return false
    return pointStartFactor == other.pointStartFactor
  }

  override fun hashCode(): Int {
    var result = backgroundColor.hashCode()
    result = 31 * result + contentDistance.hashCode()
    result = 31 * result + itemSpacing.hashCode()
    result = 31 * result + lineColor.hashCode()
    result = 31 * result + lineThickness.hashCode()
    result = 31 * result + lineBrush.hashCode()
    result = 31 * result + lineHorizontalAlignment.hashCode()
    result = 31 * result + lineVerticalAlignment.hashCode()
    result = 31 * result + pointStartFactor.hashCode()
    return result
  }
}

/**
 * Enum representing the possible arrangements for JetLime components.
 */
@Stable
enum class Arrangement {
  VERTICAL,
  HORIZONTAL,
}

/**
 * Enum representing the horizontal alignment options for JetLime components.
 */
@Stable
enum class HorizontalAlignment {
  TOP,
  BOTTOM,
}

/**
 * Enum representing the vertical alignment options for JetLime components.
 */
@Stable
enum class VerticalAlignment {
  LEFT,
  RIGHT,
}
