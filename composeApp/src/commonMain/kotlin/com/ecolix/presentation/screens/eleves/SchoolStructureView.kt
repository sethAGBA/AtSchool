package com.ecolix.presentation.screens.eleves

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecolix.data.models.DashboardColors
import com.ecolix.data.models.StudentsUiState
import com.ecolix.presentation.components.CardContainer

@Composable
fun SchoolStructureView(
    uiState: StudentsUiState,
    screenModel: StudentsScreenModel,
    colors: DashboardColors,
    isCompact: Boolean
) {
    var showAddCycleDialog by remember { mutableStateOf(false) }
    var showAddLevelDialog by remember { mutableStateOf<Int?>(null) } // cycleId
    var cycleToEdit by remember { mutableStateOf<com.ecolix.atschool.api.SchoolCycleDto?>(null) }
    var levelToEdit by remember { mutableStateOf<com.ecolix.atschool.api.SchoolLevelDto?>(null) }
    var cycleToDelete by remember { mutableStateOf<com.ecolix.atschool.api.SchoolCycleDto?>(null) }
    var levelToDelete by remember { mutableStateOf<com.ecolix.atschool.api.SchoolLevelDto?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Organisation de l'établissement (${uiState.currentYear})",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = colors.textPrimary
            )
            
            Button(
                onClick = { showAddCycleDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Ajouter un Cycle")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(uiState.cycles.sortedBy { it.sortOrder }) { cycle ->
                CycleItem(
                    cycle = cycle,
                    levels = uiState.levels.filter { it.cycleId == cycle.id },
                    colors = colors,
                    onAddLevel = { showAddLevelDialog = cycle.id },
                    onEditCycle = { cycleToEdit = cycle },
                    onEditLevel = { level -> levelToEdit = level },
                    onDeleteCycle = { cycleToDelete = cycle },
                    onDeleteLevel = { levelId -> 
                        levelToDelete = uiState.levels.find { it.id == levelId }
                    }
                )
            }
            
            if (uiState.cycles.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 64.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountTree,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = colors.textMuted.copy(alpha = 0.3f)
                        )
                        
                        Text(
                            text = "Aucune structure définie",
                            style = MaterialTheme.typography.titleMedium,
                            color = colors.textPrimary
                        )
                        
                        Text(
                            text = "Générez automatiquement les cycles et niveaux en fonction de votre type d'établissement.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.textMuted,
                            modifier = Modifier.padding(horizontal = 32.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Button(
                            onClick = { screenModel.seedDefaultStructure() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                contentColor = MaterialTheme.colorScheme.primary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.AutoFixHigh, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Générer la structure par défaut")
                        }
                    }
                }
            }
        }
    }

    if (showAddCycleDialog) {
        val nextOrder = (uiState.cycles.maxOfOrNull { it.sortOrder } ?: 0) + 1
        AddCycleDialog(
            nextOrder = nextOrder,
            onDismiss = { showAddCycleDialog = false },
            onConfirm = { name, order ->
                screenModel.createCycle(name, order)
                showAddCycleDialog = false
            },
            colors = colors
        )
    }

    showAddLevelDialog?.let { cycleId ->
        val cycleLevels = uiState.levels.filter { it.cycleId == cycleId }
        val nextOrder = (cycleLevels.maxOfOrNull { it.sortOrder } ?: 0) + 1
        AddLevelDialog(
            nextOrder = nextOrder,
            onDismiss = { showAddLevelDialog = null },
            onConfirm = { name, order ->
                screenModel.createLevel(cycleId, name, order)
                showAddLevelDialog = null
            },
            colors = colors
        )
    }

    cycleToEdit?.let { cycle ->
        EditCycleDialog(
            cycle = cycle,
            onDismiss = { cycleToEdit = null },
            onConfirm = { name, order ->
                cycle.id?.let { screenModel.updateCycle(it, name, order) }
                cycleToEdit = null
            },
            colors = colors
        )
    }

    levelToEdit?.let { level ->
        EditLevelDialog(
            level = level,
            onDismiss = { levelToEdit = null },
            onConfirm = { name, order ->
                level.id?.let { screenModel.updateLevel(it, level.cycleId, name, order) }
                levelToEdit = null
            },
            colors = colors
        )
    }

    cycleToDelete?.let { cycle ->
        val levelsInCycle = uiState.levels.filter { it.cycleId == cycle.id }
        val classesInCycle = uiState.classrooms.filter { classroom -> 
            levelsInCycle.any { it.id == classroom.schoolLevelId } 
        }
        val isInUse = classesInCycle.isNotEmpty()

        AlertDialog(
            onDismissRequest = { cycleToDelete = null },
            title = { 
                Text(
                    text = if (isInUse) "Suppression Impossible" else "Supprimer le cycle",
                    color = if (isInUse) MaterialTheme.colorScheme.error else colors.textPrimary
                ) 
            },
            text = { 
                if (isInUse) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Le cycle \"${cycle.name}\" ne peut pas être supprimé car il contient des niveaux liés aux classes suivantes :")
                        Text(
                            text = classesInCycle.joinToString(", ") { it.name },
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = colors.textPrimary
                        )
                        Text("Supprimez d'abord ces classes pour pouvoir supprimer le cycle.")
                    }
                } else {
                    Text("Voulez-vous vraiment supprimer le cycle \"${cycle.name}\" ? Cela supprimera également tous les niveaux associés.")
                }
            },
            confirmButton = {
                if (isInUse) {
                    Button(onClick = { cycleToDelete = null }) { Text("Compris") }
                } else {
                    Button(
                        onClick = {
                            cycle.id?.let { screenModel.deleteCycle(it) }
                            cycleToDelete = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) { Text("Supprimer") }
                }
            },
            dismissButton = {
                if (!isInUse) {
                    TextButton(onClick = { cycleToDelete = null }) { Text("Annuler") }
                }
            }
        )
    }

    levelToDelete?.let { level ->
        val classesInLevel = uiState.classrooms.filter { it.schoolLevelId == level.id }
        val isInUse = classesInLevel.isNotEmpty()

        AlertDialog(
            onDismissRequest = { levelToDelete = null },
            title = { 
                Text(
                    text = if (isInUse) "Suppression Impossible" else "Supprimer le niveau",
                    color = if (isInUse) MaterialTheme.colorScheme.error else colors.textPrimary
                ) 
            },
            text = { 
                if (isInUse) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Le niveau \"${level.name}\" ne peut pas être supprimé car il est utilisé par les classes suivantes :")
                        Text(
                            text = classesInLevel.joinToString(", ") { it.name },
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = colors.textPrimary
                        )
                        Text("Supprimez d'abord ces classes pour pouvoir supprimer le niveau.")
                    }
                } else {
                    Text("Voulez-vous vraiment supprimer le niveau \"${level.name}\" ?")
                }
            },
            confirmButton = {
                if (isInUse) {
                    Button(onClick = { levelToDelete = null }) { Text("Compris") }
                } else {
                    Button(
                        onClick = {
                            level.id?.let { screenModel.deleteLevel(it) }
                            levelToDelete = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) { Text("Supprimer") }
                }
            },
            dismissButton = {
                if (!isInUse) {
                    TextButton(onClick = { levelToDelete = null }) { Text("Annuler") }
                }
            }
        )
    }
}

