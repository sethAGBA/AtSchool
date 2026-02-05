package com.ecolix.atschool.api

import com.ecolix.atschool.data.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.getKoin
import org.koin.ktor.ext.inject

fun Route.academicRoutes() {
    val subjectRepository by lazy { application.getKoin().get<SubjectRepository>() }
    val evaluationRepository by lazy { application.getKoin().get<EvaluationRepository>() }
    val gradeRepository by lazy { application.getKoin().get<GradeRepository>() }
    val academicEventRepository by lazy { application.getKoin().get<AcademicEventRepository>() }
    val holidayRepository by lazy { application.getKoin().get<HolidayRepository>() }

    authenticate("auth-jwt") {
        route("/subjects") {
            get {
                val principal = call.principal<JWTPrincipal>()
                val tenantId = principal?.payload?.getClaim("tenantId")?.asInt() ?: return@get call.respond(HttpStatusCode.Unauthorized)
                call.respond(subjectRepository.getAll(tenantId))
            }
        }

        route("/evaluations") {
            get("/class/{classeId}") {
                val principal = call.principal<JWTPrincipal>()
                val tenantId = principal?.payload?.getClaim("tenantId")?.asInt() ?: return@get call.respond(HttpStatusCode.Unauthorized)
                val classeId = call.parameters["classeId"]?.toIntOrNull() ?: return@get call.respond(HttpStatusCode.BadRequest)
                
                call.respond(evaluationRepository.getByClass(classeId, tenantId))
            }
        }

        route("/grades") {
            get("/evaluation/{evaluationId}") {
                val evaluationId = call.parameters["evaluationId"]?.toLongOrNull() ?: return@get call.respond(HttpStatusCode.BadRequest)
                call.respond(gradeRepository.getByEvaluation(evaluationId))
            }
        }

        route("/academic-events") {
            get {
                val principal = call.principal<JWTPrincipal>()
                val tenantId = principal?.payload?.getClaim("tenantId")?.asInt() ?: return@get call.respond(HttpStatusCode.Unauthorized)
                call.respond(academicEventRepository.getAll(tenantId))
            }

            post {
                val principal = call.principal<JWTPrincipal>()
                val tenantId = principal?.payload?.getClaim("tenantId")?.asInt() ?: return@post call.respond(HttpStatusCode.Unauthorized)
                val event = call.receive<AcademicEventEntity>().copy(tenantId = tenantId)
                val id = academicEventRepository.create(event)
                call.respond(HttpStatusCode.Created, id)
            }

            put("/{id}") {
                val principal = call.principal<JWTPrincipal>()
                val tenantId = principal?.payload?.getClaim("tenantId")?.asInt() ?: return@put call.respond(HttpStatusCode.Unauthorized)
                val id = call.parameters["id"]?.toIntOrNull() ?: return@put call.respond(HttpStatusCode.BadRequest)
                val event = call.receive<AcademicEventEntity>().copy(tenantId = tenantId)
                if (academicEventRepository.update(id, event)) call.respond(HttpStatusCode.OK) else call.respond(HttpStatusCode.NotFound)
            }

            delete("/{id}") {
                val principal = call.principal<JWTPrincipal>()
                val tenantId = principal?.payload?.getClaim("tenantId")?.asInt() ?: return@delete call.respond(HttpStatusCode.Unauthorized)
                val id = call.parameters["id"]?.toIntOrNull() ?: return@delete call.respond(HttpStatusCode.BadRequest)
                if (academicEventRepository.delete(id, tenantId)) call.respond(HttpStatusCode.OK) else call.respond(HttpStatusCode.NotFound)
            }
        }

        route("/holidays") {
            get {
                val principal = call.principal<JWTPrincipal>()
                val tenantId = principal?.payload?.getClaim("tenantId")?.asInt() ?: return@get call.respond(HttpStatusCode.Unauthorized)
                call.respond(holidayRepository.getAll(tenantId))
            }

            post {
                val principal = call.principal<JWTPrincipal>()
                val tenantId = principal?.payload?.getClaim("tenantId")?.asInt() ?: return@post call.respond(HttpStatusCode.Unauthorized)
                val holiday = call.receive<HolidayEntity>().copy(tenantId = tenantId)
                val id = holidayRepository.create(holiday)
                call.respond(HttpStatusCode.Created, id)
            }

            put("/{id}") {
                val principal = call.principal<JWTPrincipal>()
                val tenantId = principal?.payload?.getClaim("tenantId")?.asInt() ?: return@put call.respond(HttpStatusCode.Unauthorized)
                val id = call.parameters["id"]?.toIntOrNull() ?: return@put call.respond(HttpStatusCode.BadRequest)
                val holiday = call.receive<HolidayEntity>().copy(tenantId = tenantId)
                if (holidayRepository.update(id, holiday)) call.respond(HttpStatusCode.OK) else call.respond(HttpStatusCode.NotFound)
            }

            delete("/{id}") {
                val principal = call.principal<JWTPrincipal>()
                val tenantId = principal?.payload?.getClaim("tenantId")?.asInt() ?: return@delete call.respond(HttpStatusCode.Unauthorized)
                val id = call.parameters["id"]?.toIntOrNull() ?: return@delete call.respond(HttpStatusCode.BadRequest)
                if (holidayRepository.delete(id, tenantId)) call.respond(HttpStatusCode.OK) else call.respond(HttpStatusCode.NotFound)
            }
        }
    }
}
