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
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview
import timelines.BasicDashedTimeLine
import timelines.BasicHorizontalTimeLine
import timelines.BasicVerticalTimeLine
import timelines.CustomizedHorizontalTimeLine
import timelines.CustomizedVerticalTimeLine
import timelines.ExtendedVerticalTimeLine
import timelines.VerticalDynamicTimeLine

@Composable
fun HomeScreen(
  modifier: Modifier = Modifier,
  isDarkTheme: Boolean = false,
  onThemeChange: ((Boolean) -> Unit) = {},
) {
  Scaffold(
    modifier = modifier,
    topBar = {
      HomeAppBar(
        isDarkTheme = isDarkTheme,
        onThemeChange = onThemeChange,
      )
    },
  ) { paddingValues ->
    HomeContent(
      modifier = Modifier
        .padding(paddingValues)
        .fillMaxSize(),
    )
  }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun HomeContent(modifier: Modifier = Modifier) {
  val tabs = remember { listOf("Basic", "Dashed", "Dynamic", "Custom", "Extended") }
  var selectedIndex by remember { mutableIntStateOf(0) }
  val snackBarState = remember { SnackbarHostState() }
  val coroutineScope = rememberCoroutineScope()

  Column(modifier = modifier) {
    ScrollableTabRow(
      selectedTabIndex = selectedIndex,
      edgePadding = 16.dp,
    ) {
      tabs.forEachIndexed { index, title ->
        Tab(
          selected = index == selectedIndex,
          onClick = { selectedIndex = tabs.indexOf(title) },
          text = {
            Text(
              text = title,
            )
          },
        )
      }
    }

    Surface(
      modifier = Modifier.fillMaxSize(),
    ) {
      Scaffold(
        snackbarHost = { SnackbarHost(snackBarState) },
        contentWindowInsets = WindowInsets(0.dp),
      ) {
        when (selectedIndex) {
          0 -> {
            Column {
              BasicHorizontalTimeLine { coroutineScope.launch { snackBarState.showSnackbar(it) } }
              BasicVerticalTimeLine { coroutineScope.launch { snackBarState.showSnackbar(it) } }
            }
          }

          1 -> BasicDashedTimeLine { coroutineScope.launch { snackBarState.showSnackbar(it) } }
          2 -> VerticalDynamicTimeLine { coroutineScope.launch { snackBarState.showSnackbar(it) } }
          3 -> {
            Column {
              CustomizedHorizontalTimeLine()
              CustomizedVerticalTimeLine()
            }
          }

          4 -> ExtendedVerticalTimeLine { coroutineScope.launch { snackBarState.showSnackbar(it) } }
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeAppBar(
  isDarkTheme: Boolean,
  modifier: Modifier = Modifier,
  onThemeChange: ((Boolean) -> Unit)?,
) {
  TopAppBar(
    title = {
      Text(
        text = "JetLime Samples",
      )
    },
    actions = {
      Switch(
        checked = isDarkTheme,
        onCheckedChange = onThemeChange,
      )
    },
    colors = TopAppBarDefaults.topAppBarColors(),
    modifier = modifier,
  )
}

@Preview
@Composable
private fun PreviewHomeScreen() {
  HomeScreen()
}
