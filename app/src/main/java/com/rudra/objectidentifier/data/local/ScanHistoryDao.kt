package com.rudra.objectidentifier.data.local

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import com.rudra.objectidentifier.domain.model.ScanHistoryEntry
import com.rudra.objectidentifier.domain.model.ScanMode
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "scan_history")
data class ScanHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val timestampMillis: Long,
    val topLabels: String,
    val detectionCount: Int,
    val scanMode: String
) {
    fun toDomain(): ScanHistoryEntry = ScanHistoryEntry(
        id = id,
        timestampMillis = timestampMillis,
        topLabels = topLabels,
        detectionCount = detectionCount,
        scanMode = runCatching { ScanMode.valueOf(scanMode) }.getOrDefault(ScanMode.GENERAL)
    )
}

@Dao
interface ScanHistoryDao {
    @Query("SELECT * FROM scan_history ORDER BY timestampMillis DESC LIMIT 200")
    fun observeRecent(): Flow<List<ScanHistoryEntity>>

    @Insert
    suspend fun insert(entry: ScanHistoryEntity): Long

    @Query("DELETE FROM scan_history")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM scan_history")
    suspend fun count(): Int
}
