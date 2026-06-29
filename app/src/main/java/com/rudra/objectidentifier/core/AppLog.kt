package com.rudra.objectidentifier.core

import android.util.Log
import com.rudra.objectidentifier.BuildConfig
import java.util.concurrent.ConcurrentHashMap

/**
 * Tiny, dependency-free logging facade around [android.util.Log].
 *
 * - All tags are prefixed with [TAG_PREFIX] so app logs are easy to filter in logcat ("ObjId").
 * - Verbose/debug are only emitted in debug builds; warn/error always pass through.
 * - [rateLimited] exists for hot paths (e.g. the per-frame detection loop) so a repeated
 *   failure logs at most once per window per key instead of flooding logcat.
 */
object AppLog {

    private const val TAG_PREFIX = "ObjId"
    private const val MAX_TAG_LENGTH = 23

    private val lastLogAtByKey = ConcurrentHashMap<String, Long>()

    fun v(tag: String, message: String, throwable: Throwable? = null) {
        if (BuildConfig.DEBUG) {
            Log.v(prefixed(tag), message, throwable)
        }
    }

    fun d(tag: String, message: String, throwable: Throwable? = null) {
        if (BuildConfig.DEBUG) {
            Log.d(prefixed(tag), message, throwable)
        }
    }

    fun i(tag: String, message: String, throwable: Throwable? = null) {
        Log.i(prefixed(tag), message, throwable)
    }

    fun w(tag: String, message: String, throwable: Throwable? = null) {
        Log.w(prefixed(tag), message, throwable)
    }

    fun e(tag: String, message: String, throwable: Throwable? = null) {
        Log.e(prefixed(tag), message, throwable)
    }

    /**
     * Logs at most once per [windowMs] for the given [key]. Intended for hot loops where the same
     * failure can repeat every frame. Defaults to error level; pass [level] to lower it.
     */
    fun rateLimited(
        key: String,
        tag: String,
        message: String,
        throwable: Throwable? = null,
        windowMs: Long = DEFAULT_RATE_LIMIT_MS,
        level: Int = Log.ERROR
    ) {
        val now = System.currentTimeMillis()
        val last = lastLogAtByKey[key]
        if (last != null && now - last < windowMs) return
        lastLogAtByKey[key] = now
        when (level) {
            Log.VERBOSE, Log.DEBUG -> if (BuildConfig.DEBUG) Log.println(level, prefixed(tag), render(message, throwable))
            else -> Log.println(level, prefixed(tag), render(message, throwable))
        }
    }

    private fun render(message: String, throwable: Throwable?): String {
        return if (throwable == null) message else "$message\n${Log.getStackTraceString(throwable)}"
    }

    private fun prefixed(tag: String): String {
        val combined = "$TAG_PREFIX-$tag"
        return if (combined.length <= MAX_TAG_LENGTH) combined else combined.substring(0, MAX_TAG_LENGTH)
    }

    private const val DEFAULT_RATE_LIMIT_MS = 3_000L
}
