package com.pushpal.jetlime.ui.timelines

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pushpal.jetlime.data.FakeData
import com.pushpal.jetlime.data.JetLimeItemsModel
import com.pushpal.jetlime.data.config.JetLimeViewConfig
import com.pushpal.jetlime.data.config.LineType
import com.pushpal.jetlime.ui.JetLimeView
import com.pushpal.jetlime.ui.theme.JetLimeShapes
import com.pushpal.jetlime.ui.theme.JetLimeSurface
import com.pushpal.jetlime.ui.theme.JetLimeTheme

@ExperimentalAnimationApi
@Composable
fun ModifiedTimeLine() {
  val jetLimeItemsModel = remember { JetLimeItemsModel(list = FakeData.simpleJetLimeItems) }
  val listState = rememberLazyListState()
  val jetTimeLineViewConfig = JetLimeViewConfig(
    backgroundColor = JetLimeTheme.colors.uiBorder,
    itemSpacing = 2.dp,
    lineType = LineType.Solid,
    lineColor = JetLimeTheme.colors.error,
    lineThickness = 16F,
    iconShape = JetLimeShapes.small,
    iconBorderThickness = 0.dp,
    showIcons = true
  )

  JetLimeSurface(
    color = JetLimeTheme.colors.uiBackground,
    modifier = Modifier
      .fillMaxSize()
  ) {
    JetLimeSurface(
      color = JetLimeTheme.colors.uiBackground,
      shape = JetLimeShapes.medium,
      modifier = Modifier
        .wrapContentSize()
        .padding(16.dp)
    ) {
      JetLimeView(
        jetLimeItemsModel = jetLimeItemsModel,
        jetLimeViewConfig = jetTimeLineViewConfig,
        listState = listState
      )
    }
  }
}

@ExperimentalAnimationApi
@Preview("Preview Modified TimeLine")
@Composable
fun PreviewModifiedTimeLine() {
  ModifiedTimeLine()
}