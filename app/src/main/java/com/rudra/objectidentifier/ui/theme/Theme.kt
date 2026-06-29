package com.rudra.objectidentifier.ui.theme

import android.os.Build
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.rudra.objectidentifier.domain.model.AppTheme

/**
 * Root theme. Resolves the active [ColorScheme] from the user's [AppTheme] choice and animates
 * between schemes so theme switches don't hard-cut. When [reduceMotion] is true the color
 * transition snaps instantly to the end state.
 */
@Composable
fun RealTimeObjectIdentifierTheme(
    appTheme: AppTheme = AppTheme.DARK,
    reduceMotion: Boolean = false,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val systemDark = isSystemInDarkTheme()

    val target = when (appTheme) {
        AppTheme.LIGHT -> AppLightColorScheme
        AppTheme.AMOLED -> AppAmoledColorScheme
        AppTheme.HIGH_CONTRAST -> AppHighContrastColorScheme
        AppTheme.DARK -> AppDarkColorScheme
        AppTheme.SYSTEM -> {
            // Dynamic color (Material You) is only available on API 31+. Fall back to the
            // hand-tuned dark scheme on older devices.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (systemDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            } else {
                AppDarkColorScheme
            }
        }
    }

    val spec: AnimationSpec<Color> = if (reduceMotion) snap() else tween(durationMillis = 400)
    val colorScheme = target.animated(spec)

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}

/** Returns a copy of this scheme with its major roles animated toward their target values. */
@Composable
private fun ColorScheme.animated(spec: AnimationSpec<Color>): ColorScheme {
    val primary by animateColorAsState(this.primary, spec, label = "primary")
    val onPrimary by animateColorAsState(this.onPrimary, spec, label = "onPrimary")
    val primaryContainer by animateColorAsState(this.primaryContainer, spec, label = "primaryContainer")
    val onPrimaryContainer by animateColorAsState(this.onPrimaryContainer, spec, label = "onPrimaryContainer")
    val secondary by animateColorAsState(this.secondary, spec, label = "secondary")
    val onSecondary by animateColorAsState(this.onSecondary, spec, label = "onSecondary")
    val tertiary by animateColorAsState(this.tertiary, spec, label = "tertiary")
    val background by animateColorAsState(this.background, spec, label = "background")
    val onBackground by animateColorAsState(this.onBackground, spec, label = "onBackground")
    val surface by animateColorAsState(this.surface, spec, label = "surface")
    val onSurface by animateColorAsState(this.onSurface, spec, label = "onSurface")
    val surfaceVariant by animateColorAsState(this.surfaceVariant, spec, label = "surfaceVariant")
    val onSurfaceVariant by animateColorAsState(this.onSurfaceVariant, spec, label = "onSurfaceVariant")
    val error by animateColorAsState(this.error, spec, label = "error")
    val onError by animateColorAsState(this.onError, spec, label = "onError")

    return copy(
        primary = primary,
        onPrimary = onPrimary,
        primaryContainer = primaryContainer,
        onPrimaryContainer = onPrimaryContainer,
        secondary = secondary,
        onSecondary = onSecondary,
        tertiary = tertiary,
        background = background,
        onBackground = onBackground,
        surface = surface,
        onSurface = onSurface,
        surfaceVariant = surfaceVariant,
        onSurfaceVariant = onSurfaceVariant,
        error = error,
        onError = onError
    )
}

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

private val AppAmoledColorScheme = darkColorScheme(
    primary = AccentCyan,
    onPrimary = Color(0xFF002028),
    primaryContainer = Color(0xFF003640),
    onPrimaryContainer = AccentCyan,
    secondary = AccentGreen,
    onSecondary = Color(0xFF002110),
    tertiary = AccentOrange,
    background = Color.Black,
    onBackground = TextPrimary,
    surface = Color(0xFF050505),
    onSurface = TextPrimary,
    surfaceVariant = Color(0xFF111418),
    onSurfaceVariant = TextSecondary,
    error = ErrorRed,
    onError = Color.White
)

private val AppHighContrastColorScheme = darkColorScheme(
    primary = Color.White,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF222222),
    onPrimaryContainer = Color.White,
    secondary = Color.White,
    onSecondary = Color.Black,
    tertiary = Color(0xFFFFD740),
    background = Color.Black,
    onBackground = Color.White,
    surface = Color(0xFF111111),
    onSurface = Color.White,
    surfaceVariant = Color(0xFF1A1A1A),
    onSurfaceVariant = Color.White,
    error = Color(0xFFFF8A80),
    onError = Color.Black
)

private val AppLightColorScheme = lightColorScheme(
    primary = Color(0xFF006879),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFA8EEFF),
    onPrimaryContainer = Color(0xFF001F27),
    secondary = Color(0xFF00897B),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFB2DFDB),
    onSecondaryContainer = Color(0xFF00201C),
    tertiary = Color(0xFFB5560A),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFDCC2),
    onTertiaryContainer = Color(0xFF341100),
    background = Color(0xFFF4F7FA),
    onBackground = Color(0xFF101418),
    surface = Color.White,
    onSurface = Color(0xFF101418),
    surfaceVariant = Color(0xFFDCE3EA),
    onSurfaceVariant = Color(0xFF41484D),
    outline = Color(0xFF71787E),
    outlineVariant = Color(0xFFC1C7CE),
    error = Color(0xFFBA1A1A),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002)
)
