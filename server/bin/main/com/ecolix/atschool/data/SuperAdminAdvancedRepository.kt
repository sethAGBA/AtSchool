package com.ecolix.atschool.data

import com.ecolix.atschool.api.*
import kotlinx.datetime.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class SuperAdminAdvancedRepository {

    // ==================== PAYMENT MANAGEMENT ====================
    
    fun recordPayment(
        tenantId: Int,
        amount: Double,
        paymentMethod: String,
        notes: String? = null
    ): Long = transaction {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        
        SubscriptionPayments.insert {
            it[SubscriptionPayments.tenantId] = tenantId
            it[SubscriptionPayments.amount] = amount
            it[SubscriptionPayments.paymentMethod] = paymentMethod
            it[SubscriptionPayments.paymentDate] = now
            it[SubscriptionPayments.status] = "PAID"
            it[SubscriptionPayments.notes] = notes
            it[createdAt] = now
        }[SubscriptionPayments.id].value
    }

    fun getPaymentHistory(tenantId: Int? = null): List<SubscriptionPaymentDto> = transaction {
        val query = if (tenantId != null) {
            (SubscriptionPayments innerJoin Tenants)
                .selectAll()
                .where { SubscriptionPayments.tenantId eq tenantId }
        } else {
            (SubscriptionPayments innerJoin Tenants).selectAll()
        }
        
        query.orderBy(SubscriptionPayments.paymentDate to SortOrder.DESC)
            .map {
                SubscriptionPaymentDto(
                    id = it[SubscriptionPayments.id].value,
                    tenantId = it[SubscriptionPayments.tenantId].value,
                    tenantName = it[Tenants.name],
                    amount = it[SubscriptionPayments.amount],
                    currency = it[SubscriptionPayments.currency],
                    paymentDate = it[SubscriptionPayments.paymentDate].toString(),
                    paymentMethod = it[SubscriptionPayments.paymentMethod],
                    status = it[SubscriptionPayments.status],
                    invoiceNumber = it[SubscriptionPayments.invoiceNumber],
                    notes = it[SubscriptionPayments.notes],
                    createdAt = it[SubscriptionPayments.createdAt].toString()
                )
            }
    }

    fun updatePaymentStatus(paymentId: Long, status: String, invoiceNumber: String? = null) = transaction {
        SubscriptionPayments.update({ SubscriptionPayments.id eq paymentId }) {
            it[SubscriptionPayments.status] = status
            if (invoiceNumber != null) {
                it[SubscriptionPayments.invoiceNumber] = invoiceNumber
            }
        }
    }

    fun getExpiringSubscriptions(daysThreshold: Int = 30): List<TenantDto> = transaction {
        // Get all active tenants with subscription dates and filter in Kotlin
        Tenants.selectAll()
            .where { 
                (Tenants.subscriptionExpiresAt.isNotNull()) and
                (Tenants.isActive eq true)
            }
            .map {
                TenantDto(
                    id = it[Tenants.id].value,
                    name = it[Tenants.name],
                    code = it[Tenants.code],
                    domain = it[Tenants.domain],
                    isActive = it[Tenants.isActive],
                    createdAt = it[Tenants.createdAt].toString(),
                    adminEmail = Users.selectAll()
                        .where { (Users.tenantId eq it[Tenants.id].value) and (Users.role eq "ADMIN") }
                        .limit(1)
                        .firstOrNull()?.get(Users.email),
                    contactEmail = it[Tenants.contactEmail],
                    contactPhone = it[Tenants.contactPhone],
                    address = it[Tenants.address],
                    subscriptionExpiresAt = it[Tenants.subscriptionExpiresAt]?.toString()
                )
            }
            .filter { tenant ->
                // Filter in Kotlin to avoid SQL type issues
                tenant.subscriptionExpiresAt?.let { expiryDate ->
                    try {
                        val expiry = LocalDate.parse(expiryDate)
                        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
                        val threshold = now.plus(daysThreshold, DateTimeUnit.DAY)
                        expiry <= threshold
                    } catch (e: Exception) {
                        false
                    }
                } ?: false
            }
    }

    // ==================== NOTIFICATION MANAGEMENT ====================
    
    fun createNotification(
        tenantId: Int? = null,
        userId: Long? = null,
        title: String,
        message: String,
        type: String = "INFO",
        priority: String = "NORMAL",
        expiresAt: String? = null
    ): Long = transaction {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        
        Notifications.insert {
            it[Notifications.tenantId] = tenantId
            it[Notifications.userId] = userId
            it[Notifications.title] = title
            it[Notifications.message] = message
            it[Notifications.type] = type
            it[Notifications.priority] = priority
            it[createdAt] = now
            it[Notifications.expiresAt] = expiresAt?.let { date -> 
                LocalDateTime.parse(date.replace(" ", "T"))
            }
        }[Notifications.id].value
    }

    fun getNotifications(tenantId: Int? = null, userId: Long? = null, unreadOnly: Boolean = false): List<NotificationDto> = transaction {
        var query = Notifications.selectAll()
        
        if (tenantId != null) {
            query = query.andWhere { (Notifications.tenantId eq tenantId) or (Notifications.tenantId.isNull()) }
        }
        if (userId != null) {
            query = query.andWhere { (Notifications.userId eq userId) or (Notifications.userId.isNull()) }
        }
        if (unreadOnly) {
            query = query.andWhere { Notifications.isRead eq false }
        }
        
        query.orderBy(Notifications.createdAt to SortOrder.DESC)
            .map {
                NotificationDto(
                    id = it[Notifications.id].value,
                    tenantId = it[Notifications.tenantId]?.value,
                    userId = it[Notifications.userId]?.value,
                    title = it[Notifications.title],
                    message = it[Notifications.message],
                    type = it[Notifications.type],
                    priority = it[Notifications.priority],
                    isRead = it[Notifications.isRead],
                    createdAt = it[Notifications.createdAt].toString(),
                    expiresAt = it[Notifications.expiresAt]?.toString()
                )
            }
    }

    fun markNotificationAsRead(notificationId: Long) = transaction {
        Notifications.update({ Notifications.id eq notificationId }) {
            it[isRead] = true
        }
    }

    // ==================== SUPPORT TICKET MANAGEMENT ====================
    
    fun createTicket(
        tenantId: Int,
        userId: Long,
        subject: String,
        description: String,
        priority: String = "NORMAL"
    ): Long = transaction {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        
        SupportTickets.insert {
            it[SupportTickets.tenantId] = tenantId
            it[SupportTickets.userId] = userId
            it[SupportTickets.subject] = subject
            it[SupportTickets.description] = description
            it[SupportTickets.priority] = priority
            it[createdAt] = now
            it[updatedAt] = now
        }[SupportTickets.id].value
    }

    fun listTickets(status: String? = null, tenantId: Int? = null): List<SupportTicketDto> = transaction {
        val query = (SupportTickets innerJoin Tenants innerJoin Users)
            .select(
                SupportTickets.id,
                SupportTickets.tenantId,
                Tenants.name,
                SupportTickets.userId,
                Users.email,
                SupportTickets.subject,
                SupportTickets.description,
                SupportTickets.status,
                SupportTickets.priority,
                SupportTickets.createdAt,
                SupportTickets.updatedAt,
                SupportTickets.resolvedAt,
                SupportTickets.assignedTo
            )
            .where { SupportTickets.userId eq Users.id }
        
        val filteredQuery = when {
            status != null && tenantId != null -> query.andWhere { 
                (SupportTickets.status eq status) and (SupportTickets.tenantId eq tenantId)
            }
            status != null -> query.andWhere { SupportTickets.status eq status }
            tenantId != null -> query.andWhere { SupportTickets.tenantId eq tenantId }
            else -> query
        }
        
        filteredQuery.orderBy(SupportTickets.createdAt to SortOrder.DESC)
            .map {
                SupportTicketDto(
                    id = it[SupportTickets.id].value,
                    tenantId = it[SupportTickets.tenantId].value,
                    tenantName = it[Tenants.name],
                    userId = it[SupportTickets.userId].value,
                    userEmail = it[Users.email],
                    subject = it[SupportTickets.subject],
                    description = it[SupportTickets.description],
                    status = it[SupportTickets.status],
                    priority = it[SupportTickets.priority],
                    createdAt = it[SupportTickets.createdAt].toString(),
                    updatedAt = it[SupportTickets.updatedAt].toString(),
                    resolvedAt = it[SupportTickets.resolvedAt]?.toString(),
                    assignedTo = it[SupportTickets.assignedTo]?.value
                )
            }
    }

    fun updateTicket(ticketId: Long, status: String? = null, assignedTo: Long? = null) = transaction {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        
        SupportTickets.update({ SupportTickets.id eq ticketId }) {
            it[updatedAt] = now
            if (status != null) {
                it[SupportTickets.status] = status
                if (status == "RESOLVED" || status == "CLOSED") {
                    it[resolvedAt] = now
                }
            }
            if (assignedTo != null) {
                it[SupportTickets.assignedTo] = assignedTo
            }
        }
    }

    // ==================== PERMISSION MANAGEMENT ====================
    
    fun grantPermission(userId: Long, permission: String, grantedBy: Long): Long = transaction {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        
        AdminPermissions.insert {
            it[AdminPermissions.userId] = userId
            it[AdminPermissions.permission] = permission
            it[grantedAt] = now
            it[AdminPermissions.grantedBy] = grantedBy
        }[AdminPermissions.id].value
    }

    fun revokePermission(userId: Long, permission: String) = transaction {
        val userIdToRevoke = userId
        val permissionToRevoke = permission
        AdminPermissions.deleteWhere { 
            (AdminPermissions.userId eq userIdToRevoke) and (AdminPermissions.permission eq permissionToRevoke)
        }
    }

    fun getUserPermissions(userId: Long): List<String> = transaction {
        AdminPermissions.selectAll()
            .where { AdminPermissions.userId eq userId }
            .map { it[AdminPermissions.permission] }
    }

    fun hasPermission(userId: Long, permission: String): Boolean = transaction {
        AdminPermissions.selectAll()
            .where { (AdminPermissions.userId eq userId) and (AdminPermissions.permission eq permission) }
            .count() > 0
    }

    fun listAdminPermissions(): List<AdminPermissionDto> = transaction {
        (AdminPermissions innerJoin Users)
            .select(
                AdminPermissions.id,
                AdminPermissions.userId,
                Users.email,
                AdminPermissions.permission,
                AdminPermissions.grantedAt,
                AdminPermissions.grantedBy
            )
            .where { AdminPermissions.userId eq Users.id }
            .map {
                AdminPermissionDto(
                    id = it[AdminPermissions.id].value,
                    userId = it[AdminPermissions.userId].value,
                    userEmail = it[Users.email],
                    permission = it[AdminPermissions.permission],
                    grantedAt = it[AdminPermissions.grantedAt].toString(),
                    grantedBy = it[AdminPermissions.grantedBy].value
                )
            }
    }

    // ==================== ANALYTICS ====================
    
    fun getGrowthMetrics(startDate: String, endDate: String): GrowthMetricsDto = transaction {
        // Simplified implementation - count all schools and students for now
        val totalSchools = Tenants.selectAll().count().toInt()
        val totalStudents = Eleves.selectAll().count().toInt()
        
        // Calculate total revenue from paid subscriptions
        val revenue = SubscriptionPayments.selectAll()
            .where { SubscriptionPayments.status eq "PAID" }
            .sumOf { it[SubscriptionPayments.amount] }
        
        GrowthMetricsDto(
            startDate = startDate,
            endDate = endDate,
            newSchools = totalSchools,
            newStudents = totalStudents,
            totalRevenue = revenue,
            dataPoints = emptyList() // TODO: Implement daily breakdown with proper date filtering
        )
    }

    fun getRevenueByPeriod(period: String): List<RevenueDataPoint> = transaction {
        // Simplified implementation - would need proper date grouping
        val payments = SubscriptionPayments.selectAll()
            .where { SubscriptionPayments.status eq "PAID" }
            .groupBy { it[SubscriptionPayments.paymentDate].toString().take(7) } // Group by month
        
        payments.map { (month, rows) ->
            RevenueDataPoint(
                period = month,
                amount = rows.sumOf { it[SubscriptionPayments.amount] },
                schoolCount = rows.distinctBy { it[SubscriptionPayments.tenantId] }.size
            )
        }
    }

    fun getSchoolActivityRate(): List<SchoolActivityDto> = transaction {
        Tenants.selectAll().map { tenant ->
            val tenantId = tenant[Tenants.id].value
            val studentCount = Eleves.selectAll().where { Eleves.tenantId eq tenantId }.count().toInt()
            val userCount = Users.selectAll().where { Users.tenantId eq tenantId }.count().toInt()
            
            SchoolActivityDto(
                tenantId = tenantId,
                tenantName = tenant[Tenants.name],
                lastLoginDate = null, // Would need login tracking
                activeUsers = userCount,
                totalStudents = studentCount,
                activityScore = if (studentCount > 0) (userCount.toDouble() / studentCount * 100).coerceIn(0.0, 100.0) else 0.0
            )
        }
    }
}
