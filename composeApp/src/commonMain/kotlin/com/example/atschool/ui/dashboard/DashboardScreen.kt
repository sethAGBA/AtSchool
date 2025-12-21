package com.example.atschool.ui.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PersonAddAlt
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.ToggleOff
import androidx.compose.material.icons.filled.ToggleOn
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DashboardScreen() {
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
                    items = sidebarItems(),
                    isDarkMode = isDarkMode,
                    onToggleTheme = { isDarkMode = !isDarkMode }
                )
                DashboardContent(
                    state = state,
                    isWide = true,
                    modifier = Modifier.weight(1f)
                )
            }
        } else {
            DashboardContent(
                state = state,
                isWide = false,
                modifier = Modifier.fillMaxSize()
            )
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
            StatsSection(state, isWide = isWide)
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
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        ActivitiesCard(state)
                        QuickActionsCard(state)
                        AgendaCard(state)
                        TodosCard(state)
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
private fun DashboardHeader(state: DashboardUiState, isWide: Boolean) {
    CardContainer(containerColor = state.colors.card) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    GradientIconBox(
                        colors = listOf(Color(0xFF6366F1), Color(0xFF8B5CF6)),
                        icon = Icons.Filled.BarChart,
                        size = if (isWide) 64.dp else 48.dp
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Tableau de bord",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 32.sp
                            ),
                            color = state.colors.textPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Gerez votre ecole avec style et efficacite",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
                            color = state.colors.textMuted
                        )
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatusPill(
                        text = "Licence active - 23j restants",
                        colors = listOf(Color(0xFF3B82F6), Color(0xFF60A5FA))
                    )
                    StatusPill(
                        text = "Annee 2024-2025",
                        colors = listOf(Color(0xFF10B981), Color(0xFF34D399))
                    )
                    IconButtonCard(icon = Icons.Filled.NotificationsNone)
                }
            }
        }
    }
}

