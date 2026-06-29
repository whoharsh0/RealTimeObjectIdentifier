package com.rudra.objectidentifier.data.history

import com.rudra.objectidentifier.core.AppLog
import com.rudra.objectidentifier.data.local.ScanHistoryDao
import com.rudra.objectidentifier.data.local.ScanHistoryEntity
import com.rudra.objectidentifier.domain.model.ScanHistoryEntry
import com.rudra.objectidentifier.domain.repository.ScanHistoryRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

@Singleton
class ScanHistoryRepositoryImpl @Inject constructor(
    private val dao: ScanHistoryDao
) : ScanHistoryRepository {

    override fun observeHistory(): Flow<List<ScanHistoryEntry>> {
        return dao.observeRecent()
            .map { entries -> entries.map { it.toDomain() } }
            .catch { error ->
                AppLog.e(TAG, "Failed to observe scan history", error)
                emit(emptyList())
            }
    }

    override suspend fun recordScan(
        topLabels: List<String>,
        detectionCount: Int,
        scanModeName: String
    ) {
        if (detectionCount <= 0 || topLabels.isEmpty()) return
        val labels = topLabels
            .filter { it.isNotBlank() }
            .distinct()
            .take(8)
            .joinToString(", ")
        if (labels.isBlank()) return

        dao.insert(
            ScanHistoryEntity(
                timestampMillis = System.currentTimeMillis(),
                topLabels = labels,
                detectionCount = detectionCount.coerceAtLeast(0),
                scanMode = scanModeName.ifBlank { "GENERAL" }
            )
        )
    }

    override suspend fun clearHistory() {
        dao.clearAll()
    }

    override suspend fun entryCount(): Int = dao.count()

    companion object {
        private const val TAG = "ScanHistoryRepo"
    }
}
