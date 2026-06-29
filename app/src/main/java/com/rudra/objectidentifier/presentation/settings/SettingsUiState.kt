package com.rudra.objectidentifier.presentation.settings

import com.rudra.objectidentifier.domain.model.AppTheme
import com.rudra.objectidentifier.domain.model.BoxStyle
import com.rudra.objectidentifier.domain.model.CameraLens
import com.rudra.objectidentifier.domain.model.InferenceDelegate
import com.rudra.objectidentifier.domain.model.ModelVariant
import com.rudra.objectidentifier.domain.model.ScanMode
import com.rudra.objectidentifier.domain.model.UserSettings

data class SettingsUiState(
    val confidenceThreshold: Float = UserSettings.DEFAULT_CONFIDENCE,
    val maxDetections: Int = UserSettings.DEFAULT_MAX_DETECTIONS,
    val showConfidencePercent: Boolean = true,
    val scanMode: ScanMode = ScanMode.GENERAL,
    val modelVariant: ModelVariant = ModelVariant.LITE0,
    val inferenceDelegate: InferenceDelegate = InferenceDelegate.NNAPI,
    val enableSmoothing: Boolean = true,
    val enableFrameSkip: Boolean = true,
    val batterySaverMode: Boolean = false,
    val autoStartScanning: Boolean = false,
    val keepScreenOn: Boolean = true,
    val showFpsOverlay: Boolean = false,
    val minConfidenceForLabel: Float = UserSettings.DEFAULT_MIN_LABEL_CONFIDENCE,
    val minBoxSizePercent: Float = UserSettings.DEFAULT_MIN_BOX_SIZE,
    val appTheme: AppTheme = AppTheme.DARK,
    val boxStyle: BoxStyle = BoxStyle.FULL,
    val labelScale: Float = UserSettings.DEFAULT_LABEL_SCALE,
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
    val enableOcrMode: Boolean = false,
    val versionName: String = ""
)
