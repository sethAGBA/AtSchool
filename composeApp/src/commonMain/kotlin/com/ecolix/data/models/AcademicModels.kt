package com.ecolix.data.models

import androidx.compose.ui.graphics.Color

// Enums
enum class AcademicViewMode {
    OVERVIEW,
    SCHOOL_YEARS,
    PERIODS,
    CALENDAR,
    SETTINGS
}

enum class PeriodType {
    TRIMESTER,
    SEMESTER
}

enum class AcademicStatus {
    ACTIVE,
    UPCOMING,
    COMPLETED,
    ARCHIVED
}

// Data Classes
data class SchoolYear(
    val id: String,
    val name: String,
    val startDate: String,
    val endDate: String,
    val status: AcademicStatus,
    val periodTypes: List<PeriodType>,
    val numberOfPeriods: Int,
    val isDefault: Boolean = false,
    val description: String? = null
)

data class AcademicPeriod(
    val id: String,
    val schoolYearId: String,
    val name: String,
    val periodNumber: Int,
    val startDate: String,
    val endDate: String,
    val status: AcademicStatus,
    val type: PeriodType,
    val evaluationDeadline: String? = null,
    val reportCardDeadline: String? = null
)

data class AcademicEvent(
    val id: String,
    val title: String,
    val description: String?,
    val date: String,
    val endDate: String? = null,
    val type: EventType,
    val color: Color,
    val isAllDay: Boolean = true
)

enum class EventType {
    HOLIDAY,
    EXAM,
    MEETING,
    CEREMONY,
    DEADLINE,
    OTHER
}

data class AcademicCalendar(
    val schoolYear: SchoolYear,
    val periods: List<AcademicPeriod>,
    val events: List<AcademicEvent>,
    val holidays: List<Holiday>
)

data class Holiday(
    val id: String,
    val name: String,
    val startDate: String,
    val endDate: String,
    val type: HolidayType
)

enum class HolidayType {
    NATIONAL,
    RELIGIOUS,
    SCHOOL_BREAK,
    OTHER
}

data class AcademicSettings(
    val defaultPeriodType: PeriodType,
    val gradeScale: GradeScale,
    val passingGrade: Float,
    val attendanceRequired: Float,
    val allowMidPeriodTransfer: Boolean,
    val autoPromoteStudents: Boolean,
    val decimalPrecision: Int = 2,
    val showRankOnReportCard: Boolean = true,
    val showClassAverageOnReportCard: Boolean = true,
    val absencesThresholdAlert: Int = 5,
    val matriculePrefix: String? = null
)

data class GradeScale(
    val minGrade: Float,
    val maxGrade: Float,
    val passingGrade: Float,
    val gradeLevels: List<GradeLevel>
)

data class GradeLevel(
    val name: String,
    val minValue: Float,
    val maxValue: Float,
    val description: String,
    val color: Color
)

data class AcademicStatistics(
    val totalSchoolYears: Int,
    val activeYear: SchoolYear?,
    val currentPeriods: List<AcademicPeriod> = emptyList(),
    val upcomingEvents: Int,
    val daysUntilNextPeriod: Int,
    val completionRate: Float
)

// UI State
data class AcademicUiState(
    val viewMode: AcademicViewMode = AcademicViewMode.OVERVIEW,
    val colors: DashboardColors = DashboardColors.light(),
    val searchQuery: String = "",
    val selectedSchoolYearId: String? = null,
    val selectedPeriodId: String? = null,
    val schoolYears: List<SchoolYear> = emptyList(),
    val periods: List<AcademicPeriod> = emptyList(),
    val events: List<AcademicEvent> = emptyList(),
    val holidays: List<Holiday> = emptyList(),
    val statistics: AcademicStatistics = AcademicStatistics(
        totalSchoolYears = 0,
        activeYear = null,
        currentPeriods = emptyList(),
        upcomingEvents = 0,
        daysUntilNextPeriod = 0,
        completionRate = 0f
    ),
    val settings: AcademicSettings = AcademicSettings(
        defaultPeriodType = PeriodType.TRIMESTER,
        gradeScale = GradeScale(
            minGrade = 0f,
            maxGrade = 20f,
            passingGrade = 10f,
            gradeLevels = emptyList()
        ),
        passingGrade = 10f,
        attendanceRequired = 75f,
        allowMidPeriodTransfer = false,
        autoPromoteStudents = false,
        decimalPrecision = 2,
        showRankOnReportCard = true,
        showClassAverageOnReportCard = true,
        absencesThresholdAlert = 5,
        matriculePrefix = null
    ),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

// Extensions
fun PeriodType.toFrench(): String = when (this) {
    PeriodType.TRIMESTER -> "Trimestre"
    PeriodType.SEMESTER -> "Semestre"
}

fun AcademicStatus.toFrench(): String = when (this) {
    AcademicStatus.ACTIVE -> "Actif"
    AcademicStatus.UPCOMING -> "À venir"
    AcademicStatus.COMPLETED -> "Terminé"
    AcademicStatus.ARCHIVED -> "Archivé"
}

fun AcademicStatus.toColor(): Color = when (this) {
    AcademicStatus.ACTIVE -> Color(0xFF10B981)
    AcademicStatus.UPCOMING -> Color(0xFF3B82F6)
    AcademicStatus.COMPLETED -> Color(0xFFF59E0B)
    AcademicStatus.ARCHIVED -> Color(0xFF64748B)
}

fun EventType.toFrench(): String = when (this) {
    EventType.HOLIDAY -> "Vacances"
    EventType.EXAM -> "Examen"
    EventType.MEETING -> "Réunion"
    EventType.CEREMONY -> "Cérémonie"
    EventType.DEADLINE -> "Date limite"
    EventType.OTHER -> "Autre"
}

fun HolidayType.toFrench(): String = when (this) {
    HolidayType.NATIONAL -> "Fête Nationale"
    HolidayType.RELIGIOUS -> "Fête Religieuse"
    HolidayType.SCHOOL_BREAK -> "Congés Scolaires"
    HolidayType.OTHER -> "Autre"
}
