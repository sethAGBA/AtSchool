package com.ecolix.presentation.screens.academic

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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

@Composable
fun AcademicScreenContent(isDarkMode: Boolean) {
    val screenModel = remember { AcademicScreenModel() }
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
                            text = "Gestion Académique",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 32.sp
                            ),
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
                            isCompact = isCompact
                        )
                        AcademicViewMode.SCHOOL_YEARS -> SchoolYearsTab(
                            state = state,
                            colors = state.colors,
                            isCompact = isCompact,
                            onSelectYear = { screenModel.onSelectSchoolYear(it) }
                        )
                        AcademicViewMode.PERIODS -> PeriodsTab(
                            state = state,
                            colors = state.colors,
                            isCompact = isCompact,
                            onSelectPeriod = { screenModel.onSelectPeriod(it) }
                        )
                        AcademicViewMode.CALENDAR -> CalendarTab(
                            state = state,
                            colors = state.colors,
                            isCompact = isCompact
                        )
                        AcademicViewMode.SETTINGS -> AcademicSettingsTab(
                            state = state,
                            colors = state.colors,
                            isCompact = isCompact
                        )
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
            title = "Période Actuelle",
            value = statistics.currentPeriod?.name ?: "Aucune",
            subtitle = statistics.currentPeriod?.let { "Jusqu'au ${it.endDate}" },
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
    PeriodType.QUARTER -> "Quadrimestre"
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
