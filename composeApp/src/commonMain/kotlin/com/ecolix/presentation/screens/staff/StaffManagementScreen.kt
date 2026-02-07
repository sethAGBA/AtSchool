package com.ecolix.presentation.screens.staff

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.ecolix.atschool.models.StaffRole
import com.ecolix.data.models.DashboardColors
import com.ecolix.data.models.color
import com.ecolix.data.models.icon
import com.ecolix.presentation.components.CardContainer


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffManagementScreen(
    currentDepartments: List<String>,
    colors: DashboardColors,
    onAddDepartment: (String) -> Unit,
    onDeleteDepartment: (String) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Départements", "Rôles")

    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(24.dp)) {
        // Simple Tab Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, colors.divider, RoundedCornerShape(12.dp))
                .clip(RoundedCornerShape(12.dp))
                .background(colors.card)
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            tabs.forEachIndexed { index, title ->
                val isSelected = selectedTab == index
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                        .clickable { selectedTab = index }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (isSelected) Color.White else colors.textMuted
                    )
                }
            }
        }

        when (selectedTab) {
            0 -> DepartmentsManagement(currentDepartments, colors, onAddDepartment, onDeleteDepartment)
            1 -> RolesManagement(colors)
        }
    }
}

@Composable
private fun DepartmentsManagement(
    departments: List<String>,
    colors: DashboardColors,
    onAddDepartment: (String) -> Unit,
    onDeleteDepartment: (String) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var newDepartmentName by remember { mutableStateOf("") }

    if (showAddDialog) {
        Dialog(onDismissRequest = { showAddDialog = false }) {
            CardContainer(containerColor = colors.card) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        "Ajouter un département",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = colors.textPrimary
                    )
                    OutlinedTextField(
                        value = newDepartmentName,
                        onValueChange = { newDepartmentName = it },
                        label = { Text("Nom du département") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = colors.divider,
                            focusedTextColor = colors.textPrimary,
                            unfocusedTextColor = colors.textPrimary,
                            focusedLabelColor = MaterialTheme.colorScheme.primary,
                            unfocusedLabelColor = colors.textMuted
                        )
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = { showAddDialog = false }) {
                            Text("Annuler", color = colors.textMuted)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (newDepartmentName.isNotBlank()) {
                                    onAddDepartment(newDepartmentName)
                                    showAddDialog = false
                                    newDepartmentName = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            enabled = newDepartmentName.isNotBlank()
                        ) {
                            Text("Ajouter")
                        }
                    }
                }
            }
        }
    }

    var showDeleteConfirmDialog by remember { mutableStateOf<String?>(null) }

    if (showDeleteConfirmDialog != null) {
        val deptToDelete = showDeleteConfirmDialog!!
        com.ecolix.presentation.components.ConfirmationDialog(
            title = "Supprimer le département ?",
            message = "Êtes-vous sûr de vouloir supprimer le département \"$deptToDelete\" ? cette action est irréversible.",
            onConfirm = {
                onDeleteDepartment(deptToDelete)
                showDeleteConfirmDialog = null
            },
            onDismiss = { showDeleteConfirmDialog = null },
            colors = colors
        )
    }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "Gestion des Départements",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = colors.textPrimary
                )
                Text(
                    "Ajoutez ou supprimez des départements disponibles",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textMuted
                )
            }
            Button(
                onClick = { showAddDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Ajouter")
            }
        }

        val displayDepts = departments.filter { it != "Toutes" }
        
        if (displayDepts.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                Text("Aucun département trouvé.", color = colors.textMuted)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(displayDepts) { dept ->
                    DepartmentItem(dept, colors, onDelete = { showDeleteConfirmDialog = dept })
                }
            }
        }
    }
}

@Composable
private fun DepartmentItem(name: String, colors: DashboardColors, onDelete: () -> Unit) {
    CardContainer(
        containerColor = colors.card,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Business,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    color = colors.textPrimary
                )
            }
            
            /* IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Supprimer",
                    tint = Color(0xFFEF4444)
                )
            } */
        }
    }
}

@Composable
private fun RolesManagement(colors: DashboardColors) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Column {
            Text(
                "Rôles du Personnel",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = colors.textPrimary
            )
            Text(
                "Liste des rôles définis dans le système (Lecture seule)",
                style = MaterialTheme.typography.bodySmall,
                color = colors.textMuted
            )
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(StaffRole.entries.toTypedArray()) { role ->
                RoleItem(role, colors)
            }
        }
    }
}

@Composable
private fun RoleItem(role: StaffRole, colors: DashboardColors) {
    CardContainer(
        containerColor = colors.card,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(role.color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    role.icon,
                    contentDescription = null,
                    tint = role.color,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = role.label,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    color = colors.textPrimary
                )
                Text(
                    text = "CODE: ${role.name}",
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textMuted
                )
            }
        }
    }
}
