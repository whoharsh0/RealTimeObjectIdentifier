package com.rudra.objectidentifier.presentation.detection

import com.rudra.objectidentifier.domain.model.BarcodeResult
import com.rudra.objectidentifier.domain.model.BoxStyle
import com.rudra.objectidentifier.domain.model.CameraLens
import com.rudra.objectidentifier.domain.model.DetectedObject
import com.rudra.objectidentifier.domain.model.DetectionMetrics
import com.rudra.objectidentifier.domain.model.DisplayDetection
import com.rudra.objectidentifier.domain.model.OcrLine

data class DetectionUiState(
    val isLoading: Boolean = true,
    val appTitle: String = "",
    val versionName: String = "",
    val detections: List<DetectedObject> = emptyList(),
    val displayDetections: List<DisplayDetection> = emptyList(),
    val barcodeResults: List<BarcodeResult> = emptyList(),
    val ocrLines: List<OcrLine> = emptyList(),
    val isDetecting: Boolean = false,
    val isFrozen: Boolean = false,
    val cameraLens: CameraLens = CameraLens.BACK,
    val showConfidencePercent: Boolean = true,
    val showOnboardingDialog: Boolean = false,
    val errorMessage: String? = null,
    val metrics: DetectionMetrics = DetectionMetrics(),
    val showFpsOverlay: Boolean = false,
    val sceneDescription: String = "",
    val showSceneDescription: Boolean = true,
    val showDetectionList: Boolean = false,
    val torchEnabled: Boolean = false,
    val hasFlash: Boolean = false,
    val boxStyle: BoxStyle = BoxStyle.FULL,
    val labelScale: Float = 1f,
    val highContrastMode: Boolean = false,
    val reduceMotion: Boolean = false,
    val galleryResultMessage: String? = null,
    val isGalleryProcessing: Boolean = false,
    val enableOcrMode: Boolean = false,
    val enableBarcodeScanning: Boolean = true
)
