package com.ecolix.presentation.screens.academic

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

@Composable
fun AcademicOverviewTab(
    state: AcademicUiState,
    colors: DashboardColors,
    isCompact: Boolean
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Current Academic Year Card
        item {
            state.statistics.activeYear?.let { activeYear ->
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
                            Column {
                                Text(
                                    "Année Scolaire en Cours",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = colors.textPrimary
                                )
                                Text(
                                    activeYear.name,
                                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(activeYear.status.toColor().copy(alpha = 0.1f))
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    activeYear.status.toFrench(),
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                                    color = activeYear.status.toColor()
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(32.dp)
                        ) {
                            InfoItem("Début", activeYear.startDate, Icons.Default.PlayArrow, colors)
                            InfoItem("Fin", activeYear.endDate, Icons.Default.Stop, colors)
                            InfoItem("Type", activeYear.periodType.toFrench(), Icons.Default.Category, colors)
                            InfoItem("Périodes", "${activeYear.numberOfPeriods}", Icons.Default.Numbers, colors)
                        }

                        if (activeYear.description != null) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                activeYear.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = colors.textMuted
                            )
                        }
                    }
                }
            }
        }

        // Current Period Card
        item {
            state.statistics.currentPeriod?.let { currentPeriod ->
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
                            Column {
                                Text(
                                    "Période Actuelle",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = colors.textPrimary
                                )
                                Text(
                                    currentPeriod.name,
                                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            LinearProgressIndicator(
                                progress = { state.statistics.completionRate / 100f },
                                modifier = Modifier.width(200.dp).height(8.dp),
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = colors.divider,
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(32.dp)
                        ) {
                            InfoItem("Début", currentPeriod.startDate, Icons.Default.CalendarToday, colors)
                            InfoItem("Fin", currentPeriod.endDate, Icons.Default.Event, colors)
                            currentPeriod.evaluationDeadline?.let {
                                InfoItem("Évaluations", it, Icons.Default.Assignment, colors)
                            }
                            currentPeriod.reportCardDeadline?.let {
                                InfoItem("Bulletins", it, Icons.Default.Description, colors)
                            }
                        }
                    }
                }
            }
        }

        // Upcoming Events
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = colors.card),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        "Événements à Venir",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = colors.textPrimary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    state.events.take(5).forEach { event ->
                        EventItem(event, colors)
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }

        // Holidays
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = colors.card),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        "Vacances et Jours Fériés",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = colors.textPrimary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    state.holidays.forEach { holiday ->
                        HolidayItem(holiday, colors)
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoItem(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    colors: DashboardColors
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = colors.textMuted,
            modifier = Modifier.size(20.dp)
        )
        Column {
            Text(
                label,
                style = MaterialTheme.typography.bodySmall,
                color = colors.textMuted
            )
            Text(
                value,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = colors.textPrimary
            )
        }
    }
}

@Composable
private fun EventItem(
    event: AcademicEvent,
    colors: DashboardColors
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(event.color.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = when (event.type) {
                    EventType.HOLIDAY -> Icons.Default.BeachAccess
                    EventType.EXAM -> Icons.Default.Assignment
                    EventType.MEETING -> Icons.Default.Groups
                    EventType.CEREMONY -> Icons.Default.EmojiEvents
                    EventType.DEADLINE -> Icons.Default.Schedule
                    EventType.OTHER -> Icons.Default.Event
                },
                contentDescription = null,
                tint = event.color,
                modifier = Modifier.size(24.dp)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                event.title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = colors.textPrimary
            )
            Text(
                event.type.toFrench(),
                style = MaterialTheme.typography.bodySmall,
                color = colors.textMuted
            )
            if (event.description != null) {
                Text(
                    event.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textMuted
                )
            }
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                event.date,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = colors.textPrimary
            )
            if (event.endDate != null && event.endDate != event.date) {
                Text(
                    "au ${event.endDate}",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textMuted
                )
            }
        }
    }
}

