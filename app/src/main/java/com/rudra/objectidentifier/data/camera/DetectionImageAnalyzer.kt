package com.rudra.objectidentifier.data.camera

import android.graphics.Bitmap
import androidx.camera.core.ImageProxy
import com.rudra.objectidentifier.core.AppLog
import com.rudra.objectidentifier.data.detector.BarcodeScanner
import com.rudra.objectidentifier.data.detector.DetectionMetricsTracker
import com.rudra.objectidentifier.data.detector.OcrScanner
import com.rudra.objectidentifier.data.detector.TfliteObjectDetector
import com.rudra.objectidentifier.di.IoDispatcher
import com.rudra.objectidentifier.domain.repository.DetectionResultPublisher
import com.rudra.objectidentifier.domain.repository.UserSettingsRepository
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

@Singleton
class DetectionImageAnalyzer @Inject constructor(
    private val objectDetector: TfliteObjectDetector,
    private val barcodeScanner: BarcodeScanner,
    private val ocrScanner: OcrScanner,
    private val detectionResultPublisher: DetectionResultPublisher,
    private val userSettingsRepository: UserSettingsRepository,
    private val metricsTracker: DetectionMetricsTracker,
    @IoDispatcher ioDispatcher: CoroutineDispatcher
) : androidx.camera.core.ImageAnalysis.Analyzer {

    private val scope = CoroutineScope(ioDispatcher + SupervisorJob())
    private val isProcessing = AtomicBoolean(false)
    private val lastProcessedAt = AtomicLong(0L)

    @androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
    override fun analyze(image: ImageProxy) {
        val settings = userSettingsRepository.currentSettings()
        if (!shouldProcessFrame(settings.batterySaverMode, settings.enableFrameSkip)) {
            metricsTracker.recordSkippedFrame()
            image.close()
            return
        }

        if (!isProcessing.compareAndSet(false, true)) {
            metricsTracker.recordSkippedFrame()
            image.close()
            return
        }

        val bitmap: Bitmap
        val rotation: Int
        try {
            bitmap = image.toBitmap()
            rotation = image.imageInfo.rotationDegrees
        } catch (e: Exception) {
            AppLog.rateLimited("frame-bitmap-failure", TAG, "Failed to decode frame", e)
            isProcessing.set(false)
            image.close()
            return
        }
        // Close immediately — bitmap has the copied pixels, image is no longer needed
        image.close()

        scope.launch {
            try {
                // Run TFLite + ML Kit in parallel
                val tfDeferred = async {
                    objectDetector.detect(bitmap, rotation)
                }
                val barcodeDeferred = async {
                    if (settings.enableBarcodeScanning) barcodeScanner.scan(bitmap)
                    else emptyList()
                }
                val ocrDeferred = async {
                    if (settings.enableOcrMode) ocrScanner.scan(bitmap)
                    else emptyList()
                }

                val detections = tfDeferred.await()
                val barcodes = barcodeDeferred.await()
                val ocrLines = ocrDeferred.await()

                detectionResultPublisher.publishDetections(detections)
                detectionResultPublisher.publishBarcodeResults(barcodes)
                detectionResultPublisher.publishOcrLines(ocrLines)
                detectionResultPublisher.publishMetrics(metricsTracker.snapshot())
            } catch (error: Exception) {
                AppLog.rateLimited("frame-analysis-failure", TAG, "Frame analysis failed", error)
                detectionResultPublisher.publishDetections(emptyList())
            } finally {
                bitmap.recycle()
                isProcessing.set(false)
            }
        }
    }

    private fun shouldProcessFrame(batterySaver: Boolean, frameSkipEnabled: Boolean): Boolean {
        val minIntervalMs = when {
            batterySaver -> 250L
            frameSkipEnabled -> 66L
            else -> 0L
        }
        if (minIntervalMs == 0L) return true
        val now = System.currentTimeMillis()
        val last = lastProcessedAt.get()
        if (now - last < minIntervalMs) return false
        lastProcessedAt.set(now)
        return true
    }

    companion object {
        private const val TAG = "DetectionImageAnalyzer"
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
