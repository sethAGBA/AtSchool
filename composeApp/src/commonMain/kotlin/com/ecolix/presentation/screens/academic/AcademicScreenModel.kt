package com.ecolix.presentation.screens.academic

import androidx.compose.ui.graphics.Color
import com.ecolix.data.models.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AcademicScreenModel {
    private val _state = MutableStateFlow(AcademicUiState())
    val state: StateFlow<AcademicUiState> = _state.asStateFlow()

    init {
        loadMockData()
    }

    fun onDarkModeChange(isDarkMode: Boolean) {
        val colors = if (isDarkMode) {
            DashboardColors.dark()
        } else {
            DashboardColors.light()
        }
        _state.value = _state.value.copy(colors = colors)
    }

    fun onViewModeChange(mode: AcademicViewMode) {
        _state.value = _state.value.copy(viewMode = mode)
    }

    fun onSearchQueryChange(query: String) {
        _state.value = _state.value.copy(searchQuery = query)
    }

    fun onSelectSchoolYear(yearId: String) {
        _state.value = _state.value.copy(selectedSchoolYearId = yearId)
    }

    fun onSelectPeriod(periodId: String) {
        _state.value = _state.value.copy(selectedPeriodId = periodId)
    }

    fun updateState(newState: AcademicUiState) {
        _state.value = newState
    }

    private fun loadMockData() {
        val mockSchoolYears = generateMockSchoolYears()
        val mockPeriods = generateMockPeriods(mockSchoolYears)
        val mockEvents = generateMockEvents()
        val mockHolidays = generateMockHolidays()
        val statistics = calculateStatistics(mockSchoolYears, mockPeriods, mockEvents)

        _state.value = _state.value.copy(
            schoolYears = mockSchoolYears,
            periods = mockPeriods,
            events = mockEvents,
            holidays = mockHolidays,
            statistics = statistics,
            settings = generateMockSettings()
        )
    }

    private fun generateMockSchoolYears(): List<SchoolYear> {
        return listOf(
            SchoolYear(
                id = "SY2024",
                name = "2024-2025",
                startDate = "2024-09-01",
                endDate = "2025-06-30",
                status = AcademicStatus.ACTIVE,
                periodType = PeriodType.TRIMESTER,
                numberOfPeriods = 3,
                isDefault = true,
                description = "Année scolaire en cours"
            ),
            SchoolYear(
                id = "SY2023",
                name = "2023-2024",
                startDate = "2023-09-01",
                endDate = "2024-06-30",
                status = AcademicStatus.COMPLETED,
                periodType = PeriodType.TRIMESTER,
                numberOfPeriods = 3,
                description = "Année scolaire précédente"
            ),
            SchoolYear(
                id = "SY2025",
                name = "2025-2026",
                startDate = "2025-09-01",
                endDate = "2026-06-30",
                status = AcademicStatus.UPCOMING,
                periodType = PeriodType.TRIMESTER,
                numberOfPeriods = 3,
                description = "Année scolaire à venir"
            )
        )
    }

    private fun generateMockPeriods(schoolYears: List<SchoolYear>): List<AcademicPeriod> {
        val periods = mutableListOf<AcademicPeriod>()
        val activeYear = schoolYears.find { it.status == AcademicStatus.ACTIVE }

        activeYear?.let { year ->
            periods.addAll(
                listOf(
                    AcademicPeriod(
                        id = "P1_2024",
                        schoolYearId = year.id,
                        name = "1er Trimestre",
                        periodNumber = 1,
                        startDate = "2024-09-01",
                        endDate = "2024-12-15",
                        status = AcademicStatus.COMPLETED,
                        evaluationDeadline = "2024-12-10",
                        reportCardDeadline = "2024-12-20"
                    ),
                    AcademicPeriod(
                        id = "P2_2024",
                        schoolYearId = year.id,
                        name = "2ème Trimestre",
                        periodNumber = 2,
                        startDate = "2025-01-06",
                        endDate = "2025-03-28",
                        status = AcademicStatus.ACTIVE,
                        evaluationDeadline = "2025-03-20",
                        reportCardDeadline = "2025-04-05"
                    ),
                    AcademicPeriod(
                        id = "P3_2024",
                        schoolYearId = year.id,
                        name = "3ème Trimestre",
                        periodNumber = 3,
                        startDate = "2025-04-07",
                        endDate = "2025-06-30",
                        status = AcademicStatus.UPCOMING,
                        evaluationDeadline = "2025-06-20",
                        reportCardDeadline = "2025-07-05"
                    )
                )
            )
        }

        return periods
    }

    private fun generateMockEvents(): List<AcademicEvent> {
        return listOf(
            AcademicEvent(
                id = "E1",
                title = "Rentrée des Classes",
                description = "Début de l'année scolaire 2024-2025",
                date = "2024-09-01",
                type = EventType.CEREMONY,
                color = Color(0xFF3B82F6)
            ),
            AcademicEvent(
                id = "E2",
                title = "Examens du 1er Trimestre",
                description = "Évaluations de fin de trimestre",
                date = "2024-12-01",
                endDate = "2024-12-10",
                type = EventType.EXAM,
                color = Color(0xFFEF4444),
                isAllDay = false
            ),
            AcademicEvent(
                id = "E3",
                title = "Conseil de Classe",
                description = "Réunion des enseignants",
                date = "2025-01-15",
                type = EventType.MEETING,
                color = Color(0xFF10B981)
            ),
            AcademicEvent(
                id = "E4",
                title = "Remise des Bulletins",
                description = "Distribution des bulletins du 2ème trimestre",
                date = "2025-04-05",
                type = EventType.DEADLINE,
                color = Color(0xFFF59E0B)
            ),
            AcademicEvent(
                id = "E5",
                title = "Fête de Fin d'Année",
                description = "Cérémonie de clôture",
                date = "2025-06-28",
                type = EventType.CEREMONY,
                color = Color(0xFF8B5CF6)
            )
        )
    }

    private fun generateMockHolidays(): List<Holiday> {
        return listOf(
            Holiday(
                id = "H1",
                name = "Vacances de Noël",
                startDate = "2024-12-16",
                endDate = "2025-01-05",
                type = HolidayType.SCHOOL_BREAK
            ),
            Holiday(
                id = "H2",
                name = "Vacances de Pâques",
                startDate = "2025-03-29",
                endDate = "2025-04-06",
                type = HolidayType.SCHOOL_BREAK
            ),
            Holiday(
                id = "H3",
                name = "Fête de l'Indépendance",
                startDate = "2025-04-04",
                endDate = "2025-04-04",
                type = HolidayType.NATIONAL
            ),
            Holiday(
                id = "H4",
                name = "Tabaski",
                startDate = "2025-05-15",
                endDate = "2025-05-16",
                type = HolidayType.RELIGIOUS
            )
        )
    }

    private fun generateMockSettings(): AcademicSettings {
        return AcademicSettings(
            defaultPeriodType = PeriodType.TRIMESTER,
            gradeScale = GradeScale(
                minGrade = 0f,
                maxGrade = 20f,
                passingGrade = 10f,
                gradeLevels = listOf(
                    GradeLevel("Excellent", 16f, 20f, "Très bon résultat", Color(0xFF10B981)),
                    GradeLevel("Bien", 14f, 15.99f, "Bon résultat", Color(0xFF3B82F6)),
                    GradeLevel("Assez Bien", 12f, 13.99f, "Résultat satisfaisant", Color(0xFF06B6D4)),
                    GradeLevel("Passable", 10f, 11.99f, "Résultat acceptable", Color(0xFFF59E0B)),
                    GradeLevel("Insuffisant", 0f, 9.99f, "Résultat insuffisant", Color(0xFFEF4444))
                )
            ),
            passingGrade = 10f,
            attendanceRequired = 75f,
            allowMidPeriodTransfer = false,
            autoPromoteStudents = true
        )
    }

    private fun calculateStatistics(
        schoolYears: List<SchoolYear>,
        periods: List<AcademicPeriod>,
        events: List<AcademicEvent>
    ): AcademicStatistics {
        val activeYear = schoolYears.find { it.status == AcademicStatus.ACTIVE }
        val currentPeriod = periods.find { it.status == AcademicStatus.ACTIVE }
        val upcomingEvents = events.count { 
            // Simplified: count all events as upcoming
            true
        }

        return AcademicStatistics(
            totalSchoolYears = schoolYears.size,
            activeYear = activeYear,
            currentPeriod = currentPeriod,
            upcomingEvents = upcomingEvents,
            daysUntilNextPeriod = 45, // Mock value
            completionRate = 66.7f // 2 out of 3 trimesters
        )
    }
}
