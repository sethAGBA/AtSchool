package com.ecolix.atschool

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import com.ecolix.atschool.ui.auth.LoginScreen
import com.ecolix.atschool.ui.theme.EcolixTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    EcolixTheme {
        Navigator(LoginScreen())
    }
}
