package com.pushpal.jetlime.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Tab
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.systemBarsPadding
import com.pushpal.jetlime.ui.theme.JetLimeTheme
import com.pushpal.jetlime.ui.timelines.AnimatedTimeLine
import com.pushpal.jetlime.ui.timelines.BasicTimeLine
import com.pushpal.jetlime.ui.timelines.ModifiedTimeLine
import com.pushpal.jetlime.ui.timelines.updatestate.ItemUpdateTimeLine

@Composable
fun HomeScreen() {
  Scaffold(
    modifier = Modifier.systemBarsPadding(),
    topBar = { HomeAppBar(backgroundColor = JetLimeTheme.colors.uiBorder) }
  ) {
    HomeContent(
      modifier = Modifier.fillMaxSize()
    )
  }
}

@OptIn(
  ExperimentalMaterialApi::class,
  ExperimentalAnimationApi::class
)
@Composable
fun HomeContent(
  modifier: Modifier = Modifier
) {
  val tabs = remember { listOf("Simple", "Animated", "Fancy", "Item Update") }
  var selectedIndex by remember { mutableStateOf(0) }
  Column(modifier = modifier) {
    ScrollableTabRow(
      backgroundColor = JetLimeTheme.colors.uiBorder,
      contentColor = JetLimeTheme.colors.accent,
      selectedTabIndex = selectedIndex,
      edgePadding = 16.dp
    ) {
      tabs.forEachIndexed { index, title ->
        Tab(
          selected = index == selectedIndex,
          selectedContentColor = JetLimeTheme.colors.uiBackground,
          onClick = { selectedIndex = tabs.indexOf(title) },
          text = {
            Text(
              text = title,
              color = JetLimeTheme.colors.buttonTextColor
            )
          }
        )
      }
    }

    when (selectedIndex) {
      0 -> BasicTimeLine()
      1 -> AnimatedTimeLine()
      2 -> ModifiedTimeLine()
      3 -> ItemUpdateTimeLine()
    }
  }
}

@Composable
fun HomeAppBar(
  backgroundColor: Color,
  modifier: Modifier = Modifier
) {
  TopAppBar(
    title = {
      Text(
        text = "JetLime Samples",
        color = JetLimeTheme.colors.textSecondaryDark
      )
    },
    backgroundColor = backgroundColor,
    modifier = modifier
  )
}

@Preview("Preview HomeScreen")
@Composable
fun PreviewHomeScreen() {
  HomeScreen()
}