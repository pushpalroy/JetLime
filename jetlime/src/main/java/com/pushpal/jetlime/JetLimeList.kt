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

import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pushpal.jetlime.Arrangement.HORIZONTAL
import com.pushpal.jetlime.Arrangement.VERTICAL

/**
 * A composable function that creates a vertical timeline interface with a list of items.
 *
 * This function sets up a LazyColumn layout for displaying items in a vertical timeline format. It allows for customization
 * of its appearance and behavior through various parameters.
 *
 * Example usage:
 *
 * ```
 *  val items = remember { getItemsList() }
 *
 *  JetLimeColumn(
 *   itemsList = ItemsList(items),
 *   keyExtractor = { item -> item.id },
 *   style = JetLimeDefaults.columnStyle(),
 *  ) { index, item, position ->
 *     JetLimeEvent(
 *      style = JetLimeEventDefaults.eventStyle(position = position)
 *     ) {
 *        ComposableContent(item = item)
 *       }
 *    }
 * ```
 *
 * @param T The type of items in the items list.
 * @param itemsList A list of items to be displayed in the JetLimeColumn.
 * @param modifier A modifier to be applied to the LazyColumn.
 * @param style The JetLime style configuration. Defaults to a predefined column style.
 * @param listState The state object to be used for the LazyColumn.
 * @param contentPadding The padding to apply to the content inside the LazyColumn.
 * @param keyExtractor A function to extract keys from items for optimization purposes.
 * @param itemContent A composable lambda that takes an index, an item of type [T], and an [EventPosition] to build each item's content.
 */
@Composable
fun <T> JetLimeColumn(
  itemsList: ItemsList<T>,
  modifier: Modifier = Modifier,
  style: JetLimeStyle = JetLimeDefaults.columnStyle(),
  listState: LazyListState = rememberLazyListState(),
  contentPadding: PaddingValues = PaddingValues(0.dp),
  keyExtractor: (T) -> Any = {},
  itemContent: @Composable (index: Int, T, EventPosition) -> Unit,
) {
  CompositionLocalProvider(LocalJetLimeStyle provides style.alignment(VERTICAL)) {
    LazyColumn(
      modifier = modifier,
      state = listState,
      reverseLayout = false,
      verticalArrangement = Arrangement.Top,
      horizontalAlignment = Alignment.Start,
      flingBehavior = ScrollableDefaults.flingBehavior(),
      userScrollEnabled = true,
      contentPadding = contentPadding,
    ) {
      itemsIndexed(
        items = itemsList.items,
        key = { _, item -> keyExtractor(item) },
      ) { index, item ->
        val eventPosition = EventPosition.dynamic(index, itemsList.items.size)
        itemContent(index, item, eventPosition)
      }
    }
  }
}

/**
 * A composable function that creates a horizontal timeline interface with a list of items.
 *
 * This function sets up a LazyRow layout for displaying items in a horizontal timeline format. It allows for customization
 * of its appearance and behavior through various parameters.
 *
 * Example usage:
 *
 * ```
 *  val items = remember { getItemsList() }
 *
 *  JetLimeRow(
 *   itemsList = ItemsList(items),
 *   keyExtractor = { item -> item.id },
 *   style = JetLimeDefaults.rowStyle(),
 *  ) { index, item, position ->
 *     JetLimeEvent(
 *      style = JetLimeEventDefaults.eventStyle(position = position)
 *     ) {
 *        ComposableContent(item = item)
 *       }
 *    }
 * ```
 *
 * @param T The type of items in the items list.
 * @param itemsList A list of items to be displayed in the JetLimeRow.
 * @param modifier A modifier to be applied to the LazyRow.
 * @param style The JetLime style configuration. Defaults to a predefined row style.
 * @param listState The state object to be used for the LazyRow.
 * @param contentPadding The padding to apply to the content inside the LazyRow.
 * @param keyExtractor A function to extract keys from items for optimization purposes.
 * @param itemContent A composable lambda that takes an index, an item of type [T], and an [EventPosition] to build each item's content.
 */
@Composable
fun <T> JetLimeRow(
  itemsList: ItemsList<T>,
  modifier: Modifier = Modifier,
  style: JetLimeStyle = JetLimeDefaults.rowStyle(),
  listState: LazyListState = rememberLazyListState(),
  contentPadding: PaddingValues = PaddingValues(0.dp),
  keyExtractor: (T) -> Any = {},
  itemContent: @Composable (index: Int, T, EventPosition) -> Unit,
) {
  CompositionLocalProvider(LocalJetLimeStyle provides style.alignment(HORIZONTAL)) {
    LazyRow(
      modifier = modifier,
      state = listState,
      reverseLayout = false,
      horizontalArrangement = Arrangement.Start,
      verticalAlignment = Alignment.Top,
      flingBehavior = ScrollableDefaults.flingBehavior(),
      userScrollEnabled = true,
      contentPadding = contentPadding,
    ) {
      itemsIndexed(
        items = itemsList.items,
        key = { _, item -> keyExtractor(item) },
      ) { index, item ->
        val eventPosition = EventPosition.dynamic(index, itemsList.items.size)
        itemContent(index, item, eventPosition)
      }
    }
  }
}

/**
 * A CompositionLocal providing the current [JetLimeStyle].
 *
 * This is used to provide a default or overridden style configuration down the composition tree. Accessing this without a provider
 * will result in an error, ensuring that the style is always defined when used within a composable context.
 */
val LocalJetLimeStyle = compositionLocalOf<JetLimeStyle> { error("No JetLimeStyle provided") }
