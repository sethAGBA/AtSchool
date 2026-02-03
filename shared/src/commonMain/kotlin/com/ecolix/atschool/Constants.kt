package com.ecolix.atschool

object Constants {
    val BASE_URL = BuildConfig.BASE_URL
    
    fun getBaseUrl(): String {
        var url = BASE_URL
        if (url.contains("localhost") && com.ecolix.atschool.getPlatform().name.contains("Android")) {
            url = url.replace("localhost", "10.0.2.2")
        }
        return url
    }
}