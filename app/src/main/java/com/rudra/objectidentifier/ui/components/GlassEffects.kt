package com.rudra.objectidentifier.ui.components

import android.os.Build
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** True when the platform supports RenderEffect-based blur (API 31+). */
val supportsBlur: Boolean
    get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

/**
 * Scales the element down slightly while [interactionSource] reports a press, giving tactile
 * press feedback on buttons. No-op (and no animation) when [enabled] is false (reduce motion).
 */
@Composable
fun Modifier.pressScale(
    interactionSource: InteractionSource,
    enabled: Boolean = true,
    pressedScale: Float = 0.92f
): Modifier {
    if (!enabled) return this
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) pressedScale else 1f,
        label = "pressScale"
    )
    return this.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}

/**
 * Returns a callback that fires a light haptic when invoked, but only when [enabled] is true.
 * Wire this through the user's haptic-feedback preference so haptics are never hardcoded.
 */
@Composable
fun rememberHapticPerformer(enabled: Boolean): () -> Unit {
    val haptic = LocalHapticFeedback.current
    return remember(enabled, haptic) {
        {
            if (enabled) haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }
}

/**
 * A frosted "glass" container. On API 31+ it renders a soft blurred highlight layer + a more
 * translucent fill so it reads as frosted glass; below 31 it gracefully falls back to a solid
 * translucent fill (no blur). A soft shadow gives depth.
 */
@Composable
fun GlassSurface(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(24.dp),
    tint: Color = Color.Black,
    shadowElevation: Dp = 10.dp,
    content: @Composable () -> Unit
) {
    val fillAlpha = if (supportsBlur) 0.40f else 0.55f
    Surface(
        modifier = modifier,
        shape = shape,
        color = tint.copy(alpha = fillAlpha),
        tonalElevation = 0.dp,
        shadowElevation = shadowElevation
    ) {
        Box {
            if (supportsBlur) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .blur(radius = 18.dp, edgeTreatment = BlurredEdgeTreatment(shape))
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.White.copy(alpha = 0.10f),
                                    Color.White.copy(alpha = 0.02f),
                                    Color.Transparent
                                )
                            )
                        )
                )
            }
            content()
        }
    }
}

/**
 * A frosted scrim drawn behind the transparent top app bar. On API 31+ the gradient is blurred
 * for a glassy look; otherwise it's a plain translucent gradient.
 */
@Composable
fun FrostedTopScrim(modifier: Modifier = Modifier) {
    val effect = modifier
        .then(
            if (supportsBlur) {
                Modifier.blur(radius = 16.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded)
            } else {
                Modifier
            }
        )
        .background(
            Brush.verticalGradient(
                listOf(
                    Color.Black.copy(alpha = if (supportsBlur) 0.45f else 0.55f),
                    Color.Transparent
                )
            )
        )
    Box(modifier = effect)
}
