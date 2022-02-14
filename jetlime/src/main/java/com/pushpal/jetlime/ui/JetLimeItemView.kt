package com.pushpal.jetlime.ui

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import com.pushpal.jetlime.R
import com.pushpal.jetlime.data.config.IconType
import com.pushpal.jetlime.data.config.JetLimeItemConfig
import com.pushpal.jetlime.data.config.JetLimeViewConfig
import com.pushpal.jetlime.data.config.LineType

const val heightFactor = 3

/**
 * [JetLimeItemView] is exposed to be used as composable
 * @param title is the optional title of the item
 * @param description is the optional description of the item
 * @param content is any optional composable that can be populated in the item
 * @param itemConfig is the config for the item. See [JetLimeItemConfig]
 * @param viewConfig is the config for the entire view. See [JetLimeViewConfig]
 * @param totalItems is the total number of items
 */
@Composable
fun JetLimeItemView(
  title: String? = null,
  description: String? = null,
  itemConfig: JetLimeItemConfig,
  viewConfig: JetLimeViewConfig,
  totalItems: Int,
  content: @Composable () -> Unit = {}
) {
  BoxWithConstraints(
    modifier = Modifier
      .fillMaxWidth()
      .background(color = viewConfig.backgroundColor)
      .height(itemConfig.itemHeight)
  ) {

    ConstraintLayout(
      constraintSet = decoupledConstraints(
        lineStartMargin = viewConfig.lineStartMargin,
        lineEndMargin = viewConfig.lineEndMargin
      )
    ) {
      Canvas(
        modifier = Modifier
          .fillMaxHeight()
          .layoutId("line")
      ) {

        val startX = 0f
        val startY = if (itemConfig.isFirstItem()) {
          size.height / heightFactor
        } else 0f

        val endX = 0f
        val endY = if (itemConfig.isLastItem(totalItems)) {
          size.height / heightFactor
        } else size.height

        val pathEffect = when (viewConfig.lineType) {
          is LineType.Dashed -> PathEffect.dashPathEffect(
            viewConfig.lineType.intervals,
            viewConfig.lineType.phase
          )
          else -> null
        }
        drawLine(
          cap = StrokeCap.Round,
          start = Offset(x = startX, y = startY),
          end = Offset(x = endX, y = endY),
          color = viewConfig.lineColor,
          pathEffect = pathEffect,
          strokeWidth = viewConfig.lineThickness
        )
      }

      if (viewConfig.showIcons) {
        val iconImage = when (itemConfig.iconType) {
          IconType.Empty -> ImageVector.vectorResource(id = R.drawable.icon_empty)
          IconType.Checked -> ImageVector.vectorResource(id = R.drawable.icon_check)
          IconType.Filled -> ImageVector.vectorResource(id = R.drawable.icon_filled)
          is IconType.Custom -> (itemConfig.iconType as IconType.Custom).iconImage
        }

        var finalAlpha = 1f
        itemConfig.iconAnimation?.let { safeIconAnimation ->
          val infiniteTransition = rememberInfiniteTransition()
          val alpha by infiniteTransition.animateFloat(
            initialValue = safeIconAnimation.initialValue,
            targetValue = safeIconAnimation.targetValue,
            animationSpec = infiniteRepeatable(
              animation = safeIconAnimation.keySpecs,
              repeatMode = RepeatMode.Reverse
            )
          )
          finalAlpha = alpha
        }

        Image(
          imageVector = iconImage,
          contentDescription = "Indicator",
          contentScale = ContentScale.Crop,
          colorFilter = ColorFilter.tint(
            color = itemConfig.iconColor
          ),
          modifier = Modifier
            .size(viewConfig.iconSize)
            .scale(finalAlpha)
            .clip(viewConfig.iconShape)
            .border(
              width = viewConfig.iconBorderThickness,
              color = itemConfig.iconBorderColor,
              shape = viewConfig.iconShape
            )
            .background(
              color = itemConfig.iconBackgroundColor,
              shape = viewConfig.iconShape
            )
            .layoutId("indicator")
        )
      }

      title?.let { safeTitle ->
        Text(
          text = safeTitle,
          color = itemConfig.titleColor,
          style = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
          ),
          modifier = Modifier
            .paddingFromBaseline(bottom = 8.dp)
            .layoutId("title")
        )
      }

      description?.let { safeDescription ->
        Text(
          text = safeDescription,
          color = itemConfig.descriptionColor,
          style = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp
          ),
          modifier = Modifier
            .paddingFromBaseline(bottom = 16.dp)
            .layoutId("description")
        )
      }

      Box(modifier = Modifier.layoutId("content")) {
        content()
      }
    }
  }
}

private fun decoupledConstraints(
  lineStartMargin: Dp,
  lineEndMargin: Dp
): ConstraintSet {
  return ConstraintSet {
    val lineC = createRefFor("line")
    val indicatorC = createRefFor("indicator")
    val titleC = createRefFor("title")
    val descriptionC = createRefFor("description")
    val contentC = createRefFor("content")

    constrain(lineC) {
      top.linkTo(parent.top)
      bottom.linkTo(parent.bottom)
      start.linkTo(parent.start, margin = lineStartMargin)
    }
    constrain(indicatorC) {
      start.linkTo(lineC.start)
      top.linkTo(titleC.top, margin = 0.dp)
      end.linkTo(lineC.end)
    }
    constrain(titleC) {
      top.linkTo(
        anchor = parent.top,
        margin = 16.dp
      )
      start.linkTo(
        anchor = lineC.end,
        margin = lineEndMargin
      )
    }
    constrain(descriptionC) {
      top.linkTo(titleC.bottom)
      start.linkTo(titleC.start)
    }
    constrain(contentC) {
      top.linkTo(descriptionC.bottom)
      start.linkTo(
        anchor = lineC.end,
        margin = lineEndMargin
      )
    }
  }
}

@Preview("Preview JetLimeItemView")
@Composable
fun PreviewJetLimeItemView() {
  JetLimeItemView(
    title = "Cafe Coffee Day",
    description = "2/A South Green Lane",
    itemConfig = JetLimeItemConfig(position = 0, itemHeight = 150.dp),
    viewConfig = JetLimeViewConfig(lineStartMargin = 48.dp),
    totalItems = 3
  ) {
    Text(
      text = "Canada 287",
      style = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Thin,
        fontSize = 12.sp
      )
    )
  }
}