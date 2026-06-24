package com.rudra.objectidentifier

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rudra.objectidentifier.presentation.MainViewModel
import com.rudra.objectidentifier.ui.theme.RealTimeObjectIdentifierTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RealTimeObjectIdentifierTheme {
                val viewModel: MainViewModel = hiltViewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                MainScreen(
                    title = uiState.title,
                    version = uiState.version
                )
            }
        }
    }
}

@Composable
private fun MainScreen(
    title: String,
    version: String
) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Text(
            text = if (version.isNotEmpty()) "$title\nv$version" else title,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MainActivityPreview() {
    RealTimeObjectIdentifierTheme {
        MainScreen(
            title = "Real-Time Object Identifier",
            version = "1.0.0"
        )
    }
}
