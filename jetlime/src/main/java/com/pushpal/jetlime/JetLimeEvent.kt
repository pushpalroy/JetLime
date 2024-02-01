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

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.pushpal.jetlime.Arrangement.HORIZONTAL
import com.pushpal.jetlime.Arrangement.VERTICAL

/**
 * Composable function for creating a [JetLimeColumn] or [JetLimeRow] event.
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
 *     JetLimeEvent(
 *      style = JetLimeEventDefaults.eventStyle(position = position)
 *     ) {
 *        ComposableContent(item = item)
 *       }
 *    }
 * ```
 *
 * @param modifier The modifier to be applied to the event.
 * @param style The style of the [JetLimeColumn] or [JetLimeRow] event, defaulting to [JetLimeEventDefaults.eventStyle].
 * @param content The composable content inside the event.
 */
@Composable
fun JetLimeEvent(
  modifier: Modifier = Modifier,
  style: JetLimeEventStyle = JetLimeEventDefaults.eventStyle(EventPosition.END),
  content: @Composable () -> Unit,
) {
  val jetLimeStyle = LocalJetLimeStyle.current

  when (jetLimeStyle.arrangement) {
    VERTICAL -> VerticalEvent(
      style,
      jetLimeStyle,
      modifier,
      content,
    )

    HORIZONTAL -> HorizontalEvent(
      style,
      jetLimeStyle,
      modifier,
      content,
    )
  }
}

/**
 * Composable function for creating a vertical layout for the JetLime event.
 * This composable is used internally for a [JetLimeColumn].
 *
 * @param style The style of the [JetLimeEvent].
 * @param jetLimeStyle The [JetLimeEvent] style configuration.
 * @param modifier The modifier to be applied to the event.
 * @param content The composable content inside the event.
 */
