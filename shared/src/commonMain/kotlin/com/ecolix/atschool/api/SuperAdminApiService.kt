package com.ecolix.atschool.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class SuperAdminApiService(private val client: HttpClient) {

    suspend fun getTenants(): Result<List<TenantDto>> = runCatching {
        client.get("superadmin/tenants").body()
    }

    suspend fun createTenant(request: CreateTenantRequest): Result<Unit> = runCatching {
        val response = client.post("superadmin/tenants") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        if (response.status != HttpStatusCode.Created) {
            throw Exception("Erreur lors de la création de l'école")
        }
    }

    suspend fun getGlobalStats(): Result<GlobalStatsResponse> = runCatching {
        client.get("superadmin/stats").body()
    }

    suspend fun updateTenantStatus(id: Int, isActive: Boolean): Result<Unit> = runCatching {
        client.patch("superadmin/tenants/$id/status") {
            setBody(UpdateTenantStatusRequest(isActive))
            contentType(ContentType.Application.Json)
        }
    }

    suspend fun resetAdminPassword(tenantId: Int, newPassword: String): Result<Unit> = runCatching {
        client.post("superadmin/tenants/$tenantId/reset-password") {
            setBody(ResetPasswordRequest(newPassword))
            contentType(ContentType.Application.Json)
        }
    }

    suspend fun updateSubscription(id: Int, expiresAt: String?): Result<Unit> = runCatching {
        client.patch("superadmin/tenants/$id/subscription") {
            setBody(UpdateSubscriptionRequest(expiresAt))
            contentType(ContentType.Application.Json)
        }
    }

    suspend fun getAnnouncements(): Result<List<AnnouncementDto>> = runCatching {
        client.get("superadmin/announcements").body()
    }

    suspend fun createAnnouncement(request: CreateAnnouncementRequest): Result<Unit> = runCatching {
        client.post("superadmin/announcements") {
            setBody(request)
            contentType(ContentType.Application.Json)
        }
    }

    suspend fun getAuditLogs(): Result<List<AuditLogDto>> = runCatching {
        client.get("superadmin/logs").body()
    }

    suspend fun getPayments(): Result<List<SubscriptionPaymentDto>> = runCatching {
        client.get("superadmin/payments").body()
    }

    suspend fun getTickets(): Result<List<SupportTicketDto>> = runCatching {
        client.get("superadmin/tickets").body()
    }

    suspend fun getGrowthMetrics(startDate: String = "", endDate: String = ""): Result<GrowthMetricsDto> = runCatching {
        client.get("superadmin/analytics/growth") {
            parameter("startDate", startDate)
            parameter("endDate", endDate)
        }.body()
    }

    suspend fun createPayment(request: CreatePaymentRequest): Result<Unit> = runCatching {
        val response = client.post("superadmin/payments") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        if (response.status != HttpStatusCode.Created) {
            throw Exception("Erreur lors de l'enregistrement du paiement")
        }
    }

    suspend fun updatePaymentStatus(id: Long, status: String, invoiceNumber: String? = null): Result<Unit> = runCatching {
        client.patch("superadmin/payments/$id") {
            contentType(ContentType.Application.Json)
            setBody(UpdatePaymentStatusRequest(status, invoiceNumber))
        }
    }

    suspend fun sendNotification(request: CreateNotificationRequest): Result<Unit> = runCatching {
        val response = client.post("superadmin/notifications") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        if (response.status != HttpStatusCode.Created) {
            throw Exception("Erreur lors de l'envoi de la notification")
        }
    }

    suspend fun getPlans(): Result<List<SubscriptionPlanDto>> = runCatching {
        client.get("superadmin/plans").body()
    }

    suspend fun createPlan(request: CreatePlanRequest): Result<Unit> = runCatching {
        val response = client.post("superadmin/plans") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        if (response.status != HttpStatusCode.Created) {
            throw Exception("Erreur lors de la création du plan")
        }
    }

    suspend fun updatePlan(id: Int, request: CreatePlanRequest): Result<Unit> = runCatching {
        val response = client.patch("superadmin/plans/$id") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        if (response.status != HttpStatusCode.OK) {
            throw Exception("Erreur lors de la mise à jour du plan")
        }
    }

    suspend fun deletePlan(id: Int): Result<Unit> = runCatching {
        val response = client.delete("superadmin/plans/$id")
        if (response.status != HttpStatusCode.OK) {
            throw Exception("Erreur lors de la suppression du plan")
        }
    }

    suspend fun getSchoolActivity(): Result<List<SchoolActivityDto>> = runCatching {
        client.get("superadmin/analytics/activity").body()
    }
}
