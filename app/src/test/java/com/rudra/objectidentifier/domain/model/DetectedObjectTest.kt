package com.rudra.objectidentifier.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class DetectedObjectTest {

    @Test
    fun confidencePercent_roundsCorrectly() {
        val detected = DetectedObject(
            label = "cup",
            confidence = 0.876f,
            boundingBox = BoundingBox(0f, 0f, 100f, 100f)
        )

        assertEquals(87, detected.confidencePercent)
        assertEquals(100f, detected.boundingBox.width)
    }
}
