package com.ecolix.presentation.screens.staff

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ecolix.atschool.models.Staff
import com.ecolix.data.models.DashboardColors
import com.ecolix.presentation.components.ConfirmationDialog

@Composable
fun StaffTrashScreen(
    deletedStaff: List<Staff>,
    colors: DashboardColors,
    onRestore: (String) -> Unit,
    onPermanentDelete: (String) -> Unit
) {
    var itemToDelete by remember { mutableStateOf<Staff?>(null) }
    var itemToRestore by remember { mutableStateOf<Staff?>(null) }

    if (itemToDelete != null) {
        ConfirmationDialog(
            title = "Suppression définitive",
            message = "Êtes-vous sûr de vouloir supprimer définitivement ${itemToDelete?.firstName} ${itemToDelete?.lastName} ? Cette action est irréversible.",
            onConfirm = {
                onPermanentDelete(itemToDelete!!.id)
                itemToDelete = null
            },
            onDismiss = { itemToDelete = null },
            colors = colors,
            confirmText = "Supprimer définitivement",
            confirmColor = MaterialTheme.colorScheme.error
        )
    }

    if (itemToRestore != null) {
        ConfirmationDialog(
            title = "Restaurer le membre",
            message = "Voulez-vous restaurer ${itemToRestore?.firstName} ${itemToRestore?.lastName} dans la liste principale ?",
            onConfirm = {
                onRestore(itemToRestore!!.id)
                itemToRestore = null
            },
            onDismiss = { itemToRestore = null },
            colors = colors,
            confirmText = "Restaurer",
            confirmColor = Color(0xFF10B981)
        )
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "Corbeille",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = colors.textPrimary
                )
                Text(
                    "${deletedStaff.size} élément(s) supprimé(s)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textMuted
                )
            }
        }

        if (deletedStaff.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = colors.textMuted.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "La corbeille est vide",
                        style = MaterialTheme.typography.titleMedium,
                        color = colors.textMuted
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(deletedStaff) { staff ->
                    TrashItem(staff, colors, onRestore = { itemToRestore = staff }, onDelete = { itemToDelete = staff })
                }
            }
        }
    }
}

@Composable
fun TrashItem(
    staff: Staff,
    colors: DashboardColors,
    onRestore: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = colors.card),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Avatar / Icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(colors.background),
                contentAlignment = Alignment.Center
            ) {
                if (staff.photoUrl != null) {
                    // Placeholder for image logic
                    Icon(Icons.Default.Person, null, tint = colors.textMuted)
                } else {
                    Text(
                        text = "${staff.firstName.first()}${staff.lastName.first()}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = colors.textPrimary
                    )
                }
            }

            // Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "${staff.firstName} ${staff.lastName}",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = colors.textPrimary
                )
                Text(
                    "${staff.role.label} • ${staff.department}",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textMuted
                )
            }

            // Actions
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(
                    onClick = onRestore,
                    colors = IconButtonDefaults.iconButtonColors(contentColor = Color(0xFF10B981))
                ) {
                    Icon(Icons.Default.Restore, contentDescription = "Restaurer")
                }
                
                IconButton(
                    onClick = onDelete,
                    colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.Default.DeleteForever, contentDescription = "Supprimer définitivement")
                }
            }
        }
    }
}
