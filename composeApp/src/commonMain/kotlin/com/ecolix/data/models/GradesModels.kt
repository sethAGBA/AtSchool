package com.ecolix.data.models

import com.ecolix.data.models.DashboardColors
import androidx.compose.ui.graphics.Color

enum class GradesViewMode {
    NOTES,
    BULLETINS,
    ARCHIVES,
    GRADE_FORM,
    CONFIG
}

enum class EvaluationType(val label: String) {
    DEVOIR("Devoir"),
    COMPOSITION("Composition")
}

enum class PeriodMode {
    TRIMESTRE,
    SEMESTRE
}

data class EvaluationTemplate(
    val id: String,
    val className: String,
    val type: EvaluationType,
    val label: String,
    val maxValue: Float = 20f,
    val coefficient: Float = 1f
)

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
    val status: String, // "Généré", "En attente", "Validé"
    val trend: String = "up" // "up", "down", "stable"
)

data class EvaluationSession(
    val id: String,
    val title: String,
    val type: EvaluationType,
    val subject: String,
    val classroom: String,
    val date: String,
    val period: String,
    val average: Float,
    val successRate: Int, // Percentage
    val maxGrade: Float = 20f,
    val coefficient: Float = 1f,
    val gradeCount: Int
)

data class EvaluationSummary(
    val typeName: String,
    val mark: Float,
    val weight: Float = 1f
)

data class ReportCardSubject(
    val name: String,
    val professor: String,
    val evaluations: List<EvaluationSummary>,
    val average: Float,
    val coefficient: Float,
    val total: Float,
    val totalCoefficient: Float,
    val classAverage: Float,
    val minAverage: Float,
    val maxAverage: Float,
    val rank: Int,
    val appreciation: String,
    val category: String = "Général"
)

data class ReportCard(
    val id: String,
    val studentName: String,
    val studentId: String,
    val matricule: String,
    val dateOfBirth: String,
    val sex: String = "M",
    val isRepeater: Boolean = false,
    val className: String,
    val period: String,
    val academicYear: String,
    val subjects: List<ReportCardSubject>,
    val generalAverage: Float,
    val annualAverage: Float?,
    val rank: Int,
    val totalStudents: Int,
    val classAverage: Float,
    val minAverage: Float,
    val maxAverage: Float,
    val appreciationGenerale: String,
    val decision: String,
    val sanctions: String? = null,
    val retards: Int = 0,
    val absInjustifiees: Int = 0,
    val absJustifiees: Int = 0,
    val conduite: String = "Bonne",
    val travail: String = "Bien",
    val tableauHonneur: Boolean = false,
    val tableauEncouragement: Boolean = false,
    val tableauFelicitations: Boolean = false,
    val forces: String = "NON",
    val pointsADevelopper: String = "NON",
    val historyAverages: List<Float?> = emptyList(), // Previous period averages
    val teacherName: String = "",
    val directorName: String = "",
    val isDuplicate: Boolean = false,
    val serie: String = "",
    val nb: String = "",
    val schoolInfo: com.ecolix.atschool.api.EstablishmentSettingsDto? = null,
    val generatedDate: String? = null
)

data class AcademicSummary(
    val averageGrade: Float,
    val successRate: Int,
    val bestSubject: String,
    val totalEvaluations: Int,
    val topStudent: String
)

data class GradesUiState(
    val viewMode: GradesViewMode = GradesViewMode.NOTES,
    val periodMode: PeriodMode = PeriodMode.TRIMESTRE,
    val currentPeriod: String = "1er Trimestre",
    val searchQuery: String = "",
    val selectedClassroom: String? = "Toutes les classes",
    val selectedSubject: String? = "Toutes les matières",
    val isDarkMode: Boolean = false,
    val summary: AcademicSummary = AcademicSummary(14.2f, 85, "Mathématiques", 12, "Seth Kouamé"),
    val sessions: List<EvaluationSession> = emptyList(),
    val evaluations: List<GradeEvaluation> = emptyList(),
    val bulletins: List<BulletinPreview> = emptyList(),
    val classrooms: List<String> = listOf("Toutes les classes", "6ème A", "6ème B", "5ème A", "4ème Espagnol", "3ème Rouge"),
    val subjects: List<String> = listOf("Toutes les matières", "Mathématiques", "Français", "Physique-Chimie", "SVT", "Anglais", "Histoire-Géo"),
    val currentClassStudents: List<Student> = emptyList(),
    val selectedReportCard: ReportCard? = null,
    val isExporting: Boolean = false,
    val exportProgress: Float = 0f,
    val batchExportCount: Int = 0,
    val itemsPerPage: Int = 5,
    val studentsBatchSize: Int = 10,
    val studentsLoadedCount: Int = 10,
    val notesPage: Int = 0,
    val bulletinsPage: Int = 0,
    val archivesPage: Int = 0,
    val templates: List<EvaluationTemplate> = listOf(
        EvaluationTemplate("T1", "6ème A", EvaluationType.DEVOIR, "Devoir", 20f, 1f),
        EvaluationTemplate("T2", "6ème A", EvaluationType.COMPOSITION, "Composition", 20f, 2f),
        EvaluationTemplate("T3", "5ème A", EvaluationType.DEVOIR, "Interrogation SVT", 10f, 1f)
    ),
    val schoolInfo: com.ecolix.atschool.api.EstablishmentSettingsDto? = null
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
            sessions = listOf(
                EvaluationSession("S1", "Devoir Surveillé n°1", EvaluationType.DEVOIR, "Mathématiques", "6ème A", "20 Jan 2026", "1er Trimestre", 15.2f, 92, 20f, 1f, 42),
                EvaluationSession("S2", "Composition trimestrielle", EvaluationType.COMPOSITION, "Français", "6ème A", "15 Jan 2026", "1er Trimestre", 12.8f, 78, 20f, 2f, 40),
                EvaluationSession("S3", "Interrogation surprise", EvaluationType.DEVOIR, "SVT", "5ème A", "22 Jan 2026", "1er Trimestre", 14.5f, 85, 20f, 1f, 38)
            ),
            evaluations = listOf(
                GradeEvaluation("1", "Seth Kouamé", "Mathématiques", 15.5f, 20, "20 Jan 2026", "1er Trimestre"),
                GradeEvaluation("2", "Awa Diop", "Français", 14.0f, 20, "18 Jan 2026", "1er Trimestre"),
                GradeEvaluation("3", "Koffi Mensah", "Physique-Chimie", 12.5f, 20, "22 Jan 2026", "1er Trimestre")
            ),
            bulletins = listOf(
                BulletinPreview("1", "Seth Kouamé", "6ème A", 16.2f, 1, 45, "Validé", "up"),
                BulletinPreview("2", "Awa Diop", "6ème A", 15.8f, 2, 45, "Généré", "stable"),
                BulletinPreview("3", "Koffi Mensah", "6ème A", 14.5f, 5, 45, "En attente", "down")
            )
        )
    }
}
