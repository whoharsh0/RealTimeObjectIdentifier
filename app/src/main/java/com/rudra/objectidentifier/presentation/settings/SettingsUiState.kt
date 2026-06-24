package com.rudra.objectidentifier.presentation.settings

data class SettingsUiState(
    val confidenceThreshold: Float = 0.45f,
    val maxDetections: Int = 5,
    val showConfidencePercent: Boolean = true
)
