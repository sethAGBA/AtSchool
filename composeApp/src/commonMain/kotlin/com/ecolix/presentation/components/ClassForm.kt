package com.ecolix.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.input.ImeAction
import com.ecolix.data.models.Classroom
import com.ecolix.data.models.DashboardColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassForm(
    classroom: Classroom? = null,
    levels: List<com.ecolix.atschool.api.SchoolLevelDto> = emptyList(),
    cycles: List<com.ecolix.atschool.api.SchoolCycleDto> = emptyList(),
    colors: DashboardColors,
    staffMembers: List<com.ecolix.atschool.models.Staff> = emptyList(),
    isCompact: Boolean = false,
    currentAcademicYear: String,
    onBack: () -> Unit,
    onSave: (Classroom) -> Unit
) {
    var name by remember { mutableStateOf(classroom?.name ?: "") }
    var levelName by remember { mutableStateOf(classroom?.level ?: "") }
    var schoolLevelId by remember { mutableStateOf(classroom?.schoolLevelId) }
    var roomNumber by remember { mutableStateOf(classroom?.roomNumber ?: "") }
    var capacity by remember { mutableStateOf(classroom?.capacity?.toString() ?: "") }
    var mainTeacher by remember { mutableStateOf(classroom?.mainTeacher ?: "") }
    var description by remember { mutableStateOf(classroom?.description ?: "") }
    var showTeacherDropdown by remember { mutableStateOf(false) }
    var academicYear by remember { mutableStateOf(classroom?.academicYear ?: currentAcademicYear) }

    // Set default level if editing/creating and levels are available
    LaunchedEffect(levels) {
        if (schoolLevelId == null && levels.isNotEmpty()) {
            val defaultLevel = levels.first()
            schoolLevelId = defaultLevel.id
            levelName = defaultLevel.name
            if (name.isEmpty()) name = defaultLevel.name
        }
    }

    val handleSave = {
        if (name.isNotBlank()) {
            onSave(
                Classroom(
                    id = classroom?.id ?: "CLASS_${name.uppercase()}",
                    name = name,
                    studentCount = classroom?.studentCount ?: 0,
                    boysCount = classroom?.boysCount ?: 0,
                    girlsCount = classroom?.girlsCount ?: 0,
                    level = levelName,
                    schoolLevelId = schoolLevelId,
                    academicYear = academicYear,
                    mainTeacher = if (mainTeacher.isNotBlank()) mainTeacher else null,
                    roomNumber = if (roomNumber.isNotBlank()) roomNumber else null,
                    capacity = capacity.toIntOrNull(),
                    description = if (description.isNotBlank()) description else null,
                    students = classroom?.students ?: emptyList()
                )
            )
        }
    }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = colors.textPrimary,
        unfocusedTextColor = colors.textPrimary,
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = colors.divider.copy(alpha = if (colors.textPrimary == Color(0xFF1E293B)) 0.8f else 0.5f),
        focusedLabelColor = MaterialTheme.colorScheme.primary,
        unfocusedLabelColor = colors.textMuted,
        focusedPlaceholderColor = colors.textMuted,
        unfocusedPlaceholderColor = colors.textMuted.copy(alpha = 0.7f)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(if (isCompact) 16.dp else 24.dp),
        verticalArrangement = Arrangement.spacedBy(if (isCompact) 16.dp else 24.dp)
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
                    
                    if (isCompact) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Nom de la classe *") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = fieldColors,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                        )
                        
                        if (levels.isNotEmpty()) {
                            LevelSelection(
                                selectedLevelId = schoolLevelId,
                                onLevelSelected = { l -> 
                                    schoolLevelId = l.id
                                    levelName = l.name
                                    name = l.name
                                },
                                levels = levels,
                                cycles = cycles,
                                colors = colors
                            )
                        } else {
                            OutlinedTextField(
                                value = levelName,
                                onValueChange = { levelName = it },
                                label = { Text("Niveau *") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = fieldColors,
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                            )
                        }
                    } else {
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            OutlinedTextField(
                                value = name,
                                onValueChange = { name = it },
                                label = { Text("Nom de la classe *") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = fieldColors,
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                            )
                            
                            if (levels.isNotEmpty()) {
                                Box(modifier = Modifier.weight(1f)) {
                                    LevelSelection(
                                        selectedLevelId = schoolLevelId,
                                        onLevelSelected = { l -> 
                                            schoolLevelId = l.id
                                            levelName = l.name
                                            name = l.name
                                        },
                                        levels = levels,
                                        cycles = cycles,
                                        colors = colors
                                    )
                                }
                            } else {
                                OutlinedTextField(
                                    value = levelName,
                                    onValueChange = { levelName = it },
                                    label = { Text("Niveau *") },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = fieldColors,
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                                )
                            }
                        }
                    }

                    if (isCompact) {
                        OutlinedTextField(
                            value = roomNumber,
                            onValueChange = { roomNumber = it },
                            label = { Text("Numéro de salle") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = fieldColors,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                        )
                        OutlinedTextField(
                            value = capacity,
                            onValueChange = { capacity = it },
                            label = { Text("Capacité max") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = fieldColors,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                        )
                    } else {
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            OutlinedTextField(
                                value = roomNumber,
                                onValueChange = { roomNumber = it },
                                label = { Text("Numéro de salle") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = fieldColors,
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                            )
                            OutlinedTextField(
                                value = capacity,
                                onValueChange = { capacity = it },
                                label = { Text("Capacité max") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = fieldColors,
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                            )
                        }
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

                    val teachers = remember(staffMembers) {
                        staffMembers.filter { it.role == com.ecolix.atschool.models.StaffRole.TEACHER }
                    }

                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = mainTeacher,
                            onValueChange = { mainTeacher = it },
                            label = { Text("Professeur Principal") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            readOnly = teachers.isNotEmpty(),
                            trailingIcon = {
                                if (teachers.isNotEmpty()) {
                                    IconButton(onClick = { showTeacherDropdown = !showTeacherDropdown }) {
                                        Icon(
                                            if (showTeacherDropdown) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                            contentDescription = null,
                                            tint = colors.textPrimary
                                        )
                                    }
                                }
                            },
                            colors = fieldColors,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                        )

                        DropdownMenu(
                            expanded = showTeacherDropdown,
                            onDismissRequest = { showTeacherDropdown = false },
                            modifier = Modifier.fillMaxWidth(if (isCompact) 0.9f else 0.5f).background(colors.card)
                        ) {
                            teachers.forEach { staff ->
                                DropdownMenuItem(
                                    text = { Text("${staff.firstName} ${staff.lastName}", color = colors.textPrimary) },
                                    onClick = {
                                        mainTeacher = "${staff.firstName} ${staff.lastName}"
                                        showTeacherDropdown = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = academicYear,
                        onValueChange = {},
                        label = { Text("Année Scolaire") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        readOnly = true,
                        colors = fieldColors
                    )

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description / Observations") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        minLines = 3,
                        colors = fieldColors,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { handleSave() })
                    )
                }
            }
        }

        Button(
            onClick = handleSave,
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun LevelSelection(
    selectedLevelId: Int?,
    onLevelSelected: (com.ecolix.atschool.api.SchoolLevelDto) -> Unit,
    levels: List<com.ecolix.atschool.api.SchoolLevelDto>,
    cycles: List<com.ecolix.atschool.api.SchoolCycleDto>,
    colors: DashboardColors
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Niveau Scolaire *",
            style = MaterialTheme.typography.labelMedium,
            color = if (selectedLevelId == null) colors.textMuted else MaterialTheme.colorScheme.primary
        )
        val levelsByCycle = remember(levels) { levels.groupBy { it.cycleId } }
        val sortedCycles = remember(cycles) { cycles.sortedBy { it.sortOrder } }

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            sortedCycles.forEach { cycle ->
                val cycleLevels = levelsByCycle[cycle.id] ?: emptyList()
                if (cycleLevels.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = cycle.name,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = colors.textMuted
                        )
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            cycleLevels.sortedBy { it.sortOrder }.forEach { l ->
                                FilterChip(
                                    selected = selectedLevelId == l.id,
                                    onClick = { onLevelSelected(l) },
                                    label = { Text(l.name) },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                        selectedLabelColor = MaterialTheme.colorScheme.primary
                                    )
                                )
                            }
                        }
                    }
                }
            }
            
            // Show levels without cycles if any
            val levelsWithoutCycle = levels.filter { it.cycleId == 0 || !cycles.any { c -> c.id == it.cycleId } }
            if (levelsWithoutCycle.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Autres",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = colors.textMuted
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        levelsWithoutCycle.forEach { l ->
                            FilterChip(
                                selected = selectedLevelId == l.id,
                                onClick = { onLevelSelected(l) },
                                label = { Text(l.name) },
                                shape = RoundedCornerShape(8.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                    selectedLabelColor = MaterialTheme.colorScheme.primary
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
