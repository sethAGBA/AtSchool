package com.ecolix.presentation.screens.academic

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.input.key.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.draw.scale
import com.ecolix.data.models.*
import com.ecolix.atschool.api.AcademicPeriodDto
import kotlinx.datetime.*
import androidx.compose.animation.*

@Composable
fun AcademicOverviewTab(
    state: AcademicUiState,
    colors: DashboardColors,
    isCompact: Boolean,
    onArchiveYear: (String) -> Unit
) {
    var showArchiveDialog by remember { mutableStateOf(false) }

    if (showArchiveDialog && state.statistics.activeYear != null) {
        ArchiveYearDialog(
            yearName = state.statistics.activeYear.name,
            onDismiss = { showArchiveDialog = false },
            onConfirm = {
                onArchiveYear(state.statistics.activeYear.id)
                showArchiveDialog = false
            }
        )
    }
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
                            InfoItem("Type", activeYear.periodTypes.joinToString(" / ") { it.toFrench() }, Icons.Default.Category, colors)
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

                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Button(
                            onClick = { showArchiveDialog = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.1f),
                                contentColor = MaterialTheme.colorScheme.error
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Archive, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Archiver l'année scolaire")
                        }
                    }
                }
            }
        }

        // Current Period Card
        item {
            if (state.statistics.currentPeriods.isNotEmpty()) {
                state.statistics.currentPeriods.forEach { currentPeriod ->
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
                    Spacer(modifier = Modifier.height(16.dp))
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
    onSelectYear: (String) -> Unit,
    onCreateYear: (String, String, String, List<PeriodType>, Int, List<AcademicPeriodDto>?) -> Unit,
    onUpdateYear: (String, String, String, String, List<PeriodType>, Int, List<AcademicPeriodDto>?) -> Unit,
    onDeleteYear: (String) -> Unit,
    onSetDefault: (String) -> Unit
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
                var showNewYearDialog by remember { mutableStateOf(false) }
                if (showNewYearDialog) {
                    NewSchoolYearDialog(
                        onDismiss = { showNewYearDialog = false },
                        onConfirm = { name, start, end, types, num, periods ->
                            onCreateYear(name, start, end, types, num, periods)
                            showNewYearDialog = false
                        },
                        colors = colors
                    )
                }

                Button(
                    onClick = { showNewYearDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Nouvelle Année")
                }
            }
        }

        items(state.schoolYears.sortedByDescending { it.name }) { year ->
            SchoolYearCard(
                year = year,
                colors = colors,
                onClick = { onSelectYear(year.id) },
                onEdit = { name, start, end, types, num, periods ->
                    onUpdateYear(year.id, name, start, end, types, num, periods)
                }
            )
        }
    }
}

@Composable
private fun SchoolYearCard(
    year: SchoolYear,
    colors: DashboardColors,
    onClick: () -> Unit,
    onEdit: (String, String, String, List<PeriodType>, Int, List<AcademicPeriodDto>?) -> Unit
) {
    var showEditDialog by remember { mutableStateOf(false) }

    if (showEditDialog) {
        EditSchoolYearDialog(
            year = year,
            onDismiss = { showEditDialog = false },
            onConfirm = { name, start, end, types, num, periods ->
                onEdit(name, start, end, types, num, periods)
                showEditDialog = false
            },
            colors = colors
        )
    }

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
                    "${year.numberOfPeriods} ${year.periodTypes.joinToString(" / ") { it.toFrench() }}",
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

            IconButton(onClick = { showEditDialog = true }) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Modifier",
                    tint = colors.textMuted
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
    onSelectPeriod: (String) -> Unit,
    onCreatePeriod: (String, Int, String, String, PeriodType) -> Unit,
    onUpdatePeriod: (String, String, Int, String, String, PeriodType) -> Unit,
    onDeletePeriod: (String) -> Unit
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
                    "Périodes Académiques (${state.periods.size})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = colors.textPrimary
                )
                var showNewPeriodDialog by remember { mutableStateOf(false) }
                if (showNewPeriodDialog) {
                    NewPeriodDialog(
                        onDismiss = { showNewPeriodDialog = false },
                        onConfirm = { name, num, start, end, type ->
                            onCreatePeriod(name, num, start, end, type)
                            showNewPeriodDialog = false
                        },
                        colors = colors
                    )
                }

                Button(
                    onClick = { showNewPeriodDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Nouvelle Période")
                }
            }
        }

        items(state.periods.sortedBy { it.periodNumber }) { period ->
            PeriodCard(
                period = period,
                colors = colors,
                onClick = { onSelectPeriod(period.id) },
                onEdit = { name, num, start, end, type ->
                    onUpdatePeriod(period.id, name, num, start, end, type)
                },
                onDelete = { onDeletePeriod(period.id) }
            )
        }
    }
}

