/*
* MIT License
*
* Copyright (c) 2024 Pushpal Roy
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in all
* copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
*
*/
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
import com.pushpal.jetlime.EventPointAnimation
import com.pushpal.jetlime.EventPointType
import com.pushpal.jetlime.EventPosition
import com.pushpal.jetlime.JetLimeDefaults
import com.pushpal.jetlime.JetLimeEventStyle
import com.pushpal.jetlime.JetLimeRow
import com.pushpal.jetlime.JetLimeStyle
import com.pushpal.jetlime.sample.R
import com.pushpal.jetlime.ui.data.getPlanets
import com.pushpal.jetlime.ui.theme.JetLimeSampleSurface
import com.pushpal.jetlime.ui.theme.JetLimeTheme
import com.pushpal.jetlime.ui.timelines.event.HorizontalEventContent

@ExperimentalAnimationApi
@Composable
fun SimpleHorizontalTimeLine(modifier: Modifier = Modifier) {
  val listState = rememberLazyListState()
  val items = remember { getPlanets() }

  JetLimeSampleSurface(
    color = JetLimeTheme.colors.uiBackground,
    modifier = modifier.fillMaxWidth(),
  ) {
    JetLimeRow(
      modifier = Modifier.padding(top = 32.dp, start = 16.dp, end = 16.dp),
      listState = listState,
      style = JetLimeStyle(
        backgroundColor = JetLimeTheme.colors.uiBackground,
        lineBrush = JetLimeDefaults.lineGradientBrush(),
      ),
    ) {
      JetLimeEvent(
        style = JetLimeEventStyle(
          position = EventPosition.START,
          pointType = EventPointType.EMPTY,
        ),
      ) {
        HorizontalEventContent(item = items[0])
      }

      JetLimeEvent(
        style = JetLimeEventStyle(
          pointType = EventPointType.filled(0.9f),
          pointAnimation = EventPointAnimation(),
        ),
      ) {
        HorizontalEventContent(item = items[1])
      }

      JetLimeEvent(
        style = JetLimeEventStyle(
          pointType = EventPointType.EMPTY,
        ),
      ) {
        HorizontalEventContent(item = items[2])
      }

      JetLimeEvent(
        style = JetLimeEventStyle(
          pointType = EventPointType.filled(0.1f),
        ),
      ) {
        HorizontalEventContent(item = items[3])
      }

      JetLimeEvent(
        style = JetLimeEventStyle(
          position = EventPosition.END,
          pointType =
          EventPointType.custom(
            icon = painterResource(id = R.drawable.icon_check),
          ),
        ),
      ) {
        HorizontalEventContent(item = items[4])
      }
    }
  }
}

@ExperimentalAnimationApi
@Preview("Preview SimpleHorizontalTimeLine")
@Composable
private fun PreviewSimpleHorizontalTimeLine() {
  SimpleVerticalTimeLine()
}
