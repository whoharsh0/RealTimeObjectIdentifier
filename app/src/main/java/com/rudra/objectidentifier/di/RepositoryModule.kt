package com.rudra.objectidentifier.di

import com.rudra.objectidentifier.data.camera.CameraControlHolder
import com.rudra.objectidentifier.data.history.ScanHistoryRepositoryImpl
import com.rudra.objectidentifier.data.detector.GalleryImageDetector
import com.rudra.objectidentifier.data.repository.DetectionRepositoryImpl
import com.rudra.objectidentifier.data.settings.UserSettingsRepositoryImpl
import com.rudra.objectidentifier.domain.repository.CameraControls
import com.rudra.objectidentifier.domain.repository.DetectionRepository
import com.rudra.objectidentifier.domain.repository.DetectionResultPublisher
import com.rudra.objectidentifier.domain.repository.ScanHistoryRepository
import com.rudra.objectidentifier.domain.repository.StillImageDetector
import com.rudra.objectidentifier.domain.repository.UserSettingsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindDetectionRepository(
        impl: DetectionRepositoryImpl
    ): DetectionRepository

    @Binds
    @Singleton
    abstract fun bindDetectionResultPublisher(
        impl: DetectionRepositoryImpl
    ): DetectionResultPublisher

    @Binds
    @Singleton
    abstract fun bindUserSettingsRepository(
        impl: UserSettingsRepositoryImpl
    ): UserSettingsRepository

    @Binds
    @Singleton
    abstract fun bindScanHistoryRepository(
        impl: ScanHistoryRepositoryImpl
    ): ScanHistoryRepository

    @Binds
    @Singleton
    abstract fun bindStillImageDetector(
        impl: GalleryImageDetector
    ): StillImageDetector

    @Binds
    @Singleton
    abstract fun bindCameraControls(
        impl: CameraControlHolder
    ): CameraControls
}
