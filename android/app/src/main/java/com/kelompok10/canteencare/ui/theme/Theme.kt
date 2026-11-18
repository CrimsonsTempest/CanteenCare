package com.kelompok10.canteencare.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CanteenCareColors = lightColorScheme(
    primary = ForestGreen,
    onPrimary = Color.White,
    primaryContainer = GreenContainer,
    onPrimaryContainer = OnGreenContainer,
    inversePrimary = GreenContainer,
    secondary = SageGreen,
    onSecondary = Color.White,
    secondaryContainer = LightGreen,
    onSecondaryContainer = OnGreenContainer,
    tertiary = ForestGreenDark,
    onTertiary = Color.White,
    background = Color.White,
    onBackground = Color(0xFF1A1C19),
    surface = Color.White,
    onSurface = Color(0xFF1A1C19),
    surfaceVariant = LightGreen,
    onSurfaceVariant = Color(0xFF424940),
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = SurfaceGreenTint,
    surfaceContainer = SurfaceGreenTint,
    surfaceContainerHigh = LightGreen,
    surfaceContainerHighest = LightGreen,
    outline = OutlineGreen,
    outlineVariant = Color(0xFFDDE8DB),
    error = StatusRed,
    onError = Color.White,
)

/**
 * Tema CanteenCare: selalu terang dengan latar putih dan aksen hijau, sesuai desain Figma tim.
 * Dynamic color sengaja tidak dipakai agar warna sama di semua HP.
 */
@Composable
fun CanteenCareTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = CanteenCareColors,
        typography = Typography,
        shapes = Shapes,
        content = content,
    )
}
