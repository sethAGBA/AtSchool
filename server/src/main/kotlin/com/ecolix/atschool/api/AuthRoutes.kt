package com.ecolix.atschool.api

import com.ecolix.atschool.data.User
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.koin.ktor.ext.getKoin

fun Route.authRoutes() {
    val authService by lazy { application.getKoin().get<AuthService>() }

    route("/auth") {
        post("/login") {
            try {
                // val bodyText = call.receiveText()
                // println("DEBUG LOGIN BODY: $bodyText")
                // val request = kotlinx.serialization.json.Json.decodeFromString<com.ecolix.atschool.api.LoginRequest>(bodyText)
                val request = call.receive<com.ecolix.atschool.api.LoginRequest>()
                
                if (request.schoolCode.isBlank()) {
                    call.respond(HttpStatusCode.BadRequest, "School code is required")
                    return@post
                }
                
                val loginResponse = authService.authenticate(request.email, request.password, request.schoolCode)
                
                if (loginResponse != null) {
                    call.respond(loginResponse)
                } else {
                    call.respond(HttpStatusCode.Unauthorized, "Invalid credentials")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                call.respond(HttpStatusCode.InternalServerError, "Login Error: ${e.message}")
            }
        }

        post("/register") {
            val request = call.receive<RegisterRequest>()
            
            try {
                val userId = authService.register(
                    User(
                        tenantId = request.tenantId,
                        email = request.email,
                        passwordHash = "", // Will be hashed in service
                        role = request.role,
                        nom = request.nom,
                        prenom = request.prenom
                    ),
                    request.password
                )
                call.respond(HttpStatusCode.Created, mapOf("userId" to userId))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.Conflict, "User already exists or registration failed")
            }
        }
    }
}
