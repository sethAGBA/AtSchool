package com.ecolix.presentation.screens.academic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.ecolix.data.models.*

@Composable
fun AcademicSettingsTab(
    state: AcademicUiState,
    colors: DashboardColors,
    isCompact: Boolean,
    onUpdateSettings: (AcademicSettings) -> Unit
) {
    var editedSettings by remember(state.settings) { mutableStateOf(state.settings) }
    val hasChanges by remember { 
        derivedStateOf { state.settings != editedSettings }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (hasChanges) {
                Button(
                    onClick = { onUpdateSettings(editedSettings) },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Enregistrer les modifications")
                }
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Notation Section
            item {
                SettingsSection(
                    title = "Système de Notation",
                    icon = Icons.Default.Grade,
                    colors = colors
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            SettingNumberField(
                                label = "Note Minimale",
                                value = editedSettings.gradeScale.minGrade,
                                onValueChange = { 
                                    editedSettings = editedSettings.copy(
                                        gradeScale = editedSettings.gradeScale.copy(minGrade = it)
                                    )
                                },
                                modifier = Modifier.weight(1f),
                                colors = colors
                            )
                            SettingNumberField(
                                label = "Note Maximale",
                                value = editedSettings.gradeScale.maxGrade,
                                onValueChange = { 
                                    editedSettings = editedSettings.copy(
                                        gradeScale = editedSettings.gradeScale.copy(maxGrade = it)
                                    )
                                },
                                modifier = Modifier.weight(1f),
                                colors = colors
                            )
                        }

                        SettingNumberField(
                            label = "Moyenne de Passage",
                            value = editedSettings.passingGrade,
                            onValueChange = { 
                                editedSettings = editedSettings.copy(passingGrade = it)
                            },
                            colors = colors
                        )

                        SettingNumberField(
                            label = "Précision Décimale",
                            value = editedSettings.decimalPrecision.toFloat(),
                            onValueChange = { 
                                editedSettings = editedSettings.copy(decimalPrecision = it.toInt())
                            },
                            colors = colors
                        )

                        OutlinedTextField(
                            value = editedSettings.matriculePrefix ?: "",
                            onValueChange = { editedSettings = editedSettings.copy(matriculePrefix = it) },
                            label = { Text("Préfixe des Matricules") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = colors.textPrimary,
                                unfocusedTextColor = colors.textPrimary,
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = colors.divider
                            )
                        )
                    }
                }
            }

            // Attendance Section
            item {
                SettingsSection(
                    title = "Présences et Alertes",
                    icon = Icons.Default.AccessTime,
                    colors = colors
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        SettingNumberField(
                            label = "Seuil de présence requise (%)",
                            value = editedSettings.attendanceRequired,
                            onValueChange = { 
                                editedSettings = editedSettings.copy(attendanceRequired = it)
                            },
                            colors = colors
                        )

                        SettingNumberField(
                            label = "Seuil d'alerte d'absences (jours)",
                            value = editedSettings.absencesThresholdAlert.toFloat(),
                            onValueChange = { 
                                editedSettings = editedSettings.copy(absencesThresholdAlert = it.toInt())
                            },
                            colors = colors
                        )
                    }
                }
            }

            // Performance Levels (Grade Levels)
            item {
                SettingsSection(
                    title = "Niveaux de Performance (Appréciations)",
                    icon = Icons.Default.BarChart,
                    colors = colors
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        var levelToDelete by remember { mutableStateOf<Int?>(null) }
                        
                        // Confirmation dialog
                        levelToDelete?.let { index ->
                            val levelName = editedSettings.gradeScale.gradeLevels.getOrNull(index)?.name ?: ""
                            AlertDialog(
                                onDismissRequest = { levelToDelete = null },
                                containerColor = colors.card,
                                icon = { Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                                title = {
                                    Text(
                                        "Supprimer le niveau",
                                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                        color = colors.textPrimary
                                    )
                                },
                                text = {
                                    Text(
                                        "Voulez-vous vraiment supprimer le niveau \"$levelName\" ?",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = colors.textPrimary
                                    )
                                },
                                confirmButton = {
                                    Button(
                                        onClick = {
                                            val newList = editedSettings.gradeScale.gradeLevels.toMutableList()
                                            newList.removeAt(index)
                                            editedSettings = editedSettings.copy(
                                                gradeScale = editedSettings.gradeScale.copy(gradeLevels = newList)
                                            )
                                            levelToDelete = null
                                        },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                                    ) {
                                        Text("Supprimer")
                                    }
                                },
                                dismissButton = {
                                    TextButton(onClick = { levelToDelete = null }) {
                                        Text("Annuler", color = colors.textMuted)
                                    }
                                }
                            )
                        }

                        editedSettings.gradeScale.gradeLevels.forEachIndexed { index, level ->
                            GradeLevelEditItem(
                                level = level,
                                onUpdate = { updated ->
                                    val newList = editedSettings.gradeScale.gradeLevels.toMutableList()
                                    newList[index] = updated
                                    editedSettings = editedSettings.copy(
                                        gradeScale = editedSettings.gradeScale.copy(gradeLevels = newList)
                                    )
                                },
                                onDelete = { levelToDelete = index },
                                colors = colors
                            )
                        }
                        
                        Button(
                            onClick = {
                                val newList = editedSettings.gradeScale.gradeLevels.plus(
                                    GradeLevel("Nouveau Niveau", 0f, 0f, "", Color.Gray)
                                )
                                editedSettings = editedSettings.copy(
                                    gradeScale = editedSettings.gradeScale.copy(gradeLevels = newList)
                                )
                            },
                            variant = "text",
                            colors = colors
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Text("Ajouter un niveau")
                        }
                    }
                }
            }

            // Display Options
            item {
                SettingsSection(
                    title = "Affichage et Bulletins",
                    icon = Icons.Default.Description,
                    colors = colors
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        SwitchSettingItem(
                            title = "Afficher le rang sur le bulletin",
                            description = "Calculer et afficher la position de l'élève par rapport à la classe",
                            checked = editedSettings.showRankOnReportCard,
                            onCheckedChange = { editedSettings = editedSettings.copy(showRankOnReportCard = it) },
                            colors = colors
                        )

                        SwitchSettingItem(
                            title = "Afficher la moyenne de classe",
                            description = "Afficher la moyenne générale de la classe pour comparaison",
                            checked = editedSettings.showClassAverageOnReportCard,
                            onCheckedChange = { editedSettings = editedSettings.copy(showClassAverageOnReportCard = it) },
                            colors = colors
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    colors: DashboardColors,
    content: @Composable () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = colors.card),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = colors.textPrimary
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            content()
        }
    }
}

