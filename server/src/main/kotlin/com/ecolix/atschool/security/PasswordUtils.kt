package com.ecolix.atschool.security

import org.mindrot.jbcrypt.BCrypt

object PasswordUtils {
    fun hashPassword(password: String): String {
        return BCrypt.hashpw(password, BCrypt.gensalt())
    }

    fun verifyPassword(password: String, hashed: String): Boolean {
        return BCrypt.checkpw(password, hashed)
    }
}
