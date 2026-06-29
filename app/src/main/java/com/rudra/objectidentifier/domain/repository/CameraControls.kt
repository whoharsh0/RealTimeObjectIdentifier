package com.rudra.objectidentifier.domain.repository

interface CameraControls {
    fun toggleTorch(): Boolean
    fun disableTorch()
    fun hasFlashUnit(): Boolean
}