@Composable
private fun PeriodCard(
    period: AcademicPeriod,
    colors: DashboardColors,
    onClick: () -> Unit,
    onEdit: (String, Int, String, String, PeriodType) -> Unit,
    onDelete: () -> Unit
) {
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showEditDialog) {
        EditPeriodDialog(
            period = period,
            onDismiss = { showEditDialog = false },
            onConfirm = { name, num, start, end, type ->
                onEdit(name, num, start, end, type)
                showEditDialog = false
            },
            colors = colors
        )
    }

    if (showDeleteDialog) {
        DeletePeriodDialog(
            periodName = period.name,
            onDismiss = { showDeleteDialog = false },
            onConfirm = { 
                onDelete()
                showDeleteDialog = false
            }
        )
    }

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
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                period.name,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = colors.textPrimary
                            )
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = colors.textMuted.copy(alpha = 0.1f)
                            ) {
                                Text(
                                    period.type.toFrench(),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = colors.textMuted
                                )
                            }
                        }
                        Text(
                            "${period.startDate} - ${period.endDate}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.textMuted
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
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

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        IconButton(onClick = { showEditDialog = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "Modifier", tint = colors.textMuted)
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Supprimer", tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f))
                        }
                        Icon(
                            Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = colors.textMuted
                        )
                    }
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
@Composable
fun NewSchoolYearDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, List<PeriodType>, Int, List<AcademicPeriodDto>?) -> Unit,
    colors: DashboardColors
) {
    var name by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var selectedTypes by remember { mutableStateOf(setOf(PeriodType.TRIMESTER)) }
    var numPeriods by remember { mutableStateOf(3) }
    var generatedPeriods by remember { mutableStateOf<List<AcademicPeriodDto>>(emptyList()) }
    var isManualEditing by remember { mutableStateOf(false) }

    // Auto-generation logic
    LaunchedEffect(startDate, endDate, selectedTypes) {
        if (!isManualEditing) {
            val startValid = try { LocalDate.parse(normalizeDate(startDate)); true } catch (e: Exception) { false }
            val endValid = try { LocalDate.parse(normalizeDate(endDate)); true } catch (e: Exception) { false }
            
            if (startValid && endValid && name.isNotBlank()) {
                generatedPeriods = generateDefaultPeriods(
                    normalizeDate(startDate),
                    normalizeDate(endDate),
                    selectedTypes
                )
                numPeriods = generatedPeriods.size
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .widthIn(max = 600.dp)
            .onPreviewKeyEvent { event ->
            if (event.key == Key.Escape && event.type == KeyEventType.KeyDown) {
                onDismiss()
                true
            } else if (event.key == Key.Enter && event.type == KeyEventType.KeyDown) {
                val isFormValid = name.isNotBlank() && startDate.isNotBlank() && endDate.isNotBlank()
                if (isFormValid) {
                    val normalizedStart = normalizeDate(startDate)
                    val normalizedEnd = normalizeDate(endDate)
                    onConfirm(name, normalizedStart, normalizedEnd, selectedTypes.toList(), numPeriods, generatedPeriods)
                }
                true
            } else {
                false
            }
        },
        containerColor = colors.card,
        textContentColor = colors.textPrimary,
        titleContentColor = colors.textPrimary,
        title = { 
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Nouvelle Année Scolaire")
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Fermer", tint = colors.textMuted)
                }
            }
        },
        text = {
            val focusManager = LocalFocusManager.current
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Libellé (ex: 2025-2026) *") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(androidx.compose.ui.focus.FocusDirection.Down) }),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = colors.textPrimary,
                        unfocusedTextColor = colors.textPrimary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = colors.textMuted,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    )
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val startError = validateDate(startDate)
                    OutlinedTextField(
                        value = startDate,
                        onValueChange = { startDate = it; isManualEditing = false },
                        label = { Text("Début (AAAA-MM-JJ) *") },
                        modifier = Modifier.weight(1f),
                        isError = startError != null || (if (validateDate(startDate) == null && validateDate(endDate) == null) validateDateRange(normalizeDate(startDate), normalizeDate(endDate)) != null else false),
                        supportingText = { startError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(androidx.compose.ui.focus.FocusDirection.Right) }),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = colors.textPrimary,
                            unfocusedTextColor = colors.textPrimary,
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedLabelColor = MaterialTheme.colorScheme.primary,
                            unfocusedLabelColor = colors.textMuted
                        )
                    )
                    val endError = validateDate(endDate)
                    OutlinedTextField(
                        value = endDate,
                        onValueChange = { endDate = it; isManualEditing = false },
                        label = { Text("Fin (AAAA-MM-JJ) *") },
                        modifier = Modifier.weight(1f),
                        isError = endError != null || (if (validateDate(startDate) == null && validateDate(endDate) == null) validateDateRange(normalizeDate(startDate), normalizeDate(endDate)) != null else false),
                        supportingText = { 
                            val rangeError = if (validateDate(startDate) == null && validateDate(endDate) == null) validateDateRange(normalizeDate(startDate), normalizeDate(endDate)) else null
                            endError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                            ?: rangeError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                        },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(androidx.compose.ui.focus.FocusDirection.Down) }),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = colors.textPrimary,
                            unfocusedTextColor = colors.textPrimary,
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedLabelColor = MaterialTheme.colorScheme.primary,
                            unfocusedLabelColor = colors.textMuted
                        )
                    )
                }
                Text("Types de périodes actifs", style = MaterialTheme.typography.labelMedium, color = colors.textMuted)
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PeriodType.values().forEach { pType ->
                        val isSelected = selectedTypes.contains(pType)
                        FilterChip(
                            selected = isSelected,
                            onClick = { 
                                val current = selectedTypes
                                selectedTypes = if (isSelected) {
                                    if (current.size > 1) current - pType else current
                                } else {
                                    current + pType
                                }
                                
                                numPeriods = selectedTypes.sumOf { t: PeriodType ->
                                    when(t) {
                                        PeriodType.TRIMESTER -> 3
                                        PeriodType.SEMESTER -> 2
                                    }
                                }
                            },
                            label = { Text(pType.toFrench()) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                selectedLabelColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }
                OutlinedTextField(
                    value = numPeriods.toString(),
                    onValueChange = { 
                        numPeriods = it.toIntOrNull() ?: numPeriods 
                    },
                    label = { Text("Nombre total de périodes *") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { 
                        if (name.isNotBlank() && startDate.isNotBlank() && endDate.isNotBlank() && 
                            validateDate(startDate) == null && validateDate(endDate) == null) {
                            // Use generatedPeriods for onConfirm
                            onConfirm(name, normalizeDate(startDate), normalizeDate(endDate), selectedTypes.toList(), numPeriods, generatedPeriods)
                        }
                    }),
                    supportingText = {
                        val breakdown = selectedTypes.joinToString(" + ") { t ->
                            when(t) {
                                PeriodType.TRIMESTER -> "3 Trimestres"
                                PeriodType.SEMESTER -> "2 Semestres"
                            }
                        }
                        Text("Calculé : $breakdown = $numPeriods périodes")
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = colors.textPrimary,
                        unfocusedTextColor = colors.textPrimary,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = colors.textMuted
                    )
                )
                if (generatedPeriods.isNotEmpty()) {
                    val periodError = validatePeriodsSequence(generatedPeriods, normalizeDate(startDate), normalizeDate(endDate))
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Aperçu des périodes", style = MaterialTheme.typography.titleSmall, color = colors.textPrimary)
                            periodError?.let { Text(it, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error) }
                        }
                        TextButton(onClick = { 
                            if (isManualEditing) {
                                // Reset to auto-generated
                                generatedPeriods = generateDefaultPeriods(normalizeDate(startDate), normalizeDate(endDate), selectedTypes)
                            }
                            isManualEditing = !isManualEditing 
                        }) {
                            Text(if (isManualEditing) "Réinitialiser" else "Modifier manuellement")
                        }
                    }
                    
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = colors.background.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (periodError != null) MaterialTheme.colorScheme.error else colors.divider)
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            generatedPeriods.forEachIndexed { index, period ->
                                if (isManualEditing) {
                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        OutlinedTextField(
                                            value = period.nom,
                                            onValueChange = { newNom ->
                                                generatedPeriods = generatedPeriods.toMutableList().apply {
                                                    this[index] = this[index].copy(nom = newNom)
                                                }
                                            },
                                            modifier = Modifier.fillMaxWidth(),
                                            label = { Text("Nom de la période") },
                                            colors = OutlinedTextFieldDefaults.colors(
                                                unfocusedContainerColor = Color.Transparent,
                                                focusedTextColor = colors.textPrimary,
                                                unfocusedTextColor = colors.textPrimary
                                            )
                                        )
                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            OutlinedTextField(
                                                value = period.dateDebut,
                                                onValueChange = { newDate ->
                                                    generatedPeriods = generatedPeriods.toMutableList().apply {
                                                        this[index] = this[index].copy(dateDebut = newDate)
                                                    }
                                                },
                                                modifier = Modifier.weight(1f),
                                                label = { Text("Début") },
                                                colors = OutlinedTextFieldDefaults.colors(
                                                    unfocusedContainerColor = Color.Transparent,
                                                    focusedTextColor = colors.textPrimary,
                                                    unfocusedTextColor = colors.textPrimary
                                                )
                                            )
                                            OutlinedTextField(
                                                value = period.dateFin,
                                                onValueChange = { newDate ->
                                                    generatedPeriods = generatedPeriods.toMutableList().apply {
                                                        this[index] = this[index].copy(dateFin = newDate)
                                                    }
                                                },
                                                modifier = Modifier.weight(1f),
                                                label = { Text("Fin") },
                                                colors = OutlinedTextFieldDefaults.colors(
                                                    unfocusedContainerColor = Color.Transparent,
                                                    focusedTextColor = colors.textPrimary,
                                                    unfocusedTextColor = colors.textPrimary
                                                )
                                            )
                                        }
                                        if (index < generatedPeriods.size - 1) {
                                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = colors.divider.copy(alpha = 0.5f))
                                        }
                                    }
                                } else {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        val periodColor = when(period.periodType) {
                                            "TRIMESTER" -> MaterialTheme.colorScheme.primary
                                            "SEMESTER" -> MaterialTheme.colorScheme.secondary
                                            else -> MaterialTheme.colorScheme.primary
                                        }
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(periodColor.copy(alpha = 0.1f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("${period.numero}", style = MaterialTheme.typography.labelSmall, color = periodColor)
                                        }
                                        Text(period.nom, style = MaterialTheme.typography.bodyMedium, color = colors.textPrimary, modifier = Modifier.weight(1f))
                                        Text("${period.dateDebut} au ${period.dateFin}", style = MaterialTheme.typography.labelSmall, color = colors.textMuted)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            val rangeError = validateDateRange(normalizeDate(startDate), normalizeDate(endDate))
            val periodError = validatePeriodsSequence(generatedPeriods, normalizeDate(startDate), normalizeDate(endDate))
            val isFormValid = name.isNotBlank() && startDate.isNotBlank() && endDate.isNotBlank() && 
                             validateDate(startDate) == null && validateDate(endDate) == null &&
                             rangeError == null && periodError == null
            Button(
                onClick = { 
                    onConfirm(name, normalizeDate(startDate), normalizeDate(endDate), selectedTypes.toList(), numPeriods, generatedPeriods) 
                },
                enabled = isFormValid,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                )
            ) {
                Text("Créer l'année")
            }
        }
    )
}

