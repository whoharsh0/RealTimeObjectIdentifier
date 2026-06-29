package com.rudra.objectidentifier.domain.model

enum class ScanMode(val displayName: String) {
    GENERAL("General"),
    INDOOR("Indoor"),
    OUTDOOR("Outdoor"),
    FOOD("Food"),
    PEOPLE("People");

    fun confidencePreset(base: Float): Float = when (this) {
        GENERAL -> base
        INDOOR -> (base - 0.05f).coerceAtLeast(UserSettings.MIN_CONFIDENCE)
        OUTDOOR -> (base + 0.05f).coerceAtMost(UserSettings.MAX_CONFIDENCE)
        FOOD -> (base - 0.10f).coerceAtLeast(UserSettings.MIN_CONFIDENCE)
        PEOPLE -> (base - 0.05f).coerceAtLeast(UserSettings.MIN_CONFIDENCE)
    }
}

enum class AppTheme(val displayName: String) {
    DARK("Dark"),
    AMOLED("AMOLED Black"),
    LIGHT("Light"),
    HIGH_CONTRAST("High Contrast"),
    SYSTEM("System (dynamic)")
}

enum class BoxStyle(val displayName: String) {
    FULL("Full rectangle"),
    CORNERS("Corner brackets"),
    FILLED("Filled mask")
}

enum class ModelVariant(val displayName: String, val assetFile: String, val inputSize: Int) {
    LITE0("Fast · Lite0 (320px)", "efficientdet_lite0.tflite", 320),
    LITE1("Balanced · Lite1 (384px)", "efficientdet_lite1.tflite", 384),
    LITE2("Precise · Lite2 (448px)", "efficientdet_lite2.tflite", 448),
    LITE4("Maximum · Lite4 (640px)", "efficientdet_lite4.tflite", 640)
}

enum class InferenceDelegate(val displayName: String) {
    CPU("CPU (always works)"),
    NNAPI("NNAPI (recommended)"),
    GPU("GPU (fastest on supported devices)")
}

data class DetectionMetrics(
    val fps: Float = 0f,
    val inferenceMs: Long = 0L,
    val delegateName: String = "CPU",
    val framesProcessed: Long = 0L,
    val framesSkipped: Long = 0L
)

data class ScanHistoryEntry(
    val id: Long = 0L,
    val timestampMillis: Long,
    val topLabels: String,
    val detectionCount: Int,
    val scanMode: ScanMode
)

data class DisplayDetection(
    val detection: DetectedObject,
    val trackId: Int,
    val displayLabel: String,
    val categoryColorArgb: Int,
    val isHighlighted: Boolean = false
)
