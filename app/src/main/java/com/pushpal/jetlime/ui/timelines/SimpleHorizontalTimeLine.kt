package com.pushpal.jetlime.ui.timelines

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pushpal.jetlime.EventPointType
import com.pushpal.jetlime.EventPosition
import com.pushpal.jetlime.JetLimeDefaults
import com.pushpal.jetlime.JetLimeEvent
import com.pushpal.jetlime.JetLimeEventStyle
import com.pushpal.jetlime.JetLimeRow
import com.pushpal.jetlime.JetLimeStyle
import com.pushpal.jetlime.sample.R
import com.pushpal.jetlime.ui.data.getFakeItems
import com.pushpal.jetlime.ui.theme.JetLimeSampleSurface
import com.pushpal.jetlime.ui.theme.JetLimeTheme
import com.pushpal.jetlime.ui.timelines.event.HorizontalEventContent

@ExperimentalAnimationApi
@Composable
fun SimpleHorizontalTimeLine() {
  val listState = rememberLazyListState()
  val fakeItems = remember { getFakeItems() }

  JetLimeSampleSurface(
    color = JetLimeTheme.colors.uiBackground,
    modifier = Modifier.fillMaxWidth()
  ) {
    JetLimeRow(
      modifier = Modifier.padding(top = 32.dp, start = 16.dp, end = 16.dp),
      listState = listState,
      style = JetLimeStyle(
        backgroundColor = JetLimeTheme.colors.uiBackground,
        lineBrush = JetLimeDefaults.lineGradientBrush()
      )
    ) {
      JetLimeEvent(
        style = JetLimeEventStyle(
          position = EventPosition.start(),
          pointType = EventPointType.empty()
        )
      ) {
        HorizontalEventContent(item = fakeItems[0])
      }

      JetLimeEvent(
        style = JetLimeEventStyle(
          pointType = EventPointType.filled(),
        )
      ) {
        HorizontalEventContent(item = fakeItems[1])
      }

      JetLimeEvent(
        style = JetLimeEventStyle(
          pointType = EventPointType.empty()
        )
      ) {
        HorizontalEventContent(item = fakeItems[2])
      }

      JetLimeEvent(
        style = JetLimeEventStyle(
          pointType = EventPointType.filled(),
        )
      ) {
        HorizontalEventContent(item = fakeItems[3])
      }

      JetLimeEvent(
        style = JetLimeEventStyle(
          position = EventPosition.end(),
          pointType = EventPointType.custom(icon = painterResource(id = R.drawable.icon_check))
        )
      ) {
        HorizontalEventContent(item = fakeItems[4])
      }
    }
  }
}

@ExperimentalAnimationApi
@Preview("Preview SimpleHorizontalTimeLine")
@Composable
fun PreviewSimpleHorizontalTimeLine() {
  SimpleVerticalTimeLine()
}