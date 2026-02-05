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
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.border
import androidx.compose.ui.draw.scale
import com.ecolix.data.models.*
import com.ecolix.atschool.api.AcademicPeriodDto
import kotlinx.datetime.*
import androidx.compose.animation.*
import com.ecolix.presentation.components.FormTextField
import com.ecolix.presentation.components.SectionHeader

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
            },
            colors = colors
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
    colors: DashboardColors,
    onEdit: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null
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

        if (onEdit != null && onDelete != null) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Edit, contentDescription = "Modifier", tint = colors.textMuted, modifier = Modifier.size(18.dp))
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Supprimer", tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f), modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
private fun HolidayItem(
    holiday: Holiday,
    colors: DashboardColors,
    onEdit: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null
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

        if (onEdit != null && onDelete != null) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Edit, contentDescription = "Modifier", tint = colors.textMuted, modifier = Modifier.size(18.dp))
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Supprimer", tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f), modifier = Modifier.size(18.dp))
                }
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
    onSetDefault: (String) -> Unit,
    onSetStatus: (String, AcademicStatus) -> Unit
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
                },
                onSetStatus = { onSetStatus(year.id, it) }
            )
        }
    }
}

@Composable
private fun SchoolYearCard(
    year: SchoolYear,
    colors: DashboardColors,
    onClick: () -> Unit,
    onEdit: (String, String, String, List<PeriodType>, Int, List<AcademicPeriodDto>?) -> Unit,
    onSetStatus: (AcademicStatus) -> Unit
) {
    var showEditDialog by remember { mutableStateOf(false) }
    var showStatusMenu by remember { mutableStateOf(false) }

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
                    .clickable { showStatusMenu = true }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    year.status.toFrench(),
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = year.status.toColor()
                )
                
                DropdownMenu(
                    expanded = showStatusMenu,
                    onDismissRequest = { showStatusMenu = false },
                    modifier = Modifier.background(colors.card)
                ) {
                    AcademicStatus.values().filter { it != AcademicStatus.ARCHIVED }.forEach { status ->
                        DropdownMenuItem(
                            text = { Text(status.toFrench(), color = colors.textPrimary) },
                            onClick = {
                                onSetStatus(status)
                                showStatusMenu = false
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Circle,
                                    contentDescription = null,
                                    tint = status.toColor(),
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        )
                    }
                }
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
    onDeletePeriod: (String) -> Unit,
    onSetStatus: (String, AcademicStatus) -> Unit
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
                onDelete = { onDeletePeriod(period.id) },
                onSetStatus = { onSetStatus(period.id, it) }
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
    onDelete: () -> Unit,
    onSetStatus: (AcademicStatus) -> Unit
) {
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showStatusMenu by remember { mutableStateOf(false) }

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
                            .clickable { showStatusMenu = true }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            period.status.toFrench(),
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = period.status.toColor()
                        )
                        
                        DropdownMenu(
                            expanded = showStatusMenu,
                            onDismissRequest = { showStatusMenu = false },
                            modifier = Modifier.background(colors.card)
                        ) {
                            AcademicStatus.values().filter { it != AcademicStatus.ARCHIVED }.forEach { status ->
                                DropdownMenuItem(
                                    text = { Text(status.toFrench(), color = colors.textPrimary) },
                                    onClick = {
                                        onSetStatus(status)
                                        showStatusMenu = false
                                    },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.Circle,
                                            contentDescription = null,
                                            tint = status.toColor(),
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                )
                            }
                        }
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
        title = { 
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Nouvelle Année Scolaire",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = colors.textPrimary
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Fermer", tint = colors.textMuted)
                }
            }
        },
        text = {
            val focusManager = LocalFocusManager.current
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Section: Informations de Base
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    SectionHeader("Informations de Base", colors)
                    
                    FormTextField(
                        label = "Libellé (ex: 2025-2026) *",
                        value = name,
                        onValueChange = { name = it },
                        colors = colors,
                        icon = Icons.Default.Badge,
                        placeholder = "2025-2026"
                    )
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        val startError = validateDate(startDate)
                        FormTextField(
                            label = "Date de début *",
                            value = startDate,
                            onValueChange = { startDate = it; isManualEditing = false },
                            colors = colors,
                            modifier = Modifier.weight(1f),
                            placeholder = "AAAA-MM-JJ",
                            icon = Icons.Default.CalendarToday,
                            isError = startError != null
                        )
                        
                        val endError = validateDate(endDate)
                        FormTextField(
                            label = "Date de fin *",
                            value = endDate,
                            onValueChange = { endDate = it; isManualEditing = false },
                            colors = colors,
                            modifier = Modifier.weight(1f),
                            placeholder = "AAAA-MM-JJ",
                            icon = Icons.Default.Event,
                            isError = endError != null
                        )
                    }
                    
                    val rangeError = if (validateDate(startDate) == null && validateDate(endDate) == null) 
                        validateDateRange(normalizeDate(startDate), normalizeDate(endDate)) else null
                    if (rangeError != null) {
                        Text(rangeError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
                    }
                }

                // Section: Configuration des Périodes
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    SectionHeader("Configuration des Périodes", colors)
                    
                    Text(
                        "Choisissez les types de périodes à activer pour cette année scolaire.",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textMuted
                    )
                    
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
                                        current - pType
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
                                    selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                    selectedLabelColor = MaterialTheme.colorScheme.primary
                                )
                            )
                        }
                    }
                    
                    FormTextField(
                        label = "Nombre total de périodes *",
                        value = numPeriods.toString(),
                        onValueChange = { numPeriods = it.toIntOrNull() ?: numPeriods },
                        colors = colors,
                        icon = Icons.Default.Assignment,
                        keyboardType = KeyboardType.Number,
                        placeholder = "3"
                    )
                    
                    val breakdown = selectedTypes.joinToString(" + ") { t ->
                        when(t) {
                            PeriodType.TRIMESTER -> "3 Trimestres"
                            PeriodType.SEMESTER -> "2 Semestres"
                        }
                    }
                    Text(
                        "Calculé : $breakdown = $numPeriods périodes",
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.textMuted
                    )
                }

                // Section: Aperçu des Périodes
                if (generatedPeriods.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        val periodError = validatePeriodsSequence(generatedPeriods, normalizeDate(startDate), normalizeDate(endDate))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                SectionHeader("Aperçu des Périodes", colors)
                                periodError?.let { Text(it, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error) }
                            }
                            TextButton(onClick = { 
                                if (isManualEditing) {
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
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (periodError != null) MaterialTheme.colorScheme.error.copy(alpha = 0.5f) else colors.divider.copy(alpha = 0.5f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                generatedPeriods.forEachIndexed { index, period ->
                                    if (isManualEditing) {
                                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                            FormTextField(
                                                label = "Nom de la période",
                                                value = period.nom,
                                                onValueChange = { newNom ->
                                                    generatedPeriods = generatedPeriods.toMutableList().apply {
                                                        this[index] = this[index].copy(nom = newNom)
                                                    }
                                                },
                                                colors = colors,
                                                icon = Icons.Default.Edit
                                            )
                                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                FormTextField(
                                                    label = "Début",
                                                    value = period.dateDebut,
                                                    onValueChange = { newDate ->
                                                        generatedPeriods = generatedPeriods.toMutableList().apply {
                                                            this[index] = this[index].copy(dateDebut = newDate)
                                                        }
                                                    },
                                                    colors = colors,
                                                    modifier = Modifier.weight(1f),
                                                    placeholder = "AAAA-MM-JJ"
                                                )
                                                FormTextField(
                                                    label = "Fin",
                                                    value = period.dateFin,
                                                    onValueChange = { newDate ->
                                                        generatedPeriods = generatedPeriods.toMutableList().apply {
                                                            this[index] = this[index].copy(dateFin = newDate)
                                                        }
                                                    },
                                                    colors = colors,
                                                    modifier = Modifier.weight(1f),
                                                    placeholder = "AAAA-MM-JJ"
                                                )
                                            }
                                            if (index < generatedPeriods.size - 1) {
                                                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = colors.divider.copy(alpha = 0.3f))
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
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(periodColor.copy(alpha = 0.15f)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text("${period.numero}", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = periodColor)
                                            }
                                            Text(period.nom, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = colors.textPrimary, modifier = Modifier.weight(1f))
                                            Text("${period.dateDebut} au ${period.dateFin}", style = MaterialTheme.typography.labelSmall, color = colors.textMuted)
                                        }
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
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                ),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text("Créer l'année", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler", color = colors.textMuted)
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
                status = "UPCOMING"
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
        title = { 
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Modifier l'Année Scolaire",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = colors.textPrimary
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Fermer", tint = colors.textMuted)
                }
            }
        },
        text = {
            val focusManager = LocalFocusManager.current
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Section: Informations de Base
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    SectionHeader("Informations de Base", colors)
                    
                    FormTextField(
                        label = "Libellé (ex: 2025-2026) *",
                        value = name,
                        onValueChange = { name = it },
                        colors = colors,
                        icon = Icons.Default.Badge,
                        placeholder = "2025-2026"
                    )
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        val startError = validateDate(startDate)
                        FormTextField(
                            label = "Date de début *",
                            value = startDate,
                            onValueChange = { startDate = it },
                            colors = colors,
                            modifier = Modifier.weight(1f),
                            placeholder = "AAAA-MM-JJ",
                            icon = Icons.Default.CalendarToday,
                            isError = startError != null
                        )
                        
                        val endError = validateDate(endDate)
                        FormTextField(
                            label = "Date de fin *",
                            value = endDate,
                            onValueChange = { endDate = it },
                            colors = colors,
                            modifier = Modifier.weight(1f),
                            placeholder = "AAAA-MM-JJ",
                            icon = Icons.Default.Event,
                            isError = endError != null
                        )
                    }
                    
                    val rangeError = if (validateDate(startDate) == null && validateDate(endDate) == null) 
                        validateDateRange(normalizeDate(startDate), normalizeDate(endDate)) else null
                    if (rangeError != null) {
                        Text(rangeError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
                    }
                }

                // Section: Configuration des Périodes
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    SectionHeader("Configuration des Périodes", colors)
                    
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
                                        current - pType
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
                                    selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                    selectedLabelColor = MaterialTheme.colorScheme.primary
                                )
                            )
                        }
                    }
                    
                    FormTextField(
                        label = "Nombre total de périodes *",
                        value = numPeriods.toString(),
                        onValueChange = { numPeriods = it.toIntOrNull() ?: numPeriods },
                        colors = colors,
                        icon = Icons.Default.Assignment,
                        keyboardType = KeyboardType.Number,
                        placeholder = "3"
                    )
                    
                    val breakdown = selectedTypes.joinToString(" + ") { t ->
                        when(t) {
                            PeriodType.TRIMESTER -> "3 Trimestres"
                            PeriodType.SEMESTER -> "2 Semestres"
                        }
                    }
                    Text(
                        "Calculé : $breakdown = $numPeriods périodes",
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.textMuted
                    )
                }
                
                // Period Preview Section
                if (generatedPeriods.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        val periodError = validatePeriodsSequence(generatedPeriods, normalizeDate(startDate), normalizeDate(endDate))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            SectionHeader("Aperçu des Périodes", colors)
                            
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    if (isManualEditing) "Édition manuelle" else "Automatique",
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
                        
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 240.dp),
                            color = colors.background.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (periodError != null) MaterialTheme.colorScheme.error.copy(alpha = 0.5f) else colors.divider.copy(alpha = 0.5f))
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                generatedPeriods.forEachIndexed { index, period ->
                                    if (isManualEditing) {
                                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                            FormTextField(
                                                label = "Nom de la période",
                                                value = period.nom,
                                                onValueChange = { newNom ->
                                                    generatedPeriods = generatedPeriods.toMutableList().apply {
                                                        this[index] = this[index].copy(nom = newNom)
                                                    }
                                                },
                                                colors = colors,
                                                icon = Icons.Default.Edit
                                            )
                                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                FormTextField(
                                                    label = "Début",
                                                    value = period.dateDebut,
                                                    onValueChange = { newDate ->
                                                        generatedPeriods = generatedPeriods.toMutableList().apply {
                                                            this[index] = this[index].copy(dateDebut = newDate)
                                                        }
                                                    },
                                                    colors = colors,
                                                    modifier = Modifier.weight(1f),
                                                    placeholder = "AAAA-MM-JJ"
                                                )
                                                FormTextField(
                                                    label = "Fin",
                                                    value = period.dateFin,
                                                    onValueChange = { newDate ->
                                                        generatedPeriods = generatedPeriods.toMutableList().apply {
                                                            this[index] = this[index].copy(dateFin = newDate)
                                                        }
                                                    },
                                                    colors = colors,
                                                    modifier = Modifier.weight(1f),
                                                    placeholder = "AAAA-MM-JJ"
                                                )
                                            }
                                            if (index < generatedPeriods.size - 1) {
                                                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = colors.divider.copy(alpha = 0.3f))
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
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(periodColor.copy(alpha = 0.15f)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text("${period.numero}", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = periodColor)
                                            }
                                            Text(
                                                period.nom,
                                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
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
                        }
                        
                        periodError?.let {
                            Text(
                                it,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
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
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                ),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text("Enregistrer", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler", color = colors.textMuted)
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
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Nouvelle Période",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = colors.textPrimary
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Fermer", tint = colors.textMuted)
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
                // Section: Informations
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    SectionHeader("Informations de la Période", colors)
                    
                    FormTextField(
                        label = "Nom de la période (ex: Trimestre 1) *",
                        value = name,
                        onValueChange = { name = it },
                        colors = colors,
                        icon = Icons.Default.Badge,
                        placeholder = "Trimestre 1"
                    )
                    
                    FormTextField(
                        label = "Numéro d'ordre *",
                        value = number.toString(),
                        onValueChange = { number = it.toIntOrNull() ?: number },
                        colors = colors,
                        icon = Icons.Default.FormatListNumbered,
                        keyboardType = KeyboardType.Number,
                        placeholder = "1"
                    )
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        val startError = validateDate(startDate)
                        FormTextField(
                            label = "Date de début *",
                            value = startDate,
                            onValueChange = { startDate = it },
                            colors = colors,
                            modifier = Modifier.weight(1f),
                            placeholder = "AAAA-MM-JJ",
                            icon = Icons.Default.CalendarToday,
                            isError = startError != null
                        )
                        
                        val endError = validateDate(endDate)
                        FormTextField(
                            label = "Date de fin *",
                            value = endDate,
                            onValueChange = { endDate = it },
                            colors = colors,
                            modifier = Modifier.weight(1f),
                            placeholder = "AAAA-MM-JJ",
                            icon = Icons.Default.Event,
                            isError = endError != null
                        )
                    }
                }

                // Section: Type
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    SectionHeader("Type de Période", colors)
                    
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PeriodType.values().forEach { pType ->
                            FilterChip(
                                selected = type == pType,
                                onClick = { type = pType },
                                label = { Text(pType.toFrench()) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                    selectedLabelColor = MaterialTheme.colorScheme.primary
                                )
                            )
                        }
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
            val isFormValid = name.isNotBlank() && startDate.isNotBlank() && endDate.isNotBlank() && 
                             validateDate(startDate) == null && validateDate(endDate) == null
            Button(
                onClick = { onConfirm(name, number, normalizeDate(startDate), normalizeDate(endDate), type) },
                enabled = isFormValid,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                ),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text("Créer la période", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler", color = colors.textMuted)
            }
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
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Modifier la Période",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = colors.textPrimary
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Fermer", tint = colors.textMuted)
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
                // Section: Informations
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    SectionHeader("Informations de la Période", colors)
                    
                    FormTextField(
                        label = "Nom de la période *",
                        value = name,
                        onValueChange = { name = it },
                        colors = colors,
                        icon = Icons.Default.Badge,
                        placeholder = "Trimestre 1"
                    )
                    
                    FormTextField(
                        label = "Numéro d'ordre *",
                        value = number.toString(),
                        onValueChange = { number = it.toIntOrNull() ?: number },
                        colors = colors,
                        icon = Icons.Default.FormatListNumbered,
                        keyboardType = KeyboardType.Number,
                        placeholder = "1"
                    )
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        val startError = validateDate(startDate)
                        FormTextField(
                            label = "Date de début *",
                            value = startDate,
                            onValueChange = { startDate = it },
                            colors = colors,
                            modifier = Modifier.weight(1f),
                            placeholder = "AAAA-MM-JJ",
                            icon = Icons.Default.CalendarToday,
                            isError = startError != null
                        )
                        
                        val endError = validateDate(endDate)
                        FormTextField(
                            label = "Date de fin *",
                            value = endDate,
                            onValueChange = { endDate = it },
                            colors = colors,
                            modifier = Modifier.weight(1f),
                            placeholder = "AAAA-MM-JJ",
                            icon = Icons.Default.Event,
                            isError = endError != null
                        )
                    }
                }

                // Section: Type
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    SectionHeader("Type de Période", colors)
                    
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PeriodType.values().forEach { pType ->
                            FilterChip(
                                selected = type == pType,
                                onClick = { type = pType },
                                label = { Text(pType.toFrench()) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                    selectedLabelColor = MaterialTheme.colorScheme.primary
                                )
                            )
                        }
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
            val isFormValid = name.isNotBlank() && startDate.isNotBlank() && endDate.isNotBlank() && 
                             validateDate(startDate) == null && validateDate(endDate) == null
            Button(
                onClick = { onConfirm(name, number, normalizeDate(startDate), normalizeDate(endDate), type) },
                enabled = isFormValid,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                ),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text("Enregistrer", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler", color = colors.textMuted)
            }
        }
    )
}

