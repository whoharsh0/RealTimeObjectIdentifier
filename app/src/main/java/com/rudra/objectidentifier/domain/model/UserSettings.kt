package com.rudra.objectidentifier.domain.model

data class UserSettings(
    val confidenceThreshold: Float = DEFAULT_CONFIDENCE,
    val maxDetections: Int = DEFAULT_MAX_DETECTIONS,
    val showConfidencePercent: Boolean = true,
    val hasSeenOnboarding: Boolean = false
) {
    companion object {
        const val DEFAULT_CONFIDENCE = 0.45f
        const val MIN_CONFIDENCE = 0.20f
        const val MAX_CONFIDENCE = 0.90f
        const val DEFAULT_MAX_DETECTIONS = 5
        const val MIN_MAX_DETECTIONS = 1
        const val MAX_MAX_DETECTIONS = 10
    }
}
