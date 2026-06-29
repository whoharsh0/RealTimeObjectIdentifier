package com.rudra.objectidentifier.ui.screen.about

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.OfflineBolt
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rudra.objectidentifier.R
import com.rudra.objectidentifier.presentation.settings.SettingsViewModel
import com.rudra.objectidentifier.ui.theme.AccentCyan
import com.rudra.objectidentifier.ui.theme.GradientEnd
import com.rudra.objectidentifier.ui.theme.GradientStart
import com.rudra.objectidentifier.ui.theme.NightSurfaceElevated

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.about_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Brush.verticalGradient(listOf(GradientStart, GradientEnd)))
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            AboutCard(
                title = stringResource(R.string.about_version, uiState.versionName),
                body = stringResource(R.string.about_model_info)
            )
            AboutCard(
                title = stringResource(R.string.settings_offline_title),
                body = stringResource(R.string.about_how_it_works),
                icon = { Icon(Icons.Default.OfflineBolt, null, tint = AccentCyan) }
            )
            AboutCard(
                title = stringResource(R.string.settings_privacy_title),
                body = stringResource(R.string.settings_privacy_body),
                icon = { Icon(Icons.Default.PrivacyTip, null, tint = AccentCyan) }
            )
            AboutCard(
                title = stringResource(R.string.about_licenses_title),
                body = stringResource(R.string.about_licenses_body)
            )
        }
    }
}

@Composable
private fun AboutCard(
    title: String,
    body: String,
    icon: @Composable (() -> Unit)? = null
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = NightSurfaceElevated.copy(alpha = 0.92f)
    ) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            icon?.invoke()
            Text(text = title, style = MaterialTheme.typography.titleMedium, color = Color.White)
            Text(
                text = body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
