package com.rudra.objectidentifier.di

import com.rudra.objectidentifier.data.camera.CameraControlHolder
import com.rudra.objectidentifier.data.camera.DetectionImageAnalyzer
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface CameraEntryPoint {
    fun detectionImageAnalyzer(): DetectionImageAnalyzer
    fun cameraControlHolder(): CameraControlHolder
}
