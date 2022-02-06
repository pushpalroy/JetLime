package com.pushpal.jetlime.data

import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Face
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.pushpal.jetlime.data.JetLimeItemsModel.JetLimeItem
import com.pushpal.jetlime.data.config.IconAnimation
import com.pushpal.jetlime.data.config.IconType
import com.pushpal.jetlime.data.config.IconType.Filled
import com.pushpal.jetlime.data.config.JetLimeItemConfig
import com.pushpal.jetlime.ui.theme.JetLimeTypography

object FakeData {
  val simpleJetLimeItems = mutableListOf(
    JetLimeItem(
      title = "Green Avenue",
      description = "12/A Green Avenue",
      jetLimeItemConfig = JetLimeItemConfig(
        itemHeight = 80.dp,
        iconType = Filled
      )
    ),
    JetLimeItem(
      title = "Draker's Lane",
      description = "13 Dorky Lane, South Avenue",
      jetLimeItemConfig = JetLimeItemConfig(
        itemHeight = 100.dp,
        iconType = IconType.Checked
      )
    ) {
      Text(
        text = "Canada 287",
        style = TextStyle(
          fontFamily = FontFamily.Default,
          fontWeight = FontWeight.Thin,
          fontSize = 12.sp
        )
      )
    },
    JetLimeItem(
      title = "Cafe Coffee Day",
      jetLimeItemConfig = JetLimeItemConfig(
        itemHeight = 60.dp,
        iconType = IconType.Empty
      )
    ),
    JetLimeItem(
      title = "Blue Lagoon",
      description = "14C Mandel Street",
      jetLimeItemConfig = JetLimeItemConfig(
        itemHeight = 170.dp,
        iconType = IconType.Custom(Icons.Rounded.AccountCircle)
      )
    ) {
      ImageList()
    },
    JetLimeItem(
      title = "Sunset Point",
      description = "3F Kiosk Street Oranga",
      jetLimeItemConfig = JetLimeItemConfig(
        itemHeight = 80.dp,
        iconType = IconType.Custom(Icons.Rounded.Face)
      )
    )
  )

  val animatedJetLimeItems = mutableListOf(
    JetLimeItem(
      title = "Green Avenue",
      description = "12/A Green Avenue",
      jetLimeItemConfig = JetLimeItemConfig(
        itemHeight = 80.dp,
        iconType = IconType.Filled,
        iconAnimation = IconAnimation()
      )
    ),
    JetLimeItem(
      title = "Draker's Lane",
      description = "13 Dorky Lane, South Avenue",
      jetLimeItemConfig = JetLimeItemConfig(
        itemHeight = 100.dp,
        iconType = IconType.Checked
      )
    ) {
      Text(
        text = "Canada 287",
        style = JetLimeTypography.body1,
      )
    },
    JetLimeItem(
      title = "Cafe Coffee Day",
      jetLimeItemConfig = JetLimeItemConfig(
        itemHeight = 60.dp,
        iconType = IconType.Empty
      )
    ),
    JetLimeItem(
      title = "Blue Lagoon",
      description = "14C Mandel Street",
      jetLimeItemConfig = JetLimeItemConfig(
        itemHeight = 170.dp,
        iconType = IconType.Custom(Icons.Rounded.AccountCircle),
        iconAnimation = IconAnimation(
          initialValue = 0.5f,
          targetValue = 1.5f,
          keySpecs = keyframes {
            durationMillis = 500
            0.6f at 0
            0.7f at 100
            0.8f at 200
            0.9f at 300
            1f at 500
          }
        )
      )
    ) {
      ImageList()
    },
    JetLimeItem(
      title = "Sunset Point",
      description = "3F Kiosk Street Oranga",
      jetLimeItemConfig = JetLimeItemConfig(
        itemHeight = 80.dp,
        iconType = IconType.Custom(Icons.Rounded.Face)
      )
    )
  )

  @Composable
  fun ImageList() {
    val imageUrlsList = listOf(
      "https://images.unsplash.com/photo-1525183995014-bd94c0750cd5?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=200&q=200",
      "https://images.unsplash.com/photo-1498889444388-e67ea62c464b?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=500&q=500",
      "https://images.unsplash.com/photo-1501555088652-021faa106b9b?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=500&q=500",
      "https://images.unsplash.com/photo-1518737496070-5bab26f59b3f?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=500&q=500",
      "https://images.unsplash.com/photo-1518737496070-5bab26f59b3f?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=500&q=500"
    )
    LazyRow(
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      items(imageUrlsList) { imageUrl ->
        Image(
          painter = rememberImagePainter(data = imageUrl),
          contentDescription = "Image",
          modifier = Modifier
            .size(80.dp)
            .clip(RoundedCornerShape(5.dp)),
          contentScale = ContentScale.Crop
        )
      }
    }
  }
}