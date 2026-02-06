package com.ecolix.presentation.screens.academic

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
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
import com.ecolix.presentation.components.SearchBar
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect

class AcademicScreen : Screen {
    @Composable
    override fun Content() {
        // We'll need to know if it's dark mode. 
        // Usually, this is handled by a theme provider, but here it's passed down.
        // For now, let's assume it's part of the global theme or passed via LocalTheme.
        val isDarkMode = androidx.compose.foundation.isSystemInDarkTheme() 
        val screenModel = koinScreenModel<AcademicScreenModel>()
        AcademicScreenContent(screenModel, isDarkMode)
    }
}

@Composable
fun AcademicScreenContent(screenModel: AcademicScreenModel, isDarkMode: Boolean) {
    val state: AcademicUiState by screenModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let {
            snackbarHostState.showSnackbar(it, duration = SnackbarDuration.Short)
            screenModel.clearError()
        }
    }

    LaunchedEffect(state.successMessage) {
        state.successMessage?.let {
            snackbarHostState.showSnackbar(it, duration = SnackbarDuration.Short)
            screenModel.clearSuccess()
        }
    }

    LaunchedEffect(isDarkMode) {
        screenModel.onDarkModeChange(isDarkMode)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = state.colors.background,
        modifier = Modifier.fillMaxSize()
    ) { padding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            val isCompact = maxWidth < 800.dp

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(if (isCompact) 16.dp else 24.dp),
                verticalArrangement = Arrangement.spacedBy(if (isCompact) 16.dp else 24.dp)
            ) {
            // Header (Visible on all devices)
            val headerTitleStyle = if (isCompact) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.headlineMedium
            val headerSpacing = if (isCompact) 8.dp else 24.dp

            if (isCompact) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Gestion Académique (${state.statistics.activeYear?.name ?: ""})",
                        style = headerTitleStyle.copy(fontWeight = FontWeight.Bold),
                        color = state.colors.textPrimary
                    )
                    AcademicViewToggle(
                        currentMode = state.viewMode,
                        onModeChange = { screenModel.onViewModeChange(it) },
                        colors = state.colors,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Gestion Académique (${state.statistics.activeYear?.name ?: ""})",
                            style = headerTitleStyle.copy(fontWeight = FontWeight.Bold, fontSize = 32.sp),
                            color = state.colors.textPrimary
                        )
                        Text(
                            text = "Configuration des années scolaires, périodes et calendrier",
                            style = MaterialTheme.typography.bodyMedium,
                            color = state.colors.textMuted
                        )
                    }

                    AcademicViewToggle(
                        currentMode = state.viewMode,
                        onModeChange = { screenModel.onViewModeChange(it) },
                        colors = state.colors
                    )
                }
            }

            if (!isCompact) {

                // Statistics Cards
                if (state.viewMode == AcademicViewMode.OVERVIEW && state.searchQuery.isEmpty()) {
                    AcademicStatisticsCards(
                        statistics = state.statistics,
                        colors = state.colors
                    )
                }

                // Search Bar
                if (state.viewMode != AcademicViewMode.SETTINGS) {
                    SearchBar(
                        query = state.searchQuery,
                        onQueryChange = { screenModel.onSearchQueryChange(it) },
                        colors = state.colors,
                        modifier = Modifier.width(400.dp)
                    )
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
                        AcademicViewMode.OVERVIEW -> AcademicOverviewTab(
                            state = state,
                            colors = state.colors,
                            isCompact = isCompact,
                            onArchiveYear = { screenModel.setSchoolYearStatus(it, AcademicStatus.COMPLETED) }
                        )
                        AcademicViewMode.SCHOOL_YEARS -> SchoolYearsTab(
                            state = state,
                            colors = state.colors,
                            isCompact = isCompact,
                            onSelectYear = { screenModel.onSelectSchoolYear(it) },
                            onCreateYear = { name, start, end, types, num, periods ->
                                screenModel.createSchoolYear(name, start, end, types, num, periods)
                            },
                            onUpdateYear = { id, name, start, end, types, num, periods ->
                                screenModel.updateSchoolYear(id, name, start, end, types, num, periods)
                            },
                            onDeleteYear = { screenModel.deleteSchoolYear(it) },
                            onSetDefault = { screenModel.setDefaultYear(it) },
                            onSetStatus = { id, status -> screenModel.setSchoolYearStatus(id, status) }
                        )
                        AcademicViewMode.PERIODS -> PeriodsTab(
                            state = state,
                            colors = state.colors,
                            isCompact = isCompact,
                            onSelectPeriod = { screenModel.onSelectPeriod(it) },
                            onCreatePeriod = { name, num, start, end, type ->
                                screenModel.createAcademicPeriod(name, num, start, end, type)
                            },
                            onUpdatePeriod = { id, name, num, start, end, type ->
                                screenModel.updateAcademicPeriod(id, name, num, start, end, type)
                            },
                            onDeletePeriod = { 
                                screenModel.deleteAcademicPeriod(it)
                            },
                            onSetStatus = { id, status -> screenModel.setPeriodStatus(id, status) }
                        )
                        AcademicViewMode.CALENDAR -> CalendarTab(
                            state = state,
                            colors = state.colors,
                            isCompact = isCompact,
                            onUpdateDeadlines = { id, eval, report ->
                                screenModel.updatePeriodDeadlines(id, eval, report)
                            },
                            onAddEvent = { title, desc, date, end, type, color ->
                                screenModel.createAcademicEvent(title, desc, date, end, type, color)
                            },
                            onUpdateEvent = { id, title, desc, date, end, type, color ->
                                screenModel.updateAcademicEvent(id, title, desc, date, end, type, color)
                            },
                            onDeleteEvent = { screenModel.deleteAcademicEvent(it) },
                            onAddHoliday = { name, start, end, type ->
                                screenModel.createHoliday(name, start, end, type)
                            },
                            onUpdateHoliday = { id, name, start, end, type ->
                                screenModel.updateHoliday(id, name, start, end, type)
                            },
                            onDeleteHoliday = { screenModel.deleteHoliday(it) }
                        )
                        AcademicViewMode.SETTINGS -> AcademicSettingsTab(
                            state = state,
                            colors = state.colors,
                            isCompact = isCompact,
                            onUpdateSettings = { screenModel.updateSettings(it) }
                        )
                    }
                }
            }
        }
    }
}
}

