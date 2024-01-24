package com.pushpal.jetlime

import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.runtime.Immutable

@Immutable
class EventPointAnimation(
  val initialValue: Float = 1.0f,
  val targetValue: Float = 1.2f,
  val animationSpec: InfiniteRepeatableSpec<Float> = infiniteRepeatable(
    animation = tween(800, easing = FastOutLinearInEasing),
    repeatMode = RepeatMode.Reverse
  )
) {
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is EventPointAnimation) return false

    if (initialValue != other.initialValue) return false
    if (targetValue != other.targetValue) return false
    if (animationSpec != other.animationSpec) return false

    return true
  }

  override fun hashCode(): Int {
    var result = initialValue.hashCode()
    result = 31 * result + targetValue.hashCode()
    result = 31 * result + animationSpec.hashCode()
    return result
  }
}
