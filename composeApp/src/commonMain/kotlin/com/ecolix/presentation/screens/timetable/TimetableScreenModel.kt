package com.ecolix.presentation.screens.timetable

import androidx.compose.ui.graphics.Color
import com.ecolix.data.models.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TimetableScreenModel {
    private val _state = MutableStateFlow(TimetableUiState())
    val state: StateFlow<TimetableUiState> = _state.asStateFlow()

    init {
        loadMockData()
    }

    fun onDarkModeChange(isDarkMode: Boolean) {
        val colors = if (isDarkMode) DashboardColors.dark() else DashboardColors.light()
        _state.value = _state.value.copy(colors = colors)
    }

    fun onViewModeChange(mode: TimetableViewMode) {
        _state.value = _state.value.copy(viewMode = mode)
    }

    fun onSearchQueryChange(query: String) {
        _state.value = _state.value.copy(searchQuery = query)
    }

    fun onClassroomChange(id: String?) {
        _state.value = _state.value.copy(selectedClassroomId = id)
    }

    fun onTeacherChange(id: String?) {
        _state.value = _state.value.copy(selectedTeacherId = id)
    }

    fun onDayChange(day: DayOfWeek?) {
        _state.value = _state.value.copy(selectedDay = day)
    }

    private fun loadMockData() {
        val slots = listOf(
            TimeSlot("1", "08:00", "09:00", 1),
            TimeSlot("2", "09:00", "10:00", 2),
            TimeSlot("3", "10:00", "11:00", 3),
            TimeSlot("4", "11:00", "12:00", 4),
            TimeSlot("5", "14:00", "15:00", 5),
            TimeSlot("6", "15:00", "16:00", 6),
            TimeSlot("7", "16:00", "17:00", 7),
            TimeSlot("8", "17:00", "18:00", 8)
        )

        val sampleClassrooms = listOf(
            Classroom("C1", "6ème A", 28, 14, 14, "Collège", null, "2024-2025"),
            Classroom("C2", "5ème B", 25, 12, 13, "Collège", null, "2024-2025"),
            Classroom("C3", "Terminal S1", 20, 10, 10, "Lycée", null, "2024-2025")
        )

        val sampleTeachers = listOf(
            Teacher("T1", "Jean", "Dupont", listOf("Mathématiques"), "j.dupont@ecolix.com"),
            Teacher("T2", "Marie", "Curie", listOf("Physique", "Chimie"), "m.curie@ecolix.com"),
            Teacher("T3", "Victor", "Hugo", listOf("Français", "Littérature"), "v.hugo@ecolix.com")
        )

        val sampleSubjects = listOf(
            Subject("SUB1", "Mathématiques", "MATH", categoryId = "1", categoryName = "Scientifique", categoryColorHex = "#3B82F6"),
            Subject("SUB2", "Français", "FRAN", categoryId = "2", categoryName = "Littéraire", categoryColorHex = "#EC4899"),
            Subject("SUB3", "Physique", "PHYS", categoryId = "1", categoryName = "Scientifique", categoryColorHex = "#F59E0B")
        )

        val mockSessions = listOf(
            TimetableSession(
                "S1", "C1", "6ème A", "SUB1", "Mathématiques", "T1", "Jean Dupont",
                "R101", "Salle 101", DayOfWeek.MONDAY, slots[0], SessionType.COURSE, Color(0xFF3B82F6)
            ),
            TimetableSession(
                "S2", "C1", "6ème A", "SUB2", "Français", "T3", "Victor Hugo",
                "R101", "Salle 101", DayOfWeek.MONDAY, slots[1], SessionType.COURSE, Color(0xFF10B981)
            ),
            TimetableSession(
                "S3", "C2", "5ème B", "SUB1", "Mathématiques", "T1", "Jean Dupont",
                "R102", "Salle 102", DayOfWeek.TUESDAY, slots[2], SessionType.COURSE, Color(0xFF3B82F6)
            ),
            TimetableSession(
                "S4", "C1", "6ème A", "SUB3", "Physique", "T2", "Marie Curie",
                "R201", "Labo 1", DayOfWeek.WEDNESDAY, slots[4], SessionType.TP, Color(0xFFF59E0B)
            )
        )

        _state.value = _state.value.copy(
            timeSlots = slots,
            classrooms = sampleClassrooms,
            teachers = sampleTeachers,
            subjects = sampleSubjects,
            sessions = mockSessions,
            selectedClassroomId = sampleClassrooms.firstOrNull()?.id
        )
    }
}
