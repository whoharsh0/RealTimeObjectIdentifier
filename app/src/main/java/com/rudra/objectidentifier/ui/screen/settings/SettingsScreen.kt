package com.rudra.objectidentifier.ui.screen.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rudra.objectidentifier.R
import com.rudra.objectidentifier.domain.model.UserSettings
import com.rudra.objectidentifier.presentation.settings.SettingsUiState
import com.rudra.objectidentifier.presentation.settings.SettingsViewModel
import com.rudra.objectidentifier.ui.theme.AccentCyan
import com.rudra.objectidentifier.ui.theme.GradientEnd
import com.rudra.objectidentifier.ui.theme.GradientStart
import com.rudra.objectidentifier.ui.theme.NightSurfaceElevated
import com.rudra.objectidentifier.ui.theme.RealTimeObjectIdentifierTheme
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SettingsScreenContent(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onConfidenceChanged = viewModel::onConfidenceChanged,
        onMaxDetectionsChanged = viewModel::onMaxDetectionsChanged,
        onShowConfidenceChanged = viewModel::onShowConfidenceChanged,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsScreenContent(
    uiState: SettingsUiState,
    onNavigateBack: () -> Unit,
    onConfidenceChanged: (Float) -> Unit,
    onMaxDetectionsChanged: (Int) -> Unit,
    onShowConfidenceChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Brush.verticalGradient(listOf(GradientStart, GradientEnd)))
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            SettingsControlCard(title = stringResource(R.string.settings_detection)) {
                Text(
                    text = stringResource(
                        R.string.settings_confidence_value,
                        (uiState.confidenceThreshold * 100).roundToInt()
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Slider(
                    value = uiState.confidenceThreshold,
                    onValueChange = onConfidenceChanged,
                    valueRange = UserSettings.MIN_CONFIDENCE..UserSettings.MAX_CONFIDENCE
                )

                Text(
                    text = stringResource(R.string.settings_max_detections_value, uiState.maxDetections),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Slider(
                    value = uiState.maxDetections.toFloat(),
                    onValueChange = { onMaxDetectionsChanged(it.roundToInt()) },
                    valueRange = UserSettings.MIN_MAX_DETECTIONS.toFloat()..UserSettings.MAX_MAX_DETECTIONS.toFloat(),
                    steps = UserSettings.MAX_MAX_DETECTIONS - UserSettings.MIN_MAX_DETECTIONS - 1
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.settings_show_confidence),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Switch(
                        checked = uiState.showConfidencePercent,
                        onCheckedChange = onShowConfidenceChanged
                    )
                }
            }

            SettingsInfoCard(
                icon = Icons.Default.OfflineBolt,
                title = stringResource(R.string.settings_offline_title),
                description = stringResource(R.string.settings_offline_body)
            )
            SettingsInfoCard(
                icon = Icons.Default.PrivacyTip,
                title = stringResource(R.string.settings_privacy_title),
                description = stringResource(R.string.settings_privacy_body)
            )
        }
    }
}

@Composable
private fun SettingsControlCard(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = NightSurfaceElevated.copy(alpha = 0.92f)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = title, style = MaterialTheme.typography.titleMedium, color = AccentCyan)
            content()
        }
    }
}

@Composable
private fun SettingsInfoCard(
    icon: ImageVector,
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = NightSurfaceElevated.copy(alpha = 0.92f)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = AccentCyan)
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    RealTimeObjectIdentifierTheme {
        SettingsScreenContent(
            uiState = SettingsUiState(
                confidenceThreshold = 0.55f,
                maxDetections = 5,
                showConfidencePercent = true
            ),
            onNavigateBack = {},
            onConfidenceChanged = {},
            onMaxDetectionsChanged = {},
            onShowConfidenceChanged = {}
        )
    }
}
