package com.rudra.objectidentifier.ui.screen.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.OfflineBolt
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rudra.objectidentifier.R
import com.rudra.objectidentifier.domain.model.AppTheme
import com.rudra.objectidentifier.domain.model.BoxStyle
import com.rudra.objectidentifier.domain.model.CameraLens
import com.rudra.objectidentifier.domain.model.InferenceDelegate
import com.rudra.objectidentifier.domain.model.ModelVariant
import com.rudra.objectidentifier.domain.model.ScanMode
import com.rudra.objectidentifier.domain.model.UserSettings
import com.rudra.objectidentifier.presentation.settings.SettingsViewModel
import com.rudra.objectidentifier.ui.theme.AccentCyan
import com.rudra.objectidentifier.ui.theme.ErrorRed
import com.rudra.objectidentifier.ui.theme.GradientEnd
import com.rudra.objectidentifier.ui.theme.GradientStart
import com.rudra.objectidentifier.ui.theme.NightSurfaceElevated
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onOpenAbout: () -> Unit = {},
    onOpenHistory: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showResetDialog by remember { mutableStateOf(false) }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset to defaults?") },
            text = { Text("All settings will be restored to their default values.") },
            confirmButton = {
                TextButton(onClick = { viewModel.onResetSettings(); showResetDialog = false }) {
                    Text("Reset", color = ErrorRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showResetDialog = true }) {
                        Icon(Icons.Default.RestartAlt, "Reset settings", tint = ErrorRed)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Brush.verticalGradient(listOf(GradientStart, GradientEnd)))
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SettingsSection("Detection", Icons.Default.Tune) {
                SliderRow("Confidence threshold", (uiState.confidenceThreshold * 100).roundToInt(), "%",
                    (UserSettings.MIN_CONFIDENCE * 100f)..(UserSettings.MAX_CONFIDENCE * 100f)) {
                    viewModel.onConfidenceChanged(it / 100f)
                }
                SliderRow("Max detections", uiState.maxDetections, "",
                    UserSettings.MIN_MAX_DETECTIONS.toFloat()..UserSettings.MAX_MAX_DETECTIONS.toFloat(),
                    steps = UserSettings.MAX_MAX_DETECTIONS - UserSettings.MIN_MAX_DETECTIONS - 1) {
                    viewModel.onMaxDetectionsChanged(it.roundToInt())
                }
                SliderRow("Min label confidence", (uiState.minConfidenceForLabel * 100).roundToInt(), "%",
                    (UserSettings.MIN_MIN_LABEL_CONFIDENCE * 100f)..(UserSettings.MAX_MIN_LABEL_CONFIDENCE * 100f)) {
                    viewModel.onMinLabelConfidenceChanged(it / 100f)
                }
                SwitchRow("Show confidence %", uiState.showConfidencePercent, viewModel::onShowConfidenceChanged)
                SwitchRow("Bounding box smoothing", uiState.enableSmoothing, viewModel::onSmoothingChanged)
            }

            SettingsSection("Scan Mode", Icons.Default.Search) {
                Text("Adjusts confidence thresholds per scene type",
                    style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                ChipRow(ScanMode.entries, uiState.scanMode, { it.displayName }, viewModel::onScanModeChanged)
                OutlinedTextField(
                    value = uiState.filterLabel,
                    onValueChange = { if (it.length <= UserSettings.MAX_FILTER_LABEL_LENGTH) viewModel.onFilterLabelChanged(it) },
                    label = { Text("Show only label (leave blank = all)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            SettingsSection("ML Model & Engine", Icons.Default.Build) {
                Label("Model — more accurate = slower on CPU")
                ChipRow(ModelVariant.entries, uiState.modelVariant, { it.displayName }, viewModel::onModelVariantChanged)
                if (uiState.modelVariant == ModelVariant.LITE4) {
                    Text("⚡ Lite4 at 640px needs GPU delegate for smooth FPS on most phones.",
                        style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.tertiary)
                }
                Spacer(Modifier.height(4.dp))
                Label("Inference delegate")
                ChipRow(InferenceDelegate.entries, uiState.inferenceDelegate, { it.displayName }, viewModel::onInferenceDelegateChanged)
                SwitchRow("Frame skip (stabilises FPS)", uiState.enableFrameSkip, viewModel::onFrameSkipChanged)
            }

            SettingsSection("Scanning Features", Icons.Default.QrCodeScanner) {
                SwitchRow("Barcode & QR scanning", uiState.enableBarcodeScanning, viewModel::onBarcodeScanningChanged)
                SwitchRow("Text recognition (OCR)", uiState.enableOcrMode, viewModel::onOcrModeChanged)
                SwitchRow("Scene description caption", uiState.showSceneDescription, viewModel::onSceneDescriptionChanged)
            }

            SettingsSection("Display & Appearance", Icons.Default.Palette) {
                Label("App theme")
                ChipRow(AppTheme.entries, uiState.appTheme, { it.displayName }, viewModel::onAppThemeChanged)
                Spacer(Modifier.height(4.dp))
                Label("Box style")
                ChipRow(BoxStyle.entries, uiState.boxStyle, { it.displayName }, viewModel::onBoxStyleChanged)
                SliderRow("Label text scale", (uiState.labelScale * 100).roundToInt(), "%",
                    (UserSettings.MIN_LABEL_SCALE * 100f)..(UserSettings.MAX_LABEL_SCALE * 100f)) {
                    viewModel.onLabelScaleChanged(it / 100f)
                }
                SwitchRow("Show FPS / latency overlay", uiState.showFpsOverlay, viewModel::onShowFpsChanged)
            }

            SettingsSection("Camera & Behaviour", Icons.Default.Camera) {
                Label("Default camera lens")
                ChipRow(CameraLens.entries, uiState.defaultCameraLens,
                    { it.name.lowercase().replaceFirstChar { c -> c.uppercaseChar() } },
                    viewModel::onDefaultCameraChanged)
                SwitchRow("Keep screen on", uiState.keepScreenOn, viewModel::onKeepScreenOnChanged)
                SwitchRow("Auto-start scanning on open", uiState.autoStartScanning, viewModel::onAutoStartChanged)
                SwitchRow("Haptic feedback", uiState.hapticFeedback, viewModel::onHapticChanged)
            }

            SettingsSection("Accessibility", Icons.Default.Accessibility) {
                SwitchRow("High contrast bounding boxes", uiState.highContrastMode, viewModel::onHighContrastChanged)
                SwitchRow("Reduce motion", uiState.reduceMotion, viewModel::onReduceMotionChanged)
                SwitchRow("Speak detected labels (TTS)", uiState.speakLabels, viewModel::onSpeakLabelsChanged)
            }

            SettingsSection("History & Privacy", Icons.Default.History) {
                SwitchRow("Save scan history to device", uiState.enableHistory, viewModel::onHistoryChanged)
            }

            SettingsSection("Performance", Icons.Default.BatteryAlert) {
                SwitchRow("Battery saver (4 FPS cap)", uiState.batterySaverMode, viewModel::onBatterySaverChanged)
            }

            SettingsInfoCard(Icons.Default.OfflineBolt,
                stringResource(R.string.settings_offline_title),
                stringResource(R.string.settings_offline_body))
            SettingsInfoCard(Icons.Default.PrivacyTip,
                stringResource(R.string.settings_privacy_title),
                stringResource(R.string.settings_privacy_body))

            if (uiState.versionName.isNotBlank()) {
                Text("v${uiState.versionName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 4.dp))
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SettingsSection(title: String, icon: ImageVector, content: @Composable () -> Unit) {
    Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), color = NightSurfaceElevated.copy(alpha = 0.92f)) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(icon, null, tint = AccentCyan)
                Text(title, style = MaterialTheme.typography.titleMedium, color = AccentCyan)
            }
            content()
        }
    }
}

@Composable
private fun SettingsInfoCard(icon: ImageVector, title: String, description: String) {
    Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), color = NightSurfaceElevated.copy(alpha = 0.92f)) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(icon, null, tint = AccentCyan)
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun SwitchRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(label, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f).padding(end = 8.dp))
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun SliderRow(
    label: String, value: Int, unit: String,
    valueRange: ClosedFloatingPointRange<Float>, steps: Int = 0,
    onValueChange: (Float) -> Unit
) {
    Column {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
            Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("$value$unit", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Slider(value = value.toFloat(), onValueChange = onValueChange, valueRange = valueRange, steps = steps)
    }
}

@Composable
private fun Label(text: String) {
    Text(text, style = MaterialTheme.typography.labelMedium, color = AccentCyan)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <T> ChipRow(options: List<T>, selected: T, label: (T) -> String, onSelect: (T) -> Unit) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        options.forEach { option ->
            FilterChip(
                selected = option == selected,
                onClick = { onSelect(option) },
                label = { Text(label(option), style = MaterialTheme.typography.labelSmall) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = AccentCyan.copy(alpha = 0.22f),
                    selectedLabelColor = AccentCyan
                )
            )
        }
    }
}
