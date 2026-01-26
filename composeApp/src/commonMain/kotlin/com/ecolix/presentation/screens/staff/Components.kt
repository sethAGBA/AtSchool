package com.ecolix.presentation.screens.staff

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecolix.data.models.DashboardColors
import com.ecolix.data.models.Staff
import com.ecolix.data.models.StaffRole
import com.ecolix.data.models.StaffViewMode
import com.ecolix.presentation.components.CardContainer
import com.ecolix.presentation.components.TagPill

@Composable
fun StaffRow(
    staff: Staff,
    colors: DashboardColors,
    selectionMode: Boolean = false,
    isSelected: Boolean = false,
    onToggleSelect: () -> Unit = {},
    onClick: () -> Unit
) {
    CardContainer(
        containerColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.05f) else colors.card,
        modifier = Modifier.padding(vertical = 4.dp).clickable { if (selectionMode) onToggleSelect() else onClick() }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (selectionMode) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = { onToggleSelect() },
                    colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(staff.role.color.copy(alpha = if (colors.textPrimary == Color(0xFFF8FAFC)) 0.25f else 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(staff.role.icon, contentDescription = null, tint = if (colors.textPrimary == Color(0xFFF8FAFC)) staff.role.color.copy(alpha = 0.9f) else staff.role.color)
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${staff.firstName} ${staff.lastName}",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    color = colors.textPrimary
                )
                Text(
                    text = "${staff.role.label} • ${staff.department}",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textMuted
                )
            }
            
            Column(horizontalAlignment = Alignment.End) {
                TagPill(staff.status, if (staff.status == "Actif") Color(0xFF10B981) else Color(0xFFF59E0B))
                if (staff.matricule != null) {
                    Text(staff.matricule, style = MaterialTheme.typography.labelSmall, color = colors.textMuted)
                }
            }
            
            if (!selectionMode) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = colors.textMuted)
            }
        }
    }
}

@Composable
fun StaffCard(
    staff: Staff,
    colors: DashboardColors,
    selectionMode: Boolean = false,
    isSelected: Boolean = false,
    onToggleSelect: () -> Unit = {},
    onClick: () -> Unit
) {
    CardContainer(
        containerColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.05f) else colors.card,
        modifier = Modifier.clickable { if (selectionMode) onToggleSelect() else onClick() }
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (selectionMode) {
                        Checkbox(
                            checked = isSelected,
                            onCheckedChange = { onToggleSelect() },
                            colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(staff.role.color.copy(alpha = if (colors.textPrimary == Color(0xFFF8FAFC)) 0.25f else 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(staff.role.icon, contentDescription = null, tint = if (colors.textPrimary == Color(0xFFF8FAFC)) staff.role.color.copy(alpha = 0.9f) else staff.role.color)
                    }
                }
                TagPill(staff.status, if (staff.status == "Actif") Color(0xFF10B981) else Color(0xFFF59E0B))
            }
            
            Column {
                Text(
                    text = "${staff.firstName} ${staff.lastName}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = staff.role.label,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textMuted
                )
            }
            
            HorizontalDivider(color = colors.divider, thickness = 0.5.dp)
            
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                StaffInfoMini(Icons.Default.Work, staff.department, colors)
                if (staff.specialty != null) {
                    StaffInfoMini(Icons.Default.Book, staff.specialty, colors)
                }
                StaffInfoMini(Icons.Default.Phone, staff.phone, colors)
            }
        }
    }
}

@Composable
private fun StaffInfoMini(icon: ImageVector, text: String, colors: DashboardColors) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            icon, 
            contentDescription = null, 
            modifier = Modifier.size(14.dp), 
            tint = if (colors.textPrimary == Color(0xFFF8FAFC)) colors.textMuted.copy(alpha = 0.9f) else colors.textMuted
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, style = MaterialTheme.typography.labelSmall, color = colors.textPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
fun StaffViewToggle(
    currentMode: StaffViewMode,
    onModeChange: (StaffViewMode) -> Unit,
    colors: DashboardColors,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(colors.card)
            .border(1.dp, colors.divider, RoundedCornerShape(12.dp))
            .padding(4.dp)
    ) {
        ToggleItem(
            selected = currentMode == StaffViewMode.LIST,
            onClick = { onModeChange(StaffViewMode.LIST) },
            label = "Liste",
            icon = Icons.AutoMirrored.Filled.List,
            colors = colors
        )
        ToggleItem(
            selected = currentMode == StaffViewMode.GRID,
            onClick = { onModeChange(StaffViewMode.GRID) },
            label = "Grille",
            icon = Icons.Default.GridView,
            colors = colors
        )
    }
}

