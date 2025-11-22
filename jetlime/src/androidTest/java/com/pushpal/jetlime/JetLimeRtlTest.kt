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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.LayoutDirection
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.collections.immutable.persistentListOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@SuppressLint("ComposableNaming")
@RunWith(AndroidJUnit4::class)
class JetLimeRtlTest {

  @get:Rule
  val composeTestRule = createComposeRule()

  @Test
  fun jetLimeColumn_displaysItemsInRtl() {
    val itemsList = ItemsList(persistentListOf("Item 1", "Item 2"))

    composeTestRule.setContent {
      CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        JetLimeColumn(
          itemsList = itemsList,
          itemContent = { _, item, _ ->
            JetLimeEvent {
              Text(text = item)
            }
          },
        )
      }
    }

    // Check if the items are displayed in RTL mode
    composeTestRule.onNodeWithText("Item 1").assertIsDisplayed()
    composeTestRule.onNodeWithText("Item 2").assertIsDisplayed()
  }

  @Test
  fun jetLimeColumn_displaysItemsInRtlWithRightAlignment() {
    val itemsList = ItemsList(persistentListOf("Item 1", "Item 2"))

    composeTestRule.setContent {
      CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        JetLimeColumn(
          itemsList = itemsList,
          style = JetLimeDefaults.columnStyle(
            lineVerticalAlignment = VerticalAlignment.RIGHT,
          ),
          itemContent = { _, item, _ ->
            JetLimeEvent {
              Text(text = item)
            }
          },
        )
      }
    }

    // Check if the items are displayed in RTL mode with right alignment
    composeTestRule.onNodeWithText("Item 1").assertIsDisplayed()
    composeTestRule.onNodeWithText("Item 2").assertIsDisplayed()
  }

  @Test
  fun jetLimeRow_displaysItemsInRtl() {
    val itemsList = ItemsList(persistentListOf("Item 1", "Item 2"))

    composeTestRule.setContent {
      CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        JetLimeRow(
          itemsList = itemsList,
          itemContent = { _, item, _ ->
            JetLimeEvent {
              Text(text = item)
            }
          },
        )
      }
    }

    // Check if the items are displayed in RTL mode
    composeTestRule.onNodeWithText("Item 1").assertIsDisplayed()
    composeTestRule.onNodeWithText("Item 2").assertIsDisplayed()
  }
}
