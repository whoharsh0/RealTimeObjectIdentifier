package com.rudra.objectidentifier.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rudra.objectidentifier.domain.repository.ScanHistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val scanHistoryRepository: ScanHistoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            scanHistoryRepository.observeHistory().collect { entries ->
                _uiState.update { it.copy(entries = entries, isLoading = false) }
            }
        }
    }

    fun onClearHistory() {
        viewModelScope.launch { scanHistoryRepository.clearHistory() }
    }
}
