package com.ecolix.presentation.screens.paiements

import androidx.compose.animation.*
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
import androidx.compose.ui.unit.sp
import com.ecolix.data.models.*
import com.ecolix.presentation.components.*

@Composable
fun PaymentsScreenContent(isDarkMode: Boolean) {
    val screenModel = remember { PaymentsScreenModel() }
    val state by screenModel.state.collectAsState()

    LaunchedEffect(isDarkMode) {
        screenModel.onDarkModeChange(isDarkMode)
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isCompact = maxWidth < 800.dp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(if (isCompact) 16.dp else 24.dp),
            verticalArrangement = Arrangement.spacedBy(if (isCompact) 16.dp else 24.dp)
        ) {
            if (!isCompact) {
                // Header Row (Desktop)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Gestion des Paiements",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 32.sp
                            ),
                            color = state.colors.textPrimary
                        )
                        Text(
                            text = "Suivi des frais de scolarité et autres paiements - ${state.currentYear}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = state.colors.textMuted
                        )
                    }

                    PaymentViewToggle(
                        currentMode = state.viewMode,
                        onModeChange = { screenModel.onViewModeChange(it) },
                        colors = state.colors
                    )
                }

                // Statistics Cards
                if (state.viewMode == PaymentViewMode.OVERVIEW && state.searchQuery.isEmpty()) {
                    PaymentStatisticsCards(
                        statistics = state.statistics,
                        colors = state.colors
                    )
                }

                // Filters and Search
                if (state.viewMode != PaymentViewMode.ADD_PAYMENT) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SearchBar(
                            query = state.searchQuery,
                            onQueryChange = { screenModel.onSearchQueryChange(it) },
                            colors = state.colors,
                            modifier = Modifier.weight(1f)
                        )

                        PaymentFilters(
                            selectedClassroom = state.selectedClassroom,
                            onClassroomChange = { screenModel.onClassroomChange(it) },
                            selectedStatus = state.selectedStatus,
                            onStatusChange = { screenModel.onStatusChange(it) },
                            classrooms = state.classrooms,
                            colors = state.colors
                        )
                    }
                }
            }

            // Main Content
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                AnimatedContent(
                    targetState = state.viewMode,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    modifier = Modifier.fillMaxSize()
                ) { mode ->
                    when (mode) {
                        PaymentViewMode.OVERVIEW -> PaymentOverviewTab(
                            state = state,
                            colors = state.colors,
                            isCompact = isCompact,
                            onSelectStudent = { screenModel.onSelectStudent(it) }
                        )
                        PaymentViewMode.STUDENT_PAYMENTS -> StudentPaymentsTab(
                            state = state,
                            colors = state.colors,
                            isCompact = isCompact,
                            onSelectStudent = { screenModel.onSelectStudent(it) }
                        )
                        PaymentViewMode.PAYMENT_HISTORY -> PaymentHistoryTab(
                            state = state,
                            colors = state.colors,
                            isCompact = isCompact
                        )
                        PaymentViewMode.ADD_PAYMENT -> AddPaymentForm(
                            state = state,
                            colors = state.colors,
                            isCompact = isCompact,
                            onBack = { screenModel.onViewModeChange(PaymentViewMode.OVERVIEW) },
                            onSave = { /* TODO: Save payment */ }
                        )
                        PaymentViewMode.PAYMENT_DETAILS -> {
                            val student = state.studentPayments.find { it.studentId == state.selectedStudentId }
                            if (student != null) {
                                PaymentDetailsView(
                                    student = student,
                                    payments = state.payments.filter { it.studentId == student.studentId },
                                    colors = state.colors,
                                    isCompact = isCompact,
                                    onBack = { screenModel.onViewModeChange(PaymentViewMode.STUDENT_PAYMENTS) }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Floating Action Button
        if (state.viewMode != PaymentViewMode.ADD_PAYMENT) {
            FloatingActionButton(
                onClick = { screenModel.onViewModeChange(PaymentViewMode.ADD_PAYMENT) },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(24.dp),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Ajouter un paiement",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
private fun PaymentViewToggle(
    currentMode: PaymentViewMode,
    onModeChange: (PaymentViewMode) -> Unit,
    colors: DashboardColors
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(colors.card)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        val tabs = listOf(
            Triple(PaymentViewMode.OVERVIEW, "Vue d'ensemble", Icons.Default.Dashboard),
            Triple(PaymentViewMode.STUDENT_PAYMENTS, "Par Élève", Icons.Default.People),
            Triple(PaymentViewMode.PAYMENT_HISTORY, "Historique", Icons.Default.History)
        )

        tabs.forEach { (mode, label, icon) ->
            val isSelected = currentMode == mode
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                    .clickable { onModeChange(mode) }
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = if (isSelected) Color.White else colors.textMuted
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = label,
                    color = if (isSelected) Color.White else colors.textMuted,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                )
            }
        }
    }
}

@Composable
private fun PaymentStatisticsCards(
    statistics: PaymentStatistics,
    colors: DashboardColors
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StatCard(
            title = "Total Attendu",
            value = "${statistics.totalExpected.toLong().formatCurrency()} CFA",
            icon = Icons.Default.AttachMoney,
            color = Color(0xFF3B82F6),
            colors = colors,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            title = "Total Collecté",
            value = "${statistics.totalCollected.toLong().formatCurrency()} CFA",
            subtitle = "${statistics.collectionRate.toInt()}% du total",
            icon = Icons.Default.CheckCircle,
            color = Color(0xFF10B981),
            colors = colors,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            title = "En Attente",
            value = "${statistics.totalPending.toLong().formatCurrency()} CFA",
            subtitle = "${statistics.pendingStudents + statistics.partialStudents} élèves",
            icon = Icons.Default.Schedule,
            color = Color(0xFFF59E0B),
            colors = colors,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            title = "En Retard",
            value = "${statistics.totalOverdue.toLong().formatCurrency()} CFA",
            subtitle = "${statistics.overdueStudents} élèves",
            icon = Icons.Default.Warning,
            color = Color(0xFFEF4444),
            colors = colors,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    colors: DashboardColors,
    modifier: Modifier = Modifier,
    subtitle: String? = null
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = colors.card),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(28.dp)
                )
            }

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textMuted
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = colors.textPrimary
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textMuted
                    )
                }
            }
        }
    }
}

