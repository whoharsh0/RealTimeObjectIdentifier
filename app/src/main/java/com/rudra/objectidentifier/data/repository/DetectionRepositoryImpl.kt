package com.rudra.objectidentifier.data.repository

import com.rudra.objectidentifier.domain.model.DetectedObject
import com.rudra.objectidentifier.domain.repository.DetectionRepository
import com.rudra.objectidentifier.domain.repository.DetectionResultPublisher
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@Singleton
class DetectionRepositoryImpl @Inject constructor() :
    DetectionRepository,
    DetectionResultPublisher {

    private val detections = MutableStateFlow<List<DetectedObject>>(emptyList())
    private var isRunning = false

    override fun observeDetections(): Flow<List<DetectedObject>> = detections.asStateFlow()

    override suspend fun startDetection() {
        isRunning = true
        detections.value = emptyList()
    }

    override suspend fun stopDetection() {
        isRunning = false
        detections.value = emptyList()
    }

    override fun publishDetections(detections: List<DetectedObject>) {
        if (isRunning) {
            this.detections.value = detections
        }
    }
}
