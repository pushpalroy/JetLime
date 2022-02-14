package com.pushpal.jetlime.data.config

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class JetLimeViewConfig(
  val backgroundColor: Color = Color(0xFF1C2027),
  val lineColor: Color = Color(0xFF3C9BFF),
  val lineThickness: Float = 12F,
  val lineType: LineType = LineType.Solid,
  val lineStartMargin: Dp = 48.dp,
  val lineEndMargin: Dp = 36.dp,
  val enableItemAnimation: Boolean = false,
  val iconSize: Dp = 26.dp,
  val iconShape: Shape = CircleShape,
  val iconBorderThickness: Dp = 2.dp,
  val itemSpacing: Dp = 0.dp,
  val showIcons: Boolean = true
)

sealed class LineType {
  object Solid : LineType()
  class Dashed(
    val intervals: FloatArray = floatArrayOf(20f, 20f),
    val phase: Float = 50f
  ) : LineType()
}