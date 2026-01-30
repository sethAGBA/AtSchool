package com.ecolix.atschool.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class StudentApiService(private val client: HttpClient) {
    suspend fun getStudents(): Result<List<StudentResponse>> = runCatching {
        client.get("students").body()
    }

    suspend fun getStudent(id: Long): Result<StudentResponse> = runCatching {
        client.get("students/$id").body()
    }

    suspend fun addStudent(student: StudentResponse): Result<Long> = runCatching {
        val response: Map<String, Long> = client.post("students") {
            setBody(student)
            contentType(io.ktor.http.ContentType.Application.Json)
        }.body()
        response["id"]!!
    }

    suspend fun updateStudent(id: Long, student: StudentResponse): Result<Unit> = runCatching {
        client.put("students/$id") {
            setBody(student)
            contentType(io.ktor.http.ContentType.Application.Json)
        }.body()
    }

    suspend fun deleteStudent(id: Long): Result<Unit> = runCatching {
        client.delete("students/$id").body()
    }
}
