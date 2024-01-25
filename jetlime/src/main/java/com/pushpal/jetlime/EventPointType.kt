package com.pushpal.jetlime

import androidx.compose.ui.graphics.painter.Painter

class EventPointType private constructor(
  val type: String,
  val icon: Painter? = null
) {
  companion object {
    val EMPTY = EventPointType("Empty")

    val FILLED = EventPointType("Filled")

    internal const val CUSTOM = "Custom"

    fun custom(icon: Painter): EventPointType = EventPointType(CUSTOM, icon)
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is EventPointType) return false
    if (type != other.type) return false
    return icon == other.icon
  }

  override fun hashCode(): Int {
    var result = type.hashCode()
    result = 31 * result + icon.hashCode()
    return result
  }
}
