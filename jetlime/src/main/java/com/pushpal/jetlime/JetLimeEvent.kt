package com.pushpal.jetlime

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.dp
import com.pushpal.jetlime.JetLimeStyle.Companion.HORIZONTAL
import com.pushpal.jetlime.JetLimeStyle.Companion.VERTICAL

@Composable
fun JetLimeEvent(
  modifier: Modifier = Modifier,
  style: JetLimeEventStyle = JetLimeEventStyle.Default,
  content: @Composable () -> Unit
) {
  val jetLimeStyle = LocalJetLimeStyle.current
  val infiniteTransition = rememberInfiniteTransition(label = "RadiusInfiniteTransition")
  val radiusAnimFactor by if (style.pointAnimation != null) {
    infiniteTransition.animateFloat(
      initialValue = style.pointAnimation.initialValue,
      targetValue = style.pointAnimation.targetValue,
      animationSpec = style.pointAnimation.animationSpec,
      label = "RadiusFloatAnimation"
    )
  } else {
    remember { mutableFloatStateOf(1.0f) }
  }

  when (jetLimeStyle.alignment) {
    VERTICAL -> VerticalEvent(modifier, style, jetLimeStyle, radiusAnimFactor, content)
    HORIZONTAL -> HorizontalEvent(modifier, style, jetLimeStyle, radiusAnimFactor, content)
  }
}

@Composable
internal fun VerticalEvent(
  modifier: Modifier,
  style: JetLimeEventStyle,
  jetLimeStyle: JetLimeStyle,
  radiusAnimFactor: Float,
  content: @Composable () -> Unit
) {
  Box(
    modifier = modifier
      .wrapContentSize()
      .drawBehind {

        val xOffset = style.pointRadius.toPx()
        val yOffset = xOffset * jetLimeStyle.pointStartFactor
        val radius = xOffset * radiusAnimFactor
        val strokeWidth = style.pointStrokeWidth.toPx()

        if (style.position.isNotEnd()) {
          val yShift = xOffset * (jetLimeStyle.pointStartFactor - 1)
          drawLine(
            brush = jetLimeStyle.lineBrush,
            start = Offset(
              x = xOffset,
              y = xOffset * 2 + yShift
            ),
            end = Offset(
              x = xOffset,
              y = this.size.height + yShift
            ),
            strokeWidth = jetLimeStyle.lineThickness.toPx()
          )
        }

        if (style.pointType.type == "Empty" || style.pointType.type == "Filled") {
          drawCircle(
            color = style.pointColor,
            radius = radius,
            center = Offset(x = xOffset, y = yOffset)
          )
        }

        if (style.pointType.type == "Filled") {
          drawCircle(
            color = style.pointFillColor,
            radius = radius - radius / 2,
            center = Offset(x = xOffset, y = yOffset)
          )
        }

        if (style.pointType.type == "Custom") {
          style.pointType.icon?.let { painter ->
            this.withTransform(
              transformBlock = {
                translate(
                  left = xOffset - painter.intrinsicSize.width / 2f,
                  top = yOffset - painter.intrinsicSize.height / 2f
                )
              },
              drawBlock = {
                this.drawIntoCanvas {
                  with(painter) {
                    draw(intrinsicSize)
                  }
                }
              })
          }
        }

        // Draw icon stroke
        if (strokeWidth > 0f) {
          drawCircle(
            color = style.pointStrokeColor,
            radius = radius - strokeWidth / 2,
            center = Offset(x = xOffset, y = yOffset),
            style = Stroke(width = strokeWidth)
          )
        }
      }
  ) {
    Box(
      modifier = Modifier
        .defaultMinSize(minHeight = style.pointRadius * 2)
        .padding(
          start = style.pointRadius * 2 + jetLimeStyle.gap,
          bottom = if (style.position.isNotEnd()) jetLimeStyle.itemSpacing else 0.dp
        )
    ) {
      content()
    }
  }
}

@Composable
internal fun HorizontalEvent(
  modifier: Modifier,
  style: JetLimeEventStyle,
  jetLimeStyle: JetLimeStyle,
  radiusAnimFactor: Float,
  content: @Composable () -> Unit
) {
  Box(
    modifier = modifier
      .wrapContentSize()
      .drawBehind {

        val yOffset = style.pointRadius.toPx()
        val xOffset = yOffset * jetLimeStyle.pointStartFactor
        val radius = yOffset * radiusAnimFactor
        val strokeWidth = style.pointStrokeWidth.toPx()

        if (style.position.isNotEnd()) {
          val xShift = yOffset * (jetLimeStyle.pointStartFactor - 1)
          drawLine(
            brush = jetLimeStyle.lineBrush,
            start = Offset(
              x = yOffset * 2 + xShift,
              y = yOffset
            ),
            end = Offset(
              x = this.size.width + xShift,
              y = yOffset
            ),
            strokeWidth = jetLimeStyle.lineThickness.toPx()
          )
        }

        if (style.pointType.type == "Empty" || style.pointType.type == "Filled") {
          drawCircle(
            color = style.pointColor,
            radius = radius,
            center = Offset(x = xOffset, y = yOffset)
          )
        }

        if (style.pointType.type == "Filled") {
          drawCircle(
            color = style.pointFillColor,
            radius = radius - radius / 2,
            center = Offset(x = xOffset, y = yOffset)
          )
        }

        if (style.pointType.type == "Custom") {
          style.pointType.icon?.let { painter ->
            this.withTransform(
              transformBlock = {
                translate(
                  left = xOffset - painter.intrinsicSize.width / 2f,
                  top = yOffset - painter.intrinsicSize.height / 2f
                )
              },
              drawBlock = {
                this.drawIntoCanvas {
                  with(painter) {
                    draw(intrinsicSize)
                  }
                }
              })
          }
        }

        // Draw icon stroke
        if (strokeWidth > 0f) {
          drawCircle(
            color = style.pointStrokeColor,
            radius = radius - strokeWidth / 2,
            center = Offset(x = xOffset, y = yOffset),
            style = Stroke(width = strokeWidth)
          )
        }
      }
  ) {
    Box(
      modifier = Modifier
        .defaultMinSize(minWidth = style.pointRadius * 2)
        .padding(
          top = style.pointRadius * 2 + jetLimeStyle.gap,
          end = if (style.position.isNotEnd()) jetLimeStyle.itemSpacing else 0.dp
        )
    ) {
      content()
    }
  }
}
