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
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.unit.Dp
import com.pushpal.jetlime.Arrangement.VERTICAL

/**
 * Represents the styling configuration for [JetLimeColumn] and [JetLimeRow] components.
 *
 * This class encapsulates various properties that define the appearance and layout of [JetLimeColumn] and [JetLimeRow] components,
 * such as content distance, item spacing, line thickness, and alignment properties. It provides a fluent API
 * for modifying these properties.
 *
 * - See [JetLimeDefaults.columnStyle] for the default style in a [JetLimeColumn].
 * - See [JetLimeDefaults.rowStyle] for the default style in a [JetLimeRow].
 *
 * @param contentDistance The distance of content from the start of the JetLime component.
 * @param itemSpacing The spacing between items in the JetLime component.
 * @param lineThickness The thickness of the line in the JetLime component.
 * @param lineBrush The brush used for the line in the JetLime component.
 * @param pathEffect the effect applied to the geometry of the timeline to obtain a dashed pattern.
 * @param lineHorizontalAlignment The horizontal alignment of the line in the JetLime component.
 * @param lineVerticalAlignment The vertical alignment of the line in the JetLime component.
 */
@Immutable
class JetLimeStyle internal constructor(
  val contentDistance: Dp,
  val itemSpacing: Dp,
  val lineThickness: Dp,
  val lineBrush: Brush,
  val pathEffect: PathEffect?,
  val lineHorizontalAlignment: HorizontalAlignment,
  val lineVerticalAlignment: VerticalAlignment,
) {

  internal var arrangement: Arrangement = VERTICAL
  internal val pointStartFactor: Float = 1.1f

  /**
   * Sets the arrangement of the JetLime list component.
   *
   * This function allows for setting the arrangement of a JetLime list component. It modifies the current
   * instance of [JetLimeStyle], setting its arrangement property to the specified [Arrangement] depending
   * on whether a component is a [JetLimeColumn] or [JetLimeRow].
   *
   * @param arrangement The [Arrangement] to set for the JetLime list component.
   * @return A [JetLimeStyle] instance with the updated arrangement.
   */
  @Stable
  internal fun alignment(arrangement: Arrangement): JetLimeStyle = this.apply {
    this.arrangement = arrangement
  }

  /**
   * Checks if this [JetLimeStyle] is equal to another object.
   *
   * Equality is determined based on the equality of content distance, item spacing, line thickness,
   * line brush, and both horizontal and vertical alignment properties.
   *
   * @param other The object to compare with this instance.
   * @return `true` if the specified object is equal to this [JetLimeStyle], `false` otherwise.
   */
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is JetLimeStyle) return false
    if (contentDistance != other.contentDistance) return false
    if (itemSpacing != other.itemSpacing) return false
    if (lineThickness != other.lineThickness) return false
    if (lineBrush != other.lineBrush) return false
    if (pathEffect != other.pathEffect) return false
    if (lineHorizontalAlignment != other.lineHorizontalAlignment) return false
    return lineVerticalAlignment == other.lineVerticalAlignment
  }

  /**
   * Generates a hash code for this [JetLimeStyle].
   *
   * The hash code is a combination of content distance, item spacing, line thickness, line brush,
   * and alignment properties.
   *
   * @return The hash code value for this [JetLimeStyle].
   */
  override fun hashCode(): Int {
    var result = contentDistance.hashCode()
    result = 31 * result + itemSpacing.hashCode()
    result = 31 * result + lineThickness.hashCode()
    result = 31 * result + lineBrush.hashCode()
    result = 31 * result + pathEffect.hashCode()
    result = 31 * result + lineHorizontalAlignment.hashCode()
    result = 31 * result + lineVerticalAlignment.hashCode()
    return result
  }
}

/**
 * Enum representing the possible arrangements for JetLime list components.
 */
@Stable
internal enum class Arrangement {
  VERTICAL,
  HORIZONTAL,
}

/**
 * Enum representing the alignment of the timeline line and points for [JetLimeRow].
 */
@Stable
enum class HorizontalAlignment {
  TOP,
  BOTTOM,
}

/**
 * Enum representing the alignment of the timeline line and points for [JetLimeColumn].
 */
@Stable
enum class VerticalAlignment {
  LEFT,
  RIGHT,
}
