package com.example.translingo.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.example.translingo.presentation.navigation.TranslingoNavGraph
import com.example.translingo.presentation.ui.theme.TranslingoTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            TranslingoTheme {
                TranslingoNavGraph(navController = rememberNavController())
            }
        }
    }
}