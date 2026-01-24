package com.ecolix

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import com.ecolix.presentation.screens.auth.LoginScreen
import com.ecolix.presentation.theme.EcolixTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    EcolixTheme {
        Navigator(LoginScreen())
    }
}
