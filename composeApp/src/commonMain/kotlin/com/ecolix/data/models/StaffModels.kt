package com.ecolix.data.models

import androidx.compose.runtime.Immutable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

@Immutable
data class Staff(
    val id: String,
    val firstName: String,
    val lastName: String,
    val role: StaffRole,
    val department: String,
    val email: String,
    val phone: String,
    val joinDate: String,
    val status: String = "Actif",
    val photoUrl: String? = null,
    val matricule: String? = null,
    val address: String? = null,
    val gender: String = "M",
    val specialty: String? = null,
    val assignedClasses: List<String> = emptyList(),
    val isDeleted: Boolean = false,
    
    // Nouveaux champs issus du modèle de référence
    val qualifications: String = "",
    val birthDate: String? = null,
    val birthPlace: String? = null,
    val nationality: String? = null,
    val idNumber: String? = null,
    val socialSecurityNumber: String? = null,
    val maritalStatus: String? = "Célibataire",
    val numberOfChildren: Int = 0,
    val region: String? = null,
    val highestDegree: String? = null,
    val experienceYears: Int = 0,
    val previousInstitution: String? = null,
    val contractType: String? = "CDI", // CDI, CDD, Vacataire
    val baseSalary: Double? = null,
    val weeklyHours: Int = 0,
    val supervisor: String? = null,
    val retirementDate: String? = null
)

enum class StaffRole(val label: String, val icon: ImageVector, val color: Color) {
    PRINCIPAL("Proviseur", Icons.Default.Person, Color(0xFF1E293B)),
    DIRECTOR("Directeur", Icons.Default.Person, Color(0xFF475569)),
    CENSOR("Censeur", Icons.Default.Gavel, Color(0xFF64748B)),
    TEACHER("Enseignant", Icons.Default.School, Color(0xFF6366F1)),
    ADMIN("Administration", Icons.Default.AdminPanelSettings, Color(0xFF10B981)),
    SUPERVISOR("Surveillant", Icons.Default.RemoveRedEye, Color(0xFFF59E0B)),
    SECRETARY("Secrétaire", Icons.Default.Description, Color(0xFFF43F5E)),
    ACCOUNTANT("Comptable", Icons.Default.Payments, Color(0xFF06B6D4)),
    LIBRARIAN("Bibliothécaire", Icons.Default.MenuBook, Color(0xFF8B5CF6)),
    NURSE("Infirmier(e)", Icons.Default.MedicalServices, Color(0xFFEF4444)),
    MAINTENANCE("Entretien", Icons.Default.CleaningServices, Color(0xFF94A3B8)),
    SECURITY("Sécurité", Icons.Default.Security, Color(0xFF71717A)),
    DRIVER("Chauffeur", Icons.Default.DirectionsBus, Color(0xFF0EA5E9)),
    OTHER("Autre", Icons.Default.Groups, Color(0xFF94A3B8))
}

@Immutable
data class StaffUiState(
    val staffMembers: List<Staff>,
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
    val loadedCount: Int = 10,
    val batchSize: Int = 10,
    val departments: List<String> = listOf("Toutes", "Administration", "Vie Scolaire", "Secrétariat", "Comptabilité", "Scientifique", "Littéraire", "Sport", "Arts", "Service Technique")
) {
    val colors: DashboardColors
        get() = if (isDarkMode) DashboardColors.dark() else DashboardColors.light()

    val roleDistribution: Map<StaffRole, Int>
        get() = staffMembers.groupBy { it.role }.mapValues { it.value.size }

    companion object {
        fun sample(isDarkMode: Boolean): StaffUiState {
            return StaffUiState(
                isDarkMode = isDarkMode,
                staffMembers = listOf(
                    Staff(
                        id = "P11", 
                        firstName = "Moussa", 
                        lastName = "Traoré", 
                        role = StaffRole.PRINCIPAL, 
                        department = "Administration", 
                        email = "moussa.t@ecolix.com", 
                        phone = "22 22 22 11", 
                        joinDate = "01/09/2018", 
                        matricule = "PROV-001", 
                        gender = "M", 
                        status = "Actif",
                        nationality = "Ivoirienne"
                    ),
                    Staff(
                        id = "P1", 
                        firstName = "Jean", 
                        lastName = "Koffi", 
                        role = StaffRole.DIRECTOR, 
                        department = "Administration", 
                        email = "jean.koffi@ecolix.com", 
                        phone = "22 22 22 01", 
                        joinDate = "01/09/2020", 
                        matricule = "DIR-001", 
                        gender = "M", 
                        status = "Actif",
                        birthDate = "12/05/1975",
                        nationality = "Ivoirienne",
                        maritalStatus = "Marié(e)",
                        contractType = "CDI",
                        baseSalary = 850000.0
                    ),
                    Staff(
                        id = "P8", 
                        firstName = "Blaise", 
                        lastName = "Agbéno", 
                        role = StaffRole.CENSOR, 
                        department = "Administration", 
                        email = "blaise.a@ecolix.com", 
                        phone = "22 22 22 08", 
                        joinDate = "05/09/2019", 
                        matricule = "CEN-001", 
                        gender = "M", 
                        status = "Actif",
                        qualifications = "Master en Gestion Éducative",
                        experienceYears = 12
                    ),
                    Staff(
                        id = "P2", 
                        firstName = "Marie", 
                        lastName = "Diallo", 
                        role = StaffRole.TEACHER, 
                        department = "Scientifique", 
                        email = "marie.diallo@ecolix.com", 
                        phone = "22 22 22 02", 
                        joinDate = "15/09/2021", 
                        specialty = "Mathématiques", 
                        assignedClasses = listOf("6ème A", "5ème B"), 
                        matricule = "ENS-001", 
                        gender = "F", 
                        status = "Actif",
                        highestDegree = "Doctorat en Mathématiques",
                        weeklyHours = 18
                    ),
                    Staff("P9", "Amavi", "Kpogli", StaffRole.SUPERVISOR, "Vie Scolaire", "amavi.k@ecolix.com", "22 22 22 09", "12/10/2022", matricule = "SUR-001", gender = "M", status = "Actif"),
                    Staff("P10", "Sessime", "Gado", StaffRole.SECRETARY, "Secrétariat", "sessime.g@ecolix.com", "22 22 22 10", "20/01/2023", matricule = "SEC-001", gender = "F", status = "Actif"),
                    Staff("P4", "Paul", "Konan", StaffRole.TEACHER, "Littéraire", "paul.konan@ecolix.com", "22 22 22 04", "12/09/2023", specialty = "Français", assignedClasses = listOf("6ème B", "4ème A"), matricule = "ENS-002", gender = "M", status = "En congé"),
                    Staff("P5", "Awa", "Coulibaly", StaffRole.ACCOUNTANT, "Administration", "awa.c@ecolix.com", "22 22 22 05", "05/01/2024", matricule = "CPT-001", gender = "F", status = "Actif")
                )
            )
        }
    }
}

enum class StaffViewMode {
    LIST,
    GRID,
    PROFILE,
    FORM
}
