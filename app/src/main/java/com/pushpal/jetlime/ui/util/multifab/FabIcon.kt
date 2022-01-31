package com.pushpal.jetlime.ui.util.multifab

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.vector.ImageVector

@Immutable
interface FabIcon {
  @Stable
  val iconRes: ImageVector
  @Stable val iconRotate: Float?
}

private class FabIconImpl(
  override val iconRes: ImageVector,
  override val iconRotate: Float?
) : FabIcon

fun FabIcon(
  iconRes: ImageVector,
  iconRotate: Float? = null
): FabIcon =
  FabIconImpl(iconRes, iconRotate)