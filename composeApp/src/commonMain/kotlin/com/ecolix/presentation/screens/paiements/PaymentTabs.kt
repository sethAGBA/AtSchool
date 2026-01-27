package com.ecolix.presentation.screens.paiements

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ecolix.data.models.*
import com.ecolix.presentation.components.DonutChart

@Composable
fun PaymentOverviewTab(
    state: PaymentsUiState,
    colors: DashboardColors,
    isCompact: Boolean,
    onSelectStudent: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Charts Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Payments by Type Chart
                Card(
                    colors = CardDefaults.cardColors(containerColor = colors.card),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            "Répartition par Type",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = colors.textPrimary
                        )

                        if (state.paymentsByType.isNotEmpty()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                DonutChart(
                                    data = state.paymentsByType.map { it.amount.toFloat() },
                                    colors = state.paymentsByType.map { it.color },
                                    modifier = Modifier.size(180.dp),
                                    centerText = "${state.paymentsByType.sumOf { it.amount }.toLong().formatCurrency()}",
                                    centerSubText = "CFA",
                                    textColor = colors.textPrimary
                                )

                                Column(
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    state.paymentsByType.take(5).forEach { typeData ->
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(12.dp)
                                                    .clip(RoundedCornerShape(2.dp))
                                                    .background(typeData.color)
                                            )
                                            Column {
                                                Text(
                                                    typeData.type.toFrench(),
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = colors.textPrimary
                                                )
                                                Text(
                                                    "${typeData.amount.toLong().formatCurrency()} CFA",
                                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                                    color = colors.textMuted
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Payment Status Distribution
                Card(
                    colors = CardDefaults.cardColors(containerColor = colors.card),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            "Statut des Paiements",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = colors.textPrimary
                        )

                        val statusData = listOf(
                            Triple("Payé intégralement", state.statistics.paidStudents, PaymentStatus.PAID.toColor()),
                            Triple("Paiement partiel", state.statistics.partialStudents, PaymentStatus.PARTIAL.toColor()),
                            Triple("En attente", state.statistics.pendingStudents, PaymentStatus.PENDING.toColor()),
                            Triple("En retard", state.statistics.overdueStudents, PaymentStatus.OVERDUE.toColor())
                        )

                        DonutChart(
                            data = statusData.map { it.second.toFloat() },
                            colors = statusData.map { it.third },
                            modifier = Modifier.size(180.dp).align(Alignment.CenterHorizontally),
                            centerText = "${state.statistics.numberOfStudents}",
                            centerSubText = "Élèves",
                            textColor = colors.textPrimary
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            statusData.forEach { (label, count, color) ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(12.dp)
                                                .clip(RoundedCornerShape(2.dp))
                                                .background(color)
                                        )
                                        Text(
                                            label,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = colors.textPrimary
                                        )
                                    }
                                    Text(
                                        "$count",
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                        color = colors.textMuted
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Recent Payments
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = colors.card),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Paiements Récents",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = colors.textPrimary
                        )
                        TextButton(onClick = { /* Navigate to history */ }) {
                            Text("Voir tout")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Table Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(colors.divider.copy(alpha = 0.3f))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Élève", Modifier.weight(1.5f), style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = colors.textPrimary)
                        Text("Classe", Modifier.weight(0.8f), style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = colors.textPrimary)
                        Text("Type", Modifier.weight(1f), style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = colors.textPrimary)
                        Text("Montant", Modifier.weight(1f), style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = colors.textPrimary)
                        Text("Date", Modifier.weight(0.8f), style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = colors.textPrimary)
                        Text("Statut", Modifier.weight(0.8f), style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = colors.textPrimary)
                    }

                    HorizontalDivider(color = colors.divider)

                    // Recent payments list
                    state.payments.take(10).forEach { payment ->
                        Column {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { /* View details */ }
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(payment.studentName, Modifier.weight(1.5f), style = MaterialTheme.typography.bodyMedium, color = colors.textPrimary)
                                Text(payment.classroom, Modifier.weight(0.8f), style = MaterialTheme.typography.bodyMedium, color = colors.textMuted)
                                Text(payment.paymentType.toFrench(), Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium, color = colors.textMuted)
                                Text("${payment.amount.toLong().formatCurrency()} CFA", Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = colors.textPrimary)
                                Text(payment.date, Modifier.weight(0.8f), style = MaterialTheme.typography.bodySmall, color = colors.textMuted)
                                Box(
                                    modifier = Modifier
                                        .weight(0.8f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(payment.status.toColor().copy(alpha = 0.1f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        payment.status.toFrench(),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = payment.status.toColor()
                                    )
                                }
                            }
                            HorizontalDivider(color = colors.divider.copy(alpha = 0.5f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StudentPaymentsTab(
    state: PaymentsUiState,
    colors: DashboardColors,
    isCompact: Boolean,
    onSelectStudent: (String) -> Unit
) {
    val filteredStudents = remember(state.searchQuery, state.selectedClassroom, state.selectedStatus) {
        state.studentPayments.filter { student ->
            val matchSearch = state.searchQuery.isEmpty() ||
                    student.studentName.contains(state.searchQuery, ignoreCase = true) ||
                    student.matricule.contains(state.searchQuery, ignoreCase = true)
            val matchClassroom = state.selectedClassroom == null || student.classroom == state.selectedClassroom
            val matchStatus = state.selectedStatus == null || student.status == state.selectedStatus

            matchSearch && matchClassroom && matchStatus
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                "Élèves trouvés (${filteredStudents.size})",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = colors.textPrimary
            )
        }

        items(filteredStudents) { student ->
            StudentPaymentCard(
                student = student,
                colors = colors,
                onClick = { onSelectStudent(student.studentId) }
            )
        }

        if (filteredStudents.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Aucun élève trouvé.", color = colors.textMuted)
                }
            }
        }
    }
}

@Composable
private fun StudentPaymentCard(
    student: StudentPaymentSummary,
    colors: DashboardColors,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = colors.card),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Student Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    student.studentName,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = colors.textPrimary
                )
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        student.matricule,
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textMuted
                    )
                    Text(
                        student.classroom,
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textMuted
                    )
                }
            }

            // Payment Summary
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    "${student.totalPaid.toLong().formatCurrency()} / ${student.totalDue.toLong().formatCurrency()} CFA",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = colors.textPrimary
                )
                if (student.totalPending > 0) {
                    Text(
                        "Reste: ${student.totalPending.toLong().formatCurrency()} CFA",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFEF4444)
                    )
                }
            }

            // Status Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(student.status.toColor().copy(alpha = 0.1f))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    student.status.toFrench(),
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = student.status.toColor()
                )
            }

            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = colors.textMuted
            )
        }
    }
}

