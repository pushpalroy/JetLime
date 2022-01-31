package com.pushpal.jetlime.ui.util.multifab

import android.annotation.SuppressLint
import androidx.compose.material.MaterialTheme
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color

@Immutable
interface FabOption {
  @Stable val iconTint: Color
  @Stable val iconBackgroundTint: Color
  @Stable val textBackgroundTint: Color
  @Stable val showLabel: Boolean
}

private class FabOptionImpl(
  override val iconTint: Color,
  override val iconBackgroundTint: Color,
  override val textBackgroundTint: Color,
  override val showLabel: Boolean
) : FabOption

@SuppressLint("ComposableNaming")
@Composable
fun FabOption(
  iconBackgroundTint: Color = MaterialTheme.colors.primary,
  iconTint: Color = contentColorFor(backgroundColor = iconBackgroundTint),
  textBackgroundTint: Color = MaterialTheme.colors.primary,
  showLabel: Boolean = false
): FabOption = FabOptionImpl(iconTint, iconBackgroundTint, textBackgroundTint, showLabel)
