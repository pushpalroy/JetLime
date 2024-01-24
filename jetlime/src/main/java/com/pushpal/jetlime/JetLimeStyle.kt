package com.pushpal.jetlime

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

@Immutable
class JetLimeStyle(
  val backgroundColor: Color = JetLimeDefaults.BackgroundColor,
  val gap: Dp = JetLimeDefaults.Gap,
  val itemSpacing: Dp = JetLimeDefaults.ItemSpacing,
  val lineColor: Color = JetLimeDefaults.LineColor,
  val lineThickness: Dp = JetLimeDefaults.LineThickness,
  val lineBrush: Brush = JetLimeDefaults.lineSolidBrush(),
  val pointStartFactor: Float = 1.0f
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
    if (itemSpacing != other.itemSpacing) return false

    return true
  }

  override fun hashCode(): Int {
    var result = backgroundColor.hashCode()
    result = 31 * result + lineColor.hashCode()
    result = 31 * result + lineThickness.hashCode()
    result = 31 * result + itemSpacing.hashCode()
    return result
  }
}