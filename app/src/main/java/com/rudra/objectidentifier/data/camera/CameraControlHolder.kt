package com.rudra.objectidentifier.data.camera

import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import androidx.camera.core.Camera
import com.rudra.objectidentifier.core.AppLog
import com.rudra.objectidentifier.domain.repository.CameraControls
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CameraControlHolder @Inject constructor(
    @dagger.hilt.android.qualifiers.ApplicationContext private val context: Context
) : CameraControls {
    @Volatile
    var boundCamera: Camera? = null
        private set

    @Volatile
    var torchEnabled: Boolean = false
        private set

    fun onCameraBound(camera: Camera?) {
        boundCamera = camera
        if (camera == null) {
            torchEnabled = false
        }
    }

    override fun toggleTorch(): Boolean {
        val camera = boundCamera ?: return false
        if (!hasFlashUnit()) return false
        val next = !torchEnabled
        return runCatching {
            camera.cameraControl.enableTorch(next)
            torchEnabled = next
            true
        }.getOrElse { error ->
            AppLog.w(TAG, "Failed to toggle torch", error)
            false
        }
    }

    override fun disableTorch() {
        runCatching {
            boundCamera?.cameraControl?.enableTorch(false)
        }.onFailure { error ->
            AppLog.w(TAG, "Failed to disable torch", error)
        }
        torchEnabled = false
    }

    override fun hasFlashUnit(): Boolean {
        return runCatching {
            val manager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
            manager.cameraIdList.any { id ->
                manager.getCameraCharacteristics(id)
                    .get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
            }
        }.getOrElse { error ->
            AppLog.w(TAG, "Failed to query flash availability", error)
            false
        }
    }

    companion object {
        private const val TAG = "CameraControlHolder"
    }
}