@Composable
fun ArchiveYearDialog(
    yearName: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    colors: DashboardColors
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = colors.card,
        icon = { Icon(Icons.Default.Archive, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
        title = {
            Text(
                "Archiver l'Année Scolaire",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = colors.textPrimary
            )
        },
        text = {
            Text(
                "Êtes-vous sûr de vouloir archiver l'année scolaire \"$yearName\" ? Cette action est réversible mais l'année ne sera plus active.",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textPrimary
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Archiver")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler", color = colors.textMuted)
            }
        }
    )
}

@Composable
fun DeletePeriodDialog(
    periodName: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    colors: DashboardColors
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = colors.card,
        icon = { Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
        title = {
            Text(
                "Supprimer la période",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = colors.textPrimary
            )
        },
        text = {
            Text(
                "Voulez-vous vraiment supprimer la période $periodName ? Cette action est définitive.",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textPrimary
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Supprimer")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler", color = colors.textMuted)
            }
        }
    )
}

@Composable
fun CalendarTab(
    state: AcademicUiState,
    colors: DashboardColors,
    isCompact: Boolean,
    onUpdateDeadlines: (String, String?, String?) -> Unit,
    onAddEvent: (String, String?, String, String?, EventType, Color) -> Unit,
    onUpdateEvent: (String, String, String?, String, String?, EventType, Color) -> Unit,
    onDeleteEvent: (String) -> Unit,
    onAddHoliday: (String, String, String, HolidayType) -> Unit,
    onUpdateHoliday: (String, String, String, String, HolidayType) -> Unit,
    onDeleteHoliday: (String) -> Unit
) {
    val scrollState = rememberScrollState()
    var showEventDialog by remember { mutableStateOf<AcademicEvent?>(null) }
    var showHolidayDialog by remember { mutableStateOf<Holiday?>(null) }
    var showDeleteEventDialog by remember { mutableStateOf<AcademicEvent?>(null) }
    var showDeleteHolidayDialog by remember { mutableStateOf<Holiday?>(null) }
    var isNewEvent by remember { mutableStateOf(false) }
    var isNewHoliday by remember { mutableStateOf(false) }

    if (showEventDialog != null || isNewEvent) {
        EventDialog(
            event = showEventDialog,
            onDismiss = { 
                showEventDialog = null
                isNewEvent = false
            },
            onConfirm = { title, desc, date, end, type, color ->
                if (isNewEvent) {
                    onAddEvent(title, desc, date, end, type, color)
                } else {
                    showEventDialog?.id?.let { onUpdateEvent(it, title, desc, date, end, type, color) }
                }
                showEventDialog = null
                isNewEvent = false
            },
            colors = colors
        )
    }

    if (showHolidayDialog != null || isNewHoliday) {
        HolidayDialog(
            holiday = showHolidayDialog,
            onDismiss = {
                showHolidayDialog = null
                isNewHoliday = false
            },
            onConfirm = { name, start, end, type ->
                if (isNewHoliday) {
                    onAddHoliday(name, start, end, type)
                } else {
                    showHolidayDialog?.id?.let { onUpdateHoliday(it, name, start, end, type) }
                }
                showHolidayDialog = null
                isNewHoliday = false
            },
            colors = colors
        )
    }

    if (showDeleteEventDialog != null) {
        GenericDeleteDialog(
            title = "Supprimer l'événement",
            message = "Voulez-vous vraiment supprimer l'événement \"${showDeleteEventDialog?.title}\" ?",
            onDismiss = { showDeleteEventDialog = null },
            onConfirm = {
                showDeleteEventDialog?.id?.let { onDeleteEvent(it) }
                showDeleteEventDialog = null
            },
            colors = colors
        )
    }

    if (showDeleteHolidayDialog != null) {
        GenericDeleteDialog(
            title = "Supprimer les vacances",
            message = "Voulez-vous vraiment supprimer les vacances \"${showDeleteHolidayDialog?.name}\" ?",
            onDismiss = { showDeleteHolidayDialog = null },
            onConfirm = {
                showDeleteHolidayDialog?.id?.let { onDeleteHoliday(it) }
                showDeleteHolidayDialog = null
            },
            colors = colors
        )
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Section: Timeline
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(
                "Chronologie des Périodes",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = colors.textPrimary
            )
            
            // Group periods by type for parallel tracks
            val periodsByType = state.periods.groupBy { it.type }
            
            periodsByType.forEach { (type, periods) ->
                TimelineTrack(
                    type = type,
                    periods = periods.sortedBy { it.periodNumber },
                    colors = colors,
                    isCompact = isCompact,
                    onUpdateDeadlines = onUpdateDeadlines
                )
            }
        }

        // Section: Upcoming Events & Holidays
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Events
            Card(
                colors = CardDefaults.cardColors(containerColor = colors.card),
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
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
                        IconButton(onClick = { isNewEvent = true }) {
                            Icon(Icons.Default.AddCircleOutline, contentDescription = "Ajouter un événement", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    if (state.events.isEmpty()) {
                        Text("Aucun événement prévu", style = MaterialTheme.typography.bodyMedium, color = colors.textMuted)
                    }

                    state.events.forEach { event ->
                        EventItem(
                            event = event,
                            colors = colors,
                            onEdit = { showEventDialog = event },
                            onDelete = { showDeleteEventDialog = event }
                        )
                        if (event != state.events.last()) {
                            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = colors.divider)
                        }
                    }
                }
            }
            
            // Holidays
            Card(
                colors = CardDefaults.cardColors(containerColor = colors.card),
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Vacances",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = colors.textPrimary
                        )
                        IconButton(onClick = { isNewHoliday = true }) {
                            Icon(Icons.Default.AddCircleOutline, contentDescription = "Ajouter des vacances", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    if (state.holidays.isEmpty()) {
                        Text("Aucune vacance prévue", style = MaterialTheme.typography.bodyMedium, color = colors.textMuted)
                    }

                    state.holidays.forEach { holiday ->
                        HolidayItem(
                            holiday = holiday,
                            colors = colors,
                            onEdit = { showHolidayDialog = holiday },
                            onDelete = { showDeleteHolidayDialog = holiday }
                        )
                        if (holiday != state.holidays.last()) {
                            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = colors.divider)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TimelineTrack(
    type: PeriodType,
    periods: List<AcademicPeriod>,
    colors: DashboardColors,
    isCompact: Boolean,
    onUpdateDeadlines: (String, String?, String?) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = colors.card),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                type.toFrench(),
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = colors.textMuted
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                periods.forEach { period ->
                    TimelinePeriodItem(
                        period = period,
                        modifier = Modifier.weight(1f),
                        colors = colors,
                        onUpdateDeadlines = onUpdateDeadlines
                    )
                }
            }
        }
    }
}

