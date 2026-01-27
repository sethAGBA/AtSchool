package com.ecolix.presentation.screens.discipline

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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecolix.data.models.*
import com.ecolix.presentation.components.SearchBar

@Composable
fun DisciplineScreenContent(isDarkMode: Boolean) {
    val screenModel = remember { DisciplineScreenModel() }
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
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Discipline & Vie Scolaire",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = if (isCompact) 24.sp else 32.sp
                        ),
                        color = state.colors.textPrimary
                    )
                    if (!isCompact) {
                        Text(
                            text = "Suivi comportemental, sanctions et points de mérite",
                            style = MaterialTheme.typography.bodyMedium,
                            color = state.colors.textMuted
                        )
                    }
                }

                DisciplineViewToggle(
                    currentMode = state.viewMode,
                    onModeChange = { screenModel.onViewModeChange(it) },
                    colors = state.colors
                )
            }

            // Overview Stats
            if (state.viewMode == DisciplineViewMode.OVERVIEW) {
                OverviewStats(state, isCompact)
            }

            // Search and Filter
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

                if (!isCompact) {
                    Button(
                        onClick = { /* Add incident */ },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Icon(Icons.Default.Report, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Signaler un incident")
                    }
                }
            }

            // Main Content
            Box(modifier = Modifier.weight(1f)) {
                AnimatedContent(
                    targetState = state.viewMode,
                    transitionSpec = { fadeIn() togetherWith fadeOut() }
                ) { mode ->
                    when (mode) {
                        DisciplineViewMode.OVERVIEW -> StudentBehaviorList(state, isCompact)
                        DisciplineViewMode.ATTENDANCE -> AttendanceList(state, isCompact)
                        DisciplineViewMode.INCIDENTS -> IncidentsList(state, isCompact)
                        DisciplineViewMode.SANCTIONS -> SanctionsList(state, isCompact)
                        DisciplineViewMode.MERITS -> MeritsList(state, isCompact)
                        else -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("En cours de développement", color = state.colors.textMuted)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OverviewStats(state: DisciplineUiState, isCompact: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StatCard("Absences", "${state.attendance.count { it.type == AttendanceType.ABSENCE }}", Icons.Default.EventBusy, Color(0xFFEF4444), state.colors, Modifier.weight(1f))
        StatCard("Retards", "${state.attendance.count { it.type == AttendanceType.LATE }}", Icons.Default.Schedule, Color(0xFFF59E0B), state.colors, Modifier.weight(1f))
        if (!isCompact) {
            StatCard("Sanctions", "${state.sanctions.count { it.status == SanctionStatus.ACTIVE }}", Icons.Default.Gavel, Color(0xFFEF4444), state.colors, Modifier.weight(1f))
            StatCard("Mérites", "${state.merits.sumOf { it.points }}", Icons.Default.Star, Color(0xFF10B981), state.colors, Modifier.weight(1f))
        }
    }
}

@Composable
private fun StatCard(title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, colors: DashboardColors, modifier: Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = colors.card)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(40.dp).clip(RoundedCornerShape(8.dp)).background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(title, style = MaterialTheme.typography.labelSmall, color = colors.textMuted)
                Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = colors.textPrimary)
            }
        }
    }
}

@Composable
private fun StudentBehaviorList(state: DisciplineUiState, isCompact: Boolean) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(state.studentSummaries.sortedBy { it.behaviorScore }) { summary ->
            StudentBehaviorRow(summary, state.colors)
        }
    }
}

@Composable
private fun StudentBehaviorRow(summary: StudentDisciplineSummary, colors: DashboardColors) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colors.card)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(summary.studentName, fontWeight = FontWeight.Bold, color = colors.textPrimary)
                Text(summary.classroom, style = MaterialTheme.typography.bodySmall, color = colors.textMuted)
            }
            
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                BehaviorMetric("Score", "${summary.behaviorScore}%", if (summary.behaviorScore >= 80) Color(0xFF10B981) else if (summary.behaviorScore >= 60) Color(0xFFF59E0B) else Color(0xFFEF4444), colors)
                BehaviorMetric("Points", "+${summary.totalMerits}", Color(0xFF10B981), colors)
                BehaviorMetric("Alertes", "${summary.totalIncidents}", Color(0xFFF59E0B), colors)
            }
        }
    }
}

@Composable
private fun BehaviorMetric(label: String, value: String, color: Color, colors: DashboardColors) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = colors.textMuted)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = color)
    }
}

@Composable
private fun IncidentsList(state: DisciplineUiState, isCompact: Boolean) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(state.incidents.sortedByDescending { it.date }) { incident ->
            IncidentRow(incident, state.colors)
        }
    }
}

