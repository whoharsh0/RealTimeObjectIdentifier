package com.rudra.objectidentifier.ui.screen.detection

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageAnalysis
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rudra.objectidentifier.R
import com.rudra.objectidentifier.di.CameraEntryPoint
import com.rudra.objectidentifier.presentation.detection.DetectionUiState
import com.rudra.objectidentifier.presentation.detection.DetectionViewModel
import com.rudra.objectidentifier.ui.components.BoundingBoxOverlay
import com.rudra.objectidentifier.ui.components.CameraPermissionContent
import com.rudra.objectidentifier.ui.components.CameraPreview
import com.rudra.objectidentifier.ui.components.DetectionListSheet
import com.rudra.objectidentifier.ui.components.GlassControlBar
import com.rudra.objectidentifier.ui.components.MetricsChip
import com.rudra.objectidentifier.ui.components.OcrOverlay
import com.rudra.objectidentifier.ui.components.OnboardingDialog
import com.rudra.objectidentifier.ui.theme.AccentCyan
import com.rudra.objectidentifier.ui.theme.GradientEnd
import com.rudra.objectidentifier.ui.theme.GradientStart
import dagger.hilt.android.EntryPointAccessors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetectionScreen(
    onOpenSettings: () -> Unit,
    onOpenHistory: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DetectionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
        )
    }
    var permissionDenied by rememberSaveable { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
        permissionDenied = !granted
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            val bitmap = runCatching {
                context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it) }
            }.getOrNull()
            viewModel.onGalleryImageSelected(bitmap)
        }
    }

    val imageAnalyzer = remember {
        EntryPointAccessors.fromApplication(
            context.applicationContext,
            CameraEntryPoint::class.java
        ).detectionImageAnalyzer()
    }

    LaunchedEffect(uiState.galleryResultMessage) {
        uiState.galleryResultMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.onDismissGalleryMessage()
        }
    }

    DetectionScreenContent(
        uiState = uiState,
        hasCameraPermission = hasCameraPermission,
        permissionDenied = permissionDenied,
        imageAnalyzer = imageAnalyzer,
        snackbarHostState = snackbarHostState,
        onOpenSettings = onOpenSettings,
        onOpenHistory = onOpenHistory,
        onRequestPermission = { permissionLauncher.launch(Manifest.permission.CAMERA) },
        onStartDetection = viewModel::onStartDetection,
        onStopDetection = viewModel::onStopDetection,
        onToggleCamera = viewModel::onToggleCamera,
        onToggleFreeze = viewModel::onToggleFreeze,
        onToggleTorch = viewModel::onToggleTorch,
        onToggleDetectionList = viewModel::onToggleDetectionList,
        onHighlightTrack = viewModel::onHighlightTrack,
        onPickGallery = { galleryLauncher.launch("image/*") },
        modifier = modifier
    )

    if (uiState.showOnboardingDialog) {
        OnboardingDialog(onDismiss = viewModel::onDismissOnboarding)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetectionScreenContent(
    uiState: DetectionUiState,
    hasCameraPermission: Boolean,
    permissionDenied: Boolean,
    imageAnalyzer: ImageAnalysis.Analyzer,
    snackbarHostState: SnackbarHostState,
    onOpenSettings: () -> Unit,
    onOpenHistory: () -> Unit,
    onRequestPermission: () -> Unit,
    onStartDetection: () -> Unit,
    onStopDetection: () -> Unit,
    onToggleCamera: () -> Unit,
    onToggleFreeze: () -> Unit,
    onToggleTorch: () -> Unit,
    onToggleDetectionList: () -> Unit,
    onHighlightTrack: (Int?) -> Unit,
    onPickGallery: () -> Unit,
    modifier: Modifier = Modifier
) {
    when {
        uiState.isLoading -> {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(Brush.verticalGradient(listOf(GradientStart, GradientEnd))),
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
                    analysisEnabled = !uiState.isFrozen,
                    modifier = Modifier.fillMaxSize()
                )

                BoundingBoxOverlay(
                    detections = uiState.displayDetections,
                    showConfidencePercent = uiState.showConfidencePercent,
                    boxStyle = uiState.boxStyle,
                    labelScale = uiState.labelScale,
                    barcodeResults = uiState.barcodeResults,
                    onDetectionTapped = onHighlightTrack,
                    modifier = Modifier.fillMaxSize()
                )

                if (uiState.enableOcrMode) {
                    OcrOverlay(
                        lines = uiState.ocrLines,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Gradient scrim top + bottom
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colorStops = arrayOf(
                                    0f to Color.Black.copy(alpha = 0.55f),
                                    0.18f to Color.Transparent,
                                    0.80f to Color.Transparent,
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
                            Icon(Icons.Default.Cameraswitch, "Flip camera", tint = Color.White)
                        }
                        IconButton(onClick = onOpenHistory) {
                            Icon(Icons.Default.History, "Scan history", tint = Color.White)
                        }
                        IconButton(onClick = onOpenSettings) {
                            Icon(Icons.Default.Settings, "Settings", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                    modifier = Modifier.statusBarsPadding()
                )

                if (uiState.showFpsOverlay) {
                    MetricsChip(
                        fps = uiState.metrics.fps,
                        inferenceMs = uiState.metrics.inferenceMs,
                        delegateName = uiState.metrics.delegateName,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .statusBarsPadding()
                            .padding(end = 12.dp, top = 60.dp)
                    )
                }

                if (uiState.isFrozen) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = AccentCyan.copy(alpha = 0.22f),
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .statusBarsPadding()
                            .padding(top = 60.dp)
                    ) {
                        Text(
                            text = "❄ Frozen",
                            color = AccentCyan,
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }

                GlassControlBar(
                    isDetecting = true,
                    detectionCount = uiState.displayDetections.size,
                    isFrozen = uiState.isFrozen,
                    torchEnabled = uiState.torchEnabled,
                    hasFlash = uiState.hasFlash,
                    ocrModeEnabled = uiState.enableOcrMode,
                    sceneDescription = uiState.sceneDescription,
                    showSceneDescription = uiState.showSceneDescription,
                    onPrimaryAction = onStopDetection,
                    onToggleFreeze = onToggleFreeze,
                    onToggleTorch = onToggleTorch,
                    onToggleDetectionList = onToggleDetectionList,
                    onPickGallery = onPickGallery,
                    onToggleOcr = {},
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

                if (uiState.showDetectionList && uiState.displayDetections.isNotEmpty()) {
                    DetectionListSheet(
                        detections = uiState.displayDetections,
                        showConfidencePercent = uiState.showConfidencePercent,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .padding(bottom = 180.dp),
                        onDismiss = TODO(),
                        onRowTap = TODO()
                    )
                }

                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 200.dp)
                )
            }
        }

        else -> {
            Scaffold(
                modifier = modifier.fillMaxSize(),
                containerColor = MaterialTheme.colorScheme.background,
                snackbarHost = { SnackbarHost(snackbarHostState) },
                topBar = {
                    TopAppBar(
                        title = { Text(uiState.appTitle.ifBlank { "Object Identifier" }) },
                        actions = {
                            IconButton(onClick = onOpenHistory) {
                                Icon(Icons.Default.History, "Scan history")
                            }
                            IconButton(onClick = onOpenSettings) {
                                Icon(Icons.Default.Settings, "Settings")
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                    )
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .background(Brush.verticalGradient(listOf(GradientStart, GradientEnd))),
                    contentAlignment = Alignment.Center
                ) {
                    GlassControlBar(
                        isDetecting = false,
                        detectionCount = 0,
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
