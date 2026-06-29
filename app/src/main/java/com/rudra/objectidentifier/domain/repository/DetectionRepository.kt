package com.rudra.objectidentifier.domain.repository

import com.rudra.objectidentifier.domain.model.BarcodeResult
import com.rudra.objectidentifier.domain.model.DetectedObject
import com.rudra.objectidentifier.domain.model.DetectionMetrics
import com.rudra.objectidentifier.domain.model.DisplayDetection
import com.rudra.objectidentifier.domain.model.OcrLine
import kotlinx.coroutines.flow.Flow

interface DetectionRepository {
    fun observeDetections(): Flow<List<DetectedObject>>
    fun observeDisplayDetections(): Flow<List<DisplayDetection>>
    fun observeMetrics(): Flow<DetectionMetrics>
    fun observeIsFrozen(): Flow<Boolean>
    fun observeBarcodeResults(): Flow<List<BarcodeResult>>
    fun observeOcrLines(): Flow<List<OcrLine>>
    suspend fun startDetection()
    suspend fun stopDetection()
    suspend fun toggleFreeze()
    suspend fun setHighlightedTrack(trackId: Int?)
    fun currentDetections(): List<DetectedObject>
    fun isDetecting(): Boolean
}
