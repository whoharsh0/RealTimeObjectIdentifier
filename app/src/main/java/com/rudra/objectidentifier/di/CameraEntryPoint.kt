package com.rudra.objectidentifier.di

import com.rudra.objectidentifier.data.camera.StubImageAnalyzer
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface CameraEntryPoint {
    fun stubImageAnalyzer(): StubImageAnalyzer
}
