package com.rudra.objectidentifier.data.detector

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import com.rudra.objectidentifier.core.AppLog
import com.rudra.objectidentifier.domain.model.DetectedObject
import com.rudra.objectidentifier.domain.model.InferenceDelegate
import com.rudra.objectidentifier.domain.model.ModelVariant
import com.rudra.objectidentifier.domain.repository.UserSettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.vision.detector.ObjectDetector

@Singleton
class TfliteObjectDetector @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userSettingsRepository: UserSettingsRepository,
    private val metricsTracker: DetectionMetricsTracker
) : AutoCloseable {

    private var detector: ObjectDetector? = null
    private var activeThreshold: Float? = null
    private var activeMaxResults: Int? = null
    private var activeModelFile: String? = null
    private var activeDelegate: InferenceDelegate? = null

    fun warmUp() {
        runCatching {
            val bitmap = Bitmap.createBitmap(64, 64, Bitmap.Config.ARGB_8888)
            detect(bitmap, rotationDegrees = 0)
            bitmap.recycle()
        }.onFailure { error ->
            AppLog.w(TAG, "Detector warm-up failed", error)
        }
    }

    fun detect(
        bitmap: Bitmap,
        rotationDegrees: Int,
        scoreThresholdOverride: Float? = null,
        maxResultsOverride: Int? = null
    ): List<DetectedObject> {
        val settings = userSettingsRepository.currentSettings()
        val threshold = scoreThresholdOverride
            ?: settings.scanMode.confidencePreset(settings.confidenceThreshold)
        val maxResults = maxResultsOverride ?: settings.maxDetections
        val rotated = rotateBitmap(bitmap, rotationDegrees)
        val imageWidth = rotated.width.toFloat()
        val imageHeight = rotated.height.toFloat()
        val tensorImage = TensorImage.fromBitmap(rotated)

        val startedAt = System.currentTimeMillis()
        val results = runCatching {
            getDetector(
                threshold = threshold,
                maxResults = maxResults,
                modelVariant = settings.modelVariant,
                delegate = settings.inferenceDelegate
            ).detect(tensorImage)
        }.getOrElse { error ->
            AppLog.rateLimited(
                key = "tflite-inference-failure",
                tag = TAG,
                message = "Detection inference failed",
                throwable = error
            )
            emptyList()
        }
        metricsTracker.recordInference(System.currentTimeMillis() - startedAt)

        if (rotated !== bitmap) rotated.recycle()

        return DetectionMapper.mapDetections(
            rawDetections = results.mapNotNull { detection ->
                val category = detection.categories.firstOrNull() ?: return@mapNotNull null
                val box = detection.boundingBox
                RawDetection(
                    label = category.label,
                    confidence = category.score,
                    left = box.left,
                    top = box.top,
                    right = box.right,
                    bottom = box.bottom
                )
            },
            imageWidth = imageWidth,
            imageHeight = imageHeight
        )
    }

    private fun getDetector(
        threshold: Float,
        maxResults: Int,
        modelVariant: ModelVariant,
        delegate: InferenceDelegate
    ): ObjectDetector {
        val modelFile = resolveModelFile(modelVariant)
        if (
            detector != null &&
            activeThreshold == threshold &&
            activeMaxResults == maxResults &&
            activeModelFile == modelFile &&
            activeDelegate == delegate
        ) {
            return detector!!
        }

        detector?.close()

        val baseOptionsBuilder = BaseOptions.builder().setNumThreads(4)
        when (delegate) {
            InferenceDelegate.NNAPI -> {
                runCatching { baseOptionsBuilder.useNnapi() }
                    .onSuccess { metricsTracker.setDelegateName("NNAPI") }
                    .onFailure { metricsTracker.setDelegateName("CPU") }
            }
            InferenceDelegate.GPU -> {
                runCatching { baseOptionsBuilder.useGpu() }
                    .onSuccess { metricsTracker.setDelegateName("GPU") }
                    .onFailure {
                        AppLog.w(TAG, "GPU delegate unavailable, falling back to CPU")
                        metricsTracker.setDelegateName("CPU")
                    }
            }
            InferenceDelegate.CPU -> {
                metricsTracker.setDelegateName("CPU")
            }
        }

        val options = ObjectDetector.ObjectDetectorOptions.builder()
            .setBaseOptions(baseOptionsBuilder.build())
            .setMaxResults(maxResults)
            .setScoreThreshold(threshold)
            .build()

        return runCatching {
            ObjectDetector.createFromFileAndOptions(context, modelFile, options)
        }.getOrElse { error ->
            AppLog.e(TAG, "Failed to load model $modelFile, falling back to $FALLBACK_MODEL_FILE", error)
            metricsTracker.setDelegateName("CPU")
            ObjectDetector.createFromFileAndOptions(
                context,
                FALLBACK_MODEL_FILE,
                ObjectDetector.ObjectDetectorOptions.builder()
                    .setMaxResults(maxResults)
                    .setScoreThreshold(threshold)
                    .build()
            )
        }.also {
            detector = it
            activeThreshold = threshold
            activeMaxResults = maxResults
            activeModelFile = modelFile
            activeDelegate = delegate
        }
    }

    private fun resolveModelFile(modelVariant: ModelVariant): String {
        val candidate = modelVariant.assetFile
        return runCatching {
            context.assets.open(candidate).close()
            candidate
        }.getOrElse {
            AppLog.d(TAG, "Model asset '$candidate' unavailable, using $FALLBACK_MODEL_FILE")
            FALLBACK_MODEL_FILE
        }
    }

    private fun rotateBitmap(bitmap: Bitmap, rotationDegrees: Int): Bitmap {
        if (rotationDegrees == 0) return bitmap
        val matrix = Matrix().apply { postRotate(rotationDegrees.toFloat()) }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    override fun close() {
        detector?.close()
        detector = null
        activeThreshold = null
        activeMaxResults = null
        activeModelFile = null
        activeDelegate = null
    }

    companion object {
        private const val TAG = "TfliteObjectDetector"
        private const val FALLBACK_MODEL_FILE = "efficientdet_lite0.tflite"
    }
}
