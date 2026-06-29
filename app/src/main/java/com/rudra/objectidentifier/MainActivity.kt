package com.rudra.objectidentifier

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.rudra.objectidentifier.di.DetectorWarmupMarker
import com.rudra.objectidentifier.ui.navigation.ObjectIdentifierNavHost
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var detectorWarmup: DetectorWarmupMarker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            ObjectIdentifierNavHost(
                navController = navController,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