@Composable
private fun TimelinePeriodItem(
    period: AcademicPeriod,
    modifier: Modifier = Modifier,
    colors: DashboardColors,
    onUpdateDeadlines: (String, String?, String?) -> Unit
) {
    var showDeadlineDialog by remember { mutableStateOf(false) }

    if (showDeadlineDialog) {
        EditDeadlinesDialog(
            period = period,
            onDismiss = { showDeadlineDialog = false },
            onConfirm = { eval, report ->
                onUpdateDeadlines(period.id, eval, report)
                showDeadlineDialog = false
            },
            colors = colors
        )
    }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(period.status.toColor().copy(alpha = 0.05f))
            .border(1.dp, period.status.toColor().copy(alpha = 0.2f), RoundedCornerShape(12.dp))
            .clickable { showDeadlineDialog = true }
            .padding(12.dp)
    ) {
        Text(
            period.name,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = colors.textPrimary
        )
        Text(
            "${period.startDate} au ${period.endDate}",
            style = MaterialTheme.typography.bodySmall,
            color = colors.textMuted
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        DeadlineIndicator("Évaluations", period.evaluationDeadline, Icons.Default.Assignment, colors)
        DeadlineIndicator("Bulletins", period.reportCardDeadline, Icons.Default.Description, colors)
    }
}

