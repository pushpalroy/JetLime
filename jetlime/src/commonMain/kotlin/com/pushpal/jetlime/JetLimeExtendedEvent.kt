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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pushpal.jetlime.JetLimeEventDefaults.AdditionalContentMaxWidth

/**
 * Should only be used with a [JetLimeColumn] for a vertical arrangement of events.
 *
 * Composable function for creating a [JetLimeColumn] event which has 2 slots for content.
 * The main content will be drawn on the right side of the timeline and the additional content
 * will be drawn on the left side of the timeline. The additional content is optional, and has
 * a maximum width constraint defined by the [JetLimeEventDefaults.AdditionalContentMaxWidth].
 *
 * Example usage:
 *
 * ```
 *  val items = remember { getItemsList() }
 *
 *  JetLimeColumn(
 *   itemsList = ItemsList(items),
 *   key = { _, item -> item.id },
 *   style = JetLimeDefaults.columnStyle(),
 *  ) { index, item, position ->
 *     JetLimeExtendedEvent(
 *      style = JetLimeEventDefaults.eventStyle(position = position),
 *      additionalContent = { ComposableAdditionalContent(item.icon) }
 *     ) {
 *        ComposableMainContent(item = item.content)
 *       }
 *    }
 * ```
 *
 * @param modifier The modifier to be applied to the event.
 * @param style The style of the [JetLimeColumn] event, defaulting to [JetLimeEventDefaults.eventStyle].
 * @param additionalContent The optional additional content of the event, placed on the left side of timeline.
 * @param additionalContentMaxWidth The maximum width allowed for [additionalContent]
 * @param content The main content of the event, placed on the right side of timeline.
 */
@ExperimentalComposeApi
@Composable
fun JetLimeExtendedEvent(
  modifier: Modifier = Modifier,
  style: JetLimeEventStyle = JetLimeEventDefaults.eventStyle(EventPosition.END),
  additionalContent: @Composable (BoxScope.() -> Unit) = { },
  additionalContentMaxWidth: Dp = AdditionalContentMaxWidth,
  content: @Composable () -> Unit,
) {
  val jetLimeStyle = LocalJetLimeStyle.current
  val strokeWidth = with(LocalDensity.current) { style.pointStrokeWidth.toPx() }
  val radiusAnimFactor by calculateRadiusAnimFactor(style)

  // BoxWithConstraints provides its own constraints which we can use for layout
  BoxWithConstraints(modifier = modifier) {
    // Variable for keeping track of the X position where the timeline will be drawn
    var timelineXOffset by remember { mutableFloatStateOf(0f) }
    // Maximum width for additional content
    val maxAdditionalContentWidth = with(LocalDensity.current) { additionalContentMaxWidth.toPx() }

    Layout(
      content = {
        // Box for main content with optional padding at the bottom
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
        // Additional content passed as a composable lambda
        additionalContent()
      },
    ) { measurables, constraints ->
      // Ensuring that there is at least one child in the layout
      require(measurables.isNotEmpty()) {
        "JetLimeExtendedEvent should have at-least one child for content"
      }
      // Thickness of the line drawn for the timeline
      val timelineThickness = jetLimeStyle.lineThickness.toPx()
      // Distance between the content/additional content and the timeline
      val contentDistance = jetLimeStyle.contentDistance.toPx()

      // Extracting the first and potentially second child for layout
      val contentMeasurable = measurables.first()
      val additionalContentMeasurable = measurables.getOrNull(1)

      // Measuring the additional content if it exists
      val additionalContentPlaceable = additionalContentMeasurable?.let { measurable ->
        // Calculating intrinsic width and adjusting it according to the maximum allowed width
        val intrinsicWidth = measurable.minIntrinsicWidth(constraints.maxHeight)
        val adjustedMinWidth = intrinsicWidth.coerceAtMost(maxAdditionalContentWidth.toInt())
        val newConstraints = constraints.copy(
          minWidth = adjustedMinWidth,
          maxWidth = maxAdditionalContentWidth.toInt(),
        )
        // Measuring the additional content with the new constraints
        measurable.measure(newConstraints)
      }

      // Calculating the X offset for the timeline based on the width of the additional content
      timelineXOffset = (additionalContentPlaceable?.width?.toFloat() ?: 0f) + contentDistance

      // Calculating the X offset and width available for the main content
      val contentXOffset = timelineXOffset + timelineThickness + contentDistance
      val contentWidth = constraints.maxWidth - contentXOffset

      // Measuring the main content with the calculated width
      val contentPlaceable = contentMeasurable.measure(
        constraints.copy(minWidth = 0, maxWidth = contentWidth.toInt()),
      )

      // Determining the height of the layout based on the measured content
      val contentHeight = contentPlaceable.height
      val layoutHeight = additionalContentPlaceable?.let { additional ->
        maxOf(contentHeight, additional.height)
      } ?: contentHeight

      // Placing the measured composables in the layout
      layout(constraints.maxWidth, layoutHeight) {
        additionalContentPlaceable?.placeRelative(x = 0, y = 0)
        contentPlaceable.placeRelative(x = contentXOffset.toInt(), y = 0)
      }
    }

    // Drawing on canvas for additional graphical elements
    Canvas(modifier = Modifier.matchParentSize()) {
      val yOffset = style.pointRadius.toPx() * jetLimeStyle.pointStartFactor
      val radius = style.pointRadius.toPx() * radiusAnimFactor

      if (style.position.isNotEnd()) {
        drawLine(
          brush = jetLimeStyle.lineBrush,
          start = Offset(x = timelineXOffset, y = yOffset),
          end = Offset(x = timelineXOffset, y = this.size.height),
          strokeWidth = jetLimeStyle.lineThickness.toPx(),
          pathEffect = jetLimeStyle.pathEffect,
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
