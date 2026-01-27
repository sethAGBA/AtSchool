package com.ecolix.data.models

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material.icons.filled.Groups
import androidx.compose.ui.graphics.vector.ImageVector

@Immutable
data class User(
    val id: String,
    val username: String,
    val email: String,
    val role: UserRole,
    val status: String = "Actif", // Actif, Inactif, Suspendu
    val lastLogin: String? = null,
    val linkedStaffId: String? = null,
    val linkedStudentIds: List<String> = emptyList(),
    val linkedParentId: String? = null,
    val avatarUrl: String? = null,
    val fullName: String = username
)

enum class UserRole(val label: String, val icon: ImageVector, val color: Color) {
    ADMIN("Administrateur", Icons.Filled.AdminPanelSettings, Color(0xFFEF4444)),
    MANAGER("Gestionnaire", Icons.Filled.SupervisorAccount, Color(0xFFF59E0B)),
    TEACHER("Enseignant", Icons.Filled.School, Color(0xFF6366F1)),
    STUDENT("Élève", Icons.Filled.Person, Color(0xFF10B981)),
    PARENT("Parent", Icons.Filled.Groups, Color(0xFF8B5CF6))
}

enum class UsersViewMode(val label: String) {
    ADMINS("Administrateurs"),
    TEACHERS("Enseignants"),
    PARENTS("Parents")
}

@Immutable
data class AppsUiState(
    val viewMode: UsersViewMode = UsersViewMode.ADMINS,
    val users: List<User> = emptyList(),
    val isLoading: Boolean = false,
    val selectedUserId: String? = null,
    val searchQuery: String = "",
    val roleFilter: UserRole? = null,
    val statusFilter: String? = null, // "active", "inactive", "all"
    val showUserDialog: Boolean = false,
    val userToDelete: User? = null,
    val isEditing: Boolean = false,
    val isDarkMode: Boolean = false,
    val loadedUsersCount: Int = 10,
    val parents: List<Parent> = emptyList() // We might want to separate this, but for now referencing Parent here
) {
    val colors: DashboardColors
        get() = if (isDarkMode) DashboardColors.dark() else DashboardColors.light()
        
    companion object {
        fun sample(isDarkMode: Boolean): AppsUiState {
            return AppsUiState(
                isDarkMode = isDarkMode,
                users = listOf(
                    User("1", "admin", "admin@ecolix.com", UserRole.ADMIN, "Actif", "26/01/2026 14:30", fullName = "Seth Kouamé"),
                    User("2", "secrétaire", "secretariat@ecolix.com", UserRole.MANAGER, "Actif", "26/01/2026 08:15", fullName = "Sessime Gado"),
                    User("3", "koffi.j", "jean.koffi@ecolix.com", UserRole.TEACHER, "Actif", "25/01/2026 16:45", fullName = "Jean Koffi"),
                    User("4", "parent.demo", "parent@gmail.com", UserRole.PARENT, "Actif", "20/01/2026 19:20", fullName = "M. Touré"),
                    User("5", "eleve.demo", "eleve@ecolix.com", UserRole.STUDENT, "Inactif", null, fullName = "Kouassi Aya")
                )
            )
        }
    }
}
