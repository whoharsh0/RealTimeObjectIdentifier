package com.rudra.objectidentifier.presentation.detection

import com.rudra.objectidentifier.core.AppInfoProvider
import com.rudra.objectidentifier.data.repository.DetectionRepositoryImpl
import com.rudra.objectidentifier.domain.model.DetectedObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DetectionViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun uiState_loadsAppInfoAndStartsIdle() = runTest(testDispatcher) {
        val viewModel = DetectionViewModel(
            detectionRepository = DetectionRepositoryImpl(),
            appInfoProvider = AppInfoProvider(),
            ioDispatcher = testDispatcher
        )

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals("Real-Time Object Identifier", state.appTitle)
        assertEquals("1.0.0", state.versionName)
        assertFalse(state.isDetecting)
        assertTrue(state.detections.isEmpty())
    }

    @Test
    fun onStartDetection_setsIsDetectingTrue() = runTest(testDispatcher) {
        val repository = DetectionRepositoryImpl()
        val viewModel = DetectionViewModel(
            detectionRepository = repository,
            appInfoProvider = AppInfoProvider(),
            ioDispatcher = testDispatcher
        )

        advanceUntilIdle()
        viewModel.onStartDetection()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isDetecting)
        assertEquals(emptyList<DetectedObject>(), repository.observeDetections().first())
    }
}
