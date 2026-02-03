package com.ecolix.atschool.api

import com.ecolix.atschool.data.DashboardRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.getKoin

fun Route.dashboardRoutes() {
    val repository by lazy { application.getKoin().get<DashboardRepository>() }

    authenticate("auth-jwt") {
        route("/dashboard") {
            get("/stats") {
                val principal = call.principal<JWTPrincipal>()
                val tenantId = principal?.payload?.getClaim("tenantId")?.asInt() ?: return@get call.respond(HttpStatusCode.Unauthorized)
                
                val stats = repository.getDashboardStats(tenantId)
                call.respond(stats)
            }
        }
    }
}
