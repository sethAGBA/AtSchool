package com.example.atschool

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform