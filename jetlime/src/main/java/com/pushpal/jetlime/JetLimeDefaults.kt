package com.pushpal.jetlime

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object JetLimeDefaults {
  val BackgroundColor: Color = Color(0xFF1C2027)
  val LineColor: Color = Color(0xFF3E5C81)
  val LineThickness: Dp = 4.dp
  val LineStyle: LineStyleType = LineStyleType.solid()
  val LineStyleDashedIntervals = floatArrayOf(20f, 20f)
  const val LineStyleDashedPhase = 50f
  val IconSize: Dp = 26.dp
  val Gap: Dp = 8.dp
  val IconShape: Shape = CircleShape
  val IconBorderThickness: Dp = 2.dp
  val ItemSpacing: Dp = 8.dp

//  fun lineGradientBrush(): Brush {
//    return Brush.linearGradient(
//      colors = listOf(
//        color.copy(alpha = colorAlphaList[0]),
//        color.copy(alpha = colorAlphaList[1]),
//        color.copy(alpha = colorAlphaList[2])
//      ),
//      stops = listOf(
//        colorStopList[0],
//        colorStopList[1],
//        colorStopList[2]
//      ),
//      angleInDegrees = GradientAngleInDegrees
//    )
//  }

  fun lineSolidBrush(color: Color = LineColor): Brush {
    return SolidColor(color)
  }
}