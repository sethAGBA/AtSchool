package com.ecolix.atschool.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

class DashboardApiService(private val client: HttpClient) {
    suspend fun getStats(): Result<DashboardStatsResponse> = runCatching {
        client.get("dashboard/stats").body()
    }
}