@Composable
private fun HolidayItem(
    holiday: Holiday,
    colors: DashboardColors
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFF59E0B).copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = when (holiday.type) {
                    HolidayType.NATIONAL -> Icons.Default.Flag
                    HolidayType.RELIGIOUS -> Icons.Default.Mosque
                    HolidayType.SCHOOL_BREAK -> Icons.Default.BeachAccess
                    HolidayType.OTHER -> Icons.Default.Event
                },
                contentDescription = null,
                tint = Color(0xFFF59E0B),
                modifier = Modifier.size(20.dp)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                holiday.name,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = colors.textPrimary
            )
            Text(
                holiday.type.toFrench(),
                style = MaterialTheme.typography.bodySmall,
                color = colors.textMuted
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                holiday.startDate,
                style = MaterialTheme.typography.bodySmall,
                color = colors.textPrimary
            )
            if (holiday.endDate != holiday.startDate) {
                Text(
                    "au ${holiday.endDate}",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textMuted
                )
            }
        }
    }
}

@Composable
fun SchoolYearsTab(
    state: AcademicUiState,
    colors: DashboardColors,
    isCompact: Boolean,
    onSelectYear: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Années Scolaires (${state.schoolYears.size})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = colors.textPrimary
                )
                Button(
                    onClick = { /* Add new year */ },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Nouvelle Année")
                }
            }
        }

        items(state.schoolYears.sortedByDescending { it.name }) { year ->
            SchoolYearCard(year, colors, onClick = { onSelectYear(year.id) })
        }
    }
}

@Composable
private fun SchoolYearCard(
    year: SchoolYear,
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
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(year.status.toColor().copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.School,
                    contentDescription = null,
                    tint = year.status.toColor(),
                    modifier = Modifier.size(30.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        year.name,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = colors.textPrimary
                    )
                    if (year.isDefault) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                "Par défaut",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
                Text(
                    "${year.startDate} - ${year.endDate}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textMuted
                )
                Text(
                    "${year.numberOfPeriods} ${year.periodType.toFrench()}s",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textMuted
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(year.status.toColor().copy(alpha = 0.1f))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    year.status.toFrench(),
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = year.status.toColor()
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
fun PeriodsTab(
    state: AcademicUiState,
    colors: DashboardColors,
    isCompact: Boolean,
    onSelectPeriod: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                "Périodes Académiques (${state.periods.size})",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = colors.textPrimary
            )
        }

        items(state.periods.sortedBy { it.periodNumber }) { period ->
            PeriodCard(period, colors, onClick = { onSelectPeriod(period.id) })
        }
    }
}

@Composable
private fun PeriodCard(
    period: AcademicPeriod,
    colors: DashboardColors,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = colors.card),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(period.status.toColor().copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "${period.periodNumber}",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = period.status.toColor()
                        )
                    }

                    Column {
                        Text(
                            period.name,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = colors.textPrimary
                        )
                        Text(
                            "${period.startDate} - ${period.endDate}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.textMuted
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(period.status.toColor().copy(alpha = 0.1f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        period.status.toFrench(),
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = period.status.toColor()
                    )
                }
            }

            if (period.evaluationDeadline != null || period.reportCardDeadline != null) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = colors.divider)
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    period.evaluationDeadline?.let {
                        DeadlineItem("Évaluations", it, Icons.Default.Assignment, colors)
                    }
                    period.reportCardDeadline?.let {
                        DeadlineItem("Bulletins", it, Icons.Default.Description, colors)
                    }
                }
            }
        }
    }
}

@Composable
private fun DeadlineItem(
    label: String,
    date: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    colors: DashboardColors
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFFF59E0B),
            modifier = Modifier.size(20.dp)
        )
        Column {
            Text(
                label,
                style = MaterialTheme.typography.bodySmall,
                color = colors.textMuted
            )
            Text(
                date,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = colors.textPrimary
            )
        }
    }
}
