package com.rudra.objectidentifier.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.rudra.objectidentifier.ui.theme.AccentCyan
import com.rudra.objectidentifier.ui.theme.NightSurfaceElevated

@Composable
fun GlassControlBar(
    isDetecting: Boolean,
    detectionCount: Int,
    isFrozen: Boolean = false,
    torchEnabled: Boolean = false,
    hasFlash: Boolean = false,
    ocrModeEnabled: Boolean = false,
    sceneDescription: String = "",
    showSceneDescription: Boolean = false,
    onPrimaryAction: () -> Unit,
    onToggleFreeze: () -> Unit = {},
    onToggleTorch: () -> Unit = {},
    onToggleDetectionList: () -> Unit = {},
    onPickGallery: () -> Unit = {},
    onToggleOcr: () -> Unit = {},
    primaryLabel: String,
    modifier: Modifier = Modifier,
    secondaryContent: (@Composable () -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.72f))
                )
            )
            .padding(bottom = 36.dp, top = 16.dp, start = 16.dp, end = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (showSceneDescription && sceneDescription.isNotBlank()) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = NightSurfaceElevated.copy(alpha = 0.88f)
            ) {
                Text(
                    text = sceneDescription,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.92f),
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                )
            }
        }

        secondaryContent?.invoke()

        if (isDetecting) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AnimatedContent(targetState = detectionCount, label = "count") { count ->
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = AccentCyan.copy(alpha = if (count > 0) 0.22f else 0.10f)
                    ) {
                        Text(
                            text = if (count == 0) "Scanning…" else "$count object${if (count != 1) "s" else ""}",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelMedium,
                            color = AccentCyan
                        )
                    }
                }

                if (hasFlash) {
                    ControlIconButton(
                        icon = if (torchEnabled) Icons.Default.FlashOn else Icons.Default.FlashOff,
                        contentDescription = if (torchEnabled) "Turn off flash" else "Turn on flash",
                        active = torchEnabled,
                        activeColor = Color(0xFFFFD740),
                        onClick = onToggleTorch
                    )
                }

                ControlIconButton(
                    icon = if (isFrozen) Icons.Default.PlayArrow else Icons.Default.Pause,
                    contentDescription = if (isFrozen) "Resume scanning" else "Freeze frame",
                    active = isFrozen,
                    activeColor = AccentCyan,
                    onClick = onToggleFreeze
                )

                ControlIconButton(
                    icon = Icons.Default.TextFields,
                    contentDescription = "Toggle OCR text mode",
                    active = ocrModeEnabled,
                    activeColor = Color(0xFFB39DDB),
                    onClick = onToggleOcr
                )

                ControlIconButton(
                    icon = Icons.Default.List,
                    contentDescription = "Show detection list",
                    active = false,
                    onClick = onToggleDetectionList
                )

                ControlIconButton(
                    icon = Icons.Default.PhotoLibrary,
                    contentDescription = "Analyze image from gallery",
                    active = false,
                    onClick = onPickGallery
                )
            }
        }

        val buttonBg by animateColorAsState(
            targetValue = if (isDetecting) Color(0xFFFF5252) else AccentCyan,
            animationSpec = tween(300),
            label = "primaryBtn"
        )

        Button(
            onClick = onPrimaryAction,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = buttonBg,
                contentColor = if (isDetecting) Color.White else Color(0xFF001018)
            ),
            modifier = Modifier
                .fillMaxWidth(0.72f)
                .padding(top = 4.dp)
        ) {
            Text(text = primaryLabel, style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
private fun ControlIconButton(
    icon: ImageVector,
    contentDescription: String,
    active: Boolean,
    activeColor: Color = AccentCyan,
    onClick: () -> Unit
) {
    val tint by animateColorAsState(
        targetValue = if (active) activeColor else Color.White.copy(alpha = 0.65f),
        animationSpec = tween(200),
        label = "iconTint"
    )
    Surface(
        shape = CircleShape,
        color = if (active) activeColor.copy(alpha = 0.18f) else Color.White.copy(alpha = 0.10f),
        onClick = onClick
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier
                .padding(9.dp)
                .size(22.dp)
        )
    }
}
