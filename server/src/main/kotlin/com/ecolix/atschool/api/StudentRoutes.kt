package com.ecolix.atschool.api

import com.ecolix.atschool.data.Student
import com.ecolix.atschool.data.StudentRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.getKoin

fun Route.studentRoutes() {
    val repository by lazy { application.getKoin().get<StudentRepository>() }

    authenticate("auth-jwt") {
        route("/students") {
            get {
                val principal = call.principal<JWTPrincipal>()
                val tenantId = principal?.payload?.getClaim("tenantId")?.asInt() ?: return@get call.respond(HttpStatusCode.Unauthorized)
                
                val students = repository.getAllStudents(tenantId)
                call.respond(students)
            }

            get("/{id}") {
                val principal = call.principal<JWTPrincipal>()
                val tenantId = principal?.payload?.getClaim("tenantId")?.asInt() ?: return@get call.respond(HttpStatusCode.Unauthorized)
                val id = call.parameters["id"]?.toLongOrNull() ?: return@get call.respond(HttpStatusCode.BadRequest)

                val student = repository.getStudentById(id, tenantId)
                if (student != null) {
                    call.respond(student)
                } else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }

            post {
                val principal = call.principal<JWTPrincipal>()
                val tenantId = principal?.payload?.getClaim("tenantId")?.asInt() ?: return@post call.respond(HttpStatusCode.Unauthorized)
                val student = call.receive<Student>()
                
                // Force tenantId from JWT for security
                val id = repository.addStudent(student.copy(tenantId = tenantId))
                call.respond(HttpStatusCode.Created, mapOf("id" to id))
            }

            put("/{id}") {
                val principal = call.principal<JWTPrincipal>()
                val tenantId = principal?.payload?.getClaim("tenantId")?.asInt() ?: return@put call.respond(HttpStatusCode.Unauthorized)
                val id = call.parameters["id"]?.toLongOrNull() ?: return@put call.respond(HttpStatusCode.BadRequest)
                val student = call.receive<Student>()
                
                repository.updateStudent(student.copy(id = id, tenantId = tenantId), tenantId)
                call.respond(HttpStatusCode.OK)
            }

            get("/next-matricule") {
                val principal = call.principal<JWTPrincipal>()
                val tenantId = principal?.payload?.getClaim("tenantId")?.asInt() ?: return@get call.respond(HttpStatusCode.Unauthorized)
                
                val nextMatricule = repository.generateNextMatricule(tenantId)
                call.respond(mapOf("matricule" to nextMatricule))
            }

            delete("/{id}") {
                val principal = call.principal<JWTPrincipal>()
                val tenantId = principal?.payload?.getClaim("tenantId")?.asInt() ?: return@delete call.respond(HttpStatusCode.Unauthorized)
                val id = call.parameters["id"]?.toLongOrNull() ?: return@delete call.respond(HttpStatusCode.BadRequest)
                
                repository.deleteStudent(id, tenantId)
                call.respond(HttpStatusCode.NoContent)
            }

            post("/{id}/restore") {
                val principal = call.principal<JWTPrincipal>()
                val tenantId = principal?.payload?.getClaim("tenantId")?.asInt() ?: return@post call.respond(HttpStatusCode.Unauthorized)
                val id = call.parameters["id"]?.toLongOrNull() ?: return@post call.respond(HttpStatusCode.BadRequest)
                
                repository.restoreStudent(id, tenantId)
                call.respond(HttpStatusCode.OK)
            }

            delete("/{id}/permanent") {
                val principal = call.principal<JWTPrincipal>()
                val tenantId = principal?.payload?.getClaim("tenantId")?.asInt() ?: return@delete call.respond(HttpStatusCode.Unauthorized)
                val id = call.parameters["id"]?.toLongOrNull() ?: return@delete call.respond(HttpStatusCode.BadRequest)
                
                repository.deleteStudentPermanently(id, tenantId)
                call.respond(HttpStatusCode.NoContent)
            }

            post("/transfer") {
                val principal = call.principal<JWTPrincipal>()
                val tenantId = principal?.payload?.getClaim("tenantId")?.asInt() ?: return@post call.respond(HttpStatusCode.Unauthorized)
                
                val request = call.receive<TransferStudentRequest>()
                
                val studentIds = request.studentIds.mapNotNull { it.toLongOrNull() }
                if (studentIds.isEmpty()) return@post call.respond(HttpStatusCode.BadRequest, "Invalid studentIds")
                
                val newClassroomId = request.newClassroomId.toIntOrNull()
                    ?: return@post call.respond(HttpStatusCode.BadRequest, "Invalid newClassroomId")
                
                call.application.environment.log.info("DEBUG [StudentRoutes] Received transfer request: ids=$studentIds to class=$newClassroomId")
                val updatedCount = repository.transferStudents(studentIds, newClassroomId, tenantId)
                call.application.environment.log.info("DEBUG [StudentRoutes] Transfer completed. Updated count: $updatedCount")
                call.respond(HttpStatusCode.OK, mapOf("updatedCount" to updatedCount))
            }
        }
    }
}
