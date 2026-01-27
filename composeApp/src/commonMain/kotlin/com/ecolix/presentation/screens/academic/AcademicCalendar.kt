package com.ecolix.presentation.screens.academic

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
fun CalendarTab(
    state: AcademicUiState,
    colors: DashboardColors,
    isCompact: Boolean
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Calendar Header
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = colors.card),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        "Calendrier Académique ${state.statistics.activeYear?.name ?: ""}",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Vue d'ensemble des événements, examens et vacances",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.textMuted
                    )
                }
            }
        }

        // Events Section
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
                            "Événements",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = colors.textPrimary
                        )
                        Button(
                            onClick = { /* Add event */ },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Ajouter")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Event Type Legend
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        EventTypeLegend("Examens", Color(0xFFEF4444), colors)
                        EventTypeLegend("Réunions", Color(0xFF10B981), colors)
                        EventTypeLegend("Cérémonies", Color(0xFF8B5CF6), colors)
                        EventTypeLegend("Dates limites", Color(0xFFF59E0B), colors)
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = colors.divider)
                    Spacer(modifier = Modifier.height(16.dp))

                    // Events List
                    state.events.sortedBy { it.date }.forEach { event ->
                        CalendarEventItem(event, colors)
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }

        // Holidays Section
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
                            "Vacances et Jours Fériés",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = colors.textPrimary
                        )
                        Button(
                            onClick = { /* Add holiday */ },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Ajouter")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    state.holidays.sortedBy { it.startDate }.forEach { holiday ->
                        CalendarHolidayItem(holiday, colors)
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun EventTypeLegend(
    label: String,
    color: Color,
    colors: DashboardColors
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
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
            color = colors.textMuted
        )
    }
}

@Composable
private fun CalendarEventItem(
    event: AcademicEvent,
    colors: DashboardColors
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Date Badge
        Column(
            modifier = Modifier
                .width(70.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(event.color.copy(alpha = 0.1f))
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                event.date.split("-").getOrNull(2) ?: "",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = event.color
            )
            Text(
                event.date.split("-").getOrNull(1)?.let {
                    when (it) {
                        "01" -> "Jan"
                        "02" -> "Fév"
                        "03" -> "Mar"
                        "04" -> "Avr"
                        "05" -> "Mai"
                        "06" -> "Juin"
                        "07" -> "Juil"
                        "08" -> "Août"
                        "09" -> "Sep"
                        "10" -> "Oct"
                        "11" -> "Nov"
                        "12" -> "Déc"
                        else -> it
                    }
                } ?: "",
                style = MaterialTheme.typography.bodySmall,
                color = event.color
            )
        }

        // Event Details
        Column(modifier = Modifier.weight(1f)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    event.title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = colors.textPrimary
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(event.color.copy(alpha = 0.1f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        event.type.toFrench(),
                        style = MaterialTheme.typography.labelSmall,
                        color = event.color
                    )
                }
            }

            if (event.description != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    event.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textMuted
                )
            }

            if (event.endDate != null && event.endDate != event.date) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.DateRange,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = colors.textMuted
                    )
                    Text(
                        "Jusqu'au ${event.endDate}",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textMuted
                    )
                }
            }
        }

        // Actions
        IconButton(onClick = { /* Edit */ }) {
            Icon(
                Icons.Default.Edit,
                contentDescription = "Modifier",
                tint = colors.textMuted,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun CalendarHolidayItem(
    holiday: Holiday,
    colors: DashboardColors
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFF59E0B).copy(alpha = 0.05f))
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
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
                modifier = Modifier.size(24.dp)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                holiday.name,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
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
                "${holiday.startDate} - ${holiday.endDate}",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = colors.textPrimary
            )
            val duration = calculateDuration(holiday.startDate, holiday.endDate)
            Text(
                "$duration jour${if (duration > 1) "s" else ""}",
                style = MaterialTheme.typography.bodySmall,
                color = colors.textMuted
            )
        }

        IconButton(onClick = { /* Edit */ }) {
            Icon(
                Icons.Default.Edit,
                contentDescription = "Modifier",
                tint = colors.textMuted,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun AcademicSettingsTab(
    state: AcademicUiState,
    colors: DashboardColors,
    isCompact: Boolean
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // General Settings
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = colors.card),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        "Paramètres Généraux",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = colors.textPrimary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    SettingItem(
                        title = "Type de période par défaut",
                        value = state.settings.defaultPeriodType.toFrench(),
                        icon = Icons.Default.DateRange,
                        colors = colors
                    )

                    SettingItem(
                        title = "Note de passage",
                        value = "${state.settings.passingGrade}/20",
                        icon = Icons.Default.Grade,
                        colors = colors
                    )

                    SettingItem(
                        title = "Présence requise",
                        value = "${state.settings.attendanceRequired}%",
                        icon = Icons.Default.CheckCircle,
                        colors = colors
                    )
                }
            }
        }

        // Grade Scale
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = colors.card),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        "Échelle de Notation",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = colors.textPrimary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        GradeScaleInfo("Note minimale", "${state.settings.gradeScale.minGrade}", colors)
                        GradeScaleInfo("Note maximale", "${state.settings.gradeScale.maxGrade}", colors)
                        GradeScaleInfo("Note de passage", "${state.settings.gradeScale.passingGrade}", colors)
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = colors.divider)
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        "Niveaux de Performance",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = colors.textPrimary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    state.settings.gradeScale.gradeLevels.forEach { level ->
                        GradeLevelItem(level, colors)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }

        // Advanced Settings
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = colors.card),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        "Paramètres Avancés",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = colors.textPrimary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    SwitchSettingItem(
                        title = "Autoriser les transferts en cours de période",
                        description = "Permettre aux élèves de changer de classe pendant une période",
                        checked = state.settings.allowMidPeriodTransfer,
                        onCheckedChange = { /* Update setting */ },
                        colors = colors
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    SwitchSettingItem(
                        title = "Promotion automatique des élèves",
                        description = "Promouvoir automatiquement les élèves au niveau supérieur en fin d'année",
                        checked = state.settings.autoPromoteStudents,
                        onCheckedChange = { /* Update setting */ },
                        colors = colors
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingItem(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    colors: DashboardColors
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textPrimary
            )
        }

        Text(
            value,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun GradeScaleInfo(
    label: String,
    value: String,
    colors: DashboardColors
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(colors.divider.copy(alpha = 0.3f))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            color = colors.textMuted
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            value,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = colors.textPrimary
        )
    }
}

@Composable
private fun GradeLevelItem(
    level: GradeLevel,
    colors: DashboardColors
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(level.color.copy(alpha = 0.1f))
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(40.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(level.color)
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                level.name,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = colors.textPrimary
            )
            Text(
                level.description,
                style = MaterialTheme.typography.bodySmall,
                color = colors.textMuted
            )
        }

        Text(
            "${level.minValue} - ${level.maxValue}",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = level.color
        )
    }
}

@Composable
private fun SwitchSettingItem(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    colors: DashboardColors
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = colors.textPrimary
            )
            Text(
                description,
                style = MaterialTheme.typography.bodySmall,
                color = colors.textMuted
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}

// Helper function
private fun calculateDuration(startDate: String, endDate: String): Int {
    // Simplified calculation - in real app, use proper date library
    return 7 // Mock value
}
