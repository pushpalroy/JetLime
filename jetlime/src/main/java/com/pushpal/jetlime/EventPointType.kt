package com.pushpal.jetlime

import androidx.compose.ui.graphics.painter.Painter

/**
 * Represents a type of event point in a UI component with an optional icon. This class is used to define
 * different types of event points such as empty, filled, or custom types with specific icons.
 *
 * @property type The name of the event point type.
 * @property icon An optional icon associated with the event point type.
 */
class EventPointType private constructor(
  val type: String,
  val icon: Painter? = null
) {
  companion object {
    /** Represents an empty event point type. */
    val EMPTY = EventPointType("Empty")

    /** Represents a filled event point type. */
    val FILLED = EventPointType("Filled")

    /** Internal constant used for custom event point types. */
    internal const val CUSTOM = "Custom"

    /**
     * Creates a custom event point type with a specified icon.
     *
     * @param icon The icon for the custom event point type.
     * @return A new instance of [EventPointType] with the custom icon.
     */
    fun custom(icon: Painter): EventPointType = EventPointType(CUSTOM, icon)
  }

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
    return result
  }
}
