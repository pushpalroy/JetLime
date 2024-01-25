package com.pushpal.jetlime.ui.timelines

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SimpleTimeLines() {
  Column {
    SimpleHorizontalTimeLine()
    SimpleVerticalTimeLine()
  }
}