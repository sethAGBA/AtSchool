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
    val subscriptionExpiresAt: String? = null,
    val createdAt: String,
    val isActive: Boolean
)

@Serializable
data class AnnouncementDto(
    val id: Int,
    val content: String,
    val targetRole: String? = null,
    val expiresAt: String? = null,
    val createdAt: String,
    val isActive: Boolean
)

@Serializable
data class CreateAnnouncementRequest(
    val content: String,
    val targetRole: String? = null,
    val expiresAt: String? = null
)

@Serializable
data class AuditLogDto(
    val id: Long,
    val actorEmail: String,
    val action: String,
    val details: String? = null,
    val timestamp: String
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
data class UpdateSubscriptionRequest(val expiresAt: String?)

@Serializable
data class UpdateTenantStatusRequest(
    val isActive: Boolean
)

@Serializable
data class ResetPasswordRequest(
    val newPassword: String
)

// Payment DTOs
@Serializable
data class SubscriptionPaymentDto(
    val id: Long,
    val tenantId: Int,
    val tenantName: String,
    val amount: Double,
    val currency: String,
    val paymentDate: String,
    val paymentMethod: String,
    val status: String,
    val invoiceNumber: String?,
    val notes: String?,
    val createdAt: String
)

@Serializable
data class CreatePaymentRequest(
    val tenantId: Int,
    val amount: Double,
    val paymentMethod: String,
    val notes: String? = null
)

@Serializable
data class UpdatePaymentStatusRequest(
    val status: String,
    val invoiceNumber: String? = null
)

// Notification DTOs
@Serializable
data class NotificationDto(
    val id: Long,
    val tenantId: Int?,
    val userId: Long?,
    val title: String,
    val message: String,
    val type: String,
    val priority: String,
    val isRead: Boolean,
    val createdAt: String,
    val expiresAt: String?
)

@Serializable
data class CreateNotificationRequest(
    val tenantId: Int? = null,
    val userId: Long? = null,
    val title: String,
    val message: String,
    val type: String = "INFO",
    val priority: String = "NORMAL",
    val expiresAt: String? = null
)

// Support Ticket DTOs
@Serializable
data class SupportTicketDto(
    val id: Long,
    val tenantId: Int,
    val tenantName: String,
    val userId: Long,
    val userEmail: String,
    val subject: String,
    val description: String,
    val status: String,
    val priority: String,
    val createdAt: String,
    val updatedAt: String,
    val resolvedAt: String?,
    val assignedTo: Long?
)

@Serializable
data class CreateTicketRequest(
    val subject: String,
    val description: String,
    val priority: String = "NORMAL"
)

@Serializable
data class UpdateTicketRequest(
    val status: String? = null,
    val assignedTo: Long? = null
)

// Permission DTOs
@Serializable
data class AdminPermissionDto(
    val id: Long,
    val userId: Long,
    val userEmail: String,
    val permission: String,
    val grantedAt: String,
    val grantedBy: Long
)

@Serializable
data class GrantPermissionRequest(
    val userId: Long,
    val permission: String
)

// Analytics DTOs
@Serializable
data class GrowthMetricsDto(
    val startDate: String,
    val endDate: String,
    val newSchools: Int,
    val newStudents: Int,
    val totalRevenue: Double,
    val dataPoints: List<GrowthDataPoint>
)

@Serializable
data class GrowthDataPoint(
    val date: String,
    val schools: Int,
    val students: Int,
    val revenue: Double
)

@Serializable
data class RevenueDataPoint(
    val period: String,
    val amount: Double,
    val schoolCount: Int
)

@Serializable
data class SchoolActivityDto(
    val tenantId: Int,
    val tenantName: String,
    val lastLoginDate: String?,
    val activeUsers: Int,
    val totalStudents: Int,
    val activityScore: Double // 0-100
)
