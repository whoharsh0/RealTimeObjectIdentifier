package com.rudra.objectidentifier.presentation.settings

import com.rudra.objectidentifier.core.AppInfoProvider
import com.rudra.objectidentifier.domain.model.UserSettings
import com.rudra.objectidentifier.presentation.detection.FakeUserSettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test
import app.cash.turbine.test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeUserSettingsRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeUserSettingsRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() =
        SettingsViewModel(repository, AppInfoProvider(), testDispatcher)

    @Test
    fun uiState_emitsInitialSettings() = runTest(testDispatcher) {
        val viewModel = createViewModel()

        viewModel.uiState.test {
            val initial = awaitItem()
            assertEquals(UserSettings.DEFAULT_CONFIDENCE, initial.confidenceThreshold)
            assertEquals(10, initial.maxDetections)
            assertEquals(true, initial.showConfidencePercent)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun onConfidenceChanged_updatesRepositoryAndUiState() = runTest(testDispatcher) {
        val viewModel = createViewModel()

        viewModel.uiState.test {
            awaitItem()
            viewModel.onConfidenceChanged(0.65f)
            testScheduler.advanceUntilIdle()

            val updated = expectMostRecentItem()
            assertEquals(0.65f, updated.confidenceThreshold)
            assertEquals(0.65f, repository.currentSettings().confidenceThreshold)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun onShowConfidenceChanged_updatesToggle() = runTest(testDispatcher) {
        val viewModel = createViewModel()

        viewModel.uiState.test {
            awaitItem()
            viewModel.onShowConfidenceChanged(false)
            testScheduler.advanceUntilIdle()

            val updated = expectMostRecentItem()
            assertFalse(updated.showConfidencePercent)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
