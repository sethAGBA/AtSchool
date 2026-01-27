package com.ecolix.presentation.screens.paiements

import androidx.compose.foundation.background
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

@Composable
fun PaymentDetailsView(
    student: StudentPaymentSummary,
    payments: List<Payment>,
    colors: DashboardColors,
    isCompact: Boolean,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Header with back button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Retour", tint = colors.textPrimary)
                }
                Column {
                    Text(
                        student.studentName,
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = colors.textPrimary
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text(
                            student.matricule,
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.textMuted
                        )
                        Text(
                            student.classroom,
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.textMuted
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(student.status.toColor().copy(alpha = 0.1f))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    student.status.toFrench(),
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = student.status.toColor()
                )
            }
        }

        // Payment Summary Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SummaryCard(
                title = "Total Dû",
                value = "${student.totalDue.toLong().formatCurrency()} CFA",
                icon = Icons.Default.AttachMoney,
                color = Color(0xFF3B82F6),
                colors = colors,
                modifier = Modifier.weight(1f)
            )
            SummaryCard(
                title = "Total Payé",
                value = "${student.totalPaid.toLong().formatCurrency()} CFA",
                icon = Icons.Default.CheckCircle,
                color = Color(0xFF10B981),
                colors = colors,
                modifier = Modifier.weight(1f)
            )
            SummaryCard(
                title = "Reste à Payer",
                value = "${student.totalPending.toLong().formatCurrency()} CFA",
                icon = Icons.Default.Schedule,
                color = if (student.totalPending > 0) Color(0xFFEF4444) else Color(0xFF6B7280),
                colors = colors,
                modifier = Modifier.weight(1f)
            )
        }

        // Payment History
        Card(
            colors = CardDefaults.cardColors(containerColor = colors.card),
            modifier = Modifier.fillMaxWidth().weight(1f)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Historique des Paiements",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = colors.textPrimary
                    )
                    Button(
                        onClick = { /* Add payment */ },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Nouveau Paiement")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (payments.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.Receipt,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = colors.textMuted
                            )
                            Text("Aucun paiement enregistré", color = colors.textMuted)
                        }
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(payments.sortedByDescending { it.date }) { payment ->
                            PaymentHistoryItem(payment, colors)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    colors: DashboardColors,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = colors.card),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textMuted
                )
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(color.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Text(
                value,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = colors.textPrimary
            )
        }
    }
}

@Composable
private fun PaymentHistoryItem(
    payment: Payment,
    colors: DashboardColors
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = colors.divider.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(payment.status.toColor().copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    when (payment.paymentMethod) {
                        PaymentMethod.CASH -> Icons.Default.Money
                        PaymentMethod.BANK_TRANSFER -> Icons.Default.AccountBalance
                        PaymentMethod.MOBILE_MONEY -> Icons.Default.PhoneAndroid
                        PaymentMethod.CHECK -> Icons.Default.Receipt
                        PaymentMethod.OTHER -> Icons.Default.Payment
                    },
                    contentDescription = null,
                    tint = payment.status.toColor(),
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    payment.paymentType.toFrench(),
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = colors.textPrimary
                )
                Text(
                    "${payment.paymentMethod.toFrench()} • ${payment.date}",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textMuted
                )
                Text(
                    "Réf: ${payment.reference} • Par ${payment.receivedBy}",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textMuted
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "${payment.amount.toLong().formatCurrency()} CFA",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = colors.textPrimary
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
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
        }
    }
}

