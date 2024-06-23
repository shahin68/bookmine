package com.shahin.bookmine.presentation

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.shahin.bookmine.presentation.error.IsolatedErrorHandling
import com.shahin.bookmine.presentation.navigation.AppNavHost
import com.shahin.bookmine.presentation.ui.theme.BookMineTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            enableEdgeToEdge(
                statusBarStyle = SystemBarStyle.auto(
                    lightScrim = Color.Transparent.toArgb(),
                    darkScrim = Color.Transparent.toArgb()
                ),
                navigationBarStyle = SystemBarStyle.auto(
                    lightScrim = Color.Transparent.toArgb(),
                    darkScrim = Color.Transparent.toArgb(),
                    detectDarkMode = {
                        true
                    }
                )
            )
        } else {
            enableEdgeToEdge()
        }
        setContent {
            BookMineTheme {
                AppNavHost()
                IsolatedErrorHandling()
            }
        }
    }
}