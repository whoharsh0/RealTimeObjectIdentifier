package com.rudra.objectidentifier.ui.screen.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rudra.objectidentifier.domain.model.ScanHistoryEntry
import com.rudra.objectidentifier.presentation.history.HistoryViewModel
import com.rudra.objectidentifier.ui.theme.AccentCyan
import com.rudra.objectidentifier.ui.theme.ErrorRed
import com.rudra.objectidentifier.ui.theme.GradientEnd
import com.rudra.objectidentifier.ui.theme.GradientStart
import com.rudra.objectidentifier.ui.theme.NightSurfaceElevated
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showClearDialog by remember { mutableStateOf(false) }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Clear history?") },
            text = { Text("All scan history will be permanently deleted.") },
            confirmButton = {
                TextButton(onClick = { viewModel.onClearHistory(); showClearDialog = false }) {
                    Text("Clear", color = ErrorRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text("Scan History") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    if (uiState.entries.isNotEmpty()) {
                        IconButton(onClick = { showClearDialog = true }) {
                            Icon(Icons.Default.Delete, "Clear history", tint = ErrorRed)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Brush.verticalGradient(listOf(GradientStart, GradientEnd)))
        ) {
            if (uiState.entries.isEmpty() && !uiState.isLoading) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("No scans yet", style = MaterialTheme.typography.titleMedium, color = Color.White.copy(alpha = 0.6f))
                    Text("Your scan history will appear here", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.4f))
                }
            } else {
                LazyColumn(
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(uiState.entries, key = { it.id }) { entry ->
                        HistoryEntryCard(entry)
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryEntryCard(entry: ScanHistoryEntry) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = NightSurfaceElevated.copy(alpha = 0.92f)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = formatTimestamp(entry.timestampMillis),
                    style = MaterialTheme.typography.labelMedium,
                    color = AccentCyan
                )
                Text(
                    text = entry.scanMode.displayName,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = entry.topLabels,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )
            Text(
                text = "${entry.detectionCount} object${if (entry.detectionCount != 1) "s" else ""} detected",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun formatTimestamp(millis: Long): String {
    val fmt = SimpleDateFormat("MMM d, yyyy • h:mm a", Locale.getDefault())
    return fmt.format(Date(millis))
}
