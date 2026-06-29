package com.rudra.objectidentifier.ui.components

import kotlin.math.exp

/**
 * Pure (Android-free) geometry/animation math for the bounding-box overlay. Kept separate from
 * the Compose drawing code so the tricky bits (label clamping, confidence fade, frame-rate
 * independent smoothing) can be unit-tested on the JVM.
 */
object BoundingBoxGeometry {

    /**
     * Returns the x position at which a label of [labelWidth] should start so it never overflows
     * the right edge of a [canvasWidth]-wide surface. Labels normally start at [boxLeft]; when that
     * would clip the right edge the label is shifted left. Never returns less than [minX].
     */
    fun clampLabelX(
        boxLeft: Float,
        labelWidth: Float,
        canvasWidth: Float,
        minX: Float = 0f
    ): Float {
        if (canvasWidth <= 0f) return minX
        val maxStart = (canvasWidth - labelWidth).coerceAtLeast(minX)
        return boxLeft.coerceIn(minX, maxStart)
    }

    /**
     * Maps a [confidence] in 0..1 to an alpha in [minAlpha]..[maxAlpha] so lower-confidence boxes
     * render more transparent.
     */
    fun confidenceAlpha(
        confidence: Float,
        minAlpha: Float = 0.45f,
        maxAlpha: Float = 1f
    ): Float {
        val c = confidence.coerceIn(0f, 1f)
        return (minAlpha + (maxAlpha - minAlpha) * c).coerceIn(minAlpha, maxAlpha)
    }

    /** Linear interpolation from [start] to [end] by [fraction] (clamped to 0..1). */
    fun lerp(start: Float, end: Float, fraction: Float): Float {
        val f = fraction.coerceIn(0f, 1f)
        return start + (end - start) * f
    }

    /**
     * Frame-rate independent exponential smoothing factor for a given [dtSeconds] elapsed and a
     * [timeConstantSeconds]. Returns a value in 0..1 suitable for [lerp]. A larger time constant
     * means slower/smoother glide. dt <= 0 returns 0 (no movement); non-positive time constant
     * returns 1 (snap).
     */
    fun smoothingFactor(dtSeconds: Float, timeConstantSeconds: Float): Float {
        if (timeConstantSeconds <= 0f) return 1f
        if (dtSeconds <= 0f) return 0f
        return (1f - exp(-dtSeconds / timeConstantSeconds)).coerceIn(0f, 1f)
    }
}
