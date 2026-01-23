package com.ecolix.atschool.ui.students.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecolix.atschool.ui.dashboard.models.DashboardColors
import com.ecolix.atschool.ui.students.models.Classroom

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassForm(
    classroom: Classroom? = null,
    colors: DashboardColors,
    onBack: () -> Unit,
    onSave: (Classroom) -> Unit
) {
    var name by remember { mutableStateOf(classroom?.name ?: "") }
    var level by remember { mutableStateOf(classroom?.level ?: "Primaire") }
    var roomNumber by remember { mutableStateOf(classroom?.roomNumber ?: "") }
    var capacity by remember { mutableStateOf(classroom?.capacity?.toString() ?: "") }
    var mainTeacher by remember { mutableStateOf(classroom?.mainTeacher ?: "") }
    var description by remember { mutableStateOf(classroom?.description ?: "") }
    var academicYear by remember { mutableStateOf(classroom?.academicYear ?: "2024-2025") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = colors.textPrimary)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (classroom == null) "Nouvelle Classe" else "Modifier: ${classroom.name}",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = colors.textPrimary
            )
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(28.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        "INFORMATIONS GÉNÉRALES",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Black, letterSpacing = 1.2.sp),
                        color = MaterialTheme.colorScheme.primary
                    )
                    HorizontalDivider(color = colors.divider.copy(alpha = 0.5f))
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Nom de la classe *") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = level,
                            onValueChange = { level = it },
                            label = { Text("Niveau *") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        OutlinedTextField(
                            value = roomNumber,
                            onValueChange = { roomNumber = it },
                            label = { Text("Numéro de salle") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = capacity,
                            onValueChange = { capacity = it },
                            label = { Text("Capacité max") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        "RESPONSABILITÉ & ANNÉE",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Black, letterSpacing = 1.2.sp),
                        color = MaterialTheme.colorScheme.primary
                    )
                    HorizontalDivider(color = colors.divider.copy(alpha = 0.5f))

                    OutlinedTextField(
                        value = mainTeacher,
                        onValueChange = { mainTeacher = it },
                        label = { Text("Professeur Principal") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = academicYear,
                        onValueChange = { academicYear = it },
                        label = { Text("Année Scolaire") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        readOnly = true
                    )
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        "DESCRIPTION",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Black, letterSpacing = 1.2.sp),
                        color = MaterialTheme.colorScheme.primary
                    )
                    HorizontalDivider(color = colors.divider.copy(alpha = 0.5f))

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description / Observations") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        minLines = 3
                    )
                }
            }
        }

        Button(
            onClick = {
                if (name.isNotBlank()) {
                    onSave(
                        Classroom(
                            id = classroom?.id ?: "CLASS_${name.uppercase()}",
                            name = name,
                            studentCount = classroom?.studentCount ?: 0,
                            boysCount = classroom?.boysCount ?: 0,
                            girlsCount = classroom?.girlsCount ?: 0,
                            level = level,
                            academicYear = academicYear,
                            mainTeacher = if (mainTeacher.isNotBlank()) mainTeacher else null,
                            roomNumber = if (roomNumber.isNotBlank()) roomNumber else null,
                            capacity = capacity.toIntOrNull(),
                            description = if (description.isNotBlank()) description else null
                        )
                    )
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colors.textLink,
                contentColor = Color.White
            )
        ) {
            Icon(Icons.Default.Save, contentDescription = null)
            Spacer(modifier = Modifier.width(12.dp))
            Text("Enregistrer la Classe", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}
