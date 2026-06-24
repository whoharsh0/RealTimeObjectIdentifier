package com.rudra.objectidentifier.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class UserSettingsTest {

    @Test
    fun defaults_matchDetectorBaseline() {
        val settings = UserSettings()
        assertEquals(0.45f, settings.confidenceThreshold)
        assertEquals(5, settings.maxDetections)
        assertEquals(true, settings.showConfidencePercent)
        assertEquals(false, settings.hasSeenOnboarding)
    }
}
