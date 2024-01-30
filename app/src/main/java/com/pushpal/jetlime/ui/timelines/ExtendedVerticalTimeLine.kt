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

import android.widget.Toast
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Card
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pushpal.jetlime.EventPointType
import com.pushpal.jetlime.ItemsList
import com.pushpal.jetlime.JetLimeColumn
import com.pushpal.jetlime.JetLimeDefaults
import com.pushpal.jetlime.JetLimeEventDefaults
import com.pushpal.jetlime.JetLimeExtendedEvent
import com.pushpal.jetlime.ui.data.getCharacters
import com.pushpal.jetlime.ui.timelines.event.VerticalEventContent

@OptIn(ExperimentalComposeApi::class)
@ExperimentalAnimationApi
@Composable
fun ExtendedVerticalTimeLine(modifier: Modifier = Modifier) {
  val items = remember { getCharacters() }
  val context = LocalContext.current

  Surface(
    modifier = modifier.fillMaxSize(),
  ) {
    JetLimeColumn(
      modifier = Modifier.padding(16.dp),
      itemsList = ItemsList(items),
      key = { _, item -> item.id },
      style = JetLimeDefaults.columnStyle(
        contentDistance = 24.dp,
      ),
    ) { index, item, position ->
      JetLimeExtendedEvent(
        style = JetLimeEventDefaults.eventStyle(
          position = position,
          pointAnimation = if (index == 2) JetLimeEventDefaults.pointAnimation() else null,
          pointType = if (index == 1) EventPointType.filled(0.8f) else EventPointType.Default,
        ),
        additionalContent = {
          Card(modifier = Modifier.wrapContentWidth()) {
            Text(
              modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
              text = item.id.toString(),
            )
          }
        },
      ) {
        VerticalEventContent(
          modifier = Modifier.clickable {
            Toast.makeText(context, "Clicked on item: $index", Toast.LENGTH_SHORT).show()
          },
          item = item,
        )
      }
    }
  }
}

@ExperimentalAnimationApi
@Preview("Preview ExtendedVerticalTimeLine")
@Composable
private fun PreviewExtendedVerticalTimeLine() {
  ExtendedVerticalTimeLine()
}
