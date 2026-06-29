package com.rudra.objectidentifier.domain.repository

import android.graphics.Bitmap
import com.rudra.objectidentifier.domain.model.DetectedObject

interface StillImageDetector {
    fun detect(bitmap: Bitmap?): Result<List<DetectedObject>>
}
