package com.rudra.objectidentifier.domain.repository

import com.rudra.objectidentifier.domain.model.BarcodeResult
import com.rudra.objectidentifier.domain.model.DetectedObject
import com.rudra.objectidentifier.domain.model.DetectionMetrics
import com.rudra.objectidentifier.domain.model.OcrLine

interface DetectionResultPublisher {
    fun publishDetections(detections: List<DetectedObject>)
    fun publishMetrics(snapshot: DetectionMetrics) {}
    fun publishBarcodeResults(results: List<BarcodeResult>) {}
    fun publishOcrLines(lines: List<OcrLine>) {}
}
