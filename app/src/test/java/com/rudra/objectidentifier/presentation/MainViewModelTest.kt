package com.rudra.objectidentifier.presentation

import com.rudra.objectidentifier.core.AppInfoProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

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
    fun uiState_loadsAppInfoFromInjectedProvider() = runTest(testDispatcher) {
        val viewModel = MainViewModel(
            appInfoProvider = AppInfoProvider(),
            ioDispatcher = testDispatcher
        )

        advanceUntilIdle()

        assertEquals("Real-Time Object Identifier", viewModel.uiState.value.title)
        assertEquals("1.0.0", viewModel.uiState.value.version)
    }
}