@Composable
private fun DeadlineIndicator(
    label: String,
    date: String?,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    colors: DashboardColors
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (date != null) MaterialTheme.colorScheme.primary else colors.textMuted,
            modifier = Modifier.size(14.dp)
        )
        Text(
            text = date ?: "Non définie",
            style = MaterialTheme.typography.labelSmall,
            color = if (date != null) colors.textPrimary else colors.textMuted
        )
    }
}

@Composable
fun EditDeadlinesDialog(
    period: AcademicPeriod,
    onDismiss: () -> Unit,
    onConfirm: (String?, String?) -> Unit,
    colors: DashboardColors
) {
    var evaluationDeadline by remember { mutableStateOf(period.evaluationDeadline ?: "") }
    var reportCardDeadline by remember { mutableStateOf(period.reportCardDeadline ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = colors.card,
        title = {
            Text(
                "Échéances : ${period.name}",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = colors.textPrimary
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    "Définissez les dates limites pour la saisie des notes et la génération des bulletins pour cette période.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textMuted
                )
                
                FormTextField(
                    label = "Date limite des évaluations",
                    value = evaluationDeadline,
                    onValueChange = { evaluationDeadline = it },
                    colors = colors,
                    placeholder = "AAAA-MM-JJ",
                    icon = Icons.Default.Assignment,
                    isError = evaluationDeadline.isNotEmpty() && validateDate(evaluationDeadline) != null
                )
                
                FormTextField(
                    label = "Date limite des bulletins",
                    value = reportCardDeadline,
                    onValueChange = { reportCardDeadline = it },
                    colors = colors,
                    placeholder = "AAAA-MM-JJ",
                    icon = Icons.Default.Description,
                    isError = reportCardDeadline.isNotEmpty() && validateDate(reportCardDeadline) != null
                )
            }
        },
        confirmButton = {
            val isValid = (evaluationDeadline.isEmpty() || validateDate(evaluationDeadline) == null) &&
                         (reportCardDeadline.isEmpty() || validateDate(reportCardDeadline) == null)
            Button(
                onClick = { 
                    onConfirm(
                        evaluationDeadline.takeIf { it.isNotBlank() },
                        reportCardDeadline.takeIf { it.isNotBlank() }
                    ) 
                },
                enabled = isValid,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Enregistrer")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler", color = colors.textMuted)
            }
        }
    )
}

