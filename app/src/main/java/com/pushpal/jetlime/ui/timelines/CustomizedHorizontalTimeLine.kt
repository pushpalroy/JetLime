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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pushpal.jetlime.EventPointType
import com.pushpal.jetlime.HorizontalAlignment.BOTTOM
import com.pushpal.jetlime.ItemsList
import com.pushpal.jetlime.JetLimeDefaults
import com.pushpal.jetlime.JetLimeEvent
import com.pushpal.jetlime.JetLimeEventDefaults
import com.pushpal.jetlime.JetLimeRow
import com.pushpal.jetlime.sample.R
import com.pushpal.jetlime.ui.data.getPlanets
import com.pushpal.jetlime.ui.timelines.event.HorizontalEventContent

@ExperimentalAnimationApi
@Composable
fun CustomizedHorizontalTimeLine(modifier: Modifier = Modifier) {
  val items = remember { getPlanets() }

  Surface(
    modifier = modifier.fillMaxWidth(),
  ) {
    JetLimeRow(
      modifier = Modifier.padding(vertical = 32.dp, horizontal = 16.dp),
      itemsList = ItemsList(items),
      key = { _, item -> item.id },
      style = JetLimeDefaults.rowStyle(
        contentDistance = 16.dp,
        itemSpacing = 16.dp,
        lineThickness = 2.dp,
        lineBrush = JetLimeDefaults.lineSolidBrush(color = Color(0xFF2196F3)),
        lineHorizontalAlignment = BOTTOM,
      ),
    ) { index, item, position ->
      JetLimeEvent(
        style = JetLimeEventDefaults.eventStyle(
          position = position,
          pointColor = Color(0xFF2889D6),
          pointFillColor = Color(0xFFD5F2FF),
          pointRadius = 14.dp,
          pointAnimation = if (index == 2) JetLimeEventDefaults.pointAnimation() else null,
          pointType = when (index) {
            1 -> EventPointType.filled(0.8f)
            3 -> EventPointType.custom(
              icon = painterResource(id = R.drawable.icon_check),
            )

            else -> EventPointType.Default
          },
          pointStrokeWidth = when (index) {
            3 -> 0.dp
            else -> 2.dp
          },
          pointStrokeColor = MaterialTheme.colorScheme.onBackground,
        ),
      ) {
        HorizontalEventContent(item = item)
      }
    }
  }
}

@ExperimentalAnimationApi
@Preview("Preview CustomizedHorizontalTimeLine")
@Composable
private fun PreviewCustomizedHorizontalTimeLine() {
  CustomizedHorizontalTimeLine()
}
