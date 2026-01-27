package com.ecolix.data.models

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class Parent(
    val id: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val address: String? = null,
    val profession: String? = null,
    val childrenIds: List<String> = emptyList(), // IDs of linked students
    val userId: String? = null // Linked system user account if any
) {
    val fullName: String get() = "$firstName $lastName"
}

@Immutable
data class ParentsUiState(
    val parents: List<Parent> = emptyList(),
    val isLoading: Boolean = false,
    val selectedParentId: String? = null,
    val searchQuery: String = "",
    val showParentDialog: Boolean = false,
    val isEditing: Boolean = false,
    val isDarkMode: Boolean = false
) {
    val colors: DashboardColors
        get() = if (isDarkMode) DashboardColors.dark() else DashboardColors.light()
        
    companion object {
        fun sample(isDarkMode: Boolean): ParentsUiState {
            return ParentsUiState(
                isDarkMode = isDarkMode,
                parents = listOf(
                    Parent(
                        id = "PA1",
                        firstName = "Moussa",
                        lastName = "Touré",
                        email = "moussa.toure@email.com",
                        phone = "+228 90 11 22 33",
                        profession = "Commerçant",
                        address = "Lomé, Tokoin",
                        childrenIds = listOf("S1", "S3")
                    ),
                    Parent(
                        id = "PA2",
                        firstName = "Amina",
                        lastName = "Diop",
                        email = "amina.diop@email.com",
                        phone = "+228 91 44 55 66",
                        profession = "Médecin",
                        address = "Lomé, Agoè",
                        childrenIds = listOf("S2")
                    )
                )
            )
        }
    }
}
