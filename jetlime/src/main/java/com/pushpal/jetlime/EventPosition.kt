package com.pushpal.jetlime

import androidx.compose.runtime.Immutable

@Immutable
class EventPosition private constructor(val name: String) {
  companion object {
    fun start(): EventPosition = EventPosition("Start")
    fun middle(): EventPosition = EventPosition("Middle")
    fun end(): EventPosition = EventPosition("End")

    fun dynamic(index: Int, listSize: Int) = eventPosition(index, listSize)

    private fun eventPosition(index: Int, listSize: Int) = when (index) {
      0 -> start()
      listSize - 1 -> end()
      else -> middle()
    }
  }

  fun isNotEnd(): Boolean {
    return this != end()
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is EventPosition) return false
    if (name != other.name) return false
    return true
  }

  override fun hashCode(): Int {
    return javaClass.hashCode()
  }
}