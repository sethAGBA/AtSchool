package com.ecolix.atschool.api

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(val email: String, val password: String)

@Serializable
data class LoginResponse(val token: String)

@Serializable
data class RegisterRequest(
    val email: String, 
    val password: String, 
    val tenantId: Int, 
    val role: String,
    val nom: String? = null,
    val prenom: String? = null
)

@Serializable
data class StudentResponse(
    val id: Long? = null,
    val tenantId: Int,
    val matricule: String,
    val nom: String,
    val prenom: String,
    val dateNaissance: String, // String for simplicity across platforms
    val sexe: String
)
