package com.ecolix.presentation.screens.paiements

import androidx.compose.ui.graphics.Color
import com.ecolix.data.models.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PaymentsScreenModel {
    private val _state = MutableStateFlow(PaymentsUiState())
    val state: StateFlow<PaymentsUiState> = _state.asStateFlow()

    init {
        loadMockData()
    }

    fun onDarkModeChange(isDarkMode: Boolean) {
        val colors = if (isDarkMode) {
            DashboardColors(
                background = Color(0xFF111827),
                card = Color(0xFF1F2937),
                textPrimary = Color.White,
                textMuted = Color(0xFF9CA3AF),
                divider = Color(0xFF374151),
                textLink = Color(0xFF3B82F6)
            )
        } else {
            DashboardColors(
                background = Color(0xFFF3F4F6),
                card = Color.White,
                textPrimary = Color(0xFF111827),
                textMuted = Color(0xFF6B7280),
                divider = Color(0xFFE5E7EB),
                textLink = Color(0xFF2563EB)
            )
        }
        _state.value = _state.value.copy(colors = colors)
    }

    fun onViewModeChange(mode: PaymentViewMode) {
        _state.value = _state.value.copy(viewMode = mode)
    }

    fun onSearchQueryChange(query: String) {
        _state.value = _state.value.copy(searchQuery = query)
    }

    fun onClassroomChange(classroom: String?) {
        _state.value = _state.value.copy(selectedClassroom = classroom)
    }

    fun onStatusChange(status: PaymentStatus?) {
        _state.value = _state.value.copy(selectedStatus = status)
    }

    fun onPaymentTypeChange(type: PaymentType?) {
        _state.value = _state.value.copy(selectedPaymentType = type)
    }

    fun onSelectStudent(studentId: String) {
        _state.value = _state.value.copy(
            selectedStudentId = studentId,
            viewMode = PaymentViewMode.PAYMENT_DETAILS
        )
    }

    fun onSelectPayment(paymentId: String) {
        _state.value = _state.value.copy(selectedPaymentId = paymentId)
    }

    fun updateState(newState: PaymentsUiState) {
        _state.value = newState
    }

    private fun loadMockData() {
        val mockPayments = generateMockPayments()
        val mockStudentPayments = generateMockStudentPayments()
        val statistics = calculateStatistics(mockStudentPayments)
        val paymentsByType = calculatePaymentsByType(mockPayments)
        val paymentsByMonth = calculatePaymentsByMonth(mockPayments)

        _state.value = _state.value.copy(
            payments = mockPayments,
            studentPayments = mockStudentPayments,
            statistics = statistics,
            paymentsByType = paymentsByType,
            paymentsByMonth = paymentsByMonth,
            classrooms = listOf("6ème A", "6ème B", "5ème A", "5ème B", "4ème A", "3ème A", "2nde S", "1ère S", "Tle S")
        )
    }

    private fun generateMockPayments(): List<Payment> {
        val payments = mutableListOf<Payment>()
        val students = listOf(
            Triple("Amadou Diallo", "6ème A", "MAT001"),
            Triple("Fatou Sow", "6ème A", "MAT002"),
            Triple("Moussa Kane", "5ème A", "MAT003"),
            Triple("Aissatou Ndiaye", "4ème A", "MAT004"),
            Triple("Ousmane Fall", "3ème A", "MAT005")
        )

        students.forEachIndexed { index, (name, classroom, matricule) ->
            // Frais de scolarité
            payments.add(
                Payment(
                    id = "PAY${1000 + index * 3}",
                    studentId = "STU${index + 1}",
                    studentName = name,
                    classroom = classroom,
                    amount = 150000.0,
                    paymentType = PaymentType.TUITION,
                    paymentMethod = PaymentMethod.BANK_TRANSFER,
                    status = if (index % 3 == 0) PaymentStatus.PAID else PaymentStatus.PARTIAL,
                    date = "2024-10-15",
                    dueDate = "2024-09-30",
                    reference = "REF${1000 + index * 3}",
                    receivedBy = "M. Diop",
                    academicYear = "2024-2025"
                )
            )

            // Frais d'inscription
            payments.add(
                Payment(
                    id = "PAY${1001 + index * 3}",
                    studentId = "STU${index + 1}",
                    studentName = name,
                    classroom = classroom,
                    amount = 25000.0,
                    paymentType = PaymentType.REGISTRATION,
                    paymentMethod = PaymentMethod.CASH,
                    status = PaymentStatus.PAID,
                    date = "2024-09-05",
                    dueDate = "2024-09-01",
                    reference = "REF${1001 + index * 3}",
                    receivedBy = "Mme Sarr",
                    academicYear = "2024-2025"
                )
            )
        }

        return payments
    }

    private fun generateMockStudentPayments(): List<StudentPaymentSummary> {
        return listOf(
            StudentPaymentSummary(
                studentId = "STU1",
                studentName = "Amadou Diallo",
                matricule = "MAT001",
                classroom = "6ème A",
                totalDue = 200000.0,
                totalPaid = 200000.0,
                totalPending = 0.0,
                status = PaymentStatus.PAID,
                lastPaymentDate = "2024-10-15"
            ),
            StudentPaymentSummary(
                studentId = "STU2",
                studentName = "Fatou Sow",
                matricule = "MAT002",
                classroom = "6ème A",
                totalDue = 200000.0,
                totalPaid = 125000.0,
                totalPending = 75000.0,
                status = PaymentStatus.PARTIAL,
                lastPaymentDate = "2024-10-15"
            ),
            StudentPaymentSummary(
                studentId = "STU3",
                studentName = "Moussa Kane",
                matricule = "MAT003",
                classroom = "5ème A",
                totalDue = 200000.0,
                totalPaid = 125000.0,
                totalPending = 75000.0,
                status = PaymentStatus.PARTIAL,
                lastPaymentDate = "2024-10-15"
            ),
            StudentPaymentSummary(
                studentId = "STU4",
                studentName = "Aissatou Ndiaye",
                matricule = "MAT004",
                classroom = "4ème A",
                totalDue = 200000.0,
                totalPaid = 0.0,
                totalPending = 200000.0,
                status = PaymentStatus.PENDING,
                lastPaymentDate = null
            ),
            StudentPaymentSummary(
                studentId = "STU5",
                studentName = "Ousmane Fall",
                matricule = "MAT005",
                classroom = "3ème A",
                totalDue = 200000.0,
                totalPaid = 0.0,
                totalPending = 200000.0,
                status = PaymentStatus.OVERDUE,
                lastPaymentDate = null
            )
        )
    }

    private fun calculateStatistics(studentPayments: List<StudentPaymentSummary>): PaymentStatistics {
        val totalExpected = studentPayments.sumOf { it.totalDue }
        val totalCollected = studentPayments.sumOf { it.totalPaid }
        val totalPending = studentPayments.sumOf { it.totalPending }
        val totalOverdue = studentPayments.filter { it.status == PaymentStatus.OVERDUE }.sumOf { it.totalPending }

        return PaymentStatistics(
            totalExpected = totalExpected,
            totalCollected = totalCollected,
            totalPending = totalPending,
            totalOverdue = totalOverdue,
            collectionRate = if (totalExpected > 0) (totalCollected / totalExpected * 100).toFloat() else 0f,
            numberOfStudents = studentPayments.size,
            paidStudents = studentPayments.count { it.status == PaymentStatus.PAID },
            partialStudents = studentPayments.count { it.status == PaymentStatus.PARTIAL },
            pendingStudents = studentPayments.count { it.status == PaymentStatus.PENDING },
            overdueStudents = studentPayments.count { it.status == PaymentStatus.OVERDUE }
        )
    }

    private fun calculatePaymentsByType(payments: List<Payment>): List<PaymentsByType> {
        val grouped = payments.groupBy { it.paymentType }
        return grouped.map { (type, typePayments) ->
            PaymentsByType(
                type = type,
                amount = typePayments.sumOf { it.amount },
                count = typePayments.size,
                color = when (type) {
                    PaymentType.TUITION -> Color(0xFF3B82F6)
                    PaymentType.REGISTRATION -> Color(0xFF10B981)
                    PaymentType.EXAM_FEE -> Color(0xFFF59E0B)
                    PaymentType.TRANSPORT -> Color(0xFFEC4899)
                    PaymentType.CANTEEN -> Color(0xFF8B5CF6)
                    PaymentType.UNIFORM -> Color(0xFF06B6D4)
                    PaymentType.BOOKS -> Color(0xFFF97316)
                    PaymentType.ACTIVITY -> Color(0xFF14B8A6)
                    PaymentType.OTHER -> Color(0xFF6B7280)
                }
            )
        }.sortedByDescending { it.amount }
    }

    private fun calculatePaymentsByMonth(payments: List<Payment>): List<PaymentsByMonth> {
        val months = listOf("Sept", "Oct", "Nov", "Déc", "Jan", "Fév")
        return months.mapIndexed { index, month ->
            val monthPayments = payments.filter { 
                // Simplified: just distribute some payments across months
                index % 2 == 0 || it.paymentType == PaymentType.TUITION
            }
            PaymentsByMonth(
                month = month,
                amount = monthPayments.sumOf { it.amount } / 3,
                count = monthPayments.size
            )
        }
    }
}
