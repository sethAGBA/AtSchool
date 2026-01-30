package com.ecolix.atschool.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

class StudentApiService(private val client: HttpClient) {
    suspend fun getStudents(): Result<List<StudentResponse>> = runCatching {
        client.get("students").body()
    }

    suspend fun getStudent(id: Long): Result<StudentResponse> = runCatching {
        client.get("students/$id").body()
    }
}
