package com.pushpal.jetlime

import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.runtime.Immutable

/**
 * Represents an animation configuration for an event point in a UI component.
 * This animation defines how an event point should animate, including its initial value, target value, and the specification of the animation.
 *
 * @property initialValue The starting value of the animation.
 * @property targetValue The ending value of the animation.
 * @property animationSpec The specification of the animation, including duration, easing, and repeat behavior.
 */
@Immutable
class EventPointAnimation(
  val initialValue: Float = 1.0f,
  val targetValue: Float = 1.2f,
  val animationSpec: InfiniteRepeatableSpec<Float> = JetLimeEventDefaults.PointAnimation
) {
  /**
   * Compares this EventPointAnimation with another object for equality.
   * The result is `true` if and only if the argument is not `null`, is an `EventPointAnimation` object,
   * and has the same values for `initialValue`, `targetValue`, and `animationSpec`.
   *
   * @param other The object to compare this `EventPointAnimation` against.
   * @return `true` if the given object represents an `EventPointAnimation` equivalent to this instance, `false` otherwise.
   */
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is EventPointAnimation) return false

    if (initialValue != other.initialValue) return false
    if (targetValue != other.targetValue) return false
    return animationSpec == other.animationSpec
  }

  /**
   * Returns a hash code value for the object, consistent with the definition of equality for the class.
   * This method is supported for the benefit of hash tables such as those provided by `HashMap`.
   *
   * @return A hash code value for this object.
   */
  override fun hashCode(): Int {
    var result = initialValue.hashCode()
    result = 31 * result + targetValue.hashCode()
    result = 31 * result + animationSpec.hashCode()
    return result
  }
}
