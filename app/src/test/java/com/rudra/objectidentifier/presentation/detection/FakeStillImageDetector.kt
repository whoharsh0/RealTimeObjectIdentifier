package com.rudra.objectidentifier.presentation.detection

import android.graphics.Bitmap
import com.rudra.objectidentifier.domain.model.DetectedObject
import com.rudra.objectidentifier.domain.repository.StillImageDetector

class FakeStillImageDetector : StillImageDetector {
    override fun detect(bitmap: Bitmap?): Result<List<DetectedObject>> {
        return Result.success(emptyList())
    }
}
