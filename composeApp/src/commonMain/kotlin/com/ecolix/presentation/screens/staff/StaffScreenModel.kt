package com.ecolix.presentation.screens.staff

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.ecolix.atschool.api.StaffApiService
import com.ecolix.atschool.models.Staff
import com.ecolix.atschool.models.StaffRole
import com.ecolix.data.models.StaffUiState
import com.ecolix.data.models.StaffViewMode
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class StaffScreenModel(
    private val staffApiService: StaffApiService
) : StateScreenModel<StaffUiState>(StaffUiState()) {

    init {
        screenModelScope.launch {
            loadStaff()
        }
    }

    suspend fun loadStaff() {
        staffApiService.getAllStaff().onSuccess { staff ->
            mutableState.update { 
                // Extract unique departments from staff and merge with existing list
                val distinctDepartments = staff.map { it.department }
                    .filter { it.isNotEmpty() }
                    .distinct()
                
                // Combine with default/existing departments, remove duplicates, sort
                val updatedDepartments = (it.departments + distinctDepartments)
                    .distinct()
                    .filter { dept -> dept != "Toutes" } // Remove "Toutes" to re-add it at start if needed, but usually UI handles "Toutes"
                    .sorted()
                
                // Ensure "Toutes" is first if used in filters, but for management list we might want just the list
                val finalDepartments = listOf("Toutes") + updatedDepartments

                it.copy(
                    staffMembers = staff,
                    departments = finalDepartments
                ) 
            }
        }.onFailure { error ->
            println("DEBUG: Failed to load staff: ${error.message}")
            error.printStackTrace()
            mutableState.update { it.copy(errorMessage = getFriendlyErrorMessage(error)) }
        }
    }

    fun addDepartment(name: String) {
        val trimmedName = name.trim()
        if (trimmedName.isEmpty()) return

        mutableState.update { 
            if (it.departments.contains(trimmedName)) {
                it // Duplicate
            } else {
                val newDepts = (it.departments + trimmedName).distinct().sortedWith { a, b ->
                    if (a == "Toutes") -1 else if (b == "Toutes") 1 else a.compareTo(b)
                }
                it.copy(departments = newDepts)
            }
        }
    }

    fun deleteDepartment(name: String) {
        if (name == "Toutes") return // Cannot delete "Toutes"

        mutableState.update { 
            val newDepts = it.departments.filter { dept -> dept != name }
            it.copy(departments = newDepts)
        }
    }

    fun saveStaff(staff: Staff) {
        screenModelScope.launch {
            val result = if (staff.id.isEmpty()) {
                staffApiService.addStaff(staff)
            } else {
                staffApiService.updateStaff(staff.id, staff)
            }
            
            result.onSuccess {
                // Wait for staff to load before resetting filters
                loadStaff()
                // Also ensure the department of the saved staff is in the list
                addDepartment(staff.department)

                // Reset filters and counter to show the newly added/updated staff
                mutableState.update { 
                    it.copy(
                        successMessage = "Personnel enregistré avec succès",
                        searchQuery = "",
                        roleFilter = null,
                        departmentFilter = null,
                        selectedGender = null,
                        statusFilter = null,
                        loadedCount = it.batchSize
                    ) 
                }
                onViewModeChange(StaffViewMode.LIST)
            }.onFailure { error ->
                println("DEBUG: Failed to save staff: ${error.message}")
                error.printStackTrace()
                mutableState.update { it.copy(errorMessage = getFriendlyErrorMessage(error)) }
            }
        }
    }

    fun deleteStaff(id: String) {
        screenModelScope.launch {
            staffApiService.deleteStaff(id).onSuccess {
                loadStaff()
                mutableState.update { it.copy(successMessage = "Membre du personnel supprimé") }
                onViewModeChange(StaffViewMode.LIST)
            }.onFailure { error ->
                println("DEBUG: Failed to delete staff: ${error.message}")
                error.printStackTrace()
                mutableState.update { it.copy(errorMessage = getFriendlyErrorMessage(error)) }
            }
        }
    }

    fun deleteSelectedStaff() {
        screenModelScope.launch {
            val idsToDelete = mutableState.value.selectedStaffIds.toList()
            if (idsToDelete.isEmpty()) return@launch

            staffApiService.deleteStaffBatch(idsToDelete).onSuccess {
                loadStaff()
                mutableState.update { 
                    it.copy(
                        successMessage = "${idsToDelete.size} membre(s) supprimé(s)",
                        selectedStaffIds = emptySet(),
                        selectionMode = false
                    ) 
                }
            }.onFailure { error ->
                println("DEBUG: Failed to bulk delete staff: ${error.message}")
                error.printStackTrace()
                mutableState.update { it.copy(errorMessage = getFriendlyErrorMessage(error)) }
            }
        }
    }

    fun updateSelectedStaffStatus(status: String) {
        screenModelScope.launch {
            val idsToUpdate = mutableState.value.selectedStaffIds.toList()
            if (idsToUpdate.isEmpty()) return@launch

            staffApiService.updateStaffStatusBatch(idsToUpdate, status).onSuccess {
                loadStaff()
                mutableState.update {
                    it.copy(
                        successMessage = "Statut mis à jour pour ${idsToUpdate.size} membre(s)",
                        selectedStaffIds = emptySet(),
                        selectionMode = false
                    )
                }
            }.onFailure { error ->
                println("DEBUG: Failed to bulk update staff status: ${error.message}")
                error.printStackTrace()
                mutableState.update { it.copy(errorMessage = getFriendlyErrorMessage(error)) }
            }
        }
    }

    private fun getFriendlyErrorMessage(error: Throwable): String {
        return when {
            error.message?.contains("404") == true -> "Service indisponible (404). Veuillez vérifier si le serveur est à jour."
            error.message?.contains("NoTransformationFoundException") == true -> "Erreur de format de réponse serveur (404/500)."
            error.message?.contains("ConnectException") == true -> "Serveur inaccessible. Vérifiez votre connexion."
            error.message?.contains("Timeout") == true -> "Le serveur ne répond pas."
            else -> "Une erreur est survenue. Consultez les logs pour plus de détails."
        }
    }

    fun refreshStaff() {
        screenModelScope.launch {
            loadStaff()
            // Reset filters when refreshing
            mutableState.update { 
                it.copy(
                    searchQuery = "",
                    roleFilter = null,
                    departmentFilter = null,
                    selectedGender = null,
                    statusFilter = null,
                    loadedCount = it.batchSize
                ) 
            }
        }
    }

    fun clearError() {
        mutableState.update { it.copy(errorMessage = null) }
    }

    fun clearSuccess() {
        mutableState.update { it.copy(successMessage = null) }
    }

    fun onDarkModeChange(isDark: Boolean) {
        mutableState.update { it.copy(isDarkMode = isDark) }
    }

    fun onSearchQueryChange(query: String) {
        mutableState.update { 
            it.copy(
                searchQuery = query,
                loadedCount = it.batchSize
            ) 
        }
    }

    fun onRoleFilterChange(role: StaffRole?) {
        mutableState.update { 
            it.copy(
                roleFilter = role,
                loadedCount = it.batchSize
            ) 
        }
    }

    fun onDepartmentFilterChange(dept: String?) {
        mutableState.update { 
            val finalDept = if (dept == "Toutes") null else dept
            it.copy(
                departmentFilter = finalDept,
                loadedCount = it.batchSize
            ) 
        }
    }

    fun onGenderFilterChange(gender: String?) {
        mutableState.update { 
            it.copy(
                selectedGender = if (gender == "Tous") null else gender,
                loadedCount = it.batchSize
            ) 
        }
    }

    fun onStatusFilterChange(status: String?) {
        mutableState.update { 
            it.copy(
                statusFilter = if (status == "Tous") null else status,
                loadedCount = it.batchSize
            ) 
        }
    }

    fun onToggleSelectionMode() {
        mutableState.update { 
            it.copy(
                selectionMode = !it.selectionMode,
                selectedStaffIds = emptySet()
            ) 
        }
    }

    fun onToggleStaffSelection(staffId: String) {
        mutableState.update { 
            val newSelection = it.selectedStaffIds.toMutableSet()
            if (newSelection.contains(staffId)) newSelection.remove(staffId)
            else newSelection.add(staffId)
            it.copy(selectedStaffIds = newSelection)
        }
    }

    fun onClearSelection() {
        mutableState.update { it.copy(selectedStaffIds = emptySet()) }
    }

    fun loadTrash() {
        screenModelScope.launch {
            staffApiService.getDeletedStaff().onSuccess { staff ->
                mutableState.update { 
                    it.copy(
                        staffMembers = staff,
                        loadedCount = it.batchSize,
                        selectionMode = false,
                        selectedStaffIds = emptySet()
                    ) 
                }
            }.onFailure { error ->
                println("DEBUG: Failed to load trash: ${error.message}")
                error.printStackTrace()
                mutableState.update { it.copy(errorMessage = getFriendlyErrorMessage(error)) }
            }
        }
    }

    fun restoreStaff(id: String) {
        screenModelScope.launch {
            staffApiService.restoreStaff(id).onSuccess {
                loadTrash()
                mutableState.update { it.copy(successMessage = "Membre du personnel restauré") }
            }.onFailure { error ->
                println("DEBUG: Failed to restore staff: ${error.message}")
                error.printStackTrace()
                mutableState.update { it.copy(errorMessage = getFriendlyErrorMessage(error)) }
            }
        }
    }

    fun restoreSelectedStaff() {
        screenModelScope.launch {
            val idsToRestore = mutableState.value.selectedStaffIds.toList()
            if (idsToRestore.isEmpty()) return@launch

            staffApiService.restoreStaffBatch(idsToRestore).onSuccess {
                loadTrash()
                mutableState.update { 
                    it.copy(
                        successMessage = "${idsToRestore.size} membre(s) restauré(s)",
                        selectedStaffIds = emptySet(),
                        selectionMode = false
                    ) 
                }
            }.onFailure { error ->
                println("DEBUG: Failed to restore staff batch: ${error.message}")
                error.printStackTrace()
                mutableState.update { it.copy(errorMessage = getFriendlyErrorMessage(error)) }
            }
        }
    }

    fun permanentDeleteStaff(id: String) {
        screenModelScope.launch {
            staffApiService.permanentDeleteStaff(id).onSuccess {
                loadTrash()
                mutableState.update { it.copy(successMessage = "Membre du personnel supprimé définitivement") }
            }.onFailure { error ->
                println("DEBUG: Failed to permanently delete staff: ${error.message}")
                error.printStackTrace()
                mutableState.update { it.copy(errorMessage = getFriendlyErrorMessage(error)) }
            }
        }
    }

    fun onViewModeChange(mode: StaffViewMode) {
        mutableState.update { 
            it.copy(
                viewMode = mode,
                selectedStaffId = if (mode == StaffViewMode.LIST || mode == StaffViewMode.GRID || mode == StaffViewMode.TRASH) null else it.selectedStaffId,
                selectionMode = if (mode == StaffViewMode.PROFILE || mode == StaffViewMode.FORM) false else it.selectionMode,
                // Clear selection when switching modes
                selectedStaffIds = emptySet()
            ) 
        }
        if (mode == StaffViewMode.TRASH) {
            loadTrash()
        } else if (mode == StaffViewMode.LIST || mode == StaffViewMode.GRID) {
            screenModelScope.launch { loadStaff() }
        }
    }

    fun onSelectStaff(staffId: String?) {
        mutableState.update { 
            it.copy(
                selectedStaffId = staffId,
                viewMode = if (staffId != null) StaffViewMode.PROFILE else it.viewMode
            ) 
        }
    }

    fun loadMore() {
        mutableState.update { 
            it.copy(loadedCount = it.loadedCount + it.batchSize)
        }
    }

    fun updateState(newState: StaffUiState) {
        mutableState.update { newState }
    }
}
