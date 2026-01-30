package com.ecolix.presentation.screens.eleves

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.ecolix.atschool.api.StudentApiService
import com.ecolix.atschool.api.StudentResponse
import com.ecolix.data.models.Student
import com.ecolix.data.models.StudentsUiState
import com.ecolix.data.models.StudentsViewMode
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class StudentsScreenModel(private val studentApiService: StudentApiService) : 
    StateScreenModel<StudentsUiState>(StudentsUiState.sample(false)) {

    init {
        loadStudents()
    }

    private fun loadStudents() {
        screenModelScope.launch {
            studentApiService.getStudents().onSuccess { responses ->
                val mappedStudents = responses.map { it.toUiStudent() }
                mutableState.update { it.copy(students = mappedStudents) }
            }
        }
    }

    private fun StudentResponse.toUiStudent(): Student {
        val formattedDate = if (this.dateNaissance.contains("-")) {
            val parts = this.dateNaissance.split("-")
            if (parts.size == 3) "${parts[2]}/${parts[1]}/${parts[0]}" else this.dateNaissance
        } else {
            this.dateNaissance
        }

        return Student(
            id = this.id?.toString() ?: "",
            firstName = this.prenom,
            lastName = this.nom,
            gender = this.sexe,
            classroom = "Non assigné",
            academicYear = "2024-2025",
            enrollmentDate = "",
            status = "ACTIF",
            matricule = this.matricule,
            dateOfBirth = formattedDate
        )
    }

    private fun Student.toResponse(): StudentResponse {
        val normalizedDate = if (this.dateOfBirth?.contains("/") == true) {
            val parts = this.dateOfBirth.split("/")
            if (parts.size == 3) "${parts[2]}-${parts[1]}-${parts[0]}" else "2010-01-01"
        } else {
            this.dateOfBirth ?: "2010-01-01"
        }

        return StudentResponse(
            id = this.id.toLongOrNull(),
            tenantId = 0, // Tenant ID is handled by server from JWT
            matricule = this.matricule ?: "MAT-${kotlin.random.Random.nextInt(10000)}",
            nom = this.lastName,
            prenom = this.firstName,
            dateNaissance = normalizedDate,
            sexe = this.gender
        )
    }

    fun saveStudent(student: Student) {
        screenModelScope.launch {
            val response = student.toResponse()
            val result = if (response.id == null) {
                studentApiService.addStudent(response)
            } else {
                studentApiService.updateStudent(response.id!!, response)
            }
            
            result.onSuccess {
                loadStudents()
                onViewModeChange(StudentsViewMode.STUDENTS)
            }.onFailure {
                it.printStackTrace()
            }
        }
    }

    fun deleteStudent(id: String) {
        screenModelScope.launch {
            val longId = id.toLongOrNull() ?: return@launch
            studentApiService.deleteStudent(longId).onSuccess {
                loadStudents()
            }
        }
    }

    fun onDarkModeChange(isDark: Boolean) {
        mutableState.update { it.copy(isDarkMode = isDark) }
    }

    fun onSearchQueryChange(query: String) {
        mutableState.update { 
            it.copy(
                searchQuery = query,
                loadedStudentsCount = it.batchSize,
                loadedClassesCount = it.batchSize
            ) 
        }
    }

    fun onClassroomChange(classroomId: String?) {
        mutableState.update { 
            it.copy(
                selectedClassroom = classroomId,
                loadedStudentsCount = it.batchSize
            ) 
        }
    }

    fun onLevelChange(level: String?) {
        mutableState.update { 
            it.copy(
                selectedLevel = level,
                loadedClassesCount = it.batchSize,
                loadedStudentsCount = it.batchSize
            ) 
        }
    }

    fun onGenderChange(gender: String?) {
        mutableState.update { 
            it.copy(
                selectedGender = gender,
                loadedStudentsCount = it.batchSize
            ) 
        }
    }

    fun onVisibilityChange(visibility: String) {
        mutableState.update { 
            it.copy(
                visibilityFilter = visibility,
                loadedStudentsCount = it.batchSize
            ) 
        }
    }

    fun onStudentDisplayModeChange(mode: com.ecolix.data.models.StudentDisplayMode) {
        mutableState.update { it.copy(studentDisplayMode = mode) }
    }

    fun onViewModeChange(mode: StudentsViewMode) {
        mutableState.update { 
            it.copy(
                viewMode = mode,
                selectionMode = false,
                selectedStudentIds = emptySet()
            ) 
        }
    }

    fun loadMoreStudents() {
        mutableState.update { 
            val newCount = it.loadedStudentsCount + it.batchSize
            it.copy(loadedStudentsCount = newCount)
        }
    }

    fun loadMoreClasses() {
        mutableState.update { 
            val newCount = it.loadedClassesCount + it.batchSize
            it.copy(loadedClassesCount = newCount)
        }
    }

    fun updateState(newState: StudentsUiState) {
        mutableState.update { newState }
    }
}