@Composable
fun EventDialog(
    event: AcademicEvent?,
    onDismiss: () -> Unit,
    onConfirm: (String, String?, String, String?, EventType, Color) -> Unit,
    colors: DashboardColors
) {
    var title by remember { mutableStateOf(event?.title ?: "") }
    var description by remember { mutableStateOf(event?.description ?: "") }
    var date by remember { mutableStateOf(event?.date ?: "") }
    var endDate by remember { mutableStateOf(event?.endDate ?: "") }
    var type by remember { mutableStateOf(event?.type ?: EventType.OTHER) }
    var color by remember { mutableStateOf(event?.color ?: Color(0xFF3B82F6)) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = colors.card,
        title = {
            Text(
                if (event == null) "Ajouter un événement" else "Modifier l'événement",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = colors.textPrimary
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.verticalScroll(rememberScrollState())) {
                FormTextField(
                    label = "Titre *",
                    value = title,
                    onValueChange = { title = it },
                    colors = colors,
                    icon = Icons.Default.Title
                )
                FormTextField(
                    label = "Description",
                    value = description,
                    onValueChange = { description = it },
                    colors = colors,
                    icon = Icons.Default.Description
                )
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    FormTextField(
                        label = "Date *",
                        value = date,
                        onValueChange = { date = it },
                        colors = colors,
                        modifier = Modifier.weight(1f),
                        placeholder = "AAAA-MM-JJ",
                        icon = Icons.Default.CalendarToday
                    )
                    FormTextField(
                        label = "Date de fin",
                        value = endDate ?: "",
                        onValueChange = { endDate = it },
                        colors = colors,
                        modifier = Modifier.weight(1f),
                        placeholder = "AAAA-MM-JJ",
                        icon = Icons.Default.Event
                    )
                }
                
                Text("Type d'événement", style = MaterialTheme.typography.labelMedium, color = colors.textMuted)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    EventType.values().forEach { eType ->
                        FilterChip(
                            selected = type == eType,
                            onClick = { type = eType },
                            label = { Text(eType.toFrench()) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                selectedLabelColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }

                Text("Couleur", style = MaterialTheme.typography.labelMedium, color = colors.textMuted)
                val colorOptions = listOf(
                    Color(0xFF3B82F6), // Blue
                    Color(0xFFEF4444), // Red
                    Color(0xFF10B981), // Green
                    Color(0xFFF59E0B), // Amber
                    Color(0xFF8B5CF6), // Violet
                    Color(0xFFEC4899), // Pink
                )
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    colorOptions.forEach { opt ->
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(opt)
                                .border(
                                    width = if (color == opt) 2.dp else 0.dp,
                                    color = if (color == opt) colors.textPrimary else Color.Transparent,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { color = opt }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(title, description.ifBlank { null }, normalizeDate(date), endDate?.ifBlank { null }?.let { normalizeDate(it) }, type, color) },
                enabled = title.isNotBlank() && date.isNotBlank(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Enregistrer")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler", color = colors.textMuted)
            }
        }
    )
}

@Composable
fun HolidayDialog(
    holiday: Holiday?,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, HolidayType) -> Unit,
    colors: DashboardColors
) {
    var name by remember { mutableStateOf(holiday?.name ?: "") }
    var startDate by remember { mutableStateOf(holiday?.startDate ?: "") }
    var endDate by remember { mutableStateOf(holiday?.endDate ?: "") }
    var type by remember { mutableStateOf(holiday?.type ?: HolidayType.SCHOOL_BREAK) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = colors.card,
        title = {
            Text(
                if (holiday == null) "Ajouter des vacances" else "Modifier les vacances",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = colors.textPrimary
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                FormTextField(
                    label = "Nom *",
                    value = name,
                    onValueChange = { name = it },
                    colors = colors,
                    icon = Icons.Default.Badge
                )
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    FormTextField(
                        label = "Date de début *",
                        value = startDate,
                        onValueChange = { startDate = it },
                        colors = colors,
                        modifier = Modifier.weight(1f),
                        placeholder = "AAAA-MM-JJ",
                        icon = Icons.Default.CalendarToday
                    )
                    FormTextField(
                        label = "Date de fin *",
                        value = endDate,
                        onValueChange = { endDate = it },
                        colors = colors,
                        modifier = Modifier.weight(1f),
                        placeholder = "AAAA-MM-JJ",
                        icon = Icons.Default.Event
                    )
                }
                
                Text("Type de vacances", style = MaterialTheme.typography.labelMedium, color = colors.textMuted)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    HolidayType.values().forEach { hType ->
                        FilterChip(
                            selected = type == hType,
                            onClick = { type = hType },
                            label = { Text(hType.toFrench()) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                selectedLabelColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(name, normalizeDate(startDate), normalizeDate(endDate), type) },
                enabled = name.isNotBlank() && startDate.isNotBlank() && endDate.isNotBlank(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Enregistrer")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler", color = colors.textMuted)
            }
        }
    )
}

@Composable
fun GenericDeleteDialog(
    title: String,
    message: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    colors: DashboardColors
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = colors.card,
        icon = { Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
        title = {
            Text(
                title,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = colors.textPrimary
            )
        },
        text = {
            Text(
                message,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textPrimary
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Supprimer")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler", color = colors.textMuted)
            }
        }
    )
}
