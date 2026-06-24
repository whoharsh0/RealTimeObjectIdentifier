package com.rudra.objectidentifier.ui.screen.detection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rudra.objectidentifier.presentation.detection.DetectionUiState
import com.rudra.objectidentifier.presentation.detection.DetectionViewModel
import com.rudra.objectidentifier.ui.theme.RealTimeObjectIdentifierTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetectionScreen(
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DetectionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    DetectionScreenContent(
        uiState = uiState,
        onOpenSettings = onOpenSettings,
        onStartDetection = viewModel::onStartDetection,
        onStopDetection = viewModel::onStopDetection,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetectionScreenContent(
    uiState: DetectionUiState,
    onOpenSettings: () -> Unit,
    onStartDetection: () -> Unit,
    onStopDetection: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(uiState.appTitle.ifBlank { "Object Identifier" }) },
                actions = {
                    IconButton(onClick = onOpenSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "v${uiState.versionName}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = if (uiState.isDetecting) {
                            "Detection active — camera pipeline coming in Phase 3"
                        } else {
                            "Tap Start to prepare the detection pipeline"
                        },
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Text(
                        text = "Objects detected: ${uiState.detections.size}",
                        style = MaterialTheme.typography.titleMedium
                    )

                    uiState.errorMessage?.let { message ->
                        Text(
                            text = message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    if (uiState.isDetecting) {
                        Button(onClick = onStopDetection) {
                            Text("Stop Detection")
                        }
                    } else {
                        Button(onClick = onStartDetection) {
                            Text("Start Detection")
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DetectionScreenPreview() {
    RealTimeObjectIdentifierTheme {
        DetectionScreenContent(
            uiState = DetectionUiState(
                isLoading = false,
                appTitle = "Real-Time Object Identifier",
                versionName = "1.0.0",
                isDetecting = false
            ),
            onOpenSettings = {},
            onStartDetection = {},
            onStopDetection = {}
        )
    }
}
