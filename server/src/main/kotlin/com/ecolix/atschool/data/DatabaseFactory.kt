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

    fun init(config: io.ktor.server.config.ApplicationConfig) {
        val dataSource = createHikariDataSource(config)
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

    private fun createHikariDataSource(config: io.ktor.server.config.ApplicationConfig): HikariDataSource {
        val driver = config.property("driverClassName").getString()
        val url = config.property("jdbcUrl").getString()
        val user = config.property("user").getString()
        val pwd = config.property("password").getString()
        val poolSize = config.property("maximumPoolSize").getString().toInt()

        val hikariConfig = HikariConfig().apply {
            driverClassName = driver
            jdbcUrl = url
            username = user
            password = pwd
            maximumPoolSize = poolSize
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }
        return HikariDataSource(hikariConfig)
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
