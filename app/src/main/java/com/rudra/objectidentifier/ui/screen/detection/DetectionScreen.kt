package com.rudra.objectidentifier.ui.screen.detection

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rudra.objectidentifier.R
import com.rudra.objectidentifier.di.CameraEntryPoint
import com.rudra.objectidentifier.domain.model.CameraLens
import com.rudra.objectidentifier.presentation.detection.DetectionUiState
import com.rudra.objectidentifier.presentation.detection.DetectionViewModel
import com.rudra.objectidentifier.ui.components.BoundingBoxOverlay
import com.rudra.objectidentifier.ui.components.CameraPermissionContent
import com.rudra.objectidentifier.ui.components.CameraPreview
import com.rudra.objectidentifier.ui.components.GlassControlBar
import com.rudra.objectidentifier.ui.theme.AccentCyan
import com.rudra.objectidentifier.ui.theme.GradientEnd
import com.rudra.objectidentifier.ui.theme.GradientStart
import com.rudra.objectidentifier.ui.theme.RealTimeObjectIdentifierTheme
import dagger.hilt.android.EntryPointAccessors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetectionScreen(
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DetectionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
        )
    }
    var permissionDenied by rememberSaveable { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
        permissionDenied = !granted
    }

    val imageAnalyzer = remember {
        EntryPointAccessors.fromApplication(
            context.applicationContext,
            CameraEntryPoint::class.java
        ).detectionImageAnalyzer()
    }

    DetectionScreenContent(
        uiState = uiState,
        hasCameraPermission = hasCameraPermission,
        permissionDenied = permissionDenied,
        imageAnalyzer = imageAnalyzer,
        onOpenSettings = onOpenSettings,
        onRequestPermission = { permissionLauncher.launch(Manifest.permission.CAMERA) },
        onStartDetection = viewModel::onStartDetection,
        onStopDetection = viewModel::onStopDetection,
        onToggleCamera = viewModel::onToggleCamera,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetectionScreenContent(
    uiState: DetectionUiState,
    hasCameraPermission: Boolean,
    permissionDenied: Boolean,
    imageAnalyzer: androidx.camera.core.ImageAnalysis.Analyzer,
    onOpenSettings: () -> Unit,
    onRequestPermission: () -> Unit,
    onStartDetection: () -> Unit,
    onStopDetection: () -> Unit,
    onToggleCamera: () -> Unit,
    modifier: Modifier = Modifier
) {
    when {
        uiState.isLoading -> {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(listOf(GradientStart, GradientEnd))
                    ),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = AccentCyan)
            }
        }

        !hasCameraPermission -> {
            CameraPermissionContent(
                permissionDenied = permissionDenied,
                onRequestPermission = onRequestPermission,
                modifier = modifier
            )
        }

        uiState.isDetecting -> {
            Box(modifier = modifier.fillMaxSize()) {
                CameraPreview(
                    enabled = true,
                    cameraLens = uiState.cameraLens,
                    imageAnalyzer = imageAnalyzer,
                    modifier = Modifier.fillMaxSize()
                )

                BoundingBoxOverlay(
                    detections = uiState.detections,
                    modifier = Modifier.fillMaxSize()
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colorStops = arrayOf(
                                    0f to Color.Black.copy(alpha = 0.55f),
                                    0.18f to Color.Transparent,
                                    0.82f to Color.Transparent,
                                    1f to Color.Black.copy(alpha = 0.65f)
                                )
                            )
                        )
                )

                TopAppBar(
                    title = {
                        Text(
                            text = uiState.appTitle.ifBlank { "Object Identifier" },
                            color = Color.White
                        )
                    },
                    actions = {
                        IconButton(onClick = onToggleCamera) {
                            Icon(
                                imageVector = Icons.Default.Cameraswitch,
                                contentDescription = stringResource(R.string.flip_camera),
                                tint = Color.White
                            )
                        }
                        IconButton(onClick = onOpenSettings) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = Color.Transparent
                    ),
                    modifier = Modifier.statusBarsPadding()
                )

                GlassControlBar(
                    detectionCount = uiState.detections.size,
                    isDetecting = true,
                    onPrimaryAction = onStopDetection,
                    primaryLabel = stringResource(R.string.stop_detection),
                    modifier = Modifier.align(Alignment.BottomCenter),
                    secondaryContent = uiState.errorMessage?.let { message ->
                        {
                            Text(
                                text = message,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                )
            }
        }

        else -> {
            Scaffold(
                modifier = modifier.fillMaxSize(),
                containerColor = MaterialTheme.colorScheme.background,
                topBar = {
                    TopAppBar(
                        title = { Text(uiState.appTitle.ifBlank { "Object Identifier" }) },
                        actions = {
                            IconButton(onClick = onOpenSettings) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = "Settings"
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Transparent
                        )
                    )
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .background(
                            Brush.verticalGradient(listOf(GradientStart, GradientEnd))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    GlassControlBar(
                        detectionCount = uiState.detections.size,
                        isDetecting = false,
                        onPrimaryAction = onStartDetection,
                        primaryLabel = stringResource(R.string.start_detection),
                        modifier = Modifier.align(Alignment.BottomCenter),
                        secondaryContent = {
                            Text(
                                text = stringResource(R.string.detection_idle_hint),
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DetectionScreenPreview() {
    RealTimeObjectIdentifierTheme {
        DetectionScreenContent(
            uiState = DetectionUiState(
                isLoading = false,
                appTitle = "Real-Time Object Identifier",
                versionName = "1.0.0",
                isDetecting = true,
                cameraLens = CameraLens.BACK,
                detections = emptyList()
            ),
            hasCameraPermission = true,
            permissionDenied = false,
            imageAnalyzer = androidx.camera.core.ImageAnalysis.Analyzer { image -> image.close() },
            onOpenSettings = {},
            onRequestPermission = {},
            onStartDetection = {},
            onStopDetection = {},
            onToggleCamera = {}
        )
    }
}
