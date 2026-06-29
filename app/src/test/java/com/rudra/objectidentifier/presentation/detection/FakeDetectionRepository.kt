package com.rudra.objectidentifier.presentation.detection

import com.rudra.objectidentifier.domain.model.DetectedObject
import com.rudra.objectidentifier.domain.model.DetectionMetrics
import com.rudra.objectidentifier.domain.model.DisplayDetection
import com.rudra.objectidentifier.domain.repository.DetectionRepository
import com.rudra.objectidentifier.domain.repository.ScanHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeDetectionRepository : DetectionRepository {
    private val detections = MutableStateFlow<List<DetectedObject>>(emptyList())
    private val displayDetections = MutableStateFlow<List<DisplayDetection>>(emptyList())
    private val metrics = MutableStateFlow(DetectionMetrics())
    private val frozen = MutableStateFlow(false)
    private var running = false

    override fun observeDetections(): Flow<List<DetectedObject>> = detections.asStateFlow()
    override fun observeDisplayDetections(): Flow<List<DisplayDetection>> = displayDetections.asStateFlow()
    override fun observeMetrics(): Flow<DetectionMetrics> = metrics.asStateFlow()
    override fun observeIsFrozen(): Flow<Boolean> = frozen.asStateFlow()

    override suspend fun startDetection() {
        running = true
        frozen.value = false
        detections.value = emptyList()
    }

    override suspend fun stopDetection() {
        running = false
        frozen.value = false
        detections.value = emptyList()
    }

    override suspend fun toggleFreeze() {
        frozen.value = !frozen.value
    }

    override suspend fun setHighlightedTrack(trackId: Int?) = Unit
    override fun currentDetections(): List<DetectedObject> = detections.value
    override fun isDetecting(): Boolean = running
}

class FakeScanHistoryRepository : ScanHistoryRepository {
    override fun observeHistory() = MutableStateFlow(emptyList<com.rudra.objectidentifier.domain.model.ScanHistoryEntry>()).asStateFlow()
    override suspend fun recordScan(topLabels: List<String>, detectionCount: Int, scanModeName: String) = Unit
    override suspend fun clearHistory() = Unit
    override suspend fun entryCount(): Int = 0
}
