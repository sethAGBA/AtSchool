package com.ecolix.presentation.screens.inventory

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecolix.data.models.*
import com.ecolix.presentation.components.SearchBar

@Composable
fun InventoryScreenContent(isDarkMode: Boolean) {
    val screenModel = remember { InventoryScreenModel() }
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
                        text = "Finance & Matériel",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = if (isCompact) 24.sp else 32.sp
                        ),
                        color = state.colors.textPrimary
                    )
                    if (!isCompact) {
                        Text(
                            text = "Gestion de la trésorerie et de l'inventaire matériel",
                            style = MaterialTheme.typography.bodyMedium,
                            color = state.colors.textMuted
                        )
                    }
                }

                InventoryViewToggle(
                    currentMode = state.viewMode,
                    onModeChange = { screenModel.onViewModeChange(it) },
                    colors = state.colors
                )
            }

            // Stats Row
            if (state.viewMode == InventoryViewMode.FINANCE) {
                FinanceStats(state.budgetSummary, state.colors, isCompact)
            } else if (state.viewMode == InventoryViewMode.INVENTORY) {
                InventorySummary(state.items, state.colors, isCompact)
            }

            // Search and Controls
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

                Button(
                    onClick = { /* Action based on mode */ },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    if (!isCompact) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (state.viewMode == InventoryViewMode.FINANCE) "Nouvelle Dépense" else "Nouvel Article")
                    }
                }
            }

            // Main Content Area
            Box(modifier = Modifier.weight(1f)) {
                AnimatedContent(
                    targetState = state.viewMode,
                    transitionSpec = { fadeIn() togetherWith fadeOut() }
                ) { mode ->
                    when (mode) {
                        InventoryViewMode.FINANCE -> FinanceView(state, isCompact)
                        InventoryViewMode.CLASS_FINANCE -> ClassFinanceView(state, isCompact)
                        InventoryViewMode.INVENTORY -> InventoryView(state, isCompact)
                        InventoryViewMode.PROCUREMENT -> PROCUREMENTView(state, isCompact)
                        else -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("En développement", color = state.colors.textMuted)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FinanceStats(summary: BudgetSummary, colors: DashboardColors, isCompact: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StatCard("Solde actuel", formatCFA(summary.totalBalance), Icons.Default.AccountBalanceWallet, Color(0xFF10B981), colors, Modifier.weight(1f))
        StatCard("Dépenses (ce mois)", formatCFA(summary.monthlyExpenses), Icons.Default.TrendingDown, Color(0xFFEF4444), colors, Modifier.weight(1f))
        if (!isCompact) {
            StatCard("Budget prévisionnel", formatCFA(summary.projectedBudget), Icons.Default.QueryStats, Color(0xFF3B82F6), colors, Modifier.weight(1f))
        }
    }
}

@Composable
private fun InventorySummary(items: List<InventoryItem>, colors: DashboardColors, isCompact: Boolean) {
    val lowStockCount = items.count { it.quantity <= it.minThreshold }
    val criticalCount = items.count { it.condition == ItemCondition.BROKEN || it.condition == ItemCondition.POOR }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StatCard("Articles totaux", "${items.size}", Icons.Default.Inventory2, Color(0xFF6366F1), colors, Modifier.weight(1f))
        StatCard("Alertes stock", "$lowStockCount", Icons.Default.NotificationsActive, if (lowStockCount > 0) Color(0xFFF59E0B) else Color(0xFF10B981), colors, Modifier.weight(1f))
        if (!isCompact) {
            StatCard("Maintenance requise", "$criticalCount", Icons.Default.Build, Color(0xFFEF4444), colors, Modifier.weight(1f))
        }
    }
}

@Composable
private fun FinanceView(state: InventoryUiState, isCompact: Boolean) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(state.expenses.sortedByDescending { it.date }) { expense ->
            ExpenseRow(expense, state.colors, isCompact)
        }
    }
}

@Composable
private fun ClassFinanceView(state: InventoryUiState, isCompact: Boolean) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(state.classFinances) { finance ->
            ClassFinanceRow(finance, state.colors, isCompact)
        }
    }
}

@Composable
private fun ClassFinanceRow(finance: ClassFinanceSummary, colors: DashboardColors, isCompact: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colors.card)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(finance.classroomName, fontWeight = FontWeight.Bold, color = colors.textPrimary, style = MaterialTheme.typography.titleMedium)
                Box(
                    modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(colors.divider.copy(alpha = 0.5f)).padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("${finance.paymentRate}% Payé", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = if (finance.paymentRate >= 90) Color(0xFF10B981) else if (finance.paymentRate >= 70) Color(0xFFF59E0B) else Color(0xFFEF4444))
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                FinanceMetric("Attendu", formatCFA(finance.totalExpected), colors.textMuted, colors)
                FinanceMetric("Collecté", formatCFA(finance.totalCollected), Color(0xFF10B981), colors)
                FinanceMetric("Restant", formatCFA(finance.outstanding), Color(0xFFEF4444), colors)
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            LinearProgressIndicator(
                progress = { finance.paymentRate / 100f },
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                color = if (finance.paymentRate >= 90) Color(0xFF10B981) else if (finance.paymentRate >= 70) Color(0xFFF59E0B) else Color(0xFFEF4444),
                trackColor = colors.divider
            )
        }
    }
}