@Composable
private fun SettingNumberField(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    colors: DashboardColors,
    modifier: Modifier = Modifier
) {
    var textValue by remember(value) { mutableStateOf(value.toString()) }

    OutlinedTextField(
        value = textValue,
        onValueChange = { 
            textValue = it
            it.toFloatOrNull()?.let { f -> onValueChange(f) }
        },
        label = { Text(label) },
        modifier = modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = colors.textPrimary,
            unfocusedTextColor = colors.textPrimary,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = colors.divider
        )
    )
}

@Composable
private fun GradeLevelEditItem(
    level: GradeLevel,
    onUpdate: (GradeLevel) -> Unit,
    onDelete: () -> Unit,
    colors: DashboardColors
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(level.color.copy(alpha = 0.05f))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(level.color)
            )

            OutlinedTextField(
                value = level.name,
                onValueChange = { onUpdate(level.copy(name = it)) },
                label = { Text("Nom") },
                modifier = Modifier.weight(1.5f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = colors.textPrimary,
                    unfocusedTextColor = colors.textPrimary,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = colors.divider
                )
            )

            OutlinedTextField(
                value = level.minValue.toString(),
                onValueChange = { it.toFloatOrNull()?.let { f -> onUpdate(level.copy(minValue = f)) } },
                label = { Text("Min") },
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = colors.textPrimary,
                    unfocusedTextColor = colors.textPrimary,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = colors.divider
                )
            )

            OutlinedTextField(
                value = level.maxValue.toString(),
                onValueChange = { it.toFloatOrNull()?.let { f -> onUpdate(level.copy(maxValue = f)) } },
                label = { Text("Max") },
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = colors.textPrimary,
                    unfocusedTextColor = colors.textPrimary,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = colors.divider
                )
            )
            
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Supprimer",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        
        OutlinedTextField(
            value = level.description,
            onValueChange = { onUpdate(level.copy(description = it)) },
            label = { Text("Appréciation") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = colors.textPrimary,
                unfocusedTextColor = colors.textPrimary,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = colors.divider
            )
        )
    }
}

@Composable
private fun SwitchSettingItem(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    colors: DashboardColors
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = colors.textPrimary
            )
            Text(
                description,
                style = MaterialTheme.typography.bodySmall,
                color = colors.textMuted
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}

@Composable
private fun Button(
    onClick: () -> Unit,
    variant: String = "filled",
    colors: DashboardColors,
    content: @Composable RowScope.() -> Unit
) {
    if (variant == "filled") {
        androidx.compose.material3.Button(onClick = onClick, content = content)
    } else {
        androidx.compose.material3.TextButton(onClick = onClick, content = content)
    }
}
