package com.ecolix.atschool.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*

class UploadApiService(private val client: HttpClient) {
    
    suspend fun uploadFile(fileName: String, fileBytes: ByteArray): Result<String> = runCatching {
        val response = client.submitFormWithBinaryData(
            url = "api/upload",
            formData = formData {
                append("file", fileBytes, Headers.build {
                    append(HttpHeaders.ContentDisposition, "filename=\"$fileName\"")
                })
            }
        )
        
        if (response.status != HttpStatusCode.OK) {
            throw Exception("Upload failed with status ${response.status}")
        }
        
        val body = response.body<Map<String, String>>()
        body["url"] ?: throw Exception("Upload successful but no URL returned")
    }
}
