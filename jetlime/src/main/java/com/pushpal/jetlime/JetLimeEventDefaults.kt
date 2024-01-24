package com.pushpal.jetlime

import androidx.compose.animation.core.keyframes
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object JetLimeEventDefaults {
  val Position: EventPosition = EventPosition.middle()
  val PointType: EventPointType = EventPointType.empty()
  val PointColor: Color = Color(0xFF2D4869)
  val PointFillColor: Color = Color(0xffffffff)
  val PointRadius: Dp = 12.dp
  val PointStrokeWidth: Dp = 2.dp
  val PointStrokeColor: Color = Color(0xffffffff)
  val PointAnimation = JetLimeEventStyle.EventPointAnimation(
    initialValue = 0.5f,
    targetValue = 1f,
    keySpecs = keyframes {
      durationMillis = 1000
      0.7f at 500
    }
  )
}