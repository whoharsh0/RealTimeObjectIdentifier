package com.rudra.objectidentifier.data.detector

import android.graphics.Bitmap
import com.rudra.objectidentifier.core.AppLog
import com.rudra.objectidentifier.domain.model.DetectedObject
import com.rudra.objectidentifier.domain.model.UserSettings
import com.rudra.objectidentifier.domain.repository.StillImageDetector
import com.rudra.objectidentifier.domain.repository.UserSettingsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GalleryImageDetector @Inject constructor(
    private val objectDetector: TfliteObjectDetector,
    private val userSettingsRepository: UserSettingsRepository
) : StillImageDetector {

    override fun detect(bitmap: Bitmap?): Result<List<DetectedObject>> {
        if (bitmap == null) {
            return Result.failure(IllegalArgumentException("Image could not be loaded"))
        }
        if (bitmap.width <= 0 || bitmap.height <= 0) {
            bitmap.recycle()
            return Result.failure(IllegalArgumentException("Invalid image dimensions"))
        }

        val settings = userSettingsRepository.currentSettings()
        val stillThreshold = settings.scanMode
            .confidencePreset(settings.confidenceThreshold)
            .minus(STILL_IMAGE_THRESHOLD_DELTA)
            .coerceAtLeast(UserSettings.MIN_CONFIDENCE)
        val stillMaxResults = settings.maxDetections
            .coerceAtMost(UserSettings.MAX_MAX_DETECTIONS)

        return runCatching {
            objectDetector.detect(
                bitmap = bitmap,
                rotationDegrees = 0,
                scoreThresholdOverride = stillThreshold,
                maxResultsOverride = stillMaxResults
            )
        }.onFailure { error ->
            AppLog.e(TAG, "Still-image detection failed", error)
        }.also {
            if (!bitmap.isRecycled) bitmap.recycle()
        }
    }

    companion object {
        private const val TAG = "GalleryImageDetector"
        /** Slightly lower threshold for still images — latency is not a concern. */
        private const val STILL_IMAGE_THRESHOLD_DELTA = 0.05f
    }
}
