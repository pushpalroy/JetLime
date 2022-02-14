package com.pushpal.jetlime.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.ExperimentalTransitionApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import com.pushpal.jetlime.data.JetLimeItemsModel
import com.pushpal.jetlime.data.JetLimeItemsModel.JetLimeItem
import com.pushpal.jetlime.data.config.JetLimeViewConfig
import kotlinx.coroutines.flow.collect

/**
 * [JetLimeView] is exposed to be used as composable.
 * @param modifier is the Modifier for the JetLimeView.
 * @param jetLimeItemsModel is the model of list of items. See [JetLimeItem]
 * @param jetLimeViewConfig is the config for the view. See [jetLimeViewConfig]
 * @param listState is the state of the LazyColumn which will hold the JetLimeItems.
 */
@OptIn(
  ExperimentalAnimationApi::class,
  ExperimentalTransitionApi::class,
  ExperimentalFoundationApi::class
)
@ExperimentalAnimationApi
@Composable
fun JetLimeView(
  modifier: Modifier = Modifier,
  jetLimeItemsModel: JetLimeItemsModel,
  jetLimeViewConfig: JetLimeViewConfig,
  listState: LazyListState = rememberLazyListState()
) {
  LaunchedEffect(jetLimeItemsModel) {
    snapshotFlow {
      jetLimeItemsModel.items.firstOrNull { jetLimeItem ->
        jetLimeItem.visible.isIdle && jetLimeItem.visible.targetState.not()
      }
    }.collect { jetLimeItem ->
      if (jetLimeItem != null) {
        jetLimeItemsModel.pruneItems()
      }
    }
  }
  LazyColumn(
    modifier = modifier,
    state = listState,
    verticalArrangement = Arrangement.spacedBy(jetLimeViewConfig.itemSpacing)
  ) {
    items(count = jetLimeItemsModel.items.size) { pos ->
      jetLimeItemsModel.items[pos].let { jetLimeItem ->
        val slideInVertically = slideInVertically(
          initialOffsetY = { a -> -a / 4 },
          animationSpec = tween(
            durationMillis = 600,
            delayMillis = 100,
            easing = FastOutSlowInEasing
          )
        )
        val shrinkVertically = shrinkVertically(
          animationSpec = tween(
            durationMillis = 400,
            delayMillis = 100,
            easing = LinearOutSlowInEasing
          )
        )

        if (jetLimeViewConfig.enableItemAnimation) {
          AnimatedVisibility(
            visibleState = jetLimeItem.visible,
            enter = slideInVertically,
            exit = shrinkVertically
          ) {
            JetLimeItemView(
              title = jetLimeItem.title,
              description = jetLimeItem.description,
              content = jetLimeItem.content,
              itemConfig = jetLimeItem.jetLimeItemConfig.apply { position = pos },
              viewConfig = jetLimeViewConfig,
              totalItems = jetLimeItemsModel.items.size
            )
          }
        } else {
          JetLimeItemView(
            title = jetLimeItem.title,
            description = jetLimeItem.description,
            content = jetLimeItem.content,
            itemConfig = jetLimeItem.jetLimeItemConfig.apply { position = pos },
            viewConfig = jetLimeViewConfig,
            totalItems = jetLimeItemsModel.items.size
          )
        }
      }
    }
  }
}