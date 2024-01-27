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
package com.pushpal.jetlime.ui.timelines.event

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pushpal.jetlime.ui.data.Item

@Composable
fun VerticalEventContent(item: Item, modifier: Modifier = Modifier) {
  Box(
    modifier = modifier
      .wrapContentHeight()
      .fillMaxWidth(),
  ) {
    Card(
      modifier = Modifier
        .fillMaxWidth(0.9f)
        .align(Alignment.CenterStart)
        .clickable {},
    ) {
      Text(
        modifier = Modifier
          .fillMaxWidth()
          .wrapContentHeight()
          .padding(horizontal = 12.dp, vertical = 8.dp),
        color = Color.White,
        fontSize = 18.sp,
        text = item.name,
      )
      item.description?.let {
        Text(
          modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 12.dp, vertical = 8.dp),
          color = Color.White.copy(alpha = 0.8f),
          fontSize = 14.sp,
          text = it,
        )
      }
    }
  }
}

@Composable
fun HorizontalEventContent(item: Item, modifier: Modifier = Modifier) {
  Box(
    modifier = modifier
      .wrapContentHeight()
      .width(160.dp),
  ) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .height(120.dp)
        .align(Alignment.CenterStart)
        .clickable {},
    ) {
      Text(
        modifier = Modifier
          .fillMaxWidth()
          .wrapContentHeight()
          .padding(12.dp),
        color = Color.White,
        fontSize = 18.sp,
        text = item.name,
      )
      item.description?.let {
        Text(
          modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 12.dp, vertical = 8.dp),
          color = Color.White.copy(alpha = 0.8f),
          fontSize = 14.sp,
          text = it,
        )
      }
    }
  }
}
