package com.rudra.objectidentifier.ui.components

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.rudra.objectidentifier.R
import com.rudra.objectidentifier.core.AppLog
import com.rudra.objectidentifier.ui.theme.AccentCyan
import com.rudra.objectidentifier.ui.theme.GradientEnd
import com.rudra.objectidentifier.ui.theme.GradientStart
import com.rudra.objectidentifier.ui.theme.NightSurfaceElevated

@Composable
fun CameraPermissionContent(
    permissionDenied: Boolean,
    onRequestPermission: () -> Unit,
    modifier: Modifier = Modifier,
    reduceMotion: Boolean = false
) {
    val context = LocalContext.current

    // Subtle scale-in of the card on first composition (skipped when reduce-motion is on).
    val appeared = remember { mutableStateOf(false) }
    androidx.compose.runtime.LaunchedEffect(Unit) { appeared.value = true }
    val cardScale by animateFloatAsState(
        targetValue = if (reduceMotion || appeared.value) 1f else 0.92f,
        animationSpec = tween(durationMillis = 320, easing = EaseOutBack),
        label = "permission-card-scale"
    )

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
                .fillMaxWidth()
                .graphicsLayer {
                    scaleX = cardScale
                    scaleY = cardScale
                },
            shape = RoundedCornerShape(28.dp),
            color = NightSurfaceElevated.copy(alpha = 0.95f),
            tonalElevation = 8.dp,
            shadowElevation = 12.dp
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

                AnimatedVisibility(
                    visible = permissionDenied,
                    enter = if (reduceMotion) EnterTransition.None else fadeIn() + expandVertically(),
                    exit = if (reduceMotion) ExitTransition.None else fadeOut() + shrinkVertically()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.camera_permission_denied),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                        OutlinedButton(
                            onClick = {
                                runCatching {
                                    val intent = Intent(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                        Uri.fromParts("package", context.packageName, null)
                                    ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    context.startActivity(intent)
                                }.onFailure { error ->
                                    AppLog.w("CameraPermission", "Failed to open app settings", error)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.camera_permission_open_settings),
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            }
        }
    }
}
