package com.rudra.objectidentifier.core

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri

/**
 * Decodes gallery images with [BitmapFactory] downsampling so large photos are scaled to a
 * workable size before detection. Still images use a higher cap than live camera frames because
 * latency is not a concern and more pixels improve recall on small/distant objects.
 */
object GalleryBitmapDecoder {

    /** Largest side of the decoded bitmap for gallery still-image detection. */
    const val MAX_GALLERY_DIMENSION = 2048

    fun decode(context: Context, uri: Uri): Bitmap? {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        context.contentResolver.openInputStream(uri)?.use { stream ->
            BitmapFactory.decodeStream(stream, null, bounds)
        } ?: return null
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null

        val decodeOptions = BitmapFactory.Options().apply {
            inSampleSize = calculateInSampleSize(bounds.outWidth, bounds.outHeight, MAX_GALLERY_DIMENSION)
        }
        return context.contentResolver.openInputStream(uri)?.use { stream ->
            BitmapFactory.decodeStream(stream, null, decodeOptions)
        }
    }

    fun calculateInSampleSize(width: Int, height: Int, maxDimension: Int): Int {
        var sampleSize = 1
        val largest = maxOf(width, height)
        while (largest / sampleSize > maxDimension) {
            sampleSize *= 2
        }
        return sampleSize
    }
}
