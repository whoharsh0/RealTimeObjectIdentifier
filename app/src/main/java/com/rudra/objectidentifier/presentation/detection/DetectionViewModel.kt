package com.rudra.objectidentifier.presentation.detection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rudra.objectidentifier.core.AppInfoProvider
import com.rudra.objectidentifier.di.IoDispatcher
import com.rudra.objectidentifier.domain.repository.DetectionRepository
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
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetectionUiState())
    val uiState: StateFlow<DetectionUiState> = _uiState.asStateFlow()

    init {
        loadInitialState()
        observeDetections()
    }

    private fun loadInitialState() {
        viewModelScope.launch {
            val title = withContext(ioDispatcher) { appInfoProvider.getAppTitle() }
            val version = withContext(ioDispatcher) { appInfoProvider.getVersionName() }
            _uiState.update {
                it.copy(
                    isLoading = false,
                    appTitle = title,
                    versionName = version
                )
            }
        }
    }

    private fun observeDetections() {
        viewModelScope.launch {
            detectionRepository.observeDetections().collect { detections ->
                _uiState.update { it.copy(detections = detections) }
            }
        }
    }

    fun onStartDetection() {
        viewModelScope.launch(ioDispatcher) {
            runCatching {
                detectionRepository.startDetection()
            }.onSuccess {
                _uiState.update { it.copy(isDetecting = true, errorMessage = null) }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isDetecting = false,
                        errorMessage = error.message ?: "Failed to start detection"
                    )
                }
            }
        }
    }

    fun onStopDetection() {
        viewModelScope.launch(ioDispatcher) {
            runCatching {
                detectionRepository.stopDetection()
            }.onSuccess {
                _uiState.update { it.copy(isDetecting = false, errorMessage = null) }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(errorMessage = error.message ?: "Failed to stop detection")
                }
            }
        }
    }

    fun onToggleCamera() {
        _uiState.update { it.copy(cameraLens = it.cameraLens.toggled()) }
    }
}
