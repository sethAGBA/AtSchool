package com.ecolix.atschool.api

import com.ecolix.atschool.data.EstablishmentRepository
import com.ecolix.atschool.data.ClassRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.getKoin

fun Route.structureRoutes() {
    val establishmentRepository by lazy { application.getKoin().get<EstablishmentRepository>() }
    val classRepository by lazy { application.getKoin().get<ClassRepository>() }

    authenticate("auth-jwt") {
        route("/establishments") {
            get {
                val principal = call.principal<JWTPrincipal>()
                val tenantId = principal?.payload?.getClaim("tenantId")?.asInt() ?: return@get call.respond(HttpStatusCode.Unauthorized)
                call.respond(establishmentRepository.getAll(tenantId))
            }
        }

        route("/classes") {
            get {
                val principal = call.principal<JWTPrincipal>()
                val tenantId = principal?.payload?.getClaim("tenantId")?.asInt() ?: return@get call.respond(HttpStatusCode.Unauthorized)
                call.respond(classRepository.getAllByTenant(tenantId))
            }

            get("/{id}") {
                val principal = call.principal<JWTPrincipal>()
                val tenantId = principal?.payload?.getClaim("tenantId")?.asInt() ?: return@get call.respond(HttpStatusCode.Unauthorized)
                val id = call.parameters["id"]?.toIntOrNull() ?: return@get call.respond(HttpStatusCode.BadRequest)
                
                val classEntity = classRepository.getById(id, tenantId)
                if (classEntity != null) call.respond(classEntity) else call.respond(HttpStatusCode.NotFound)
            }
        }
    }
}
