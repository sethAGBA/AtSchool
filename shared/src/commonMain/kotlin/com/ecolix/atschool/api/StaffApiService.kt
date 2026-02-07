package com.ecolix.atschool.api

import com.ecolix.atschool.models.Staff
import com.ecolix.atschool.models.BulkStatusUpdate
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class StaffApiService(private val client: HttpClient) {
    suspend fun getAllStaff(): Result<List<Staff>> = runCatching {
        client.get("staff").body()
    }

    suspend fun getStaffById(id: String): Result<Staff> = runCatching {
        client.get("staff/$id").body()
    }

    suspend fun addStaff(staff: Staff): Result<Int> = runCatching {
        val response: Map<String, Int> = client.post("staff") {
            setBody(staff)
            contentType(ContentType.Application.Json)
        }.body()
        response["id"]!!
    }

    suspend fun updateStaff(id: String, staff: Staff): Result<Unit> = runCatching {
        client.put("staff/$id") {
            setBody(staff)
            contentType(ContentType.Application.Json)
        }.body()
    }

    suspend fun deleteStaff(id: String): Result<Unit> = runCatching {
        client.delete("staff/$id").body()
    }

    suspend fun restoreStaffBatch(ids: List<String>): Result<Unit> = runCatching {
        client.post("staff/bulk-restore") {
            setBody(ids)
            contentType(ContentType.Application.Json)
        }.body()
    }

    suspend fun getDeletedStaff(): Result<List<Staff>> = runCatching {
        client.get("staff/trash").body()
    }

    suspend fun restoreStaff(id: String): Result<Unit> = runCatching {
        client.post("staff/$id/restore").body()
    }

    suspend fun permanentDeleteStaff(id: String): Result<Unit> = runCatching {
        client.delete("staff/$id/permanent").body()
    }

    suspend fun updateStaffStatusBatch(ids: List<String>, status: String): Result<Unit> = runCatching {
        client.post("staff/bulk-status") {
            contentType(ContentType.Application.Json)
            setBody(BulkStatusUpdate(ids, status))
        }.body()
    }

    suspend fun deleteStaffBatch(ids: List<String>): Result<Unit> = runCatching {
        client.post("staff/bulk-delete") {
            setBody(ids)
            contentType(ContentType.Application.Json)
        }.body()
    }
}
