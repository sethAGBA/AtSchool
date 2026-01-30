package com.ecolix.atschool.api

import com.ecolix.atschool.data.User
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.koin.ktor.ext.inject

@Serializable
data class LoginRequest(val email: String, val password: String)

@Serializable
data class RegisterRequest(
    val email: String, 
    val password: String, 
    val tenantId: Int, 
    val role: String,
    val nom: String? = null,
    val prenom: String? = null
)

fun Route.authRoutes() {
    val authService by inject<AuthService>()

    route("/auth") {
        post("/login") {
            val request = call.receive<LoginRequest>()
            val token = authService.authenticate(request.email, request.password)
            
            if (token != null) {
                call.respond(mapOf("token" to token))
            } else {
                call.respond(HttpStatusCode.Unauthorized, "Invalid credentials")
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
