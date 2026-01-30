package com.ecolix.presentation.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.currentOrThrow
import com.ecolix.data.models.DashboardColors
import com.ecolix.presentation.components.*
import com.ecolix.data.models.DashboardUiState
import com.ecolix.presentation.screens.settings.SettingsScreenContent
import com.ecolix.presentation.screens.statistics.StatsScreenContent

class DashboardScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = cafe.adriel.voyager.navigator.LocalNavigator.currentOrThrow
        val screenModel: DashboardScreenModel = org.koin.compose.koinInject()
        val state by screenModel.state.collectAsState()
        
        var isDarkMode by remember { mutableStateOf(false) }
        var selectedIndex by remember { mutableStateOf(0) }

        LaunchedEffect(Unit) {
            screenModel.refreshStats()
        }

        LaunchedEffect(isDarkMode) {
            screenModel.onDarkModeChange(isDarkMode)
        }

        var showLogoutDialog by remember { mutableStateOf(false) }

        if (showLogoutDialog) {
            LogoutDialog(
                onConfirm = {
                    showLogoutDialog = false
                    com.ecolix.atschool.api.TokenProvider.token = null
                    navigator.replaceAll(com.ecolix.presentation.screens.auth.LoginScreen())
                },
                onDismiss = { showLogoutDialog = false },
                colors = state.colors
            )
        }

        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val isWide = maxWidth > 800.dp
            
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = state.colors.background,
                bottomBar = {
                    if (!isWide && (selectedIndex in 0..5)) {
                        BottomAppBar(
                            containerColor = state.colors.card,
                            contentColor = MaterialTheme.colorScheme.primary,
                            tonalElevation = 8.dp
                        ) {
                            val navItems = listOf(
                                Triple(0, "Board", Icons.Default.Dashboard),
                                Triple(1, "Élèves", Icons.Default.People),
                                Triple(2, "Staff", Icons.Default.Person),
                                Triple(3, "Notes", Icons.Default.Description),
                                Triple(4, "Matières", Icons.AutoMirrored.Filled.MenuBook),
                                Triple(5, "Réglages", Icons.Default.Settings)
                            )
                            
                            navItems.forEach { (index, label, icon) ->
                                val selected = selectedIndex == index
                                NavigationBarItem(
                                    selected = selected,
                                    onClick = { selectedIndex = index },
                                    icon = { 
                                        Icon(
                                            icon, 
                                            contentDescription = label,
                                            tint = if (selected) MaterialTheme.colorScheme.primary else state.colors.textMuted
                                        ) 
                                    },
                                    label = { 
                                        Text(
                                            label, 
                                            fontSize = 10.sp, 
                                            color = if (selected) MaterialTheme.colorScheme.primary else state.colors.textMuted 
                                        ) 
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = MaterialTheme.colorScheme.primary,
                                        indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                    )
                                )
                            }
                        }
                    }
                },
                floatingActionButton = {
                    // FAB removed for Grades module as requested
                }
            ) { paddingValues ->
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                    if (isWide) {
                        Row(modifier = Modifier.fillMaxSize()) {
                            Sidebar(
                                selectedIndex = selectedIndex,
                                onItemSelected = { selectedIndex = it },
                                isDarkMode = isDarkMode,
                                onToggleTheme = { isDarkMode = !isDarkMode },
                                onLogout = { showLogoutDialog = true }
                            )
                                Box(modifier = Modifier.weight(1f)) {
                                when (selectedIndex) {
                                    0 -> DashboardContent(state = state, isWide = true, modifier = Modifier.fillMaxSize())
                                    1 -> com.ecolix.presentation.screens.eleves.StudentsScreenContent(isDarkMode = isDarkMode)
                                    2 -> com.ecolix.presentation.screens.staff.StaffScreenContent(isDarkMode = isDarkMode)
                                    3 -> com.ecolix.presentation.screens.notes.GradesScreenContent(isDarkMode = isDarkMode)
                                    4 -> com.ecolix.presentation.screens.subjects.SubjectsScreenContent(isDarkMode = isDarkMode)
                                    5 -> SettingsScreenContent(isDarkMode = isDarkMode)
                                    6 -> com.ecolix.presentation.screens.users.UsersScreenContent(isDarkMode = isDarkMode)
                                    7 -> com.ecolix.presentation.screens.timetable.TimetableScreenContent(isDarkMode = isDarkMode)
                                    8 -> com.ecolix.presentation.screens.academic.AcademicScreenContent(isDarkMode = isDarkMode)
                                    9 -> com.ecolix.presentation.screens.paiements.PaymentsScreenContent(isDarkMode = isDarkMode)
                                    10 -> com.ecolix.presentation.screens.inventory.InventoryScreenContent(isDarkMode = isDarkMode)
                                    11 -> com.ecolix.presentation.screens.audits.AuditScreenContent(isDarkMode = isDarkMode)
                             12 -> com.ecolix.presentation.screens.vault.VaultScreenContent(isDarkMode = isDarkMode)
                                    12 -> com.ecolix.presentation.screens.vault.VaultScreenContent(isDarkMode = isDarkMode)
                                    13 -> com.ecolix.presentation.screens.signatures.SignatureScreenContent(isDarkMode = isDarkMode)
                                    14 -> com.ecolix.presentation.screens.library.LibraryScreenContent(isDarkMode = isDarkMode)
                                    15 -> com.ecolix.presentation.screens.discipline.DisciplineScreenContent(isDarkMode = isDarkMode)
                                    16 -> StatsScreenContent(isDarkMode = isDarkMode)
                                    17 -> com.ecolix.presentation.screens.communication.CommunicationScreenContent(isDarkMode = isDarkMode)
                                    18 -> com.ecolix.presentation.screens.accounting.AccountingScreenContent(isDarkMode = isDarkMode)
                                    19 -> com.ecolix.presentation.screens.exports.ExportScreenContent(isDarkMode = isDarkMode)
                                    else -> ScreenPlaceholder("Module en développement (Index $selectedIndex)", state.colors)
                                }
                            }
                        }
                    } else {
                        when (selectedIndex) {
                            0 -> DashboardContent(state = state, isWide = false, modifier = Modifier.fillMaxSize())
                            1 -> com.ecolix.presentation.screens.eleves.StudentsScreenContent(isDarkMode = isDarkMode)
                            2 -> com.ecolix.presentation.screens.staff.StaffScreenContent(isDarkMode = isDarkMode)
                            3 -> com.ecolix.presentation.screens.notes.GradesScreenContent(isDarkMode = isDarkMode)
                            4 -> com.ecolix.presentation.screens.subjects.SubjectsScreenContent(isDarkMode = isDarkMode)
                            5 -> SettingsScreenContent(isDarkMode = isDarkMode)
                            6 -> com.ecolix.presentation.screens.users.UsersScreenContent(isDarkMode = isDarkMode)
                            7 -> com.ecolix.presentation.screens.timetable.TimetableScreenContent(isDarkMode = isDarkMode)
                            8 -> com.ecolix.presentation.screens.academic.AcademicScreenContent(isDarkMode = isDarkMode)
                            9 -> com.ecolix.presentation.screens.paiements.PaymentsScreenContent(isDarkMode = isDarkMode)
                            10 -> com.ecolix.presentation.screens.inventory.InventoryScreenContent(isDarkMode = isDarkMode)
                            11 -> com.ecolix.presentation.screens.audits.AuditScreenContent(isDarkMode = isDarkMode)
                            12 -> com.ecolix.presentation.screens.vault.VaultScreenContent(isDarkMode = isDarkMode)
                            13 -> com.ecolix.presentation.screens.signatures.SignatureScreenContent(isDarkMode = isDarkMode)
                            14 -> com.ecolix.presentation.screens.library.LibraryScreenContent(isDarkMode = isDarkMode)
                            15 -> com.ecolix.presentation.screens.discipline.DisciplineScreenContent(isDarkMode = isDarkMode)
                            16 -> StatsScreenContent(isDarkMode = isDarkMode)
                            17 -> com.ecolix.presentation.screens.communication.CommunicationScreenContent(isDarkMode = isDarkMode)
                            18 -> com.ecolix.presentation.screens.accounting.AccountingScreenContent(isDarkMode = isDarkMode)
                            19 -> com.ecolix.presentation.screens.exports.ExportScreenContent(isDarkMode = isDarkMode)
                            else -> ScreenPlaceholder("Module en développement (Index $selectedIndex)", state.colors)
                        }
                    }
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
private fun ScreenPlaceholder(title: String, colors: DashboardColors) {
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
