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
  style: JetLimeStyle = JetLimeStyle.Default,
  listState: LazyListState = rememberLazyListState(),
  contentPadding: PaddingValues = PaddingValues(0.dp),
  content: @Composable JetLimeListScope.() -> Unit
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
      item {
        JetLimeListScope().content()
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
  style: JetLimeStyle = JetLimeStyle.Default,
  listState: LazyListState = rememberLazyListState(),
  contentPadding: PaddingValues = PaddingValues(0.dp),
  content: @Composable JetLimeListScope.() -> Unit
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
      item {
        JetLimeListScope().content()
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
