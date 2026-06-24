package com.rudra.objectidentifier.di

import com.rudra.objectidentifier.data.repository.DetectionRepositoryImpl
import com.rudra.objectidentifier.domain.repository.DetectionRepository
import com.rudra.objectidentifier.domain.repository.DetectionResultPublisher
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
}
