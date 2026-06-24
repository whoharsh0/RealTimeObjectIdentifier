package com.rudra.objectidentifier.domain.repository

import com.rudra.objectidentifier.domain.model.DetectedObject

interface DetectionResultPublisher {
    fun publishDetections(detections: List<DetectedObject>)
}
