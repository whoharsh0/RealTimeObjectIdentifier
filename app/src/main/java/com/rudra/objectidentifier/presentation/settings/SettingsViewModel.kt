package com.rudra.objectidentifier.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rudra.objectidentifier.core.AppInfoProvider
import com.rudra.objectidentifier.di.IoDispatcher
import com.rudra.objectidentifier.domain.model.AppTheme
import com.rudra.objectidentifier.domain.model.BoxStyle
import com.rudra.objectidentifier.domain.model.CameraLens
import com.rudra.objectidentifier.domain.model.InferenceDelegate
import com.rudra.objectidentifier.domain.model.ModelVariant
import com.rudra.objectidentifier.domain.model.ScanMode
import com.rudra.objectidentifier.domain.model.UserSettings
import com.rudra.objectidentifier.domain.repository.UserSettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userSettingsRepository: UserSettingsRepository,
    private val appInfoProvider: AppInfoProvider,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val version = withContext(ioDispatcher) { appInfoProvider.getVersionName() }
            _uiState.update { it.copy(versionName = version) }
        }
        viewModelScope.launch {
            userSettingsRepository.settings.collect { settings ->
                _uiState.value = settings.toUiState(versionName = _uiState.value.versionName)
            }
        }
    }

    fun onConfidenceChanged(value: Float) = update { it.copy(confidenceThreshold = value) }
    fun onMaxDetectionsChanged(value: Int) = update { it.copy(maxDetections = value) }
    fun onShowConfidenceChanged(value: Boolean) = update { it.copy(showConfidencePercent = value) }
    fun onScanModeChanged(value: ScanMode) = update { it.copy(scanMode = value) }
    fun onModelVariantChanged(value: ModelVariant) = update { it.copy(modelVariant = value) }
    fun onInferenceDelegateChanged(value: InferenceDelegate) = update { it.copy(inferenceDelegate = value) }
    fun onSmoothingChanged(value: Boolean) = update { it.copy(enableSmoothing = value) }
    fun onFrameSkipChanged(value: Boolean) = update { it.copy(enableFrameSkip = value) }
    fun onBatterySaverChanged(value: Boolean) = update { it.copy(batterySaverMode = value) }
    fun onAutoStartChanged(value: Boolean) = update { it.copy(autoStartScanning = value) }
    fun onKeepScreenOnChanged(value: Boolean) = update { it.copy(keepScreenOn = value) }
    fun onShowFpsChanged(value: Boolean) = update { it.copy(showFpsOverlay = value) }
    fun onMinLabelConfidenceChanged(value: Float) = update { it.copy(minConfidenceForLabel = value) }
    fun onMinBoxSizeChanged(value: Float) = update { it.copy(minBoxSizePercent = value) }
    fun onAppThemeChanged(value: AppTheme) = update { it.copy(appTheme = value) }
    fun onBoxStyleChanged(value: BoxStyle) = update { it.copy(boxStyle = value) }
    fun onLabelScaleChanged(value: Float) = update { it.copy(labelScale = value) }
    fun onHapticChanged(value: Boolean) = update { it.copy(hapticFeedback = value) }
    fun onDefaultCameraChanged(value: CameraLens) = update { it.copy(defaultCameraLens = value) }
    fun onHistoryChanged(value: Boolean) = update { it.copy(enableHistory = value) }
    fun onHighContrastChanged(value: Boolean) = update { it.copy(highContrastMode = value) }
    fun onReduceMotionChanged(value: Boolean) = update { it.copy(reduceMotion = value) }
    fun onRoiChanged(value: Boolean) = update { it.copy(regionOfInterestEnabled = value) }
    fun onFilterLabelChanged(value: String) = update { it.copy(filterLabel = value) }
    fun onSceneDescriptionChanged(value: Boolean) = update { it.copy(showSceneDescription = value) }
    fun onSpeakLabelsChanged(value: Boolean) = update { it.copy(speakLabels = value) }
    fun onBarcodeScanningChanged(value: Boolean) = update { it.copy(enableBarcodeScanning = value) }
    fun onOcrModeChanged(value: Boolean) = update { it.copy(enableOcrMode = value) }

    fun onResetSettings() {
        viewModelScope.launch { userSettingsRepository.resetToDefaults() }
    }

    private fun update(transform: (UserSettings) -> UserSettings) {
        viewModelScope.launch { userSettingsRepository.applySettings(transform) }
    }

    private fun UserSettings.toUiState(versionName: String) = SettingsUiState(
        confidenceThreshold = confidenceThreshold,
        maxDetections = maxDetections,
        showConfidencePercent = showConfidencePercent,
        scanMode = scanMode,
        modelVariant = modelVariant,
        inferenceDelegate = inferenceDelegate,
        enableSmoothing = enableSmoothing,
        enableFrameSkip = enableFrameSkip,
        batterySaverMode = batterySaverMode,
        autoStartScanning = autoStartScanning,
        keepScreenOn = keepScreenOn,
        showFpsOverlay = showFpsOverlay,
        minConfidenceForLabel = minConfidenceForLabel,
        minBoxSizePercent = minBoxSizePercent,
        appTheme = appTheme,
        boxStyle = boxStyle,
        labelScale = labelScale,
        hapticFeedback = hapticFeedback,
        defaultCameraLens = defaultCameraLens,
        enableHistory = enableHistory,
        highContrastMode = highContrastMode,
        reduceMotion = reduceMotion,
        regionOfInterestEnabled = regionOfInterestEnabled,
        filterLabel = filterLabel,
        showSceneDescription = showSceneDescription,
        speakLabels = speakLabels,
        enableBarcodeScanning = enableBarcodeScanning,
        enableOcrMode = enableOcrMode,
        versionName = versionName
    )
}
