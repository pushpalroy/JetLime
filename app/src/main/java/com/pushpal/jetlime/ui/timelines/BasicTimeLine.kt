package com.pushpal.jetlime.ui.timelines

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pushpal.jetlime.data.JetLimeItem
import com.pushpal.jetlime.data.config.JetLimeViewConfig
import com.pushpal.jetlime.data.config.LineType
import com.pushpal.jetlime.data.initBasic
import com.pushpal.jetlime.ui.JetLimeView
import com.pushpal.jetlime.ui.theme.JetLimeSurface
import com.pushpal.jetlime.ui.theme.JetLimeTheme

@ExperimentalAnimationApi
@Composable
fun BasicTimeLine() {
  val jetLimeList = remember { mutableStateListOf<JetLimeItem>() }.apply { initBasic() }
  val listState = rememberLazyListState()
  val jetTimeLineViewConfig = JetLimeViewConfig(
    backgroundColor = JetLimeTheme.colors.uiBackground,
    itemSpacing = 0.dp,
    lineType = LineType.Solid,
    enableItemAnimation = false,
    showIcons = true
  )

  JetLimeSurface(
    color = JetLimeTheme.colors.uiBackground,
    modifier = Modifier
      .fillMaxSize()
  ) {
    JetLimeView(
      jetLimeItems = jetLimeList,
      jetLimeViewConfig = jetTimeLineViewConfig,
      listState = listState,
      modifier = Modifier.padding(16.dp)
    )
  }
}

@ExperimentalAnimationApi
@Preview("Preview Basic TimeLine")
@Composable
fun PreviewBasicTimeLine() {
  BasicTimeLine()
}