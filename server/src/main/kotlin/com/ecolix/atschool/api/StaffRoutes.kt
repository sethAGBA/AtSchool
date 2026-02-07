package com.ecolix.atschool.api

import com.ecolix.atschool.models.Staff
import com.ecolix.atschool.data.StaffRepository
import com.ecolix.atschool.data.StaffTable
import org.jetbrains.exposed.sql.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.getKoin

fun Route.staffRoutes() {
    val repository by lazy { application.getKoin().get<StaffRepository>() }


    authenticate("auth-jwt") {
        route("/staff") {
            get {
                try {
                    val principal = call.principal<JWTPrincipal>()
                    val tenantId = principal?.payload?.getClaim("tenantId")?.asInt() ?: return@get call.respond(HttpStatusCode.Unauthorized)
                    
                    val staff = repository.getAllStaff(tenantId)
                    call.respond(staff)
                } catch (e: Exception) {
                    println("DEBUG: Error in GET /staff: ${e.message}")
                    e.printStackTrace()
                    call.respond(HttpStatusCode.InternalServerError, e.message ?: "Unknown error")
                }
            }

            get("/{id}") {
                try {
                    val principal = call.principal<JWTPrincipal>()
                    val tenantId = principal?.payload?.getClaim("tenantId")?.asInt() ?: return@get call.respond(HttpStatusCode.Unauthorized)
                    val id = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest)

                    val member = repository.getStaffById(id, tenantId)
                    if (member != null) {
                        call.respond(member)
                    } else {
                        call.respond(HttpStatusCode.NotFound)
                    }
                } catch (e: Exception) {
                    println("DEBUG: Error in GET /staff/{id}: ${e.message}")
                    e.printStackTrace()
                    call.respond(HttpStatusCode.InternalServerError, e.message ?: "Unknown error")
                }
            }

            post {
                try {
                    val principal = call.principal<JWTPrincipal>()
                    val tenantId = principal?.payload?.getClaim("tenantId")?.asInt() ?: return@post call.respond(HttpStatusCode.Unauthorized)
                    val member = call.receive<Staff>()
                    
                    val id = repository.addStaff(member, tenantId)
                    call.respond(HttpStatusCode.Created, mapOf("id" to id))
                } catch (e: Exception) {
                    println("DEBUG: Error in POST /staff: ${e.message}")
                    e.printStackTrace()
                    call.respond(HttpStatusCode.InternalServerError, e.message ?: "Unknown error")
                }
            }

            put("/{id}") {
                try {
                    val principal = call.principal<JWTPrincipal>()
                    val tenantId = principal?.payload?.getClaim("tenantId")?.asInt() ?: return@put call.respond(HttpStatusCode.Unauthorized)
                    val id = call.parameters["id"] ?: return@put call.respond(HttpStatusCode.BadRequest)
                    val member = call.receive<Staff>()
                    
                    repository.updateStaff(member.copy(id = id), tenantId)
                    call.respond(HttpStatusCode.OK)
                } catch (e: Exception) {
                    println("DEBUG: Error in PUT /staff/{id}: ${e.message}")
                    e.printStackTrace()
                    call.respond(HttpStatusCode.InternalServerError, e.message ?: "Unknown error")
                }
            }

            delete("/{id}") {
                try {
                    val principal = call.principal<JWTPrincipal>()
                    val tenantId = principal?.payload?.getClaim("tenantId")?.asInt() ?: return@delete call.respond(HttpStatusCode.Unauthorized)
                    val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
                    
                    repository.deleteStaff(id, tenantId)
                    call.respond(HttpStatusCode.NoContent)
                } catch (e: Exception) {
                    println("DEBUG: Error in DELETE /staff/{id}: ${e.message}")
                    e.printStackTrace()
                    call.respond(HttpStatusCode.InternalServerError, e.message ?: "Unknown error")
                }
            }
            post("/bulk-delete") {
                try {
                    val principal = call.principal<JWTPrincipal>()
                    val tenantId = principal?.payload?.getClaim("tenantId")?.asInt() ?: return@post call.respond(HttpStatusCode.Unauthorized)
                    val ids = call.receive<List<String>>()
                    
                    repository.deleteStaffBatch(ids, tenantId)
                    call.respond(HttpStatusCode.NoContent)
                } catch (e: Exception) {
                    println("DEBUG: Error in POST /staff/bulk-delete: ${e.message}")
                    e.printStackTrace()
                    call.respond(HttpStatusCode.InternalServerError, e.message ?: "Unknown error")
                }
            }
            get("/trash") {
                try {
                    val principal = call.principal<JWTPrincipal>()
                    val tenantId = principal?.payload?.getClaim("tenantId")?.asInt() ?: return@get call.respond(HttpStatusCode.Unauthorized)
                    
                    val staff = repository.getDeletedStaff(tenantId)
                    call.respond(staff)
                } catch (e: Exception) {
                    println("DEBUG: Error in GET /staff/trash: ${e.message}")
                    e.printStackTrace()
                    call.respond(HttpStatusCode.InternalServerError, e.message ?: "Unknown error")
                }
            }

            post("/{id}/restore") {
                try {
                    val principal = call.principal<JWTPrincipal>()
                    val tenantId = principal?.payload?.getClaim("tenantId")?.asInt() ?: return@post call.respond(HttpStatusCode.Unauthorized)
                    val id = call.parameters["id"] ?: return@post call.respond(HttpStatusCode.BadRequest)
                    
                    repository.restoreStaff(id, tenantId)
                    call.respond(HttpStatusCode.OK)
                } catch (e: Exception) {
                    println("DEBUG: Error in POST /staff/{id}/restore: ${e.message}")
                    e.printStackTrace()
                    call.respond(HttpStatusCode.InternalServerError, e.message ?: "Unknown error")
                }
            }

            post("/bulk-restore") {
                try {
                    val principal = call.principal<JWTPrincipal>()
                    val tenantId = principal?.payload?.getClaim("tenantId")?.asInt() ?: return@post call.respond(HttpStatusCode.Unauthorized)
                    val ids = call.receive<List<String>>()
                    
                    repository.restoreStaffBatch(ids, tenantId)
                    call.respond(HttpStatusCode.NoContent)
                } catch (e: Exception) {
                    println("DEBUG: Error in POST /staff/bulk-restore: ${e.message}")
                    e.printStackTrace()
                    call.respond(HttpStatusCode.InternalServerError, e.message ?: "Unknown error")
                }
            }

            post("/bulk-status") {
                try {
                    val principal = call.principal<JWTPrincipal>()
                    val tenantId = principal?.payload?.getClaim("tenantId")?.asInt() ?: return@post call.respond(HttpStatusCode.Unauthorized)
                    val request = call.receive<com.ecolix.atschool.models.BulkStatusUpdate>()

                    repository.updateStaffStatusBatch(request.ids, request.status, tenantId)
                    call.respond(HttpStatusCode.OK)
                } catch (e: Exception) {
                    println("DEBUG: Error in POST /staff/bulk-status: ${e.message}")
                    e.printStackTrace()
                    call.respond(HttpStatusCode.InternalServerError, e.message ?: "Unknown error")
                }
            }

            delete("/{id}/permanent") {
                try {
                    val principal = call.principal<JWTPrincipal>()
                    val tenantId = principal?.payload?.getClaim("tenantId")?.asInt() ?: return@delete call.respond(HttpStatusCode.Unauthorized)
                    val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
                    
                    repository.permanentDeleteStaff(id, tenantId)
                    call.respond(HttpStatusCode.NoContent)
                } catch (e: Exception) {
                    println("DEBUG: Error in DELETE /staff/{id}/permanent: ${e.message}")
                    e.printStackTrace()
                    call.respond(HttpStatusCode.InternalServerError, e.message ?: "Unknown error")
                }
            }
        }
    }
}
