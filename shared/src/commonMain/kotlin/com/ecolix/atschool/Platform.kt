package com.ecolix.atschool

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform