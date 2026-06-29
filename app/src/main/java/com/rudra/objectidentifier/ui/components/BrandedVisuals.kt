package com.rudra.objectidentifier.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.rudra.objectidentifier.R
import com.rudra.objectidentifier.ui.theme.AccentCyan
import com.rudra.objectidentifier.ui.theme.GradientEnd
import com.rudra.objectidentifier.ui.theme.GradientStart

/**
 * A viewfinder-style graphic with corner brackets and a sweeping scan line. The scan line animates
 * vertically unless [reduceMotion] is true, in which case it stays centered (static end-state).
 */
@Composable
fun ScanViewfinder(
    modifier: Modifier = Modifier,
    reduceMotion: Boolean = false,
    accent: Color = AccentCyan
) {
    val scanFraction = if (reduceMotion) {
        0.5f
    } else {
        val transition = rememberInfiniteTransition(label = "scan-line")
        transition.animateFloat(
            initialValue = 0.12f,
            targetValue = 0.88f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1800, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "scan-line-y"
        ).value
    }

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        if (w <= 0f || h <= 0f) return@Canvas
        val stroke = 4.dp.toPx()
        val corner = minOf(w, h) * 0.22f

        // Corner brackets
        fun bracket(x: Float, y: Float, dx: Float, dy: Float) {
            drawLine(accent, Offset(x, y), Offset(x + dx, y), stroke)
            drawLine(accent, Offset(x, y), Offset(x, y + dy), stroke)
        }
        bracket(0f, 0f, corner, corner)
        bracket(w, 0f, -corner, corner)
        bracket(0f, h, corner, -corner)
        bracket(w, h, -corner, -corner)

        // Sweeping scan line with a soft glow band
        val y = h * scanFraction
        val bandHeight = h * 0.10f
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color.Transparent, accent.copy(alpha = 0.18f), Color.Transparent),
                startY = y - bandHeight,
                endY = y + bandHeight
            ),
            topLeft = Offset(0f, y - bandHeight),
            size = Size(w, bandHeight * 2)
        )
        drawLine(
            color = accent,
            start = Offset(0f, y),
            end = Offset(w, y),
            strokeWidth = 2.dp.toPx()
        )
        // Subtle reticle in the center
        drawCircle(
            color = accent.copy(alpha = 0.5f),
            radius = minOf(w, h) * 0.04f,
            center = Offset(w / 2f, h / 2f),
            style = Stroke(width = 2.dp.toPx())
        )
    }
}

/**
 * Branded full-screen startup state: gradient background, animated scan viewfinder and a
 * "warming up" caption. Replaces a bare CircularProgressIndicator. Honors [reduceMotion].
 */
@Composable
fun BrandedLoading(
    modifier: Modifier = Modifier,
    reduceMotion: Boolean = false
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(GradientStart, GradientEnd))),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            ScanViewfinder(
                modifier = Modifier.size(140.dp),
                reduceMotion = reduceMotion
            )
            Text(
                text = stringResource(R.string.loading_initializing),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.85f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
        }
    }
}
