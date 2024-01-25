package com.pushpal.jetlime.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pushpal.jetlime.ui.theme.JetLimeSampleSurface
import com.pushpal.jetlime.ui.theme.JetLimeTheme
import com.pushpal.jetlime.ui.timelines.SimpleTimeLines
import com.pushpal.jetlime.ui.timelines.SimpleVerticalLongTimeLine

@Composable
fun HomeScreen() {
  Scaffold(
    modifier = Modifier,
    topBar = { HomeAppBar(backgroundColor = JetLimeTheme.colors.uiBorder) }
  ) { paddingValues ->
    HomeContent(
      modifier = Modifier
        .padding(paddingValues)
        .fillMaxSize()
    )
  }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun HomeContent(
  modifier: Modifier = Modifier
) {
  val tabs = remember { listOf("Simple", "Vertical Scrollable") }
  var selectedIndex by remember { mutableIntStateOf(0) }
  Column(modifier = modifier) {
    ScrollableTabRow(
      containerColor = JetLimeTheme.colors.uiBorder,
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

    JetLimeSampleSurface(
      color = JetLimeTheme.colors.uiBackground,
      modifier = Modifier.fillMaxSize()
    ) {
      when (selectedIndex) {
        0 -> SimpleTimeLines()
        1 -> SimpleVerticalLongTimeLine()
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
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
    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
      containerColor = backgroundColor
    ),
    modifier = modifier
  )
}

@Preview("Preview HomeScreen")
@Composable
fun PreviewHomeScreen() {
  HomeScreen()
}