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
package com.pushpal.jetlime.sample

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.pushpal.jetlime.ItemsList
import com.pushpal.jetlime.JetLimeColumn
import com.pushpal.jetlime.JetLimeDefaults
import com.pushpal.jetlime.JetLimeEvent
import com.pushpal.jetlime.JetLimeEventDefaults
import com.pushpal.jetlime.JetLimeExtendedEvent
import com.pushpal.jetlime.JetLimeRow
import com.pushpal.jetlime.PointPlacement

@Preview(name = "JetLimeEvent Horizontal LTR", showBackground = true, widthDp = 360)
@Composable
private fun JetLimeEventHorizontalLtrPreview() {
  MaterialTheme {
    Surface {
      Row(modifier = Modifier.padding(16.dp)) {
        JetLimeRow(
          itemsList = ItemsList(listOf("One", "Two", "Three")),
          style = JetLimeDefaults.rowStyle(),
        ) { index, item, position ->
          JetLimeEvent(
            style = JetLimeEventDefaults.eventStyle(
              position = position,
              pointPlacement = when (index) {
                0 -> PointPlacement.START
                1 -> PointPlacement.CENTER
                else -> PointPlacement.END
              },
            ),
          ) {
            Text(text = item, modifier = Modifier.padding(8.dp))
          }
        }
      }
    }
  }
}

@Preview(name = "JetLimeEvent Horizontal RTL", showBackground = true, widthDp = 360)
@Composable
private fun JetLimeEventHorizontalRtlPreview() {
  MaterialTheme {
    Surface {
      CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Row(modifier = Modifier.padding(16.dp)) {
          JetLimeRow(
            itemsList = ItemsList(listOf("One", "Two", "Three")),
            style = JetLimeDefaults.rowStyle(),
          ) { index, item, position ->
            JetLimeEvent(
              style = JetLimeEventDefaults.eventStyle(
                position = position,
                pointPlacement = when (index) {
                  0 -> PointPlacement.START
                  1 -> PointPlacement.CENTER
                  else -> PointPlacement.END
                },
              ),
            ) {
              Text(text = item, modifier = Modifier.padding(8.dp))
            }
          }
        }
      }
    }
  }
}

@OptIn(ExperimentalComposeApi::class)
@Preview(name = "JetLimeExtendedEvent LTR", showBackground = true, widthDp = 360)
@Composable
private fun JetLimeExtendedEventLtrPreview() {
  MaterialTheme {
    Surface {
      Column(modifier = Modifier.padding(16.dp)) {
        JetLimeColumn(
          itemsList = ItemsList(listOf("A", "B", "C")),
          style = JetLimeDefaults.columnStyle(),
        ) { index, item, position ->
          JetLimeExtendedEvent(
            style = JetLimeEventDefaults.eventStyle(position = position),
            additionalContent = {
              Text(text = "Left $index", modifier = Modifier.padding(4.dp))
            },
          ) {
            Text(text = "Right $item", modifier = Modifier.padding(4.dp))
          }
        }
      }
    }
  }
}

@OptIn(ExperimentalComposeApi::class)
@Preview(name = "JetLimeExtendedEvent RTL", showBackground = true, widthDp = 360)
@Composable
private fun JetLimeExtendedEventRtlPreview() {
  MaterialTheme {
    Surface {
      CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(modifier = Modifier.padding(16.dp)) {
          JetLimeColumn(
            itemsList = ItemsList(listOf("A", "B", "C")),
            style = JetLimeDefaults.columnStyle(),
          ) { index, item, position ->
            JetLimeExtendedEvent(
              style = JetLimeEventDefaults.eventStyle(position = position),
              additionalContent = {
                Text(text = "Left $index", modifier = Modifier.padding(4.dp))
              },
            ) {
              Text(text = "Right $item", modifier = Modifier.padding(4.dp))
            }
          }
        }
      }
    }
  }
}
