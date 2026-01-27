package com.ecolix.presentation.screens.timetable

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.unit.sp
import com.ecolix.data.models.*

@Composable
fun TimetableScreenContent(isDarkMode: Boolean) {
    val screenModel = remember { TimetableScreenModel() }
    val state by screenModel.state.collectAsState()

    LaunchedEffect(isDarkMode) {
        screenModel.onDarkModeChange(isDarkMode)
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isCompact = maxWidth < 800.dp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(if (isCompact) 16.dp else 24.dp),
            verticalArrangement = Arrangement.spacedBy(if (isCompact) 16.dp else 24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Emplois du Temps",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = if (isCompact) 24.sp else 32.sp
                        ),
                        color = state.colors.textPrimary
                    )
                    Text(
                        text = "Planification et gestion des cours par classe et enseignant",
                        style = MaterialTheme.typography.bodyMedium,
                        color = state.colors.textMuted
                    )
                }

                if (!isCompact) {
                    TimetableViewToggle(
                        currentMode = state.viewMode,
                        onModeChange = { screenModel.onViewModeChange(it) },
                        colors = state.colors
                    )
                }
            }

            // Filters
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                when (state.viewMode) {
                    TimetableViewMode.BY_CLASS, TimetableViewMode.OVERVIEW -> {
                        ClassSelector(
                            selectedId = state.selectedClassroomId,
                            classrooms = state.classrooms,
                            onSelect = { screenModel.onClassroomChange(it) },
                            colors = state.colors
                        )
                    }
                    TimetableViewMode.BY_TEACHER -> {
                        TeacherSelector(
                            selectedId = state.selectedTeacherId,
                            teachers = state.teachers,
                            onSelect = { screenModel.onTeacherChange(it) },
                            colors = state.colors
                        )
                    }
                    else -> {}
                }

                Spacer(modifier = Modifier.weight(1f))
                
                Button(
                    onClick = { /* Add session */ },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    if (!isCompact) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Ajouter un cours")
                    }
                }
            }

            // Grid
            val filteredSessions = remember(state.sessions, state.selectedClassroomId, state.selectedTeacherId, state.viewMode) {
                when (state.viewMode) {
                    TimetableViewMode.BY_CLASS -> state.sessions.filter { it.classroomId == state.selectedClassroomId }
                    TimetableViewMode.BY_TEACHER -> state.sessions.filter { it.teacherId == state.selectedTeacherId }
                    else -> state.sessions // Overview: show all or filter by selected class by default
                }
            }

            Card(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = state.colors.card),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                TimetableGrid(
                    state = state,
                    sessions = filteredSessions,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Composable
private fun TimetableViewToggle(
    currentMode: TimetableViewMode,
    onModeChange: (TimetableViewMode) -> Unit,
    colors: DashboardColors
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(colors.card)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        val tabs = listOf(
            Triple(TimetableViewMode.BY_CLASS, "Par Classe", Icons.Default.Groups),
            Triple(TimetableViewMode.BY_TEACHER, "Par Enseignant", Icons.Default.Person),
            Triple(TimetableViewMode.BY_ROOM, "Par Salle", Icons.Default.MeetingRoom)
        )

        tabs.forEach { (mode, label, icon) ->
            val isSelected = currentMode == mode
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                    .clickable { onModeChange(mode) }
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = if (isSelected) Color.White else colors.textMuted
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = label,
                    color = if (isSelected) Color.White else colors.textMuted,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                )
            }
        }
    }
}

@Composable
private fun ClassSelector(
    selectedId: String?,
    classrooms: List<Classroom>,
    onSelect: (String) -> Unit,
    colors: DashboardColors
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedClass = classrooms.find { it.id == selectedId }

    Box {
        OutlinedButton(
            onClick = { expanded = true },
            colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.textPrimary)
        ) {
            Icon(Icons.Default.School, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(selectedClass?.name ?: "Sélectionner une classe")
            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            classrooms.forEach { classroom ->
                DropdownMenuItem(
                    text = { Text(classroom.name) },
                    onClick = {
                        onSelect(classroom.id)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun TeacherSelector(
    selectedId: String?,
    teachers: List<Teacher>,
    onSelect: (String) -> Unit,
    colors: DashboardColors
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedTeacher = teachers.find { it.id == selectedId }

    Box {
        OutlinedButton(
            onClick = { expanded = true },
            colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.textPrimary)
        ) {
            Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(selectedTeacher?.let { "${it.firstName} ${it.lastName}" } ?: "Sélectionner un enseignant")
            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            teachers.forEach { teacher ->
                DropdownMenuItem(
                    text = { Text("${teacher.firstName} ${teacher.lastName}") },
                    onClick = {
                        onSelect(teacher.id)
                        expanded = false
                    }
                )
            }
        }
    }
}
