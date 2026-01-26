package com.ecolix.data.models

import androidx.compose.ui.graphics.Color

data class Category(
    val id: String,
    val name: String,
    val description: String? = null,
    val colorHex: String,
    val order: Int = 0
) {
    val color: Color
        get() = try {
            Color(colorHex.removePrefix("#").toLong(16) or 0xFF00000000)
        } catch (e: Exception) {
            Color(0xFF6366F1) // Default indigo
        }

    companion object {
        fun empty() = Category(
            id = "",
            name = "",
            description = null,
            colorHex = "#6366F1",
            order = 0
        )
    }
}

data class CategoriesUiState(
    val categories: List<Category> = emptyList(),
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val isDarkMode: Boolean = false,
    val selectedCategory: Category? = null
) {
    val colors: DashboardColors
        get() = if (isDarkMode) {
            DashboardColors(
                background = Color(0xFF0F172A),
                card = Color(0xFF1E293B),
                textPrimary = Color.White,
                textMuted = Color(0xFF94A3B8),
                divider = Color.White.copy(alpha = 0.1f),
                textLink = Color(0xFF38BDF8)
            )
        } else {
            DashboardColors(
                background = Color(0xFFF8FAFC),
                card = Color.White,
                textPrimary = Color(0xFF1E293B),
                textMuted = Color(0xFF64748B),
                divider = Color(0xFFE2E8F0),
                textLink = Color(0xFF0284C7)
            )
        }
}