@Composable
private fun Sidebar(
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    items: List<SidebarItem>,
    isDarkMode: Boolean,
    onToggleTheme: () -> Unit
) {
    val gradientColors = if (isDarkMode) {
        listOf(Color(0xFF1E3A8A), Color(0xFF3B82F6))
    } else {
        listOf(Color(0xFF60A5FA), Color(0xFF93C5FD))
    }
    Column(
        modifier = Modifier
            .width(280.dp)
            .fillMaxSize()
            .background(
                Brush.verticalGradient(gradientColors)
            )
            .padding(vertical = 18.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(62.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.School,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(34.dp)
                )
            }
            Text(
                text = "Ecole Manager",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(Color.White.copy(alpha = 0.2f))
                    .padding(horizontal = 12.dp, vertical = 5.dp)
            ) {
                Text(
                    text = "SAFE MODE",
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold)
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))
        HorizontalDivider(color = Color.White.copy(alpha = 0.2f))
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color.White.copy(alpha = 0.12f))
                .padding(horizontal = 12.dp, vertical = 10.dp)
                .clickable { onToggleTheme() },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isDarkMode) Icons.Filled.DarkMode else Icons.Filled.LightMode,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isDarkMode) "Mode Sombre" else "Mode Clair",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp)
                )
            }
            Icon(
                imageVector = if (isDarkMode) Icons.Filled.ToggleOff else Icons.Filled.ToggleOn,
                contentDescription = null,
                tint = if (isDarkMode) Color.White else Color(0xFF10B981),
                modifier = Modifier.size(52.dp)
            )
        }

        HorizontalDivider(color = Color.White.copy(alpha = 0.2f))

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(items) { item ->
                SidebarItemRow(
                    item = item,
                    selected = selectedIndex == item.index,
                    onClick = { onItemSelected(item.index) }
                )
            }
        }

        HorizontalDivider(color = Color.White.copy(alpha = 0.25f))
        Spacer(modifier = Modifier.height(12.dp))
        Box(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFE11D48))
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Se deconnecter",
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
private fun SidebarItemRow(item: SidebarItem, selected: Boolean, onClick: () -> Unit) {
    val background = if (selected) Color.White.copy(alpha = 0.22f) else Color.Transparent
    val textColor = if (selected) Color.White else Color.White.copy(alpha = 0.85f)
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(background)
            .border(
                width = if (selected) 1.dp else 0.dp,
                color = Color.White.copy(alpha = 0.25f),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White.copy(alpha = 0.92f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = null,
                tint = item.color,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = item.title,
            color = textColor,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                fontSize = 16.sp
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        if (selected) {
            Spacer(modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(Color.White)
            )
        }
    }
}

private fun sidebarItems(): List<SidebarItem> = listOf(
    SidebarItem(0, "Tableau de bord", Icons.Filled.Dashboard, Color(0xFF3B82F6)),
    SidebarItem(1, "Eleves & Classes", Icons.Filled.People, Color(0xFF06B6D4)),
    SidebarItem(2, "Personnel", Icons.Filled.Person, Color(0xFF10B981)),
    SidebarItem(3, "Notes & Bulletins", Icons.Filled.Description, Color(0xFFF59E0B)),
    SidebarItem(4, "Paiements", Icons.Filled.Payments, Color(0xFF22C55E)),
    SidebarItem(6, "Utilisateurs", Icons.Filled.AdminPanelSettings, Color(0xFF6366F1)),
    SidebarItem(7, "Emplois du Temps", Icons.Filled.CalendarMonth, Color(0xFF0EA5E9)),
    SidebarItem(9, "Matieres", Icons.AutoMirrored.Filled.MenuBook, Color(0xFF38BDF8)),
    SidebarItem(14, "Bibliotheque", Icons.AutoMirrored.Filled.LibraryBooks, Color(0xFFF97316)),
    SidebarItem(15, "Discipline", Icons.Filled.Gavel, Color(0xFF8B5CF6)),
    SidebarItem(13, "Signatures & Cachets", Icons.Filled.AssignmentTurnedIn, Color(0xFF3B82F6)),
    SidebarItem(10, "Finance & Materiel", Icons.Filled.Inventory2, Color(0xFFF59E0B)),
    SidebarItem(11, "Audits", Icons.AutoMirrored.Filled.ReceiptLong, Color(0xFF64748B)),
    SidebarItem(12, "Mode coffre fort", Icons.Filled.Lock, Color(0xFF475569)),
    SidebarItem(5, "Parametres", Icons.Filled.Settings, Color(0xFF64748B))
)

@Composable
private fun StatsSection(state: DashboardUiState, isWide: Boolean) {
    val cards = state.stats
    if (isWide) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            cards.forEach { stat ->
                StatCard(
                    modifier = Modifier.weight(1f),
                    stat = stat,
                    colors = state.colors
                )
            }
        }
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            cards.forEach { stat ->
                StatCard(
                    modifier = Modifier.fillMaxWidth(),
                    stat = stat,
                    colors = state.colors
                )
            }
        }
    }
}

@Composable
private fun EnrollmentChartCard(state: DashboardUiState) {
    CardContainer(containerColor = state.colors.card) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(
                text = "Evolution des inscriptions",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                color = state.colors.textPrimary
            )
            EnrollmentChart(
                values = state.enrollmentChartValues,
                labels = state.enrollmentChartLabels,
                lineColor = Color(0xFF3B82F6),
                gridColor = state.colors.divider
            )
        }
    }
}

