package com.ecolix.atschool

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.platform.ComposeView
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val composeView = ComposeView(this).apply {
            setContent { App() }
        }
        setContentView(composeView)
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
