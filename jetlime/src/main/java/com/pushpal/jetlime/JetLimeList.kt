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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pushpal.jetlime.Arrangement.HORIZONTAL
import com.pushpal.jetlime.Arrangement.VERTICAL

/**
 * A composable function that creates a vertically scrolling list (column) with JetLime styling.
 * It uses the standard LazyColumn composable from Jetpack Compose but adds additional styling
 * and configuration options defined by JetLime.
 *
 * @param modifier A [Modifier] applied to the column.
 * @param style The [JetLimeStyle] to apply to the column.
 * @param listState The state object to be used to control or observe the list's scroll position.
 * @param contentPadding The padding to apply to the content inside the column.
 * @param content A composable lambda defining the content of the column.
 */
@Composable
fun JetLimeColumn(
  modifier: Modifier = Modifier,
  style: JetLimeStyle = JetLimeDefaults.columnStyle(),
  listState: LazyListState = rememberLazyListState(),
  contentPadding: PaddingValues = PaddingValues(0.dp),
  content: @Composable JetLimeListScope.() -> Unit,
) {
  CompositionLocalProvider(LocalJetLimeStyle provides style.alignment(VERTICAL)) {
    val items = remember { mutableStateListOf<@Composable (EventPosition) -> Unit>() }
    JetLimeListScope(items).content()
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
      items(items.size) { index ->
        items[index](EventPosition.dynamic(index, items.size))
      }
    }
  }
}

/**
 * A composable function that creates a horizontally scrolling list (row) with JetLime styling.
 * Similar to JetLimeColumn, it is a customized version of the standard LazyRow composable
 * from Jetpack Compose with additional styling and configuration options provided by JetLime.
 *
 * @param modifier A [Modifier] applied to the row.
 * @param style The [JetLimeStyle] to apply to the row.
 * @param listState The state object to be used to control or observe the list's scroll position.
 * @param contentPadding The padding to apply to the content inside the row.
 * @param content A composable lambda defining the content of the row.
 */
@Composable
fun JetLimeRow(
  modifier: Modifier = Modifier,
  style: JetLimeStyle = JetLimeDefaults.rowStyle(),
  listState: LazyListState = rememberLazyListState(),
  contentPadding: PaddingValues = PaddingValues(0.dp),
  content: @Composable JetLimeListScope.() -> Unit,
) {
  CompositionLocalProvider(LocalJetLimeStyle provides style.alignment(HORIZONTAL)) {
    val items = remember { mutableStateListOf<@Composable (EventPosition) -> Unit>() }
    JetLimeListScope(items).content()
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
      items(items.size) { index ->
        items[index](EventPosition.dynamic(index, items.size))
      }
    }
  }
}

/**
 * A CompositionLocal providing the current [JetLimeStyle]. This can be used to propagate the
 * JetLime styling down the composable tree and allow child composables to access and utilize
 * the current style.
 */
val LocalJetLimeStyle = compositionLocalOf<JetLimeStyle> { error("No JetLimeStyle provided") }