@Composable
private fun ActivitiesCard(state: DashboardUiState) {
    CardContainer(containerColor = state.colors.card) {
        Column(
            modifier = Modifier.height(320.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Activites recentes",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                color = state.colors.textPrimary
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (state.activities.isEmpty()) {
                    Text(
                        text = "Aucune activite recente.",
                        color = state.colors.textMuted
                    )
                } else {
                    state.activities.forEach { activity ->
                        ActivityRow(activity, state.colors)
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickActionsCard(state: DashboardUiState) {
    CardContainer(containerColor = state.colors.card) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(
                text = "Actions rapides",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                color = state.colors.textPrimary
            )
            val rows = state.quickActions.chunked(2)
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                rows.forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        row.forEach { action ->
                            QuickActionCard(
                                modifier = Modifier.weight(1f),
                                action = action
                            )
                        }
                        if (row.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AgendaCard(state: DashboardUiState) {
    CardContainer(containerColor = state.colors.card) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Echeances (7 jours)",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ),
                    color = state.colors.textPrimary
                )
                Text(
                    text = "Details",
                    color = Color(0xFF3B82F6),
                    fontWeight = FontWeight.SemiBold
                )
            }
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(state.agendaDays) { day ->
                    AgendaChip(day = day, colors = state.colors)
                }
            }
            AnimatedVisibility(visible = state.dueItems.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    state.dueItems.take(5).forEach { item ->
                        DueItemRow(item)
                    }
                }
            }
            if (state.dueItems.isEmpty()) {
                Text(
                    text = "Aucune echeance detectee.",
                    color = state.colors.textMuted
                )
            }
        }
    }
}

@Composable
private fun TodosCard(state: DashboardUiState) {
    val total = state.todos.size
    val doneCount = state.todos.count { it.done }
    val progress by animateFloatAsState(if (total == 0) 0f else doneCount / total.toFloat())

    CardContainer(containerColor = state.colors.card) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "A faire",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        ),
                        color = state.colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TagPill("${total - doneCount} en cours", Color(0xFF0EA5E9))
                        TagPill("$doneCount terminee(s)", Color(0xFF10B981))
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Ajouter",
                        color = Color(0xFF3B82F6),
                        fontWeight = FontWeight.SemiBold
                    )
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = null,
                        tint = Color(0xFF3B82F6),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            if (total > 0) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(999.dp)),
                    color = Color(0xFF10B981),
                    trackColor = state.colors.divider.copy(alpha = 0.4f)
                )
            }
            if (state.todos.isEmpty()) {
                Text(
                    text = "Aucune tache. Ajoutez-en une pour commencer.",
                    color = state.colors.textMuted
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    state.todos.take(6).forEach { todo ->
                        TodoRow(todo, state.colors)
                    }
                }
            }
        }
    }
}

@Composable
private fun AlertsCard(state: DashboardUiState) {
    CardContainer(containerColor = state.colors.card) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Alertes et suivi",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ),
                    color = state.colors.textPrimary
                )
                Text(
                    text = "Actualiser",
                    color = Color(0xFF3B82F6),
                    fontWeight = FontWeight.SemiBold
                )
            }
            if (state.alerts.isEmpty()) {
                Text(
                    text = "Aucune alerte pour le moment.",
                    color = state.colors.textMuted
                )
            } else {
                state.alerts.forEach { alert ->
                    AlertRow(alert, state.colors)
                }
            }
        }
    }
}

@Composable
private fun CardContainer(
    containerColor: Color = Color(0xFFFFFFFF),
    content: @Composable () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            content()
        }
    }
}

@Composable
private fun GradientIconBox(
    colors: List<Color>,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    size: androidx.compose.ui.unit.Dp
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(14.dp))
            .background(Brush.linearGradient(colors)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(size / 2)
        )
    }
}

@Composable
private fun StatusPill(text: String, colors: List<Color>) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(Brush.horizontalGradient(colors))
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(Color.White)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            color = Color.White,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
        )
    }
}

@Composable
private fun IconButtonCard(icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White)
            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF475569)
        )
    }
}

@Composable
private fun StatCard(modifier: Modifier, stat: StatCardData, colors: DashboardColors) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colors.card),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Brush.linearGradient(listOf(stat.color, stat.color.copy(alpha = 0.7f)))),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = stat.icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            Text(
                text = stat.value,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                ),
                color = colors.textPrimary
            )
            Text(
                text = stat.title,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
                color = colors.textMuted
            )
            if (stat.subtitle.isNotBlank()) {
                Text(
                    text = stat.subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                    color = stat.color
                )
            }
        }
    }
}