private fun validateDate(input: String): String? {
    if (input.isBlank()) return null
    val parts = input.trim().split(Regex("[\\-\\s/]+"))
    if (parts.size != 3) return "Format: AAAA-MM-JJ"
    
    val year = parts[0].toIntOrNull() ?: return "Année invalide"
    val month = parts[1].toIntOrNull() ?: return "Mois invalide"
    val day = parts[2].toIntOrNull() ?: return "Jour invalide"
    
    if (month !in 1..12) return "Mois (1-12)"
    if (year < 1900 || year > 2100) return "Année (1900-2100)"
    
    val maxDays = when (month) {
        1, 3, 5, 7, 8, 10, 12 -> 31
        4, 6, 9, 11 -> 30
        2 -> if ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)) 29 else 28
        else -> 0
    }
    
    if (day !in 1..maxDays) return "Jour (1-$maxDays)"
    
    return null
}

private fun normalizeDate(input: String): String {
    val parts = input.trim().split(Regex("[\\-\\s/]+"))
    if (parts.size != 3) return input
    return "${parts[0]}-${parts[1].padStart(2, '0')}-${parts[2].padStart(2, '0')}"
}

private fun validateDateRange(start: String, end: String): String? {
    val s = try { LocalDate.parse(start) } catch (e: Exception) { return null }
    val e = try { LocalDate.parse(end) } catch (e: Exception) { return null }
    
    if (e <= s) return "La date de fin doit être après la date de début"
    
    val days = e.toEpochDays() - s.toEpochDays()
    if (days > 548) return "La durée ne peut pas dépasser 18 mois"
    if (days < 90) return "La durée doit être d'au moins 3 mois"
    
    return null
}

