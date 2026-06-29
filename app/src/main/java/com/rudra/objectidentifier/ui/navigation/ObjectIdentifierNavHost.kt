package com.rudra.objectidentifier.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.rudra.objectidentifier.presentation.settings.SettingsViewModel
import com.rudra.objectidentifier.ui.screen.about.AboutScreen
import com.rudra.objectidentifier.ui.screen.detection.DetectionScreen
import com.rudra.objectidentifier.ui.screen.history.HistoryScreen
import com.rudra.objectidentifier.ui.screen.settings.SettingsScreen
import com.rudra.objectidentifier.ui.theme.RealTimeObjectIdentifierTheme

@Composable
fun ObjectIdentifierNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val settings by settingsViewModel.uiState.collectAsStateWithLifecycle()

    RealTimeObjectIdentifierTheme(
        appTheme = settings.appTheme,
        reduceMotion = settings.reduceMotion
    ) {
        NavHost(
            navController = navController,
            startDestination = ObjectIdentifierDestination.DETECTION,
            modifier = modifier
        ) {
            composable(ObjectIdentifierDestination.DETECTION) {
                DetectionScreen(
                    onOpenSettings = { navController.navigate(ObjectIdentifierDestination.SETTINGS) },
                    onOpenHistory = { navController.navigate(ObjectIdentifierDestination.HISTORY) }
                )
            }
            composable(ObjectIdentifierDestination.SETTINGS) {
                SettingsScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onOpenAbout = { navController.navigate(ObjectIdentifierDestination.ABOUT) },
                    onOpenHistory = { navController.navigate(ObjectIdentifierDestination.HISTORY) }
                )
            }
            composable(ObjectIdentifierDestination.HISTORY) {
                HistoryScreen(onNavigateBack = { navController.popBackStack() })
            }
            composable(ObjectIdentifierDestination.ABOUT) {
                AboutScreen(onNavigateBack = { navController.popBackStack() })
            }
        }
    }
}
