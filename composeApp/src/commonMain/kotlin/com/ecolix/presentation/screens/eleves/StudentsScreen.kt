package com.ecolix.presentation.screens.eleves

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecolix.data.models.StudentsUiState
import com.ecolix.data.models.StudentsViewMode
import com.ecolix.presentation.components.*

@Composable
fun StudentsScreenContent(isDarkMode: Boolean) {
    var state by remember(isDarkMode) { mutableStateOf(StudentsUiState.sample(isDarkMode)) }

    LaunchedEffect(isDarkMode) {
        state = state.copy(isDarkMode = isDarkMode)
    }
    
    val filteredStudents = remember(state.searchQuery, state.selectedClassroom, state.selectedLevel, state.selectedGender, state.visibilityFilter) {
        state.students.filter { student ->
            val matchClass = state.selectedClassroom == null || student.classroom == state.classrooms.find { it.id == state.selectedClassroom }?.name
            val matchLevel = state.selectedLevel == null || state.classrooms.any { it.name == student.classroom && it.level == state.selectedLevel }
            val matchGender = state.selectedGender == null || student.gender == state.selectedGender
            val matchVisibility = when (state.visibilityFilter) {
                "active" -> !student.isDeleted
                "deleted" -> student.isDeleted
                else -> true
            }
            val matchSearch = state.searchQuery.isEmpty() || 
                             "${student.firstName} ${student.lastName}".contains(state.searchQuery, ignoreCase = true) ||
                             student.matricule?.contains(state.searchQuery, ignoreCase = true) == true
            
            matchClass && matchLevel && matchGender && matchVisibility && matchSearch
        }
    }

    val filteredClasses = remember(state.searchQuery, state.selectedLevel) {
        state.classrooms.filter { classroom ->
            val matchLevel = state.selectedLevel == null || classroom.level == state.selectedLevel
            val matchSearch = state.searchQuery.isEmpty() || classroom.name.contains(state.searchQuery, ignoreCase = true)
            matchLevel && matchSearch
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isCompact = maxWidth < 800.dp
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(if (isCompact) 16.dp else 24.dp),
            verticalArrangement = Arrangement.spacedBy(if (isCompact) 16.dp else 24.dp)
        ) {
            if (!isCompact) {
                // Header Row (Desktop)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (state.viewMode == StudentsViewMode.CLASSES) "Groupes & Classes" else "Liste des Eleves",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 32.sp
                            ),
                            color = state.colors.textPrimary
                        )
                        Text(
                            text = "Gerez et suivez vos effectifs pour l'annee ${state.currentYear}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = state.colors.textMuted
                        )
                    }
                    
                    ViewToggle(
                        currentMode = state.viewMode,
                        onModeChange = { state = state.copy(viewMode = it, selectionMode = false, selectedStudentIds = emptySet()) },
                        colors = state.colors
                    )
                }

                ActionBar(
                    onAddStudentClick = { state = state.copy(viewMode = StudentsViewMode.STUDENT_FORM, selectedStudentId = null) },
                    onAddClassClick = { state = state.copy(viewMode = StudentsViewMode.CLASS_FORM, selectedClassroom = null) },
                    colors = state.colors,
                    isCompact = false
                )
                
                // Charts Section (Desktop)
                if (state.viewMode == StudentsViewMode.CLASSES && state.searchQuery.isEmpty()) {
                    DistributionChart(distribution = state.levelDistribution, colors = state.colors)
                }

                // Filters and Search (Desktop)
                if (state.viewMode != StudentsViewMode.STUDENT_FORM && state.viewMode != StudentsViewMode.PROFILE) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            SearchBar(
                                query = state.searchQuery,
                                onQueryChange = { state = state.copy(searchQuery = it) },
                                colors = state.colors,
                                modifier = Modifier.weight(1f)
                            )
                            if (state.viewMode == StudentsViewMode.STUDENTS) {
                                IconButton(
                                    onClick = { state = state.copy(selectionMode = !state.selectionMode, selectedStudentIds = emptySet()) },
                                    colors = IconButtonDefaults.iconButtonColors(
                                        containerColor = if (state.selectionMode) MaterialTheme.colorScheme.primary else state.colors.card,
                                        contentColor = if (state.selectionMode) Color.White else state.colors.textPrimary
                                    )
                                ) {
                                    Icon(Icons.Default.Checklist, contentDescription = null)
                                }
                            }
                        }

                        AdvancedFilters(
                            selectedLevel = state.selectedLevel,
                            onLevelChange = { state = state.copy(selectedLevel = it) },
                            selectedGender = state.selectedGender,
                            onGenderChange = { state = state.copy(selectedGender = it) },
                            visibility = state.visibilityFilter,
                            onVisibilityChange = { state = state.copy(visibilityFilter = it) },
                            colors = state.colors,
                            isCompact = false
                        )
                    }
                }
            }

            // Main Content View
            when (state.viewMode) {
                StudentsViewMode.CLASSES -> {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 300.dp),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        if (isCompact) {
                            item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(maxLineSpan) }) {
                                MobileStudentsHeader(
                                    state = state,
                                    isCompact = true,
                                    onStateChange = { state = it }
                                )
                            }
                        }

                        items(filteredClasses) { classroom ->
                            ClassCard(classroom, state.colors, onClick = {
                                state = state.copy(viewMode = StudentsViewMode.CLASS_DETAILS, selectedClassroom = classroom.id)
                            })
                        }
                    }
                }
                StudentsViewMode.STUDENTS -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(bottom = 80.dp) // Space for selection bar
                    ) {
                        if (isCompact) {
                            item {
                                MobileStudentsHeader(
                                    state = state,
                                    isCompact = true,
                                    onStateChange = { state = it }
                                )
                            }
                        }

                        item {
                            Text(
                                text = "Eleves trouves (${filteredStudents.size})",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = state.colors.textPrimary
                            )
                        }

                        items(filteredStudents) { student ->
                            StudentRow(
                                student = student,
                                colors = state.colors,
                                selectionMode = state.selectionMode,
                                isSelected = state.selectedStudentIds.contains(student.id),
                                onToggleSelect = {
                                    val newSelection = state.selectedStudentIds.toMutableSet()
                                    if (newSelection.contains(student.id)) newSelection.remove(student.id)
                                    else newSelection.add(student.id)
                                    state = state.copy(selectedStudentIds = newSelection)
                                },
                                onClick = { state = state.copy(viewMode = StudentsViewMode.PROFILE, selectedStudentId = student.id) }
                            )
                        }
                        
                        if (filteredStudents.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier.fillMaxWidth().padding(48.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = "Aucun eleve trouve.", color = state.colors.textMuted)
                                }
                            }
                        }
                    }
                }
                StudentsViewMode.PROFILE -> {
                    val student = state.students.find { it.id == state.selectedStudentId }
                    if (student != null) {
                        StudentProfileScreen(
                            student = student,
                            colors = state.colors,
                            isCompact = isCompact,
                            onBack = { state = state.copy(viewMode = StudentsViewMode.STUDENTS) }
                        )
                    }
                }
                StudentsViewMode.CLASS_DETAILS -> {
                    val classroom = state.classrooms.find { it.id == state.selectedClassroom }
                    if (classroom != null) {
                        ClassDetailsScreen(
                            classroom = classroom,
                            students = state.students.filter { it.classroom == classroom.name },
                            colors = state.colors,
                            isCompact = isCompact,
                            onBack = { state = state.copy(viewMode = StudentsViewMode.CLASSES) },
                            onStudentClick = { studentId ->
                                state = state.copy(viewMode = StudentsViewMode.PROFILE, selectedStudentId = studentId)
                            }
                        )
                    }
                }
                StudentsViewMode.STUDENT_FORM -> {
                    val student = state.students.find { it.id == state.selectedStudentId }
                    StudentForm(
                        student = student,
                        classrooms = state.classrooms.map { it.name },
                        currentAcademicYear = state.currentYear,
                        colors = state.colors,
                        isCompact = isCompact,
                        onBack = { state = state.copy(viewMode = StudentsViewMode.STUDENTS) },
                        onSave = { updatedStudent ->
                            // Update logic would go here
                            state = state.copy(viewMode = StudentsViewMode.STUDENTS)
                        }
                    )
                }
                StudentsViewMode.CLASS_FORM -> {
                    val classroom = state.classrooms.find { it.id == state.selectedClassroom }
                    ClassForm(
                        classroom = classroom,
                        colors = state.colors,
                        isCompact = isCompact,
                        onBack = { state = state.copy(viewMode = StudentsViewMode.CLASSES) },
                        onSave = { updatedClass ->
                            val newList = state.classrooms.toMutableList()
                            val index = newList.indexOfFirst { it.id == updatedClass.id }
                            if (index != -1) newList[index] = updatedClass
                            else newList.add(updatedClass)
                            state = state.copy(classrooms = newList, viewMode = StudentsViewMode.CLASSES)
                        }
                    )
                }
            }
        }

        // Floating Selection Bar
        AnimatedVisibility(
            visible = state.selectionMode && state.selectedStudentIds.isNotEmpty(),
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter).padding(24.dp)
        ) {
            SelectionActionBar(
                selectedCount = state.selectedStudentIds.size,
                onClearSelection = { state = state.copy(selectedStudentIds = emptySet()) },
                onDeleteSelected = { /* Logic for delete */ },
                colors = state.colors,
                isCompact = isCompact
            )
        }
    }
}

