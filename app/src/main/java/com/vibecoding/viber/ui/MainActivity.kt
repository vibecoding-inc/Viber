package com.vibecoding.viber.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.vibecoding.viber.ui.auth.AuthScreen
import com.vibecoding.viber.ui.home.HomeScreen
import com.vibecoding.viber.ui.theme.ViberTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val isAuthenticated by viewModel.isAuthenticated.collectAsState()
            val vibeMode by viewModel.vibeMode.collectAsState()
            val catMode by viewModel.catMode.collectAsState()

            ViberTheme(
                vibeMode = vibeMode,
                catMode = catMode
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (isAuthenticated) {
                        HomeScreen()
                    } else {
                        AuthScreen(
                            onAuthSuccess = {
                                viewModel.checkAuth()
                            }
                        )
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Re-check authentication state when returning from OAuth callback
        viewModel.checkAuth()
    }
}
