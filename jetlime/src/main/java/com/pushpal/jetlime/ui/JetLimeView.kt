package com.pushpal.jetlime.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.pushpal.jetlime.data.JetLimeItem
import com.pushpal.jetlime.data.config.JetLimeViewConfig

/**
 * [JetLimeView] is exposed to be used as composable
 * @param modifier is the Modifier for the JetLimeView
 * @param jetLimeItems is the list of items. See [JetLimeItem]
 * @param jetLimeViewConfig is the config for the view. See [jetLimeViewConfig]
 * @param listState is the state of the LazyColumn which will hold the JetLimeItems
 */
@ExperimentalAnimationApi
@Composable
fun JetLimeView(
  modifier: Modifier = Modifier,
  jetLimeItems: List<JetLimeItem>,
  jetLimeViewConfig: JetLimeViewConfig,
  listState: LazyListState = rememberLazyListState()
) {
  LazyColumn(
    modifier = modifier,
    state = listState,
    verticalArrangement = Arrangement.spacedBy(jetLimeViewConfig.itemSpacing)
  ) {
    items(count = jetLimeItems.size) { pos ->
      jetLimeItems[pos].let { item ->
        val jetLimeItem = remember { mutableStateOf(item) }
        val itemVisibilityState = remember {
          MutableTransitionState(
            initialState = jetLimeViewConfig.enableItemAnimation.not()
          ).apply {
            // Start the animation immediately
            targetState = true
          }
        }
        val slideInVertically = remember {
          slideInVertically(
            initialOffsetY = { a -> -a / 3 },
            animationSpec = tween(
              durationMillis = 600,
              delayMillis = 100
            )
          )
        }
        val fadeOut = remember {
          fadeOut(
            animationSpec = tween(
              durationMillis = 1000
            )
          )
        }
        AnimatedVisibility(
          visibleState = itemVisibilityState,
          enter = slideInVertically,
          exit = fadeOut
        ) {

          JetLimeItemView(
            title = jetLimeItem.value.title,
            description = jetLimeItem.value.description,
            content = jetLimeItem.value.content,
            jetLimeItemConfig = jetLimeItem.value.jetLimeItemConfig.apply { position = pos },
            jetLimeViewConfig = jetLimeViewConfig,
            totalItems = jetLimeItems.size
          )
        }
      }
    }
  }
}