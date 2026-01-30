package com.ecolix.atschool

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.ecolix.atschool.api.authRoutes
import com.ecolix.atschool.api.studentRoutes
import com.ecolix.atschool.api.structureRoutes
import com.ecolix.atschool.api.academicRoutes
import com.ecolix.atschool.data.DatabaseFactory
import com.ecolix.atschool.di.appModule
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.plugin.Koin
import org.koin.core.logger.Level

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    // 1. Dependency Injection
    install(Koin) {
        printLogger(Level.INFO)
        modules(appModule)
    }

    // 2. Database
    DatabaseFactory.init()

    // 3. Content Negotiation
    install(ContentNegotiation) {
        json()
    }

    // 4. Authentication
    val jwtSecret = System.getenv("JWT_SECRET") ?: "secret-key-atschool-2026"
    val jwtIssuer = "http://0.0.0.0:8080/"
    val jwtAudience = "atschool-users"
    val jwtRealm = "Access to atschool"

    install(Authentication) {
        jwt("auth-jwt") {
            realm = jwtRealm
            verifier(
                JWT.require(Algorithm.HMAC256(jwtSecret))
                    .withAudience(jwtAudience)
                    .withIssuer(jwtIssuer)
                    .build()
            )
            validate { credential ->
                if (credential.payload.getClaim("email").asString() != "") {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }
    }

    // 5. Routing
    routing {
        get("/") {
            call.respondText("AtSchool API is running!")
        }
        
        authRoutes()
        studentRoutes()
        structureRoutes()
        academicRoutes()
    }
}