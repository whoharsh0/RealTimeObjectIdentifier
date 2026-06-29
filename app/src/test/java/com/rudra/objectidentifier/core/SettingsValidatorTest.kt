package com.rudra.objectidentifier.core

import com.rudra.objectidentifier.domain.model.AppTheme
import com.rudra.objectidentifier.domain.model.UserSettings
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SettingsValidatorTest {

    @Test
    fun sanitize_clampsOutOfRangeValues() {
        val sanitized = SettingsValidator.sanitize(
            UserSettings(
                confidenceThreshold = 2f,
                maxDetections = 99,
                minConfidenceForLabel = -1f,
                minBoxSizePercent = 1f,
                labelScale = 5f,
                filterLabel = "  phone filter with a very long label that should be trimmed down  "
            )
        )

        assertEquals(UserSettings.MAX_CONFIDENCE, sanitized.confidenceThreshold)
        assertEquals(UserSettings.MAX_MAX_DETECTIONS, sanitized.maxDetections)
        assertEquals(UserSettings.MIN_MIN_LABEL_CONFIDENCE, sanitized.minConfidenceForLabel)
        assertEquals(UserSettings.MAX_BOX_SIZE, sanitized.minBoxSizePercent)
        assertEquals(UserSettings.MAX_LABEL_SCALE, sanitized.labelScale)
        assertTrue(sanitized.filterLabel.length <= UserSettings.MAX_FILTER_LABEL_LENGTH)
    }

    @Test
    fun sanitize_preservesSystemDynamicTheme() {
        val sanitized = SettingsValidator.sanitize(UserSettings(appTheme = AppTheme.SYSTEM))
        assertEquals(AppTheme.SYSTEM, sanitized.appTheme)
    }
}
