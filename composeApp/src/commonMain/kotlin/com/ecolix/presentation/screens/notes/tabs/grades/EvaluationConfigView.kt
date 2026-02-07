package com.ecolix.presentation.screens.notes.tabs.grades

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ecolix.data.models.EvaluationTemplate
import com.ecolix.data.models.EvaluationType
import com.ecolix.data.models.GradesUiState
import com.ecolix.presentation.components.CardContainer
import com.ecolix.presentation.components.ConfirmationDialog
import com.ecolix.presentation.components.SearchableDropdownMenu

@Composable
fun EvaluationConfigView(
    state: GradesUiState,
    onBack: () -> Unit,
    onTemplatesUpdated: (List<EvaluationTemplate>) -> Unit
) {
    var selectedClass by remember { mutableStateOf(state.classrooms.first { it != "Toutes les classes" }) }
    var showDialog by remember { mutableStateOf(false) }
    var editingTemplate by remember { mutableStateOf<EvaluationTemplate?>(null) }
    var templateToDelete by remember { mutableStateOf<EvaluationTemplate?>(null) }

    val filteredTemplates = state.templates.filter { it.className == selectedClass }

    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(24.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = state.colors.textPrimary)
            }
            Text("Modèles: $selectedClass", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold), color = state.colors.textPrimary)
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = { 
                    editingTemplate = null
                    showDialog = true 
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                )
            ) {
                Icon(Icons.Default.Add, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Nouveau Modèle")
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Sélectionner une classe:", color = state.colors.textMuted, style = MaterialTheme.typography.bodySmall)
            var showClassMenu by remember { mutableStateOf(false) }
            Box {
                OutlinedButton(
                    onClick = { showClassMenu = true },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(36.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                ) {
                    Text(selectedClass, color = state.colors.textPrimary, style = MaterialTheme.typography.bodySmall)
                    Icon(Icons.Default.ArrowDropDown, null, tint = state.colors.textMuted)
                }
                DropdownMenu(
                    expanded = showClassMenu, 
                    onDismissRequest = { showClassMenu = false },
                    modifier = Modifier.background(state.colors.card)
                ) {
                    state.classrooms.filter { it != "Toutes les classes" }.forEach { cls ->
                        DropdownMenuItem(
                            text = { Text(cls, color = state.colors.textPrimary) },
                            onClick = { 
                                selectedClass = cls
                                showClassMenu = false 
                            },
                            colors = MenuDefaults.itemColors(
                                textColor = state.colors.textPrimary,
                                leadingIconColor = state.colors.textMuted
                            )
                        )
                    }
                }
            }
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            items(filteredTemplates) { template ->
                CardContainer(containerColor = state.colors.card) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Book, null, tint = MaterialTheme.colorScheme.primary)
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(template.label, fontWeight = FontWeight.Bold, color = state.colors.textPrimary)
                                Text(template.type.label, style = MaterialTheme.typography.bodySmall, color = state.colors.textMuted)
                            }
                        }
                        
                        Row(horizontalArrangement = Arrangement.spacedBy(24.dp), verticalAlignment = Alignment.CenterVertically) {

                            
                            Row {
                                IconButton(onClick = { 
                                    editingTemplate = template
                                    showDialog = true 
                                }) {
                                    Icon(Icons.Default.Edit, null, tint = state.colors.textMuted)
                                }
                                IconButton(onClick = { 
                                    templateToDelete = template
                                }) {
                                    Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f))
                                }
                            }
                        }
                    }
                }
            }
            if (filteredTemplates.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Info, null, modifier = Modifier.size(48.dp), tint = state.colors.textMuted.copy(alpha = 0.3f))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Aucun modèle défini pour cette classe.", color = state.colors.textMuted)
                    }
                }
            }
        }
    }

    if (showDialog) {
        EvaluationTemplateDialog(
            template = editingTemplate,
            className = selectedClass,
            colors = state.colors,
            onDismiss = { showDialog = false },
            onSave = { updated ->
                val newList = if (editingTemplate == null) {
                    state.templates + updated
                } else {
                    state.templates.map { if (it.id == updated.id) updated else it }
                }
                onTemplatesUpdated(newList)
                showDialog = false
            }
        )
    }

    if (templateToDelete != null) {
        ConfirmationDialog(
            title = "Supprimer le modèle",
            message = "Voulez-vous vraiment supprimer le modèle '${templateToDelete?.label}' ?",
            colors = state.colors,
            onConfirm = {
                onTemplatesUpdated(state.templates.filter { it.id != templateToDelete?.id })
                templateToDelete = null
            },
            onDismiss = { templateToDelete = null }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EvaluationTemplateDialog(
    template: EvaluationTemplate?,
    className: String,
    colors: com.ecolix.data.models.DashboardColors,
    onDismiss: () -> Unit,
    onSave: (EvaluationTemplate) -> Unit
) {
    var label by remember { mutableStateOf(template?.label ?: "") }
    var type by remember { mutableStateOf(template?.type ?: EvaluationType.DEVOIR) }
    var maxValue by remember { mutableStateOf(template?.maxValue?.toInt()?.toString() ?: "20") }
    var coefficient by remember { mutableStateOf(template?.coefficient?.toInt()?.toString() ?: "1") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = colors.card,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    if (template == null) "Nouveau Modèle" else "Modifier Modèle",
                    color = colors.textPrimary,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, null, tint = colors.textMuted)
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = label,
                    onValueChange = { label = it },
                    label = { Text("Titre (ex: Devoir)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { 
                        if (label.isNotBlank()) {
                            onSave(
                                EvaluationTemplate(
                                    id = template?.id ?: "TMP_${kotlin.random.Random.nextInt(10000)}",
                                    className = className,
                                    type = type,
                                    label = label,
                                    maxValue = maxValue.toFloatOrNull() ?: 20f,
                                    coefficient = coefficient.toFloatOrNull() ?: 1f
                                )
                            )
                        }
                    }),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = colors.textPrimary,
                        unfocusedTextColor = colors.textPrimary,
                        unfocusedBorderColor = colors.divider
                    )
                )

                Column {
                    Text("Type d'évaluation", style = MaterialTheme.typography.labelSmall, color = colors.textMuted)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        EvaluationType.values().forEach { evType ->
                            val selected = type == evType
                            Surface(
                                onClick = { type = evType },
                                shape = RoundedCornerShape(8.dp),
                                color = if (selected) MaterialTheme.colorScheme.primary else colors.background,
                                border = if (selected) null else androidx.compose.foundation.BorderStroke(1.dp, colors.divider),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    evType.label,
                                    modifier = Modifier.padding(vertical = 12.dp),
                                    textAlign = TextAlign.Center,
                                    color = if (selected) Color.White else colors.textPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Barème et Coefficient sont fixés par défaut (20 et 1)
                // car gérés au niveau global ou matière

            }
        },
        confirmButton = {
            Button(
                onClick = { 
                    onSave(
                        EvaluationTemplate(
                            id = template?.id ?: "TMP_${kotlin.random.Random.nextInt(10000)}",
                            className = className,
                            type = type,
                            label = label.ifBlank { type.label },
                            maxValue = maxValue.toFloatOrNull() ?: 20f,
                            coefficient = coefficient.toFloatOrNull() ?: 1f
                        )
                    )
                },
                enabled = label.isNotBlank() || true,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = Color.White)
            ) {
                Text("Enregistrer")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler", color = colors.textMuted)
            }
        }
    )
}
