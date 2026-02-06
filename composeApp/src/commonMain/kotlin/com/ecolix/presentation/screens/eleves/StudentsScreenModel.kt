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
import kotlinx.datetime.Clock
import com.ecolix.atschool.api.UploadApiService
import com.ecolix.atschool.api.StructureApiService

class StudentsScreenModel(
    private val studentApiService: StudentApiService,
    private val structureApiService: com.ecolix.atschool.api.StructureApiService,
    private val uploadApiService: com.ecolix.atschool.api.UploadApiService
) : StateScreenModel<StudentsUiState>(StudentsUiState.sample(false)) {

    init {
        screenModelScope.launch {
            loadActiveYear().join()
            loadStudents()
            loadStructure().join()
            loadClassrooms()
        }
    }

    private fun loadClassrooms(): kotlinx.coroutines.Job {
        return screenModelScope.launch {
            structureApiService.getClasses().onSuccess { classes ->
                val mappedClassrooms = classes.map { it.toUiClassroom() }
                updateClassroomCounts(mappedClassrooms)
            }
        }
    }

    private fun com.ecolix.atschool.api.ClassDto.toUiClassroom(): com.ecolix.data.models.Classroom {
        val level = this.schoolLevelId?.let { id ->
            state.value.levels.find { it.id == id }
        }
        val levelName = level?.name ?: this.legacyLevel ?: "Niveaux"
        val cycleName = level?.cycleId?.let { cId ->
            state.value.cycles.find { it.id == cId }?.name
        }

        return com.ecolix.data.models.Classroom(
            id = this.id?.toString() ?: "",
            name = this.nom,
            studentCount = 0, 
            boysCount = 0,
            girlsCount = 0,
            level = levelName,
            cycle = cycleName,
            schoolLevelId = this.schoolLevelId,
            academicYear = state.value.currentYear,
            mainTeacher = this.mainTeacher,
            roomNumber = this.roomNumber,
            capacity = this.capacity,
            description = this.description
        )
    }

    private fun com.ecolix.data.models.Classroom.toDto(): com.ecolix.atschool.api.ClassDto {
        return com.ecolix.atschool.api.ClassDto(
            id = this.id.toIntOrNull(),
            tenantId = 0,
            nom = this.name,
            code = "CL-${this.name.uppercase().replace(" ", "")}",
            schoolLevelId = this.schoolLevelId,
            legacyLevel = this.level,
            mainTeacher = this.mainTeacher,
            roomNumber = this.roomNumber,
            capacity = this.capacity,
            description = this.description
        )
    }

    fun saveClassroom(classroom: com.ecolix.data.models.Classroom) {
        screenModelScope.launch {
            val dto = classroom.toDto()
            val result = if (dto.id == null) {
                structureApiService.createClass(dto)
            } else {
                structureApiService.updateClass(dto.id!!, dto)
            }
            
            result.onSuccess {
                loadClassrooms()
                mutableState.update { it.copy(successMessage = if (dto.id == null) "Classe créée avec succès" else "Classe mise à jour avec succès") }
                onViewModeChange(com.ecolix.data.models.StudentsViewMode.CLASSES)
            }.onFailure { error ->
                error.printStackTrace()
                mutableState.update { s -> s.copy(errorMessage = "Erreur lors de l'enregistrement de la classe : ${error.message}") }
            }
        }
    }

    fun deleteClassroom(classId: String) {
        screenModelScope.launch {
            val id = classId.toIntOrNull() ?: return@launch
            structureApiService.deleteClass(id).onSuccess {
                loadClassrooms()
                mutableState.update { it.copy(successMessage = "Classe supprimée avec succès") }
                onViewModeChange(com.ecolix.data.models.StudentsViewMode.CLASSES)
            }.onFailure { error ->
                error.printStackTrace()
                mutableState.update { it.copy(errorMessage = "Erreur lors de la suppression de la classe : ${error.message}") }
            }
        }
    }

    fun onDeleteClassAttempt(classId: String) {
        mutableState.update { it.copy(showClassDeleteConfirmation = true, classToDeleteId = classId) }
    }

    fun dismissClassDeleteConfirmation() {
        mutableState.update { it.copy(showClassDeleteConfirmation = false, classToDeleteId = null) }
    }

    fun confirmClassDeletion() {
        val classId = state.value.classToDeleteId ?: return
        deleteClassroom(classId)
        dismissClassDeleteConfirmation()
    }

    private fun loadStructure(): kotlinx.coroutines.Job {
        return screenModelScope.launch {
            val cyclesResult = structureApiService.getSchoolCycles()
            val levelsResult = structureApiService.getAllSchoolLevels()
            
            if (cyclesResult.isSuccess && levelsResult.isSuccess) {
                mutableState.update { 
                    it.copy(
                        cycles = cyclesResult.getOrDefault(emptyList()),
                        levels = levelsResult.getOrDefault(emptyList())
                    ) 
                }
            }
        }
    }

    private fun loadStudents() {
        screenModelScope.launch {
            studentApiService.getStudents().onSuccess { responses ->
                val mappedStudents = responses.map { it.toUiStudent() }
                mutableState.update { it.copy(students = mappedStudents) }
                updateClassroomCounts(state.value.classrooms)
            }
        }
    }

    private fun updateClassroomCounts(classrooms: List<com.ecolix.data.models.Classroom>) {
        val students = state.value.students
        val updatedClassrooms = classrooms.map { classroom ->
            classroom.copy(
                studentCount = students.count { it.classroom == classroom.name && !it.isDeleted },
                boysCount = students.count { it.classroom == classroom.name && it.gender == "M" && !it.isDeleted },
                girlsCount = students.count { it.classroom == classroom.name && it.gender == "F" && !it.isDeleted }
            )
        }
        mutableState.update { it.copy(classrooms = updatedClassrooms) }
    }

    private fun StudentResponse.toUiStudent(): Student {
        val formattedBirthDate = if (this.dateNaissance.contains("-")) {
            val parts = this.dateNaissance.split("-")
            if (parts.size == 3) "${parts[2]}/${parts[1]}/${parts[0]}" else this.dateNaissance
        } else {
            this.dateNaissance
        }

        val dateIns = this.dateInscription
        val formattedEnrollmentDate = if (dateIns != null && dateIns.contains("-")) {
            val parts = dateIns.split("-")
            if (parts.size == 3) "${parts[2]}/${parts[1]}/${parts[0]}" else dateIns
        } else {
            dateIns ?: ""
        }

        return Student(
            id = this.id?.toString() ?: "",
            firstName = this.prenom,
            lastName = this.nom,
            gender = this.sexe,
            classroom = this.classeNom ?: "Non assigné",
            classroomId = this.classeId,
            academicYear = state.value.currentYear,
            enrollmentDate = formattedEnrollmentDate,
            status = "ACTIF",
            matricule = this.matricule,
            dateOfBirth = formattedBirthDate,
            placeOfBirth = this.lieuNaissance,
            address = this.adresse,
            contactNumber = this.telephone,
            email = this.email,
            emergencyContact = this.contactUrgence,
            guardianName = this.nomTuteur,
            guardianContact = this.contactTuteur,
            medicalInfo = this.infoMedicale,
            bloodGroup = this.groupeSanguin,
            remarks = this.remarques,
            nationality = this.nationalite,
            photoUrl = this.photoUrl,
            isDeleted = this.deleted
        )
    }

    private fun Student.toResponse(): StudentResponse {
        val normalizedBirthDate = if (this.dateOfBirth?.contains("/") == true) {
            val parts = this.dateOfBirth.split("/")
            if (parts.size == 3) "${parts[2]}-${parts[1]}-${parts[0]}" else "2010-01-01"
        } else {
            this.dateOfBirth ?: "2010-01-01"
        }

        val resolvedClasseId = this.classroomId ?: state.value.classrooms.find { it.name == this.classroom }?.id?.toIntOrNull()

        val normalizedEnrollmentDate = if (this.enrollmentDate.contains("/")) {
            val parts = this.enrollmentDate.split("/")
            if (parts.size == 3) "${parts[2]}-${parts[1]}-${parts[0]}" else "2026-01-24"
        } else {
            this.enrollmentDate
        }

        return StudentResponse(
            id = this.id.toLongOrNull(),
            tenantId = 0, // Tenant ID is handled by server from JWT
            matricule = this.matricule ?: "MAT-${kotlin.random.Random.nextInt(10000)}",
            nom = this.lastName,
            prenom = this.firstName,
            dateNaissance = normalizedBirthDate,
            sexe = this.gender,
            classeId = resolvedClasseId,
            classeNom = this.classroom,
            dateInscription = normalizedEnrollmentDate,
            lieuNaissance = this.placeOfBirth,
            adresse = this.address,
            telephone = this.contactNumber,
            email = this.email,
            contactUrgence = this.emergencyContact,
            nomTuteur = this.guardianName,
            contactTuteur = this.guardianContact,
            infoMedicale = this.medicalInfo,
            groupeSanguin = this.bloodGroup,
            remarques = this.remarks,
            nationalite = this.nationality,
            photoUrl = this.photoUrl
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
                mutableState.update { 
                    it.copy(
                        successMessage = if (response.id == null) "Élève ajouté avec succès" else "Élève mis à jour avec succès",
                        lastSavedTimestamp = Clock.System.now().toEpochMilliseconds()
                    ) 
                }
                if (!state.value.isClassroomFixed) {
                    onViewModeChange(StudentsViewMode.STUDENTS)
                }
            }.onFailure { error ->
                error.printStackTrace()
                mutableState.update { it.copy(errorMessage = "Erreur lors de l'enregistrement de l'élève : ${error.message}") }
            }
        }
    }

    fun onDeleteAttempt(ids: Set<String>) {
        mutableState.update { it.copy(showDeleteConfirmation = true, studentToDeleteIds = ids) }
    }

    fun onDismissDeleteConfirmation() {
        mutableState.update { it.copy(showDeleteConfirmation = false, studentToDeleteIds = emptySet()) }
    }

    fun deleteStudent(id: String) {
        screenModelScope.launch {
            val longId = id.toLongOrNull() ?: return@launch
            studentApiService.deleteStudent(longId).onSuccess {
                loadStudents()
                mutableState.update { 
                    it.copy(
                        showDeleteConfirmation = false,
                        studentToDeleteIds = emptySet(),
                        selectedStudentIds = it.selectedStudentIds - id
                    )
                }
            }
        }
    }

    fun deleteSelectedStudents() {
        screenModelScope.launch {
            val idsToDelete = state.value.studentToDeleteIds
            idsToDelete.forEach { id ->
                val longId = id.toLongOrNull() ?: return@forEach
                studentApiService.deleteStudent(longId).onSuccess {
                    // Soft delete successful
                }.onFailure { error ->
                    error.printStackTrace()
                    mutableState.update { it.copy(errorMessage = "Erreur lors de la mise à la corbeille : ${error.message}") }
                }
            }
            loadStudents()
            mutableState.update { 
                it.copy(
                    showDeleteConfirmation = false, 
                    studentToDeleteIds = emptySet(),
                    selectedStudentIds = emptySet(),
                    selectionMode = false
                ) 
            }
        }
    }

    fun restoreStudent(id: String) {
        screenModelScope.launch {
            val longId = id.toLongOrNull() ?: return@launch
            studentApiService.restoreStudent(longId).onSuccess {
                loadStudents()
                mutableState.update { it.copy(selectedStudentIds = it.selectedStudentIds - id) }
            }
        }
    }

    fun restoreSelectedStudents() {
        screenModelScope.launch {
            val ids = state.value.selectedStudentIds
            ids.forEach { id ->
                val longId = id.toLongOrNull() ?: return@forEach
                studentApiService.restoreStudent(longId).onSuccess {
                    // Restored
                }
            }
            loadStudents()
            mutableState.update { it.copy(selectedStudentIds = emptySet(), selectionMode = false) }
        }
    }

    fun deleteSelectedStudentsPermanently() {
        screenModelScope.launch {
            val idsToDelete = state.value.studentToDeleteIds
            idsToDelete.forEach { id ->
                val longId = id.toLongOrNull() ?: return@forEach
                studentApiService.deleteStudentPermanently(longId).onSuccess {
                    // Permanent delete successful
                }
            }
            loadStudents()
            mutableState.update { 
                it.copy(
                    showDeleteConfirmation = false, 
                    studentToDeleteIds = emptySet(),
                    selectedStudentIds = emptySet(),
                    selectionMode = false
                ) 
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
                loadedStudentsCount = it.batchSize,
                viewMode = if (visibility == "deleted") StudentsViewMode.STUDENTS else it.viewMode,
                selectedClassroom = if (visibility == "deleted") null else it.selectedClassroom,
                selectedLevel = if (visibility == "deleted") null else it.selectedLevel
            ) 
        }
    }

    fun onStudentDisplayModeChange(mode: com.ecolix.data.models.StudentDisplayMode) {
        mutableState.update { it.copy(studentDisplayMode = mode) }
    }

    fun onViewModeChange(mode: StudentsViewMode) {
        mutableState.update { 
            val resetFilters = mode == StudentsViewMode.STRUCTURE || mode == StudentsViewMode.CLASSES || mode == StudentsViewMode.STUDENTS
            it.copy(
                viewMode = mode,
                selectionMode = false,
                selectedStudentIds = emptySet(),
                selectedClassroom = if (resetFilters) null else it.selectedClassroom,
                selectedLevel = if (resetFilters) null else it.selectedLevel,
                selectedGender = if (resetFilters) null else it.selectedGender,
                searchQuery = if (resetFilters) "" else it.searchQuery,
                isClassroomFixed = if (mode != StudentsViewMode.STUDENT_FORM) false else it.isClassroomFixed
            ) 
        }
    }

    fun onQuickAddStudent(classroomId: String) {
        mutableState.update {
            it.copy(
                viewMode = StudentsViewMode.STUDENT_FORM,
                selectedStudentId = null,
                selectedClassroom = classroomId,
                isClassroomFixed = true
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

    // Structure Mutation Methods
    fun createCycle(name: String, sortOrder: Int) {
        screenModelScope.launch {
            val cycle = com.ecolix.atschool.api.SchoolCycleDto(
                id = null,
                tenantId = 0,
                name = name,
                sortOrder = sortOrder
            )
            structureApiService.createSchoolCycle(cycle).onSuccess {
                loadStructure()
                mutableState.update { it.copy(successMessage = "Cycle '${name}' créé avec succès") }
            }.onFailure { error ->
                error.printStackTrace()
                mutableState.update { it.copy(errorMessage = "Erreur lors de la création du cycle : ${error.message}") }
            }
        }
    }

    fun updateCycle(id: Int, name: String, sortOrder: Int) {
        screenModelScope.launch {
            val cycle = com.ecolix.atschool.api.SchoolCycleDto(
                id = id,
                tenantId = 0,
                name = name,
                sortOrder = sortOrder
            )
            structureApiService.updateSchoolCycle(id, cycle).onSuccess {
                loadStructure()
                mutableState.update { it.copy(successMessage = "Cycle mis à jour avec succès") }
            }.onFailure { error ->
                error.printStackTrace()
                mutableState.update { it.copy(errorMessage = "Erreur lors de la mise à jour du cycle : ${error.message}") }
            }
        }
    }

    fun deleteCycle(id: Int) {
        screenModelScope.launch {
            structureApiService.deleteSchoolCycle(id).onSuccess {
                loadStructure()
                mutableState.update { it.copy(successMessage = "Cycle supprimé avec succès") }
            }.onFailure { error ->
                error.printStackTrace()
                mutableState.update { it.copy(errorMessage = "Erreur lors de la suppression du cycle : ${error.message}") }
            }
        }
    }

    fun createLevel(cycleId: Int, name: String, sortOrder: Int) {
        screenModelScope.launch {
            val level = com.ecolix.atschool.api.SchoolLevelDto(
                id = null,
                tenantId = 0,
                cycleId = cycleId,
                name = name,
                sortOrder = sortOrder
            )
            structureApiService.createSchoolLevel(level).onSuccess {
                loadStructure()
                mutableState.update { it.copy(successMessage = "Niveau '${name}' créé avec succès") }
            }.onFailure { error ->
                error.printStackTrace()
                mutableState.update { it.copy(errorMessage = "Erreur lors de la création du niveau : ${error.message}") }
            }
        }
    }

    fun updateLevel(id: Int, cycleId: Int, name: String, sortOrder: Int) {
        screenModelScope.launch {
            val level = com.ecolix.atschool.api.SchoolLevelDto(
                id = id,
                tenantId = 0,
                cycleId = cycleId,
                name = name,
                sortOrder = sortOrder
            )
            structureApiService.updateSchoolLevel(id, level).onSuccess {
                loadStructure()
                mutableState.update { it.copy(successMessage = "Niveau mis à jour avec succès") }
            }.onFailure { error ->
                error.printStackTrace()
                mutableState.update { it.copy(errorMessage = "Erreur lors de la mise à jour du niveau : ${error.message}") }
            }
        }
    }

    fun deleteLevel(id: Int) {
        screenModelScope.launch {
            structureApiService.deleteSchoolLevel(id).onSuccess {
                loadStructure()
                mutableState.update { it.copy(successMessage = "Niveau supprimé avec succès") }
            }.onFailure { error ->
                error.printStackTrace()
                mutableState.update { it.copy(errorMessage = "Erreur lors de la suppression du niveau : ${error.message}") }
            }
        }
    }

    fun seedDefaultStructure() {
        screenModelScope.launch {
            structureApiService.seedDefaultStructure().onSuccess {
                loadStructure()
                mutableState.update { it.copy(successMessage = "Structure par défaut générée avec succès") }
            }.onFailure { error ->
                error.printStackTrace()
                mutableState.update { it.copy(errorMessage = "Erreur lors de la génération de la structure : ${error.message}") }
            }
        }
    }

    private fun loadActiveYear(): kotlinx.coroutines.Job {
        return screenModelScope.launch {
            structureApiService.getSchoolYears().onSuccess { years ->
                val activeYear = years.find { it.status == "ACTIVE" } ?: years.find { it.isDefault } ?: years.firstOrNull()
                activeYear?.let { year ->
                    mutableState.update { it.copy(
                        currentYear = year.libelle,
                        schoolYears = years
                    ) }
                }
            }
        }
    }

    fun onYearChange(yearLabel: String) {
        val selectedYear = state.value.schoolYears.find { it.libelle == yearLabel } ?: return
        mutableState.update { it.copy(currentYear = selectedYear.libelle) }
        // Refresh all data for the new year
        screenModelScope.launch {
            loadStudents()
            loadClassrooms()
        }
    }

    fun refreshData() {
        screenModelScope.launch {
            loadStudents()
            loadStructure().join()
            loadClassrooms()
            mutableState.update { it.copy(successMessage = "Données actualisées") }
        }
    }

    fun clearError() {
        mutableState.update { it.copy(errorMessage = null) }
    }

    fun clearSuccess() {
        mutableState.update { it.copy(successMessage = null) }
    }

    fun pickAndUploadPhoto(onPhotoUploaded: (String) -> Unit) {
        screenModelScope.launch {
            val fileData = com.ecolix.utils.FilePicker.pickFile() ?: return@launch
            
            mutableState.update { state: StudentsUiState -> state.copy(isUploadingPhoto = true, photoUploadError = null) }
            
            uploadApiService.uploadFile(fileData.name, fileData.bytes)
                .onSuccess { url: String ->
                    mutableState.update { state: StudentsUiState -> state.copy(isUploadingPhoto = false) }
                    onPhotoUploaded(url)
                }
                .onFailure { error: Throwable ->
                    mutableState.update { state: StudentsUiState -> state.copy(isUploadingPhoto = false, photoUploadError = "Échec de l'upload: ${error.message}") }
                }
                }
        }

    fun showTransferDialog(studentIds: Set<String>) {
        mutableState.update {
            it.copy(
                showTransferDialog = true,
                transferStudentIds = studentIds
            )
        }
    }

    fun hideTransferDialog() {
        mutableState.update {
            it.copy(
                showTransferDialog = false,
                transferStudentIds = emptySet()
            )
        }
    }

    fun transferStudents(newClassroomId: String) {
        screenModelScope.launch {
            val studentIds = state.value.transferStudentIds.toList()
            if (studentIds.isEmpty()) return@launch

            println("DEBUG [StudentsScreenModel] Initiating transfer for students: $studentIds to class: $newClassroomId")
            val targetClassId = newClassroomId.toIntOrNull() ?: return@launch

            studentApiService.transferStudents(studentIds.map { it }, newClassroomId).onSuccess { result ->
                println("DEBUG [StudentsScreenModel] Transfer success: $result")
                loadStudents()
                mutableState.update {
                    it.copy(
                        showTransferDialog = false,
                        transferStudentIds = emptySet(),
                        selectedStudentIds = emptySet(),
                        selectionMode = false,
                        successMessage = "Transfert réussi: ${result["updatedCount"]} élève(s) transféré(s)"
                    )
                }
            }.onFailure { error ->
                println("DEBUG [StudentsScreenModel] Transfer failed: ${error.message}")
                mutableState.update {
                    it.copy(
                        errorMessage = "Erreur lors du transfert: ${error.message}"
                    )
                }
            }
        }
    }
}
