package com.santiago.soberpath

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.santiago.soberpath.presentation.navigation.SoberPathApp
import com.santiago.soberpath.ui.theme.SoberPathTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SoberPathTheme {
                SoberPathApp()
            }
        }
    }
}
