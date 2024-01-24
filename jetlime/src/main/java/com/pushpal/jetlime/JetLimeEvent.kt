package com.pushpal.jetlime

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.dp

@Composable
fun JetLimeEvent(
  modifier: Modifier = Modifier,
  style: JetLimeEventStyle = JetLimeEventStyle.Default,
  content: @Composable () -> Unit
) {

  val jetLimeStyle = LocalJetLimeStyle.current

  Box(
    modifier = modifier
      .wrapContentSize()
      .drawBehind {

        val iconRadiusInPx = style.pointRadius.toPx()
        val iconStrokeWidthInPx = style.pointStrokeWidth.toPx()

        if (style.pointType.type == "Empty" || style.pointType.type == "Filled") {
          drawCircle(
            color = style.pointColor,
            radius = iconRadiusInPx,
            center = Offset(x = iconRadiusInPx, y = iconRadiusInPx)
          )
        }

        if (style.pointType.type == "Filled") {
          drawCircle(
            color = style.pointFillColor,
            radius = iconRadiusInPx - iconRadiusInPx / 2,
            center = Offset(x = iconRadiusInPx, y = iconRadiusInPx)
          )
        }

        if (style.pointType.type == "Custom") {
          style.pointType.icon?.let { painter ->
            this.withTransform(
              transformBlock = {
                translate(
                  left = iconRadiusInPx - painter.intrinsicSize.width / 2f,
                  top = iconRadiusInPx - painter.intrinsicSize.height / 2f
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
        if (iconStrokeWidthInPx > 0f) {
          drawCircle(
            color = style.pointStrokeColor,
            radius = iconRadiusInPx - iconStrokeWidthInPx / 2,
            center = Offset(x = iconRadiusInPx, y = iconRadiusInPx),
            style = Stroke(width = iconStrokeWidthInPx)
          )
        }

        if (style.position.isNotEnd()) {
          drawLine(
            brush = jetLimeStyle.lineBrush,
            start = Offset(x = iconRadiusInPx, y = iconRadiusInPx * 2),
            end = Offset(x = iconRadiusInPx, y = this.size.height),
            strokeWidth = jetLimeStyle.lineThickness.toPx()
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