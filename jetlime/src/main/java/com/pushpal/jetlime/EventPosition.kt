package com.pushpal.jetlime

import androidx.compose.runtime.Immutable

@Immutable
class EventPosition private constructor(val name: String) {
  companion object {

    val START = EventPosition("Start")
    val MIDDLE = EventPosition("Middle")
    val END = EventPosition("End")

    fun dynamic(
      index: Int,
      listSize: Int
    ) = eventPosition(index, listSize)

    private fun eventPosition(
      index: Int,
      listSize: Int
    ) = when (index) {
      0 -> START
      listSize - 1 -> END
      else -> MIDDLE
    }
  }

  fun isNotEnd(): Boolean {
    return this != END
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is EventPosition) return false
    return name == other.name
  }

  override fun hashCode(): Int {
    return javaClass.hashCode()
  }
}