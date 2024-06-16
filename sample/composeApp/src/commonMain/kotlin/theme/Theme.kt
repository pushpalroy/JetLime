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
package theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

val DarkColorPalette = darkColorScheme(
  primary = DarkColors.Primary,
  onPrimary = DarkColors.OnPrimary,
  primaryContainer = DarkColors.PrimaryContainer,
  onPrimaryContainer = DarkColors.OnPrimaryContainer,
  inversePrimary = DarkColors.InversePrimary,
  secondary = DarkColors.Secondary,
  onSecondary = DarkColors.OnSecondary,
  secondaryContainer = DarkColors.SecondaryContainer,
  onSecondaryContainer = DarkColors.OnSecondaryContainer,
  tertiary = DarkColors.Tertiary,
  onTertiary = DarkColors.OnTertiary,
  tertiaryContainer = DarkColors.TertiaryContainer,
  onTertiaryContainer = DarkColors.OnTertiaryContainer,
  background = DarkColors.Background,
  onBackground = DarkColors.OnBackground,
  surface = DarkColors.Surface,
  onSurface = DarkColors.OnSurface,
  surfaceVariant = DarkColors.SurfaceVariant,
  onSurfaceVariant = DarkColors.OnSurfaceVariant,
  surfaceTint = DarkColors.SurfaceTint,
  inverseSurface = DarkColors.InverseSurface,
  inverseOnSurface = DarkColors.InverseOnSurface,
  error = DarkColors.Error,
  onError = DarkColors.OnError,
  errorContainer = DarkColors.ErrorContainer,
  onErrorContainer = DarkColors.OnErrorContainer,
  outline = DarkColors.Outline,
  outlineVariant = DarkColors.OutlineVariant,
  scrim = DarkColors.Scrim,
)

val LightColorPalette = lightColorScheme(
  primary = LightColors.Primary,
  onPrimary = LightColors.OnPrimary,
  primaryContainer = LightColors.PrimaryContainer,
  onPrimaryContainer = LightColors.OnPrimaryContainer,
  inversePrimary = LightColors.InversePrimary,
  secondary = LightColors.Secondary,
  onSecondary = LightColors.OnSecondary,
  secondaryContainer = LightColors.SecondaryContainer,
  onSecondaryContainer = LightColors.OnSecondaryContainer,
  tertiary = LightColors.Tertiary,
  onTertiary = LightColors.OnTertiary,
  tertiaryContainer = LightColors.TertiaryContainer,
  onTertiaryContainer = LightColors.OnTertiaryContainer,
  background = LightColors.Background,
  onBackground = LightColors.OnBackground,
  surface = LightColors.Surface,
  onSurface = LightColors.OnSurface,
  surfaceVariant = LightColors.SurfaceVariant,
  onSurfaceVariant = LightColors.OnSurfaceVariant,
  surfaceTint = LightColors.SurfaceTint,
  inverseSurface = LightColors.InverseSurface,
  inverseOnSurface = LightColors.InverseOnSurface,
  error = LightColors.Error,
  onError = LightColors.OnError,
  errorContainer = LightColors.ErrorContainer,
  onErrorContainer = LightColors.OnErrorContainer,
  outline = LightColors.Outline,
  outlineVariant = LightColors.OutlineVariant,
  scrim = LightColors.Scrim,
)

@Composable
fun JetLimeTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
  val colors = if (darkTheme) {
    DarkColorPalette
  } else {
    LightColorPalette
  }

  MaterialTheme(
    colorScheme = colors,
    shapes = JetLimeShapes,
    content = content,
  )
}
