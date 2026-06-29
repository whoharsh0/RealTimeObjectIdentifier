package com.rudra.objectidentifier.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.sp
import com.rudra.objectidentifier.domain.model.OcrLine

private val OcrBoxColor = Color(0xFFB39DDB)
private val OcrBoxStrokeColor = Color(0xFF9C27B0)

@Composable
fun OcrOverlay(
    lines: List<OcrLine>,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val textSizePx = with(density) { 11.sp.toPx() }
    val padPx = with(density) { 4f }

    Canvas(modifier = modifier.fillMaxSize()) {
        lines.forEach { line ->
            val box = line.boundingBox
            val left = box.left * size.width
            val top = box.top * size.height
            val width = box.width * size.width
            val height = box.height * size.height

            // Box stroke
            drawRect(
                color = OcrBoxStrokeColor,
                topLeft = Offset(left, top),
                size = Size(width, height),
                style = Stroke(width = 2f)
            )
            // Fill tint
            drawRect(
                color = OcrBoxColor.copy(alpha = 0.15f),
                topLeft = Offset(left, top),
                size = Size(width, height)
            )

            // Text label
            val paint = android.graphics.Paint().apply {
                color = android.graphics.Color.WHITE
                textSize = textSizePx
                isAntiAlias = true
                typeface = android.graphics.Typeface.DEFAULT
            }
            val displayText = if (line.text.length > 40) line.text.take(37) + "…" else line.text
            val textWidth = paint.measureText(displayText)
            val labelHeight = textSizePx + padPx * 2
            val labelTop = (top - labelHeight).coerceAtLeast(0f)

            drawRect(
                color = OcrBoxStrokeColor,
                topLeft = Offset(left, labelTop),
                size = Size(textWidth + padPx * 2, labelHeight)
            )
            drawContext.canvas.nativeCanvas.drawText(
                displayText,
                left + padPx,
                labelTop + padPx + textSizePx * 0.85f,
                paint
            )
        }
    }
}
