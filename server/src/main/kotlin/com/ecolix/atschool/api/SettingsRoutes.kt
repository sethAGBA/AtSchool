package com.ecolix.atschool.api

import com.ecolix.atschool.data.EstablishmentSettingsDto
import com.ecolix.atschool.data.EstablishmentSettingsRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.settingsRoutes() {
    val repository = EstablishmentSettingsRepository()

    route("/api/settings") {
        authenticate("auth-jwt") {
            // GET /api/settings - Récupérer les paramètres de l'établissement
            get {
                val principal = call.principal<JWTPrincipal>()
                val tenantId = principal?.payload?.getClaim("tenantId")?.asInt()

                if (tenantId == null) {
                    call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Tenant ID not found"))
                    return@get
                }

                val settings = repository.getSettings(tenantId)
                if (settings != null) {
                    call.respond(HttpStatusCode.OK, settings)
                } else {
                    // Return default structure if not found, to avoid front-end errors
                     val defaultSettings = EstablishmentSettingsDto(
                        tenantId = tenantId,
                        schoolName = "",
                        schoolCode = ""
                    )
                    call.respond(HttpStatusCode.OK, defaultSettings)
                }
            }

            // POST /api/settings - Créer les paramètres (première configuration)
            post {
                val principal = call.principal<JWTPrincipal>()
                val tenantId = principal?.payload?.getClaim("tenantId")?.asInt()

                if (tenantId == null) {
                    call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Tenant ID not found"))
                    return@post
                }

                // Vérifier si les paramètres existent déjà
                val existing = repository.getSettings(tenantId)
                if (existing != null) {
                    call.respond(HttpStatusCode.Conflict, mapOf("error" to "Settings already exist for this tenant"))
                    return@post
                }

                val dto = call.receive<EstablishmentSettingsDto>()
                val settingsDto = dto.copy(tenantId = tenantId)
                
                val id = repository.createSettings(settingsDto)
                call.respond(HttpStatusCode.Created, mapOf("id" to id, "message" to "Settings created successfully"))
            }

            // PUT /api/settings - Mettre à jour les paramètres (UPSERT)
            put {
                val principal = call.principal<JWTPrincipal>()
                val tenantId = principal?.payload?.getClaim("tenantId")?.asInt()

                if (tenantId == null) {
                    call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Tenant ID not found"))
                    return@put
                }

                val dto = call.receive<EstablishmentSettingsDto>()
                val settingsDto = dto.copy(tenantId = tenantId)
                
                // Try update first
                val updated = repository.updateSettings(tenantId, settingsDto)
                if (updated) {
                    call.respond(HttpStatusCode.OK, mapOf("message" to "Settings updated successfully"))
                } else {
                    // If update failed (not found), create it
                    repository.createSettings(settingsDto)
                    call.respond(HttpStatusCode.OK, mapOf("message" to "Settings created successfully"))
                }
            }
        }
    }
}
