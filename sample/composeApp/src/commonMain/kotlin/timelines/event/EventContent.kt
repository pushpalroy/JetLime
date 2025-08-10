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
package timelines.event

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pushpal.jetlime.EventPointAnimation
import com.pushpal.jetlime.EventPointType
import com.pushpal.jetlime.JetLimeEventDefaults
import data.Item
import data.extractFirstTime
import jetlime.sample.composeapp.generated.resources.Res
import jetlime.sample.composeapp.generated.resources.icon_check
import jetlime.sample.composeapp.generated.resources.image_1
import jetlime.sample.composeapp.generated.resources.image_2
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.painterResource

@Composable
fun VerticalEventContent(item: Item, modifier: Modifier = Modifier) {
  Card(
    modifier = modifier,
  ) {
    Text(
      modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()
        .padding(horizontal = 12.dp, vertical = 8.dp),
      fontSize = 16.sp,
      fontWeight = FontWeight.SemiBold,
      text = item.name,
    )
    item.description?.let {
      Text(
        modifier = Modifier
          .fillMaxWidth()
          .wrapContentHeight()
          .padding(horizontal = 12.dp, vertical = 8.dp),
        fontSize = 14.sp,
        text = it,
      )
    }
  }
}

@Composable
fun HorizontalEventContent(item: Item, modifier: Modifier = Modifier) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .height(120.dp)
      .width(160.dp),
  ) {
    Text(
      modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()
        .padding(12.dp),
      fontSize = 16.sp,
      fontWeight = FontWeight.SemiBold,
      text = item.name,
    )
    item.description?.let {
      Text(
        modifier = Modifier
          .fillMaxWidth()
          .wrapContentHeight()
          .padding(horizontal = 12.dp, vertical = 8.dp),
        fontSize = 14.sp,
        text = it,
      )
    }
  }
}

@Composable
fun ExtendedEventAdditionalContent(item: Item, modifier: Modifier = Modifier) {
  Card(modifier = modifier) {
    Text(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 8.dp, horizontal = 16.dp),
      fontSize = 12.sp,
      text = item.description?.extractFirstTime() ?: "",
      textAlign = TextAlign.Center,
    )
  }
}

@Composable
fun ExtendedEventContent(item: Item, modifier: Modifier = Modifier) {
  Surface(
    modifier = modifier
      .wrapContentHeight(),
  ) {
    Column(
      modifier = Modifier.padding(bottom = 8.dp),
    ) {
      Text(
        modifier = Modifier
          .fillMaxWidth()
          .wrapContentHeight()
          .padding(bottom = 4.dp),
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        text = item.name,
      )
      Text(
        modifier = Modifier
          .fillMaxWidth()
          .wrapContentHeight()
          .padding(bottom = 2.dp),
        color = MaterialTheme.colorScheme.secondary,
        fontSize = 12.sp,
        text = item.info,
      )
      item.description?.let {
        Text(
          modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
          color = MaterialTheme.colorScheme.secondary,
          fontSize = 12.sp,
          text = it,
        )
      }
      if (item.images.isNotEmpty()) {
        Column {
          Row {
            item.images.forEach {
              Image(
                modifier = Modifier
                  .size(100.dp)
                  .padding(top = 12.dp, end = 8.dp)
                  .clip(
                    RoundedCornerShape(5),
                  ),
                contentScale = ContentScale.Crop,
                painter = painterResource(resource = it),
                contentDescription = null,
              )
            }
          }
          Spacer(modifier = Modifier.height(10.dp))
          Text(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.secondary,
            fontSize = 12.sp,
            text = "From Google Photos",
          )
          Spacer(modifier = Modifier.height(8.dp))
        }
      }

      if (item.showActions) {
        Row(modifier = Modifier.padding(top = 8.dp)) {
          Button(onClick = {}) {
            Icon(Icons.Filled.Check, "Add item")
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "Yes")
          }
          Spacer(modifier = Modifier.width(8.dp))
          Button(onClick = {}) {
            Icon(Icons.Filled.Edit, "Edit item")
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "Edit")
          }
        }
      }
    }
  }
}

@Composable
fun Int.decidePointAnimation(): EventPointAnimation? =
  if (this == 3) JetLimeEventDefaults.pointAnimation() else null

fun placeImages(i: Int) = if (i == 1) {
  persistentListOf(
    Res.drawable.image_1,
    Res.drawable.image_2,
  )
} else {
  persistentListOf()
}

fun placeInfo(i: Int) = "Address ${i + 1}, City, Country"

fun placeDescription(i: Int) = "Visited at ${10 + i % 12}:${
  if (i % 2 == 0) {
    "00"
  } else {
    "30"
  }
} AM"

fun activityInfo(i: Int) = "${1 + i / 2} mi . ${15 + i * 2} min"

fun activityDescription(i: Int) = "${1 + i % 12}:${if (i % 2 == 0) "00" else "30"} PM - " +
  "${1 + (i + 1) % 12}:${if ((i + 1) % 2 == 0) "00" else "30"} PM"

@Composable
fun Int.decidePointType(): EventPointType = when (this) {
  1 -> EventPointType.filled(
    0.8f,
  )

  2, 3 -> EventPointType.custom(
    icon = painterResource(Res.drawable.icon_check),
  )

  4 -> EventPointType.filled(
    0.4f,
  )

  5 -> EventPointType.Default
  else -> EventPointType.Default
}