@Composable
private fun AcademicViewToggle(
    currentMode: AcademicViewMode,
    onModeChange: (AcademicViewMode) -> Unit,
    colors: DashboardColors,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(colors.card)
            .horizontalScroll(scrollState)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        val tabs = listOf(
            Triple(AcademicViewMode.OVERVIEW, "Vue d'ensemble", Icons.Default.Dashboard),
            Triple(AcademicViewMode.SCHOOL_YEARS, "Années", Icons.Default.CalendarMonth),
            Triple(AcademicViewMode.PERIODS, "Périodes", Icons.Default.DateRange),
            Triple(AcademicViewMode.CALENDAR, "Calendrier", Icons.Default.Event),
            Triple(AcademicViewMode.SETTINGS, "Paramètres", Icons.Default.Settings)
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
private fun AcademicStatisticsCards(
    statistics: AcademicStatistics,
    colors: DashboardColors
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StatCard(
            title = "Année Active",
            value = statistics.activeYear?.name ?: "Aucune",
            subtitle = statistics.activeYear?.let { "${it.numberOfPeriods} périodes" },
            icon = Icons.Default.School,
            color = Color(0xFF3B82F6),
            colors = colors,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            title = "Période(s) Actuelle(s)",
            value = if (statistics.currentPeriods.isNotEmpty()) statistics.currentPeriods.joinToString(", ") { it.name } else "Aucune",
            subtitle = statistics.currentPeriods.minByOrNull { it.endDate }?.let { "Fin : ${it.endDate}" },
            icon = Icons.Default.DateRange,
            color = Color(0xFF10B981),
            colors = colors,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            title = "Progression",
            value = "${statistics.completionRate.toInt()}%",
            subtitle = "de l'année complétée",
            icon = Icons.Default.TrendingUp,
            color = Color(0xFFF59E0B),
            colors = colors,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            title = "Événements",
            value = "${statistics.upcomingEvents}",
            subtitle = "événements à venir",
            icon = Icons.Default.Event,
            color = Color(0xFF8B5CF6),
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

// Helper extension functions
fun AcademicStatus.toFrench(): String = when (this) {
    AcademicStatus.ACTIVE -> "Active"
    AcademicStatus.UPCOMING -> "À venir"
    AcademicStatus.COMPLETED -> "Terminée"
    AcademicStatus.ARCHIVED -> "Archivée"
}

fun AcademicStatus.toColor(): Color = when (this) {
    AcademicStatus.ACTIVE -> Color(0xFF10B981)
    AcademicStatus.UPCOMING -> Color(0xFF3B82F6)
    AcademicStatus.COMPLETED -> Color(0xFF6B7280)
    AcademicStatus.ARCHIVED -> Color(0xFF9CA3AF)
}

fun PeriodType.toFrench(): String = when (this) {
    PeriodType.TRIMESTER -> "Trimestre"
    PeriodType.SEMESTER -> "Semestre"
}

fun EventType.toFrench(): String = when (this) {
    EventType.HOLIDAY -> "Vacances"
    EventType.EXAM -> "Examen"
    EventType.MEETING -> "Réunion"
    EventType.CEREMONY -> "Cérémonie"
    EventType.DEADLINE -> "Date limite"
    EventType.OTHER -> "Autre"
}

fun HolidayType.toFrench(): String = when (this) {
    HolidayType.NATIONAL -> "Fête Nationale"
    HolidayType.RELIGIOUS -> "Fête Religieuse"
    HolidayType.SCHOOL_BREAK -> "Vacances Scolaires"
    HolidayType.OTHER -> "Autre"
}
