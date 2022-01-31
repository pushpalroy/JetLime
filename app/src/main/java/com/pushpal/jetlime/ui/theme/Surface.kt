package com.pushpal.jetlime.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.material.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlin.math.ln

/**
 * An alternative to [androidx.compose.material.Surface]
 */
@Composable
fun JetLimeSurface(
  modifier: Modifier = Modifier,
  shape: Shape = RectangleShape,
  color: Color = JetLimeTheme.colors.uiBackground,
  contentColor: Color = JetLimeTheme.colors.textSecondary,
  border: BorderStroke? = null,
  elevation: Dp = 0.dp,
  content: @Composable () -> Unit
) {
  Box(
    modifier = modifier
      .shadow(elevation = elevation, shape = shape, clip = false)
      .zIndex(elevation.value)
      .then(if (border != null) Modifier.border(border, shape) else Modifier)
      .background(
        color = getBackgroundColorForElevation(color, elevation),
        shape = shape
      )
      .clip(shape)
  ) {
    CompositionLocalProvider(LocalContentColor provides contentColor, content = content)
  }
}

@Composable
private fun getBackgroundColorForElevation(
  color: Color,
  elevation: Dp
): Color {
  return if (elevation > 0.dp) {
    color.withElevation(elevation)
  } else {
    color
  }
}

/**
 * Applies a [Color.White] overlay to this color based on the [elevation]. This increases visibility
 * of elevation for surfaces in a dark theme.
 */
private fun Color.withElevation(elevation: Dp): Color {
  val foreground = calculateForeground(elevation)
  return foreground.compositeOver(background = this)
}

/**
 * @return the alpha-modified [Color.White] to overlay on top of the surface color to produce
 * the resultant color.
 */
private fun calculateForeground(elevation: Dp): Color {
  val alpha = ((4.5f * ln(elevation.value + 1)) + 2f) / 20f
  return Color.White.copy(alpha = alpha)
}