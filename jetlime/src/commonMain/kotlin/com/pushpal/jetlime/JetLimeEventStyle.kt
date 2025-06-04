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

/**
 * Represents the style configuration for an event in a [JetLimeEvent] UI component.
 * This class encapsulates various styling properties such as position, point type, colors, radius, animation, and stroke attributes for an event point.
 *
 * @property pointType The type of the event point: Empty, Filled or Custom.
 * @property pointColor The color of the event point.
 * @property pointFillColor The fill color of the event point.
 * @property pointRadius The radius of the event point.
 * @property pointAnimation Optional animation for the event point.
 * @property pointStrokeWidth The stroke width of the event point.
 * @property pointStrokeColor The stroke color of the event point.
 * @property pointPlacement The placement of the point relative to the event content (START, CENTER, or END).
 */
@Immutable
class JetLimeEventStyle internal constructor(
  var pointPlacement: PointPlacement,
  val pointType: EventPointType,
  val pointColor: Color,
  val pointFillColor: Color,
  val pointRadius: Dp,
  val pointAnimation: EventPointAnimation?,
  val pointStrokeWidth: Dp,
  val pointStrokeColor: Color,
  val lineBrush: Brush?
) {

  /** The position of the event in the UI component. */
  var position: EventPosition = EventPosition.END

  /**
   * Sets the position of the [JetLimeEvent].
   *
   * This function allows for changing the position of a JetLime event. It modifies the current
   * instance of [JetLimeEventStyle], setting its position property to the specified [EventPosition].
   *
   * @param position The [EventPosition] to set for the JetLime event.
   * @return A [JetLimeEventStyle] instance with the updated position.
   */
  @Stable
  fun setPosition(position: EventPosition): JetLimeEventStyle = this.apply {
    this.position = position
  }

  /**
   * Sets the placement of the point relative to the event content.
   *
   * @param pointPlacement The [PointPlacement] to use for drawing the point.
   * @return A [JetLimeEventStyle] instance with the updated point placement.
   */
  @Stable
  fun setPointPlacement(pointPlacement: PointPlacement): JetLimeEventStyle = this.apply {
    this.pointPlacement = pointPlacement
  }

  /**
   * Checks if this instance is equal to another object. Two instances of [JetLimeEventStyle] are
   * considered equal if they have the same values for all properties.
   *
   * @param other The object to compare this instance with.
   * @return `true` if the other object is an instance of [JetLimeEventStyle] and has the same property values, `false` otherwise.
   */
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other == null || other !is JetLimeEventStyle) return false
    if (position != other.position) return false
    if (pointPlacement != other.pointPlacement) return false
    if (pointType != other.pointType) return false
    if (pointColor != other.pointColor) return false
    if (pointFillColor != other.pointFillColor) return false
    if (pointRadius != other.pointRadius) return false
    if (pointAnimation != other.pointAnimation) return false
    if (pointStrokeWidth != other.pointStrokeWidth) return false
    return pointStrokeColor == other.pointStrokeColor
  }

  /**
   * Returns a hash code value for the object, consistent with the definition of equality for the class.
   * This supports the use in hash tables, like those provided by `HashMap`.
   *
   * @return A hash code value for this object.
   */
  override fun hashCode(): Int {
    var result = position.hashCode()
    result = 31 * result + pointPlacement.hashCode()
    result = 31 * result + pointType.hashCode()
    result = 31 * result + pointColor.hashCode()
    result = 31 * result + pointFillColor.hashCode()
    result = 31 * result + pointRadius.hashCode()
    result = 31 * result + pointAnimation.hashCode()
    result = 31 * result + pointStrokeWidth.hashCode()
    result = 31 * result + pointStrokeColor.hashCode()

    return result
  }
}

/**
 * Defines the placement of the timeline point relative to the event content.
 */
enum class PointPlacement {
  /** Point drawn at the start edge (existing default behaviour). */
  START,

  /** Point drawn centered relative to the event content box. */
  CENTER,

  /** Point drawn at the end edge (bottom for vertical, right for horizontal). */
  END,
}
