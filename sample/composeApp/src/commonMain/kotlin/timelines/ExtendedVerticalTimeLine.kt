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
package timelines

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pushpal.jetlime.ItemsList
import com.pushpal.jetlime.JetLimeColumn
import com.pushpal.jetlime.JetLimeDefaults
import com.pushpal.jetlime.JetLimeEventDefaults
import com.pushpal.jetlime.JetLimeExtendedEvent
import data.Item
import data.activityNames
import data.placeNames
import org.jetbrains.compose.ui.tooling.preview.Preview
import timelines.event.ExtendedEventAdditionalContent
import timelines.event.ExtendedEventContent
import timelines.event.activityDescription
import timelines.event.activityInfo
import timelines.event.decidePointAnimation
import timelines.event.decidePointType
import timelines.event.placeDescription
import timelines.event.placeImages
import timelines.event.placeInfo

@OptIn(ExperimentalComposeApi::class)
@ExperimentalAnimationApi
@Composable
fun ExtendedVerticalTimeLine(
  modifier: Modifier = Modifier,
  showSnackbar: (message: String) -> Unit,
) {
  val items = remember { mutableStateListOf<Item>() }

  // Generate sample data to populate in the list
  GenerateDataEffect(items)

  Surface(
    modifier = modifier.fillMaxSize(),
  ) {
    JetLimeColumn(
      modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp),
      itemsList = ItemsList(items),
      key = { _, item -> item.id },
      style = JetLimeDefaults.columnStyle(contentDistance = 24.dp),
    ) { index, item, position ->
      JetLimeExtendedEvent(
        style = JetLimeEventDefaults.eventStyle(
          position = position,
          pointAnimation = index.decidePointAnimation(),
          pointRadius = 14.dp,
          pointColor = Color.White,
          pointStrokeColor = MaterialTheme.colorScheme.onPrimaryContainer,
          pointType = index.decidePointType(),
        ),
        additionalContentMaxWidth = 88.dp,
        additionalContent = {
          ExtendedEventAdditionalContent(
            modifier = Modifier
              .fillMaxWidth()
              .clickable {
                showSnackbar("Clicked on additional content: $index")
              },
            item = item,
          )
        },
      ) {
        ExtendedEventContent(
          modifier = Modifier
            .clickable {
              showSnackbar("Clicked on content: $index")
            },
          item = item,
        )
      }
    }
  }
}

@Composable
private fun GenerateDataEffect(items: SnapshotStateList<Item>) {
  LaunchedEffect(Unit) {
    for (i in 0 until 15) {
      items.add(
        Item(
          id = i,
          name = placeNames[i % placeNames.size],
          info = placeInfo(i),
          description = placeDescription(i),
          images = placeImages(i),
          showActions = i == 2,
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
}

@ExperimentalAnimationApi
@Preview
@Composable
private fun PreviewExtendedVerticalTimeLine() {
  ExtendedVerticalTimeLine {}
}