@Composable
private fun MobileStudentsHeader(
    state: StudentsUiState,
    isCompact: Boolean,
    onStateChange: (StudentsUiState) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Header Row
        Column {
            Text(
                text = if (state.viewMode == StudentsViewMode.CLASSES) "Groupes & Classes" else "Liste des Eleves",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = state.colors.textPrimary
            )
            Text(
                text = "Gerez vos effectifs pour l'annee ${state.currentYear}",
                style = MaterialTheme.typography.bodySmall,
                color = state.colors.textMuted
            )
        }
        
        ViewToggle(
            currentMode = state.viewMode,
            onModeChange = { onStateChange(state.copy(viewMode = it, selectionMode = false, selectedStudentIds = emptySet())) },
            colors = state.colors,
            modifier = Modifier.fillMaxWidth()
        )

        ActionBar(
            onAddStudentClick = { onStateChange(state.copy(viewMode = StudentsViewMode.STUDENT_FORM, selectedStudentId = null)) },
            onAddClassClick = { onStateChange(state.copy(viewMode = StudentsViewMode.CLASS_FORM, selectedClassroom = null)) },
            colors = state.colors,
            isCompact = true
        )
        
        // Charts Section
        if (state.viewMode == StudentsViewMode.CLASSES && state.searchQuery.isEmpty()) {
            DistributionChart(distribution = state.levelDistribution, colors = state.colors)
        }

        // Filters and Search
        if (state.viewMode != StudentsViewMode.STUDENT_FORM && state.viewMode != StudentsViewMode.PROFILE) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SearchBar(
                        query = state.searchQuery,
                        onQueryChange = { onStateChange(state.copy(searchQuery = it)) },
                        colors = state.colors,
                        modifier = Modifier.weight(1f)
                    )
                    if (state.viewMode == StudentsViewMode.STUDENTS) {
                        IconButton(
                            onClick = { onStateChange(state.copy(selectionMode = !state.selectionMode, selectedStudentIds = emptySet())) },
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = if (state.selectionMode) MaterialTheme.colorScheme.primary else state.colors.card,
                                contentColor = if (state.selectionMode) Color.White else state.colors.textPrimary
                            )
                        ) {
                            Icon(Icons.Default.Checklist, contentDescription = null)
                        }
                    }
                }

                AdvancedFilters(
                    selectedLevel = state.selectedLevel,
                    onLevelChange = { onStateChange(state.copy(selectedLevel = it)) },
                    selectedGender = state.selectedGender,
                    onGenderChange = { onStateChange(state.copy(selectedGender = it)) },
                    visibility = state.visibilityFilter,
                    onVisibilityChange = { onStateChange(state.copy(visibilityFilter = it)) },
                    colors = state.colors,
                    isCompact = true
                )
            }
        }
    }
}
