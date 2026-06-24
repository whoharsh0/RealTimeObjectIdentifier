package com.rudra.objectidentifier.data.repository

import com.rudra.objectidentifier.domain.model.DetectedObject
import com.rudra.objectidentifier.domain.repository.DetectionRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@Singleton
class DetectionRepositoryImpl @Inject constructor() : DetectionRepository {

    private val detections = MutableStateFlow<List<DetectedObject>>(emptyList())
    private var isRunning = false

    override fun observeDetections(): Flow<List<DetectedObject>> = detections.asStateFlow()

    override suspend fun startDetection() {
        isRunning = true
        // Stub: real detections will come from CameraX + TFLite in Phase 4/5.
        detections.value = emptyList()
    }

    override suspend fun stopDetection() {
        isRunning = false
        detections.value = emptyList()
    }
}
