package com.rudra.objectidentifier.di

import com.rudra.objectidentifier.data.detector.TfliteObjectDetector
import com.rudra.objectidentifier.di.IoDispatcher
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Module
@InstallIn(SingletonComponent::class)
object DetectorModule {

    @Provides
    @Singleton
    fun provideDetectorWarmup(
        detector: TfliteObjectDetector,
        @ApplicationScope applicationScope: CoroutineScope,
        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ): DetectorWarmupMarker {
        applicationScope.launch(ioDispatcher) {
            detector.warmUp()
        }
        return DetectorWarmupMarker
    }
}

object DetectorWarmupMarker
