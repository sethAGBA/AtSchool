package com.ecolix.atschool.data

import com.ecolix.atschool.api.AcademicSettingsDto
import com.ecolix.atschool.api.GradeLevelDto
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class AcademicSettingsRepository {

    fun getSettings(tenantId: Int): AcademicSettingsDto = transaction {
        AcademicSettings.selectAll().where { AcademicSettings.tenantId eq tenantId }
            .map { it.toDto() }
            .singleOrNull() ?: createDefaultSettings(tenantId)
    }

    fun updateSettings(tenantId: Int, dto: AcademicSettingsDto) = transaction {
        val exists = AcademicSettings.selectAll().where { AcademicSettings.tenantId eq tenantId }.count() > 0
        if (exists) {
            AcademicSettings.update({ AcademicSettings.tenantId eq tenantId }) {
                it[defaultPeriodType] = dto.defaultPeriodType
                it[minGrade] = dto.minGrade
                it[maxGrade] = dto.maxGrade
                it[passingGrade] = dto.passingGrade
                it[attendanceRequiredPercentage] = dto.attendanceRequiredPercentage
                it[allowMidPeriodTransfer] = dto.allowMidPeriodTransfer
                it[autoPromoteStudents] = dto.autoPromoteStudents
                it[decimalPrecision] = dto.decimalPrecision
                it[showRankOnReportCard] = dto.showRankOnReportCard
                it[showClassAverageOnReportCard] = dto.showClassAverageOnReportCard
                it[absencesThresholdAlert] = dto.absencesThresholdAlert
                it[matriculePrefix] = dto.matriculePrefix
                it[updatedAt] = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            }
        } else {
            AcademicSettings.insert {
                it[this.tenantId] = tenantId
                it[defaultPeriodType] = dto.defaultPeriodType
                it[minGrade] = dto.minGrade
                it[maxGrade] = dto.maxGrade
                it[passingGrade] = dto.passingGrade
                it[attendanceRequiredPercentage] = dto.attendanceRequiredPercentage
                it[allowMidPeriodTransfer] = dto.allowMidPeriodTransfer
                it[autoPromoteStudents] = dto.autoPromoteStudents
                it[decimalPrecision] = dto.decimalPrecision
                it[showRankOnReportCard] = dto.showRankOnReportCard
                it[showClassAverageOnReportCard] = dto.showClassAverageOnReportCard
                it[absencesThresholdAlert] = dto.absencesThresholdAlert
                it[matriculePrefix] = dto.matriculePrefix
                it[updatedAt] = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            }
        }
    }

    private fun createDefaultSettings(tenantId: Int): AcademicSettingsDto = transaction {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        AcademicSettings.insert {
            it[this.tenantId] = tenantId
            it[updatedAt] = now
        }
        
        // Ensure grade levels are initialized
        GradeLevelRepository().initializeDefaults(tenantId)
        
        AcademicSettingsDto(
            tenantId = tenantId,
            updatedAt = now.toString()
        )
    }

    private fun ResultRow.toDto() = AcademicSettingsDto(
        id = this[AcademicSettings.id].value,
        tenantId = this[AcademicSettings.tenantId].value,
        defaultPeriodType = this[AcademicSettings.defaultPeriodType],
        minGrade = this[AcademicSettings.minGrade],
        maxGrade = this[AcademicSettings.maxGrade],
        passingGrade = this[AcademicSettings.passingGrade],
        attendanceRequiredPercentage = this[AcademicSettings.attendanceRequiredPercentage],
        allowMidPeriodTransfer = this[AcademicSettings.allowMidPeriodTransfer],
        autoPromoteStudents = this[AcademicSettings.autoPromoteStudents],
        decimalPrecision = this[AcademicSettings.decimalPrecision],
        showRankOnReportCard = this[AcademicSettings.showRankOnReportCard],
        showClassAverageOnReportCard = this[AcademicSettings.showClassAverageOnReportCard],
        absencesThresholdAlert = this[AcademicSettings.absencesThresholdAlert],
        matriculePrefix = this[AcademicSettings.matriculePrefix],
        updatedAt = this[AcademicSettings.updatedAt].toString()
    )
}

class GradeLevelRepository {

    fun getGradeLevels(tenantId: Int): List<GradeLevelDto> = transaction {
        GradeLevels.selectAll().where { GradeLevels.tenantId eq tenantId }
            .map { it.toDto() }
    }
    
    fun initializeDefaults(tenantId: Int) = transaction {
        if (GradeLevels.selectAll().where { GradeLevels.tenantId eq tenantId }.count() == 0L) {
            seedDefaultGradeLevels(tenantId)
        }
    }

    fun updateGradeLevels(tenantId: Int, levels: List<GradeLevelDto>) = transaction {
        // Simple approach: delete all and re-insert
        GradeLevels.deleteWhere { GradeLevels.tenantId eq tenantId }
        levels.forEach { level ->
            GradeLevels.insert {
                it[this.tenantId] = tenantId
                it[name] = level.name
                it[minValue] = level.minValue
                it[maxValue] = level.maxValue
                it[description] = level.description
                it[color] = level.color
            }
        }
    }

    private fun seedDefaultGradeLevels(tenantId: Int) = transaction {
        val defaults = listOf(
            GradeLevelDto(tenantId = tenantId, name = "Excellent", minValue = 19f, maxValue = 20f, description = "Excellent", color = "#10B981"),
            GradeLevelDto(tenantId = tenantId, name = "Très Bien", minValue = 16f, maxValue = 18.99f, description = "Très Bien", color = "#3B82F6"),
            GradeLevelDto(tenantId = tenantId, name = "Bien", minValue = 14f, maxValue = 15.99f, description = "Bien", color = "#6366F1"),
            GradeLevelDto(tenantId = tenantId, name = "Assez Bien", minValue = 12f, maxValue = 13.99f, description = "Assez Bien", color = "#F59E0B"),
            GradeLevelDto(tenantId = tenantId, name = "Passable", minValue = 10f, maxValue = 11.99f, description = "Passable", color = "#8B5CF6"),
            GradeLevelDto(tenantId = tenantId, name = "Insuffisant", minValue = 0f, maxValue = 9.99f, description = "Insuffisant", color = "#EF4444")
        )
        
        defaults.forEach { level ->
            GradeLevels.insert {
                it[this.tenantId] = tenantId
                it[name] = level.name
                it[minValue] = level.minValue
                it[maxValue] = level.maxValue
                it[description] = level.description
                it[color] = level.color
            }
        }
    }

    private fun ResultRow.toDto() = GradeLevelDto(
        id = this[GradeLevels.id].value,
        tenantId = this[GradeLevels.tenantId].value,
        name = this[GradeLevels.name],
        minValue = this[GradeLevels.minValue],
        maxValue = this[GradeLevels.maxValue],
        description = this[GradeLevels.description],
        color = this[GradeLevels.color]
    )
}
