package com.ecolix.atschool.data

import com.ecolix.atschool.api.TenantDto
import com.ecolix.atschool.security.PasswordUtils
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class SuperAdminRepository {

    fun listTenants(): List<TenantDto> = transaction {
        Tenants.selectAll().map {
            TenantDto(
                id = it[Tenants.id].value,
                name = it[Tenants.name],
                domain = it[Tenants.domain],
                code = it[Tenants.code],
                createdAt = it[Tenants.createdAt].toString()
            )
        }
    }

    fun createTenant(name: String, code: String, adminEmail: String, password: String): Int = transaction {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        
        // 1. Create Tenant
        val tenantId = Tenants.insertAndGetId {
            it[Tenants.name] = name
            it[Tenants.domain] = "${code.lowercase()}.atschool.com"
            it[Tenants.code] = code.uppercase()
            it[Tenants.createdAt] = today
        }.value

        // 2. Create Admin User for this Tenant
        Users.insert {
            it[Users.tenantId] = tenantId
            it[Users.email] = adminEmail
            it[Users.passwordHash] = PasswordUtils.hashPassword(password)
            it[Users.role] = "ADMIN"
            it[Users.nom] = "Administrateur"
            it[Users.prenom] = name
        }

        tenantId
    }

    fun getGlobalStats() = transaction {
        val schoolsCount = Tenants.selectAll().count().toInt()
        val studentsCount = Eleves.selectAll().count().toInt()
        // Simple aggregate for demo, can be improved with real payment tables later
        val revenue = 0.0 

        mapOf(
            "totalSchools" to schoolsCount,
            "totalStudents" to studentsCount,
            "totalRevenue" to revenue
        )
    }
}
