package com.rudra.objectidentifier.domain.repository

import com.rudra.objectidentifier.domain.model.UserSettings
import kotlinx.coroutines.flow.Flow

interface UserSettingsRepository {
    val settings: Flow<UserSettings>
    fun currentSettings(): UserSettings
    suspend fun setConfidenceThreshold(value: Float)
    suspend fun setMaxDetections(value: Int)
    suspend fun setShowConfidencePercent(value: Boolean)
    suspend fun setHasSeenOnboarding(value: Boolean)
}
