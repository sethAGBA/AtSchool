package com.ecolix.presentation.theme

import androidx.compose.ui.graphics.Color

// Brand Colors
val BluePrimary = Color(0xFF6366F1) // Indigo 500 from Flutter Login
val BlueSecondary = Color(0xFF3B82F6) // AppColors.primaryBlue
val OrangeSecondary = Color(0xFFF97316)
val GreenAccent = Color(0xFF10B981) // AppColors.successGreen
val PinkAccent = Color(0xFFEC4899)

// Gradients
val LoginLightGradient = listOf(
    Color(0xFF6366F1),
    Color(0xFF8B5CF6),
    Color(0xFFEC4899)
)

val LoginDarkGradient = listOf(
    Color(0xFF1E293B),
    Color(0xFF334155),
    Color(0xFF475569)
)

// Light Theme Colors
val LightBackground = Color(0xFFFFFFFF) // Flutter scaffoldBackgroundColor
val LightSurface = Color(0xFFFFFFFF)
val LightOnBackground = Color(0xFF1E293B)
val LightOnSurface = Color(0xFF1E293B)

// Dark Theme Colors
val DarkBackground = Color(0xFF111827) // approximations for Colors.grey[900]
val DarkSurface = Color(0xFF1F2937) // approximations for Colors.grey[850]
val DarkOnBackground = Color(0xFFF1F5F9)
val DarkOnSurface = Color(0xFFF1F5F9)
