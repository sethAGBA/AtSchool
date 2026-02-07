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

import com.ecolix.data.services.SubjectDataCache
import com.ecolix.data.services.StaffDataCache
import com.ecolix.data.services.ClassroomDataCache

class SubjectsScreenModel(
    private val academicApiService: AcademicApiService,
    private val structureApiService: StructureApiService,
    private val staffApiService: StaffApiService,
    private val subjectCache: SubjectDataCache? = null,
    private val staffCache: StaffDataCache? = null,
    private val classroomCache: ClassroomDataCache? = null
) : StateScreenModel<SubjectsUiState>(SubjectsUiState()) {

    init {
        screenModelScope.launch {
            loadAllData()
        }
    }

    private suspend fun loadAllData() {
        mutableState.update { it.copy(isLoading = true) }
        
        // 1. Categories
        val cachedCategories = subjectCache?.get(SubjectDataCache.KEY_ALL_CATEGORIES) as? List<SubjectCategoryDto>
        val categories = if (cachedCategories != null) {
            cachedCategories.map { it.toCategory() }
        } else {
            val result = academicApiService.getAllCategories()
            val dtos = result.getOrDefault(emptyList())
            if (result.isSuccess) subjectCache?.put(SubjectDataCache.KEY_ALL_CATEGORIES, dtos)
            dtos.map { it.toCategory() }
        }

        // 2. Subjects
        val cachedSubjects = subjectCache?.get(SubjectDataCache.KEY_ALL_SUBJECTS) as? List<SubjectDto>
        val subjects = if (cachedSubjects != null) {
            cachedSubjects.map { dto ->
                val category = categories.find { it.id == dto.categoryId?.toString() }
                dto.toSubject(category)
            }
        } else {
            val result = academicApiService.getAllSubjects()
            val dtos = result.getOrDefault(emptyList())
            if (result.isSuccess) subjectCache?.put(SubjectDataCache.KEY_ALL_SUBJECTS, dtos)
            dtos.map { dto ->
                val category = categories.find { it.id == dto.categoryId?.toString() }
                dto.toSubject(category)
            }
        }
        
        // 3. Staff (Teachers)
        val cachedStaff = staffCache?.get(StaffDataCache.KEY_ALL_STAFF)
        val staff = if (cachedStaff != null) {
            cachedStaff
        } else {
            val result = staffApiService.getAllStaff()
            val list = result.getOrDefault(emptyList())
            if (result.isSuccess) staffCache?.put(StaffDataCache.KEY_ALL_STAFF, list)
            list
        }
        
        // 4. Classrooms
        val cachedClasses = classroomCache?.get(ClassroomDataCache.KEY_ALL_CLASSES)
        val classrooms = if (cachedClasses != null) {
            cachedClasses
        } else {
            val result = structureApiService.getClasses()
            val list = result.getOrDefault(emptyList())
            if (result.isSuccess) classroomCache?.put(ClassroomDataCache.KEY_ALL_CLASSES, list)
            list
        }
        
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
            // Check cache
            val cacheKey = SubjectDataCache.keyForClassAssignments(classId)
            val cachedAssignments = subjectCache?.get(cacheKey) as? List<ClassSubjectAssignmentDto>
            
            if (cachedAssignments != null) {
                val configs = cachedAssignments.map { it.toConfig() }
                mutableState.update { it.copy(classroomConfigs = configs) }
                return@launch
            }
            
            academicApiService.getClassSubjects(classId).onSuccess { assignments ->
                // Cache
                subjectCache?.put(cacheKey, assignments)
                
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
                subjectCache?.invalidate(SubjectDataCache.KEY_ALL_SUBJECTS)
                refreshData()
                onViewModeChange(SubjectsViewMode.SUBJECTS)
            }
        }
    }

    fun deleteSubject(subjectId: String) {
        screenModelScope.launch {
            val idInt = subjectId.toIntOrNull() ?: return@launch
            academicApiService.deleteSubject(idInt).onSuccess {
                subjectCache?.invalidate(SubjectDataCache.KEY_ALL_SUBJECTS)
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
                subjectCache?.invalidate(SubjectDataCache.keyForClassAssignments(classId))
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
                subjectCache?.invalidate(SubjectDataCache.keyForClassAssignments(classId))
                loadClassAssignments(classId)
            }
        }
    }

    fun toggleSubjectInClass(classId: Int, subjectId: String) {
        val sidInt = subjectId.toIntOrNull() ?: return
        screenModelScope.launch {
            academicApiService.toggleClassSubject(classId, sidInt).onSuccess {
                subjectCache?.invalidate(SubjectDataCache.keyForClassAssignments(classId))
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
