package com.rudra.objectidentifier.core

import com.rudra.objectidentifier.domain.model.AppTheme
import com.rudra.objectidentifier.domain.model.BoxStyle
import com.rudra.objectidentifier.domain.model.CameraLens
import com.rudra.objectidentifier.domain.model.ModelVariant
import com.rudra.objectidentifier.domain.model.ScanMode
import com.rudra.objectidentifier.domain.model.UserSettings

object SettingsValidator {

    fun sanitize(settings: UserSettings): UserSettings {
        return settings.copy(
            confidenceThreshold = settings.confidenceThreshold.coerceIn(
                UserSettings.MIN_CONFIDENCE,
                UserSettings.MAX_CONFIDENCE
            ),
            maxDetections = settings.maxDetections.coerceIn(
                UserSettings.MIN_MAX_DETECTIONS,
                UserSettings.MAX_MAX_DETECTIONS
            ),
            minConfidenceForLabel = settings.minConfidenceForLabel.coerceIn(
                UserSettings.MIN_MIN_LABEL_CONFIDENCE,
                UserSettings.MAX_MIN_LABEL_CONFIDENCE
            ),
            minBoxSizePercent = settings.minBoxSizePercent.coerceIn(
                UserSettings.MIN_BOX_SIZE,
                UserSettings.MAX_BOX_SIZE
            ),
            labelScale = settings.labelScale.coerceIn(
                UserSettings.MIN_LABEL_SCALE,
                UserSettings.MAX_LABEL_SCALE
            ),
            filterLabel = settings.filterLabel
                .trim()
                .take(UserSettings.MAX_FILTER_LABEL_LENGTH),
            scanMode = parseEnum(settings.scanMode.name, ScanMode.GENERAL),
            modelVariant = parseEnum(settings.modelVariant.name, ModelVariant.LITE0),
            appTheme = parseEnum(settings.appTheme.name, AppTheme.DARK),
            boxStyle = parseEnum(settings.boxStyle.name, BoxStyle.FULL),
            defaultCameraLens = parseEnum(settings.defaultCameraLens.name, CameraLens.BACK)
        )
    }

    private inline fun <reified T : Enum<T>> parseEnum(raw: String, fallback: T): T {
        return enumValues<T>().firstOrNull { it.name.equals(raw, ignoreCase = true) } ?: fallback
    }
}

inline fun <T> runSafely(
    fallback: T,
    noinline onError: ((Throwable) -> Unit)? = null,
    block: () -> T
): T {
    return runCatching(block).getOrElse { error ->
        onError?.invoke(error)
        fallback
    }
}
