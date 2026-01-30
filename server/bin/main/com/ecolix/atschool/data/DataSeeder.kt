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
                    it[AuditLogs.tenantId] = tenantId
                    it[userId] = Users.selectAll().where { Users.email eq adminEmail }.single()[Users.id]
                    it[action] = "Initialisation système"
                    it[timestamp] = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                    it[details] = "Initialisation des données par défaut"
                }
            } else {
                logger.info("Database already seeded. Skipping.")
            }
        }
    }
}