private fun validatePeriodsSequence(periods: List<AcademicPeriodDto>, yearStart: String, yearEnd: String): String? {
    val yS = try { LocalDate.parse(yearStart) } catch (e: Exception) { return null }
    val yE = try { LocalDate.parse(yearEnd) } catch (e: Exception) { return null }
    
    // Group periods by type to validate sequences independently
    val periodsByType = periods.groupBy { it.periodType }
    
    periodsByType.forEach { (_, typePeriods) ->
        // Sort by number or date to ensure correct order
        val sortedPeriods = typePeriods.sortedBy { it.numero }
        var lastEnd: LocalDate? = null
        
        sortedPeriods.forEach { period ->
            val pS = try { LocalDate.parse(period.dateDebut) } catch (e: Exception) { return "Format de date invalide (Début) pour ${period.nom}" }
            val pE = try { LocalDate.parse(period.dateFin) } catch (e: Exception) { return "Format de date invalide (Fin) pour ${period.nom}" }
            
            if (pE <= pS) return "${period.nom} : La fin doit être après le début"
            if (pS < yS) return "${period.nom} commence avant l'année scolaire"
            if (pE > yE) return "${period.nom} finit après l'année scolaire"
            
            lastEnd?.let {
                if (pS <= it) return "${period.nom} commence avant la fin de la période précédente (${period.periodType})" // Added overlap check
            }
            lastEnd = pE
        }
    }
    
    return null
}

