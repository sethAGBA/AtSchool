package com.ecolix.presentation.screens.accounting

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
fun AccountingScreenContent(isDarkMode: Boolean) {
    val screenModel = remember { AccountingScreenModel() }
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
                        text = "Comptabilité",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = if (isCompact) 24.sp else 32.sp
                        ),
                        color = state.colors.textPrimary
                    )
                    if (!isCompact) {
                        Text(
                            text = "Suivi des revenus, dépenses et bilan financier",
                            style = MaterialTheme.typography.bodyMedium,
                            color = state.colors.textMuted
                        )
                    }
                }

                if (!isCompact) {
                    TypeToggle(
                        selected = state.selectedType,
                        onTypeChange = { screenModel.onTypeFilterChange(it) },
                        colors = state.colors
                    )
                }
            }

            // Summary Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SummaryCard(
                    title = "Revenus",
                    amount = state.summary.totalRevenue,
                    icon = Icons.Default.TrendingUp,
                    color = Color(0xFF10B981), // Emerald
                    modifier = Modifier.weight(1f),
                    colors = state.colors
                )
                SummaryCard(
                    title = "Dépenses",
                    amount = state.summary.totalExpenses,
                    icon = Icons.Default.TrendingDown,
                    color = Color(0xFFEF4444), // Red
                    modifier = Modifier.weight(1f),
                    colors = state.colors
                )
                if (!isCompact) {
                    SummaryCard(
                        title = "Balance",
                        amount = state.summary.balance,
                        icon = Icons.Default.AccountBalanceWallet,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f),
                        colors = state.colors
                    )
                }
            }

            // Filters Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { /* Add Transaction */ },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                    modifier = Modifier.height(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    if (!isCompact) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Nouvelle Transaction")
                    }
                }

                SearchBar(
                    query = state.searchQuery,
                    onQueryChange = { screenModel.onSearchQueryChange(it) },
                    colors = state.colors,
                    modifier = Modifier.weight(1f)
                )
            }

            // Transactions List
            Box(modifier = Modifier.weight(1f)) {
                val filteredTransactions = remember(state.transactions, state.searchQuery, state.selectedType, state.selectedCategory) {
                    state.transactions.filter { tx ->
                        (state.searchQuery.isEmpty() || tx.description.contains(state.searchQuery, ignoreCase = true) || tx.reference?.contains(state.searchQuery, ignoreCase = true) == true) &&
                        (state.selectedType == null || tx.type == state.selectedType) &&
                        (state.selectedCategory == null || tx.category == state.selectedCategory)
                    }
                }

                if (filteredTransactions.isEmpty()) {
                    EmptyTransactionsView(state.colors)
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(filteredTransactions, key = { it.id }) { transaction ->
                            TransactionCard(transaction, state.colors)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryCard(
    title: String,
    amount: Double,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier,
    colors: DashboardColors
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = colors.card),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, style = MaterialTheme.typography.labelMedium, color = colors.textMuted)
            }
            Text(
                text = "${formatCurrency(amount)} FCFA",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = colors.textPrimary
            )
        }
    }
}

@Composable
private fun TransactionCard(transaction: FinancialTransaction, colors: DashboardColors) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colors.card),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        if (transaction.type == TransactionType.INCOME) Color(0xFF10B981).copy(alpha = 0.1f)
                        else Color(0xFFEF4444).copy(alpha = 0.1f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (transaction.type == TransactionType.INCOME) Icons.Default.Add else Icons.Default.Remove,
                    contentDescription = null,
                    tint = if (transaction.type == TransactionType.INCOME) Color(0xFF10B981) else Color(0xFFEF4444),
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.description,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    color = colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = transaction.category.toFrench(),
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.textMuted
                    )
                    if (transaction.reference != null) {
                        Text(" • ", color = colors.textMuted)
                        Text(transaction.reference, style = MaterialTheme.typography.labelSmall, color = colors.textMuted)
                    }
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${if (transaction.type == TransactionType.INCOME) "+" else "-"}${formatCurrency(transaction.amount)}",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    color = if (transaction.type == TransactionType.INCOME) Color(0xFF10B981) else Color(0xFFEF4444)
                )
                Text(
                    text = transaction.date,
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textMuted
                )
            }
        }
    }
}

@Composable
private fun TypeToggle(
    selected: TransactionType?,
    onTypeChange: (TransactionType?) -> Unit,
    colors: DashboardColors
) {
    Row(
        modifier = Modifier
            .height(44.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(colors.card)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        val options = listOf(null, TransactionType.INCOME, TransactionType.EXPENSE)
        options.forEach { option ->
            val isSelected = selected == option
            val icon = when (option) {
                TransactionType.INCOME -> Icons.Default.TrendingUp
                TransactionType.EXPENSE -> Icons.Default.TrendingDown
                else -> Icons.Default.AccountBalanceWallet
            }
            
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                    .clickable { onTypeChange(option) }
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = if (isSelected) Color.White else colors.textMuted
                    )
                    Text(
                        text = when (option) {
                            null -> "Tout"
                            TransactionType.INCOME -> "Revenus"
                            TransactionType.EXPENSE -> "Dépenses"
                        },
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (isSelected) Color.White else colors.textMuted
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyTransactionsView(colors: DashboardColors) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.ReceiptLong,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = colors.textMuted.copy(alpha = 0.3f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Aucune transaction trouvée",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
            color = colors.textMuted
        )
    }
}

private fun formatCurrency(amount: Double): String {
    return amount.toLong().toString().reversed().chunked(3).joinToString(" ").reversed()
}
