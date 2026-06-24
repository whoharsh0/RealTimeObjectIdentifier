package com.rudra.objectidentifier.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rudra.objectidentifier.domain.model.DetectedObject
import com.rudra.objectidentifier.ui.theme.AccentCyan
import com.rudra.objectidentifier.ui.theme.DetectionBoxFill
import com.rudra.objectidentifier.ui.theme.DetectionBoxStroke

@Composable
fun BoundingBoxOverlay(
    detections: List<DetectedObject>,
    showConfidencePercent: Boolean = true,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val labelTextSizePx = with(density) { 12.sp.toPx() }
    val labelPaddingPx = with(density) { 6.dp.toPx() }
    val strokeWidthPx = with(density) { 2.5.dp.toPx() }

    Canvas(modifier = modifier.fillMaxSize()) {
        detections.forEach { detection ->
            val box = detection.boundingBox
            val left = box.left * size.width
            val top = box.top * size.height
            val width = box.width * size.width
            val height = box.height * size.height

            drawRect(
                color = DetectionBoxFill,
                topLeft = Offset(left, top),
                size = Size(width, height)
            )
            drawRect(
                color = DetectionBoxStroke,
                topLeft = Offset(left, top),
                size = Size(width, height),
                style = Stroke(width = strokeWidthPx)
            )

            val label = if (showConfidencePercent) {
                "${detection.label} ${detection.confidencePercent}%"
            } else {
                detection.label
            }
            val textPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.WHITE
                textSize = labelTextSizePx
                isAntiAlias = true
                typeface = android.graphics.Typeface.create(
                    android.graphics.Typeface.DEFAULT,
                    android.graphics.Typeface.BOLD
                )
            }
            val textWidth = textPaint.measureText(label)
            val labelHeight = labelTextSizePx + labelPaddingPx * 2
            val labelTop = (top - labelHeight).coerceAtLeast(0f)

            drawRect(
                color = DetectionBoxStroke,
                topLeft = Offset(left, labelTop),
                size = Size(textWidth + labelPaddingPx * 2, labelHeight)
            )

            drawContext.canvas.nativeCanvas.drawText(
                label,
                left + labelPaddingPx,
                labelTop + labelPaddingPx + labelTextSizePx * 0.85f,
                textPaint
            )
        }
    }
}

@Composable
fun ScanningPulse(
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        drawCircle(
            color = AccentCyan.copy(alpha = 0.85f),
            radius = 5.dp.toPx(),
            center = center
        )
        drawCircle(
            color = AccentCyan.copy(alpha = 0.25f),
            radius = 12.dp.toPx(),
            center = center
        )
    }
}