@Composable
internal fun VerticalEvent(
  style: JetLimeEventStyle,
  jetLimeStyle: JetLimeStyle,
  modifier: Modifier = Modifier,
  content: @Composable () -> Unit,
) {
  val verticalAlignment = remember { jetLimeStyle.lineVerticalAlignment }
  val radiusAnimFactor by calculateRadiusAnimFactor(style)
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
              y = yOffset,
            ),
            end = Offset(
              x = xOffset,
              y = this.size.height + yShift,
            ),
            strokeWidth = jetLimeStyle.lineThickness.toPx(),
          )
        }

        if (style.pointType.isEmptyOrFilled()) {
          drawCircle(
            color = style.pointColor,
            radius = radius,
            center = Offset(x = xOffset, y = yOffset),
          )
        }

        if (style.pointType.isFilled()) {
          drawCircle(
            color = style.pointFillColor,
            radius = radius - radius * (1 - (style.pointType.fillPercent ?: 1f)),
            center = Offset(x = xOffset, y = yOffset),
          )
        }

        if (style.pointType.isCustom()) {
          style.pointType.icon?.let { painter ->
            this.withTransform(
              transformBlock = {
                translate(
                  left = xOffset - painter.intrinsicSize.width / 2f,
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

        // Draw icon stroke
        if (strokeWidth > 0f) {
          drawCircle(
            color = style.pointStrokeColor,
            radius = radius - strokeWidth / 2,
            center = Offset(x = xOffset, y = yOffset),
            style = Stroke(width = strokeWidth),
          )
        }
      },
  ) {
    PlaceVerticalEventContent(style, jetLimeStyle, verticalAlignment, content)
  }
}

/**
 * Composable function to place the content within a [VerticalEvent].
 *
 * @param style The style of the [JetLimeEvent].
 * @param jetLimeStyle The JetLime style configuration.
 * @param alignment The vertical alignment for the event.
 * @param content The composable content to be placed.
 */
@Composable
private fun PlaceVerticalEventContent(
  style: JetLimeEventStyle,
  jetLimeStyle: JetLimeStyle,
  alignment: VerticalAlignment,
  content: @Composable () -> Unit,
) {
  Box(
    modifier = Modifier
      .testTag("VerticalEventContentBox")
      .defaultMinSize(minHeight = style.pointRadius * 2)
      .padding(
        start = if (alignment == VerticalAlignment.LEFT) {
          style.pointRadius * 2 + jetLimeStyle.contentDistance
        } else {
          0.dp
        },
        end = if (alignment == VerticalAlignment.RIGHT) {
          style.pointRadius * 2 + jetLimeStyle.contentDistance
        } else {
          0.dp
        },
        bottom = if (style.position.isNotEnd()) {
          jetLimeStyle.itemSpacing
        } else {
          0.dp
        },
      ),
  ) {
    content()
  }
}

/**
 * Composable function for creating a horizontal layout for the [JetLimeEvent].
 * This composable is used internally for a [JetLimeRow].
 *
 * @param style The style of the [JetLimeEvent].
 * @param jetLimeStyle The [JetLimeEvent] style configuration.
 * @param modifier The modifier to be applied to the event.
 * @param content The composable content inside the event.
 */
@Composable
internal fun HorizontalEvent(
  style: JetLimeEventStyle,
  jetLimeStyle: JetLimeStyle,
  modifier: Modifier = Modifier,
  content: @Composable () -> Unit,
) {
  val horizontalAlignment = remember { jetLimeStyle.lineHorizontalAlignment }
  val radiusAnimFactor by calculateRadiusAnimFactor(style)
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
              x = xOffset,
              y = yOffset,
            ),
            end = Offset(
              x = this.size.width + xShift,
              y = yOffset,
            ),
            strokeWidth = jetLimeStyle.lineThickness.toPx(),
          )
        }

        if (style.pointType.isEmptyOrFilled()) {
          drawCircle(
            color = style.pointColor,
            radius = radius,
            center = Offset(x = xOffset, y = yOffset),
          )
        }

        if (style.pointType.isFilled()) {
          drawCircle(
            color = style.pointFillColor,
            radius = radius - radius * (1 - (style.pointType.fillPercent ?: 1f)),
            center = Offset(x = xOffset, y = yOffset),
          )
        }

        if (style.pointType.isCustom()) {
          style.pointType.icon?.let { painter ->
            this.withTransform(
              transformBlock = {
                translate(
                  left = xOffset - painter.intrinsicSize.width / 2f,
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

        // Draw icon stroke
        if (strokeWidth > 0f) {
          drawCircle(
            color = style.pointStrokeColor,
            radius = radius - strokeWidth / 2,
            center = Offset(x = xOffset, y = yOffset),
            style = Stroke(width = strokeWidth),
          )
        }
      },
  ) {
    PlaceHorizontalEventContent(style, jetLimeStyle, horizontalAlignment, content)
  }
}

/**
 * Composable function to place the content within a [HorizontalEvent].
 *
 * @param style The style of the [JetLimeEvent].
 * @param jetLimeStyle The JetLime style configuration.
 * @param alignment The vertical alignment for the event.
 * @param content The composable content to be placed.
 */
@Composable
private fun PlaceHorizontalEventContent(
  style: JetLimeEventStyle,
  jetLimeStyle: JetLimeStyle,
  alignment: HorizontalAlignment,
  content: @Composable () -> Unit,
) {
  Box(
    modifier = Modifier
      .testTag("HorizontalEventContentBox")
      .defaultMinSize(minWidth = style.pointRadius * 2)
      .padding(
        top = if (alignment == HorizontalAlignment.TOP) {
          style.pointRadius * 2 + jetLimeStyle.contentDistance
        } else {
          0.dp
        },
        bottom = if (alignment == HorizontalAlignment.BOTTOM) {
          style.pointRadius * 2 + jetLimeStyle.contentDistance
        } else {
          0.dp
        },
        end = if (style.position.isNotEnd()) {
          jetLimeStyle.itemSpacing
        } else {
          0.dp
        },
      ),
  ) {
    content()
  }
}

/**
 * Calculates and returns the radius animation factor for a given [JetLimeEventStyle].
 * The result defaults to 1.0f if [JetLimeEventStyle.pointAnimation] is not set or null.
 *
 * @param style The style of the [JetLimeEvent].
 * @return The calculated radius animation factor as a [Float].
 */
@Composable
internal fun calculateRadiusAnimFactor(style: JetLimeEventStyle): State<Float> {
  val infiniteTransition = rememberInfiniteTransition(label = "RadiusInfiniteTransition")
  return if (style.pointAnimation != null) {
    infiniteTransition.animateFloat(
      initialValue = style.pointAnimation.initialValue,
      targetValue = style.pointAnimation.targetValue,
      animationSpec = style.pointAnimation.animationSpec,
      label = "RadiusFloatAnimation",
    )
  } else {
    remember { mutableFloatStateOf(1.0f) }
  }
}
