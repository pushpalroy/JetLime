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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertPositionInRootIsEqualTo
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.pushpal.jetlime.JetLimeDefaults.lineSolidBrush
import kotlinx.collections.immutable.persistentListOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@SuppressLint("ComposableNaming")
@RunWith(AndroidJUnit4::class)
class JetLimeRowTest {

  @get:Rule
  val composeTestRule = createComposeRule()

  @Test
  fun jetLimeRow_displaysItems() {
    // Prepare a list of items to display
    val itemsList = ItemsList(persistentListOf("Item 1", "Item 2"))

    composeTestRule.setContent {
      JetLimeRow(
        itemsList = itemsList,
        itemContent = { _, item, _ ->
          Text(text = item)
        },
      )
    }

    // Check if the items are displayed
    composeTestRule.onNodeWithText("Item 1").assertIsDisplayed()
    composeTestRule.onNodeWithText("Item 2").assertIsDisplayed()
  }

  @Test
  fun jetLimeRow_displaysJetLimeItems() {
    // Prepare a list of items to display
    val itemsList = ItemsList(persistentListOf("Item 1", "Item 2"))

    composeTestRule.setContent {
      JetLimeRow(
        itemsList = itemsList,
        itemContent = { _, item, _ ->
          JetLimeEvent {
            Text(text = item)
          }
        },
      )
    }

    // Check if the items are displayed
    composeTestRule.onNodeWithText("Item 1").assertIsDisplayed()
    composeTestRule.onNodeWithText("Item 2").assertIsDisplayed()
  }

  @Test
  fun jetLimeRow_appliesCustomJetLimeStyle() {
    val itemsList = ItemsList(listOf("Item 1", "Item 2"))
    val contentDistance = 32.dp
    val itemSpacing = 32.dp
    val lineThickness = 10.dp
    val lineBrushColor = Color.Blue
    val lineHorizontalAlign = HorizontalAlignment.BOTTOM

    composeTestRule.setContent {
      JetLimeRow(
        itemsList = itemsList,
        style = JetLimeDefaults.rowStyle(
          contentDistance = contentDistance,
          itemSpacing = itemSpacing,
          lineThickness = lineThickness,
          lineBrush = lineSolidBrush(color = lineBrushColor),
          lineHorizontalAlignment = lineHorizontalAlign,
        ),
        itemContent = { _, item, _ ->
          JetLimeEvent {
            Text(text = item)
          }

          // Check if the style is applied
          with(LocalJetLimeStyle.current) {
            assertThat(this.contentDistance).isEqualTo(contentDistance)
            assertThat(this.itemSpacing).isEqualTo(itemSpacing)
            assertThat(this.lineThickness).isEqualTo(lineThickness)
            assertThat(this.lineBrush).isEqualTo(SolidColor(lineBrushColor))
            assertThat(this.lineHorizontalAlignment).isEqualTo(lineHorizontalAlign)
          }
        },
      )
    }
  }

  @Test
  fun jetLimeRow_appliesCustomEvenStyle() {
    val itemsList = ItemsList(persistentListOf("Item 1"))
    val pointRadius = 12.dp
    val paddingApplied = 16.dp

    composeTestRule.setContent {
      JetLimeRow(
        modifier = Modifier.padding(paddingApplied),
        itemsList = itemsList,
        itemContent = { _, item, pos ->
          JetLimeEvent(
            style = JetLimeEventDefaults.eventStyle(
              position = pos,
              pointType = EventPointType.filled(0.7f),
              pointColor = Color.DarkGray,
              pointFillColor = Color.Cyan,
              pointRadius = pointRadius,
              pointAnimation = JetLimeEventDefaults.pointAnimation(),
              pointStrokeWidth = 8.dp,
              pointStrokeColor = Color.Magenta,
            ),
          ) {
            Text(text = item)
          }
        },
      )
    }

    // Check if the style is applied
    composeTestRule.onNodeWithTag("HorizontalEventContentBox")
      .assertPositionInRootIsEqualTo(paddingApplied, paddingApplied)
  }

  @Test
  fun jetLimeRow_eventHandlesClick() {
    val itemsList = ItemsList(persistentListOf("Item 1", "Item 2"))
    var clickFlag = 0

    composeTestRule.setContent {
      JetLimeRow(
        itemsList = itemsList,
        itemContent = { _, item, _ ->
          JetLimeEvent(
            modifier = Modifier.clickable {
              clickFlag++
            },
          ) {
            Text(text = item)
          }
        },
      )
    }

    assertThat(clickFlag).isEqualTo(0)

    // Perform click action
    composeTestRule.onNodeWithText("Item 1").performClick()

    // Assertions to verify the result of the click
    assertThat(clickFlag).isEqualTo(1)
  }

