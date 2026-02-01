package com.ecolix.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.MutableState

val LocalThemeIsDark = compositionLocalOf { mutableStateOf(false) }
private val LightColorScheme = lightColorScheme(
    primary = BluePrimary,
    secondary = OrangeSecondary,
    tertiary = GreenAccent,
    background = LightBackground,
    surface = LightSurface,
    onPrimary = LightSurface,
    onSecondary = LightSurface,
    onBackground = LightOnBackground,
    onSurface = LightOnSurface
)

private val DarkColorScheme = darkColorScheme(
    primary = BluePrimary,
    secondary = OrangeSecondary,
    tertiary = GreenAccent,
    background = DarkBackground,
    surface = DarkSurface,
    onPrimary = DarkSurface,
    onSecondary = DarkSurface,
    onBackground = DarkOnBackground,
    onSurface = DarkOnSurface
)

@Composable
fun EcolixTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
