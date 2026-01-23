package com.ecolix.atschool.ui.grades.models

import com.ecolix.atschool.ui.dashboard.models.DashboardColors
import androidx.compose.ui.graphics.Color

enum class GradesViewMode {
    NOTES,
    BULLETINS,
    ARCHIVES,
    GRADE_FORM
}

data class GradeEvaluation(
    val id: String,
    val studentName: String,
    val subject: String,
    val grade: Float,
    val base: Int = 20,
    val date: String,
    val period: String
)

data class BulletinPreview(
    val id: String,
    val studentName: String,
    val classroom: String,
    val average: Float,
    val rank: Int,
    val totalStudents: Int,
    val status: String // "Généré", "En attente", "Validé"
)

data class GradesUiState(
    val viewMode: GradesViewMode = GradesViewMode.NOTES,
    val currentPeriod: String = "1er Trimestre",
    val searchQuery: String = "",
    val selectedClassroom: String? = null,
    val selectedSubject: String? = null,
    val isDarkMode: Boolean = false,
    val formStudentName: String = "",
    val formGradeValue: String = "",
    val formSubject: String = "",
    val formClassroom: String = "",
    val evaluations: List<GradeEvaluation> = emptyList(),
    val bulletins: List<BulletinPreview> = emptyList(),
    val classrooms: List<String> = listOf("6ème A", "6ème B", "5ème A", "4ème Espagnol", "3ème Rouge"),
    val subjects: List<String> = listOf("Mathématiques", "Français", "Physique-Chimie", "SVT", "Anglais", "Histoire-Géo")
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
        fun sample(isDarkMode: Boolean) = GradesUiState(
            isDarkMode = isDarkMode,
            evaluations = listOf(
                GradeEvaluation("1", "Seth Kouamé", "Mathématiques", 15.5f, 20, "20 Jan 2026", "1er Trimestre"),
                GradeEvaluation("2", "Awa Diop", "Français", 14.0f, 20, "18 Jan 2026", "1er Trimestre"),
                GradeEvaluation("3", "Koffi Mensah", "Physique-Chimie", 12.5f, 20, "22 Jan 2026", "1er Trimestre")
            ),
            bulletins = listOf(
                BulletinPreview("1", "Seth Kouamé", "6ème A", 16.2f, 1, 45, "Validé"),
                BulletinPreview("2", "Awa Diop", "6ème A", 15.8f, 2, 45, "Généré"),
                BulletinPreview("3", "Koffi Mensah", "6ème A", 14.5f, 5, 45, "En attente")
            )
        )
    }
}
