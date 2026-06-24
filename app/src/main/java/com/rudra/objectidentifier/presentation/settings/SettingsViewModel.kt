package com.rudra.objectidentifier.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rudra.objectidentifier.domain.repository.UserSettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userSettingsRepository: UserSettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            userSettingsRepository.settings.collect { settings ->
                _uiState.update {
                    SettingsUiState(
                        confidenceThreshold = settings.confidenceThreshold,
                        maxDetections = settings.maxDetections,
                        showConfidencePercent = settings.showConfidencePercent
                    )
                }
            }
        }
    }

    fun onConfidenceChanged(value: Float) {
        viewModelScope.launch {
            userSettingsRepository.setConfidenceThreshold(value)
        }
    }

    fun onMaxDetectionsChanged(value: Int) {
        viewModelScope.launch {
            userSettingsRepository.setMaxDetections(value)
        }
    }

    fun onShowConfidenceChanged(value: Boolean) {
        viewModelScope.launch {
            userSettingsRepository.setShowConfidencePercent(value)
        }
    }
}
