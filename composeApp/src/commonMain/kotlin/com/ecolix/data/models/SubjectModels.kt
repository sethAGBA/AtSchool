package com.ecolix.data.models

import androidx.compose.ui.graphics.Color

data class Subject(
    val id: String,
    val name: String,
    val code: String,
    val categoryId: String? = null,
    val categoryName: String = "Non classée", // Convenience field
    val categoryColorHex: String = "#6366F1", // Convenience field
    val defaultCoefficient: Float = 1f,
    val description: String? = null,
    val professorIds: List<String> = emptyList(),
    val weeklyHours: Int = 2 // Default to 2 hours
) {
    val color: Color
        get() = try {
            Color(categoryColorHex.removePrefix("#").toLong(16) or 0xFF00000000)
        } catch (e: Exception) {
            Color(0xFF6366F1)
        }
}

data class ClassSubjectConfig(
    val classId: Int,
    val subjectId: String,
    val professorId: String? = null,
    val coefficient: Float = 1f,
    val weeklyHours: Int = 2
)

enum class SubjectsViewMode {
    SUBJECTS,
    CATEGORIES,
    PROFESSORS,
    CONFIG,
    FORM
}

enum class SubjectsLayoutMode {
    LIST,
    GRID
}

data class SubjectsUiState(
    val viewMode: SubjectsViewMode = SubjectsViewMode.SUBJECTS,
    val layoutMode: SubjectsLayoutMode = SubjectsLayoutMode.LIST,
    val subjects: List<Subject> = emptyList(),
    val categories: List<Category> = emptyList(), // Dynamic categories
    val searchQuery: String = "",
    val selectedCategoryId: String? = null,
    val isLoading: Boolean = false,
    val isDarkMode: Boolean = false,
    val selectedSubject: Subject? = null,
    val showCategoriesDialog: Boolean = false,
    val classrooms: List<com.ecolix.atschool.api.ClassDto> = emptyList(),
    val selectedClassId: Int? = null,
    val classroomConfigs: List<ClassSubjectConfig> = emptyList(),
    val staffMembers: List<com.ecolix.atschool.models.Staff> = emptyList()
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

    companion object {
        fun sample(isDarkMode: Boolean) = SubjectsUiState(
            isDarkMode = isDarkMode,
            subjects = listOf(
                Subject("1", "Mathématiques", "MATH", categoryId="1", categoryName="Scientifique", categoryColorHex="#3B82F6", defaultCoefficient = 4f),
                Subject("2", "Français", "FRAN", categoryId="2", categoryName="Littéraire", categoryColorHex="#EC4899", defaultCoefficient = 4f),
                Subject("3", "Physique-Chimie", "PC", categoryId="1", categoryName="Scientifique", categoryColorHex="#8B5CF6", defaultCoefficient = 3f),
                Subject("4", "SVT", "SVT", categoryId="1", categoryName="Scientifique", categoryColorHex="#10B981", defaultCoefficient = 2f)
            ),
            categories = listOf(
                Category("1", "Scientifique", "Sciences exactes", "#3B82F6", 1),
                Category("2", "Littéraire", "Langues et littérature", "#EC4899", 2)
            )
        )
    }
}
