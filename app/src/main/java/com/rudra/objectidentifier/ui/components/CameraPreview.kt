package com.rudra.objectidentifier.ui.components

import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.rudra.objectidentifier.core.AppLog
import com.rudra.objectidentifier.data.camera.CameraControlHolder
import com.rudra.objectidentifier.domain.model.CameraLens
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import android.util.Size

private const val TAG = "CameraPreview"

@Composable
fun CameraPreview(
    enabled: Boolean,
    cameraLens: CameraLens,
    imageAnalyzer: ImageAnalysis.Analyzer,
    cameraControlHolder: CameraControlHolder,
    modifier: Modifier = Modifier,
    analysisEnabled: Boolean = true
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }
    val cameraExecutor = rememberCameraExecutor()

    DisposableEffect(enabled, cameraLens, analysisEnabled) {
        if (!enabled) {
            cameraControlHolder.onCameraBound(null)
            onDispose { }
            return@DisposableEffect onDispose { }
        }

        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        val mainExecutor = ContextCompat.getMainExecutor(context)

        val listener = Runnable {
            runCatching {
                val cameraProvider = cameraProviderFuture.get()
                bindCameraUseCases(
                    cameraProvider = cameraProvider,
                    lifecycleOwner = lifecycleOwner,
                    previewView = previewView,
                    cameraLens = cameraLens,
                    imageAnalyzer = imageAnalyzer,
                    cameraExecutor = cameraExecutor,
                    cameraControlHolder = cameraControlHolder,
                    analysisEnabled = analysisEnabled
                )
            }.onFailure { error ->
                AppLog.e(TAG, "Camera binding failed", error)
                cameraControlHolder.onCameraBound(null)
            }
        }

        cameraProviderFuture.addListener(listener, mainExecutor)

        onDispose {
            // Unbind all use cases BEFORE the PreviewView surface is torn down so CameraX stops
            // submitting capture requests against a surface that is about to be abandoned. This is
            // what reduces the "BufferQueue has been abandoned" errors during camera teardown.
            cameraControlHolder.disableTorch()
            cameraControlHolder.onCameraBound(null)
            runCatching {
                cameraProviderFuture.get().unbindAll()
            }.onFailure { error ->
                AppLog.e(TAG, "Camera unbind failed during teardown", error)
            }
        }
    }

    AndroidView(
        factory = { previewView },
        modifier = modifier
    )
}

@Composable
private fun rememberCameraExecutor(): ExecutorService {
    val executor = remember { Executors.newSingleThreadExecutor() }
    DisposableEffect(Unit) {
        onDispose { executor.shutdown() }
    }
    return executor
}

private fun bindCameraUseCases(
    cameraProvider: ProcessCameraProvider,
    lifecycleOwner: androidx.lifecycle.LifecycleOwner,
    previewView: PreviewView,
    cameraLens: CameraLens,
    imageAnalyzer: ImageAnalysis.Analyzer,
    cameraExecutor: ExecutorService,
    cameraControlHolder: CameraControlHolder,
    analysisEnabled: Boolean
) {
    val lensFacing = when (cameraLens) {
        CameraLens.BACK -> CameraSelector.LENS_FACING_BACK
        CameraLens.FRONT -> CameraSelector.LENS_FACING_FRONT
    }

    val preview = Preview.Builder().build().also {
        it.surfaceProvider = previewView.surfaceProvider
    }

    val analysis = ImageAnalysis.Builder()
        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
        .setResolutionSelector(
            ResolutionSelector.Builder()
                .setResolutionStrategy(
                    ResolutionStrategy(
                        Size(1280, 720),
                        ResolutionStrategy.FALLBACK_RULE_CLOSEST_HIGHER_THEN_LOWER
                    )
                )
                .build()
        )
        .build()
        .also {
            if (analysisEnabled) {
                it.setAnalyzer(cameraExecutor, imageAnalyzer)
            }
        }

    val cameraSelector = CameraSelector.Builder()
        .requireLensFacing(lensFacing)
        .build()

    cameraProvider.unbindAll()
    val camera = cameraProvider.bindToLifecycle(
        lifecycleOwner,
        cameraSelector,
        preview,
        analysis
    )
    cameraControlHolder.onCameraBound(camera)
}
