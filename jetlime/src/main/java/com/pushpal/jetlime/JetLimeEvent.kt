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
import com.pushpal.jetlime.Arrangement.HORIZONTAL
import com.pushpal.jetlime.Arrangement.VERTICAL
import com.pushpal.jetlime.EventPointType.Companion.CUSTOM

class JetLimeListScope

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

  when (jetLimeStyle.arrangement) {
    VERTICAL -> VerticalEvent(
      modifier, style, jetLimeStyle, radiusAnimFactor, jetLimeStyle.lineVerticalAlignment, content
    )

    HORIZONTAL -> HorizontalEvent(
      modifier, style, jetLimeStyle, radiusAnimFactor, jetLimeStyle.lineHorizontalAlignment, content
    )
  }
}

@Composable
fun VerticalEvent(
  modifier: Modifier,
  style: JetLimeEventStyle,
  jetLimeStyle: JetLimeStyle,
  radiusAnimFactor: Float,
  verticalAlignment: VerticalAlignment = VerticalAlignment.LEFT,
  content: @Composable () -> Unit
) {
  Box(
    modifier = modifier
      .wrapContentSize()
      .drawBehind {

        val xOffset = when (verticalAlignment) {
          VerticalAlignment.LEFT -> style.pointRadius.toPx()
          VerticalAlignment.RIGHT -> this.size.width - style.pointRadius.toPx()
        }
        val yOffset = style.pointRadius.toPx() * jetLimeStyle.pointStartFactor
        val radius = style.pointRadius.toPx() * radiusAnimFactor
        val strokeWidth = style.pointStrokeWidth.toPx()

        if (style.position.isNotEnd()) {
          val yShift = yOffset * (jetLimeStyle.pointStartFactor - 1)
          drawLine(
            brush = jetLimeStyle.lineBrush,
            start = Offset(
              x = xOffset,
              y = yOffset * 2 + yShift
            ),
            end = Offset(
              x = xOffset,
              y = this.size.height + yShift
            ),
            strokeWidth = jetLimeStyle.lineThickness.toPx()
          )
        }

        if (style.pointType == EventPointType.EMPTY || style.pointType == EventPointType.FILLED) {
          drawCircle(
            color = style.pointColor,
            radius = radius,
            center = Offset(x = xOffset, y = yOffset)
          )
        }

        if (style.pointType == EventPointType.FILLED) {
          drawCircle(
            color = style.pointFillColor,
            radius = radius - radius / 2,
            center = Offset(x = xOffset, y = yOffset)
          )
        }

        if (style.pointType.type == CUSTOM) {
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
          start = if (verticalAlignment == VerticalAlignment.LEFT) style.pointRadius * 2 + jetLimeStyle.contentDistance else 0.dp,
          end = if (verticalAlignment == VerticalAlignment.RIGHT) style.pointRadius * 2 + jetLimeStyle.contentDistance else 0.dp,
          bottom = if (style.position.isNotEnd()) jetLimeStyle.itemSpacing else 0.dp
        )
    ) {
      content()
    }
  }
}

@Composable
fun HorizontalEvent(
  modifier: Modifier,
  style: JetLimeEventStyle,
  jetLimeStyle: JetLimeStyle,
  radiusAnimFactor: Float,
  horizontalAlignment: HorizontalAlignment = HorizontalAlignment.TOP,
  content: @Composable () -> Unit
) {
  Box(
    modifier = modifier
      .wrapContentSize()
      .drawBehind {

        val yOffset = when (horizontalAlignment) {
          HorizontalAlignment.TOP -> style.pointRadius.toPx()
          HorizontalAlignment.BOTTOM -> this.size.height - style.pointRadius.toPx()
        }
        val xOffset = style.pointRadius.toPx() * jetLimeStyle.pointStartFactor
        val radius = style.pointRadius.toPx() * radiusAnimFactor
        val strokeWidth = style.pointStrokeWidth.toPx()

        if (style.position.isNotEnd()) {
          val xShift = xOffset * (jetLimeStyle.pointStartFactor - 1)
          drawLine(
            brush = jetLimeStyle.lineBrush,
            start = Offset(
              x = xOffset * 2 + xShift,
              y = yOffset
            ),
            end = Offset(
              x = this.size.width + xShift,
              y = yOffset
            ),
            strokeWidth = jetLimeStyle.lineThickness.toPx()
          )
        }

        if (style.pointType == EventPointType.EMPTY || style.pointType == EventPointType.FILLED) {
          drawCircle(
            color = style.pointColor,
            radius = radius,
            center = Offset(x = xOffset, y = yOffset)
          )
        }

        if (style.pointType == EventPointType.FILLED) {
          drawCircle(
            color = style.pointFillColor,
            radius = radius - radius / 2,
            center = Offset(x = xOffset, y = yOffset)
          )
        }

        if (style.pointType.type == CUSTOM) {
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
          top = if (horizontalAlignment == HorizontalAlignment.TOP) style.pointRadius * 2 + jetLimeStyle.contentDistance else 0.dp,
          bottom = if (horizontalAlignment == HorizontalAlignment.BOTTOM) style.pointRadius * 2 + jetLimeStyle.contentDistance else 0.dp,
          end = if (style.position.isNotEnd()) jetLimeStyle.itemSpacing else 0.dp
        )
    ) {
      content()
    }
  }
}
