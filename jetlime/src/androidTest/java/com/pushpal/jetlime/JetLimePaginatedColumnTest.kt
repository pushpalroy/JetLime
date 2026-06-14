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
package com.pushpal.jetlime

import android.annotation.SuppressLint
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollToNode
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import kotlinx.collections.immutable.persistentListOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@SuppressLint("ComposableNaming")
@RunWith(AndroidJUnit4::class)
class JetLimePaginatedColumnTest {

  @get:Rule
  val composeTestRule = createComposeRule()

  @Test
  fun jetLimePaginatedColumn_displaysItems() {
    val itemsList = ItemsList(persistentListOf("Item 1", "Item 2"))

    composeTestRule.setContent {
      JetLimePaginatedColumn(
        itemsList = itemsList,
        onLoadMore = {},
        itemContent = { _, item, _ ->
          JetLimeEvent {
            Text(text = item)
          }
        },
      )
    }

    composeTestRule.onNodeWithText("Item 1").assertIsDisplayed()
    composeTestRule.onNodeWithText("Item 2").assertIsDisplayed()
  }

  @Test
  fun jetLimePaginatedColumn_showsLoadingContentWhenLoading() {
    val itemsList = ItemsList(persistentListOf("Item 1", "Item 2"))

    composeTestRule.setContent {
      JetLimePaginatedColumn(
        itemsList = itemsList,
        onLoadMore = {},
        isLoading = true,
        loadingContent = {
          Text(text = "Loading", modifier = Modifier.testTag("LoadingFooter"))
        },
        itemContent = { _, item, _ ->
          JetLimeEvent {
            Text(text = item)
          }
        },
      )
    }

    composeTestRule.onNodeWithTag("LoadingFooter").assertIsDisplayed()
  }

  @Test
  fun jetLimePaginatedColumn_triggersLoadMoreOnScroll() {
    val items = mutableStateListOf<String>().apply {
      addAll((1..30).map { "Item $it" })
    }
    var loadMoreCount = 0

    composeTestRule.setContent {
      JetLimePaginatedColumn(
        modifier = Modifier.testTag("JetLimePaginatedColumn"),
        itemsList = ItemsList(items),
        key = { _, item -> item },
        onLoadMore = {
          loadMoreCount++
          val next = items.size
          items.addAll((next + 1..next + 10).map { "Item $it" })
        },
        itemContent = { _, item, _ ->
          JetLimeEvent {
            Text(text = item)
          }
        },
      )
    }

    composeTestRule.onNodeWithTag("JetLimePaginatedColumn")
      .performScrollToNode(hasText("Item 29"))
    composeTestRule.waitForIdle()

    assertThat(loadMoreCount).isAtLeast(1)
    // A subsequent page should have been appended and become reachable.
    composeTestRule.onNodeWithTag("JetLimePaginatedColumn")
      .performScrollToNode(hasText("Item 31"))
    composeTestRule.onNodeWithText("Item 31").assertIsDisplayed()
  }

  @Test
  fun jetLimePaginatedColumn_doesNotLoadMoreWhenNoMoreItems() {
    val items = mutableStateListOf<String>().apply {
      addAll((1..30).map { "Item $it" })
    }
    var loadMoreCount = 0

    composeTestRule.setContent {
      JetLimePaginatedColumn(
        modifier = Modifier.testTag("JetLimePaginatedColumn"),
        itemsList = ItemsList(items),
        key = { _, item -> item },
        hasMoreItems = false,
        onLoadMore = { loadMoreCount++ },
        itemContent = { _, item, _ ->
          JetLimeEvent {
            Text(text = item)
          }
        },
      )
    }

    composeTestRule.onNodeWithTag("JetLimePaginatedColumn")
      .performScrollToNode(hasText("Item 30"))
    composeTestRule.waitForIdle()

    assertThat(loadMoreCount).isEqualTo(0)
  }

  @Test
  fun jetLimePaginatedColumn_lastItemConnectorContinuesWhileMoreItems() {
    val itemsList = ItemsList(persistentListOf("Item 1", "Item 2", "Item 3"))
    val isNotEndByIndex = mutableMapOf<Int, Boolean>()

    composeTestRule.setContent {
      JetLimePaginatedColumn(
        itemsList = itemsList,
        onLoadMore = {},
        hasMoreItems = true,
        itemContent = { index, item, position ->
          isNotEndByIndex[index] = position.isNotEnd()
          JetLimeEvent {
            Text(text = item)
          }
        },
      )
    }

    composeTestRule.waitForIdle()
    // While more items can load, the last loaded item must keep drawing its connector.
    assertThat(isNotEndByIndex[2]).isTrue()
  }

  @Test
  fun jetLimePaginatedColumn_lastItemTerminatesOnLastPage() {
    val itemsList = ItemsList(persistentListOf("Item 1", "Item 2", "Item 3"))
    val isNotEndByIndex = mutableMapOf<Int, Boolean>()

    composeTestRule.setContent {
      JetLimePaginatedColumn(
        itemsList = itemsList,
        onLoadMore = {},
        hasMoreItems = false,
        itemContent = { index, item, position ->
          isNotEndByIndex[index] = position.isNotEnd()
          JetLimeEvent {
            Text(text = item)
          }
        },
      )
    }

    composeTestRule.waitForIdle()
    // On the last page, the last item is the timeline end and its connector terminates.
    assertThat(isNotEndByIndex[2]).isFalse()
  }
}
