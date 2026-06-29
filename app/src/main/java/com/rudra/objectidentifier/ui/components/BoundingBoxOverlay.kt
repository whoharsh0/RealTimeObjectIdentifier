package com.rudra.objectidentifier.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rudra.objectidentifier.domain.model.BarcodeResult
import com.rudra.objectidentifier.domain.model.BoxStyle
import com.rudra.objectidentifier.domain.model.DisplayDetection
import com.rudra.objectidentifier.ui.theme.DetectionBoxFill

private val BarcodeColor = Color(0xFF76FF03)

@Composable
fun BoundingBoxOverlay(
    detections: List<DisplayDetection>,
    showConfidencePercent: Boolean,
    boxStyle: BoxStyle,
    labelScale: Float,
    barcodeResults: List<BarcodeResult> = emptyList(),
    modifier: Modifier = Modifier,
    onDetectionTapped: (Int?) -> Unit = {}
) {
    val density = LocalDensity.current
    val labelTextSizePx = with(density) { (12.sp * labelScale).toPx() }
    val labelPaddingPx = with(density) { 6.dp.toPx() }
    val strokeWidthPx = with(density) { (if (boxStyle == BoxStyle.CORNERS) 3f else 2.5f).dp.toPx() }
    val barcodeStrokePx = with(density) { 2.5f.dp.toPx() }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(detections) {
                detectTapGestures { offset ->
                    val tapped = detections.firstOrNull { display ->
                        val box = display.detection.boundingBox
                        val left = box.left * size.width
                        val top = box.top * size.height
                        val right = box.right * size.width
                        val bottom = box.bottom * size.height
                        offset.x in left..right && offset.y in top..bottom
                    }
                    onDetectionTapped(tapped?.trackId)
                }
            }
    ) {
        detections.forEach { display ->
            val detection = display.detection
            val box = detection.boundingBox
            val left = box.left * size.width
            val top = box.top * size.height
            val width = box.width * size.width
            val height = box.height * size.height
            val strokeColor = Color(display.categoryColorArgb)
            val fillColor = if (display.isHighlighted) strokeColor.copy(alpha = 0.35f)
                            else DetectionBoxFill.copy(alpha = 0.22f)

            when (boxStyle) {
                BoxStyle.FULL, BoxStyle.FILLED -> {
                    if (boxStyle == BoxStyle.FILLED || display.isHighlighted) {
                        drawRect(color = fillColor, topLeft = Offset(left, top), size = Size(width, height))
                    }
                    drawRect(color = strokeColor, topLeft = Offset(left, top), size = Size(width, height), style = Stroke(strokeWidthPx))
                }
                BoxStyle.CORNERS -> {
                    val corner = minOf(width, height) * 0.22f
                    drawLine(strokeColor, Offset(left, top), Offset(left + corner, top), strokeWidthPx)
                    drawLine(strokeColor, Offset(left, top), Offset(left, top + corner), strokeWidthPx)
                    drawLine(strokeColor, Offset(left + width, top), Offset(left + width - corner, top), strokeWidthPx)
                    drawLine(strokeColor, Offset(left + width, top), Offset(left + width, top + corner), strokeWidthPx)
                    drawLine(strokeColor, Offset(left, top + height), Offset(left + corner, top + height), strokeWidthPx)
                    drawLine(strokeColor, Offset(left, top + height), Offset(left, top + height - corner), strokeWidthPx)
                    drawLine(strokeColor, Offset(left + width, top + height), Offset(left + width - corner, top + height), strokeWidthPx)
                    drawLine(strokeColor, Offset(left + width, top + height), Offset(left + width, top + height - corner), strokeWidthPx)
                }
            }

            val label = buildString {
                append(display.displayLabel)
                if (showConfidencePercent) append(" ${detection.confidencePercent}%")
            }
            drawLabel(label, left, top, labelTextSizePx, labelPaddingPx, strokeColor)
        }

        barcodeResults.forEach { barcode ->
            val box = barcode.boundingBox
            val left = box.left * size.width
            val top = box.top * size.height
            val width = box.width * size.width
            val height = box.height * size.height

            drawRect(color = BarcodeColor.copy(alpha = 0.18f), topLeft = Offset(left, top), size = Size(width, height))
            drawRect(color = BarcodeColor, topLeft = Offset(left, top), size = Size(width, height), style = Stroke(barcodeStrokePx))

            val label = "${barcode.format.displayName}: ${barcode.displayValue.take(32)}"
            drawLabel(label, left, top, labelTextSizePx, labelPaddingPx, BarcodeColor)
        }
    }
}

private fun DrawScope.drawLabel(
    text: String,
    boxLeft: Float,
    boxTop: Float,
    textSizePx: Float,
    paddingPx: Float,
    bgColor: Color
) {
    val paint = android.graphics.Paint().apply {
        color = android.graphics.Color.WHITE
        textSize = textSizePx
        isAntiAlias = true
        typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
    }
    val textWidth = paint.measureText(text)
    val labelHeight = textSizePx + paddingPx * 2
    val labelTop = (boxTop - labelHeight).coerceAtLeast(0f)

    drawRect(color = bgColor, topLeft = Offset(boxLeft, labelTop), size = Size(textWidth + paddingPx * 2, labelHeight))
    drawContext.canvas.nativeCanvas.drawText(
        text,
        boxLeft + paddingPx,
        labelTop + paddingPx + textSizePx * 0.85f,
        paint
    )
}
