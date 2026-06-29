package com.rudra.objectidentifier.data.repository

import com.rudra.objectidentifier.data.detector.DetectionSmoother
import com.rudra.objectidentifier.domain.model.BarcodeResult
import com.rudra.objectidentifier.domain.model.DetectedObject
import com.rudra.objectidentifier.domain.model.DetectionMetrics
import com.rudra.objectidentifier.domain.model.DisplayDetection
import com.rudra.objectidentifier.domain.model.OcrLine
import com.rudra.objectidentifier.domain.repository.DetectionRepository
import com.rudra.objectidentifier.domain.repository.DetectionResultPublisher
import com.rudra.objectidentifier.domain.repository.UserSettingsRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine

@Singleton
class DetectionRepositoryImpl @Inject constructor(
    private val detectionSmoother: DetectionSmoother,
    private val userSettingsRepository: UserSettingsRepository
) : DetectionRepository, DetectionResultPublisher {

    private val rawDetections = MutableStateFlow<List<DetectedObject>>(emptyList())
    private val isRunning = MutableStateFlow(false)
    private val isFrozen = MutableStateFlow(false)
    private val highlightedTrackId = MutableStateFlow<Int?>(null)
    private val metrics = MutableStateFlow(DetectionMetrics())
    private val barcodeResults = MutableStateFlow<List<BarcodeResult>>(emptyList())
    private val ocrLines = MutableStateFlow<List<OcrLine>>(emptyList())

    override fun observeDetections(): Flow<List<DetectedObject>> {
        return combine(rawDetections, isRunning, isFrozen) { detections, running, frozen ->
            if (running && !frozen) detections else if (frozen) detections else emptyList()
        }
    }

    override fun observeDisplayDetections(): Flow<List<DisplayDetection>> {
        return combine(
            rawDetections,
            isRunning,
            isFrozen,
            highlightedTrackId,
            userSettingsRepository.settings
        ) { detections, running, frozen, highlighted, settings ->
            if (!running && !frozen) {
                emptyList()
            } else {
                val source = if (frozen) rawDetections.value else detections
                detectionSmoother.smooth(
                    current = source,
                    enabled = settings.enableSmoothing,
                    minBoxSizePercent = settings.minBoxSizePercent,
                    minConfidenceForLabel = settings.minConfidenceForLabel,
                    filterLabel = settings.filterLabel,
                    highlightedTrackId = highlighted,
                    highContrast = settings.highContrastMode
                )
            }
        }
    }

    override fun observeMetrics(): Flow<DetectionMetrics> = metrics.asStateFlow()
    override fun observeIsFrozen(): Flow<Boolean> = isFrozen.asStateFlow()
    override fun observeBarcodeResults(): Flow<List<BarcodeResult>> = barcodeResults.asStateFlow()
    override fun observeOcrLines(): Flow<List<OcrLine>> = ocrLines.asStateFlow()

    override suspend fun startDetection() {
        isRunning.value = true
        isFrozen.value = false
        highlightedTrackId.value = null
        rawDetections.value = emptyList()
        barcodeResults.value = emptyList()
        ocrLines.value = emptyList()
        detectionSmoother.reset()
    }

    override suspend fun stopDetection() {
        isRunning.value = false
        isFrozen.value = false
        highlightedTrackId.value = null
        rawDetections.value = emptyList()
        barcodeResults.value = emptyList()
        ocrLines.value = emptyList()
        detectionSmoother.reset()
    }

    override suspend fun toggleFreeze() {
        if (!isRunning.value) return
        isFrozen.value = !isFrozen.value
    }

    override suspend fun setHighlightedTrack(trackId: Int?) {
        highlightedTrackId.value = trackId
    }

    override fun publishMetrics(snapshot: DetectionMetrics) {
        metrics.value = snapshot
    }

    override fun publishDetections(detections: List<DetectedObject>) {
        if (isRunning.value && !isFrozen.value) {
            rawDetections.value = detections
        }
    }

    override fun publishBarcodeResults(results: List<BarcodeResult>) {
        if (isRunning.value && !isFrozen.value) {
            barcodeResults.value = results
        }
    }

    override fun publishOcrLines(lines: List<OcrLine>) {
        if (isRunning.value && !isFrozen.value) {
            ocrLines.value = lines
        }
    }

    override fun currentDetections(): List<DetectedObject> = rawDetections.value
    override fun isDetecting(): Boolean = isRunning.value
}
