package com.rudra.objectidentifier.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AppDarkColorScheme = darkColorScheme(
    primary = AccentCyan,
    onPrimary = Color(0xFF002028),
    primaryContainer = Color(0xFF004E5C),
    onPrimaryContainer = AccentCyan,
    secondary = AccentGreen,
    onSecondary = Color(0xFF002110),
    tertiary = AccentOrange,
    background = NightBackground,
    onBackground = TextPrimary,
    surface = NightSurface,
    onSurface = TextPrimary,
    surfaceVariant = NightSurfaceElevated,
    onSurfaceVariant = TextSecondary,
    error = ErrorRed,
    onError = Color.White
)

@Composable
fun RealTimeObjectIdentifierTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AppDarkColorScheme,
        typography = AppTypography,
        content = content
    )
}
