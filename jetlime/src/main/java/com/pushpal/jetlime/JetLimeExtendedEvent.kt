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

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

@ExperimentalComposeApi
@Composable
fun JetLimeExtendedEvent(
  modifier: Modifier = Modifier,
  style: JetLimeEventStyle = JetLimeEventDefaults.eventStyle(EventPosition.END),
  additionalContent: @Composable (BoxScope.() -> Unit) = {},
  content: @Composable () -> Unit,
) {
  val jetLimeStyle = LocalJetLimeStyle.current
  val strokeWidth = with(LocalDensity.current) { style.pointStrokeWidth.toPx() }
  val radiusAnimFactor by calculateRadiusAnimFactor(style)

  BoxWithConstraints(modifier = modifier) {
    var timelineXOffset by remember { mutableFloatStateOf(0f) }
    val maxAdditionalContentWidth = with(LocalDensity.current) { AdditionalContentMaxWidth.toPx() }

    Layout(
      content = {
        Box(
          modifier = Modifier.padding(
            bottom = if (style.position.isNotEnd()) {
              jetLimeStyle.itemSpacing
            } else {
              0.dp
            },
          ),
        ) {
          content()
        }
        additionalContent()
      },
    ) { measurables, constraints ->
      require(measurables.isNotEmpty()) {
        "JetLimeEvent should have at-least one child for content"
      }

      val contentMeasurable = measurables.first()
      val additionalMeasurable = measurables.getOrNull(1)

      val additionalPlaceable = additionalMeasurable?.let { measurable ->
        val intrinsicWidth = measurable.minIntrinsicWidth(constraints.maxHeight)
        val adjustedMinWidth = intrinsicWidth.coerceAtMost(maxAdditionalContentWidth.toInt())
        val newConstraints = constraints.copy(
          minWidth = adjustedMinWidth,
          maxWidth = maxAdditionalContentWidth.toInt(),
        )
        measurable.measure(newConstraints)
      }
      val contentHeight = contentMeasurable.minIntrinsicHeight(constraints.maxWidth)
      timelineXOffset = (additionalPlaceable?.width?.toFloat() ?: 0f) +
        jetLimeStyle.contentDistance.toPx()

      val contentWidth = constraints.maxWidth -
        (timelineXOffset + jetLimeStyle.contentDistance.toPx())

      val contentPlaceable = contentMeasurable.let { measurable ->
        val intrinsicWidth = measurable.minIntrinsicWidth(constraints.maxHeight)
        val newConstraints = constraints.copy(
          minWidth = intrinsicWidth,
          maxWidth = contentWidth.toInt(),
          maxHeight = contentHeight,
        )
        measurable.measure(newConstraints)
      }

      val contentX = timelineXOffset + jetLimeStyle.lineThickness.toPx() +
        jetLimeStyle.contentDistance.toPx()

      layout(constraints.maxWidth, contentHeight) {
        additionalPlaceable?.placeRelative(x = 0, y = 0)
        contentPlaceable.placeRelative(x = contentX.toInt(), y = 0)
      }
    }

    Canvas(modifier = Modifier.matchParentSize()) {
      val yOffset = style.pointRadius.toPx() * jetLimeStyle.pointStartFactor
      val radius = style.pointRadius.toPx() * radiusAnimFactor

      if (style.position.isNotEnd()) {
        drawLine(
          brush = jetLimeStyle.lineBrush,
          start = Offset(x = timelineXOffset, y = yOffset),
          end = Offset(x = timelineXOffset, y = this.size.height),
          strokeWidth = jetLimeStyle.lineThickness.toPx(),
        )
      }

      if (style.pointType.isEmptyOrFilled()) {
        drawCircle(
          color = style.pointColor,
          radius = radius,
          center = Offset(x = timelineXOffset, y = yOffset),
        )
      }

      if (style.pointType.isFilled()) {
        drawCircle(
          color = style.pointFillColor,
          radius = radius - radius * (1 - (style.pointType.fillPercent ?: 1f)),
          center = Offset(x = timelineXOffset, y = yOffset),
        )
      }

      if (style.pointType.isCustom()) {
        style.pointType.icon?.let { painter ->
          this.withTransform(
            transformBlock = {
              translate(
                left = timelineXOffset - painter.intrinsicSize.width / 2f,
                top = yOffset - painter.intrinsicSize.height / 2f,
              )
            },
            drawBlock = {
              this.drawIntoCanvas {
                with(painter) {
                  draw(intrinsicSize)
                }
              }
            },
          )
        }
      }
      if (strokeWidth > 0f) {
        drawCircle(
          color = style.pointStrokeColor,
          radius = radius - strokeWidth / 2,
          center = Offset(x = timelineXOffset, y = yOffset),
          style = Stroke(width = strokeWidth),
        )
      }
    }
  }
}

private val AdditionalContentMaxWidth = 64.dp