@Composable
private fun ActivityRow(activity: ActivityData, colors: DashboardColors) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Brush.linearGradient(listOf(activity.color.copy(alpha = 0.2f), activity.color.copy(alpha = 0.4f)))),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = activity.icon,
                contentDescription = null,
                tint = activity.color,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = activity.title,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                color = colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = activity.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = colors.textMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Text(
            text = activity.time,
            style = MaterialTheme.typography.labelSmall,
            color = colors.textMuted
        )
    }
}

@Composable
private fun QuickActionCard(modifier: Modifier, action: QuickActionData) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Brush.linearGradient(listOf(action.color.copy(alpha = 0.2f), action.color.copy(alpha = 0.4f))))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = action.icon,
                contentDescription = null,
                tint = action.color,
                modifier = Modifier.size(32.dp)
            )
            Text(
                text = action.title,
                color = action.color,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun AgendaChip(day: AgendaDay, colors: DashboardColors) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (day.count > 0) Color(0xFF0EA5E9).copy(alpha = 0.12f)
                else colors.background.copy(alpha = 0.5f)
            )
            .border(1.dp, colors.divider.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = day.label,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
            color = colors.textPrimary
        )
        Spacer(modifier = Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(999.dp))
                .background(if (day.count > 0) Color(0xFF0EA5E9) else colors.divider)
                .padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
            Text(
                text = day.count.toString(),
                color = Color.White,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}

@Composable
private fun DueItemRow(item: DueItem) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(item.color)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "${item.dateLabel} - ${item.title} - ${item.subtitle}",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF475569),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun TagPill(label: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(color.copy(alpha = 0.14f))
            .border(1.dp, color.copy(alpha = 0.35f), RoundedCornerShape(999.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            color = color,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold)
        )
    }
}

@Composable
private fun TodoRow(todo: TodoItem, colors: DashboardColors) {
    val accent = when {
        todo.done -> Color(0xFF10B981)
        todo.overdue -> Color(0xFFEF4444)
        todo.dueSoon -> Color(0xFFF59E0B)
        else -> Color(0xFF0EA5E9)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(colors.background.copy(alpha = 0.6f))
            .border(1.dp, colors.divider.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(18.dp)
                .clip(CircleShape)
                .background(if (todo.done) accent else Color.Transparent)
                .border(2.dp, accent, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (todo.done) {
                Icon(
                    imageVector = Icons.Filled.AssignmentTurnedIn,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = todo.title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = colors.textPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                TagPill(todo.dueLabel, accent)
                if (todo.overdue) TagPill("En retard", Color(0xFFEF4444))
                if (todo.done) TagPill("Termine", Color(0xFF10B981))
            }
        }
        Icon(
            imageVector = Icons.Filled.WarningAmber,
            contentDescription = null,
            tint = if (todo.overdue) Color(0xFFEF4444) else Color.Transparent
        )
    }
}

@Composable
private fun AlertRow(alert: AlertData, colors: DashboardColors) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(colors.background.copy(alpha = 0.55f))
            .border(1.dp, colors.divider.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(alert.color.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = alert.icon,
                contentDescription = null,
                tint = alert.color,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = alert.title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = colors.textPrimary
            )
            Text(
                text = alert.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = colors.textMuted,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
        Text(
            text = alert.actionLabel,
            color = Color(0xFF3B82F6),
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
        )
    }
}

@Composable
private fun EnrollmentChart(
    values: List<Int>,
    labels: List<String>,
    lineColor: Color,
    gridColor: Color
) {
    val maxValue = (values.maxOrNull() ?: 1).coerceAtLeast(1)
    val minValue = (values.minOrNull() ?: 0)
    val range = (maxValue - minValue).coerceAtLeast(1)

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
        ) {
            val width = size.width
            val height = size.height
            val leftPadding = 20f
            val bottomPadding = 24f
            val topPadding = 12f
            val usableHeight = height - bottomPadding - topPadding
            val usableWidth = width - leftPadding

            val stepX = if (values.size > 1) usableWidth / (values.size - 1) else 0f

            val gridLines = 4
            repeat(gridLines + 1) { index ->
                val y = topPadding + usableHeight * (index / gridLines.toFloat())
                drawLine(
                    color = gridColor.copy(alpha = 0.4f),
                    start = androidx.compose.ui.geometry.Offset(leftPadding, y),
                    end = androidx.compose.ui.geometry.Offset(width, y),
                    strokeWidth = 1f
                )
            }

            val path = Path()
            values.forEachIndexed { index, value ->
                val x = leftPadding + stepX * index
                val normalized = (value - minValue).toFloat() / range
                val y = topPadding + usableHeight * (1f - normalized)
                if (index == 0) {
                    path.moveTo(x, y)
                } else {
                    path.lineTo(x, y)
                }
            }

            drawPath(
                path = path,
                color = lineColor,
                style = Stroke(width = 4f)
            )

            values.forEachIndexed { index, value ->
                val x = leftPadding + stepX * index
                val normalized = (value - minValue).toFloat() / range
                val y = topPadding + usableHeight * (1f - normalized)
                drawCircle(color = lineColor, radius = 5f, center = androidx.compose.ui.geometry.Offset(x, y))
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            labels.forEach { label ->
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF64748B)
                )
            }
        }
    }
}

