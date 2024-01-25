package com.pushpal.jetlime.ui.timelines.event

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pushpal.jetlime.ui.data.Item

@Composable
fun VerticalEventContent(item: Item) {
  Box(
    modifier = Modifier
      .wrapContentHeight()
      .fillMaxWidth()
  ) {
    Card(
      modifier = Modifier
        .fillMaxWidth(0.80f)
        .align(Alignment.CenterStart)
        .clickable {},
      colors = CardDefaults.cardColors(
        containerColor = Color(0xFF2D4869),
        contentColor = Color(0xFFFFFFFF),
      )
    ) {
      Text(
        modifier = Modifier
          .fillMaxWidth()
          .wrapContentHeight()
          .padding(horizontal = 12.dp, vertical = 8.dp),
        color = Color.White,
        fontSize = 18.sp,
        text = item.name
      )
      item.description?.let {
        Text(
          modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 12.dp, vertical = 8.dp),
          color = Color.White.copy(alpha = 0.8f),
          fontSize = 14.sp,
          text = it
        )
      }
    }
  }
}

@Composable
fun HorizontalEventContent(item: Item) {
  Box(
    modifier = Modifier
      .wrapContentHeight()
      .width(160.dp)
  ) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .height(80.dp)
        .align(Alignment.CenterStart)
        .clickable {},
      colors = CardDefaults.cardColors(
        containerColor = Color(0xFF2D4869),
        contentColor = Color(0xFFFFFFFF),
      )
    ) {
      Text(
        modifier = Modifier
          .fillMaxWidth()
          .wrapContentHeight()
          .padding(12.dp),
        color = Color.White,
        fontSize = 18.sp,
        text = item.name
      )
    }
  }
}