package com.rudra.objectidentifier.domain.model

data class UserSettings(
    val confidenceThreshold: Float = DEFAULT_CONFIDENCE,
    val maxDetections: Int = DEFAULT_MAX_DETECTIONS,
    val showConfidencePercent: Boolean = true,
    val hasSeenOnboarding: Boolean = false,
    val scanMode: ScanMode = ScanMode.GENERAL,
    val modelVariant: ModelVariant = ModelVariant.LITE0,
    val inferenceDelegate: InferenceDelegate = InferenceDelegate.NNAPI,
    val enableSmoothing: Boolean = true,
    val enableFrameSkip: Boolean = true,
    val batterySaverMode: Boolean = false,
    val autoStartScanning: Boolean = false,
    val keepScreenOn: Boolean = true,
    val showFpsOverlay: Boolean = false,
    val minConfidenceForLabel: Float = DEFAULT_MIN_LABEL_CONFIDENCE,
    val minBoxSizePercent: Float = DEFAULT_MIN_BOX_SIZE,
    val appTheme: AppTheme = AppTheme.DARK,
    val boxStyle: BoxStyle = BoxStyle.FULL,
    val labelScale: Float = DEFAULT_LABEL_SCALE,
    val hapticFeedback: Boolean = true,
    val defaultCameraLens: CameraLens = CameraLens.BACK,
    val enableHistory: Boolean = true,
    val highContrastMode: Boolean = false,
    val reduceMotion: Boolean = false,
    val regionOfInterestEnabled: Boolean = false,
    val filterLabel: String = "",
    val showSceneDescription: Boolean = true,
    val speakLabels: Boolean = false,
    val enableBarcodeScanning: Boolean = true,
    val enableOcrMode: Boolean = false
) {
    companion object {
        const val DEFAULT_CONFIDENCE = 0.40f
        const val MIN_CONFIDENCE = 0.20f
        const val MAX_CONFIDENCE = 0.90f
        const val DEFAULT_MAX_DETECTIONS = 10
        const val MIN_MAX_DETECTIONS = 1
        const val MAX_MAX_DETECTIONS = 20
        const val DEFAULT_MIN_LABEL_CONFIDENCE = 0.22f
        const val MIN_MIN_LABEL_CONFIDENCE = 0.10f
        const val MAX_MIN_LABEL_CONFIDENCE = 0.80f
        const val DEFAULT_MIN_BOX_SIZE = 0.015f
        const val MIN_BOX_SIZE = 0.005f
        const val MAX_BOX_SIZE = 0.15f
        const val DEFAULT_LABEL_SCALE = 1.0f
        const val MIN_LABEL_SCALE = 0.75f
        const val MAX_LABEL_SCALE = 1.5f
        const val MAX_FILTER_LABEL_LENGTH = 32
    }
}
