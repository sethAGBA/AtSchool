package com.ecolix.data.models

import androidx.compose.runtime.Immutable
import com.ecolix.data.models.DashboardColors

@Immutable
data class Student(
    val id: String,
    val firstName: String,
    val lastName: String,
    val gender: String, // "M" or "F"
    val classroom: String,
    val classroomId: Int? = null,
    val academicYear: String,
    val enrollmentDate: String,
    val status: String, // Dynamic from form (Nouveau, Redoublant, etc.)
    val averageGrade: Double = 0.0,
    val matricule: String? = null,
    val dateOfBirth: String? = null,
    val placeOfBirth: String? = null,
    val address: String? = null,
    val contactNumber: String? = null,
    val email: String? = null,
    val emergencyContact: String? = null,
    val guardianName: String? = null,
    val guardianContact: String? = null,
    val medicalInfo: String? = null,
    val bloodGroup: String? = null,
    val remarks: String? = null,
    val isDeleted: Boolean = false,
    val nationality: String? = null,
    val photoUrl: String? = null,
    val documents: List<StudentDocument> = emptyList()
)

@Immutable
data class StudentDocument(
    val id: String,
    val name: String,
    val path: String,
    val addedAt: String
)

enum class StudentStatus {
    ACTIVE,
    INACTIVE,
    SUSPENDED,
    GRADUATED
}

@Immutable
data class Classroom(
    val id: String,
    val name: String,
    val studentCount: Int,
    val boysCount: Int,
    val girlsCount: Int,
    val level: String,
    val schoolLevelId: Int? = null,
    val academicYear: String,
    val cycle: String? = null,
    val mainTeacher: String? = null,
    val roomNumber: String? = null,
    val capacity: Int? = null,
    val description: String? = null,
    val students: List<Student> = emptyList()
)

@Immutable
data class StudentsUiState(
    val students: List<Student>,
    val classrooms: List<Classroom>,
    val viewMode: StudentsViewMode = StudentsViewMode.CLASSES,
    val studentDisplayMode: StudentDisplayMode = StudentDisplayMode.LIST,
    val selectedClassroom: String? = null,
    val selectedStudentId: String? = null,
    val selectedLevel: String? = null,
    val selectedGender: String? = null,
    val visibilityFilter: String = "active", // "active", "deleted", "all"
    val searchQuery: String = "",
    val currentYear: String = "",
    val selectionMode: Boolean = false,
    val selectedStudentIds: Set<String> = emptySet(),
    val isDarkMode: Boolean = false,
    val loadedStudentsCount: Int = 10,
    val loadedClassesCount: Int = 10,
    val batchSize: Int = 50,
    val isClassroomFixed: Boolean = false,
    val showDeleteConfirmation: Boolean = false,
    val studentToDeleteIds: Set<String> = emptySet(),
    val schoolYears: List<com.ecolix.atschool.api.SchoolYearDto> = emptyList(),
    // School Structure
    val cycles: List<com.ecolix.atschool.api.SchoolCycleDto> = emptyList(),
    val levels: List<com.ecolix.atschool.api.SchoolLevelDto> = emptyList(),
    val isUploadingPhoto: Boolean = false,
    val photoUploadError: String? = null,
    val showTransferDialog: Boolean = false,
    val transferStudentIds: Set<String> = emptySet(),
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val lastSavedTimestamp: Long? = null,
    val showClassDeleteConfirmation: Boolean = false,
    val classToDeleteId: String? = null
) {
    val colors: DashboardColors
        get() = if (isDarkMode) DashboardColors.dark() else DashboardColors.light()

    val levelDistribution: Map<String, Int>
        get() = classrooms.groupBy { it.level }.mapValues { it.value.sumOf { c -> c.studentCount } }

    companion object {
        fun sample(isDarkMode: Boolean): StudentsUiState {
            return StudentsUiState(
                isDarkMode = isDarkMode,
                currentYear = "",
                students = emptyList(),
                classrooms = emptyList()
            )
        }
    }
}

enum class StudentDisplayMode {
    LIST,
    GRID
}

enum class StudentsViewMode {
    CLASSES,
    STUDENTS,
    STRUCTURE,
    PROFILE,
    CLASS_DETAILS,
    STUDENT_FORM,
    CLASS_FORM
}
