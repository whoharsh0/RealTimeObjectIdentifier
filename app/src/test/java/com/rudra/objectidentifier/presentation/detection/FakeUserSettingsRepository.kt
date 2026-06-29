package com.rudra.objectidentifier.presentation.detection

import com.rudra.objectidentifier.domain.model.UserSettings
import com.rudra.objectidentifier.domain.repository.UserSettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeUserSettingsRepository(
    initial: UserSettings = UserSettings()
) : UserSettingsRepository {

    private val _settings = MutableStateFlow(initial)
    override val settings: Flow<UserSettings> = _settings.asStateFlow()

    override fun currentSettings(): UserSettings = _settings.value

    override suspend fun applySettings(transform: (UserSettings) -> UserSettings) {
        _settings.value = transform(_settings.value)
    }

    override suspend fun resetToDefaults() {
        _settings.value = UserSettings()
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
}
