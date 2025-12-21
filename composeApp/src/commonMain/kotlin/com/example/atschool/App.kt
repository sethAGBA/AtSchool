package com.example.atschool

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.example.atschool.ui.dashboard.DashboardScreen
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
        DashboardScreen()
    }
}
