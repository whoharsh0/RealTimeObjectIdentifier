package com.rudra.objectidentifier.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rudra.objectidentifier.core.AppInfoProvider
import com.rudra.objectidentifier.di.IoDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class MainUiState(
    val title: String = "",
    val version: String = ""
)

@HiltViewModel
class MainViewModel @Inject constructor(
    private val appInfoProvider: AppInfoProvider,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        loadAppInfo()
    }

    private fun loadAppInfo() {
        viewModelScope.launch {
            val info = withContext(ioDispatcher) {
                MainUiState(
                    title = appInfoProvider.getAppTitle(),
                    version = appInfoProvider.getVersionName()
                )
            }
            _uiState.value = info
        }
    }
}