@Composable
fun CycleItem(
    cycle: com.ecolix.atschool.api.SchoolCycleDto,
    levels: List<com.ecolix.atschool.api.SchoolLevelDto>,
    colors: DashboardColors,
    onAddLevel: () -> Unit,
    onEditCycle: () -> Unit,
    onEditLevel: (com.ecolix.atschool.api.SchoolLevelDto) -> Unit,
    onDeleteCycle: () -> Unit,
    onDeleteLevel: (Int) -> Unit
) {
    CardContainer(containerColor = colors.card) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Folder, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = cycle.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = colors.textPrimary,
                    modifier = Modifier.weight(1f)
                )
                
                IconButton(onClick = onAddLevel) {
                    Icon(Icons.Default.AddCircleOutline, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = onEditCycle) {
                    Icon(Icons.Default.Edit, contentDescription = null, tint = colors.textMuted, modifier = Modifier.size(18.dp))
                }
                IconButton(onClick = onDeleteCycle) {
                    Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFEF4444).copy(alpha = 0.7f))
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Column(modifier = Modifier.padding(start = 32.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                levels.sortedBy { it.sortOrder }.forEach { level ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(colors.background.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = level.name,
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.textPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { onEditLevel(level) }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp), tint = colors.textMuted)
                        }
                        IconButton(onClick = { level.id?.let { onDeleteLevel(it) } }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(14.dp), tint = colors.textMuted)
                        }
                    }
                }
                
                if (levels.isEmpty()) {
                    Text("Aucun niveau ajouté", style = MaterialTheme.typography.bodySmall, color = colors.textMuted)
                }
            }
        }
    }
}