@Composable
private fun PaymentFilters(
    selectedClassroom: String?,
    onClassroomChange: (String?) -> Unit,
    selectedStatus: PaymentStatus?,
    onStatusChange: (PaymentStatus?) -> Unit,
    classrooms: List<String>,
    colors: DashboardColors
) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        // Classroom Filter
        var classroomExpanded by remember { mutableStateOf(false) }
        Box {
            OutlinedButton(
                onClick = { classroomExpanded = true },
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (selectedClassroom != null) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent
                )
            ) {
                Icon(Icons.Default.Class, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(selectedClassroom ?: "Toutes les classes")
            }
            DropdownMenu(
                expanded = classroomExpanded,
                onDismissRequest = { classroomExpanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Toutes les classes") },
                    onClick = {
                        onClassroomChange(null)
                        classroomExpanded = false
                    }
                )
                classrooms.forEach { classroom ->
                    DropdownMenuItem(
                        text = { Text(classroom) },
                        onClick = {
                            onClassroomChange(classroom)
                            classroomExpanded = false
                        }
                    )
                }
            }
        }

        // Status Filter
        var statusExpanded by remember { mutableStateOf(false) }
        Box {
            OutlinedButton(
                onClick = { statusExpanded = true },
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (selectedStatus != null) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent
                )
            ) {
                Icon(Icons.Default.FilterList, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(selectedStatus?.toFrench() ?: "Tous les statuts")
            }
            DropdownMenu(
                expanded = statusExpanded,
                onDismissRequest = { statusExpanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Tous les statuts") },
                    onClick = {
                        onStatusChange(null)
                        statusExpanded = false
                    }
                )
                PaymentStatus.entries.forEach { status ->
                    DropdownMenuItem(
                        text = { Text(status.toFrench()) },
                        onClick = {
                            onStatusChange(status)
                            statusExpanded = false
                        }
                    )
                }
            }
        }
    }
}

// Helper extension functions
fun PaymentStatus.toFrench(): String = when (this) {
    PaymentStatus.PAID -> "Payé"
    PaymentStatus.PARTIAL -> "Partiel"
    PaymentStatus.PENDING -> "En attente"
    PaymentStatus.OVERDUE -> "En retard"
}

fun PaymentStatus.toColor(): Color = when (this) {
    PaymentStatus.PAID -> Color(0xFF10B981)
    PaymentStatus.PARTIAL -> Color(0xFFF59E0B)
    PaymentStatus.PENDING -> Color(0xFF6B7280)
    PaymentStatus.OVERDUE -> Color(0xFFEF4444)
}

fun Long.formatCurrency(): String {
    return this.toString().reversed().chunked(3).joinToString(" ").reversed()
}

fun PaymentType.toFrench(): String = when (this) {
    PaymentType.TUITION -> "Scolarité"
    PaymentType.REGISTRATION -> "Inscription"
    PaymentType.EXAM_FEE -> "Examen"
    PaymentType.TRANSPORT -> "Transport"
    PaymentType.CANTEEN -> "Cantine"
    PaymentType.UNIFORM -> "Uniforme"
    PaymentType.BOOKS -> "Livres"
    PaymentType.ACTIVITY -> "Activités"
    PaymentType.OTHER -> "Autre"
}

fun PaymentMethod.toFrench(): String = when (this) {
    PaymentMethod.CASH -> "Espèces"
    PaymentMethod.BANK_TRANSFER -> "Virement"
    PaymentMethod.MOBILE_MONEY -> "Mobile Money"
    PaymentMethod.CHECK -> "Chèque"
    PaymentMethod.OTHER -> "Autre"
}
