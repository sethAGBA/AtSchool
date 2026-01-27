package com.ecolix.data.models

import androidx.compose.ui.graphics.Color

// Enums
enum class PaymentViewMode {
    OVERVIEW,
    STUDENT_PAYMENTS,
    PAYMENT_HISTORY,
    ADD_PAYMENT,
    PAYMENT_DETAILS
}

enum class PaymentStatus {
    PAID,
    PARTIAL,
    PENDING,
    OVERDUE
}

enum class PaymentMethod {
    CASH,
    BANK_TRANSFER,
    MOBILE_MONEY,
    CHECK,
    OTHER
}

enum class PaymentType {
    TUITION,
    REGISTRATION,
    EXAM_FEE,
    TRANSPORT,
    CANTEEN,
    UNIFORM,
    BOOKS,
    ACTIVITY,
    OTHER
}

// Data Classes
data class Payment(
    val id: String,
    val studentId: String,
    val studentName: String,
    val classroom: String,
    val amount: Double,
    val paymentType: PaymentType,
    val paymentMethod: PaymentMethod,
    val status: PaymentStatus,
    val date: String,
    val dueDate: String? = null,
    val reference: String,
    val notes: String? = null,
    val receivedBy: String,
    val academicYear: String
)

data class StudentPaymentSummary(
    val studentId: String,
    val studentName: String,
    val matricule: String,
    val classroom: String,
    val totalDue: Double,
    val totalPaid: Double,
    val totalPending: Double,
    val status: PaymentStatus,
    val lastPaymentDate: String?,
    val payments: List<Payment> = emptyList()
)

data class PaymentStatistics(
    val totalExpected: Double,
    val totalCollected: Double,
    val totalPending: Double,
    val totalOverdue: Double,
    val collectionRate: Float,
    val numberOfStudents: Int,
    val paidStudents: Int,
    val partialStudents: Int,
    val pendingStudents: Int,
    val overdueStudents: Int
)

data class PaymentsByType(
    val type: PaymentType,
    val amount: Double,
    val count: Int,
    val color: Color
)

data class PaymentsByMonth(
    val month: String,
    val amount: Double,
    val count: Int
)

// UI State
data class PaymentsUiState(
    val viewMode: PaymentViewMode = PaymentViewMode.OVERVIEW,
    val colors: DashboardColors = DashboardColors.light(),
    val searchQuery: String = "",
    val selectedClassroom: String? = null,
    val selectedStatus: PaymentStatus? = null,
    val selectedPaymentType: PaymentType? = null,
    val selectedStudentId: String? = null,
    val selectedPaymentId: String? = null,
    val payments: List<Payment> = emptyList(),
    val studentPayments: List<StudentPaymentSummary> = emptyList(),
    val statistics: PaymentStatistics = PaymentStatistics(
        totalExpected = 0.0,
        totalCollected = 0.0,
        totalPending = 0.0,
        totalOverdue = 0.0,
        collectionRate = 0f,
        numberOfStudents = 0,
        paidStudents = 0,
        partialStudents = 0,
        pendingStudents = 0,
        overdueStudents = 0
    ),
    val paymentsByType: List<PaymentsByType> = emptyList(),
    val paymentsByMonth: List<PaymentsByMonth> = emptyList(),
    val classrooms: List<String> = emptyList(),
    val currentYear: String = "2024-2025",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
