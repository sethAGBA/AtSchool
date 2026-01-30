package com.ecolix.atschool.data

import com.ecolix.atschool.security.PasswordUtils
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll
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
                    it[createdAt] = today // Exposed Date column usually takes LocalDate
                }

                logger.info("Created Default Tenant: $tenantId")

                // 2. Create Admin User
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
            } else {
                logger.info("Database already seeded. Skipping.")
            }
        }
    }
}
