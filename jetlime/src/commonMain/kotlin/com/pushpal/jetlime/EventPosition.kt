/*
* MIT License
*
* Copyright (c) 2024 Pushpal Roy
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in all
* copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
*
*/
package com.pushpal.jetlime

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable

/**
 * Represents a position of an event within a sequence, such as the start, middle, or end.
 * This class encapsulates the logic for determining the position based on the index in a list.
 *
 * @property name The name of the event position.
 */
@Immutable
class EventPosition internal constructor(val name: String) {
  companion object {

    /** Represents the start position in a sequence. */
    private val START = EventPosition("Start")

    /** Represents the middle position in a sequence. */
    private val MIDDLE = EventPosition("Middle")

    /** Represents the end position in a sequence. */
    internal val END = EventPosition("End")

    /**
     * Determines the event position dynamically based on the index and the size of the list.
     *
     * @param index The index of the item in the list.
     * @param listSize The total size of the list.
     * @return [EventPosition] corresponding to the index in the list.
     */
    @Stable
    fun dynamic(index: Int, listSize: Int) = eventPosition(index, listSize)

    /**
     * Internal function to determine the event position based on index and list size.
     */
    @Stable
    private fun eventPosition(index: Int, listSize: Int) = when (index) {
      listSize - 1 -> END
      0 -> START
      else -> MIDDLE
    }
  }

  /**
   * A helper function to check if the current position is not the end position.
   * This can be useful for determining layout or drawing logic based on the position of an event.
   *
   * @return `true` if the current position is not the end, `false` otherwise.
   */
  @Stable
  fun isNotEnd(): Boolean = this != END

  /** Helper to check if current position is not the start. */
  @Stable
  fun isNotStart(): Boolean = name != "Start"

  /**
   * Checks if this instance is equal to another object. Two instances of [EventPosition] are
   * considered equal if they have the same name.
   *
   * @param other The object to compare this instance with.
   * @return `true` if the other object is an instance of [EventPosition] and has the same name, `false` otherwise.
   */
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is EventPosition) return false
    return name == other.name
  }

  /**
   * Returns a hash code value for the object, which is consistent with the definition of equality for the class.
   * This supports the use in hash tables, like those provided by `HashMap`.
   *
   * @return A hash code value for this object.
   */
  override fun hashCode(): Int = name.hashCode()
}
