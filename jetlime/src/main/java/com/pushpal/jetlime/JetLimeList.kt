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

@Composable
fun JetLimeColumn(
  modifier: Modifier = Modifier,
  style: JetLimeStyle = JetLimeStyle.Default,
  listState: LazyListState = rememberLazyListState(),
  contentPadding: PaddingValues = PaddingValues(0.dp),
  content: @Composable JetLimeListScope.() -> Unit
) {
  CompositionLocalProvider(LocalJetLimeStyle provides style.addAlignment(VERTICAL)) {
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

@Composable
fun JetLimeRow(
  modifier: Modifier = Modifier,
  style: JetLimeStyle = JetLimeStyle.Default,
  listState: LazyListState = rememberLazyListState(),
  contentPadding: PaddingValues = PaddingValues(0.dp),
  content: @Composable JetLimeListScope.() -> Unit
) {
  CompositionLocalProvider(LocalJetLimeStyle provides style.addAlignment(HORIZONTAL)) {
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

val LocalJetLimeStyle = compositionLocalOf<JetLimeStyle> { error("No JetLimeStyle provided") }
