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
        Tenants.selectAll().map { row ->
            val tenantId = row[Tenants.id].value
            val adminEmail = Users.select(Users.email)
                .where { (Users.tenantId eq tenantId) and (Users.role eq "ADMIN") }
                .singleOrNull()?.get(Users.email)

            TenantDto(
                id = tenantId,
                name = row[Tenants.name],
                domain = row[Tenants.domain],
                code = row[Tenants.code],
                adminEmail = adminEmail,
                contactEmail = row[Tenants.contactEmail],
                contactPhone = row[Tenants.contactPhone],
                address = row[Tenants.address],
                createdAt = row[Tenants.createdAt].toString(),
                isActive = row[Tenants.isActive]
            )
        }
    }

    fun toggleTenantStatus(tenantId: Int, isActive: Boolean) = transaction {
        Tenants.update({ Tenants.id eq tenantId }) {
            it[Tenants.isActive] = isActive
        }
    }

    fun resetAdminPassword(tenantId: Int, newPassword: String) = transaction {
        Users.update({ (Users.tenantId eq tenantId) and (Users.role eq "ADMIN") }) {
            it[Users.passwordHash] = PasswordUtils.hashPassword(newPassword)
        }
    }

    fun createTenant(
        name: String, 
        code: String, 
        adminEmail: String, 
        password: String,
        contactEmail: String? = null,
        contactPhone: String? = null,
        address: String? = null
    ): Int = transaction {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        
        // 1. Create Tenant
        val tenantId = Tenants.insertAndGetId {
            it[Tenants.name] = name
            it[Tenants.domain] = "${code.lowercase()}.atschool.com"
            it[Tenants.code] = code.uppercase()
            it[Tenants.contactEmail] = contactEmail
            it[Tenants.contactPhone] = contactPhone
            it[Tenants.address] = address
            it[Tenants.createdAt] = today
            it[Tenants.isActive] = true
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
