package com.santiago.soberpath.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import org.koin.androidx.compose.koinViewModel

@Composable
fun SoberPathApp(
    viewModel: AppStartViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    if (state.isLoading || state.startDestination == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        val navController = rememberNavController()

        SoberNavHost(
            navController = navController,
            startDestination = state.startDestination!!
        )
    }
}