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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
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
  val density = LocalDensity.current
  val strokeWidth = with(density) { style.pointStrokeWidth.toPx() }
  val radiusAnimFactor by calculateRadiusAnimFactor(style)
  val layoutDirection = LocalLayoutDirection.current
  val isRtl = layoutDirection == LayoutDirection.Rtl
  val lineBrush = remember(style, jetLimeStyle) { style.lineBrush ?: jetLimeStyle.lineBrush }

  BoxWithConstraints(modifier = modifier) {
    var logicalTimelineXOffset by remember { mutableFloatStateOf(0f) }
    val maxAdditionalContentWidth = with(density) { additionalContentMaxWidth.toPx() }

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
        // Ensure we do not exceed available width
        val maxWidthForAdditional =
          maxAdditionalContentWidth.coerceAtMost(constraints.maxWidth.toFloat()).toInt()
        val newConstraints = constraints.copy(
          minWidth = adjustedMinWidth.coerceAtMost(maxWidthForAdditional),
          maxWidth = maxWidthForAdditional,
        )
        // Measuring the additional content with the new constraints
        measurable.measure(newConstraints)
      }

      // Calculating the logical X offset for the timeline based on the width of the additional content
      logicalTimelineXOffset =
        (additionalContentPlaceable?.width?.toFloat() ?: 0f) + contentDistance

      // Calculating the X offset and width available for the main content in logical LTR space
      val logicalContentXOffset = logicalTimelineXOffset + timelineThickness + contentDistance
      val contentWidth = constraints.maxWidth - logicalContentXOffset

      // Measuring the main content with the calculated width
      val contentPlaceable = contentMeasurable.measure(
        constraints.copy(minWidth = 0, maxWidth = contentWidth.toInt()),
      )

      // Determining the height of the layout based on the measured content
      val contentHeight = contentPlaceable.height
      val layoutHeight = additionalContentPlaceable?.let { additional ->
        maxOf(contentHeight, additional.height)
      } ?: contentHeight

      val totalWidth = constraints.maxWidth

      // Placing the measured composables in the layout, mirroring in RTL so that
      // additional content is always on the logical "left" of the timeline and main
      // content on the "right", but visually flipped when isRtl is true.
      layout(totalWidth, layoutHeight) {
        if (isRtl) {
          // In RTL, place additional content flush to the right so it stays visible
          additionalContentPlaceable?.placeRelative(
            x = totalWidth - additionalContentPlaceable.width,
            y = 0,
          )
        } else {
          // LTR: original behavior, additional content starts from left
          additionalContentPlaceable?.placeRelative(x = 0, y = 0)
        }

        // Place main content on the opposite side of the timeline depending on direction
        val contentX = if (isRtl) {
          // In RTL, main content should be left of the timeline, but still within bounds
          (logicalTimelineXOffset - contentPlaceable.width - jetLimeStyle.contentDistance.toPx())
            .coerceAtLeast(0f)
            .toInt()
        } else {
          logicalContentXOffset.toInt()
        }
        contentPlaceable.placeRelative(x = contentX, y = 0)
      }
    }

    // Drawing on canvas for additional graphical elements
    Canvas(modifier = Modifier.matchParentSize()) {
      // Use the logical timeline offset directly in both LTR and RTL so that
      // the line stays aligned with the layout’s coordinate system. RTL
      // placement is handled by how content is positioned relative to this
      // logical offset, avoiding overlap with the main content.
      val timelineXOffset = logicalTimelineXOffset

      val yOffset = when (style.pointPlacement) {
        PointPlacement.START -> style.pointRadius.toPx() * jetLimeStyle.pointStartFactor
        PointPlacement.CENTER -> (
          this.size.height -
            if (style.position.isNotEnd()) jetLimeStyle.itemSpacing.toPx() else 0f
          ) /
          2f

        PointPlacement.END -> {
          val effectiveHeight =
            this.size.height -
              if (style.position.isNotEnd()) jetLimeStyle.itemSpacing.toPx() else 0f
          effectiveHeight - style.pointRadius.toPx() * jetLimeStyle.pointStartFactor
        }
      }
      val radius = style.pointRadius.toPx() * radiusAnimFactor

      if (style.pointPlacement == PointPlacement.START) {
        if (style.position.isNotEnd()) {
          drawLine(
            brush = lineBrush,
            start = Offset(x = timelineXOffset, y = yOffset),
            end = Offset(
              x = timelineXOffset,
              y = this.size.height + yOffset * (jetLimeStyle.pointStartFactor - 1),
            ),
            strokeWidth = jetLimeStyle.lineThickness.toPx(),
            pathEffect = jetLimeStyle.pathEffect,
          )
        }
      } else {
        // CENTER or END: draw upward (except first) and downward (except last) segments
        if (style.position.isNotStart()) {
          drawLine(
            brush = lineBrush,
            start = Offset(x = timelineXOffset, y = 0f),
            end = Offset(x = timelineXOffset, y = yOffset),
            strokeWidth = jetLimeStyle.lineThickness.toPx(),
            pathEffect = jetLimeStyle.pathEffect,
          )
        }
        if (style.position.isNotEnd()) {
          drawLine(
            brush = lineBrush,
            start = Offset(x = timelineXOffset, y = yOffset),
            end = Offset(x = timelineXOffset, y = this.size.height),
            strokeWidth = jetLimeStyle.lineThickness.toPx(),
            pathEffect = jetLimeStyle.pathEffect,
          )
        }
      }

      drawCircle(
        color = style.pointColor,
        radius = radius,
        center = Offset(x = timelineXOffset, y = yOffset),
      )

      if (style.pointType.isFilled()) {
        val fillPercent = style.pointType.fillPercent?.coerceIn(0f, 1f) ?: 1f
        drawCircle(
          color = style.pointFillColor,
          radius = radius * fillPercent,
          center = Offset(x = timelineXOffset, y = yOffset),
        )
      }
      if (style.pointType.isCustom()) {
        val pointSizeInPixels = style.pointRadius.toPx() * 2.4f * radiusAnimFactor
        val iconSize = Size(pointSizeInPixels, pointSizeInPixels)
        style.pointType.icon?.let { painter ->
          this.withTransform(
            transformBlock = {
              translate(
                left = timelineXOffset - iconSize.width / 2f,
                top = yOffset - iconSize.height / 2f,
              )
            },
            drawBlock = {
              this.drawIntoCanvas {
                with(painter) {
                  val tint = style.pointType.tint?.let { ColorFilter.tint(it) }
                  draw(size = iconSize, colorFilter = tint)
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
