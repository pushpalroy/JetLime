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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pushpal.jetlime.ItemsList
import com.pushpal.jetlime.JetLimeColumn
import com.pushpal.jetlime.JetLimeDefaults
import com.pushpal.jetlime.JetLimeEvent
import com.pushpal.jetlime.JetLimeEventDefaults
import com.pushpal.jetlime.PointPlacement
import data.Item
import data.getCharacters
import org.jetbrains.compose.ui.tooling.preview.Preview
import timelines.event.VerticalEventContent

@ExperimentalAnimationApi
@Composable
fun VerticalDynamicTimeLine(
  modifier: Modifier = Modifier,
  showSnackbar: (message: String) -> Unit,
) {
  val listState = rememberLazyListState()
  val items = remember { mutableStateListOf<Item>() }
  val allCharacters = getCharacters().distinct()

  LaunchedEffect(Unit) {
    items.add(allCharacters.first())
  }

  Scaffold(
    modifier = modifier,
    contentWindowInsets = WindowInsets(0.dp),
    floatingActionButton = {
      Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        ExtendedFloatingActionButton(
          onClick = {
            if (items.size < allCharacters.size) {
              val newItem = allCharacters[items.size]
              if (!items.contains(newItem)) {
                items.add(newItem)
              }
            }
          },
          containerColor = MaterialTheme.colorScheme.secondaryContainer,
          contentColor = MaterialTheme.colorScheme.secondary,
        ) {
          Icon(Icons.Filled.Add, "Add item")
          Spacer(modifier = Modifier.width(4.dp))
          Text(text = "Add Item")
        }
        ExtendedFloatingActionButton(
          onClick = {
            if (items.isNotEmpty()) {
              items.removeAt(items.size - 1)
            }
          },
          containerColor = MaterialTheme.colorScheme.secondaryContainer,
          contentColor = MaterialTheme.colorScheme.secondary,
        ) {
          Icon(Icons.Filled.Delete, "Remove")
          Spacer(modifier = Modifier.width(4.dp))
          Text(text = "Remove Item")
        }
      }
    },
  ) { paddingValues ->
    Surface(
      modifier = Modifier
        .padding(paddingValues)
        .fillMaxSize(),
    ) {
      JetLimeColumn(
        modifier = Modifier.padding(32.dp),
        listState = listState,
        style = JetLimeDefaults.columnStyle(
          lineBrush = JetLimeDefaults.lineGradientBrush(),
        ),
        itemsList = ItemsList(items),
        key = { _, item -> item.id },
      ) { index, item, position ->
        JetLimeEvent(
          modifier = Modifier.clickable {
            showSnackbar("Clicked on item: $index")
          },
          style = JetLimeEventDefaults.eventStyle(
            position = position,
            pointPlacement = if (index == 3 ||
              index == 5
            ) {
              PointPlacement.CENTER
            } else {
              PointPlacement.START
            },
          ),
        ) {
          VerticalEventContent(item = item)
        }
      }
    }
  }
}

@ExperimentalAnimationApi
@Preview
@Composable
private fun PreviewSimpleVerticalDynamicTimeLine() {
  VerticalDynamicTimeLine {}
}
