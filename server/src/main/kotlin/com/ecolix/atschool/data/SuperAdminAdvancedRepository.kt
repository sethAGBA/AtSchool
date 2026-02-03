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
        val year = now.year
        
        // Count payments for this year for the invoice number
        val count = SubscriptionPayments.selectAll()
            .where { SubscriptionPayments.createdAt.greaterEq(LocalDateTime(year, 1, 1, 0, 0)) }
            .count()
        
        val autoInvoiceNumber = "INV-$year-${(count + 1).toString().padStart(4, '0')}"

        SubscriptionPayments.insert {
            it[SubscriptionPayments.tenantId] = tenantId
            it[SubscriptionPayments.amount] = amount
            it[SubscriptionPayments.paymentMethod] = paymentMethod
            it[SubscriptionPayments.paymentDate] = now
            it[SubscriptionPayments.status] = "PENDING"
            it[SubscriptionPayments.invoiceNumber] = autoInvoiceNumber
            it[SubscriptionPayments.notes] = notes
            it[createdAt] = now
        }[SubscriptionPayments.id].value
    }

    fun updatePayment(
        paymentId: Long,
        amount: Double? = null,
        paymentMethod: String? = null,
        status: String? = null,
        notes: String? = null,
        invoiceNumber: String? = null
    ) = transaction {
        SubscriptionPayments.update({ SubscriptionPayments.id eq paymentId }) {
            if (amount != null) it[SubscriptionPayments.amount] = amount
            if (paymentMethod != null) it[SubscriptionPayments.paymentMethod] = paymentMethod
            if (status != null) it[SubscriptionPayments.status] = status
            if (notes != null) it[SubscriptionPayments.notes] = notes
            if (invoiceNumber != null) it[SubscriptionPayments.invoiceNumber] = invoiceNumber
        }
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



    // ==================== PLAN MANAGEMENT ====================

    fun listPlans(): List<SubscriptionPlanDto> = transaction {
        SubscriptionPlans.selectAll().orderBy(SubscriptionPlans.price to SortOrder.ASC).map {
            SubscriptionPlanDto(
                id = it[SubscriptionPlans.id].value,
                name = it[SubscriptionPlans.name],
                price = it[SubscriptionPlans.price],
                currency = it[SubscriptionPlans.currency],
                description = it[SubscriptionPlans.description],
                isPopular = it[SubscriptionPlans.isPopular],
                createdAt = it[SubscriptionPlans.createdAt].toString()
            )
        }
    }

    fun createPlan(request: CreatePlanRequest): Int = transaction {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        SubscriptionPlans.insert {
            it[name] = request.name
            it[price] = request.price
            it[description] = request.description
            it[isPopular] = request.isPopular
            it[createdAt] = now
        }[SubscriptionPlans.id].value
    }

    fun updatePlan(
        id: Int,
        name: String? = null,
        price: Double? = null,
        description: String? = null,
        isPopular: Boolean? = null
    ) = transaction {
        SubscriptionPlans.update({ SubscriptionPlans.id eq id }) {
            if (name != null) it[SubscriptionPlans.name] = name
            if (price != null) it[SubscriptionPlans.price] = price
            if (description != null) it[SubscriptionPlans.description] = description
            if (isPopular != null) it[SubscriptionPlans.isPopular] = isPopular
        }
    }

    fun deletePlan(id: Int) = transaction {
        SubscriptionPlans.deleteWhere { SubscriptionPlans.id eq id }
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
        val start = try { LocalDate.parse(startDate) } catch (e: Exception) { Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.minus(30, DateTimeUnit.DAY) }
        val end = try { LocalDate.parse(endDate) } catch (e: Exception) { Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date }
        
        // Total schools added in range
        val newSchools = Tenants.selectAll()
            .where { (Tenants.createdAt greaterEq start) and (Tenants.createdAt lessEq end) }
            .count().toInt()

        // Total students inscribed in range
        val newStudents = Inscriptions.selectAll()
            .where { (Inscriptions.dateInscription greaterEq start) and (Inscriptions.dateInscription lessEq end) }
            .count().toInt()
        
        // Total revenue from PAID subscription payments in range
        // Since paymentDate is LocalDateTime, we compare with start of start day and end of end day
        val startDateTime = start.atTime(0, 0)
        val endDateTime = end.atTime(23, 59, 59)
        
        val totalRevenue = SubscriptionPayments.selectAll()
            .where { 
                (SubscriptionPayments.status eq "PAID") and 
                (SubscriptionPayments.paymentDate greaterEq startDateTime) and 
                (SubscriptionPayments.paymentDate lessEq endDateTime)
            }
            .sumOf { it[SubscriptionPayments.amount] }
        
        // Generate data points (daily for now)
        val dataPoints = mutableListOf<GrowthDataPoint>()
        var current = start
        while (current <= end) {
            val cStart = current.atTime(0,0)
            val cEnd = current.atTime(23,59,59)
            
            val schools = Tenants.selectAll().where { Tenants.createdAt eq current }.count().toInt()
            val students = Inscriptions.selectAll().where { Inscriptions.dateInscription eq current }.count().toInt()
            val revenue = SubscriptionPayments.selectAll()
                .where { (SubscriptionPayments.status eq "PAID") and (SubscriptionPayments.paymentDate greaterEq cStart) and (SubscriptionPayments.paymentDate lessEq cEnd) }
                .sumOf { it[SubscriptionPayments.amount] }
            
            dataPoints.add(GrowthDataPoint(current.toString(), schools, students, revenue))
            current = current.plus(1, DateTimeUnit.DAY)
        }
        
        GrowthMetricsDto(
            startDate = startDate,
            endDate = endDate,
            newSchools = newSchools,
            newStudents = newStudents,
            totalRevenue = totalRevenue,
            dataPoints = dataPoints
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
        // Optimized: Single query with joins and aggregation would be better, 
        // but let's keep it simple and safe for now while fixing the immediate loop.
        val tenants = Tenants.selectAll().toList()
        
        tenants.map { tenant ->
            val tId = tenant[Tenants.id].value
            val studentCount = (Inscriptions innerJoin Eleves).selectAll().where { Eleves.tenantId eq tId }.count().toInt()
            val userCount = Users.selectAll().where { Users.tenantId eq tId }.count().toInt()
            
            SchoolActivityDto(
                tenantId = tId,
                tenantName = tenant[Tenants.name],
                lastLoginDate = null,
                activeUsers = userCount,
                totalStudents = studentCount,
                activityScore = if (studentCount > 0) (userCount.toDouble() / studentCount * 100).coerceIn(0.0, 100.0) else 0.0
            )
        }
    }
}
