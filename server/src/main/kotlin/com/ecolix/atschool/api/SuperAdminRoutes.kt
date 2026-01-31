package com.ecolix.atschool.api

import com.ecolix.atschool.data.SuperAdminRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.superAdminRoutes() {
    val superAdminRepository by inject<SuperAdminRepository>()

    authenticate("auth-jwt") {
        route("/superadmin") {
            // Middleware to check if user is SUPER_ADMIN
            intercept(ApplicationCallPipeline.Call) {
                val principal = call.principal<JWTPrincipal>()
                val role = principal?.payload?.getClaim("role")?.asString()
                val email = principal?.payload?.getClaim("email")?.asString()
                
                // Add server-side logging to help debug
                if (principal == null) {
                    application.log.error("SuperAdmin Check: Principal is NULL despite Authentication Success log!")
                } else {
                    application.log.info("SuperAdmin Check: user=$email, role=$role")
                }

                if (role != "SUPER_ADMIN") {
                    application.log.warn("Forbidden Access Attempt: $email (role: $role)")
                    call.respond(HttpStatusCode.Forbidden, "Accès réservé au Super Administrateur")
                    finish()
                }
            }

            get("/tenants") {
                val tenants = superAdminRepository.listTenants()
                call.respond(tenants)
            }

            post("/tenants") {
                val request = call.receive<CreateTenantRequest>()
                try {
                    val id = superAdminRepository.createTenant(
                        name = request.name, 
                        code = request.code, 
                        adminEmail = request.adminEmail, 
                        password = request.adminPassword,
                        contactEmail = request.contactEmail,
                        contactPhone = request.contactPhone,
                        address = request.address
                    )
                    call.respond(HttpStatusCode.Created, mapOf("id" to id))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, "Email ou Code déjà utilisé")
                }
            }

            patch("/tenants/{id}/status") {
                val id = call.parameters["id"]?.toIntOrNull() ?: return@patch call.respond(HttpStatusCode.BadRequest)
                val request = call.receive<UpdateTenantStatusRequest>()
                superAdminRepository.toggleTenantStatus(id, request.isActive)
                call.respond(HttpStatusCode.OK)
            }

            post("/tenants/{id}/reset-password") {
                val id = call.parameters["id"]?.toIntOrNull() ?: return@post call.respond(HttpStatusCode.BadRequest)
                val request = call.receive<ResetPasswordRequest>()
                superAdminRepository.resetAdminPassword(id, request.newPassword)
                call.respond(HttpStatusCode.OK)
            }

            get("/stats") {
                val stats = superAdminRepository.getGlobalStats()
                call.respond(GlobalStatsResponse(
                    totalSchools = stats["totalSchools"] as Int,
                    totalStudents = stats["totalStudents"] as Int,
                    totalRevenue = stats["totalRevenue"] as Double
                ))
            }
        }
    }
}
