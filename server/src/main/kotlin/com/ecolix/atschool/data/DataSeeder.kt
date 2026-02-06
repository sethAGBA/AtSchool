package com.ecolix.atschool.data

import com.ecolix.atschool.security.PasswordUtils
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory

object DataSeeder {
    private val logger = LoggerFactory.getLogger("DataSeeder")

    fun seed() {
        transaction {
            if (Tenants.selectAll().count() == 0L) {
                logger.info("Database empty. Seeding default data...")

                // 1. Create Default Tenant
                val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
                val tenantId = Tenants.insertAndGetId {
                    it[name] = "École Demo"
                    it[domain] = "demo.atschool.com"
                    it[code] = "DEMO"
                    it[createdAt] = today // Exposed Date column usually takes LocalDate
                }

                logger.info("Created Default Tenant: $tenantId")

                // 2. Create Super Admin (Global - attached to first tenant for convenience)
                val superAdminEmail = "seth@atschool.com"
                if (Users.selectAll().where { Users.email eq superAdminEmail }.count() == 0L) {
                    Users.insert {
                        it[Users.tenantId] = tenantId.value
                        it[email] = superAdminEmail
                        it[passwordHash] = PasswordUtils.hashPassword("superadmin")
                        it[role] = "SUPER_ADMIN"
                        it[nom] = "AGBA"
                        it[prenom] = "Seth"
                    }
                    logger.info("Created Super Admin: $superAdminEmail / superadmin")
                }

                // 3. Create normal Admin for Demo
                val adminEmail = "admin@atschool.com"
                
                // Check if user exists (should not if users table is empty, but safety first)
                if (Users.selectAll().where { Users.email eq adminEmail }.count() == 0L) {
                    Users.insert {
                        it[Users.tenantId] = tenantId
                        it[email] = adminEmail
                        it[passwordHash] = PasswordUtils.hashPassword("admin") // Default password
                        it[role] = "ADMIN"
                        it[nom] = "Admin"
                        it[prenom] = "System"
                        it[isMfaEnabled] = false
                    }
                    logger.info("Created Default Admin: $adminEmail / admin")
                }

                // 3. Create basic structure (cycles and levels only - no classes)
                val cycleId = Cycles.insertAndGetId {
                    it[Cycles.tenantId] = tenantId
                    it[nom] = "Secondaire"
                }

                val niveauId = Niveaux.insertAndGetId {
                    it[Niveaux.cycleId] = cycleId
                    it[nom] = "6ème"
                }

                // Classes and students should be created by administrators through the UI
                logger.info("Created basic structure (cycle and niveau). Classes and students should be added via UI.")

                // Establishment settings should be configured through the UI
                logger.info("Establishment settings should be configured via UI.")

                // 5. Audit Log
                AuditLogs.insert {
                    it[AuditLogs.actorEmail] = adminEmail
                    it[AuditLogs.action] = "Initialisation système"
                    it[AuditLogs.timestamp] = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                    it[AuditLogs.details] = "Initialisation de la structure de base (utilisateurs et structure scolaire)"
                }
                logger.info("System initialized. Configure establishment settings, classes, and students via UI.")
                logger.info("Database already seeded. Skipping main flow.")
            }

            val superAdminEmail = "seth@atschool.com"
            val existingSuperAdmin = Users.selectAll().where { Users.email eq superAdminEmail }.singleOrNull()
            
            if (existingSuperAdmin == null) {
                val firstTenantId = Tenants.selectAll().firstOrNull()?.get(Tenants.id)?.value
                if (firstTenantId != null) {
                    Users.insert {
                        it[tenantId] = firstTenantId
                        it[email] = superAdminEmail
                        it[passwordHash] = PasswordUtils.hashPassword("superadmin")
                        it[role] = "SUPER_ADMIN"
                        it[nom] = "AGBA"
                        it[prenom] = "Seth"
                    }
                    logger.info("Created Super Admin: $superAdminEmail")
                }
            } else if (existingSuperAdmin[Users.role] != "SUPER_ADMIN") {
                // Update existing user to Super Admin
                Users.update({ Users.email eq superAdminEmail }) {
                    it[role] = "SUPER_ADMIN"
                }
                logger.info("Updated existing user to Super Admin: $superAdminEmail")
            }
        }
    }
}
