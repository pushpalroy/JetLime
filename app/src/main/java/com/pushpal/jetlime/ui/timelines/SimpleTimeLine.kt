package com.pushpal.jetlime.ui.timelines

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pushpal.jetlime.EventPointAnimation
import com.pushpal.jetlime.EventPointType
import com.pushpal.jetlime.EventPosition
import com.pushpal.jetlime.JetLimeColumn
import com.pushpal.jetlime.JetLimeEvent
import com.pushpal.jetlime.JetLimeEventStyle
import com.pushpal.jetlime.JetLimeStyle
import com.pushpal.jetlime.sample.R
import com.pushpal.jetlime.ui.data.Item
import com.pushpal.jetlime.ui.data.getFakeItems
import com.pushpal.jetlime.ui.theme.JetLimeSampleSurface
import com.pushpal.jetlime.ui.theme.JetLimeTheme

@ExperimentalAnimationApi
@Composable
fun SimpleTimeLine() {
  val listState = rememberLazyListState()
  val fakeItems = remember { getFakeItems() }

  JetLimeSampleSurface(
    color = JetLimeTheme.colors.uiBackground,
    modifier = Modifier.fillMaxSize()
  ) {
    JetLimeColumn(
      modifier = Modifier.padding(top = 32.dp, start = 16.dp, end = 16.dp),
      listState = listState,
      style = JetLimeStyle(
        backgroundColor = JetLimeTheme.colors.uiBackground,
        gap = 16.dp,
        pointStartFactor = 1.2f
      )
    ) {
      JetLimeEvent(
        style = JetLimeEventStyle(
          position = EventPosition.start(),
          pointType = EventPointType.empty()
        )
      ) {
        Event(item = fakeItems[0])
      }

      JetLimeEvent(
        style = JetLimeEventStyle(
          pointType = EventPointType.filled(),
          pointAnimation = EventPointAnimation()
        )
      ) {
        Event(item = fakeItems[1])
      }

      JetLimeEvent(
        style = JetLimeEventStyle(
          pointType = EventPointType.empty()
        )
      ) {
        Event(item = fakeItems[2])
      }

      JetLimeEvent(
        style = JetLimeEventStyle(
          pointType = EventPointType.filled(),
        )
      ) {
        Event(item = fakeItems[3])
      }

      JetLimeEvent(
        style = JetLimeEventStyle(
          position = EventPosition.end(),
          pointType = EventPointType.custom(icon = painterResource(id = R.drawable.icon_check))
        )
      ) {
        Event(item = fakeItems[4])
      }
    }
  }
}

@Composable
fun Event(item: Item) {
  Box(
    modifier = Modifier
      .wrapContentHeight()
      .fillMaxWidth()
  ) {
    Card(
      modifier = Modifier
        .fillMaxWidth(0.80f)
        .align(Alignment.CenterStart)
        .clickable {},
      colors = CardDefaults.cardColors(
        containerColor = Color(0xFF2D4869),
        contentColor = Color(0xFFFFFFFF),
      )
    ) {
      Text(
        modifier = Modifier
          .fillMaxWidth()
          .wrapContentHeight()
          .padding(12.dp),
        color = Color.White,
        fontSize = 18.sp,
        text = item.name
      )
      item.description?.let {
        Text(
          modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(12.dp),
          color = Color.White.copy(alpha = 0.8f),
          fontSize = 14.sp,
          text = it
        )
      }
    }
  }
}

@ExperimentalAnimationApi
@Preview("Preview SimpleTimeLine")
@Composable
fun PreviewSimpleTimeLine() {
  SimpleTimeLine()
}