@Immutable
private data class DashboardUiState(
    val colors: DashboardColors,
    val stats: List<StatCardData>,
    val enrollmentChartValues: List<Int>,
    val enrollmentChartLabels: List<String>,
    val activities: List<ActivityData>,
    val quickActions: List<QuickActionData>,
    val agendaDays: List<AgendaDay>,
    val dueItems: List<DueItem>,
    val todos: List<TodoItem>,
    val alerts: List<AlertData>
) {
    companion object {
        fun sample(isDarkMode: Boolean): DashboardUiState {
            val colors = if (isDarkMode) DashboardColors.dark() else DashboardColors.light()
            return DashboardUiState(
                colors = colors,
                stats = listOf(
                    StatCardData("Total Eleves", "1248", Icons.Filled.School, Color(0xFF3B82F6), ""),
                    StatCardData("Personnel", "68", Icons.Filled.Groups, Color(0xFF10B981), ""),
                    StatCardData("Classes", "38", Icons.Filled.School, Color(0xFFF59E0B), ""),
                    StatCardData("Revenus", "18.2M FCFA", Icons.Filled.Payments, Color(0xFFEF4444), "")
                ),
                enrollmentChartValues = listOf(120, 160, 180, 200, 230, 280, 310),
                enrollmentChartLabels = listOf("Jan", "Fev", "Mar", "Avr", "Mai", "Juin", "Juil"),
                activities = listOf(
                    ActivityData(
                        title = "Nouveau paiement recu",
                        subtitle = "Classe 3e B - 120 000 FCFA",
                        time = "09:10",
                        icon = Icons.Filled.Payments,
                        color = Color(0xFF3B82F6)
                    ),
                    ActivityData(
                        title = "Absence signalee",
                        subtitle = "Eleve: Binta D. (4e A)",
                        time = "08:45",
                        icon = Icons.Filled.WarningAmber,
                        color = Color(0xFFF59E0B)
                    ),
                    ActivityData(
                        title = "Nouvel enseignant",
                        subtitle = "Mme Diallo - Sciences",
                        time = "08:10",
                        icon = Icons.Filled.PersonAdd,
                        color = Color(0xFF10B981)
                    )
                ),
                quickActions = listOf(
                    QuickActionData("Nouvel Eleve", Icons.Filled.PersonAdd, Color(0xFF10B981)),
                    QuickActionData("Saisir Notes", Icons.Filled.Edit, Color(0xFF3B82F6)),
                    QuickActionData("Generer Bulletin", Icons.Filled.Description, Color(0xFFF59E0B)),
                    QuickActionData("Emploi du Temps", Icons.Filled.Schedule, Color(0xFF8B5CF6)),
                    QuickActionData("Paiements", Icons.Filled.Payments, Color(0xFF4CAF50)),
                    QuickActionData("Ajouter Personnel", Icons.Filled.PersonAddAlt, Color(0xFF60A5FA)),
                    QuickActionData("Annuler Paiement", Icons.Filled.Timeline, Color(0xFFEF4444)),
                    QuickActionData("Finance et Materiel", Icons.Filled.Inventory2, Color(0xFFF59E0B))
                ),
                agendaDays = listOf(
                    AgendaDay("Lun 18", 2),
                    AgendaDay("Mar 19", 0),
                    AgendaDay("Mer 20", 1),
                    AgendaDay("Jeu 21", 3),
                    AgendaDay("Ven 22", 0),
                    AgendaDay("Sam 23", 1),
                    AgendaDay("Dim 24", 0)
                ),
                dueItems = listOf(
                    DueItem("18/09", "Relance impayes", "3e B", Color(0xFF0EA5E9)),
                    DueItem("19/09", "Retour livres", "Bibliotheque", Color(0xFFEF4444)),
                    DueItem("21/09", "Conseil classe", "Terminale S", Color(0xFF8B5CF6))
                ),
                todos = listOf(
                    TodoItem("Relancer les impayes", "Echeance 20/09/2025", done = false, overdue = false, dueSoon = true),
                    TodoItem("Signer les bulletins", "Echeance 18/09/2025", done = false, overdue = true, dueSoon = false),
                    TodoItem("Exporter les statistiques", "Sans echeance", done = true, overdue = false, dueSoon = false)
                ),
                alerts = listOf(
                    AlertData(
                        title = "Impayes (estimation)",
                        subtitle = "Reste a encaisser: 8.5M FCFA / 26.7M FCFA",
                        icon = Icons.Filled.Payments,
                        color = Color(0xFFF59E0B),
                        actionLabel = "Voir paiements"
                    ),
                    AlertData(
                        title = "Bibliotheque",
                        subtitle = "2 emprunts en retard",
                        icon = Icons.AutoMirrored.Filled.LibraryBooks,
                        color = Color(0xFFEF4444),
                        actionLabel = "Voir bibliotheque"
                    ),
                    AlertData(
                        title = "Discipline",
                        subtitle = "3 sanctions ces 7 derniers jours",
                        icon = Icons.Filled.Gavel,
                        color = Color(0xFF8B5CF6),
                        actionLabel = "Voir discipline"
                    )
                )
            )
        }
    }
}

