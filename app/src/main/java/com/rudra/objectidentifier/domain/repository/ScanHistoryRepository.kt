package com.rudra.objectidentifier.domain.repository

import com.rudra.objectidentifier.domain.model.ScanHistoryEntry
import kotlinx.coroutines.flow.Flow

interface ScanHistoryRepository {
    fun observeHistory(): Flow<List<ScanHistoryEntry>>
    suspend fun recordScan(topLabels: List<String>, detectionCount: Int, scanModeName: String)
    suspend fun clearHistory()
    suspend fun entryCount(): Int
}