@Composable
fun AddCycleDialog(
    nextOrder: Int,
    onDismiss: () -> Unit,
    onConfirm: (String, Int) -> Unit,
    colors: DashboardColors
) {
    var name by remember { mutableStateOf("") }
    var order by remember { mutableStateOf(nextOrder.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nouveau Cycle") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nom du cycle (ex: Primaire)") },
                    modifier = Modifier.fillMaxWidth()
                )
                TextField(
                    value = order,
                    onValueChange = { order = it },
                    label = { Text("Ordre d'affichage") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { if (name.isNotBlank()) onConfirm(name, order.toIntOrNull() ?: nextOrder) },
                enabled = name.isNotBlank()
            ) { Text("Créer") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annuler") }
        }
    )
}

@Composable
fun AddLevelDialog(
    nextOrder: Int,
    onDismiss: () -> Unit,
    onConfirm: (String, Int) -> Unit,
    colors: DashboardColors
) {
    var name by remember { mutableStateOf("") }
    var order by remember { mutableStateOf(nextOrder.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nouveau Niveau Scolaire") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nom (ex: CM1, 6ème)") },
                    modifier = Modifier.fillMaxWidth()
                )
                TextField(
                    value = order,
                    onValueChange = { order = it },
                    label = { Text("Ordre d'affichage") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { if (name.isNotBlank()) onConfirm(name, order.toIntOrNull() ?: nextOrder) },
                enabled = name.isNotBlank()
            ) { Text("Ajouter") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annuler") }
        }
    )
}

@Composable
fun EditCycleDialog(
    cycle: com.ecolix.atschool.api.SchoolCycleDto,
    onDismiss: () -> Unit,
    onConfirm: (String, Int) -> Unit,
    colors: DashboardColors
) {
    var name by remember { mutableStateOf(cycle.name) }
    var order by remember { mutableStateOf(cycle.sortOrder.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Modifier le Cycle") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nom du cycle") },
                    modifier = Modifier.fillMaxWidth()
                )
                TextField(
                    value = order,
                    onValueChange = { order = it },
                    label = { Text("Ordre d'affichage") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { if (name.isNotBlank()) onConfirm(name, order.toIntOrNull() ?: cycle.sortOrder) },
                enabled = name.isNotBlank()
            ) { Text("Enregistrer") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annuler") }
        }
    )
}

@Composable
fun EditLevelDialog(
    level: com.ecolix.atschool.api.SchoolLevelDto,
    onDismiss: () -> Unit,
    onConfirm: (String, Int) -> Unit,
    colors: DashboardColors
) {
    var name by remember { mutableStateOf(level.name) }
    var order by remember { mutableStateOf(level.sortOrder.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Modifier le Niveau Scolaire") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nom") },
                    modifier = Modifier.fillMaxWidth()
                )
                TextField(
                    value = order,
                    onValueChange = { order = it },
                    label = { Text("Ordre d'affichage") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { if (name.isNotBlank()) onConfirm(name, order.toIntOrNull() ?: level.sortOrder) },
                enabled = name.isNotBlank()
            ) { Text("Enregistrer") }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) { Text("Annuler") }
        }
    )
}
