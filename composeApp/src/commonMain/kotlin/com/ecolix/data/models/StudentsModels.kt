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
    val academicYear: String,
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
    val currentYear: String = "2024-2025",
    val selectionMode: Boolean = false,
    val selectedStudentIds: Set<String> = emptySet(),
    val isDarkMode: Boolean = false,
    val loadedStudentsCount: Int = 10,
    val loadedClassesCount: Int = 10,
    val batchSize: Int = 10
) {
    val colors: DashboardColors
        get() = if (isDarkMode) DashboardColors.dark() else DashboardColors.light()

    val levelDistribution: Map<String, Int>
        get() = classrooms.groupBy { it.level }.mapValues { it.value.sumOf { c -> c.studentCount } }

    companion object {
        fun sample(isDarkMode: Boolean): StudentsUiState {
            val year = "2024-2025"
            return StudentsUiState(
                isDarkMode = isDarkMode,
                currentYear = year,
                students = listOf(
                    Student("S1", "Binta", "Diallo", "F", "6e A", year, "12/09/2024", "ACTIF", 14.5, "2024-001", "15/05/2012"),
                    Student("S2", "Moussa", "Traore", "M", "6e A", year, "12/09/2024", "ACTIF", 12.8, "2024-002", "20/08/2012"),
                    Student("S3", "Fatoumata", "Sow", "F", "5e B", year, "12/09/2024", "ACTIF", 15.2, "2024-003", "10/02/2011"),
                    Student("S4", "Amadou", "Barry", "M", "Terminale S", year, "10/09/2024", "ACTIF", 13.4, "2024-004", "05/11/2007"),
                    Student("S5", "Awa", "Keita", "F", "4e C", year, "14/09/2024", "SUSPENDU", 9.5, "2024-005", "12/12/2010"),
                    Student("S6", "Deleted", "User", "M", "6e A", year, "01/01/2024", "INACTIF", 0.0, isDeleted = true)
                ),
                classrooms = listOf(
                    Classroom("C1", "6e A", 42, 22, 20, "College", year, students = emptyList()),
                    Classroom("C2", "6e B", 40, 20, 20, "College", year, students = emptyList()),
                    Classroom("C3", "5e A", 38, 18, 20, "College", year, students = emptyList()),
                    Classroom("C4", "Terminale S", 32, 16, 16, "Lycee", year, students = emptyList())
                )
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
    PROFILE,
    CLASS_DETAILS,
    STUDENT_FORM,
    CLASS_FORM
}
