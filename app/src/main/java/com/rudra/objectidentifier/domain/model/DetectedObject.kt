package com.rudra.objectidentifier.domain.model

data class BoundingBox(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float
) {
    val width: Float get() = right - left
    val height: Float get() = bottom - top
}

data class DetectedObject(
    val label: String,
    val confidence: Float,
    val boundingBox: BoundingBox
) {
    val confidencePercent: Int get() = (confidence * 100).toInt()
}