fun generateDefaultPeriods(
    startDate: String,
    endDate: String,
    types: Set<PeriodType>
): List<AcademicPeriodDto> {
    val s = try { LocalDate.parse(startDate) } catch (e: Exception) { return emptyList() }
    val e = try { LocalDate.parse(endDate) } catch (e: Exception) { return emptyList() }
    
    val periods = mutableListOf<AcademicPeriodDto>()
    
    // Generate periods for EACH type independently, covering the full school year
    types.forEach { type ->
        val count = when(type) {
            PeriodType.TRIMESTER -> 3
            PeriodType.SEMESTER -> 2
        }
        
        val totalDays = (e.toEpochDays() - s.toEpochDays()).toLong()
        val daysPerPeriod = totalDays / count
        
        for (i in 1..count) {
            val pStart = LocalDate.fromEpochDays((s.toEpochDays() + (i - 1) * daysPerPeriod).toInt())
            val pEnd = if (i == count) e else LocalDate.fromEpochDays((s.toEpochDays() + i * daysPerPeriod - 1).toInt())
            
            val typeLabel = when(type) {
                PeriodType.TRIMESTER -> "Trimestre"
                PeriodType.SEMESTER -> "Semestre"
            }

            periods.add(AcademicPeriodDto(
                tenantId = 0,
                anneeScolaireId = 0,
                nom = "$typeLabel $i",
                numero = i,
                dateDebut = pStart.toString(),
                dateFin = pEnd.toString(),
                periodType = type.name,
                isActif = false
            ))
        }
    }
    
    return periods
}

@Composable
fun ArchiveYearDialog(
    yearName: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.onPreviewKeyEvent { event ->
            if (event.key == Key.Escape && event.type == KeyEventType.KeyDown) {
                onDismiss()
                true
            } else if (event.key == Key.Enter && event.type == KeyEventType.KeyDown) {
                onConfirm()
                true
            } else {
                false
            }
        },
        title = { Text("Archiver l'année scolaire") },
        text = { Text("Êtes-vous sûr de vouloir archiver l'année $yearName ? Cette action est irréversible et clôturera toutes les activités de cette année.") },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Archiver")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )
}

