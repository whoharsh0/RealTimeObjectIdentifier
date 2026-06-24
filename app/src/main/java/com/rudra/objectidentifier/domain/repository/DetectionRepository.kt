package com.rudra.objectidentifier.domain.repository

import com.rudra.objectidentifier.domain.model.DetectedObject
import kotlinx.coroutines.flow.Flow

interface DetectionRepository {

    fun observeDetections(): Flow<List<DetectedObject>>

    suspend fun startDetection()

    suspend fun stopDetection()
}
