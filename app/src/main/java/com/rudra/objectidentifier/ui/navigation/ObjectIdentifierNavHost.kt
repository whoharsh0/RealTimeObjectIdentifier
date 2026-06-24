package com.rudra.objectidentifier.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.rudra.objectidentifier.ui.screen.detection.DetectionScreen
import com.rudra.objectidentifier.ui.screen.settings.SettingsScreen

@Composable
fun ObjectIdentifierNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = ObjectIdentifierDestination.DETECTION,
        modifier = modifier
    ) {
        composable(ObjectIdentifierDestination.DETECTION) {
            DetectionScreen(
                onOpenSettings = {
                    navController.navigate(ObjectIdentifierDestination.SETTINGS)
                }
            )
        }
        composable(ObjectIdentifierDestination.SETTINGS) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
