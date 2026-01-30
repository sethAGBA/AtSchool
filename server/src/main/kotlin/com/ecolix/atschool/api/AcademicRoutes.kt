package com.ecolix.atschool.api

import com.ecolix.atschool.data.SubjectRepository
import com.ecolix.atschool.data.EvaluationRepository
import com.ecolix.atschool.data.GradeRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.academicRoutes() {
    val subjectRepository by inject<SubjectRepository>()
    val evaluationRepository by inject<EvaluationRepository>()
    val gradeRepository by inject<GradeRepository>()

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
    }
}
