package com.pushpal.jetlime

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.pushpal.jetlime.Arrangement.VERTICAL
import com.pushpal.jetlime.HorizontalAlignment.TOP
import com.pushpal.jetlime.VerticalAlignment.LEFT

@Immutable
class JetLimeStyle(
  val backgroundColor: Color = JetLimeDefaults.BackgroundColor,
  val contentDistance: Dp = JetLimeDefaults.ContentDistance,
  val itemSpacing: Dp = JetLimeDefaults.ItemSpacing,
  val lineColor: Color = JetLimeDefaults.LineColor,
  val lineThickness: Dp = JetLimeDefaults.LineThickness,
  val lineBrush: Brush = JetLimeDefaults.lineSolidBrush(),
  val lineHorizontalAlignment: HorizontalAlignment = TOP,
  val lineVerticalAlignment: VerticalAlignment = LEFT,
  val pointStartFactor: Float = 1.1f
) {

  var arrangement: Arrangement = VERTICAL

  companion object {
    @Stable
    val Default = JetLimeStyle()
  }

  fun alignment(arrangement: Arrangement = VERTICAL): JetLimeStyle {
    return this.apply {
      this.arrangement = arrangement
    }
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is JetLimeStyle) return false
    if (backgroundColor != other.backgroundColor) return false
    if (lineColor != other.lineColor) return false
    if (lineThickness != other.lineThickness) return false
    if (arrangement != other.arrangement) return false
    return itemSpacing == other.itemSpacing
  }

  override fun hashCode(): Int {
    var result = backgroundColor.hashCode()
    result = 31 * result + lineColor.hashCode()
    result = 31 * result + lineThickness.hashCode()
    result = 31 * result + itemSpacing.hashCode()
    result = 31 * result + arrangement.hashCode()
    return result
  }
}

enum class Arrangement {
  VERTICAL,
  HORIZONTAL
}

enum class HorizontalAlignment {
  TOP,
  BOTTOM
}

enum class VerticalAlignment {
  LEFT,
  RIGHT
}