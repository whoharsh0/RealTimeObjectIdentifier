package com.rudra.objectidentifier.presentation.history

import com.rudra.objectidentifier.domain.model.ScanHistoryEntry

data class HistoryUiState(
    val entries: List<ScanHistoryEntry> = emptyList(),
    val isLoading: Boolean = true
)
