package com.ecolix.atschool.data

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import kotlinx.serialization.Serializable

@Serializable
data class DashboardStatsResponse(
    val totalStudents: Int,
    val totalStaff: Int,
    val totalClasses: Int,
    val totalRevenue: Double,
    val recentActivities: List<ActivityDto>
)

@Serializable
data class ActivityDto(
    val title: String,
    val subtitle: String,
    val time: String,
    val type: String // PAYMENT, ENROLLMENT, DISCIPLINE, etc.
)

class DashboardRepository {
    fun getDashboardStats(tenantId: Int): DashboardStatsResponse = transaction {
        val studentsCount = Eleves.selectAll().where { (Eleves.tenantId eq tenantId) and (Eleves.deleted eq false) }.count().toInt()
        val classesCount = Classes.selectAll().where { Classes.tenantId eq tenantId }.count().toInt()
        val staffCount = StaffTable.selectAll().where { (StaffTable.tenantId eq tenantId) and (StaffTable.isDeleted eq false) }.count().toInt()
        
        val revenue = Paiements.selectAll().where { Paiements.tenantId eq tenantId }
            .sumOf { it[Paiements.montant] }

        // Fetch recent audit logs as activities
        val activities = AuditLogs.selectAll().where { AuditLogs.tenantId eq tenantId }
            .orderBy(AuditLogs.timestamp to SortOrder.DESC)
            .limit(5)
            .map {
                ActivityDto(
                    title = it[AuditLogs.action],
                    subtitle = it[AuditLogs.details] ?: "",
                    time = it[AuditLogs.timestamp].toString().substringAfter("T").substringBefore("."),
                    type = "SYSTEM"
                )
            }

        DashboardStatsResponse(
            totalStudents = studentsCount,
            totalStaff = staffCount,
            totalClasses = classesCount,
            totalRevenue = revenue,
            recentActivities = activities
        )
    }
}
