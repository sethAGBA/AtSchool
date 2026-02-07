package com.ecolix.presentation.screens.subjects

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.ecolix.data.models.DashboardColors
import com.ecolix.atschool.models.Staff
import com.ecolix.data.models.Subject
import com.ecolix.presentation.components.CardContainer

@Composable
fun ProfessorAssignmentDialog(
    subject: Subject,
    currentProfessorId: String?,
    allTeachers: List<Staff>,
    colors: DashboardColors,
    onDismiss: () -> Unit,
    onSave: (String?) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedId by remember { mutableStateOf(currentProfessorId) }

    val filteredTeachers = remember(searchQuery, allTeachers) {
        allTeachers.filter {
            searchQuery.isEmpty() ||
            it.firstName.contains(searchQuery, ignoreCase = true) ||
            it.lastName.contains(searchQuery, ignoreCase = true)
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        CardContainer(containerColor = colors.card) {
            Column(
                modifier = Modifier.fillMaxWidth().heightIn(max = 600.dp),
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
                            text = "Affecter Professeurs",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = colors.textPrimary
                        )
                        Text(
                            text = "Pour: ${subject.name}",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.textMuted
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Fermer", tint = colors.textMuted)
                    }
                }

                HorizontalDivider(color = colors.divider)

                // Search
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Rechercher un enseignant...", color = colors.textMuted) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = colors.textMuted) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = colors.textPrimary,
                        unfocusedTextColor = colors.textPrimary,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = colors.divider,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = colors.textMuted
                    )
                )

                // List
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredTeachers) { teacher ->
                        val isSelected = selectedId == teacher.id
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    selectedId = if (isSelected) null else teacher.id
                                }
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = {
                                    selectedId = if (isSelected) null else teacher.id
                                },
                                colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier.size(32.dp).clip(CircleShape).background(colors.background),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Person, null, tint = colors.textMuted, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    "${teacher.firstName} ${teacher.lastName}",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = colors.textPrimary
                                )
                                Text(
                                    teacher.specialty ?: "Enseignant",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = colors.textMuted
                                )
                            }
                        }
                    }
                }
                
                Button(
                    onClick = { onSave(selectedId) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Confirmer l'affectation")
                }
            }
        }
    }
}

@Composable
fun SubjectConfigDialog(
    subject: Subject,
    initialCoefficient: Float?,
    initialWeeklyHours: Int?,
    colors: DashboardColors,
    onDismiss: () -> Unit,
    onSave: (Float, Int) -> Unit
) {
    var coefficient by remember { mutableStateOf((initialCoefficient ?: subject.defaultCoefficient).toString()) }
    var weeklyHours by remember { mutableStateOf((initialWeeklyHours ?: subject.weeklyHours).toString()) }

    Dialog(onDismissRequest = onDismiss) {
        CardContainer(containerColor = colors.card) {
            Column(
                modifier = Modifier.fillMaxWidth(),
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
                            text = "Configuration & Volumes",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = colors.textPrimary
                        )
                        Text(
                            text = "Matière: ${subject.name}",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.textMuted
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Fermer", tint = colors.textMuted)
                    }
                }

                HorizontalDivider(color = colors.divider)

                OutlinedTextField(
                    value = coefficient,
                    onValueChange = { if (it.all { c -> c.isDigit() || c == '.' }) coefficient = it },
                    label = { Text("Coefficient par défaut") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = colors.textPrimary,
                        unfocusedTextColor = colors.textPrimary,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = colors.divider,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = colors.textMuted
                    )
                )

                OutlinedTextField(
                    value = weeklyHours,
                    onValueChange = { if (it.all { c -> c.isDigit() }) weeklyHours = it },
                    label = { Text("Volume horaire hebdomadaire (Heures)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = colors.textPrimary,
                        unfocusedTextColor = colors.textPrimary,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = colors.divider,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = colors.textMuted
                    )
                )

                Button(
                    onClick = { 
                        onSave(
                            coefficient.toFloatOrNull() ?: 1f,
                            weeklyHours.toIntOrNull() ?: 2
                        ) 
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Mettre à jour")
                }
            }
        }
    }
}
