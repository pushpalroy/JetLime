package com.pushpal.jetlime

import androidx.compose.runtime.Immutable

@Immutable
class LineStyleType private constructor(
  private val intervals: FloatArray? = null,
  private val phase: Float? = null
) {
  companion object {
    fun solid(): LineStyleType = LineStyleType(null, null)
    fun dashed(
      intervals: FloatArray = JetLimeDefaults.LineStyleDashedIntervals,
      phase: Float = JetLimeDefaults.LineStyleDashedPhase
    ): LineStyleType = LineStyleType(intervals, phase)
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is LineStyleType) return false

    if (!intervals.contentEquals(other.intervals)) return false
    if (phase != other.phase) return false

    return true
  }

  override fun hashCode(): Int {
    var result = intervals.hashCode()
    result = 31 * result + phase.hashCode()
    return result
  }
}