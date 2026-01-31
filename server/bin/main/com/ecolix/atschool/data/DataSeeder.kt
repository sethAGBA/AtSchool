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

                // 3. Create basic structure
                val cycleId = Cycles.insertAndGetId {
                    it[Cycles.tenantId] = tenantId
                    it[nom] = "Secondaire"
                }

                val niveauId = Niveaux.insertAndGetId {
                    it[Niveaux.cycleId] = cycleId
                    it[nom] = "6ème"
                }

                val classeId = Classes.insertAndGetId {
                    it[Classes.tenantId] = tenantId
                    it[Classes.niveauId] = niveauId
                    it[code] = "6A"
                    it[nom] = "6ème A"
                }

                // 4. Create sample students
                Eleves.insert {
                    it[Eleves.tenantId] = tenantId
                    it[matricule] = "MAT001"
                    it[nom] = "Diallo"
                    it[prenom] = "Binta"
                    it[dateNaissance] = kotlinx.datetime.LocalDate(2012, 5, 15)
                    it[sexe] = "F"
                }

                Eleves.insert {
                    it[Eleves.tenantId] = tenantId
                    it[matricule] = "MAT002"
                    it[nom] = "Traore"
                    it[prenom] = "Moussa"
                    it[dateNaissance] = kotlinx.datetime.LocalDate(2012, 8, 20)
                    it[sexe] = "M"
                }

                // 5. Audit Log
                AuditLogs.insert {
                    it[AuditLogs.actorEmail] = adminEmail
                    it[AuditLogs.action] = "Initialisation système"
                    it[AuditLogs.timestamp] = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                    it[AuditLogs.details] = "Initialisation des données par défaut"
                }
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
