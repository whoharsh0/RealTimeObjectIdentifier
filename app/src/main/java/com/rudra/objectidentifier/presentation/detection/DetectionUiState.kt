package com.rudra.objectidentifier.presentation.detection

import com.rudra.objectidentifier.domain.model.CameraLens
import com.rudra.objectidentifier.domain.model.DetectedObject

data class DetectionUiState(
    val isLoading: Boolean = true,
    val appTitle: String = "",
    val versionName: String = "",
    val detections: List<DetectedObject> = emptyList(),
    val isDetecting: Boolean = false,
    val cameraLens: CameraLens = CameraLens.BACK,
    val showConfidencePercent: Boolean = true,
    val showOnboardingDialog: Boolean = false,
    val errorMessage: String? = null
)
