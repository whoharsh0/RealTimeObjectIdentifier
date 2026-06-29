package com.rudra.objectidentifier.core

import org.junit.Assert.assertEquals
import org.junit.Test

class GalleryBitmapDecoderTest {

    @Test
    fun calculateInSampleSize_scalesDownLargeImages() {
        assertEquals(1, GalleryBitmapDecoder.calculateInSampleSize(1024, 768, 2048))
        assertEquals(2, GalleryBitmapDecoder.calculateInSampleSize(4096, 3072, 2048))
        assertEquals(4, GalleryBitmapDecoder.calculateInSampleSize(8192, 6144, 2048))
    }
}
