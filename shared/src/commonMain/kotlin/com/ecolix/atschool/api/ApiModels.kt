package com.ecolix.atschool.api

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(val email: String, val password: String, val schoolCode: String)

@Serializable
data class LoginResponse(val token: String, val role: String)

@Serializable
data class RegisterRequest(
    val email: String, 
    val password: String, 
    val tenantId: Int, 
    val role: String,
    val nom: String? = null,
    val prenom: String? = null
)

@Serializable
data class StudentResponse(
    val id: Long? = null,
    val tenantId: Int,
    val matricule: String,
    val nom: String,
    val prenom: String,
    val dateNaissance: String, // String for simplicity across platforms
    val sexe: String
)
@Serializable
data class DashboardStatsResponse(
    val totalStudents: Int,
    val totalStaff: Int,
    val totalClasses: Int,
    val totalRevenue: Double,
    val recentActivities: List<ActivityDto>
)

@Serializable
data class ActivityDto(
    val title: String,
    val subtitle: String,
    val time: String,
    val type: String
)

@Serializable
data class TenantDto(
    val id: Int,
    val name: String,
    val domain: String,
    val code: String,
    val adminEmail: String? = null,
    val contactEmail: String? = null,
    val contactPhone: String? = null,
    val address: String? = null,
    val createdAt: String,
    val isActive: Boolean
)

@Serializable
data class CreateTenantRequest(
    val name: String,
    val code: String,
    val adminEmail: String,
    val adminPassword: String,
    val contactEmail: String? = null,
    val contactPhone: String? = null,
    val address: String? = null
)

@Serializable
data class GlobalStatsResponse(
    val totalSchools: Int,
    val totalStudents: Int,
    val totalRevenue: Double
)

@Serializable
data class UpdateTenantStatusRequest(
    val isActive: Boolean
)

@Serializable
data class ResetPasswordRequest(
    val newPassword: String
)
