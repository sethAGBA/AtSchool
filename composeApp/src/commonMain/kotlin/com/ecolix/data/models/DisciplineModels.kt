package com.ecolix.data.models

import androidx.compose.ui.graphics.Color

enum class IncidentType {
    LATE,
    ABSENCE,
    DISRESPECT,
    VIOLENCE,
    CHEATING,
    VANDALISM,
    CELLPHONE_USE,
    OTHER;

    fun toFrench(): String = when (this) {
        LATE -> "Retard"
        ABSENCE -> "Absence"
        DISRESPECT -> "Manque de respect"
        VIOLENCE -> "Violence"
        CHEATING -> "Triche"
        VANDALISM -> "Vandalisme"
        CELLPHONE_USE -> "Usage de téléphone"
        OTHER -> "Autre"
    }
}

enum class Severity {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL;

    fun toFrench(): String = when (this) {
        LOW -> "Faible"
        MEDIUM -> "Moyenne"
        HIGH -> "Élevée"
        CRITICAL -> "Critique"
    }
}

enum class DisciplineViewMode {
    OVERVIEW,
    ATTENDANCE,
    INCIDENTS,
    SANCTIONS,
    MERITS,
    FORM
}

data class DisciplineIncident(
    val id: String,
    val studentId: String,
    val studentName: String,
    val classroom: String,
    val type: IncidentType,
    val date: String,
    val description: String,
    val severity: Severity,
    val reportedBy: String,
    val sanctionId: String? = null
)

data class Sanction(
    val id: String,
    val incidentId: String,
    val studentId: String,
    val type: String,
    val status: SanctionStatus,
    val startDate: String,
    val endDate: String?,
    val duration: String? = null
)

enum class SanctionStatus {
    PENDING,
    ACTIVE,
    COMPLETED,
    CANCELLED;

    fun toFrench(): String = when (this) {
        PENDING -> "En attente"
        ACTIVE -> "Active"
        COMPLETED -> "Terminée"
        CANCELLED -> "Annulée"
    }
}

data class MeritPoint(
    val id: String,
    val studentId: String,
    val points: Int,
    val reason: String,
    val date: String,
    val awardedBy: String
)

data class StudentDisciplineSummary(
    val studentId: String,
    val studentName: String,
    val classroom: String,
    val totalIncidents: Int,
    val totalMerits: Int,
    val totalAbsences: Int,
    val totalLates: Int,
    val activeSanctions: Int,
    val behaviorScore: Int // 0 to 100
)

data class AttendanceRecord(
    val id: String,
    val studentId: String,
    val studentName: String,
    val classroom: String,
    val date: String,
    val time: String?,
    val type: AttendanceType,
    val reason: String?,
    val isJustified: Boolean = false,
    val reportedBy: String
)

enum class AttendanceType {
    ABSENCE,
    LATE;

    fun toFrench(): String = when (this) {
        ABSENCE -> "ABSENCE"
        LATE -> "RETARD"
    }
}

data class DisciplineUiState(
    val viewMode: DisciplineViewMode = DisciplineViewMode.OVERVIEW,
    val incidents: List<DisciplineIncident> = emptyList(),
    val sanctions: List<Sanction> = emptyList(),
    val merits: List<MeritPoint> = emptyList(),
    val attendance: List<AttendanceRecord> = emptyList(),
    val studentSummaries: List<StudentDisciplineSummary> = emptyList(),
    val searchQuery: String = "",
    val selectedClassroom: String? = null,
    val isLoading: Boolean = false,
    val isDarkMode: Boolean = false
) {
    val colors: DashboardColors
        get() = if (isDarkMode) DashboardColors.dark() else DashboardColors.light()
}
