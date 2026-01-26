package com.ecolix.presentation.screens.eleves

import cafe.adriel.voyager.core.model.StateScreenModel
import com.ecolix.data.models.StudentsUiState
import com.ecolix.data.models.StudentsViewMode
import kotlinx.coroutines.flow.update

class StudentsScreenModel : StateScreenModel<StudentsUiState>(StudentsUiState.sample(false)) {

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
