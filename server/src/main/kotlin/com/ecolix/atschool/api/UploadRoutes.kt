package com.ecolix.atschool.api

import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File
import java.util.*

fun Route.uploadRoutes() {
    authenticate("auth-jwt") {
        post("/api/upload") {
            val multipart = call.receiveMultipart()
            var fileUrl = ""

            multipart.forEachPart { part ->
                if (part is PartData.FileItem) {
                    val name = part.originalFileName ?: "upload"
                    val extension = name.substringAfterLast(".", "png")
                    val fileName = "${UUID.randomUUID()}.$extension"
                    
                    val uploadDir = File("uploads")
                    if (!uploadDir.exists()) {
                        uploadDir.mkdirs()
                    }
                    
                    val file = File(uploadDir, fileName)
                    part.streamProvider().use { input ->
                        file.outputStream().buffered().use { output ->
                            input.copyTo(output)
                        }
                    }
                    fileUrl = "/uploads/$fileName"
                }
                part.dispose()
            }

            if (fileUrl.isNotEmpty()) {
                call.respond(mapOf("url" to fileUrl))
            } else {
                call.respond(io.ktor.http.HttpStatusCode.BadRequest, mapOf("error" to "No file uploaded"))
            }
        }
    }
}
