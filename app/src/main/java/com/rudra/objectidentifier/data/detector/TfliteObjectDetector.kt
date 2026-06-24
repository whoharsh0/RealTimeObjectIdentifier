package com.rudra.objectidentifier.data.detector

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import com.rudra.objectidentifier.domain.model.BoundingBox
import com.rudra.objectidentifier.domain.model.DetectedObject
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.vision.detector.ObjectDetector

@Singleton
class TfliteObjectDetector @Inject constructor(
    @ApplicationContext private val context: Context
) : AutoCloseable {

    private var detector: ObjectDetector? = null

    private fun getDetector(): ObjectDetector {
        return detector ?: ObjectDetector.createFromFileAndOptions(
            context,
            MODEL_FILE,
            ObjectDetector.ObjectDetectorOptions.builder()
                .setMaxResults(5)
                .setScoreThreshold(0.45f)
                .build()
        ).also { detector = it }
    }

    fun detect(bitmap: Bitmap, rotationDegrees: Int): List<DetectedObject> {
        val rotated = rotateBitmap(bitmap, rotationDegrees)
        val imageWidth = rotated.width.toFloat()
        val imageHeight = rotated.height.toFloat()
        val tensorImage = TensorImage.fromBitmap(rotated)
        val results = getDetector().detect(tensorImage)

        if (rotated !== bitmap) {
            rotated.recycle()
        }

        return results.mapNotNull { detection ->
            val category = detection.categories.firstOrNull() ?: return@mapNotNull null
            val box = detection.boundingBox
            DetectedObject(
                label = category.label,
                confidence = category.score,
                boundingBox = BoundingBox(
                    left = box.left / imageWidth,
                    top = box.top / imageHeight,
                    right = box.right / imageWidth,
                    bottom = box.bottom / imageHeight
                )
            )
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
    }

    companion object {
        private const val MODEL_FILE = "efficientdet_lite0.tflite"
    }
}
