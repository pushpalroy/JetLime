package com.pushpal.jetlime

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp

@Immutable
class JetLimeStyle(
  val backgroundColor: Color = JetLimeDefaults.BackgroundColor,
  val gap: Dp = JetLimeDefaults.Gap,
  val iconSize: Dp = JetLimeDefaults.IconSize,
  val iconShape: Shape = JetLimeDefaults.IconShape,
  val iconBorderThickness: Dp = JetLimeDefaults.IconBorderThickness,
  val itemSpacing: Dp = JetLimeDefaults.ItemSpacing,
  val lineColor: Color = JetLimeDefaults.LineColor,
  val lineThickness: Dp = JetLimeDefaults.LineThickness,
  val lineType: LineStyleType = JetLimeDefaults.LineStyle,
  val lineBrush: Brush = JetLimeDefaults.lineSolidBrush(),
  val enableItemAnimation: Boolean = false
) {

  companion object {
    @Stable
    val Default = JetLimeStyle()
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is JetLimeStyle) return false
    if (backgroundColor != other.backgroundColor) return false
    if (lineColor != other.lineColor) return false
    if (lineThickness != other.lineThickness) return false
    if (lineType != other.lineType) return false
    if (enableItemAnimation != other.enableItemAnimation) return false
    if (iconSize != other.iconSize) return false
    if (iconShape != other.iconShape) return false
    if (iconBorderThickness != other.iconBorderThickness) return false
    if (itemSpacing != other.itemSpacing) return false

    return true
  }

  override fun hashCode(): Int {
    var result = backgroundColor.hashCode()
    result = 31 * result + lineColor.hashCode()
    result = 31 * result + lineThickness.hashCode()
    result = 31 * result + lineType.hashCode()
    result = 31 * result + enableItemAnimation.hashCode()
    result = 31 * result + iconSize.hashCode()
    result = 31 * result + iconShape.hashCode()
    result = 31 * result + iconBorderThickness.hashCode()
    result = 31 * result + itemSpacing.hashCode()
    return result
  }
}