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
                val principal = call.principal<JWTPrincipal>()
                val actor = principal?.payload?.getClaim("email")?.asString() ?: "Unknown"
                
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
                    superAdminRepository.auditLog(actor, "CREATE_TENANT", "Created school: ${request.name} (${request.code})")
                    call.respond(HttpStatusCode.Created, mapOf("id" to id))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, "Email ou Code déjà utilisé")
                }
            }

            patch("/tenants/{id}/status") {
                val id = call.parameters["id"]?.toIntOrNull() ?: return@patch call.respond(HttpStatusCode.BadRequest)
                val request = call.receive<UpdateTenantStatusRequest>()
                val principal = call.principal<JWTPrincipal>()
                val actor = principal?.payload?.getClaim("email")?.asString() ?: "Unknown"

                superAdminRepository.toggleTenantStatus(id, request.isActive)
                superAdminRepository.auditLog(actor, "TOGGLE_STATUS", "Tenant #$id set to isActive=${request.isActive}")
                call.respond(HttpStatusCode.OK)
            }

            post("/tenants/{id}/reset-password") {
                val id = call.parameters["id"]?.toIntOrNull() ?: return@post call.respond(HttpStatusCode.BadRequest)
                val request = call.receive<ResetPasswordRequest>()
                val principal = call.principal<JWTPrincipal>()
                val actor = principal?.payload?.getClaim("email")?.asString() ?: "Unknown"

                superAdminRepository.resetAdminPassword(id, request.newPassword)
                superAdminRepository.auditLog(actor, "RESET_PASSWORD", "Reset admin password for tenant #$id")
                call.respond(HttpStatusCode.OK)
            }

            patch("/tenants/{id}/subscription") {
                val id = call.parameters["id"]?.toIntOrNull() ?: return@patch call.respond(HttpStatusCode.BadRequest)
                val request = call.receive<UpdateSubscriptionRequest>()
                val principal = call.principal<JWTPrincipal>()
                val actor = principal?.payload?.getClaim("email")?.asString() ?: "Unknown"

                val expiryDate = request.expiresAt?.let { kotlinx.datetime.LocalDate.parse(it) }
                superAdminRepository.updateSubscription(id, expiryDate)
                superAdminRepository.auditLog(actor, "UPDATE_SUBSCRIPTION", "Updated subscription for tenant #$id to $expiryDate")
                call.respond(HttpStatusCode.OK)
            }

            get("/announcements") {
                call.respond(superAdminRepository.listAnnouncements())
            }

            post("/announcements") {
                val request = call.receive<CreateAnnouncementRequest>()
                val principal = call.principal<JWTPrincipal>()
                val actor = principal?.payload?.getClaim("email")?.asString() ?: "Unknown"

                val expiryDate = request.expiresAt?.let { kotlinx.datetime.LocalDate.parse(it) }
                superAdminRepository.createAnnouncement(request.content, request.targetRole, expiryDate)
                superAdminRepository.auditLog(actor, "CREATE_ANNOUNCEMENT", "Published: ${request.content.take(50)}...")
                call.respond(HttpStatusCode.Created)
            }

            get("/logs") {
                call.respond(superAdminRepository.listAuditLogs())
            }

            get("/stats") {
                val stats = superAdminRepository.getGlobalStats()
                call.respond(GlobalStatsResponse(
                    totalSchools = stats["totalSchools"] as Int,
                    totalStudents = stats["totalStudents"] as Int,
                    totalRevenue = stats["totalRevenue"] as Double
                ))
            }

            // ==================== PAYMENT ROUTES ====================
            val advancedRepo by inject<com.ecolix.atschool.data.SuperAdminAdvancedRepository>()

            post("/payments") {
                val request = call.receive<CreatePaymentRequest>()
                val principal = call.principal<JWTPrincipal>()
                val actor = principal?.payload?.getClaim("email")?.asString() ?: "Unknown"
                
                val paymentId = advancedRepo.recordPayment(
                    tenantId = request.tenantId,
                    amount = request.amount,
                    paymentMethod = request.paymentMethod,
                    notes = request.notes
                )
                
                superAdminRepository.auditLog(actor, "RECORD_PAYMENT", "Payment recorded for tenant ${request.tenantId}: ${request.amount} FCFA", request.tenantId)
                call.respond(HttpStatusCode.Created, mapOf("id" to paymentId))
            }

            get("/payments") {
                val tenantId = call.request.queryParameters["tenantId"]?.toIntOrNull()
                val payments = advancedRepo.getPaymentHistory(tenantId)
                call.respond(payments)
            }

            patch("/payments/{id}") {
                val paymentId = call.parameters["id"]?.toLongOrNull() 
                    ?: return@patch call.respond(HttpStatusCode.BadRequest, "Invalid payment ID")
                val request = call.receive<UpdatePaymentStatusRequest>()
                
                advancedRepo.updatePaymentStatus(paymentId, request.status, request.invoiceNumber)
                call.respond(HttpStatusCode.OK)
            }

            get("/subscriptions/expiring") {
                val days = call.request.queryParameters["days"]?.toIntOrNull() ?: 30
                val expiring = advancedRepo.getExpiringSubscriptions(days)
                call.respond(expiring)
            }

            // ==================== NOTIFICATION ROUTES ====================

            post("/notifications") {
                val request = call.receive<CreateNotificationRequest>()
                val principal = call.principal<JWTPrincipal>()
                val actor = principal?.payload?.getClaim("email")?.asString() ?: "Unknown"
                
                val notifId = advancedRepo.createNotification(
                    tenantId = request.tenantId,
                    userId = request.userId,
                    title = request.title,
                    message = request.message,
                    type = request.type,
                    priority = request.priority,
                    expiresAt = request.expiresAt
                )
                
                superAdminRepository.auditLog(actor, "CREATE_NOTIFICATION", "Notification sent: ${request.title}")
                call.respond(HttpStatusCode.Created, mapOf("id" to notifId))
            }

            get("/notifications") {
                val tenantId = call.request.queryParameters["tenantId"]?.toIntOrNull()
                val userId = call.request.queryParameters["userId"]?.toLongOrNull()
                val unreadOnly = call.request.queryParameters["unreadOnly"]?.toBoolean() ?: false
                
                val notifications = advancedRepo.getNotifications(tenantId, userId, unreadOnly)
                call.respond(notifications)
            }

            patch("/notifications/{id}/read") {
                val notifId = call.parameters["id"]?.toLongOrNull()
                    ?: return@patch call.respond(HttpStatusCode.BadRequest, "Invalid notification ID")
                
                advancedRepo.markNotificationAsRead(notifId)
                call.respond(HttpStatusCode.OK)
            }

            // ==================== SUPPORT TICKET ROUTES ====================

            post("/tickets") {
                val request = call.receive<CreateTicketRequest>()
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asLong() ?: 0L
                val tenantId = principal?.payload?.getClaim("tenantId")?.asInt() ?: 0
                
                val ticketId = advancedRepo.createTicket(
                    tenantId = tenantId,
                    userId = userId,
                    subject = request.subject,
                    description = request.description,
                    priority = request.priority
                )
                
                call.respond(HttpStatusCode.Created, mapOf("id" to ticketId))
            }

            get("/tickets") {
                val status = call.request.queryParameters["status"]
                val tenantId = call.request.queryParameters["tenantId"]?.toIntOrNull()
                
                val tickets = advancedRepo.listTickets(status, tenantId)
                call.respond(tickets)
            }

            patch("/tickets/{id}") {
                val ticketId = call.parameters["id"]?.toLongOrNull()
                    ?: return@patch call.respond(HttpStatusCode.BadRequest, "Invalid ticket ID")
                val request = call.receive<UpdateTicketRequest>()
                val principal = call.principal<JWTPrincipal>()
                val actor = principal?.payload?.getClaim("email")?.asString() ?: "Unknown"
                
                advancedRepo.updateTicket(ticketId, request.status, request.assignedTo)
                superAdminRepository.auditLog(actor, "UPDATE_TICKET", "Ticket #$ticketId updated")
                call.respond(HttpStatusCode.OK)
            }

            // ==================== PERMISSION ROUTES ====================

            get("/admins/permissions") {
                val permissions = advancedRepo.listAdminPermissions()
                call.respond(permissions)
            }

            post("/admins/{userId}/permissions") {
                val userId = call.parameters["userId"]?.toLongOrNull()
                    ?: return@post call.respond(HttpStatusCode.BadRequest, "Invalid user ID")
                val request = call.receive<GrantPermissionRequest>()
                val principal = call.principal<JWTPrincipal>()
                val grantedBy = principal?.payload?.getClaim("userId")?.asLong() ?: 0L
                val actor = principal?.payload?.getClaim("email")?.asString() ?: "Unknown"
                
                advancedRepo.grantPermission(request.userId, request.permission, grantedBy)
                superAdminRepository.auditLog(actor, "GRANT_PERMISSION", "Granted ${request.permission} to user ${request.userId}")
                call.respond(HttpStatusCode.Created)
            }

            delete("/admins/{userId}/permissions/{permission}") {
                val userId = call.parameters["userId"]?.toLongOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest, "Invalid user ID")
                val permission = call.parameters["permission"]
                    ?: return@delete call.respond(HttpStatusCode.BadRequest, "Invalid permission")
                val principal = call.principal<JWTPrincipal>()
                val actor = principal?.payload?.getClaim("email")?.asString() ?: "Unknown"
                
                advancedRepo.revokePermission(userId, permission)
                superAdminRepository.auditLog(actor, "REVOKE_PERMISSION", "Revoked $permission from user $userId")
                call.respond(HttpStatusCode.OK)
            }

            // ==================== ANALYTICS ROUTES ====================

            get("/analytics/growth") {
                val start = call.request.queryParameters["start"] ?: "2026-01-01"
                val end = call.request.queryParameters["end"] ?: "2026-12-31"
                
                val metrics = advancedRepo.getGrowthMetrics(start, end)
                call.respond(metrics)
            }

            get("/analytics/revenue") {
                val period = call.request.queryParameters["period"] ?: "monthly"
                val revenue = advancedRepo.getRevenueByPeriod(period)
                call.respond(revenue)
            }

            get("/analytics/activity") {
                val activity = advancedRepo.getSchoolActivityRate()
                call.respond(activity)
            }
        }
    }
}
