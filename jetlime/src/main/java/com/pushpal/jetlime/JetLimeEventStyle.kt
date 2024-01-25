package com.pushpal.jetlime

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

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
    @Stable
    val Default = JetLimeEventStyle()
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is JetLimeEventStyle) return false
    if (position != other.position) return false
    if (pointType != other.pointType) return false
    if (pointAnimation != other.pointAnimation) return false
    if (pointColor != other.pointColor) return false
    return pointStrokeColor == other.pointStrokeColor
  }

  override fun hashCode(): Int {
    var result = position.hashCode()
    result = 31 * result + pointType.hashCode()
    result = 31 * result + pointAnimation.hashCode()
    result = 31 * result + pointColor.hashCode()
    result = 31 * result + pointStrokeColor.hashCode()

    return result
  }
}