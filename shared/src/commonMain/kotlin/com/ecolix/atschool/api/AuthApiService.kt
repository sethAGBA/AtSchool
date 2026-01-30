package com.ecolix.atschool.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class AuthApiService(private val client: HttpClient) {
    suspend fun login(request: LoginRequest): Result<LoginResponse> = runCatching {
        val response = client.post("auth/login") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }

        if (response.status == HttpStatusCode.OK) {
            val loginResponse: LoginResponse = response.body()
            TokenProvider.token = loginResponse.token
            loginResponse
        } else {
            // User-friendly error messages
            val errorMessage = when(response.status) {
                HttpStatusCode.Unauthorized -> "Email ou mot de passe incorrect"
                HttpStatusCode.BadRequest -> "Données invalides"
                HttpStatusCode.Conflict -> "Cet utilisateur existe déjà"
                HttpStatusCode.InternalServerError -> "Erreur serveur, veuillez réessayer plus tard"
                else -> "Erreur de connexion (${response.status.value})"
            }
            throw Exception(errorMessage)
        }
    }

    suspend fun register(request: RegisterRequest): Result<Long> = runCatching {
        client.post("auth/register") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body<Map<String, Long>>()["userId"] ?: throw Exception("Registration failed")
    }

    fun logout() {
        TokenProvider.token = null
    }
}
