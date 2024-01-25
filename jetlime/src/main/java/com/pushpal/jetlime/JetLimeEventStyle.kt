package com.pushpal.jetlime

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

/**
 * Represents the style configuration for an event in a JetLime UI component.
 * This class encapsulates various styling properties such as position, point type, colors, radius, animation, and stroke attributes for an event point.
 *
 * @property position The position of the event in the UI component.
 * @property pointType The type of the event point.
 * @property pointColor The color of the event point.
 * @property pointFillColor The fill color of the event point.
 * @property pointRadius The radius of the event point.
 * @property pointAnimation Optional animation for the event point.
 * @property pointStrokeWidth The stroke width of the event point.
 * @property pointStrokeColor The stroke color of the event point.
 */
@Immutable
class JetLimeEventStyle(
  val position: EventPosition = JetLimeEventDefaults.Position,
  val pointType: EventPointType = JetLimeEventDefaults.PointType,
  val pointColor: Color = JetLimeEventDefaults.PointColor,
  val pointFillColor: Color = JetLimeEventDefaults.PointFillColor,
  val pointRadius: Dp = JetLimeEventDefaults.PointRadius,
  val pointAnimation: EventPointAnimation? = null,
  val pointStrokeWidth: Dp = JetLimeEventDefaults.PointStrokeWidth,
  val pointStrokeColor: Color = JetLimeEventDefaults.PointStrokeColor
) {
  companion object {
    /** The default style for JetLime event. */
    @Stable
    val Default = JetLimeEventStyle()
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
    if (other !is JetLimeEventStyle) return false
    if (position != other.position) return false
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