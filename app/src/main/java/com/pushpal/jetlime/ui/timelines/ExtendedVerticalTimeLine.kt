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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pushpal.jetlime.EventPointAnimation
import com.pushpal.jetlime.EventPointType
import com.pushpal.jetlime.ItemsList
import com.pushpal.jetlime.JetLimeColumn
import com.pushpal.jetlime.JetLimeDefaults
import com.pushpal.jetlime.JetLimeEventDefaults
import com.pushpal.jetlime.JetLimeExtendedEvent
import com.pushpal.jetlime.ui.data.Item
import com.pushpal.jetlime.ui.data.activityNames
import com.pushpal.jetlime.ui.data.placeNames
import com.pushpal.jetlime.ui.timelines.event.ExtendedEventAdditionalContent
import com.pushpal.jetlime.ui.timelines.event.ExtendedEventContent

@OptIn(ExperimentalComposeApi::class)
@ExperimentalAnimationApi
@Composable
fun ExtendedVerticalTimeLine(modifier: Modifier = Modifier) {
  val items = remember { mutableStateListOf<Item>() }
  val context = LocalContext.current

  // Generate sample data to populate in the list
  LaunchedEffect(Unit) {
    for (i in 0 until 15) {
      items.add(
        Item(
          id = i,
          name = placeNames[i % placeNames.size],
          info = placeInfo(i),
          description = placeDescription(i),
        ),
      )
      items.add(
        Item(
          id = i + 15,
          name = activityNames[i % activityNames.size],
          info = activityInfo(i),
          description = activityDescription(i),
        ),
      )
    }
  }

  Surface(
    modifier = modifier.fillMaxSize(),
  ) {
    JetLimeColumn(
      modifier = Modifier.padding(16.dp),
      itemsList = ItemsList(items),
      key = { _, item -> item.id },
      style = JetLimeDefaults.columnStyle(contentDistance = 24.dp),
    ) { index, item, position ->
      JetLimeExtendedEvent(
        style = JetLimeEventDefaults.eventStyle(
          position = position,
          pointAnimation = index.decidePointAnimation(),
          pointType = index.decidePointType(),
        ),
        additionalContent = {
          ExtendedEventAdditionalContent(
            modifier = Modifier
              .fillMaxWidth()
              .clickable {
                Toast
                  .makeText(context, "Clicked on additional content: $index", Toast.LENGTH_SHORT)
                  .show()
              },
            item = item,
          )
        },
      ) {
        ExtendedEventContent(
          modifier = Modifier
            .clickable {
              Toast.makeText(context, "Clicked on content: $index", Toast.LENGTH_SHORT).show()
            },
          item = item,
        )
      }
    }
  }
}

private fun placeInfo(i: Int) = "Address ${i + 1}, City, Country"

private fun placeDescription(i: Int) = "Visited at ${10 + i % 12}:${
  if (i % 2 == 0) {
    "00"
  } else {
    "30"
  }
} AM"

private fun activityInfo(i: Int) = "${1 + i / 2} mi . ${15 + i * 2} min"

private fun activityDescription(i: Int) = "${1 + i % 12}:${if (i % 2 == 0) "00" else "30"} PM - " +
  "${1 + (i + 1) % 12}:${if ((i + 1) % 2 == 0) "00" else "30"} PM"

fun Int.decidePointType(): EventPointType {
  return when (this) {
    1 -> EventPointType.filled(
      0.8f,
    )

    4 -> EventPointType.filled(0.2f)
    else -> EventPointType.Default
  }
}

@Composable
fun Int.decidePointAnimation(): EventPointAnimation? {
  return if (this == 2) JetLimeEventDefaults.pointAnimation() else null
}

@ExperimentalAnimationApi
@Preview("Preview ExtendedVerticalTimeLine")
@Composable
private fun PreviewExtendedVerticalTimeLine() {
  ExtendedVerticalTimeLine()
}
