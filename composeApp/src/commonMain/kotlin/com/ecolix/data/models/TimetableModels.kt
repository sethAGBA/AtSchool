package com.ecolix.data.models

import androidx.compose.ui.graphics.Color

// Enums
enum class TimetableViewMode {
    OVERVIEW,
    BY_CLASS,
    BY_TEACHER,
    BY_ROOM,
    EDITOR
}

enum class DayOfWeek {
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY;

    fun toFrench(): String = when (this) {
        MONDAY -> "Lundi"
        TUESDAY -> "Mardi"
        WEDNESDAY -> "Mercredi"
        THURSDAY -> "Jeudi"
        FRIDAY -> "Vendredi"
        SATURDAY -> "Samedi"
    }

    fun toShortFrench(): String = when (this) {
        MONDAY -> "Lun"
        TUESDAY -> "Mar"
        WEDNESDAY -> "Mer"
        THURSDAY -> "Jeu"
        FRIDAY -> "Ven"
        SATURDAY -> "Sam"
    }
}

enum class SessionType {
    COURSE,
    TD,
    TP,
    EXAM,
    STUDY,
    BREAK,
    SPORT,
    OTHER;

    fun toFrench(): String = when (this) {
        COURSE -> "Cours"
        TD -> "TD"
        TP -> "TP"
        EXAM -> "Examen"
        STUDY -> "Étude"
        BREAK -> "Pause"
        SPORT -> "Sport"
        OTHER -> "Autre"
    }
}

// Data Classes
data class TimeSlot(
    val id: String,
    val startTime: String, // Format: "HH:mm"
    val endTime: String,
    val order: Int
)

data class TimetableSession(
    val id: String,
    val classroomId: String,
    val classroomName: String,
    val subjectId: String,
    val subjectName: String,
    val teacherId: String,
    val teacherName: String,
    val roomId: String?,
    val roomName: String?,
    val dayOfWeek: DayOfWeek,
    val timeSlot: TimeSlot,
    val sessionType: SessionType,
    val color: Color,
    val notes: String? = null,
    val isRecurring: Boolean = true
)

data class Teacher(
    val id: String,
    val firstName: String,
    val lastName: String,
    val subjects: List<String>,
    val email: String?
)

data class Room(
    val id: String,
    val name: String,
    val type: String,
    val capacity: Int,
    val equipment: List<String>
)

data class TimetableTemplate(
    val id: String,
    val name: String,
    val description: String?,
    val sessions: List<TimetableSession>,
    val isDefault: Boolean = false
)

data class TimetableConflict(
    val type: ConflictType,
    val session1: TimetableSession,
    val session2: TimetableSession,
    val message: String
)

enum class ConflictType {
    TEACHER_DOUBLE_BOOKING,
    ROOM_DOUBLE_BOOKING,
    CLASS_DOUBLE_BOOKING,
    TEACHER_OVERLOAD
}

data class TimetableStatistics(
    val totalSessions: Int,
    val totalHoursPerWeek: Float,
    val averageSessionsPerDay: Float,
    val mostBusyDay: DayOfWeek?,
    val teacherUtilization: Map<String, Float>,
    val roomUtilization: Map<String, Float>,
    val conflicts: List<TimetableConflict>
)

// UI State
data class TimetableUiState(
    val viewMode: TimetableViewMode = TimetableViewMode.OVERVIEW,
    val colors: DashboardColors = DashboardColors.light(),
    val searchQuery: String = "",
    val selectedClassroomId: String? = null,
    val selectedTeacherId: String? = null,
    val selectedRoomId: String? = null,
    val selectedDay: DayOfWeek? = null,
    val timeSlots: List<TimeSlot> = emptyList(),
    val sessions: List<TimetableSession> = emptyList(),
    val classrooms: List<Classroom> = emptyList(),
    val teachers: List<Teacher> = emptyList(),
    val rooms: List<Room> = emptyList(),
    val subjects: List<Subject> = emptyList(),
    val templates: List<TimetableTemplate> = emptyList(),
    val statistics: TimetableStatistics = TimetableStatistics(
        totalSessions = 0,
        totalHoursPerWeek = 0f,
        averageSessionsPerDay = 0f,
        mostBusyDay = null,
        teacherUtilization = emptyMap(),
        roomUtilization = emptyMap(),
        conflicts = emptyList()
    ),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val showConflicts: Boolean = false
)
