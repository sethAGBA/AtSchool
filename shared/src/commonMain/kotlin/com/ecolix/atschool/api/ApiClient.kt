package com.ecolix.atschool.api

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

import com.ecolix.atschool.BuildConfig
import com.ecolix.atschool.getPlatform

object TokenProvider {
    var token: String? = null
}

fun createHttpClient(): HttpClient {
    return HttpClient {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
        
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.ALL
        }
        
        install(Auth) {
            bearer {
                loadTokens {
                    TokenProvider.token?.let { BearerTokens(it, "") }
                }
            }
        }
        
        defaultRequest {
            var baseUrl = BuildConfig.BASE_URL
            
            // Fix for Android Emulator using 10.0.2.2 for localhost
            if (baseUrl.contains("localhost") && getPlatform().name.contains("Android")) {
                baseUrl = baseUrl.replace("localhost", "10.0.2.2")
            }
            
            url(baseUrl)
        }
    }
}
