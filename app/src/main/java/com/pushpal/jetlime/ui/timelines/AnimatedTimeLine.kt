package com.pushpal.jetlime.ui.timelines

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.ExperimentalTransitionApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pushpal.jetlime.data.FakeData
import com.pushpal.jetlime.data.JetLimeItemsModel
import com.pushpal.jetlime.data.JetLimeItemsModel.JetLimeItem
import com.pushpal.jetlime.data.config.IconAnimation
import com.pushpal.jetlime.data.config.JetLimeItemConfig
import com.pushpal.jetlime.data.config.JetLimeViewConfig
import com.pushpal.jetlime.data.config.LineType
import com.pushpal.jetlime.ui.JetLimeView
import com.pushpal.jetlime.ui.theme.JetLimeSurface
import com.pushpal.jetlime.ui.theme.JetLimeTheme
import com.pushpal.jetlime.ui.util.multifab.FabIcon
import com.pushpal.jetlime.ui.util.multifab.FabOption
import com.pushpal.jetlime.ui.util.multifab.MultiFabItem
import com.pushpal.jetlime.ui.util.multifab.MultiFloatingActionButton
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class, ExperimentalTransitionApi::class)
@ExperimentalAnimationApi
@Composable
fun AnimatedTimeLine() {
  val jetLimeItemsModel = remember {
    JetLimeItemsModel(list = FakeData.animatedJetLimeItems)
  }

  val scaffoldState = rememberScaffoldState()
  val listState = rememberLazyListState()
  val jetTimeLineViewConfig = JetLimeViewConfig(
    backgroundColor = JetLimeTheme.colors.uiBackground,
    itemSpacing = 0.dp,
    lineType = LineType.Solid,
    enableItemAnimation = true,
    showIcons = true
  )

  Scaffold(
    scaffoldState = scaffoldState,
    floatingActionButton = {
      FAB(
        jetLimeItemsModel = jetLimeItemsModel,
        listState = listState
      )
    }
  ) {
    JetLimeSurface(
      color = JetLimeTheme.colors.uiBackground,
      modifier = Modifier
        .fillMaxSize()
    ) {
      JetLimeView(
        jetLimeItemsModel = jetLimeItemsModel,
        jetLimeViewConfig = jetTimeLineViewConfig,
        listState = listState,
        modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)
      )
    }
  }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun FAB(
  jetLimeItemsModel: JetLimeItemsModel,
  listState: LazyListState
) {
  val coroutineScope = rememberCoroutineScope()
  MultiFloatingActionButton(
    items = getFabItemsList(),
    fabIcon = FabIcon(iconRes = Icons.Filled.Add, iconRotate = 45f),
    onFabItemClicked = { item ->
      coroutineScope.launch {
        when (item.id) {
          1 -> jetLimeItemsModel.addItem(
            JetLimeItem(
              title = "New Item",
              description = "New item description",
              jetLimeItemConfig = JetLimeItemConfig(iconAnimation = IconAnimation())
            )
          )
          2 -> jetLimeItemsModel.removeItem(jetLimeItemsModel.items.lastOrNull())
        }
        listState.scrollToItem(jetLimeItemsModel.items.size)
      }
    },
    fabOption = FabOption(
      iconBackgroundTint = JetLimeTheme.colors.uiFloated,
      iconTint = JetLimeTheme.colors.uiBorder,
      textBackgroundTint = JetLimeTheme.colors.uiBackground,
      showLabel = true
    )
  )
}

private fun getFabItemsList(): List<MultiFabItem> {
  return listOf(
    MultiFabItem(
      id = 1,
      iconRes = Icons.Filled.Add,
      label = "Add"
    ),
    MultiFabItem(
      id = 2,
      iconRes = Icons.Filled.Clear,
      label = "Remove"
    )
  )
}

@ExperimentalAnimationApi
@Preview("Preview Animated TimeLine")
@Composable
fun PreviewAnimatedTimeLine() {
  AnimatedTimeLine()
}