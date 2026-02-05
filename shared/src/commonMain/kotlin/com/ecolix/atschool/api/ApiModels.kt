package com.ecolix.atschool.api

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(val email: String, val password: String, val schoolCode: String = "")

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
data class SubscriptionPlanDto(
    val id: Int,
    val name: String,
    val price: Double,
    val currency: String,
    val description: String,
    val isPopular: Boolean,
    val createdAt: String
)

@Serializable
data class CreatePlanRequest(
    val name: String,
    val price: Double,
    val description: String,
    val isPopular: Boolean = false
)

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

// Establishment Settings DTOs
@Serializable
data class EstablishmentSettingsDto(
    val id: Int? = null,
    val tenantId: Int,
    
    // Identité
    val schoolName: String,
    val schoolCode: String,
    val schoolSlogan: String? = null,
    val schoolLevel: String = "Primaire",
    val logoUrl: String? = null,
    val republicLogoUrl: String? = null,
    
    // Tutelle
    val ministry: String? = null,
    val republicName: String? = null,
    val republicMotto: String? = null,
    val educationDirection: String? = null,
    val inspection: String? = null,
    
    // Direction
    val genCivility: String = "M.",
    val genDirector: String? = null,
    val matCivility: String = "Mme",
    val matDirector: String? = null,
    val priCivility: String = "M.",
    val priDirector: String? = null,
    val colCivility: String = "M.",
    val colDirector: String? = null,
    val lycCivility: String = "M.",
    val lycDirector: String? = null,
    val uniCivility: String = "Pr",
    val uniDirector: String? = null,
    val supCivility: String = "Dr",
    val supDirector: String? = null,
    
    // Contact
    val phone: String? = null,
    val email: String? = null,
    val website: String? = null,
    val bp: String? = null,
    val address: String? = null,
    
    // Configuration
    val pdfFooter: String? = null,
    val useTrimesters: Boolean = true,
    val useSemesters: Boolean = false,
    
    // Système
    val autoBackup: Boolean = true,
    val backupFrequency: String = "Quotidienne",
    val retentionDays: Int = 30,
    
    val updatedAt: String? = null
)

// Academic & Structure DTOs
@Serializable
data class SchoolYearDto(
    val id: Int? = null,
    val tenantId: Int,
    val libelle: String,
    val dateDebut: String,
    val dateFin: String,
    val status: String = "UPCOMING",
    val numberOfPeriods: Int = 3,
    val periodType: String = "TRIMESTER",
    val isDefault: Boolean = false,
    val description: String? = null,
    val periods: List<AcademicPeriodDto>? = null
)

@Serializable
data class AcademicPeriodDto(
    val id: Int? = null,
    val tenantId: Int,
    val anneeScolaireId: Int,
    val nom: String,
    val numero: Int,
    val dateDebut: String,
    val dateFin: String,
    val periodType: String = "TRIMESTER",
    val evaluationDeadline: String? = null,
    val reportCardDeadline: String? = null,
    val status: String = "UPCOMING"
)

@Serializable
data class CycleDto(
    val id: Int? = null,
    val tenantId: Int,
    val nom: String
)

@Serializable
data class LevelDto(
    val id: Int? = null,
    val cycleId: Int,
    val nom: String
)

@Serializable
data class ClassDto(
    val id: Int? = null,
    val tenantId: Int,
    val niveauId: Int,
    val code: String,
    val nom: String
)
