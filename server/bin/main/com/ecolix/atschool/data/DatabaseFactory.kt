package com.ecolix.atschool.data

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory

object DatabaseFactory {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun init() {
        val dataSource = createHikariDataSource()
        Database.connect(dataSource)
        
        // Run Flyway migrations
        runFlyway(dataSource)

        // Initialize tables if they don't exist (optional fallback)
        transaction {
            SchemaUtils.create(
                Tenants,
                Establishments,
                Users,
                AnneesScolaires,
                Cycles,
                Niveaux,
                Classes,
                Eleves,
                Inscriptions,
                Matieres,
                Evaluations,
                Notes,
                Paiements,
                AuditLogs
            )
        }
    }

    private fun createHikariDataSource(): HikariDataSource {
        val config = HikariConfig().apply {
            driverClassName = "org.postgresql.Driver"
            jdbcUrl = System.getenv("JDBC_URL") ?: "jdbc:postgresql://localhost:5432/atschool"
            username = System.getenv("DB_USER") ?: "postgres"
            password = System.getenv("DB_PASSWORD") ?: "postgres"
            maximumPoolSize = 10
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }
        return HikariDataSource(config)
    }

    private fun runFlyway(dataSource: HikariDataSource) {
        val flyway = Flyway.configure()
            .dataSource(dataSource)
            .load()
        try {
            flyway.migrate()
        } catch (e: Exception) {
            logger.error("Flyway migration failed", e)
            throw e
        }
    }
}