@Immutable
private data class DashboardColors(
    val background: Color,
    val card: Color,
    val textPrimary: Color,
    val textMuted: Color,
    val divider: Color
) {
    companion object {
        fun light() = DashboardColors(
            background = Color(0xFFF5F7FB),
            card = Color(0xFFFFFFFF),
            textPrimary = Color(0xFF1E293B),
            textMuted = Color(0xFF64748B),
            divider = Color(0xFFE2E8F0)
        )

        fun dark() = DashboardColors(
            background = Color(0xFF0F172A),
            card = Color(0xFF111827),
            textPrimary = Color(0xFFF8FAFC),
            textMuted = Color(0xFF94A3B8),
            divider = Color(0xFF1F2937)
        )
    }
}

@Immutable
private data class StatCardData(
    val title: String,
    val value: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color,
    val subtitle: String
)

@Immutable
private data class ActivityData(
    val title: String,
    val subtitle: String,
    val time: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color
)

@Immutable
private data class QuickActionData(
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color
)

@Immutable
private data class AgendaDay(
    val label: String,
    val count: Int
)

@Immutable
private data class DueItem(
    val dateLabel: String,
    val title: String,
    val subtitle: String,
    val color: Color
)

@Immutable
private data class TodoItem(
    val title: String,
    val dueLabel: String,
    val done: Boolean,
    val overdue: Boolean,
    val dueSoon: Boolean
)

@Immutable
private data class AlertData(
    val title: String,
    val subtitle: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color,
    val actionLabel: String
)

@Immutable
private data class SidebarItem(
    val index: Int,
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color
)
