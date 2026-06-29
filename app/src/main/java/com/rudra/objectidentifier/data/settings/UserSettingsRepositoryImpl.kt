package com.rudra.objectidentifier.data.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.rudra.objectidentifier.core.SettingsValidator
import com.rudra.objectidentifier.di.ApplicationScope
import com.rudra.objectidentifier.domain.model.AppTheme
import com.rudra.objectidentifier.domain.model.BoxStyle
import com.rudra.objectidentifier.domain.model.CameraLens
import com.rudra.objectidentifier.domain.model.InferenceDelegate
import com.rudra.objectidentifier.domain.model.ModelVariant
import com.rudra.objectidentifier.domain.model.ScanMode
import com.rudra.objectidentifier.domain.model.UserSettings
import com.rudra.objectidentifier.domain.repository.UserSettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

private val Context.userSettingsDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "user_settings"
)

@Singleton
class UserSettingsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    @ApplicationScope private val applicationScope: CoroutineScope
) : UserSettingsRepository {

    private val confidenceKey = floatPreferencesKey("confidence_threshold")
    private val maxDetectionsKey = intPreferencesKey("max_detections")
    private val showConfidenceKey = booleanPreferencesKey("show_confidence_percent")
    private val onboardingKey = booleanPreferencesKey("has_seen_onboarding")
    private val scanModeKey = stringPreferencesKey("scan_mode")
    private val modelVariantKey = stringPreferencesKey("model_variant")
    private val inferenceDelegateKey = stringPreferencesKey("inference_delegate")
    private val smoothingKey = booleanPreferencesKey("enable_smoothing")
    private val frameSkipKey = booleanPreferencesKey("enable_frame_skip")
    private val batterySaverKey = booleanPreferencesKey("battery_saver_mode")
    private val autoStartKey = booleanPreferencesKey("auto_start_scanning")
    private val keepScreenOnKey = booleanPreferencesKey("keep_screen_on")
    private val showFpsKey = booleanPreferencesKey("show_fps_overlay")
    private val minLabelConfidenceKey = floatPreferencesKey("min_confidence_for_label")
    private val minBoxSizeKey = floatPreferencesKey("min_box_size_percent")
    private val appThemeKey = stringPreferencesKey("app_theme")
    private val boxStyleKey = stringPreferencesKey("box_style")
    private val labelScaleKey = floatPreferencesKey("label_scale")
    private val hapticKey = booleanPreferencesKey("haptic_feedback")
    private val defaultCameraKey = stringPreferencesKey("default_camera_lens")
    private val historyKey = booleanPreferencesKey("enable_history")
    private val highContrastKey = booleanPreferencesKey("high_contrast_mode")
    private val reduceMotionKey = booleanPreferencesKey("reduce_motion")
    private val roiKey = booleanPreferencesKey("region_of_interest_enabled")
    private val filterLabelKey = stringPreferencesKey("filter_label")
    private val sceneDescriptionKey = booleanPreferencesKey("show_scene_description")
    private val speakLabelsKey = booleanPreferencesKey("speak_labels")
    private val barcodeScanKey = booleanPreferencesKey("enable_barcode_scanning")
    private val ocrModeKey = booleanPreferencesKey("enable_ocr_mode")

    private val _settings = MutableStateFlow(UserSettings())
    override val settings: Flow<UserSettings> = _settings.asStateFlow()

    init {
        applicationScope.launch {
            context.userSettingsDataStore.data
                .map { preferences -> preferences.toUserSettings() }
                .collect { loaded -> _settings.value = SettingsValidator.sanitize(loaded) }
        }
    }

    override fun currentSettings(): UserSettings = _settings.value

    override suspend fun applySettings(transform: (UserSettings) -> UserSettings) {
        val sanitized = SettingsValidator.sanitize(transform(_settings.value))
        context.userSettingsDataStore.edit { prefs -> prefs.writeSettings(sanitized) }
    }

    override suspend fun resetToDefaults() {
        applySettings { UserSettings() }
    }

    override suspend fun setConfidenceThreshold(value: Float) {
        applySettings { it.copy(confidenceThreshold = value) }
    }

    override suspend fun setMaxDetections(value: Int) {
        applySettings { it.copy(maxDetections = value) }
    }

    override suspend fun setShowConfidencePercent(value: Boolean) {
        applySettings { it.copy(showConfidencePercent = value) }
    }

    override suspend fun setHasSeenOnboarding(value: Boolean) {
        applySettings { it.copy(hasSeenOnboarding = value) }
    }

    private fun Preferences.toUserSettings(): UserSettings {
        return UserSettings(
            confidenceThreshold = this[confidenceKey] ?: UserSettings.DEFAULT_CONFIDENCE,
            maxDetections = this[maxDetectionsKey] ?: UserSettings.DEFAULT_MAX_DETECTIONS,
            showConfidencePercent = this[showConfidenceKey] ?: true,
            hasSeenOnboarding = this[onboardingKey] ?: false,
            scanMode = enumValueOrDefault(this[scanModeKey], ScanMode.GENERAL),
            modelVariant = enumValueOrDefault(this[modelVariantKey], ModelVariant.LITE0),
            inferenceDelegate = enumValueOrDefault(this[inferenceDelegateKey], InferenceDelegate.NNAPI),
            enableSmoothing = this[smoothingKey] ?: true,
            enableFrameSkip = this[frameSkipKey] ?: true,
            batterySaverMode = this[batterySaverKey] ?: false,
            autoStartScanning = this[autoStartKey] ?: false,
            keepScreenOn = this[keepScreenOnKey] ?: true,
            showFpsOverlay = this[showFpsKey] ?: false,
            minConfidenceForLabel = this[minLabelConfidenceKey] ?: UserSettings.DEFAULT_MIN_LABEL_CONFIDENCE,
            minBoxSizePercent = this[minBoxSizeKey] ?: UserSettings.DEFAULT_MIN_BOX_SIZE,
            appTheme = enumValueOrDefault(this[appThemeKey], AppTheme.DARK),
            boxStyle = enumValueOrDefault(this[boxStyleKey], BoxStyle.FULL),
            labelScale = this[labelScaleKey] ?: UserSettings.DEFAULT_LABEL_SCALE,
            hapticFeedback = this[hapticKey] ?: true,
            defaultCameraLens = enumValueOrDefault(this[defaultCameraKey], CameraLens.BACK),
            enableHistory = this[historyKey] ?: true,
            highContrastMode = this[highContrastKey] ?: false,
            reduceMotion = this[reduceMotionKey] ?: false,
            regionOfInterestEnabled = this[roiKey] ?: false,
            filterLabel = this[filterLabelKey].orEmpty(),
            showSceneDescription = this[sceneDescriptionKey] ?: true,
            speakLabels = this[speakLabelsKey] ?: false,
            enableBarcodeScanning = this[barcodeScanKey] ?: true,
            enableOcrMode = this[ocrModeKey] ?: false
        )
    }

    private fun MutablePreferences.writeSettings(settings: UserSettings) {
        this[confidenceKey] = settings.confidenceThreshold
        this[maxDetectionsKey] = settings.maxDetections
        this[showConfidenceKey] = settings.showConfidencePercent
        this[onboardingKey] = settings.hasSeenOnboarding
        this[scanModeKey] = settings.scanMode.name
        this[modelVariantKey] = settings.modelVariant.name
        this[inferenceDelegateKey] = settings.inferenceDelegate.name
        this[smoothingKey] = settings.enableSmoothing
        this[frameSkipKey] = settings.enableFrameSkip
        this[batterySaverKey] = settings.batterySaverMode
        this[autoStartKey] = settings.autoStartScanning
        this[keepScreenOnKey] = settings.keepScreenOn
        this[showFpsKey] = settings.showFpsOverlay
        this[minLabelConfidenceKey] = settings.minConfidenceForLabel
        this[minBoxSizeKey] = settings.minBoxSizePercent
        this[appThemeKey] = settings.appTheme.name
        this[boxStyleKey] = settings.boxStyle.name
        this[labelScaleKey] = settings.labelScale
        this[hapticKey] = settings.hapticFeedback
        this[defaultCameraKey] = settings.defaultCameraLens.name
        this[historyKey] = settings.enableHistory
        this[highContrastKey] = settings.highContrastMode
        this[reduceMotionKey] = settings.reduceMotion
        this[roiKey] = settings.regionOfInterestEnabled
        this[filterLabelKey] = settings.filterLabel
        this[sceneDescriptionKey] = settings.showSceneDescription
        this[speakLabelsKey] = settings.speakLabels
        this[barcodeScanKey] = settings.enableBarcodeScanning
        this[ocrModeKey] = settings.enableOcrMode
    }

    private inline fun <reified T : Enum<T>> enumValueOrDefault(raw: String?, fallback: T): T {
        if (raw.isNullOrBlank()) return fallback
        return enumValues<T>().firstOrNull { it.name.equals(raw, ignoreCase = true) } ?: fallback
    }
}
