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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pushpal.jetlime.Arrangement.HORIZONTAL
import com.pushpal.jetlime.Arrangement.VERTICAL

/**
 * A composable function that creates a vertical timeline with built-in pagination (infinite scroll).
 *
 * It behaves exactly like [JetLimeColumn] but additionally invokes [onLoadMore] when the user scrolls
 * within [loadMoreThreshold] items of the end, as long as [isLoading] is `false` and [hasMoreItems]
 * is `true`. While a page is being fetched ([isLoading] is `true`), [loadingContent] is rendered as a
 * footer. The page state ([itemsList], [isLoading], [hasMoreItems]) is owned by the caller — the
 * caller appends the next page inside [onLoadMore] and flips the flags accordingly.
 *
 * The timeline's connecting line stays continuous across page boundaries: while [hasMoreItems] is
 * `true` the last loaded item keeps drawing its connector toward the next page instead of terminating.
 *
 * Example usage:
 *
 * ```
 *  val items = remember { mutableStateListOf<MyItem>() }
 *  var isLoading by remember { mutableStateOf(false) }
 *  var hasMore by remember { mutableStateOf(true) }
 *  val scope = rememberCoroutineScope()
 *
 *  JetLimePaginatedColumn(
 *    itemsList = ItemsList(items),
 *    isLoading = isLoading,
 *    hasMoreItems = hasMore,
 *    key = { _, item -> item.id },
 *    onLoadMore = {
 *      scope.launch {
 *        isLoading = true
 *        val page = repository.loadNextPage()
 *        items.addAll(page.items)
 *        hasMore = !page.isLast
 *        isLoading = false
 *      }
 *    },
 *  ) { _, item, position ->
 *    JetLimeEvent(style = JetLimeEventDefaults.eventStyle(position = position)) {
 *      ComposableContent(item = item)
 *    }
 *  }
 * ```
 *
 * @param T The type of items in the items list.
 * @param itemsList A list of items currently loaded in the timeline.
 * @param onLoadMore Callback invoked when the next page should be loaded.
 * @param modifier A modifier to be applied to the LazyColumn.
 * @param style The JetLime style configuration. Defaults to a predefined column style.
 * @param listState The state object to be used for the LazyColumn.
 * @param contentPadding The padding to apply to the content inside the LazyColumn.
 * @param key A factory of stable and unique keys representing the item. If null is passed the position in the list will represent the key.
 * @param isLoading Whether a page fetch is currently in flight. Suppresses further [onLoadMore] calls and shows [loadingContent].
 * @param hasMoreItems Whether more pages can be loaded. When `false`, [onLoadMore] is not called and the timeline line terminates at the last item.
 * @param loadMoreThreshold The number of items from the end at which [onLoadMore] is triggered.
 * @param loadingContent The footer composable shown while [isLoading] is `true`. Defaults to a centered circular progress indicator; pass `null` to show no footer at all.
 * @param itemContent A composable lambda that takes an index, an item of type [T], and an [EventPosition] to build each item's content.
 */
@Composable
fun <T> JetLimePaginatedColumn(
  itemsList: ItemsList<T>,
  onLoadMore: () -> Unit,
  modifier: Modifier = Modifier,
  style: JetLimeStyle = JetLimeDefaults.columnStyle(),
  listState: LazyListState = rememberLazyListState(),
  contentPadding: PaddingValues = PaddingValues(0.dp),
  key: ((index: Int, item: T) -> Any)? = null,
  isLoading: Boolean = false,
  hasMoreItems: Boolean = true,
  loadMoreThreshold: Int = JetLimeDefaults.LoadMoreThreshold,
  loadingContent: (@Composable () -> Unit)? = { DefaultColumnLoadingContent() },
  itemContent: @Composable (index: Int, T, EventPosition) -> Unit,
) {
  TriggerLoadMore(
    listState = listState,
    loadMoreThreshold = loadMoreThreshold,
    isLoading = isLoading,
    hasMoreItems = hasMoreItems,
    onLoadMore = onLoadMore,
  )

  val providedStyle = remember(style) { style.alignment(VERTICAL) }
  CompositionLocalProvider(LocalJetLimeStyle provides providedStyle) {
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
        key = key,
      ) { index, item ->
        val eventPosition = EventPosition.dynamicPaginated(
          index = index,
          listSize = itemsList.items.size,
          isLastPage = !hasMoreItems,
        )
        itemContent(index, item, eventPosition)
      }
      val footer = loadingContent
      if (isLoading && footer != null) {
        item(key = LoadMoreItemKey) { footer() }
      }
    }
  }
}

