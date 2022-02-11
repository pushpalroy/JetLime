package com.pushpal.jetlime.data.config

import androidx.compose.animation.core.KeyframesSpec
import androidx.compose.animation.core.keyframes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class JetLimeItemConfig(
  var position: Int = 0,
  var titleColor: Color = Color(0xFFFFFFFF),
  var descriptionColor: Color = Color(0xFFCFCFCF),
  var iconType: IconType = IconType.Empty,
  var iconAnimation: IconAnimation? = null,
  var iconColor: Color = Color(0xFF2D4869),
  var iconBorderColor: Color = Color(0xffffffff),
  var iconBackgroundColor: Color = Color(0xffffffff),
  var itemHeight: Dp = 80.dp
) {
  fun isFirstItem(): Boolean {
    return position == 0
  }

  fun isLastItem(totalItems: Int): Boolean {
    return position == totalItems - 1
  }
}

sealed class IconType {
  object Empty : IconType()
  object Checked : IconType()
  object Filled : IconType()
  class Custom(val iconImage: ImageVector = Icons.Filled.CheckCircle) : IconType()
}

data class IconAnimation(
  val initialValue: Float = 0.5f,
  val targetValue: Float = 1f,
  val keySpecs: KeyframesSpec<Float> = keyframes {
    durationMillis = 1000
    0.7f at 500
  }
)