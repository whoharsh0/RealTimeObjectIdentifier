package com.rudra.objectidentifier.data.detector

import com.rudra.objectidentifier.domain.model.BoundingBox
import com.rudra.objectidentifier.domain.model.DetectedObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DetectionSmootherTest {

    private val smoother = DetectionSmoother()

    @Test
    fun smooth_filtersSmallBoxesAndBlankFilter() {
        val result = smoother.smooth(
            current = listOf(
                DetectedObject("cup", 0.9f, BoundingBox(0.1f, 0.1f, 0.3f, 0.3f)),
                DetectedObject("phone", 0.8f, BoundingBox(0f, 0f, 0.005f, 0.005f))
            ),
            enabled = true,
            minBoxSizePercent = 0.02f,
            minConfidenceForLabel = 0.25f,
            filterLabel = "",
            highlightedTrackId = null,
            highContrast = false
        )

        assertEquals(1, result.size)
        assertEquals("Cup", result.first().displayLabel)
    }

    @Test
    fun smooth_appliesLabelFilter() {
        val result = smoother.smooth(
            current = listOf(
                DetectedObject("cup", 0.9f, BoundingBox(0.1f, 0.1f, 0.3f, 0.3f)),
                DetectedObject("phone", 0.8f, BoundingBox(0.4f, 0.4f, 0.6f, 0.6f))
            ),
            enabled = true,
            minBoxSizePercent = 0.02f,
            minConfidenceForLabel = 0.25f,
            filterLabel = "phone",
            highlightedTrackId = null,
            highContrast = false
        )

        assertEquals(1, result.size)
        assertEquals("Phone", result.first().displayLabel)
    }

    @Test
    fun smooth_marksHighlightedTrack() {
        val result = smoother.smooth(
            current = listOf(
                DetectedObject("cup", 0.9f, BoundingBox(0.1f, 0.1f, 0.3f, 0.3f))
            ),
            enabled = false,
            minBoxSizePercent = 0.01f,
            minConfidenceForLabel = 0.1f,
            filterLabel = "",
            highlightedTrackId = 1,
            highContrast = false
        )

        assertTrue(result.first().isHighlighted)
    }
}
