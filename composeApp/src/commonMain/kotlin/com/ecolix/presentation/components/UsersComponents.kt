package com.ecolix.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecolix.data.models.DashboardColors
import com.ecolix.data.models.UsersViewMode
import com.ecolix.data.models.UserRole

@Composable
fun UsersViewToggle(
    currentMode: UsersViewMode,
    onModeChange: (UsersViewMode) -> Unit,
    colors: DashboardColors,
    modifier: Modifier = Modifier,
    isFullWidth: Boolean = false
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(colors.card)
            .border(1.dp, colors.divider, RoundedCornerShape(12.dp))
            .padding(4.dp)
    ) {
        listOf(
            Triple(UsersViewMode.ADMINS, "Admins", Icons.Filled.AdminPanelSettings),
            Triple(UsersViewMode.TEACHERS, "Enseignants", Icons.Filled.School),
            Triple(UsersViewMode.PARENTS, "Parents", Icons.Filled.Groups)
        ).forEach { (mode, label, icon) ->
            UsersToggleItem(
                selected = currentMode == mode,
                onClick = { onModeChange(mode) },
                label = label,
                icon = icon,
                colors = colors,
                modifier = if (isFullWidth) Modifier.weight(1f) else Modifier
            )
        }
    }
}

@Composable
private fun UsersToggleItem(
    selected: Boolean,
    onClick: () -> Unit,
    label: String,
    icon: ImageVector,
    colors: DashboardColors,
    modifier: Modifier = Modifier
) {
    val bg = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent
    val contentColor = if (selected) Color.White else colors.textMuted
    
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp), tint = contentColor)
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = label, color = contentColor, style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, fontSize = 12.sp), maxLines = 1)
    }
}

@Composable
fun UsersActionBar(
    onAddUserClick: () -> Unit,
    colors: DashboardColors,
    isCompact: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(if (isCompact) 8.dp else 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ActionButton(
            icon = Icons.Default.Add,
            label = if (isCompact) "Ajouter" else "Nouvel Utilisateur",
            bg = MaterialTheme.colorScheme.primary,
            contentColor = Color.White,
            onClick = onAddUserClick,
            modifier = if (isCompact) Modifier.weight(1f) else Modifier
        )

        if (!isCompact) {
            ActionButton(
                icon = Icons.Default.FileUpload,
                label = "Importer",
                bg = colors.card,
                contentColor = colors.textPrimary,
                modifier = Modifier,
                colors = colors,
                onClick = {}
            )
            
            ActionButton(
                icon = Icons.Default.FileDownload,
                label = "Exporter",
                bg = colors.card,
                contentColor = colors.textPrimary,
                modifier = Modifier,
                colors = colors,
                onClick = {}
            )
        }
        
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun UsersAdvancedFilters(
    selectedRole: UserRole?,
    onRoleChange: (UserRole?) -> Unit,
    selectedStatus: String?,
    onStatusChange: (String?) -> Unit,
    colors: DashboardColors,
    isCompact: Boolean = false
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            item {
                Text(
                    "Rôles:",
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textMuted,
                    modifier = Modifier.padding(end = 4.dp)
                )
            }
            
            item {
                FilterChip(
                    selected = selectedRole == null,
                    onClick = { onRoleChange(null) },
                    label = { Text("Tous") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        selectedLabelColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
            
            UserRole.entries.forEach { role ->
                item {
                    FilterChip(
                        selected = selectedRole == role,
                        onClick = { onRoleChange(role) },
                        label = { Text(role.label) },
                        leadingIcon = { Icon(role.icon, null, modifier = Modifier.size(16.dp)) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = role.color.copy(alpha = 0.1f),
                            selectedLabelColor = role.color
                        )
                    )
                }
            }
        }
        
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            item {
                Text(
                    "Statut:",
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textMuted,
                    modifier = Modifier.padding(end = 4.dp)
                )
            }
            
            item {
                FilterChip(
                    selected = selectedStatus == null,
                    onClick = { onStatusChange(null) },
                    label = { Text("Tous") }
                )
            }
            
            listOf("Actif", "Inactif", "Suspendu").forEach { status ->
                item {
                    FilterChip(
                        selected = selectedStatus == status,
                        onClick = { onStatusChange(status) },
                        label = { Text(status) }
                    )
                }
            }
        }
    }
}
