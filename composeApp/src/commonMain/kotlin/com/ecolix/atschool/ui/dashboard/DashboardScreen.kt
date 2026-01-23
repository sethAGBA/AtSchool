package com.ecolix.atschool.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import com.ecolix.atschool.ui.dashboard.components.*
import com.ecolix.atschool.ui.dashboard.models.DashboardUiState

import com.ecolix.atschool.ui.settings.SettingsScreen

class DashboardScreen : Screen {
    @Composable
    override fun Content() {
        var isDarkMode by remember { mutableStateOf(false) }
        val state = remember(isDarkMode) { DashboardUiState.sample(isDarkMode) }
        var selectedIndex by remember { mutableStateOf(0) }

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .background(state.colors.background)
        ) {
            val isWide = maxWidth > 900.dp

            if (isWide) {
                Row(modifier = Modifier.fillMaxSize()) {
                    Sidebar(
                        selectedIndex = selectedIndex,
                        onItemSelected = { selectedIndex = it },
                        isDarkMode = isDarkMode,
                        onToggleTheme = { isDarkMode = !isDarkMode }
                    )
                    Box(modifier = Modifier.weight(1f)) {
                        when (selectedIndex) {
                            0 -> DashboardContent(state = state, isWide = true, modifier = Modifier.fillMaxSize())
                            1 -> com.ecolix.atschool.ui.students.StudentsScreenContent(isDarkMode = isDarkMode)
                            2 -> ScreenPlaceholder("Gestion du Personnel", state.colors)
                            3 -> com.ecolix.atschool.ui.grades.GradesScreenContent(isDarkMode = isDarkMode)
                            5 -> SettingsScreen(colors = state.colors)
                            else -> ScreenPlaceholder("Module en développement (Index $selectedIndex)", state.colors)
                        }
                    }
                }
            } else {
                when (selectedIndex) {
                    0 -> DashboardContent(state = state, isWide = false, modifier = Modifier.fillMaxSize())
                    1 -> com.ecolix.atschool.ui.students.StudentsScreenContent(isDarkMode = isDarkMode)
                    2 -> ScreenPlaceholder("Gestion du Personnel", state.colors)
                    3 -> com.ecolix.atschool.ui.grades.GradesScreenContent(isDarkMode = isDarkMode)
                    5 -> SettingsScreen(colors = state.colors)
                    else -> ScreenPlaceholder("Module en développement (Index $selectedIndex)", state.colors)
                }
            }
        }
    }
}

@Composable
private fun DashboardContent(state: DashboardUiState, isWide: Boolean, modifier: Modifier) {
    LazyColumn(
        modifier = modifier.padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            DashboardHeader(state, isWide = isWide)
        }

        item {
            StatsSection(stats = state.stats, colors = state.colors, isWide = isWide)
        }

        item {
            if (isWide) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Column(
                        modifier = Modifier.weight(2f),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        EnrollmentChartCard(state)
                        AlertsCard(state)
                        AgendaCard(state)
                        TodosCard(state)
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        ActivitiesCard(state)
                        QuickActionsCard(state)
                    }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    EnrollmentChartCard(state)
                    ActivitiesCard(state)
                    QuickActionsCard(state)
                    AlertsCard(state)
                    AgendaCard(state)
                    TodosCard(state)
                }
            }
        }
    }
}

@Composable
private fun ScreenPlaceholder(title: String, colors: com.ecolix.atschool.ui.dashboard.models.DashboardColors) {
    Box(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = colors.textPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Cet ecran est en cours de developpement.",
                color = colors.textMuted
            )
        }
    }
}
