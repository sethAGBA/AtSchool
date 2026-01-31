package com.ecolix.atschool.data

import com.ecolix.atschool.api.TenantDto
import com.ecolix.atschool.api.AnnouncementDto
import com.ecolix.atschool.api.AuditLogDto
import com.ecolix.atschool.security.PasswordUtils
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.kotlin.datetime.datetime
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toKotlinLocalDate

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
                subscriptionExpiresAt = row[Tenants.subscriptionExpiresAt]?.toString(),
                createdAt = row[Tenants.createdAt].toString(),
                isActive = row[Tenants.isActive]
            )
        }
    }

    fun auditLog(actorEmail: String, action: String, details: String? = null, tenantId: Int? = null) = transaction {
        AuditLogs.insert {
            it[AuditLogs.tenantId] = tenantId
            it[AuditLogs.actorEmail] = actorEmail
            it[AuditLogs.action] = action
            it[AuditLogs.details] = details
            it[AuditLogs.timestamp] = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        }
    }

    fun listAuditLogs(): List<AuditLogDto> = transaction {
        AuditLogs.selectAll().orderBy(AuditLogs.timestamp, SortOrder.DESC).limit(100).map {
            AuditLogDto(
                id = it[AuditLogs.id].value,
                actorEmail = it[AuditLogs.actorEmail],
                action = it[AuditLogs.action],
                details = it[AuditLogs.details],
                timestamp = it[AuditLogs.timestamp].toString()
            )
        }
    }

    fun createAnnouncement(content: String, targetRole: String?, expiresAt: kotlinx.datetime.LocalDate?) = transaction {
        Announcements.insert {
            it[Announcements.content] = content
            it[Announcements.targetRole] = targetRole
            it[Announcements.expiresAt] = expiresAt
            it[Announcements.createdAt] = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
            it[Announcements.isActive] = true
        }
    }

    fun listAnnouncements(): List<AnnouncementDto> = transaction {
        Announcements.selectAll().orderBy(Announcements.createdAt, SortOrder.DESC).map {
            AnnouncementDto(
                id = it[Announcements.id].value,
                content = it[Announcements.content],
                targetRole = it[Announcements.targetRole],
                expiresAt = it[Announcements.expiresAt]?.toString(),
                createdAt = it[Announcements.createdAt].toString(),
                isActive = it[Announcements.isActive]
            )
        }
    }

    fun updateSubscription(tenantId: Int, expiresAt: kotlinx.datetime.LocalDate?) = transaction {
        Tenants.update({ Tenants.id eq tenantId }) {
            it[Tenants.subscriptionExpiresAt] = expiresAt
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
