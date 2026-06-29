package com.rudra.objectidentifier.data.detector

import com.rudra.objectidentifier.domain.model.BoundingBox
import com.rudra.objectidentifier.domain.model.DetectedObject

data class RawDetection(
    val label: String,
    val confidence: Float,
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float
)

object DetectionMapper {

    fun mapDetection(
        raw: RawDetection,
        imageWidth: Float,
        imageHeight: Float
    ): DetectedObject? {
        if (imageWidth <= 0f || imageHeight <= 0f) return null
        if (raw.label.isBlank()) return null

        return DetectedObject(
            label = raw.label,
            confidence = raw.confidence,
            boundingBox = normalizeBoundingBox(
                left = raw.left,
                top = raw.top,
                right = raw.right,
                bottom = raw.bottom,
                imageWidth = imageWidth,
                imageHeight = imageHeight
            )
        )
    }

    fun mapDetections(
        rawDetections: List<RawDetection>,
        imageWidth: Float,
        imageHeight: Float
    ): List<DetectedObject> {
        return rawDetections.mapNotNull { mapDetection(it, imageWidth, imageHeight) }
    }

    fun normalizeBoundingBox(
        left: Float,
        top: Float,
        right: Float,
        bottom: Float,
        imageWidth: Float,
        imageHeight: Float
    ): BoundingBox {
        return BoundingBox(
            left = left / imageWidth,
            top = top / imageHeight,
            right = right / imageWidth,
            bottom = bottom / imageHeight
        )
    }
}
