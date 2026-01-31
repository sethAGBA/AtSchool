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
}
