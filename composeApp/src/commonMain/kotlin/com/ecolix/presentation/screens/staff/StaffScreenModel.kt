package com.ecolix.presentation.screens.staff

import cafe.adriel.voyager.core.model.StateScreenModel
import com.ecolix.data.models.StaffRole
import com.ecolix.data.models.StaffUiState
import com.ecolix.data.models.StaffViewMode
import kotlinx.coroutines.flow.update

class StaffScreenModel : StateScreenModel<StaffUiState>(StaffUiState.sample(false)) {

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

    fun onViewModeChange(mode: StaffViewMode) {
        mutableState.update { 
            it.copy(
                viewMode = mode,
                selectedStaffId = if (mode == StaffViewMode.LIST || mode == StaffViewMode.GRID) null else it.selectedStaffId,
                selectionMode = if (mode == StaffViewMode.PROFILE || mode == StaffViewMode.FORM) false else it.selectionMode
            ) 
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
