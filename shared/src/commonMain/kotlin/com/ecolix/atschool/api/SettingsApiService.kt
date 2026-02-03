package com.ecolix.atschool.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class SettingsApiService(private val client: HttpClient) {
    
    suspend fun getSettings(): Result<EstablishmentSettingsDto> = runCatching {
        val response = client.get("api/settings")
        if (response.status != HttpStatusCode.OK) {
            val errorBody = response.body<Map<String, String>>()
            throw Exception(errorBody["error"] ?: "Erreur inconnue (${response.status.value})")
        }
        response.body()
    }
    
    suspend fun createSettings(settings: EstablishmentSettingsDto): Result<Map<String, Any>> = runCatching {
        val response = client.post("api/settings") {
            contentType(ContentType.Application.Json)
            setBody(settings)
        }
        if (response.status != HttpStatusCode.Created && response.status != HttpStatusCode.OK) {
             val errorBody = try {
                response.body<Map<String, String>>()
            } catch (e: Exception) {
                mapOf("error" to "Erreur de communication (${response.status.value})")
            }
            throw Exception(errorBody["error"] ?: "Erreur lors de la création")
        }
        response.body()
    }
    
    suspend fun updateSettings(settings: EstablishmentSettingsDto): Result<Map<String, String>> = runCatching {
        val response = client.put("api/settings") {
            contentType(ContentType.Application.Json)
            setBody(settings)
        }
        if (response.status != HttpStatusCode.OK) {
             val errorBody = try {
                response.body<Map<String, String>>()
            } catch (e: Exception) {
                mapOf("error" to "Erreur de communication (${response.status.value})")
            }
            throw Exception(errorBody["error"] ?: "Erreur de sauvegarde")
        }
        response.body()
    }
}
