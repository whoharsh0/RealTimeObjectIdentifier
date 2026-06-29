package com.rudra.objectidentifier.data.detector

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DetectionMapperTest {

    @Test
    fun mapDetection_normalizesBoundingBoxToZeroOneRange() {
        val result = DetectionMapper.mapDetection(
            raw = RawDetection(
                label = "cup",
                confidence = 0.87f,
                left = 100f,
                top = 200f,
                right = 300f,
                bottom = 500f
            ),
            imageWidth = 1000f,
            imageHeight = 2000f
        )

        requireNotNull(result)
        assertEquals("cup", result.label)
        assertEquals(0.87f, result.confidence)
        assertEquals(0.1f, result.boundingBox.left, 0.001f)
        assertEquals(0.1f, result.boundingBox.top, 0.001f)
        assertEquals(0.3f, result.boundingBox.right, 0.001f)
        assertEquals(0.25f, result.boundingBox.bottom, 0.001f)
    }

    @Test
    fun mapDetection_returnsNullForBlankLabel() {
        val result = DetectionMapper.mapDetection(
            raw = RawDetection(" ", 0.5f, 0f, 0f, 10f, 10f),
            imageWidth = 100f,
            imageHeight = 100f
        )
        assertNull(result)
    }

    @Test
    fun mapDetections_filtersInvalidEntries() {
        val results = DetectionMapper.mapDetections(
            rawDetections = listOf(
                RawDetection("phone", 0.9f, 0f, 0f, 50f, 50f),
                RawDetection("", 0.8f, 10f, 10f, 20f, 20f)
            ),
            imageWidth = 100f,
            imageHeight = 100f
        )

        assertEquals(1, results.size)
        assertEquals("phone", results.first().label)
    }

    @Test
    fun normalizeBoundingBox_preservesAspectRatioMapping() {
        val box = DetectionMapper.normalizeBoundingBox(
            left = 0f,
            top = 0f,
            right = 640f,
            bottom = 480f,
            imageWidth = 640f,
            imageHeight = 480f
        )

        assertEquals(1f, box.right)
        assertEquals(1f, box.bottom)
        assertTrue(box.width > 0f)
        assertTrue(box.height > 0f)
    }
}
