package com.ecolix.atschool.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class AuthApiService(private val client: HttpClient) {
    suspend fun login(request: LoginRequest): Result<LoginResponse> = runCatching {
        val response: LoginResponse = client.post("auth/login") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
        TokenProvider.token = response.token
        response
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
