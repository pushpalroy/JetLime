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
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.unit.dp
import com.pushpal.jetlime.EventPointType
import com.pushpal.jetlime.ItemsList
import com.pushpal.jetlime.JetLimeColumn
import com.pushpal.jetlime.JetLimeDefaults
import com.pushpal.jetlime.JetLimeEvent
import com.pushpal.jetlime.JetLimeEventDefaults
import com.pushpal.jetlime.PointPlacement
import data.getCharacters
import org.jetbrains.compose.ui.tooling.preview.Preview
import timelines.event.VerticalEventContent

@ExperimentalAnimationApi
@Composable
fun BasicDashedTimeLine(modifier: Modifier = Modifier, showSnackbar: (message: String) -> Unit) {
  val items = remember { getCharacters().subList(0, 7) }

  Surface(
    modifier = modifier.fillMaxSize(),
  ) {
    JetLimeColumn(
      modifier = Modifier.padding(16.dp),
      itemsList = ItemsList(items),
      style = JetLimeDefaults.columnStyle(
        pathEffect = PathEffect.dashPathEffect(
          intervals = floatArrayOf(30f, 30f),
          phase = 0f,
        ),
      ),
      key = { _, item -> item.id },
    ) { index, item, position ->
      JetLimeEvent(
        style = JetLimeEventDefaults.eventStyle(
          position = position,
          pointPlacement = if (index > 1) PointPlacement.CENTER else PointPlacement.START,
          pointAnimation = if (index == 2) JetLimeEventDefaults.pointAnimation() else null,
          pointType = if (index == 1) EventPointType.filled(0.8f) else EventPointType.Default,
        ),
      ) {
        VerticalEventContent(
          modifier = Modifier.clickable {
            showSnackbar("Clicked on item: $index")
          },
          item = item,
        )
      }
    }
  }
}

@ExperimentalAnimationApi
@Preview
@Composable
private fun PreviewBasicDashedTimeLine() {
  BasicDashedTimeLine {}
}
