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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pushpal.jetlime.EventPointType
import com.pushpal.jetlime.ItemsList
import com.pushpal.jetlime.JetLimeColumn
import com.pushpal.jetlime.JetLimeDefaults
import com.pushpal.jetlime.JetLimeEvent
import com.pushpal.jetlime.JetLimeEventDefaults
import com.pushpal.jetlime.PointPlacement
import com.pushpal.jetlime.VerticalAlignment.RIGHT
import data.getCharacters
import jetlime.sample.composeapp.generated.resources.Res
import jetlime.sample.composeapp.generated.resources.icon_change
import jetlime.sample.composeapp.generated.resources.icon_check
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import timelines.event.VerticalEventContent

@ExperimentalAnimationApi
@Composable
fun CustomizedVerticalTimeLine(modifier: Modifier = Modifier) {
  val items = remember { getCharacters().subList(0, 5) }

  Surface(
    modifier = modifier.fillMaxSize(),
  ) {
    JetLimeColumn(
      modifier = Modifier.padding(horizontal = 16.dp),
      itemsList = ItemsList(items),
      key = { _, item -> item.id },
      style = JetLimeDefaults.columnStyle(
        contentDistance = 24.dp,
        itemSpacing = 16.dp,
        lineThickness = 2.dp,
        lineBrush = JetLimeDefaults.lineSolidBrush(color = Color(0xFF2196F3)),
        lineVerticalAlignment = RIGHT,
      ),
    ) { index, item, position ->
      JetLimeEvent(
        style = JetLimeEventDefaults.eventStyle(
          position = position,
          pointFillColor = Color(0xFFCCEFFF),
          pointRadius = 12.dp,
          pointColor = when (index) {
            3, 4 -> Color.White
            else -> Color(0xFF2889D6)
          },
          pointPlacement = if (index > 3) PointPlacement.CENTER else PointPlacement.START,
          pointAnimation = when (index) {
            1, 4 -> JetLimeEventDefaults.pointAnimation()
            else -> null
          },
          pointType = when (index) {
            1 -> EventPointType.filled(0.7f) // 70% fill
            3 -> EventPointType.custom(
              icon = painterResource(Res.drawable.icon_check),
              tint = Color(0xFF649228),
            )

            4 -> EventPointType.custom(
              icon = painterResource(Res.drawable.icon_change),
              tint = Color(0xFFFF5722),
            )

            else -> EventPointType.Default // Default style for rest
          },
          pointStrokeWidth = when (index) {
            2, 4 -> 0.dp
            3 -> 1.dp
            else -> 2.dp
          },
          pointStrokeColor = MaterialTheme.colorScheme.onBackground,
        ),
      ) {
        VerticalEventContent(item = item)
      }
    }
  }
}

@ExperimentalAnimationApi
@Preview
@Composable
private fun PreviewCustomizedVerticalTimeLine() {
  CustomizedVerticalTimeLine()
}
