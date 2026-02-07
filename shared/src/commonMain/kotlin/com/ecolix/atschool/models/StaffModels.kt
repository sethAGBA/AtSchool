package com.ecolix.atschool.models

import kotlinx.serialization.Serializable

@Serializable
enum class StaffRole(val label: String) {
    PRINCIPAL("Proviseur"),
    DIRECTOR("Directeur"),
    CENSOR("Censeur"),
    TEACHER("Enseignant"),
    ADMIN("Administration"),
    SUPERVISOR("Surveillant"),
    SECRETARY("Secrétaire"),
    ACCOUNTANT("Comptable"),
    LIBRARIAN("Bibliothécaire"),
    NURSE("Infirmier(e)"),
    MAINTENANCE("Entretien"),
    SECURITY("Sécurité"),
    DRIVER("Chauffeur"),
    OTHER("Autre")
}

@Serializable
enum class StaffStatus(val label: String) {
    ACTIVE("Actif"),
    INACTIVE("Inactif"),
    ON_LEAVE("En congé")
}

@Serializable
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

    // Informations complémentaires
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
    val contractType: String? = "CDI",
    val baseSalary: Double? = null,
    val weeklyHours: Int = 0,
    val supervisor: String? = null,
    val retirementDate: String? = null
)

@Serializable
data class BulkStatusUpdate(
    val ids: List<String>,
    val status: String
)
