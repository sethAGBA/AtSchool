package com.ecolix.atschool.data

import org.jetbrains.exposed.sql.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

@Serializable
data class User(
    val id: Long? = null,
    val tenantId: Int,
    val email: String,
    val passwordHash: String,
    val role: String,
    val nom: String? = null,
    val prenom: String? = null,
    val isMfaEnabled: Boolean = false
)

class UserRepository {
    fun findByEmailAndCode(email: String, schoolCode: String): User? = transaction {
        (Users innerJoin Tenants).selectAll()
            .where { (Users.email eq email) and (Tenants.code eq schoolCode) }
            .map { it.toUser() }
            .singleOrNull()
    }

    fun findById(id: Long): User? = transaction {
        Users.selectAll().where { Users.id eq id }
            .map { it.toUser() }
            .singleOrNull()
    }

    fun createUser(user: User): Long = transaction {
        Users.insert {
            it[tenantId] = user.tenantId
            it[email] = user.email
            it[passwordHash] = user.passwordHash
            it[role] = user.role
            it[nom] = user.nom
            it[prenom] = user.prenom
            it[isMfaEnabled] = user.isMfaEnabled
        } get Users.id
    }.value

    private fun ResultRow.toUser() = User(
        id = this[Users.id].value,
        tenantId = this[Users.tenantId].value,
        email = this[Users.email],
        passwordHash = this[Users.passwordHash],
        role = this[Users.role],
        nom = this[Users.nom],
        prenom = this[Users.prenom],
        isMfaEnabled = this[Users.isMfaEnabled]
    )
}
