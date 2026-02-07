package com.ecolix.atschool

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.ecolix.atschool.api.authRoutes
import com.ecolix.atschool.api.studentRoutes
import com.ecolix.atschool.api.dashboardRoutes
import com.ecolix.atschool.api.superAdminRoutes
import com.ecolix.atschool.api.structureRoutes
import com.ecolix.atschool.api.academicRoutes
import com.ecolix.atschool.api.settingsRoutes
import com.ecolix.atschool.api.uploadRoutes
import com.ecolix.atschool.api.staffRoutes
import com.ecolix.atschool.data.DatabaseFactory
import com.ecolix.atschool.di.appModule
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.engine.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File
import org.koin.ktor.plugin.Koin
import org.koin.core.logger.Level
import org.koin.dsl.module

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    // 📂 Consistent Upload Directory (Always at project root)
    val projectRoot = File(System.getProperty("user.dir")).let { dir ->
        if (dir.name == "server" || dir.path.endsWith("/server")) dir.parentFile else dir
    }
    val uploadDir = File(projectRoot, "uploads").absoluteFile
    if (!uploadDir.exists()) uploadDir.mkdirs()

    println("🚀 Server Working Directory: ${System.getProperty("user.dir")}")
    println("📂 Serving uploads from: ${uploadDir.absolutePath}")

    // Configuration
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
                single(org.koin.core.qualifier.named("uploadDir")) { uploadDir }
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
        settingsRoutes()
        uploadRoutes()
        staffRoutes()

        get("/uploads/{filename}") {
            val filename = call.parameters["filename"] ?: return@get call.respond(io.ktor.http.HttpStatusCode.BadRequest)
            val file = File(uploadDir, filename)
            if (file.exists()) {
                call.respondFile(file)
            } else {
                call.respond(io.ktor.http.HttpStatusCode.NotFound)
            }
        }
    }
}
