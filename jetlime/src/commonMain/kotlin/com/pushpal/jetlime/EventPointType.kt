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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter

/**
 * Represents a type of event point in a UI component with an optional icon. This class is used to define
 * different types of event points such as empty, filled, or custom types with specific icons.
 *
 * @property type The name of the event point type.
 * @property icon An optional [Painter] icon associated with the event point type.
 * @property tint An optional tint [Color] overlay for the icon.
 * @property fillPercent An optional percentage value for the [FILLED] type
 */
@Immutable
class EventPointType internal constructor(
  val type: String,
  val icon: Painter? = null,
  val tint: Color? = null,
  val fillPercent: Float? = null,
) {
  companion object {
    /** Represents an empty event point type. */
    val EMPTY = EventPointType("Empty")

    /** Internal constant used for filled event point types. */
    internal const val FILLED = "Filled"

    /** Internal constant used for custom event point types. */
    internal const val CUSTOM = "Custom"

    /** The default value of [EventPointType] */
    val Default = filled()

    /**
     * Creates a custom event point type with a specified [Painter] icon.
     *
     * @param icon The [Painter] icon for the custom event point type.
     * @param tint The optional tint [Color] of the custom icon.
     * @return A new instance of [EventPointType] with the custom icon.
     */
    @Stable
    fun custom(icon: Painter, tint: Color? = null): EventPointType =
      EventPointType(type = CUSTOM, icon = icon, tint = tint)

    /**
     * Creates a filled event point type with a specified fill percentage.
     *
     * @param fillPercent The percentage of fill for the event point.
     * @return A new instance of [EventPointType] with the filled icon.
     */
    @Stable
    fun filled(fillPercent: Float = 0.5f): EventPointType =
      EventPointType(type = FILLED, fillPercent = fillPercent)
  }

  /**
   * A helper function to check if the current [EventPointType] is [FILLED]
   *
   * @return `true` if the current EventPointType is filled, `false` otherwise.
   */
  @Stable
  fun isFilled(): Boolean = this.type == FILLED

  /**
   * A helper function to check if the current [EventPointType] is [CUSTOM]
   *
   * @return `true` if the current EventPointType is custom, `false` otherwise.
   */
  @Stable
  fun isCustom(): Boolean = this.type == CUSTOM

  /**
   * A helper function to check if the current [EventPointType] is [EMPTY] or [FILLED]
   *
   * @return `true` if the current EventPointType is empty or filled, `false` otherwise.
   */
  @Stable
  fun isEmptyOrFilled(): Boolean = this == EMPTY || this.type == FILLED

  /**
   * Checks if this instance is equal to another object. Two instances of [EventPointType] are
   * considered equal if they have the same type and icon.
   *
   * @param other The object to compare this instance with.
   * @return `true` if the other object is an instance of [EventPointType] and has the same type and icon, `false` otherwise.
   */
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is EventPointType) return false
    if (type != other.type) return false
    if (tint != other.tint) return false
    if (fillPercent != other.fillPercent) return false
    return icon == other.icon
  }

  /**
   * Returns a hash code value for the object, which is consistent with the definition of equality for the class.
   * This supports the use in hash tables, like those provided by `HashMap`.
   *
   * @return A hash code value for this object.
   */
  override fun hashCode(): Int {
    var result = type.hashCode()
    result = 31 * result + icon.hashCode()
    result = 31 * result + tint.hashCode()
    result = 31 * result + fillPercent.hashCode()
    return result
  }
}