@Composable
private fun ToggleItem(
    selected: Boolean,
    onClick: () -> Unit,
    label: String,
    icon: ImageVector,
    colors: DashboardColors
) {
    val bg = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent
    val contentColor = if (selected) Color.White else colors.textMuted
    
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp), tint = contentColor)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label, color = contentColor, style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
    }
}

@Composable
fun StaffSelectionActionBar(
    selectedCount: Int,
    onClearSelection: () -> Unit,
    onDeleteSelected: () -> Unit,
    onStatusChange: (String) -> Unit,
    colors: DashboardColors,
    isCompact: Boolean = false
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth(if (isCompact) 1f else 0.7f)
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, colors.divider, RoundedCornerShape(20.dp)),
        color = MaterialTheme.colorScheme.primary,
        tonalElevation = 8.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onClearSelection) {
                    Icon(Icons.Default.Close, contentDescription = null, tint = Color.White)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "$selectedCount sélectionné(s)",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
            
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (!isCompact) {
                    Button(
                        onClick = { onStatusChange("Actif") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.2f), contentColor = Color.White),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Activer")
                    }
                }
                
                IconButton(onClick = onDeleteSelected) {
                    Icon(Icons.Default.Delete, contentDescription = "Supprimer", tint = Color.White)
                }
                
                IconButton(onClick = { /* More actions */ }) {
                    Icon(Icons.Default.MoreVert, contentDescription = null, tint = Color.White)
                }
            }
        }
    }
}

@Composable
fun StaffStatsSection(
    total: Int,
    teachers: Int,
    admins: Int,
    onLeave: Int,
    colors: DashboardColors,
    isCompact: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        MiniStatCard("Total", total.toString(), Icons.Default.Groups, Color(0xFF6366F1), colors, Modifier.weight(1f))
        MiniStatCard("Enseignants", teachers.toString(), Icons.Default.School, Color(0xFF10B981), colors, Modifier.weight(1f))
        if (!isCompact) {
            MiniStatCard("Admins", admins.toString(), Icons.Default.AdminPanelSettings, Color(0xFFF59E0B), colors, Modifier.weight(1f))
            MiniStatCard("En congé", onLeave.toString(), Icons.Default.EventBusy, Color(0xFFEF4444), colors, Modifier.weight(1f))
        }
    }
}

@Composable
private fun MiniStatCard(
    label: String,
    value: String,
    icon: ImageVector,
    color: Color,
    colors: DashboardColors,
    modifier: Modifier = Modifier
) {
    CardContainer(containerColor = colors.card, modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(color.copy(alpha = if (colors.textPrimary == Color(0xFFF8FAFC)) 0.25f else 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon, 
                    contentDescription = null, 
                    modifier = Modifier.size(20.dp), 
                    tint = if (colors.textPrimary == Color(0xFFF8FAFC)) color.copy(alpha = 0.9f) else color
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(value, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = colors.textPrimary)
                Text(label, style = MaterialTheme.typography.labelSmall, color = colors.textMuted)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StaffDistributionChart(
    distribution: Map<StaffRole, Int>,
    colors: DashboardColors
) {
    CardContainer(containerColor = colors.card) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(
                text = "Distribution par Rôle",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = colors.textPrimary
            )
            
            Row(
                modifier = Modifier.fillMaxWidth().height(16.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val total = distribution.values.sum()
                if (total > 0) {
                    distribution.forEach { (role, count) ->
                        val weight = count.toFloat() / total
                        if (weight > 0) {
                            Box(
                                modifier = Modifier
                                    .weight(weight)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(role.color)
                            )
                        }
                    }
                }
            }
            
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                distribution.forEach { (role, count) ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(role.color))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${role.label}: $count",
                            style = MaterialTheme.typography.labelSmall,
                            color = colors.textPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyState(
    message: String,
    icon: ImageVector = Icons.Default.SearchOff,
    colors: DashboardColors
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = colors.textMuted.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = colors.textMuted,
            textAlign = TextAlign.Center
        )
    }
}