  @Test
  fun jetLimeRow_handlesScrolling() {
    val longItemList = ItemsList((1..100).map { "Item $it" })

    composeTestRule.setContent {
      JetLimeRow(
        modifier = Modifier.testTag("JetLimeRow"),
        itemsList = longItemList,
        key = { _, item -> item },
        itemContent = { _, item, _ ->
          JetLimeEvent {
            Text(text = item)
          }
        },
      )
    }

    // Perform scroll action
    composeTestRule.onNodeWithTag("JetLimeRow")
      .performScrollToNode(hasText("Item 80"))

    // Assertions to check if the scrolling occurred
    composeTestRule.onNodeWithText("Item 80")
      .assertIsDisplayed()
  }

  @Test
  fun jetLimeRow_handlesDynamicContent() {
    val items = mutableStateListOf("Initial Item")
    composeTestRule.setContent {
      JetLimeRow(
        itemsList = ItemsList(items),
        itemContent = { _, item, _ ->
          JetLimeEvent {
            Text(text = item)
          }
        },
      )
    }
    composeTestRule.onNodeWithText("Initial Item").assertIsDisplayed()

    items.add("Next Item")
    composeTestRule.onNodeWithText("Next Item").assertIsDisplayed()

    items.add("Another Item")
    composeTestRule.onNodeWithText("Another Item").assertIsDisplayed()
  }

  @Test
  fun jetLimeRow_horizontalEvent_ltr_contentIsVisible() {
    val itemsList = ItemsList(persistentListOf("Item 1", "Item 2", "Item 3"))

    composeTestRule.setContent {
      CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        JetLimeRow(
          itemsList = itemsList,
          itemContent = { _, item, pos ->
            JetLimeEvent(
              style = JetLimeEventDefaults.eventStyle(
                position = pos,
                pointPlacement = PointPlacement.CENTER,
              ),
            ) {
              Text(text = item, modifier = Modifier.testTag("RowItem_$item"))
            }
          },
        )
      }
    }

    composeTestRule.onNodeWithTag("RowItem_Item 1").assertIsDisplayed()
    composeTestRule.onNodeWithTag("RowItem_Item 3").assertIsDisplayed()
  }

  @Test
  fun jetLimeRow_horizontalEvent_rtl_contentIsVisible() {
    val itemsList = ItemsList(persistentListOf("Item 1", "Item 2", "Item 3"))

    composeTestRule.setContent {
      CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        JetLimeRow(
          itemsList = itemsList,
          itemContent = { _, item, pos ->
            JetLimeEvent(
              style = JetLimeEventDefaults.eventStyle(
                position = pos,
                pointPlacement = PointPlacement.CENTER,
              ),
            ) {
              Text(text = item, modifier = Modifier.testTag("RowItem_$item"))
            }
          },
        )
      }
    }

    composeTestRule.onNodeWithTag("RowItem_Item 1").assertIsDisplayed()
    composeTestRule.onNodeWithTag("RowItem_Item 3").assertIsDisplayed()
  }

  @OptIn(ExperimentalComposeApi::class)
  @Test
  fun jetLimeExtendedEvent_ltr_contentsAreVisible() {
    val itemsList = ItemsList(persistentListOf("Item 1"))

    composeTestRule.setContent {
      CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        JetLimeColumn(
          itemsList = itemsList,
          itemContent = { _, item, pos ->
            JetLimeExtendedEvent(
              style = JetLimeEventDefaults.eventStyle(position = pos),
              additionalContent = {
                Text(text = "Additional", modifier = Modifier.testTag("ExtendedAdditional_LTR"))
              },
            ) {
              Text(text = item, modifier = Modifier.testTag("ExtendedMain_LTR"))
            }
          },
        )
      }
    }

    composeTestRule.onNodeWithTag("ExtendedAdditional_LTR").assertIsDisplayed()
    composeTestRule.onNodeWithTag("ExtendedMain_LTR").assertIsDisplayed()
  }

  @OptIn(ExperimentalComposeApi::class)
  @Test
  fun jetLimeExtendedEvent_rtl_contentsAreVisible() {
    val itemsList = ItemsList(persistentListOf("Item 1"))

    composeTestRule.setContent {
      CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        JetLimeColumn(
          itemsList = itemsList,
          itemContent = { _, item, pos ->
            JetLimeExtendedEvent(
              style = JetLimeEventDefaults.eventStyle(position = pos),
              additionalContent = {
                Text(text = "Additional", modifier = Modifier.testTag("ExtendedAdditional_RTL"))
              },
            ) {
              Text(text = item, modifier = Modifier.testTag("ExtendedMain_RTL"))
            }
          },
        )
      }
    }

    composeTestRule.onNodeWithTag("ExtendedAdditional_RTL").assertIsDisplayed()
    composeTestRule.onNodeWithTag("ExtendedMain_RTL").assertIsDisplayed()
  }
}