@Composable
private fun FinanceMetric(label: String, value: String, valueColor: Color, colors: DashboardColors) {
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall, color = colors.textMuted)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = valueColor)
    }
}

@Composable
private fun ExpenseRow(expense: Expense, colors: DashboardColors, isCompact: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colors.card)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier.size(40.dp).clip(RoundedCornerShape(8.dp)).background(colors.divider.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    when (expense.category) {
                        ExpenseCategory.SALARIES -> Icons.Default.Group
                        ExpenseCategory.UTILITIES -> Icons.Default.Lightbulb
                        ExpenseCategory.MAINTENANCE -> Icons.Default.Handyman
                        ExpenseCategory.SUPPLIES -> Icons.Default.ShoppingBag
                        else -> Icons.Default.Receipt
                    },
                    contentDescription = null,
                    tint = colors.textMuted
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(expense.title, fontWeight = FontWeight.Bold, color = colors.textPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("${expense.category.name} • ${expense.date}", style = MaterialTheme.typography.bodySmall, color = colors.textMuted)
            }

            if (!isCompact) {
                Text(expense.recipient ?: "", modifier = Modifier.weight(0.8f), style = MaterialTheme.typography.bodyMedium, color = colors.textMuted)
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(formatCFA(expense.amount), fontWeight = FontWeight.Bold, color = colors.textPrimary)
                StatusBadge(expense.status)
            }
        }
    }
}

@Composable
private fun InventoryView(state: InventoryUiState, isCompact: Boolean) {
    LazyVerticalGrid(
        columns = if (isCompact) GridCells.Fixed(1) else GridCells.Adaptive(minSize = 300.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(state.items) { item ->
            InventoryItemCard(item, state.colors)
        }
    }
}

@Composable
private fun InventoryItemCard(item: InventoryItem, colors: DashboardColors) {
    val isLowStock = item.quantity <= item.minThreshold

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colors.card)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(item.name, fontWeight = FontWeight.Bold, color = colors.textPrimary, modifier = Modifier.weight(1f))
                ConditionBadge(item.condition)
            }
            Text(item.category.name, style = MaterialTheme.typography.labelSmall, color = colors.textMuted)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("Quantité", style = MaterialTheme.typography.labelSmall, color = colors.textMuted)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("${item.quantity}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = if (isLowStock) Color(0xFFEF4444) else colors.textPrimary)
                        if (isLowStock) {
                            Icon(Icons.Default.Warning, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFFEF4444))
                        }
                    }
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text("Localisation", style = MaterialTheme.typography.labelSmall, color = colors.textMuted)
                    Text(item.location ?: "N/A", style = MaterialTheme.typography.bodyMedium, color = colors.textPrimary)
                }
            }
        }
    }
}

@Composable
private fun PROCUREMENTView(state: InventoryUiState, isCompact: Boolean) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(64.dp), tint = state.colors.textMuted.copy(alpha = 0.3f))
            Text("Liste de réapprovisionnement générée automatiquement", color = state.colors.textMuted, textAlign = TextAlign.Center)
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
private fun StatusBadge(status: ExpenseStatus) {
    val (label, color) = when (status) {
        ExpenseStatus.PAID -> "Payé" to Color(0xFF10B981)
        ExpenseStatus.PENDING -> "En attente" to Color(0xFFF59E0B)
        ExpenseStatus.CANCELLED -> "Annulé" to Color(0xFFEF4444)
        ExpenseStatus.PLANNED -> "Prévu" to Color(0xFF3B82F6)
    }
    Box(modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(color.copy(alpha = 0.1f)).padding(horizontal = 6.dp, vertical = 2.dp)) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = color, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun ConditionBadge(condition: ItemCondition) {
    val (label, color) = when (condition) {
        ItemCondition.NEW -> "Neuf" to Color(0xFF10B981)
        ItemCondition.GOOD -> "Bon" to Color(0xFF10B981)
        ItemCondition.FAIR -> "Moyen" to Color(0xFFF59E0B)
        ItemCondition.POOR -> "Mauvais" to Color(0xFFEF4444)
        ItemCondition.BROKEN -> "Hors service" to Color(0xFF1E293B)
    }
    Box(modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(color.copy(alpha = 0.1f)).padding(horizontal = 6.dp, vertical = 2.dp)) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = color, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun InventoryViewToggle(currentMode: InventoryViewMode, onModeChange: (InventoryViewMode) -> Unit, colors: DashboardColors) {
    Row(
        modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(colors.card).padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        val tabs = listOf(
            Triple(InventoryViewMode.FINANCE, "Trésorerie", Icons.Default.CurrencyExchange),
            Triple(InventoryViewMode.CLASS_FINANCE, "Classes", Icons.Default.Groups),
            Triple(InventoryViewMode.INVENTORY, "Inventaire", Icons.Default.Inventory)
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

private fun formatCFA(amount: Double): String {
    return "%,.0f FCFA".format(amount).replace(",", " ")
}
