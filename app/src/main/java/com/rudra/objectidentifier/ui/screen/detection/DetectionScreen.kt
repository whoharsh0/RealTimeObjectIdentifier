package com.rudra.objectidentifier.ui.screen.detection

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.rudra.objectidentifier.ui.components.CameraPermissionContent
import com.rudra.objectidentifier.ui.components.CameraPreview
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
        ).stubImageAnalyzer()
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
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(uiState.appTitle.ifBlank { "Object Identifier" }) },
                actions = {
                    if (hasCameraPermission && uiState.isDetecting) {
                        IconButton(onClick = onToggleCamera) {
                            Icon(
                                imageVector = Icons.Default.Cameraswitch,
                                contentDescription = stringResource(R.string.flip_camera)
                            )
                        }
                    }
                    IconButton(onClick = onOpenSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            !hasCameraPermission -> {
                CameraPermissionContent(
                    permissionDenied = permissionDenied,
                    onRequestPermission = onRequestPermission,
                    modifier = Modifier.padding(innerPadding)
                )
            }

            uiState.isDetecting -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    CameraPreview(
                        enabled = true,
                        cameraLens = uiState.cameraLens,
                        imageAnalyzer = imageAnalyzer,
                        modifier = Modifier.fillMaxSize()
                    )

                    DetectionOverlay(
                        detectionCount = uiState.detections.size,
                        errorMessage = uiState.errorMessage,
                        onStopDetection = onStopDetection,
                        modifier = Modifier.align(Alignment.BottomCenter)
                    )
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.detection_idle_hint),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(top = 32.dp)
                    )

                    uiState.errorMessage?.let { message ->
                        Text(
                            text = message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Button(onClick = onStartDetection) {
                        Text(stringResource(R.string.start_detection))
                    }
                }
            }
        }
    }
}

@Composable
private fun DetectionOverlay(
    detectionCount: Int,
    errorMessage: String?,
    onStopDetection: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.45f))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(R.string.objects_detected, detectionCount),
            color = Color.White,
            style = MaterialTheme.typography.titleMedium
        )

        errorMessage?.let { message ->
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        FilledTonalButton(onClick = onStopDetection) {
            Text(stringResource(R.string.stop_detection))
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
                isDetecting = false,
                cameraLens = CameraLens.BACK
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