@Composable
fun EditSchoolYearDialog(
    year: SchoolYear,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, List<PeriodType>, Int, List<AcademicPeriodDto>?) -> Unit,
    colors: DashboardColors
) {
    var name by remember { mutableStateOf(year.name) }
    var startDate by remember { mutableStateOf(year.startDate) }
    var endDate by remember { mutableStateOf(year.endDate) }
    var selectedTypes by remember { mutableStateOf(year.periodTypes.toSet()) }
    var numPeriods by remember { mutableStateOf(year.numberOfPeriods) }
    var generatedPeriods by remember { mutableStateOf<List<AcademicPeriodDto>>(emptyList()) }
    var isManualEditing by remember { mutableStateOf(false) }

    // Auto-generation logic (same as NewSchoolYearDialog)
    LaunchedEffect(startDate, endDate, selectedTypes) {
        if (!isManualEditing) {
            val startValid = try { LocalDate.parse(normalizeDate(startDate)); true } catch (e: Exception) { false }
            val endValid = try { LocalDate.parse(normalizeDate(endDate)); true } catch (e: Exception) { false }
            
            if (startValid && endValid && name.isNotBlank()) {
                generatedPeriods = generateDefaultPeriods(
                    normalizeDate(startDate),
                    normalizeDate(endDate),
                    selectedTypes
                )
                numPeriods = generatedPeriods.size
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.onPreviewKeyEvent { event ->
            if (event.key == Key.Escape && event.type == KeyEventType.KeyDown) {
                onDismiss()
                true
            } else if (event.key == Key.Enter && event.type == KeyEventType.KeyDown) {
                val rangeError = validateDateRange(normalizeDate(startDate), normalizeDate(endDate))
                val periodError = validatePeriodsSequence(generatedPeriods, normalizeDate(startDate), normalizeDate(endDate))
                val isFormValid = name.isNotBlank() && startDate.isNotBlank() && endDate.isNotBlank() && 
                                 validateDate(startDate) == null && validateDate(endDate) == null &&
                                 rangeError == null && periodError == null
                if (isFormValid) {
                    val normalizedStart = normalizeDate(startDate)
                    val normalizedEnd = normalizeDate(endDate)
                    onConfirm(name, normalizedStart, normalizedEnd, selectedTypes.toList(), numPeriods, generatedPeriods)
                }
                true
            } else {
                false
            }
        },
        containerColor = colors.card,
        textContentColor = colors.textPrimary,
        titleContentColor = colors.textPrimary,
        title = { 
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Modifier l'Année Scolaire")
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Fermer", tint = colors.textMuted)
                }
            }
        },
        text = {
            val focusManager = LocalFocusManager.current
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Libellé (ex: 2025-2026) *") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(androidx.compose.ui.focus.FocusDirection.Down) }),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = colors.textPrimary,
                        unfocusedTextColor = colors.textPrimary,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = colors.textMuted
                    )
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val startError = validateDate(startDate)
                    OutlinedTextField(
                        value = startDate,
                        onValueChange = { startDate = it },
                        label = { Text("Début (AAAA-MM-JJ) *") },
                        modifier = Modifier.weight(1f),
                        isError = startError != null || (if (validateDate(startDate) == null && validateDate(endDate) == null) validateDateRange(normalizeDate(startDate), normalizeDate(endDate)) != null else false),
                        supportingText = { startError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(androidx.compose.ui.focus.FocusDirection.Right) }),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = colors.textPrimary,
                            unfocusedTextColor = colors.textPrimary,
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedLabelColor = MaterialTheme.colorScheme.primary,
                            unfocusedLabelColor = colors.textMuted
                        )
                    )
                    val endError = validateDate(endDate)
                    OutlinedTextField(
                        value = endDate,
                        onValueChange = { endDate = it },
                        label = { Text("Fin (AAAA-MM-JJ) *") },
                        modifier = Modifier.weight(1f),
                        isError = endError != null || (if (validateDate(startDate) == null && validateDate(endDate) == null) validateDateRange(normalizeDate(startDate), normalizeDate(endDate)) != null else false),
                        supportingText = { 
                            val rangeError = if (validateDate(startDate) == null && validateDate(endDate) == null) validateDateRange(normalizeDate(startDate), normalizeDate(endDate)) else null
                            endError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                            ?: rangeError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                        },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(androidx.compose.ui.focus.FocusDirection.Down) }),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = colors.textPrimary,
                            unfocusedTextColor = colors.textPrimary,
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedLabelColor = MaterialTheme.colorScheme.primary,
                            unfocusedLabelColor = colors.textMuted
                        )
                    )
                }
                Text("Types de périodes actifs", style = MaterialTheme.typography.labelMedium, color = colors.textMuted)
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PeriodType.values().forEach { pType ->
                        val isSelected = selectedTypes.contains(pType)
                        FilterChip(
                            selected = isSelected,
                            onClick = { 
                                val current = selectedTypes
                                selectedTypes = if (isSelected) {
                                    if (current.size > 1) current - pType else current
                                } else {
                                    current + pType
                                }
                                
                                numPeriods = selectedTypes.sumOf { t: PeriodType ->
                                    when(t) {
                                        PeriodType.TRIMESTER -> 3
                                        PeriodType.SEMESTER -> 2
                                    }
                                }
                            },
                            label = { Text(pType.toFrench()) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                selectedLabelColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }
                OutlinedTextField(
                    value = numPeriods.toString(),
                    onValueChange = { 
                        numPeriods = it.toIntOrNull() ?: numPeriods 
                    },
                    label = { Text("Nombre total de périodes *") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { 
                        val rangeError = validateDateRange(normalizeDate(startDate), normalizeDate(endDate))
                        val periodError = validatePeriodsSequence(generatedPeriods, normalizeDate(startDate), normalizeDate(endDate))
                        if (name.isNotBlank() && startDate.isNotBlank() && endDate.isNotBlank() && 
                            validateDate(startDate) == null && validateDate(endDate) == null &&
                            rangeError == null && periodError == null) {
                            onConfirm(name, normalizeDate(startDate), normalizeDate(endDate), selectedTypes.toList(), numPeriods, generatedPeriods)
                        }
                    }),
                    supportingText = {
                        val breakdown = selectedTypes.joinToString(" + ") { t ->
                            when(t) {
                                PeriodType.TRIMESTER -> "3 Trimestres"
                                PeriodType.SEMESTER -> "2 Semestres"
                            }
                        }
                        Text("Calculé : $breakdown = $numPeriods périodes")
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = colors.textPrimary,
                        unfocusedTextColor = colors.textPrimary,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = colors.textMuted
                    )
                )
                
                // Period Preview Section
                if (generatedPeriods.isNotEmpty()) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Périodes générées (${generatedPeriods.size})",
                            style = MaterialTheme.typography.labelMedium,
                            color = colors.textMuted
                        )
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                if (isManualEditing) "Édition manuelle" else "Auto",
                                style = MaterialTheme.typography.labelSmall,
                                color = colors.textMuted
                            )
                            Switch(
                                checked = isManualEditing,
                                onCheckedChange = { isManualEditing = it },
                                modifier = Modifier.scale(0.8f)
                            )
                        }
                    }
                    
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 200.dp)
                            .verticalScroll(rememberScrollState())
                            .background(colors.card.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        generatedPeriods.forEachIndexed { index, period ->
                            if (isManualEditing) {
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    OutlinedTextField(
                                        value = period.nom,
                                        onValueChange = { newNom ->
                                            generatedPeriods = generatedPeriods.toMutableList().apply {
                                                this[index] = this[index].copy(nom = newNom)
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        label = { Text("Nom de la période") },
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                            focusedTextColor = colors.textPrimary,
                                            unfocusedTextColor = colors.textPrimary,
                                            focusedLabelColor = MaterialTheme.colorScheme.primary,
                                            unfocusedLabelColor = colors.textMuted
                                        )
                                    )
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        OutlinedTextField(
                                            value = period.dateDebut,
                                            onValueChange = { newDate ->
                                                generatedPeriods = generatedPeriods.toMutableList().apply {
                                                    this[index] = this[index].copy(dateDebut = newDate)
                                                }
                                            },
                                            modifier = Modifier.weight(1f),
                                            label = { Text("Début") },
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                                focusedTextColor = colors.textPrimary,
                                                unfocusedTextColor = colors.textPrimary,
                                                focusedLabelColor = MaterialTheme.colorScheme.primary,
                                                unfocusedLabelColor = colors.textMuted
                                            )
                                        )
                                        OutlinedTextField(
                                            value = period.dateFin,
                                            onValueChange = { newDate ->
                                                generatedPeriods = generatedPeriods.toMutableList().apply {
                                                    this[index] = this[index].copy(dateFin = newDate)
                                                }
                                            },
                                            modifier = Modifier.weight(1f),
                                            label = { Text("Fin") },
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                                focusedTextColor = colors.textPrimary,
                                                unfocusedTextColor = colors.textPrimary,
                                                focusedLabelColor = MaterialTheme.colorScheme.primary,
                                                unfocusedLabelColor = colors.textMuted
                                            )
                                        )
                                    }
                                    if (index < generatedPeriods.size - 1) {
                                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = colors.divider.copy(alpha = 0.5f))
                                    }
                                }
                            } else {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val periodColor = when(period.periodType) {
                                        "TRIMESTER" -> MaterialTheme.colorScheme.primary
                                        "SEMESTER" -> MaterialTheme.colorScheme.secondary
                                        else -> MaterialTheme.colorScheme.primary
                                    }
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(periodColor.copy(alpha = 0.1f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("${period.numero}", style = MaterialTheme.typography.labelSmall, color = periodColor)
                                    }
                                    Text(
                                        period.nom,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = colors.textPrimary,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        "${period.dateDebut} → ${period.dateFin}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = colors.textMuted
                                    )
                                }
                            }
                        }
                    }
                    
                    val periodError = validatePeriodsSequence(generatedPeriods, normalizeDate(startDate), normalizeDate(endDate))
                    if (periodError != null) {
                        Text(
                            periodError,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
                
                Text(
                    "* Champs obligatoires",
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textMuted
                )
            }
        },
        confirmButton = {
            val rangeError = validateDateRange(normalizeDate(startDate), normalizeDate(endDate))
            val periodError = validatePeriodsSequence(generatedPeriods, normalizeDate(startDate), normalizeDate(endDate))
            val isFormValid = name.isNotBlank() && startDate.isNotBlank() && endDate.isNotBlank() && 
                             validateDate(startDate) == null && validateDate(endDate) == null &&
                             rangeError == null && periodError == null
            Button(
                onClick = { 
                    onConfirm(name, normalizeDate(startDate), normalizeDate(endDate), selectedTypes.toList(), numPeriods, generatedPeriods) 
                },
                enabled = isFormValid,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                )
            ) {
                Text("Enregistrer")
            }
        }
    )
}