@Composable
private fun IncidentRow(incident: DisciplineIncident, colors: DashboardColors) {
    val severityColor = when (incident.severity) {
        Severity.LOW -> Color(0xFF3B82F6)
        Severity.MEDIUM -> Color(0xFFF59E0B)
        Severity.HIGH -> Color(0xFFEF4444)
        Severity.CRITICAL -> Color(0xFF1E293B)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colors.card)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("${incident.studentName} (${incident.classroom})", fontWeight = FontWeight.Bold, color = colors.textPrimary)
                Text(incident.date, style = MaterialTheme.typography.labelSmall, color = colors.textMuted)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(8.dp).clip(RoundedCornerShape(4.dp)).background(severityColor))
                Spacer(modifier = Modifier.width(8.dp))
                Text(incident.type.name, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, color = colors.textPrimary)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(incident.description, style = MaterialTheme.typography.bodyMedium, color = colors.textMuted, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Signalé par: ${incident.reportedBy}", style = MaterialTheme.typography.labelSmall, color = colors.textMuted)
        }
    }
}

@Composable
private fun SanctionsList(state: DisciplineUiState, isCompact: Boolean) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(state.sanctions) { sanction ->
            SanctionRow(sanction, state.colors)
        }
    }
}

@Composable
private fun SanctionRow(sanction: Sanction, colors: DashboardColors) {
    val statusColor = when (sanction.status) {
        SanctionStatus.PENDING -> Color(0xFFF59E0B)
        SanctionStatus.ACTIVE -> Color(0xFFEF4444)
        SanctionStatus.COMPLETED -> Color(0xFF10B981)
        SanctionStatus.CANCELLED -> Color(0xFF71717A)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colors.card)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(sanction.type, fontWeight = FontWeight.Bold, color = colors.textPrimary)
                Text(if (sanction.duration != null) "Durée: ${sanction.duration}" else "Date: ${sanction.startDate}", style = MaterialTheme.typography.bodySmall, color = colors.textMuted)
            }
            Box(
                modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(statusColor.copy(alpha = 0.1f)).padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(sanction.status.name, style = MaterialTheme.typography.labelSmall, color = statusColor, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun MeritsList(state: DisciplineUiState, isCompact: Boolean) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(state.merits) { merit ->
            MeritRow(merit, state.colors)
        }
    }
}

@Composable
private fun MeritRow(merit: MeritPoint, colors: DashboardColors) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colors.card)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(40.dp).clip(RoundedCornerShape(20.dp)).background(Color(0xFF10B981).copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text("+${merit.points}", fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(merit.reason, fontWeight = FontWeight.Bold, color = colors.textPrimary)
                Text("Le ${merit.date} par ${merit.awardedBy}", style = MaterialTheme.typography.labelSmall, color = colors.textMuted)
            }
        }
    }
}

@Composable
private fun AttendanceList(state: DisciplineUiState, isCompact: Boolean) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(state.attendance.sortedByDescending { it.date }) { record ->
            AttendanceRow(record, state.colors)
        }
    }
}

@Composable
private fun AttendanceRow(record: AttendanceRecord, colors: DashboardColors) {
    val typeColor = if (record.type == AttendanceType.ABSENCE) Color(0xFFEF4444) else Color(0xFFF59E0B)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colors.card)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(record.studentName, fontWeight = FontWeight.Bold, color = colors.textPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(typeColor.copy(alpha = 0.1f)).padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(if (record.type == AttendanceType.ABSENCE) "ABSENCE" else "RETARD", style = MaterialTheme.typography.labelSmall, color = typeColor, fontWeight = FontWeight.Bold)
                    }
                }
                Text(record.classroom, style = MaterialTheme.typography.bodySmall, color = colors.textMuted)
                if (record.reason != null) {
                    Text(record.reason, style = MaterialTheme.typography.bodyMedium, color = colors.textMuted)
                }
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(record.date, style = MaterialTheme.typography.labelSmall, color = colors.textMuted)
                if (record.time != null) {
                    Text(record.time, style = MaterialTheme.typography.bodySmall, color = colors.textPrimary)
                }
                if (record.isJustified) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Justifié", style = MaterialTheme.typography.labelSmall, color = Color(0xFF10B981))
                    }
                }
            }
        }
    }
}

@Composable
private fun DisciplineViewToggle(currentMode: DisciplineViewMode, onModeChange: (DisciplineViewMode) -> Unit, colors: DashboardColors) {
    Row(
        modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(colors.card).padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        val tabs = listOf(
            Triple(DisciplineViewMode.OVERVIEW, "Aperçu", Icons.Default.Assessment),
            Triple(DisciplineViewMode.ATTENDANCE, "Présences", Icons.Default.Rule),
            Triple(DisciplineViewMode.INCIDENTS, "Incidents", Icons.Default.Warning),
            Triple(DisciplineViewMode.SANCTIONS, "Sanctions", Icons.Default.Gavel),
            Triple(DisciplineViewMode.MERITS, "Mérites", Icons.Default.Star)
        )

        tabs.forEach { (mode, label, icon) ->
            val isSelected = currentMode == mode
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                    .clickable { onModeChange(mode) }
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = if (isSelected) Color.White else colors.textMuted
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    label,
                    color = if (isSelected) Color.White else colors.textMuted,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                )
            }
        }
    }
}
