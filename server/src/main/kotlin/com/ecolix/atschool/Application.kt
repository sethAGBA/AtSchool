package com.ecolix.atschool

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.ecolix.atschool.api.authRoutes
import com.ecolix.atschool.api.studentRoutes
import com.ecolix.atschool.api.dashboardRoutes
import com.ecolix.atschool.api.superAdminRoutes
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
import org.koin.dsl.module

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    // 1. Dependency Injection
    val jwtConfig = environment.config.config("jwt")
    val jwtSecret = jwtConfig.property("secret").getString()
    val jwtIssuer = jwtConfig.property("issuer").getString()
    val jwtAudience = jwtConfig.property("audience").getString()
    val jwtRealm = jwtConfig.property("realm").getString()

    // 1. Dependency Injection
    install(Koin) {
        printLogger(Level.INFO)
        modules(
            module {
                single(org.koin.core.qualifier.named("jwtSecret")) { jwtSecret }
                single(org.koin.core.qualifier.named("jwtIssuer")) { jwtIssuer }
                single(org.koin.core.qualifier.named("jwtAudience")) { jwtAudience }
                single(org.koin.core.qualifier.named("jwtRealm")) { jwtRealm }
            },
            appModule
        )
    }

    // 2. Database
    val dbConfig = environment.config.config("database")
    DatabaseFactory.init(dbConfig)
    
    // Seed data if empty
    com.ecolix.atschool.data.DataSeeder.seed()

    // 3. Content Negotiation
    install(ContentNegotiation) {
        json()
    }

    // 4. Authentication
    // Config loaded above for DI

    install(io.ktor.server.plugins.cors.routing.CORS) {
        allowMethod(io.ktor.http.HttpMethod.Options)
        allowMethod(io.ktor.http.HttpMethod.Put)
        allowMethod(io.ktor.http.HttpMethod.Delete)
        allowMethod(io.ktor.http.HttpMethod.Patch)
        allowHeader(io.ktor.http.HttpHeaders.Authorization)
        allowHeader(io.ktor.http.HttpHeaders.ContentType)
        anyHost() // Allow all hosts for development
    }

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
        dashboardRoutes()
        structureRoutes()
        academicRoutes()
        superAdminRoutes()
    }
}
