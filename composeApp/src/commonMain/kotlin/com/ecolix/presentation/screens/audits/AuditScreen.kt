package com.ecolix.presentation.screens.audits

import androidx.compose.animation.*
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecolix.data.models.*
import com.ecolix.presentation.components.SearchBar

@Composable
fun AuditScreenContent(isDarkMode: Boolean) {
    val screenModel = remember { AuditScreenModel() }
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
                        text = "Journaux d'Audit",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = if (isCompact) 24.sp else 32.sp
                        ),
                        color = state.colors.textPrimary
                    )
                    if (!isCompact) {
                        Text(
                            text = "Surveillance de l'activité du système et actions administratives",
                            style = MaterialTheme.typography.bodyMedium,
                            color = state.colors.textMuted
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(
                        onClick = { /* Export */ },
                        colors = IconButtonDefaults.iconButtonColors(containerColor = state.colors.card)
                    ) {
                        Icon(Icons.Default.FileDownload, contentDescription = "Exporter", tint = state.colors.textPrimary)
                    }
                    IconButton(
                        onClick = { /* Refresh */ },
                        colors = IconButtonDefaults.iconButtonColors(containerColor = state.colors.card)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Actualiser", tint = state.colors.textPrimary)
                    }
                }
            }

            // Search and Filters
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

                if (!isCompact) {
                    ModuleFilterDropdown(
                        selected = state.selectedModule,
                        modules = state.modules,
                        onModuleChange = { screenModel.onModuleFilterChange(it) },
                        colors = state.colors
                    )

                    SeverityFilterToggle(
                        selected = state.selectedSeverity,
                        onSeverityChange = { screenModel.onSeverityFilterChange(it) },
                        colors = state.colors
                    )
                }
            }

            // Logs List
            Box(modifier = Modifier.weight(1f)) {
                val filteredLogs = remember(state.logs, state.searchQuery, state.selectedModule, state.selectedSeverity) {
                    state.logs.filter { log ->
                        (state.searchQuery.isEmpty() || 
                         log.action.contains(state.searchQuery, ignoreCase = true) ||
                         log.userName.contains(state.searchQuery, ignoreCase = true) ||
                         log.details?.contains(state.searchQuery, ignoreCase = true) == true) &&
                        (state.selectedModule == null || log.module == state.selectedModule) &&
                        (state.selectedSeverity == null || log.severity == state.selectedSeverity)
                    }
                }

                if (filteredLogs.isEmpty()) {
                    EmptyLogsView(state.colors)
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(filteredLogs, key = { it.id }) { log ->
                            AuditLogRow(log, state.colors, isCompact)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AuditLogRow(log: AuditLog, colors: DashboardColors, isCompact: Boolean) {
    val severityColor = when (log.severity) {
        LogSeverity.INFO -> Color(0xFF3B82F6)
        LogSeverity.WARNING -> Color(0xFFF59E0B)
        LogSeverity.CRITICAL -> Color(0xFFEF4444)
        LogSeverity.SECURITY -> Color(0xFF1E293B)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colors.card),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(severityColor.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (log.severity) {
                                LogSeverity.SECURITY -> Icons.Default.Shield
                                LogSeverity.CRITICAL -> Icons.Default.Error
                                LogSeverity.WARNING -> Icons.Default.Warning
                                LogSeverity.INFO -> Icons.Default.Info
                            },
                            contentDescription = null,
                            tint = severityColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Column {
                        Text(
                            text = log.action,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = colors.textPrimary
                        )
                        Text(
                            text = "${log.userName} (${log.userRole}) • ${log.module}",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.textMuted
                        )
                    }
                }
                
                Text(
                    text = log.timestamp,
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textMuted
                )
            }
            
            if (log.details != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(colors.divider.copy(alpha = 0.3f))
                        .padding(12.dp)
                ) {
                    Text(
                        text = log.details,
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.textPrimary
                    )
                }
            }
            
            if (log.ipAddress != null && !isCompact) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Dns, contentDescription = null, modifier = Modifier.size(14.dp), tint = colors.textMuted)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "IP: ${log.ipAddress}",
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.textMuted
                    )
                }
            }
        }
    }
}

@Composable
private fun ModuleFilterDropdown(
    selected: String?,
    modules: List<String>,
    onModuleChange: (String?) -> Unit,
    colors: DashboardColors
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Surface(
            modifier = Modifier
                .height(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .clickable { expanded = true },
            color = colors.card,
            border = androidx.compose.foundation.BorderStroke(1.dp, colors.divider)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selected ?: "Tous les modules",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (selected == null) colors.textMuted else colors.textPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = colors.textMuted)
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(colors.card)
        ) {
            DropdownMenuItem(
                text = { Text("Tous les modules", color = colors.textPrimary) },
                onClick = {
                    onModuleChange(null)
                    expanded = false
                }
            )
            modules.forEach { module ->
                DropdownMenuItem(
                    text = { Text(module, color = colors.textPrimary) },
                    onClick = {
                        onModuleChange(module)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun SeverityFilterToggle(
    selected: LogSeverity?,
    onSeverityChange: (LogSeverity?) -> Unit,
    colors: DashboardColors
) {
    Row(
        modifier = Modifier
            .height(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(colors.card)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        val options = listOf(null) + LogSeverity.entries
                options.forEach { option ->
                    val isSelected = selected == option
                    val color = when (option) {
                        LogSeverity.INFO -> Color(0xFF3B82F6)
                        LogSeverity.WARNING -> Color(0xFFF59E0B)
                        LogSeverity.CRITICAL -> Color(0xFFEF4444)
                        LogSeverity.SECURITY -> Color(0xFF1E293B)
                        null -> colors.textMuted
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) color.copy(alpha = 0.2f) else Color.Transparent)
                            .clickable { onSeverityChange(option) }
                            .padding(horizontal = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = option?.toFrench() ?: "TOUS",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (isSelected) color else colors.textMuted
                        )
                    }
                }
    }
}

@Composable
private fun EmptyLogsView(colors: DashboardColors) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.SearchOff,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = colors.textMuted.copy(alpha = 0.3f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Aucun log ne correspond à vos critères",
            style = MaterialTheme.typography.bodyLarge,
            color = colors.textMuted
        )
    }
}