@Composable
fun AddPaymentForm(
    state: PaymentsUiState,
    colors: DashboardColors,
    isCompact: Boolean,
    onBack: () -> Unit,
    onSave: () -> Unit
) {
    var selectedStudent by remember { mutableStateOf<String?>(null) }
    var paymentType by remember { mutableStateOf(PaymentType.TUITION) }
    var paymentMethod by remember { mutableStateOf(PaymentMethod.CASH) }
    var amount by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var reference by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    Card(
        colors = CardDefaults.cardColors(containerColor = colors.card),
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour", tint = colors.textPrimary)
                    }
                    Text(
                        "Nouveau Paiement",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = colors.textPrimary
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    // Student Selection
                    var studentExpanded by remember { mutableStateOf(false) }
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            "Élève *",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = colors.textPrimary
                        )
                        Box {
                            OutlinedButton(
                                onClick = { studentExpanded = true },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    selectedStudent?.let { id ->
                                        state.studentPayments.find { it.studentId == id }?.studentName
                                    } ?: "Sélectionner un élève",
                                    modifier = Modifier.weight(1f)
                                )
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                            }
                            DropdownMenu(
                                expanded = studentExpanded,
                                onDismissRequest = { studentExpanded = false }
                            ) {
                                state.studentPayments.forEach { student ->
                                    DropdownMenuItem(
                                        text = {
                                            Column {
                                                Text(student.studentName)
                                                Text(
                                                    "${student.matricule} • ${student.classroom}",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = colors.textMuted
                                                )
                                            }
                                        },
                                        onClick = {
                                            selectedStudent = student.studentId
                                            studentExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Payment Type
                        var typeExpanded by remember { mutableStateOf(false) }
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                "Type de Paiement *",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = colors.textPrimary
                            )
                            Box {
                                OutlinedButton(
                                    onClick = { typeExpanded = true },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(paymentType.toFrench(), modifier = Modifier.weight(1f))
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                }
                                DropdownMenu(
                                    expanded = typeExpanded,
                                    onDismissRequest = { typeExpanded = false }
                                ) {
                                    PaymentType.entries.forEach { type ->
                                        DropdownMenuItem(
                                            text = { Text(type.toFrench()) },
                                            onClick = {
                                                paymentType = type
                                                typeExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        // Payment Method
                        var methodExpanded by remember { mutableStateOf(false) }
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                "Méthode de Paiement *",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = colors.textPrimary
                            )
                            Box {
                                OutlinedButton(
                                    onClick = { methodExpanded = true },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(paymentMethod.toFrench(), modifier = Modifier.weight(1f))
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                }
                                DropdownMenu(
                                    expanded = methodExpanded,
                                    onDismissRequest = { methodExpanded = false }
                                ) {
                                    PaymentMethod.entries.forEach { method ->
                                        DropdownMenuItem(
                                            text = { Text(method.toFrench()) },
                                            onClick = {
                                                paymentMethod = method
                                                methodExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Amount
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                "Montant (CFA) *",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = colors.textPrimary
                            )
                            OutlinedTextField(
                                value = amount,
                                onValueChange = { amount = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("Ex: 150000") }
                            )
                        }

                        // Date
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                "Date *",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = colors.textPrimary
                            )
                            OutlinedTextField(
                                value = date,
                                onValueChange = { date = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("YYYY-MM-DD") }
                            )
                        }
                    }
                }

                item {
                    // Reference
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            "Référence *",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = colors.textPrimary
                        )
                        OutlinedTextField(
                            value = reference,
                            onValueChange = { reference = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Ex: REF2024001") }
                        )
                    }
                }

                item {
                    // Notes
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            "Notes (optionnel)",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = colors.textPrimary
                        )
                        OutlinedTextField(
                            value = notes,
                            onValueChange = { notes = it },
                            modifier = Modifier.fillMaxWidth().height(120.dp),
                            placeholder = { Text("Ajouter des notes...") }
                        )
                    }
                }
            }

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Annuler")
                }
                Button(
                    onClick = onSave,
                    modifier = Modifier.weight(1f),
                    enabled = selectedStudent != null && amount.isNotEmpty() && date.isNotEmpty() && reference.isNotEmpty()
                ) {
                    Text("Enregistrer le Paiement")
                }
            }
        }
    }
}
