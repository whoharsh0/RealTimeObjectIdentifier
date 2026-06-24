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

    override suspend fun setConfidenceThreshold(value: Float) {
        _settings.value = _settings.value.copy(confidenceThreshold = value)
    }

    override suspend fun setMaxDetections(value: Int) {
        _settings.value = _settings.value.copy(maxDetections = value)
    }

    override suspend fun setShowConfidencePercent(value: Boolean) {
        _settings.value = _settings.value.copy(showConfidencePercent = value)
    }

    override suspend fun setHasSeenOnboarding(value: Boolean) {
        _settings.value = _settings.value.copy(hasSeenOnboarding = value)
    }
}
