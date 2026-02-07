package com.ecolix.data.models

import com.ecolix.atschool.models.Staff
import com.ecolix.atschool.models.StaffRole

import androidx.compose.runtime.Immutable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Groups
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

val StaffRole.icon: ImageVector
    get() = when (this) {
        StaffRole.PRINCIPAL -> Icons.Filled.Person
        StaffRole.DIRECTOR -> Icons.Filled.Person
        StaffRole.CENSOR -> Icons.Filled.Gavel
        StaffRole.TEACHER -> Icons.Filled.School
        StaffRole.ADMIN -> Icons.Filled.AdminPanelSettings
        StaffRole.SUPERVISOR -> Icons.Filled.RemoveRedEye
        StaffRole.SECRETARY -> Icons.Filled.Description
        StaffRole.ACCOUNTANT -> Icons.Filled.Payments
        StaffRole.LIBRARIAN -> Icons.AutoMirrored.Filled.MenuBook
        StaffRole.NURSE -> Icons.Filled.MedicalServices
        StaffRole.MAINTENANCE -> Icons.Filled.CleaningServices
        StaffRole.SECURITY -> Icons.Filled.Security
        StaffRole.DRIVER -> Icons.Filled.DirectionsBus
        StaffRole.OTHER -> Icons.Filled.Groups
    }

val StaffRole.color: Color
    get() = when (this) {
        StaffRole.PRINCIPAL -> Color(0xFF1E293B)
        StaffRole.DIRECTOR -> Color(0xFF475569)
        StaffRole.CENSOR -> Color(0xFF64748B)
        StaffRole.TEACHER -> Color(0xFF6366F1)
        StaffRole.ADMIN -> Color(0xFF10B981)
        StaffRole.SUPERVISOR -> Color(0xFFF59E0B)
        StaffRole.SECRETARY -> Color(0xFFF43F5E)
        StaffRole.ACCOUNTANT -> Color(0xFF06B6D4)
        StaffRole.LIBRARIAN -> Color(0xFF8B5CF6)
        StaffRole.NURSE -> Color(0xFFEF4444)
        StaffRole.MAINTENANCE -> Color(0xFF94A3B8)
        StaffRole.SECURITY -> Color(0xFF71717A)
        StaffRole.DRIVER -> Color(0xFF0EA5E9)
        StaffRole.OTHER -> Color(0xFF94A3B8)
    }

@Immutable
data class StaffUiState(
    val staffMembers: List<Staff> = emptyList(),
    val viewMode: StaffViewMode = StaffViewMode.LIST,
    val selectedStaffId: String? = null,
    val selectedStaffIds: Set<String> = emptySet(),
    val selectionMode: Boolean = false,
    val searchQuery: String = "",
    val roleFilter: StaffRole? = null,
    val departmentFilter: String? = null,
    val selectedGender: String? = null,
    val statusFilter: String? = null,
    val isDarkMode: Boolean = false,
    val loadedCount: Int = 20,
    val batchSize: Int = 20,
    val successMessage: String? = null,
    val errorMessage: String? = null,
    val departments: List<String> = listOf("Toutes", "Administration", "Vie Scolaire", "Secrétariat", "Comptabilité", "Scientifique", "Littéraire", "Sport", "Arts", "Service Technique")
) {
    val colors: DashboardColors
        get() = if (isDarkMode) DashboardColors.dark() else DashboardColors.light()

    val roleDistribution: Map<StaffRole, Int>
        get() = staffMembers.groupBy { it.role }.mapValues { it.value.size }
}

enum class StaffViewMode {
    LIST,
    GRID,
    PROFILE,
    FORM,
    MANAGEMENT,
    TRASH
}
