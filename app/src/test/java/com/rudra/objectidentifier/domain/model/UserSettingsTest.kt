package com.rudra.objectidentifier.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class UserSettingsTest {

    @Test
    fun defaults_matchDetectorBaseline() {
        val settings = UserSettings()
        assertEquals(0.40f, settings.confidenceThreshold)
        assertEquals(10, settings.maxDetections)
        assertEquals(true, settings.showConfidencePercent)
        assertEquals(false, settings.hasSeenOnboarding)
    }
}