@Composable
fun NewPeriodDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Int, String, String, PeriodType) -> Unit,
    colors: DashboardColors
) {
    var name by remember { mutableStateOf("") }
    var number by remember { mutableStateOf(1) }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(PeriodType.TRIMESTER) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = colors.card,
        textContentColor = colors.textPrimary,
        titleContentColor = colors.textPrimary,
        title = { Text("Nouvelle Période") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nom de la période (ex: Trimestre 1) *") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = number.toString(),
                    onValueChange = { number = it.toIntOrNull() ?: number },
                    label = { Text("Numéro d'ordre *") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val startError = validateDate(startDate)
                    OutlinedTextField(
                        value = startDate,
                        onValueChange = { startDate = it },
                        label = { Text("Début (AAAA-MM-JJ) *") },
                        modifier = Modifier.weight(1f),
                        isError = startError != null || (if (validateDate(startDate) == null && validateDate(endDate) == null) validateDateRange(normalizeDate(startDate), normalizeDate(endDate)) != null else false),
                        supportingText = { startError?.let { Text(it, color = MaterialTheme.colorScheme.error) } }
                    )
                    val endError = validateDate(endDate)
                    OutlinedTextField(
                        value = endDate,
                        onValueChange = { endDate = it },
                        label = { Text("Fin (AAAA-MM-JJ) *") },
                        modifier = Modifier.weight(1f),
                        isError = endError != null || (if (validateDate(startDate) == null && validateDate(endDate) == null) validateDateRange(normalizeDate(startDate), normalizeDate(endDate)) != null else false),
                        supportingText = { endError?.let { Text(it, color = MaterialTheme.colorScheme.error) } }
                    )
                }

                Text("Type de période", style = MaterialTheme.typography.labelMedium, color = colors.textMuted)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PeriodType.values().forEach { pType ->
                        FilterChip(
                            selected = type == pType,
                            onClick = { type = pType },
                            label = { Text(pType.toFrench()) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            val isFormValid = name.isNotBlank() && startDate.isNotBlank() && endDate.isNotBlank() && 
                             validateDate(startDate) == null && validateDate(endDate) == null
            Button(
                onClick = { onConfirm(name, number, normalizeDate(startDate), normalizeDate(endDate), type) },
                enabled = isFormValid
            ) {
                Text("Créer la période")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annuler") }
        }
    )
}

@Composable
fun EditPeriodDialog(
    period: AcademicPeriod,
    onDismiss: () -> Unit,
    onConfirm: (String, Int, String, String, PeriodType) -> Unit,
    colors: DashboardColors
) {
    var name by remember { mutableStateOf(period.name) }
    var number by remember { mutableStateOf(period.periodNumber) }
    var startDate by remember { mutableStateOf(period.startDate) }
    var endDate by remember { mutableStateOf(period.endDate) }
    var type by remember { mutableStateOf(period.type) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = colors.card,
        textContentColor = colors.textPrimary,
        titleContentColor = colors.textPrimary,
        title = { Text("Modifier la Période") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nom de la période *") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = number.toString(),
                    onValueChange = { number = it.toIntOrNull() ?: number },
                    label = { Text("Numéro d'ordre *") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val startError = validateDate(startDate)
                    OutlinedTextField(
                        value = startDate,
                        onValueChange = { startDate = it },
                        label = { Text("Début (AAAA-MM-JJ) *") },
                        modifier = Modifier.weight(1f),
                        isError = startError != null || (if (validateDate(startDate) == null && validateDate(endDate) == null) validateDateRange(normalizeDate(startDate), normalizeDate(endDate)) != null else false),
                        supportingText = { startError?.let { Text(it, color = MaterialTheme.colorScheme.error) } }
                    )
                    val endError = validateDate(endDate)
                    OutlinedTextField(
                        value = endDate,
                        onValueChange = { endDate = it },
                        label = { Text("Fin (AAAA-MM-JJ) *") },
                        modifier = Modifier.weight(1f),
                        isError = endError != null || (if (validateDate(startDate) == null && validateDate(endDate) == null) validateDateRange(normalizeDate(startDate), normalizeDate(endDate)) != null else false),
                        supportingText = { endError?.let { Text(it, color = MaterialTheme.colorScheme.error) } }
                    )
                }

                Text("Type de période", style = MaterialTheme.typography.labelMedium, color = colors.textMuted)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PeriodType.values().forEach { pType ->
                        FilterChip(
                            selected = type == pType,
                            onClick = { type = pType },
                            label = { Text(pType.toFrench()) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            val isFormValid = name.isNotBlank() && startDate.isNotBlank() && endDate.isNotBlank() && 
                             validateDate(startDate) == null && validateDate(endDate) == null
            Button(
                onClick = { onConfirm(name, number, normalizeDate(startDate), normalizeDate(endDate), type) },
                enabled = isFormValid
            ) {
                Text("Enregistrer")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annuler") }
        }
    )
}

@Composable
fun DeletePeriodDialog(
    periodName: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Supprimer la période") },
        text = { Text("Voulez-vous vraiment supprimer la période $periodName ? Cette action est définitive.") },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Supprimer")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annuler") }
        }
    )
}
