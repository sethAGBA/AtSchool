package com.ecolix.atschool.api

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders

import com.ecolix.atschool.BuildConfig
import com.ecolix.atschool.getPlatform

object TokenProvider {
    private val settings = com.ecolix.atschool.util.Settings()
    private const val KEY_TOKEN = "auth_token"
    private const val KEY_ROLE = "auth_role"
    private const val KEY_REMEMBER_ME = "remember_me"

    var rememberMe: Boolean
        get() = settings.getBoolean(KEY_REMEMBER_ME, false)
        set(value) {
            settings.putBoolean(KEY_REMEMBER_ME, value)
            if (!value) {
                settings.putString(KEY_TOKEN, null)
                settings.putString(KEY_ROLE, null)
            }
        }

    var token: String? = settings.getString(KEY_TOKEN)
        set(value) {
            field = value
            if (rememberMe) {
                settings.putString(KEY_TOKEN, value)
            }
        }

    var role: String? = settings.getString(KEY_ROLE)
        set(value) {
            field = value
            if (rememberMe) {
                settings.putString(KEY_ROLE, value)
            }
        }
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

        HttpResponseValidator {
            validateResponse { response ->
                if (response.status.value == 401) {
                    AuthEvents.onUnauthorized()
                }
            }
        }
        
        /*
        install(Auth) {
            bearer {
                loadTokens {
                    TokenProvider.token?.let { BearerTokens(it, "") }
                }
            }
        }
        */
        
        defaultRequest {
            var baseUrl = BuildConfig.BASE_URL
            
            // Fix for Android Emulator using 10.0.2.2 for localhost
            if (baseUrl.contains("localhost") && getPlatform().name.contains("Android")) {
                baseUrl = baseUrl.replace("localhost", "10.0.2.2")
            }
            
            url(baseUrl)

            TokenProvider.token?.let {
                header(HttpHeaders.Authorization, "Bearer $it")
            }
        }
    }
}
