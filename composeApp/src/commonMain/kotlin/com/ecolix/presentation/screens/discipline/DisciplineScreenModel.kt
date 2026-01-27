package com.ecolix.presentation.screens.discipline

import androidx.compose.ui.graphics.Color
import com.ecolix.data.models.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DisciplineScreenModel {
    private val _state = MutableStateFlow(DisciplineUiState())
    val state: StateFlow<DisciplineUiState> = _state.asStateFlow()

    init {
        loadMockData()
    }

    fun onDarkModeChange(isDarkMode: Boolean) {
        _state.value = _state.value.copy(isDarkMode = isDarkMode)
    }

    fun onViewModeChange(mode: DisciplineViewMode) {
        _state.value = _state.value.copy(viewMode = mode)
    }

    fun onSearchQueryChange(query: String) {
        _state.value = _state.value.copy(searchQuery = query)
    }

    fun onClassroomChange(classroom: String?) {
        _state.value = _state.value.copy(selectedClassroom = classroom)
    }

    private fun loadMockData() {
        val mockIncidents = listOf(
            DisciplineIncident("I1", "S5", "Awa Keita", "4ème C", IncidentType.LATE, "20/01/2026", "Retard de 15 minutes sans justificatif", Severity.LOW, "M. Diop"),
            DisciplineIncident("I2", "S2", "Moussa Traoré", "6ème A", IncidentType.DISRESPECT, "18/01/2026", "Perturbation répétée du cours de Français", Severity.MEDIUM, "Mme Sarr"),
            DisciplineIncident("I3", "S4", "Amadou Barry", "Terminale S", IncidentType.ABSENCE, "15/01/2026", "Absence injustifiée au devoir de Physique", Severity.MEDIUM, "M. Koffi"),
            DisciplineIncident("I4", "S1", "Binta Diallo", "6ème A", IncidentType.VIOLENCE, "10/01/2026", "Bagarre dans la cour de récréation", Severity.HIGH, "M. Ndiaye", "SA1")
        )

        val mockSanctions = listOf(
            Sanction("SA1", "I4", "S1", "Exclusion temporaire", SanctionStatus.COMPLETED, "11/01/2026", "13/01/2026", "3 jours"),
            Sanction("SA2", "I2", "S2", "Heures de colle", SanctionStatus.ACTIVE, "24/01/2026", "24/01/2026", "2 heures")
        )

        val mockMerits = listOf(
            MeritPoint("M1", "S3", 10, "Excellente participation au club de théâtre", "15/01/2026", "M. Hugo"),
            MeritPoint("M2", "S1", 5, "Aide spontanée à un camarade en difficulté", "12/01/2026", "Mme Curie")
        )

        val mockAttendance = listOf(
            AttendanceRecord("A1", "S1", "Binta Diallo", "6ème A", "25/01/2026", "08:15", AttendanceType.LATE, "Réveil difficile", false, "Surveillant Camara"),
            AttendanceRecord("A2", "S2", "Moussa Traoré", "6ème A", "25/01/2026", null, AttendanceType.ABSENCE, "Maladie", true, "Mme Sarr"),
            AttendanceRecord("A3", "S5", "Awa Keita", "4ème C", "24/01/2026", "08:30", AttendanceType.LATE, "Embouteillages", false, "Surveillant Camara"),
            AttendanceRecord("A4", "S2", "Moussa Traoré", "6ème A", "23/01/2026", null, AttendanceType.ABSENCE, null, false, "M. Diop")
        )

        val mockSummaries = listOf(
            StudentDisciplineSummary("S1", "Binta Diallo", "6ème A", 2, 5, 0, 1, 0, 85),
            StudentDisciplineSummary("S2", "Moussa Traoré", "6ème A", 3, 0, 2, 0, 1, 60),
            StudentDisciplineSummary("S3", "Fatoumata Sow", "5ème B", 0, 15, 0, 0, 0, 100),
            StudentDisciplineSummary("S4", "Amadou Barry", "Terminale S", 1, 0, 0, 0, 0, 90),
            StudentDisciplineSummary("S5", "Awa Keita", "4ème C", 5, 2, 0, 1, 0, 75)
        )

        _state.value = _state.value.copy(
            incidents = mockIncidents,
            sanctions = mockSanctions,
            merits = mockMerits,
            attendance = mockAttendance,
            studentSummaries = mockSummaries
        )
    }
}
