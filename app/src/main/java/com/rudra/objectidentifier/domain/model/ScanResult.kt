package com.rudra.objectidentifier.domain.model

data class BarcodeResult(
    val rawValue: String,
    val displayValue: String,
    val format: BarcodeFormat,
    val boundingBox: BoundingBox
)

enum class BarcodeFormat(val displayName: String, val emoji: String) {
    QR_CODE("QR Code", "⬛"),
    EAN_13("EAN-13", "▦"),
    EAN_8("EAN-8", "▦"),
    UPC_A("UPC-A", "▦"),
    UPC_E("UPC-E", "▦"),
    CODE_128("Code 128", "▦"),
    CODE_39("Code 39", "▦"),
    DATA_MATRIX("Data Matrix", "⬛"),
    PDF417("PDF417", "▦"),
    AZTEC("Aztec", "⬛"),
    UNKNOWN("Barcode", "▦")
}

data class OcrLine(
    val text: String,
    val boundingBox: BoundingBox,
    val confidence: Float = 1f
)
