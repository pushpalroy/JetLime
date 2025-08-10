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

/**
 * An immutable list class that holds a list of items of type [T].
 *
 * This class provides an immutable wrapper around a standard list, ensuring that the contents cannot be modified after creation.
 * It overrides `equals` and `hashCode` methods to provide proper equality checks and hash code generation based on the list contents.
 *
 * @param T The type of elements in the list.
 * @property items The list of items contained in this `ItemsList`.
 */
@Immutable
class ItemsList<T>(val items: List<T>) {

  /**
   * Compares this `ItemsList` object with another object for equality.
   * The comparison checks whether the other object is also an `ItemsList` and contains the same items in the same order.
   *
   * @param other The object to compare with this `ItemsList`.
   * @return `true` if the other object is an `ItemsList` with the same items, `false` otherwise.
   */
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is ItemsList<*>) return false
    return items == other.items
  }

  /**
   * Generates a hash code for this `ItemsList`.
   * The hash code is generated based on the items in the list.
   *
   * @return The hash code value for this `ItemsList`.
   */
  override fun hashCode(): Int = items.hashCode()
}
