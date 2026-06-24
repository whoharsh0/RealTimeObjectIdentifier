package com.rudra.objectidentifier.data.camera

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StubImageAnalyzer @Inject constructor() : ImageAnalysis.Analyzer {

    @Volatile
    var framesAnalyzed: Long = 0
        private set

    @androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
    override fun analyze(image: ImageProxy) {
        framesAnalyzed++
        image.close()
    }
}
