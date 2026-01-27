package com.ecolix.data.models

enum class StatsTab {
    OVERVIEW,
    SUCCESS_RATE,
    ATTENDANCE,
    SUBJECT_PERFORMANCE,
    ENROLLMENT_EVOLUTION,
    PAYMENT_DETAILS,
    STAFF_STATS
}

data class SuccessRateRow(
    val level: String,
    val totalStudents: Int,
    val passed: Int,
    val failed: Int,
    val average: Float
)

data class AttendanceRow(
    val month: String,
    val totalDays: Int,
    val presentDays: Int,
    val absentDays: Int,
    val rate: Float
)

data class SubjectPerformanceRow(
    val subject: String,
    val teacher: String,
    val studentsCount: Int,
    val average: Float,
    val passRate: Float
)

data class EnrollmentEvolutionRow(
    val year: String,
    val totalStudents: Int,
    val boys: Int,
    val girls: Int,
    val variation: Float
)

data class PaymentDetailsRow(
    val status: String,
    val count: Int,
    val amount: String,
    val percentage: Float
)

data class StaffStatsRow(
    val department: String,
    val totalStaff: Int,
    val permanent: Int,
    val contract: Int,
    val averageSalary: String
)
