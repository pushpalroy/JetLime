package com.pushpal.jetlime

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.RepeatMode.Reverse
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object JetLimeEventDefaults {
  val Position: EventPosition = EventPosition.MIDDLE
  val PointType: EventPointType = EventPointType.EMPTY
  val PointColor: Color = Color(0xFF2D4869)
  val PointFillColor: Color = Color(0xffffffff)
  val PointRadius: Dp = 12.dp
  val PointStrokeWidth: Dp = 2.dp
  val PointStrokeColor: Color = Color(0xffffffff)
  val PointAnimation: InfiniteRepeatableSpec<Float> = infiniteRepeatable(
    animation = tween(800, easing = FastOutLinearInEasing),
    repeatMode = Reverse
  )
}