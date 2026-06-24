package com.rudra.objectidentifier.data.camera

import android.graphics.Bitmap
import androidx.camera.core.ImageProxy
import com.rudra.objectidentifier.data.detector.TfliteObjectDetector
import com.rudra.objectidentifier.domain.repository.DetectionResultPublisher
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DetectionImageAnalyzer @Inject constructor(
    private val objectDetector: TfliteObjectDetector,
    private val detectionResultPublisher: DetectionResultPublisher
) : androidx.camera.core.ImageAnalysis.Analyzer {

    private val isProcessing = AtomicBoolean(false)

    @androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
    override fun analyze(image: ImageProxy) {
        if (!isProcessing.compareAndSet(false, true)) {
            image.close()
            return
        }

        try {
            val bitmap = image.toBitmap()
            val rotation = image.imageInfo.rotationDegrees
            val detections = objectDetector.detect(bitmap, rotation)
            detectionResultPublisher.publishDetections(detections)
            bitmap.recycle()
        } catch (_: Exception) {
            detectionResultPublisher.publishDetections(emptyList())
        } finally {
            isProcessing.set(false)
            image.close()
        }
    }
}

@androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
private fun ImageProxy.toBitmap(): Bitmap {
    val plane = planes[0]
    val buffer = plane.buffer
    buffer.rewind()
    val pixelStride = plane.pixelStride
    val rowStride = plane.rowStride
    val rowPadding = rowStride - pixelStride * width

    val bitmap = Bitmap.createBitmap(
        width + rowPadding / pixelStride,
        height,
        Bitmap.Config.ARGB_8888
    )
    bitmap.copyPixelsFromBuffer(buffer)
    return if (rowPadding == 0) {
        bitmap
    } else {
        Bitmap.createBitmap(bitmap, 0, 0, width, height).also {
            if (it !== bitmap) bitmap.recycle()
        }
    }
}
