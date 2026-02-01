package com.ecolix

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import com.ecolix.presentation.screens.auth.LoginScreen
import com.ecolix.presentation.theme.EcolixTheme




import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.CompositionLocalProvider
import org.jetbrains.compose.ui.tooling.preview.Preview
import com.ecolix.presentation.theme.LocalThemeIsDark

@Composable
@Preview
fun App() {
    val systemIsDark = isSystemInDarkTheme()
    val isDarkState = remember { mutableStateOf(systemIsDark) }

    CompositionLocalProvider(LocalThemeIsDark provides isDarkState) {
        EcolixTheme(darkTheme = isDarkState.value) {
            Navigator(LoginScreen())
        }
    }
}
