package com.rudra.objectidentifier.di

import com.rudra.objectidentifier.data.repository.DetectionRepositoryImpl
import com.rudra.objectidentifier.data.settings.UserSettingsRepositoryImpl
import com.rudra.objectidentifier.domain.repository.DetectionRepository
import com.rudra.objectidentifier.domain.repository.DetectionResultPublisher
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
}
