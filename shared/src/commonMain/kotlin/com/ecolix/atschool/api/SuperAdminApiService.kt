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
}
