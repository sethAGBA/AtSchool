package com.ecolix.atschool.api

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.ecolix.atschool.data.User
import com.ecolix.atschool.data.UserRepository
import com.ecolix.atschool.security.PasswordUtils
import java.util.*

class AuthService(
    private val userRepository: UserRepository,
    private val jwtSecret: String,
    private val jwtIssuer: String,
    private val jwtAudience: String
) {

    fun authenticate(email: String, password: String, schoolCode: String): LoginResponse? {
        val user = userRepository.findByEmailAndCode(email, schoolCode) ?: return null
        
        if (!user.isTenantActive && user.role != "SUPER_ADMIN") {
            return null // Inactive school, only superadmin can pass if hosted elsewhere
        }

        return if (PasswordUtils.verifyPassword(password, user.passwordHash)) {
            val token = generateToken(user)
            LoginResponse(token, user.role)
        } else {
            null
        }
    }

    fun register(user: User, password: String): Long {
        val passwordHash = PasswordUtils.hashPassword(password)
        return userRepository.createUser(user.copy(passwordHash = passwordHash))
    }

    private fun generateToken(user: User): String {
        return JWT.create()
            .withAudience(jwtAudience)
            .withIssuer(jwtIssuer)
            .withClaim("email", user.email)
            .withClaim("userId", user.id)
            .withClaim("tenantId", user.tenantId)
            .withClaim("role", user.role)
            .withExpiresAt(Date(System.currentTimeMillis() + 3600000 * 24)) // 24 hours
            .sign(Algorithm.HMAC256(jwtSecret))
    }
}
