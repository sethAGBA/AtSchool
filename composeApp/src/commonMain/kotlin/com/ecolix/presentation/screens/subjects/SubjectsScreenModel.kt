package com.ecolix.presentation.screens.subjects

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.ecolix.atschool.api.AcademicApiService
import com.ecolix.atschool.api.StructureApiService
import com.ecolix.atschool.api.StaffApiService
import com.ecolix.atschool.api.SubjectDto
import com.ecolix.atschool.api.SubjectCategoryDto
import com.ecolix.atschool.api.ClassSubjectAssignmentDto
import com.ecolix.atschool.models.*
import com.ecolix.data.models.*
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SubjectsScreenModel(
    private val academicApiService: AcademicApiService,
    private val structureApiService: StructureApiService,
    private val staffApiService: StaffApiService
) : StateScreenModel<SubjectsUiState>(SubjectsUiState()) {

    init {
        screenModelScope.launch {
            loadAllData()
        }
    }

    private suspend fun loadAllData() {
        mutableState.update { it.copy(isLoading = true) }
        
        val categoriesResult = academicApiService.getAllCategories()
        val subjectsResult = academicApiService.getAllSubjects()
        val staffResult = staffApiService.getAllStaff()
        
        val categories = categoriesResult.getOrDefault(emptyList<SubjectCategoryDto>()).map { it.toCategory() }
        val subjects = subjectsResult.getOrDefault(emptyList<SubjectDto>()).map { dto ->
            val category = categories.find { it.id == dto.categoryId?.toString() }
            dto.toSubject(category)
        }
        
        val staff = staffResult.getOrDefault(emptyList<Staff>())
        
        // Fetch actual classrooms from StructureApiService
        val classroomsResult = structureApiService.getClasses()
        val classrooms = classroomsResult.getOrDefault(emptyList())
        
        val selectedClassId = state.value.selectedClassId ?: classrooms.firstOrNull()?.id

        mutableState.update { it.copy(
            subjects = subjects,
            categories = categories,
            classrooms = classrooms,
            selectedClassId = selectedClassId,
            staffMembers = staff,
            isLoading = false
        ) }

        if (selectedClassId != null) {
            loadClassAssignments(selectedClassId)
        }
    }

    private fun loadClassAssignments(classId: Int) {
        screenModelScope.launch {
            academicApiService.getClassSubjects(classId).onSuccess { assignments ->
                val configs = assignments.map { it.toConfig() }
                mutableState.update { it.copy(classroomConfigs = configs) }
            }
        }
    }

    fun refreshData() {
        screenModelScope.launch {
            loadAllData()
        }
    }

    fun onDarkModeChange(isDark: Boolean) {
        mutableState.update { it.copy(isDarkMode = isDark) }
    }

    fun onSearchQueryChange(query: String) {
        mutableState.update { it.copy(searchQuery = query) }
    }

    fun onCategoryChange(categoryId: String?) {
        mutableState.update { it.copy(selectedCategoryId = categoryId) }
    }

    fun toggleCategoriesDialog() {
        mutableState.update { it.copy(showCategoriesDialog = !it.showCategoriesDialog) }
    }

    fun onViewModeChange(mode: SubjectsViewMode) {
        mutableState.update { it.copy(viewMode = mode) }
        if (mode == SubjectsViewMode.SUBJECTS) {
            refreshData()
        }
    }

    fun onLayoutModeChange(mode: SubjectsLayoutMode) {
        mutableState.update { it.copy(layoutMode = mode) }
    }

    fun onSelectClass(classId: Int) {
        mutableState.update { it.copy(selectedClassId = classId) }
        loadClassAssignments(classId)
    }

    fun onSelectSubject(subject: Subject?) {
        mutableState.update { it.copy(selectedSubject = subject) }
    }

    fun saveSubject(subject: Subject) {
        screenModelScope.launch {
            val dto = subject.toDto()
            val idInt = subject.id.toIntOrNull()
            val result = if (idInt == null) {
                academicApiService.createSubject(dto)
            } else {
                academicApiService.updateSubject(idInt, dto)
            }
            
            result.onSuccess {
                refreshData()
                onViewModeChange(SubjectsViewMode.SUBJECTS)
            }
        }
    }

    fun deleteSubject(subjectId: String) {
        screenModelScope.launch {
            val idInt = subjectId.toIntOrNull() ?: return@launch
            academicApiService.deleteSubject(idInt).onSuccess {
                refreshData()
            }
        }
    }

    // Teachers Management
    fun getAvailableTeachers(): List<Staff> {
        return state.value.staffMembers.filter { it.role == StaffRole.TEACHER }
    }

    fun updateClassSubjectProfessor(classId: Int, subjectId: String, professorId: String?) {
        val config = state.value.classroomConfigs.find { it.classId == classId && it.subjectId == subjectId }
            ?: ClassSubjectConfig(classId, subjectId)
        
        val updatedConfig = config.copy(professorId = professorId)
        
        screenModelScope.launch {
            academicApiService.saveClassSubject(updatedConfig.toAssignmentDto()).onSuccess {
                loadClassAssignments(classId)
            }
        }
    }

    fun updateClassSubjectConfig(classId: Int, subjectId: String, coefficient: Float, weeklyHours: Int) {
        val config = state.value.classroomConfigs.find { it.classId == classId && it.subjectId == subjectId }
            ?: ClassSubjectConfig(classId, subjectId)
        
        val updatedConfig = config.copy(coefficient = coefficient, weeklyHours = weeklyHours)
        
        screenModelScope.launch {
            academicApiService.saveClassSubject(updatedConfig.toAssignmentDto()).onSuccess {
                loadClassAssignments(classId)
            }
        }
    }

    fun toggleSubjectInClass(classId: Int, subjectId: String) {
        val sidInt = subjectId.toIntOrNull() ?: return
        screenModelScope.launch {
            academicApiService.toggleClassSubject(classId, sidInt).onSuccess {
                loadClassAssignments(classId)
            }
        }
    }

    // Mappings
    private fun SubjectCategoryDto.toCategory() = Category(
        id = id?.toString() ?: "",
        name = name,
        description = description,
        colorHex = colorHex,
        order = sortOrder
    )

    private fun SubjectDto.toSubject(category: Category?) = Subject(
        id = id?.toString() ?: "",
        name = nom,
        code = code,
        categoryId = categoryId?.toString(),
        categoryName = category?.name ?: "Non classée",
        categoryColorHex = category?.colorHex ?: "#6366F1",
        defaultCoefficient = defaultCoefficient,
        description = description,
        weeklyHours = weeklyHours
    )

    private fun Subject.toDto() = SubjectDto(
        id = id.toIntOrNull(),
        nom = name,
        code = code,
        categoryId = categoryId?.toIntOrNull(),
        defaultCoefficient = defaultCoefficient,
        weeklyHours = weeklyHours,
        description = description,
        colorHex = categoryColorHex // Assuming it's the subject's color if no category or derived
    )

    private fun ClassSubjectAssignmentDto.toConfig() = ClassSubjectConfig(
        classId = classeId,
        subjectId = matiereId.toString(),
        professorId = professeurId?.toString(),
        coefficient = coefficient ?: 1f,
        weeklyHours = weeklyHours ?: 2
    )

    private fun ClassSubjectConfig.toAssignmentDto() = ClassSubjectAssignmentDto(
        classeId = classId,
        matiereId = subjectId.toIntOrNull() ?: 0,
        professeurId = professorId?.toIntOrNull(),
        coefficient = coefficient,
        weeklyHours = weeklyHours
    )
}
