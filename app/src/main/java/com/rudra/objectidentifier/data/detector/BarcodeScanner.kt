package com.rudra.objectidentifier.data.detector

import android.graphics.Bitmap
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.rudra.objectidentifier.domain.model.BarcodeFormat
import com.rudra.objectidentifier.domain.model.BarcodeResult
import com.rudra.objectidentifier.domain.model.BoundingBox
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine

@Singleton
class BarcodeScanner @Inject constructor() {

    private val scanner = BarcodeScanning.getClient(
        BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
            .build()
    )

    suspend fun scan(bitmap: Bitmap): List<BarcodeResult> {
        val w = bitmap.width.toFloat()
        val h = bitmap.height.toFloat()
        val image = InputImage.fromBitmap(bitmap, 0)
        return suspendCancellableCoroutine { cont ->
            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    val results = barcodes.mapNotNull { it.toResult(w, h) }
                    if (cont.isActive) cont.resume(results)
                }
                .addOnFailureListener {
                    if (cont.isActive) cont.resume(emptyList())
                }
        }
    }

    private fun Barcode.toResult(imgW: Float, imgH: Float): BarcodeResult? {
        val rect = boundingBox ?: return null
        val raw = rawValue ?: return null
        return BarcodeResult(
            rawValue = raw,
            displayValue = displayValue ?: raw,
            format = formatToEnum(format),
            boundingBox = BoundingBox(
                left = (rect.left / imgW).coerceIn(0f, 1f),
                top = (rect.top / imgH).coerceIn(0f, 1f),
                right = (rect.right / imgW).coerceIn(0f, 1f),
                bottom = (rect.bottom / imgH).coerceIn(0f, 1f)
            )
        )
    }

    private fun formatToEnum(format: Int): BarcodeFormat = when (format) {
        Barcode.FORMAT_QR_CODE -> BarcodeFormat.QR_CODE
        Barcode.FORMAT_EAN_13 -> BarcodeFormat.EAN_13
        Barcode.FORMAT_EAN_8 -> BarcodeFormat.EAN_8
        Barcode.FORMAT_UPC_A -> BarcodeFormat.UPC_A
        Barcode.FORMAT_UPC_E -> BarcodeFormat.UPC_E
        Barcode.FORMAT_CODE_128 -> BarcodeFormat.CODE_128
        Barcode.FORMAT_CODE_39 -> BarcodeFormat.CODE_39
        Barcode.FORMAT_DATA_MATRIX -> BarcodeFormat.DATA_MATRIX
        Barcode.FORMAT_PDF417 -> BarcodeFormat.PDF417
        Barcode.FORMAT_AZTEC -> BarcodeFormat.AZTEC
        else -> BarcodeFormat.UNKNOWN
    }

    fun close() = scanner.close()
}
