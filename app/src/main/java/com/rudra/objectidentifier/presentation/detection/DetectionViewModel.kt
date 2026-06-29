package com.rudra.objectidentifier.presentation.detection

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rudra.objectidentifier.core.AppInfoProvider
import com.rudra.objectidentifier.data.camera.CameraControlHolder
import com.rudra.objectidentifier.data.detector.GalleryImageDetector
import com.rudra.objectidentifier.di.IoDispatcher
import com.rudra.objectidentifier.domain.repository.DetectionRepository
import com.rudra.objectidentifier.domain.repository.ScanHistoryRepository
import com.rudra.objectidentifier.domain.repository.UserSettingsRepository
import com.rudra.objectidentifier.domain.util.LabelFormatter
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
class DetectionViewModel @Inject constructor(
    private val detectionRepository: DetectionRepository,
    private val appInfoProvider: AppInfoProvider,
    private val userSettingsRepository: UserSettingsRepository,
    private val scanHistoryRepository: ScanHistoryRepository,
    private val galleryImageDetector: GalleryImageDetector,
    private val cameraControlHolder: CameraControlHolder,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetectionUiState())
    val uiState: StateFlow<DetectionUiState> = _uiState.asStateFlow()

    init {
        loadInitialState()
        observeDetections()
        observeDisplayDetections()
        observeMetrics()
        observeFrozen()
        observeSettings()
        observeBarcodes()
        observeOcr()
    }

    private fun loadInitialState() {
        viewModelScope.launch {
            val title = withContext(ioDispatcher) { appInfoProvider.getAppTitle() }
            val version = withContext(ioDispatcher) { appInfoProvider.getVersionName() }
            _uiState.update {
                it.copy(
                    isLoading = false,
                    appTitle = title,
                    versionName = version,
                    hasFlash = cameraControlHolder.hasFlashUnit()
                )
            }
            val settings = userSettingsRepository.currentSettings()
            if (settings.autoStartScanning) onStartDetection()
        }
    }

    private fun observeDetections() {
        viewModelScope.launch {
            detectionRepository.observeDetections().collect { detections ->
                _uiState.update {
                    it.copy(
                        detections = detections,
                        sceneDescription = if (it.showSceneDescription) {
                            LabelFormatter.sceneDescription(detections.map { d -> d.label })
                        } else ""
                    )
                }
            }
        }
    }

    private fun observeDisplayDetections() {
        viewModelScope.launch {
            detectionRepository.observeDisplayDetections().collect { displayDetections ->
                _uiState.update { it.copy(displayDetections = displayDetections) }
            }
        }
    }

    private fun observeMetrics() {
        viewModelScope.launch {
            detectionRepository.observeMetrics().collect { metrics ->
                _uiState.update { it.copy(metrics = metrics) }
            }
        }
    }

    private fun observeFrozen() {
        viewModelScope.launch {
            detectionRepository.observeIsFrozen().collect { frozen ->
                _uiState.update { it.copy(isFrozen = frozen) }
            }
        }
    }

    private fun observeBarcodes() {
        viewModelScope.launch {
            detectionRepository.observeBarcodeResults().collect { barcodes ->
                _uiState.update { it.copy(barcodeResults = barcodes) }
            }
        }
    }

    private fun observeOcr() {
        viewModelScope.launch {
            detectionRepository.observeOcrLines().collect { lines ->
                _uiState.update { it.copy(ocrLines = lines) }
            }
        }
    }

    private fun observeSettings() {
        viewModelScope.launch {
            userSettingsRepository.settings.collect { settings ->
                _uiState.update {
                    it.copy(
                        showConfidencePercent = settings.showConfidencePercent,
                        showOnboardingDialog = !settings.hasSeenOnboarding && !it.isLoading,
                        showFpsOverlay = settings.showFpsOverlay,
                        showSceneDescription = settings.showSceneDescription,
                        boxStyle = settings.boxStyle,
                        labelScale = settings.labelScale,
                        highContrastMode = settings.highContrastMode,
                        reduceMotion = settings.reduceMotion,
                        enableOcrMode = settings.enableOcrMode,
                        enableBarcodeScanning = settings.enableBarcodeScanning,
                        cameraLens = if (!it.isDetecting) settings.defaultCameraLens else it.cameraLens
                    )
                }
            }
        }
    }

    fun onStartDetection() {
        viewModelScope.launch(ioDispatcher) {
            runCatching { detectionRepository.startDetection() }
                .onSuccess {
                    _uiState.update {
                        it.copy(isDetecting = true, errorMessage = null, galleryResultMessage = null)
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(isDetecting = false, errorMessage = error.message ?: "Failed to start detection")
                    }
                }
        }
    }

    fun onStopDetection() {
        viewModelScope.launch(ioDispatcher) {
            recordHistoryIfNeeded()
            cameraControlHolder.disableTorch()
            runCatching { detectionRepository.stopDetection() }
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isDetecting = false,
                            isFrozen = false,
                            torchEnabled = false,
                            errorMessage = null,
                            showDetectionList = false
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(errorMessage = error.message ?: "Failed to stop detection") }
                }
        }
    }

    fun onToggleFreeze() {
        viewModelScope.launch(ioDispatcher) {
            runCatching { detectionRepository.toggleFreeze() }
        }
    }

    fun onToggleDetectionList() {
        _uiState.update { it.copy(showDetectionList = !it.showDetectionList) }
    }

    fun onHighlightTrack(trackId: Int?) {
        viewModelScope.launch(ioDispatcher) {
            detectionRepository.setHighlightedTrack(trackId)
        }
    }

    fun onToggleTorch() {
        val enabled = cameraControlHolder.toggleTorch()
        _uiState.update { it.copy(torchEnabled = enabled) }
    }

    fun onToggleCamera() {
        _uiState.update { it.copy(cameraLens = it.cameraLens.toggled()) }
    }

    fun onDismissOnboarding() {
        viewModelScope.launch {
            userSettingsRepository.setHasSeenOnboarding(true)
            _uiState.update { it.copy(showOnboardingDialog = false) }
        }
    }

    fun onDismissGalleryMessage() {
        _uiState.update { it.copy(galleryResultMessage = null) }
    }

    fun onGalleryImageSelected(bitmap: Bitmap?) {
        viewModelScope.launch(ioDispatcher) {
            _uiState.update { it.copy(isGalleryProcessing = true, galleryResultMessage = null) }
            val result = galleryImageDetector.detect(bitmap)
            result.fold(
                onSuccess = { detections ->
                    _uiState.update {
                        it.copy(
                            isGalleryProcessing = false,
                            galleryResultMessage = if (detections.isEmpty()) {
                                "No objects found in this image"
                            } else {
                                LabelFormatter.sceneDescription(detections.map { d -> d.label })
                            },
                            detections = detections,
                            displayDetections = detections.mapIndexed { index, detection ->
                                com.rudra.objectidentifier.domain.model.DisplayDetection(
                                    detection = detection,
                                    trackId = index + 1,
                                    displayLabel = LabelFormatter.format(detection.label),
                                    categoryColorArgb = com.rudra.objectidentifier.domain.util.ObjectCategory
                                        .colorArgbForLabel(detection.label)
                                )
                            }
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isGalleryProcessing = false,
                            galleryResultMessage = error.message ?: "Could not analyze image"
                        )
                    }
                }
            )
        }
    }

    private suspend fun recordHistoryIfNeeded() {
        val settings = userSettingsRepository.currentSettings()
        if (!settings.enableHistory) return
        val detections = detectionRepository.currentDetections()
        if (detections.isEmpty()) return
        runCatching {
            scanHistoryRepository.recordScan(
                topLabels = detections.map { it.label },
                detectionCount = detections.size,
                scanModeName = settings.scanMode.name
            )
        }
    }
}
