package com.rudra.objectidentifier.data.detector

import com.rudra.objectidentifier.domain.model.DetectionMetrics
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToInt

@Singleton
class DetectionMetricsTracker @Inject constructor() {

    private var frameTimestamps = ArrayDeque<Long>()
    private var lastInferenceMs = 0L
    private var framesProcessed = 0L
    private var framesSkipped = 0L
    private var delegateName = "CPU"

    fun recordInference(durationMs: Long) {
        lastInferenceMs = durationMs.coerceAtLeast(0L)
        framesProcessed++
        val now = System.currentTimeMillis()
        frameTimestamps.addLast(now)
        while (frameTimestamps.size > FPS_WINDOW) {
            frameTimestamps.removeFirst()
        }
    }

    fun recordSkippedFrame() {
        framesSkipped++
    }

    fun setDelegateName(name: String) {
        delegateName = name.ifBlank { "CPU" }
    }

    fun snapshot(): DetectionMetrics {
        val fps = if (frameTimestamps.size < 2) {
            0f
        } else {
            val elapsed = (frameTimestamps.last() - frameTimestamps.first()).coerceAtLeast(1L)
            ((frameTimestamps.size - 1) * 1000f / elapsed * 10).roundToInt() / 10f
        }
        return DetectionMetrics(
            fps = fps,
            inferenceMs = lastInferenceMs,
            delegateName = delegateName,
            framesProcessed = framesProcessed,
            framesSkipped = framesSkipped
        )
    }

    fun reset() {
        frameTimestamps.clear()
        lastInferenceMs = 0L
        framesProcessed = 0L
        framesSkipped = 0L
    }

    companion object {
        private const val FPS_WINDOW = 30
    }
}
