package com.rudra.objectidentifier.ui.components

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BoundingBoxGeometryTest {

    @Test
    fun clampLabelX_keepsLeftWhenItFits() {
        val x = BoundingBoxGeometry.clampLabelX(boxLeft = 100f, labelWidth = 200f, canvasWidth = 1000f)
        assertEquals(100f, x, 0.001f)
    }

    @Test
    fun clampLabelX_shiftsLeftWhenItWouldOverflowRightEdge() {
        // Label would end at 950 + 200 = 1150 > 1000, so it must shift left to 800.
        val x = BoundingBoxGeometry.clampLabelX(boxLeft = 950f, labelWidth = 200f, canvasWidth = 1000f)
        assertEquals(800f, x, 0.001f)
    }

    @Test
    fun clampLabelX_neverNegativeWhenLabelWiderThanCanvas() {
        val x = BoundingBoxGeometry.clampLabelX(boxLeft = 50f, labelWidth = 1200f, canvasWidth = 1000f)
        assertEquals(0f, x, 0.001f)
    }

    @Test
    fun clampLabelX_zeroCanvasReturnsMin() {
        val x = BoundingBoxGeometry.clampLabelX(boxLeft = 50f, labelWidth = 100f, canvasWidth = 0f)
        assertEquals(0f, x, 0.001f)
    }

    @Test
    fun confidenceAlpha_mapsRange() {
        assertEquals(1f, BoundingBoxGeometry.confidenceAlpha(1f), 0.001f)
        assertEquals(0.45f, BoundingBoxGeometry.confidenceAlpha(0f), 0.001f)
        // Halfway between 0.45 and 1.0
        assertEquals(0.725f, BoundingBoxGeometry.confidenceAlpha(0.5f), 0.001f)
    }

    @Test
    fun confidenceAlpha_clampsOutOfRange() {
        assertEquals(0.45f, BoundingBoxGeometry.confidenceAlpha(-5f), 0.001f)
        assertEquals(1f, BoundingBoxGeometry.confidenceAlpha(5f), 0.001f)
    }

    @Test
    fun lerp_interpolatesAndClampsFraction() {
        assertEquals(0f, BoundingBoxGeometry.lerp(0f, 10f, 0f), 0.001f)
        assertEquals(10f, BoundingBoxGeometry.lerp(0f, 10f, 1f), 0.001f)
        assertEquals(5f, BoundingBoxGeometry.lerp(0f, 10f, 0.5f), 0.001f)
        assertEquals(10f, BoundingBoxGeometry.lerp(0f, 10f, 2f), 0.001f)
        assertEquals(0f, BoundingBoxGeometry.lerp(0f, 10f, -1f), 0.001f)
    }

    @Test
    fun smoothingFactor_zeroDtMeansNoMovement() {
        assertEquals(0f, BoundingBoxGeometry.smoothingFactor(0f, 0.08f), 0.001f)
    }

    @Test
    fun smoothingFactor_nonPositiveTimeConstantSnaps() {
        assertEquals(1f, BoundingBoxGeometry.smoothingFactor(0.016f, 0f), 0.001f)
    }

    @Test
    fun smoothingFactor_isBetweenZeroAndOneForTypicalFrame() {
        val f = BoundingBoxGeometry.smoothingFactor(0.016f, 0.08f)
        assertTrue("factor in (0,1): $f", f > 0f && f < 1f)
    }

    @Test
    fun smoothingFactor_largerDtMovesMore() {
        val small = BoundingBoxGeometry.smoothingFactor(0.016f, 0.08f)
        val large = BoundingBoxGeometry.smoothingFactor(0.05f, 0.08f)
        assertTrue("larger dt should yield larger factor", large > small)
    }
}
