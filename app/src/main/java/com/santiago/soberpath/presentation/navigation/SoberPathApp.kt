package com.santiago.soberpath.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController

@Composable
fun SoberPathApp() {
    val navController = rememberNavController()
    SoberNavHost(navController = navController)
}

