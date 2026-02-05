package com.ecolix.atschool.api

import com.ecolix.atschool.data.EstablishmentRepository
import com.ecolix.atschool.data.ClassRepository
import com.ecolix.atschool.data.SchoolYearRepository
import com.ecolix.atschool.data.AcademicPeriodRepository
import com.ecolix.atschool.data.CycleRepository
import com.ecolix.atschool.data.LevelRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.request.*
import com.ecolix.atschool.data.*
import org.koin.ktor.ext.getKoin

fun Route.structureRoutes() {
    val establishmentRepository by lazy { application.getKoin().get<EstablishmentRepository>() }
    val classRepository by lazy { application.getKoin().get<ClassRepository>() }
    val schoolYearRepository by lazy { application.getKoin().get<SchoolYearRepository>() }
    val academicPeriodRepository by lazy { application.getKoin().get<AcademicPeriodRepository>() }
    val cycleRepository by lazy { application.getKoin().get<CycleRepository>() }
    val levelRepository by lazy { application.getKoin().get<LevelRepository>() }

    authenticate("auth-jwt") {
        route("/establishments") {
            get {
                val principal = call.principal<JWTPrincipal>()
                val tenantId = principal?.payload?.getClaim("tenantId")?.asInt() ?: return@get call.respond(HttpStatusCode.Unauthorized)
                call.respond(establishmentRepository.getAll(tenantId))
            }
        }

        route("/school-years") {
            get {
                val principal = call.principal<JWTPrincipal>()
                val tenantId = principal?.payload?.getClaim("tenantId")?.asInt() ?: return@get call.respond(HttpStatusCode.Unauthorized)
                
                // Get all school years
                val years = schoolYearRepository.getAll(tenantId)
                
                // For each year, fetch its periods
                val yearsWithPeriods = years.map { year ->
                    val periods = year.id?.let { yearId ->
                        academicPeriodRepository.getByYear(yearId, tenantId)
                    } ?: emptyList()
                    
                    year.copy(periods = periods)
                }
                
                call.respond(yearsWithPeriods)
            }

            get("/{id}/periods") {
                val principal = call.principal<JWTPrincipal>()
                val tenantId = principal?.payload?.getClaim("tenantId")?.asInt() ?: return@get call.respond(HttpStatusCode.Unauthorized)
                val yearId = call.parameters["id"]?.toIntOrNull() ?: return@get call.respond(HttpStatusCode.BadRequest)
                call.respond(academicPeriodRepository.getByYear(yearId, tenantId))
            }

            post {
                val principal = call.principal<JWTPrincipal>()
                val tenantId = principal?.payload?.getClaim("tenantId")?.asInt() ?: return@post call.respond(HttpStatusCode.Unauthorized)
                val year = call.receive<SchoolYearEntity>().copy(tenantId = tenantId)
                
                println("[CREATE SCHOOL YEAR] Received request:")
                println("  - Name: ${year.libelle}")
                println("  - Start: ${year.dateDebut}, End: ${year.dateFin}")
                println("  - Period Types: ${year.periodType}")
                println("  - Number of Periods: ${year.numberOfPeriods}")
                println("  - Custom Periods: ${year.periods?.size ?: 0} periods")
                year.periods?.forEach { period ->
                    println("    * ${period.nom}: ${period.dateDebut} → ${period.dateFin} (${period.periodType})")
                }
                
                try {
                    val id = schoolYearRepository.create(year)
                    println("[CREATE SCHOOL YEAR] ✅ Success - ID: $id")
                    call.respond(HttpStatusCode.Created, id)
                } catch (e: IllegalArgumentException) {
                    println("[CREATE SCHOOL YEAR] ❌ Validation Error: ${e.message}")
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to (e.message ?: "Validation failed")))
                } catch (e: Exception) {
                    println("[CREATE SCHOOL YEAR] ❌ Error: ${e.message}")
                    e.printStackTrace()
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Failed to create school year"))
                }
            }

            put("/{id}") {
                val principal = call.principal<JWTPrincipal>()
                val tenantId = principal?.payload?.getClaim("tenantId")?.asInt() ?: return@put call.respond(HttpStatusCode.Unauthorized)
                val id = call.parameters["id"]?.toIntOrNull() ?: return@put call.respond(HttpStatusCode.BadRequest)
                val year = call.receive<SchoolYearEntity>().copy(tenantId = tenantId)
                
                println("[UPDATE SCHOOL YEAR] Received request for ID: $id")
                println("  - Name: ${year.libelle}")
                println("  - Start: ${year.dateDebut}, End: ${year.dateFin}")
                println("  - Period Types: ${year.periodType}")
                println("  - Number of Periods: ${year.numberOfPeriods}")
                println("  - Custom Periods: ${year.periods?.size ?: 0} periods")
                year.periods?.forEach { period ->
                    println("    * ${period.nom}: ${period.dateDebut} → ${period.dateFin} (${period.periodType})")
                }
                
                try {
                    if (schoolYearRepository.update(id, year)) {
                        println("[UPDATE SCHOOL YEAR] ✅ Success - ID: $id")
                        call.respond(HttpStatusCode.OK)
                    } else {
                        println("[UPDATE SCHOOL YEAR] ❌ Not Found - ID: $id")
                        call.respond(HttpStatusCode.NotFound)
                    }
                } catch (e: IllegalArgumentException) {
                    println("[UPDATE SCHOOL YEAR] ❌ Validation Error: ${e.message}")
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to (e.message ?: "Validation failed")))
                } catch (e: Exception) {
                    println("[UPDATE SCHOOL YEAR] ❌ Error: ${e.message}")
                    e.printStackTrace()
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Failed to update school year"))
                }
            }

            delete("/{id}") {
                val principal = call.principal<JWTPrincipal>()
                val tenantId = principal?.payload?.getClaim("tenantId")?.asInt() ?: return@delete call.respond(HttpStatusCode.Unauthorized)
                val id = call.parameters["id"]?.toIntOrNull() ?: return@delete call.respond(HttpStatusCode.BadRequest)
                if (schoolYearRepository.delete(id, tenantId)) call.respond(HttpStatusCode.OK) else call.respond(HttpStatusCode.NotFound)
            }

            post("/{id}/set-default") {
                val principal = call.principal<JWTPrincipal>()
                val tenantId = principal?.payload?.getClaim("tenantId")?.asInt() ?: return@post call.respond(HttpStatusCode.Unauthorized)
                val id = call.parameters["id"]?.toIntOrNull() ?: return@post call.respond(HttpStatusCode.BadRequest)
                schoolYearRepository.setAsDefault(id, tenantId)
                call.respond(HttpStatusCode.OK)
            }

            post("/{id}/set-status") {
                val principal = call.principal<JWTPrincipal>()
                val tenantId = principal?.payload?.getClaim("tenantId")?.asInt() ?: return@post call.respond(HttpStatusCode.Unauthorized)
                val id = call.parameters["id"]?.toIntOrNull() ?: return@post call.respond(HttpStatusCode.BadRequest)
                val status = call.receive<Map<String, String>>()["status"] ?: return@post call.respond(HttpStatusCode.BadRequest)
                schoolYearRepository.setStatus(id, status, tenantId)
                call.respond(HttpStatusCode.OK)
            }
        }

        route("/academic-periods") {
            post {
                val principal = call.principal<JWTPrincipal>()
                val tenantId = principal?.payload?.getClaim("tenantId")?.asInt() ?: return@post call.respond(HttpStatusCode.Unauthorized)
                val period = call.receive<AcademicPeriodEntity>().copy(tenantId = tenantId)
                val id = academicPeriodRepository.create(period)
                call.respond(HttpStatusCode.Created, id)
            }

            put("/{id}") {
                val principal = call.principal<JWTPrincipal>()
                val tenantId = principal?.payload?.getClaim("tenantId")?.asInt() ?: return@put call.respond(HttpStatusCode.Unauthorized)
                val id = call.parameters["id"]?.toIntOrNull() ?: return@put call.respond(HttpStatusCode.BadRequest)
                val period = call.receive<AcademicPeriodEntity>().copy(tenantId = tenantId)
                if (academicPeriodRepository.update(id, period)) call.respond(HttpStatusCode.OK) else call.respond(HttpStatusCode.NotFound)
            }

            delete("/{id}") {
                val principal = call.principal<JWTPrincipal>()
                val tenantId = principal?.payload?.getClaim("tenantId")?.asInt() ?: return@delete call.respond(HttpStatusCode.Unauthorized)
                val id = call.parameters["id"]?.toIntOrNull() ?: return@delete call.respond(HttpStatusCode.BadRequest)
                if (academicPeriodRepository.delete(id, tenantId)) call.respond(HttpStatusCode.OK) else call.respond(HttpStatusCode.NotFound)
            }

            post("/{id}/set-active") {
                val principal = call.principal<JWTPrincipal>()
                val tenantId = principal?.payload?.getClaim("tenantId")?.asInt() ?: return@post call.respond(HttpStatusCode.Unauthorized)
                val id = call.parameters["id"]?.toIntOrNull() ?: return@post call.respond(HttpStatusCode.BadRequest)
                academicPeriodRepository.setStatus(id, "ACTIVE", tenantId)
                call.respond(HttpStatusCode.OK)
            }

            post("/{id}/set-status") {
                val principal = call.principal<JWTPrincipal>()
                val tenantId = principal?.payload?.getClaim("tenantId")?.asInt() ?: return@post call.respond(HttpStatusCode.Unauthorized)
                val id = call.parameters["id"]?.toIntOrNull() ?: return@post call.respond(HttpStatusCode.BadRequest)
                val status = call.receive<Map<String, String>>()["status"] ?: return@post call.respond(HttpStatusCode.BadRequest)
                if (academicPeriodRepository.setStatus(id, status, tenantId)) {
                    call.respond(HttpStatusCode.OK)
                } else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }
        }

        route("/cycles") {
            get {
                val principal = call.principal<JWTPrincipal>()
                val tenantId = principal?.payload?.getClaim("tenantId")?.asInt() ?: return@get call.respond(HttpStatusCode.Unauthorized)
                call.respond(cycleRepository.getAll(tenantId))
            }

            get("/{id}/levels") {
                val cycleId = call.parameters["id"]?.toIntOrNull() ?: return@get call.respond(HttpStatusCode.BadRequest)
                call.respond(levelRepository.getByCycle(cycleId))
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
