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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pushpal.jetlime.EventPointType
import com.pushpal.jetlime.EventPosition
import com.pushpal.jetlime.JetLimeColumn
import com.pushpal.jetlime.JetLimeEvent
import com.pushpal.jetlime.JetLimeEventDefaults
import com.pushpal.jetlime.JetLimeEventStyle
import com.pushpal.jetlime.JetLimeStyle
import com.pushpal.jetlime.ui.theme.JetLimeSampleSurface
import com.pushpal.jetlime.ui.theme.JetLimeTheme
import com.pushpal.jetlime.ui.timelines.updatestate.data.getFakeItems

@ExperimentalAnimationApi
@Composable
fun BasicTimeLineNew() {
  val listState = rememberLazyListState()
  val fakeItems = remember { getFakeItems() }

  JetLimeSampleSurface(
    color = JetLimeTheme.colors.uiBackground,
    modifier = Modifier.fillMaxSize()
  ) {
    JetLimeColumn(
      modifier = Modifier.padding(top = 16.dp),
      listState = listState,
      style = JetLimeStyle(
        backgroundColor = JetLimeTheme.colors.uiBackground
      )
    ) {

      fakeItems.forEachIndexed { index, item ->
        JetLimeEvent(
          modifier = Modifier
            .padding(horizontal = 16.dp),
          style = JetLimeEventStyle(
            position = EventPosition.dynamic(index, fakeItems.size),
            pointType = EventPointType.filled()
          )
        ) {
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
      }
    }
  }
}

@ExperimentalAnimationApi
@Preview("Preview Basic TimeLine New")
@Composable
fun PreviewBasicTimeLineNew() {
  BasicTimeLineNew()
}