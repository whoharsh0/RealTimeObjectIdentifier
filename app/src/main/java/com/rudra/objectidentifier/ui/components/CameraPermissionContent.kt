package com.rudra.objectidentifier.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.rudra.objectidentifier.R
import com.rudra.objectidentifier.ui.theme.AccentCyan
import com.rudra.objectidentifier.ui.theme.GradientEnd
import com.rudra.objectidentifier.ui.theme.GradientStart
import com.rudra.objectidentifier.ui.theme.NightSurfaceElevated

@Composable
fun CameraPermissionContent(
    permissionDenied: Boolean,
    onRequestPermission: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(GradientStart, GradientEnd)
                )
            )
    ) {
        Surface(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(24.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            color = NightSurfaceElevated.copy(alpha = 0.95f),
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(AccentCyan.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = null,
                        tint = AccentCyan,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Text(
                    text = stringResource(R.string.camera_permission_title),
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = stringResource(R.string.camera_permission_rationale),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Button(
                    onClick = onRequestPermission,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentCyan,
                        contentColor = Color(0xFF002028)
                    )
                ) {
                    Text(
                        text = stringResource(R.string.camera_permission_grant),
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                if (permissionDenied) {
                    Text(
                        text = stringResource(R.string.camera_permission_denied),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun GlassControlBar(
    detectionCount: Int,
    isDetecting: Boolean,
    onPrimaryAction: () -> Unit,
    primaryLabel: String,
    modifier: Modifier = Modifier,
    secondaryContent: @Composable (() -> Unit)? = null
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 20.dp),
        shape = RoundedCornerShape(24.dp),
        color = Color.Black.copy(alpha = 0.55f),
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isDetecting) {
                        ScanningPulse(modifier = Modifier.size(24.dp))
                    }
                    Text(
                        text = if (isDetecting) {
                            stringResource(R.string.status_scanning)
                        } else {
                            stringResource(R.string.status_ready)
                        },
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White
                    )
                }
                Text(
                    text = stringResource(R.string.objects_detected, detectionCount),
                    style = MaterialTheme.typography.titleMedium,
                    color = AccentCyan
                )
            }

            secondaryContent?.invoke()

            Button(
                onClick = onPrimaryAction,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isDetecting) Color.White.copy(alpha = 0.15f) else AccentCyan,
                    contentColor = if (isDetecting) Color.White else Color(0xFF002028)
                )
            ) {
                Text(text = primaryLabel, style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}
