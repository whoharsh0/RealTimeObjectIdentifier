package com.rudra.objectidentifier.data.detector

import com.rudra.objectidentifier.domain.model.BoundingBox
import com.rudra.objectidentifier.domain.model.DetectedObject
import com.rudra.objectidentifier.domain.model.DisplayDetection
import com.rudra.objectidentifier.domain.util.LabelFormatter
import com.rudra.objectidentifier.domain.util.ObjectCategory
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max

@Singleton
class DetectionSmoother @Inject constructor() {

    private var previousTracks: List<InternalTrack> = emptyList()
    private var nextTrackId = 1

    fun smooth(
        current: List<DetectedObject>,
        enabled: Boolean,
        minBoxSizePercent: Float,
        minConfidenceForLabel: Float,
        filterLabel: String,
        highlightedTrackId: Int?,
        highContrast: Boolean
    ): List<DisplayDetection> {
        val filtered = current.filter { detection ->
            detection.confidence >= minConfidenceForLabel &&
                detection.boundingBox.width >= minBoxSizePercent &&
                detection.boundingBox.height >= minBoxSizePercent &&
                LabelFormatter.matchesFilter(detection.label, filterLabel)
        }

        if (!enabled || filtered.isEmpty()) {
            previousTracks = filtered.mapIndexed { index, detection ->
                InternalTrack(
                    trackId = index + 1,
                    detection = detection
                )
            }
            nextTrackId = max(nextTrackId, previousTracks.size + 1)
            return filtered.mapIndexed { index, detection ->
                toDisplay(
                    detection = detection,
                    trackId = index + 1,
                    highlightedTrackId = highlightedTrackId,
                    highContrast = highContrast
                )
            }
        }

        val matchedPrevious = BooleanArray(previousTracks.size)
        val output = mutableListOf<InternalTrack>()

        filtered.forEach { detection ->
            val bestMatch = previousTracks.withIndex()
                .filter { (index, _) -> !matchedPrevious[index] }
                .maxByOrNull { (_, track) ->
                    iou(track.detection.boundingBox, detection.boundingBox)
                }

            val matchIndex = bestMatch?.takeIf { (_, track) ->
                iou(track.detection.boundingBox, detection.boundingBox) >= IOU_THRESHOLD
            }?.index

            val track = if (matchIndex != null) {
                matchedPrevious[matchIndex] = true
                val previous = previousTracks[matchIndex]
                InternalTrack(
                    trackId = previous.trackId,
                    detection = blend(previous.detection, detection)
                )
            } else {
                InternalTrack(
                    trackId = nextTrackId++,
                    detection = detection
                )
            }
            output += track
        }

        previousTracks = output
        return output.map { track ->
            toDisplay(
                detection = track.detection,
                trackId = track.trackId,
                highlightedTrackId = highlightedTrackId,
                highContrast = highContrast
            )
        }
    }

    fun reset() {
        previousTracks = emptyList()
        nextTrackId = 1
    }

    private fun blend(previous: DetectedObject, current: DetectedObject): DetectedObject {
        val alpha = 0.65f
        val prevBox = previous.boundingBox
        val currBox = current.boundingBox
        return DetectedObject(
            label = current.label,
            confidence = previous.confidence * (1f - alpha) + current.confidence * alpha,
            boundingBox = BoundingBox(
                left = lerp(prevBox.left, currBox.left, alpha),
                top = lerp(prevBox.top, currBox.top, alpha),
                right = lerp(prevBox.right, currBox.right, alpha),
                bottom = lerp(prevBox.bottom, currBox.bottom, alpha)
            )
        )
    }

    private fun toDisplay(
        detection: DetectedObject,
        trackId: Int,
        highlightedTrackId: Int?,
        highContrast: Boolean
    ): DisplayDetection {
        val color = ObjectCategory.colorArgbForLabel(detection.label, highContrast)
        return DisplayDetection(
            detection = detection,
            trackId = trackId,
            displayLabel = LabelFormatter.format(detection.label),
            categoryColorArgb = color,
            isHighlighted = highlightedTrackId == trackId
        )
    }

    private fun lerp(start: Float, end: Float, amount: Float): Float {
        return start + (end - start) * amount
    }

    private fun iou(a: BoundingBox, b: BoundingBox): Float {
        val left = max(a.left, b.left)
        val top = max(a.top, b.top)
        val right = minOf(a.right, b.right)
        val bottom = minOf(a.bottom, b.bottom)
        val intersection = max(0f, right - left) * max(0f, bottom - top)
        val union = a.width * a.height + b.width * b.height - intersection
        if (union <= 0f) return 0f
        return intersection / union
    }

    private data class InternalTrack(
        val trackId: Int,
        val detection: DetectedObject
    )

    companion object {
        private const val IOU_THRESHOLD = 0.35f
    }
}