/**
 * A composable function that creates a horizontal timeline with built-in pagination (infinite scroll).
 *
 * It behaves exactly like [JetLimeRow] but additionally invokes [onLoadMore] when the user scrolls
 * within [loadMoreThreshold] items of the end, as long as [isLoading] is `false` and [hasMoreItems]
 * is `true`. While a page is being fetched ([isLoading] is `true`), [loadingContent] is rendered as a
 * trailing item. The page state ([itemsList], [isLoading], [hasMoreItems]) is owned by the caller.
 *
 * The timeline's connecting line stays continuous across page boundaries: while [hasMoreItems] is
 * `true` the last loaded item keeps drawing its connector toward the next page instead of terminating.
 *
 * @param T The type of items in the items list.
 * @param itemsList A list of items currently loaded in the timeline.
 * @param onLoadMore Callback invoked when the next page should be loaded.
 * @param modifier A modifier to be applied to the LazyRow.
 * @param style The JetLime style configuration. Defaults to a predefined row style.
 * @param listState The state object to be used for the LazyRow.
 * @param contentPadding The padding to apply to the content inside the LazyRow.
 * @param key A factory of stable and unique keys representing the item. If null is passed the position in the list will represent the key.
 * @param isLoading Whether a page fetch is currently in flight. Suppresses further [onLoadMore] calls and shows [loadingContent].
 * @param hasMoreItems Whether more pages can be loaded. When `false`, [onLoadMore] is not called and the timeline line terminates at the last item.
 * @param loadMoreThreshold The number of items from the end at which [onLoadMore] is triggered.
 * @param loadingContent The trailing composable shown while [isLoading] is `true`. Defaults to a centered circular progress indicator; pass `null` to show no trailing loader at all.
 * @param itemContent A composable lambda that takes an index, an item of type [T], and an [EventPosition] to build each item's content.
 */
@Composable
fun <T> JetLimePaginatedRow(
  itemsList: ItemsList<T>,
  onLoadMore: () -> Unit,
  modifier: Modifier = Modifier,
  style: JetLimeStyle = JetLimeDefaults.rowStyle(),
  listState: LazyListState = rememberLazyListState(),
  contentPadding: PaddingValues = PaddingValues(0.dp),
  key: ((index: Int, item: T) -> Any)? = null,
  isLoading: Boolean = false,
  hasMoreItems: Boolean = true,
  loadMoreThreshold: Int = JetLimeDefaults.LoadMoreThreshold,
  loadingContent: (@Composable () -> Unit)? = { DefaultRowLoadingContent() },
  itemContent: @Composable (index: Int, T, EventPosition) -> Unit,
) {
  TriggerLoadMore(
    listState = listState,
    loadMoreThreshold = loadMoreThreshold,
    isLoading = isLoading,
    hasMoreItems = hasMoreItems,
    onLoadMore = onLoadMore,
  )

  val providedStyle = remember(style) { style.alignment(HORIZONTAL) }
  CompositionLocalProvider(LocalJetLimeStyle provides providedStyle) {
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
        key = key,
      ) { index, item ->
        val eventPosition = EventPosition.dynamicPaginated(
          index = index,
          listSize = itemsList.items.size,
          isLastPage = !hasMoreItems,
        )
        itemContent(index, item, eventPosition)
      }
      val footer = loadingContent
      if (isLoading && footer != null) {
        item(key = LoadMoreItemKey) { footer() }
      }
    }
  }
}

/** Stable key for the load-more footer/trailing item. */
private val LoadMoreItemKey = "jetlime-load-more"

/**
 * Observes [listState] and invokes [onLoadMore] once the user scrolls within [loadMoreThreshold]
 * items of the end, guarded by [isLoading] and [hasMoreItems].
 */
@Composable
private fun TriggerLoadMore(
  listState: LazyListState,
  loadMoreThreshold: Int,
  isLoading: Boolean,
  hasMoreItems: Boolean,
  onLoadMore: () -> Unit,
) {
  val currentOnLoadMore by rememberUpdatedState(onLoadMore)
  val shouldLoadMore by remember(listState, loadMoreThreshold) {
    derivedStateOf {
      val layoutInfo = listState.layoutInfo
      val totalItems = layoutInfo.totalItemsCount
      val lastVisibleIndex =
        layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: return@derivedStateOf false
      totalItems > 0 && lastVisibleIndex >= totalItems - 1 - loadMoreThreshold
    }
  }

  LaunchedEffect(shouldLoadMore, isLoading, hasMoreItems) {
    if (shouldLoadMore && !isLoading && hasMoreItems) {
      currentOnLoadMore()
    }
  }
}

/** Default centered progress indicator used as the footer of [JetLimePaginatedColumn]. */
@Composable
private fun DefaultColumnLoadingContent() {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .padding(16.dp),
    contentAlignment = Alignment.Center,
  ) {
    CircularProgressIndicator()
  }
}

/** Default centered progress indicator used as the trailing item of [JetLimePaginatedRow]. */
@Composable
private fun DefaultRowLoadingContent() {
  Box(
    modifier = Modifier
      .fillMaxHeight()
      .padding(16.dp),
    contentAlignment = Alignment.Center,
  ) {
    CircularProgressIndicator()
  }
}
