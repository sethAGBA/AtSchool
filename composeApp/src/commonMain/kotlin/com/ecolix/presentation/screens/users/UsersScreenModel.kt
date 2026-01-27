package com.ecolix.presentation.screens.users

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.ecolix.data.models.AppsUiState
import com.ecolix.data.models.User
import com.ecolix.data.models.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UsersScreenModel : ScreenModel {
    private val _state = MutableStateFlow(AppsUiState.sample(false)) // Default to light mode for now, should be injected
    val state: StateFlow<AppsUiState> = _state.asStateFlow()

    fun onDarkModeChange(isDarkMode: Boolean) {
        _state.value = _state.value.copy(
            isDarkMode = isDarkMode
        )
        // Ideally reload sample with correct mode or just update mode if sample is dynamic
        // For now, we just update the flag which triggers color re-computation
    }
    
    fun onSearchQueryChange(query: String) {
        _state.value = _state.value.copy(searchQuery = query, loadedUsersCount = 10)
    }

    fun onViewModeChange(mode: com.ecolix.data.models.UsersViewMode) {
        _state.value = _state.value.copy(viewMode = mode, loadedUsersCount = 10)
    }

    fun loadMoreUsers() {
        val currentCount = _state.value.loadedUsersCount
        _state.value = _state.value.copy(loadedUsersCount = currentCount + 10)
    }

    fun onRoleFilterChange(role: UserRole?) {
        _state.value = _state.value.copy(roleFilter = role)
    }

    fun onStatusFilterChange(status: String?) {
        _state.value = _state.value.copy(statusFilter = status)
    }
    
    fun onUserClick(userId: String) {
         _state.value = _state.value.copy(selectedUserId = userId, isEditing = true, showUserDialog = true)
    }

    fun onAddUserClick() {
        _state.value = _state.value.copy(selectedUserId = null, isEditing = false, showUserDialog = true)
    }

    fun onDismissDialog() {
        _state.value = _state.value.copy(showUserDialog = false, selectedUserId = null)
    }

    fun onDeleteUser(userId: String) {
        val user = _state.value.users.find { it.id == userId }
        _state.value = _state.value.copy(userToDelete = user)
    }

    fun confirmDeleteUser() {
        val userToDelete = _state.value.userToDelete ?: return
        val currentUsers = _state.value.users.toMutableList()
        currentUsers.removeAll { it.id == userToDelete.id }
        _state.value = _state.value.copy(users = currentUsers, userToDelete = null)
    }

    fun onDismissDeleteConfirmation() {
        _state.value = _state.value.copy(userToDelete = null)
    }
    
    fun onToggleUserStatus(userId: String) {
         val currentUsers = _state.value.users.toMutableList()
         val index = currentUsers.indexOfFirst { it.id == userId }
         if (index != -1) {
             val user = currentUsers[index]
             val newStatus = if (user.status == "Actif") "Inactif" else "Actif"
             currentUsers[index] = user.copy(status = newStatus)
             _state.value = _state.value.copy(users = currentUsers)
         }
    }

    fun onSaveUser(user: User) {
        val currentUsers = _state.value.users.toMutableList()
        val index = currentUsers.indexOfFirst { it.id == user.id }
        if (index != -1) {
            currentUsers[index] = user
        } else {
            currentUsers.add(0, user)
        }
        _state.value = _state.value.copy(users = currentUsers, showUserDialog = false, selectedUserId = null)
    }
}