@Composable
fun PaymentHistoryTab(
    state: PaymentsUiState,
    colors: DashboardColors,
    isCompact: Boolean
) {
    val filteredPayments = remember(state.searchQuery, state.selectedClassroom, state.selectedPaymentType) {
        state.payments.filter { payment ->
            val matchSearch = state.searchQuery.isEmpty() ||
                    payment.studentName.contains(state.searchQuery, ignoreCase = true) ||
                    payment.reference.contains(state.searchQuery, ignoreCase = true)
            val matchClassroom = state.selectedClassroom == null || payment.classroom == state.selectedClassroom
            val matchType = state.selectedPaymentType == null || payment.paymentType == state.selectedPaymentType

            matchSearch && matchClassroom && matchType
        }
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = colors.card),
        modifier = Modifier.fillMaxSize()
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                "Historique des Paiements (${filteredPayments.size})",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = colors.textPrimary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Table Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.divider.copy(alpha = 0.3f))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Référence", Modifier.weight(0.8f), style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = colors.textPrimary)
                Text("Élève", Modifier.weight(1.5f), style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = colors.textPrimary)
                Text("Type", Modifier.weight(1f), style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = colors.textPrimary)
                Text("Montant", Modifier.weight(1f), style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = colors.textPrimary)
                Text("Méthode", Modifier.weight(1f), style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = colors.textPrimary)
                Text("Date", Modifier.weight(0.8f), style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = colors.textPrimary)
                Text("Reçu par", Modifier.weight(1f), style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = colors.textPrimary)
            }

            HorizontalDivider(color = colors.divider)

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                items(filteredPayments) { payment ->
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { /* View details */ }
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(payment.reference, Modifier.weight(0.8f), style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold), color = colors.textLink)
                            Text(payment.studentName, Modifier.weight(1.5f), style = MaterialTheme.typography.bodyMedium, color = colors.textPrimary)
                            Text(payment.paymentType.toFrench(), Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium, color = colors.textMuted)
                            Text("${payment.amount.toLong().formatCurrency()} CFA", Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = colors.textPrimary)
                            Text(payment.paymentMethod.toFrench(), Modifier.weight(1f), style = MaterialTheme.typography.bodySmall, color = colors.textMuted)
                            Text(payment.date, Modifier.weight(0.8f), style = MaterialTheme.typography.bodySmall, color = colors.textMuted)
                            Text(payment.receivedBy, Modifier.weight(1f), style = MaterialTheme.typography.bodySmall, color = colors.textMuted)
                        }
                        HorizontalDivider(color = colors.divider.copy(alpha = 0.5f))
                    }
                }

                if (filteredPayments.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Aucun paiement trouvé.", color = colors.textMuted)
                        }
                    }
                }
            }
        }
    }
}
