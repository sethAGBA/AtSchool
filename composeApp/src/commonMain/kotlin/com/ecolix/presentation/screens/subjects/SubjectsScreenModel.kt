package com.ecolix.presentation.screens.subjects

import cafe.adriel.voyager.core.model.ScreenModel
import com.ecolix.atschool.models.Staff
import com.ecolix.atschool.models.StaffRole
import com.ecolix.data.models.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SubjectsScreenModel : ScreenModel {

    private val _state = MutableStateFlow(SubjectsUiState.sample(false))
    val state: StateFlow<SubjectsUiState> = _state.asStateFlow()

    init {
        // Load initial categories
        // In a real app, this would come from a repository
        val initialCategories = listOf(
            Category("1", "Scientifique", "Sciences exactes", "#3B82F6", 1),
            Category("2", "Littéraire", "Langues et littérature", "#EC4899", 2),
            Category("3", "Général", "Matières communes", "#6366F1", 3),
            Category("4", "Sports", "Education physique", "#10B981", 4)
        )
        val initialClassrooms = listOf("6ème A", "6ème B", "5ème A", "4ème Espagnol", "3ème Rouge")
        
        // Sample configs: Assign MATH and FRAN to 6ème A
        val initialConfigs = listOf(
            ClassSubjectConfig("6ème A", "1", coefficient = 4f, weeklyHours = 5),
            ClassSubjectConfig("6ème A", "2", coefficient = 4f, weeklyHours = 4),
            ClassSubjectConfig("6ème B", "1", coefficient = 3f, weeklyHours = 4)
        )

        _state.update { it.copy(
            categories = initialCategories, 
            classrooms = initialClassrooms,
            selectedClass = initialClassrooms.firstOrNull(),
            classroomConfigs = initialConfigs
        ) }
    }

    fun onDarkModeChange(isDark: Boolean) {
        _state.update { it.copy(isDarkMode = isDark) }
    }

    fun onSearchQueryChange(query: String) {
        _state.update { it.copy(searchQuery = query) }
    }

    fun onCategoryChange(categoryId: String?) {
        _state.update { it.copy(selectedCategoryId = categoryId) }
    }

    fun toggleCategoriesDialog() {
        _state.update { it.copy(showCategoriesDialog = !it.showCategoriesDialog) }
    }

    fun onViewModeChange(mode: SubjectsViewMode) {
        _state.update { it.copy(viewMode = mode) }
    }

    fun onLayoutModeChange(mode: SubjectsLayoutMode) {
        _state.update { it.copy(layoutMode = mode) }
    }

    fun onSelectClass(className: String) {
        _state.update { it.copy(selectedClass = className) }
    }

    fun onSelectSubject(subject: Subject?) {
        _state.update { it.copy(selectedSubject = subject) }
    }

    fun saveSubject(subject: Subject) {
        _state.update { state ->
            val updatedList = state.subjects.toMutableList()
            val index = updatedList.indexOfFirst { it.id == subject.id }
            if (index != -1) {
                updatedList[index] = subject
            } else {
                updatedList.add(subject)
            }
            state.copy(subjects = updatedList, viewMode = SubjectsViewMode.SUBJECTS)
        }
    }

    fun deleteSubject(subjectId: String) {
        _state.update { state ->
            state.copy(subjects = state.subjects.filterNot { it.id == subjectId })
        }
    }

    fun updateState(newState: SubjectsUiState) {
        _state.update { newState }
    }

    // Teachers Management
    fun getAvailableTeachers(): List<Staff> {
        // In a real app, this would come from a Repository. 
        return emptyList()
    }

    fun updateClassSubjectProfessor(className: String, subjectId: String, professorId: String?) {
        _state.update { state ->
            val updatedConfigs = state.classroomConfigs.toMutableList()
            val index = updatedConfigs.indexOfFirst { it.className == className && it.subjectId == subjectId }
            if (index != -1) {
                updatedConfigs[index] = updatedConfigs[index].copy(professorId = professorId)
            } else {
                updatedConfigs.add(ClassSubjectConfig(className, subjectId, professorId))
            }
            state.copy(classroomConfigs = updatedConfigs)
        }
    }

    fun updateClassSubjectConfig(className: String, subjectId: String, coefficient: Float, weeklyHours: Int) {
        _state.update { state ->
            val updatedConfigs = state.classroomConfigs.toMutableList()
            val index = updatedConfigs.indexOfFirst { it.className == className && it.subjectId == subjectId }
            if (index != -1) {
                updatedConfigs[index] = updatedConfigs[index].copy(coefficient = coefficient, weeklyHours = weeklyHours)
            } else {
                updatedConfigs.add(ClassSubjectConfig(className, subjectId, coefficient = coefficient, weeklyHours = weeklyHours))
            }
            state.copy(classroomConfigs = updatedConfigs)
        }
    }

    fun toggleSubjectInClass(className: String, subjectId: String) {
        _state.update { state ->
            val updatedConfigs = state.classroomConfigs.toMutableList()
            val index = updatedConfigs.indexOfFirst { it.className == className && it.subjectId == subjectId }
            if (index != -1) {
                updatedConfigs.removeAt(index)
            } else {
                updatedConfigs.add(ClassSubjectConfig(className, subjectId))
            }
            state.copy(classroomConfigs = updatedConfigs)
        }
    }
}
