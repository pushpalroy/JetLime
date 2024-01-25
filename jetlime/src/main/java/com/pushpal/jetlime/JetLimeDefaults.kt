package com.pushpal.jetlime

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object JetLimeDefaults {
  val BackgroundColor: Color = Color(0xFF1C2027)
  val LineColor: Color = Color(0xFF3E5C81)
  val LineThickness: Dp = 4.dp
  val ContentDistance: Dp = 16.dp
  val ItemSpacing: Dp = 8.dp

  fun lineGradientBrush(
    colors: List<Color> = listOf(
      Color(0xFF3E5C81),
      Color(0xFF2A9E36),
      Color(0xFFBB1F67)
    ),
    start: Offset = Offset.Zero,
    end: Offset = Offset.Infinite,
    tileMode: TileMode = TileMode.Clamp
  ): Brush {
    return Brush.linearGradient(
      colors = colors,
      start = start,
      end = end,
      tileMode = tileMode
    )
  }

  fun lineSolidBrush(color: Color = LineColor): Brush {
    return SolidColor(color)
  }
}