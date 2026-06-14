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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pushpal.jetlime.ItemsList
import com.pushpal.jetlime.JetLimeDefaults
import com.pushpal.jetlime.JetLimeEvent
import com.pushpal.jetlime.JetLimeEventDefaults
import com.pushpal.jetlime.JetLimePaginatedColumn
import data.Item
import data.getCharacters
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview
import timelines.event.VerticalEventContent

private const val PAGE_SIZE = 6

@Composable
fun PaginatedVerticalTimeLine(
  modifier: Modifier = Modifier,
  showSnackbar: (message: String) -> Unit,
) {
  val listState = rememberLazyListState()
  val coroutineScope = rememberCoroutineScope()
  val allCharacters = remember { getCharacters().distinct() }

  val items = remember { mutableStateListOf<Item>() }
  var page by remember { mutableIntStateOf(0) }
  var isLoading by remember { mutableStateOf(false) }
  var hasMoreItems by remember { mutableStateOf(true) }

  val loadPage: () -> Unit = {
    coroutineScope.launch {
      isLoading = true
      // Simulate a network/database fetch.
      delay(800)
      val start = page * PAGE_SIZE
      val nextChunk = allCharacters.drop(start).take(PAGE_SIZE)
      items.addAll(nextChunk)
      page += 1
      hasMoreItems = items.size < allCharacters.size
      isLoading = false
    }
  }

  // The first page must be triggered explicitly; the scroll-based loader only fires once items exist.
  LaunchedEffect(Unit) {
    if (items.isEmpty()) loadPage()
  }

  Scaffold(
    modifier = modifier,
    contentWindowInsets = WindowInsets(0.dp),
  ) { paddingValues ->
    Surface(
      modifier = Modifier
        .padding(paddingValues)
        .fillMaxSize(),
    ) {
      JetLimePaginatedColumn(
        modifier = Modifier.padding(32.dp),
        itemsList = ItemsList(items),
        listState = listState,
        style = JetLimeDefaults.columnStyle(
          lineBrush = JetLimeDefaults.lineGradientBrush(),
        ),
        key = { _, item -> item.id },
        isLoading = isLoading,
        hasMoreItems = hasMoreItems,
        onLoadMore = loadPage,
      ) { index, item, position ->
        JetLimeEvent(
          modifier = Modifier.clickable {
            showSnackbar("Clicked on item: $index")
          },
          style = JetLimeEventDefaults.eventStyle(position = position),
        ) {
          VerticalEventContent(item = item)
        }
      }
    }
  }
}

@Preview
@Composable
private fun PreviewPaginatedVerticalTimeLine() {
  PaginatedVerticalTimeLine {}
}
