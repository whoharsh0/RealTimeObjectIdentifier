package com.rudra.objectidentifier.data.detector

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.rudra.objectidentifier.domain.model.BoundingBox
import com.rudra.objectidentifier.domain.model.OcrLine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine

@Singleton
class OcrScanner @Inject constructor() {

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    suspend fun scan(bitmap: Bitmap): List<OcrLine> {
        val w = bitmap.width.toFloat()
        val h = bitmap.height.toFloat()
        val image = InputImage.fromBitmap(bitmap, 0)
        return suspendCancellableCoroutine { cont ->
            recognizer.process(image)
                .addOnSuccessListener { result ->
                    val lines = result.textBlocks.flatMap { block ->
                        block.lines.mapNotNull { line ->
                            val rect = line.boundingBox ?: return@mapNotNull null
                            if (line.text.isBlank()) return@mapNotNull null
                            OcrLine(
                                text = line.text.trim(),
                                boundingBox = BoundingBox(
                                    left = (rect.left / w).coerceIn(0f, 1f),
                                    top = (rect.top / h).coerceIn(0f, 1f),
                                    right = (rect.right / w).coerceIn(0f, 1f),
                                    bottom = (rect.bottom / h).coerceIn(0f, 1f)
                                )
                            )
                        }
                    }
                    if (cont.isActive) cont.resume(lines)
                }
                .addOnFailureListener {
                    if (cont.isActive) cont.resume(emptyList())
                }
        }
    }

    fun close() = recognizer.close()
}
