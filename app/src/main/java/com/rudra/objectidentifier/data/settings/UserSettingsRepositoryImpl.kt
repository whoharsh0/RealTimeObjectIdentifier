package com.rudra.objectidentifier.data.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
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
import com.rudra.objectidentifier.di.ApplicationScope

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

    private val _settings = MutableStateFlow(UserSettings())
    override val settings: Flow<UserSettings> = _settings.asStateFlow()

    init {
        applicationScope.launch {
            context.userSettingsDataStore.data
                .map { preferences ->
                    UserSettings(
                        confidenceThreshold = preferences[confidenceKey]
                            ?: UserSettings.DEFAULT_CONFIDENCE,
                        maxDetections = preferences[maxDetectionsKey]
                            ?: UserSettings.DEFAULT_MAX_DETECTIONS,
                        showConfidencePercent = preferences[showConfidenceKey] ?: true,
                        hasSeenOnboarding = preferences[onboardingKey] ?: false
                    )
                }
                .collect { _settings.value = it }
        }
    }

    override fun currentSettings(): UserSettings = _settings.value

    override suspend fun setConfidenceThreshold(value: Float) {
        context.userSettingsDataStore.edit { prefs ->
            prefs[confidenceKey] = value.coerceIn(
                UserSettings.MIN_CONFIDENCE,
                UserSettings.MAX_CONFIDENCE
            )
        }
    }

    override suspend fun setMaxDetections(value: Int) {
        context.userSettingsDataStore.edit { prefs ->
            prefs[maxDetectionsKey] = value.coerceIn(
                UserSettings.MIN_MAX_DETECTIONS,
                UserSettings.MAX_MAX_DETECTIONS
            )
        }
    }

    override suspend fun setShowConfidencePercent(value: Boolean) {
        context.userSettingsDataStore.edit { prefs ->
            prefs[showConfidenceKey] = value
        }
    }

    override suspend fun setHasSeenOnboarding(value: Boolean) {
        context.userSettingsDataStore.edit { prefs ->
            prefs[onboardingKey] = value
        }
    }
}
