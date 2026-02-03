package com.ecolix.atschool.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

class DashboardApiService(private val client: HttpClient) {
    suspend fun getStats(): Result<DashboardStatsResponse> = runCatching {
        val response = client.get("dashboard/stats")
        if (response.status == io.ktor.http.HttpStatusCode.Unauthorized) {
            throw Exception("Unauthorized: Please login again")
        }
        response.body()
    }
}
