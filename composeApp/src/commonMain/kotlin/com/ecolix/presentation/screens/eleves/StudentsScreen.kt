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
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.*
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.ecolix.data.models.Classroom
import com.ecolix.data.models.Student
import com.ecolix.data.models.StudentsUiState
import com.ecolix.data.models.StudentsViewMode
import com.ecolix.data.models.DashboardColors
import com.ecolix.presentation.components.*
import kotlinx.coroutines.flow.StateFlow
import org.koin.compose.koinInject

class StudentsScreen : Screen {
    @Composable
    override fun Content() {
        StudentsScreenContent(isDarkMode = androidx.compose.foundation.isSystemInDarkTheme())
    }
}

@Composable
fun StudentsScreenContent(isDarkMode: Boolean) {
    val screenModel: StudentsScreenModel = koinInject()
    val uiState by screenModel.state.collectAsState<StudentsUiState>()
    val colors = if (isDarkMode) DashboardColors.dark() else DashboardColors.light()

    LaunchedEffect(isDarkMode) {
        screenModel.onDarkModeChange(isDarkMode)
    }
    
    val filteredStudents = remember<List<Student>>(uiState.searchQuery, uiState.selectedClassroom, uiState.selectedLevel, uiState.selectedGender, uiState.visibilityFilter) {
        uiState.students.filter { student ->
            val matchClass = uiState.selectedClassroom == null || student.classroom == uiState.classrooms.find { it.id == uiState.selectedClassroom }?.name
            val matchLevel = uiState.selectedLevel == null || uiState.classrooms.any { it.name == student.classroom && it.level == uiState.selectedLevel }
            val matchGender = uiState.selectedGender == null || student.gender == uiState.selectedGender
            val matchVisibility = when (uiState.visibilityFilter) {
                "active" -> !student.isDeleted
                "deleted" -> student.isDeleted
                else -> true
            }
            val matchSearch = uiState.searchQuery.isEmpty() || 
                             "${student.firstName} ${student.lastName}".contains(uiState.searchQuery, ignoreCase = true) ||
                             student.matricule?.contains(uiState.searchQuery, ignoreCase = true) == true
            
            matchClass && matchLevel && matchGender && matchVisibility && matchSearch
        }
    }

    val filteredClasses = remember<List<Classroom>>(uiState.searchQuery, uiState.selectedLevel) {
        uiState.classrooms.filter { classroom ->
            val matchLevel = uiState.selectedLevel == null || classroom.level == uiState.selectedLevel
            val matchSearch = uiState.searchQuery.isEmpty() || classroom.name.contains(uiState.searchQuery, ignoreCase = true)
            matchLevel && matchSearch
        }
    }

    // SLICE FOR LAZY LOADING
    val visibleStudents = remember<List<Student>>(filteredStudents, uiState.loadedStudentsCount) {
        filteredStudents.take(uiState.loadedStudentsCount)
    }
    val visibleClasses = remember<List<Classroom>>(filteredClasses, uiState.loadedClassesCount) {
        filteredClasses.take(uiState.loadedClassesCount)
    }

    // Scroll States
    val studentsListState = androidx.compose.foundation.lazy.rememberLazyListState()
    val studentsGridState = androidx.compose.foundation.lazy.grid.rememberLazyGridState()
    val classesGridState = androidx.compose.foundation.lazy.grid.rememberLazyGridState()

    // Load more triggers
    val studentsEndReached by remember {
        derivedStateOf {
            val layoutInfo = studentsListState.layoutInfo
            val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisibleItemIndex >= visibleStudents.size - 1
        }
    }
    LaunchedEffect(studentsEndReached) {
        if (studentsEndReached && filteredStudents.size > uiState.loadedStudentsCount) {
            screenModel.loadMoreStudents()
        }
    }

    val studentsGridEndReached by remember {
        derivedStateOf {
            val layoutInfo = studentsGridState.layoutInfo
            val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisibleItemIndex >= visibleStudents.size - 1
        }
    }
    LaunchedEffect(studentsGridEndReached) {
        if (studentsGridEndReached && filteredStudents.size > uiState.loadedStudentsCount) {
            screenModel.loadMoreStudents()
        }
    }

    val classesEndReached by remember {
        derivedStateOf {
            val layoutInfo = classesGridState.layoutInfo
            val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisibleItemIndex >= visibleClasses.size - 1
        }
    }
    LaunchedEffect(classesEndReached) {
        if (classesEndReached && filteredClasses.size > uiState.loadedClassesCount) {
            screenModel.loadMoreClasses()
        }
    }

    val isCompact = false // Placeholder for screen size check

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = if (isCompact) 16.dp else 32.dp)
        ) {
            // Header (Desktop)
            if (!isCompact) {
                Spacer(modifier = Modifier.height(32.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (uiState.viewMode == StudentsViewMode.CLASSES) "Groupes & Classes" else "Liste des Eleves",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 32.sp
                            ),
                            color = colors.textPrimary
                        )
                        Text(
                            text = "Gerez et suivez vos effectifs pour l'annee ${uiState.currentYear}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.textMuted
                        )
                    }
                    
                    ViewToggle(
                        currentMode = uiState.viewMode,
                        onModeChange = { screenModel.onViewModeChange(it) },
                        colors = colors
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
                
                ActionBar(
                    onAddStudentClick = { screenModel.onViewModeChange(StudentsViewMode.STUDENT_FORM) },
                    onAddClassClick = { screenModel.onViewModeChange(StudentsViewMode.CLASS_FORM) },
                    colors = colors,
                    isCompact = false
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                if (uiState.viewMode == StudentsViewMode.CLASSES && uiState.searchQuery.isEmpty()) {
                    DistributionChart(distribution = uiState.levelDistribution, colors = colors)
                }

                // Filters and Search (Desktop)
                if (uiState.viewMode != StudentsViewMode.STUDENT_FORM && uiState.viewMode != StudentsViewMode.PROFILE) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            SearchBar(
                                query = uiState.searchQuery,
                                onQueryChange = { screenModel.onSearchQueryChange(it) },
                                colors = colors,
                                modifier = Modifier.weight(1f)
                            )
                            if (uiState.viewMode == StudentsViewMode.STUDENTS) {
                                StudentViewToggle(
                                    currentMode = uiState.studentDisplayMode,
                                    onModeChange = { screenModel.onStudentDisplayModeChange(it) },
                                    colors = colors,
                                    ListIcon = Icons.AutoMirrored.Filled.LibraryBooks,
                                )
                                IconButton(
                                    onClick = { screenModel.updateState(uiState.copy(selectionMode = !uiState.selectionMode, selectedStudentIds = emptySet())) },
                                    colors = IconButtonDefaults.iconButtonColors(
                                        containerColor = if (uiState.selectionMode) MaterialTheme.colorScheme.primary else colors.card,
                                        contentColor = if (uiState.selectionMode) Color.White else colors.textPrimary
                                    )
                                ) {
                                    Icon(Icons.Default.Checklist, contentDescription = null)
                                }
                            }
                        }

                        AdvancedFilters(
                            selectedLevel = uiState.selectedLevel,
                            onLevelChange = { screenModel.onLevelChange(it) },
                            selectedGender = uiState.selectedGender,
                            onGenderChange = { screenModel.onGenderChange(it) },
                            visibility = uiState.visibilityFilter,
                            onVisibilityChange = { screenModel.onVisibilityChange(it) },
                            colors = colors,
                            isCompact = false
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Main Content View
            when (uiState.viewMode) {
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
                                    uiState = uiState,
                                    colors = colors,
                                    isCompact = true,
                                    onStateChange = { screenModel.updateState(it) }
                                )
                            }
                        }

                        items(visibleClasses) { classroom ->
                            ClassCard(classroom, colors, onClick = {
                                screenModel.updateState(uiState.copy(viewMode = StudentsViewMode.CLASS_DETAILS, selectedClassroom = classroom.id))
                            })
                        }

                        if (filteredClasses.size > uiState.loadedClassesCount) {
                            item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(maxLineSpan) }) {
                                Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                                }
                            }
                        }
                    }
                }
                StudentsViewMode.STUDENTS -> {
                    if (uiState.studentDisplayMode == com.ecolix.data.models.StudentDisplayMode.LIST) {
                        LazyColumn(
                            state = studentsListState,
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(bottom = 80.dp) // Space for selection bar
                        ) {
                            if (isCompact) {
                                item {
                                    MobileStudentsHeader(
                                        uiState = uiState,
                                        colors = colors,
                                        isCompact = true,
                                        onStateChange = { screenModel.updateState(it) }
                                    )
                                }
                            } else {
                                item {
                                    Text(
                                        text = "Eleves trouves (${filteredStudents.size})",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = colors.textPrimary
                                    )
                                }
                            }

                            items(visibleStudents, key = { it.id }) { student ->
                                StudentRow(
                                    student = student,
                                    colors = colors,
                                    selectionMode = uiState.selectionMode,
                                    isSelected = uiState.selectedStudentIds.contains(student.id),
                                    onToggleSelect = {
                                        val newSelection = uiState.selectedStudentIds.toMutableSet()
                                        if (newSelection.contains(student.id)) newSelection.remove(student.id)
                                        else newSelection.add(student.id)
                                        screenModel.updateState(uiState.copy(selectedStudentIds = newSelection))
                                    },
                                    onClick = { screenModel.updateState(uiState.copy(viewMode = StudentsViewMode.PROFILE, selectedStudentId = student.id)) }
                                )
                            }
                            
                            if (filteredStudents.size > uiState.loadedStudentsCount) {
                                item {
                                    Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                                        CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                                    }
                                }
                            }

                            if (filteredStudents.isEmpty()) {
                                item {
                                    Box(
                                        modifier = Modifier.fillMaxWidth().padding(48.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = "Aucun eleve trouve.", color = colors.textMuted)
                                    }
                                }
                            }
                        }
                    } else {
                        LazyVerticalGrid(
                            state = studentsGridState,
                            columns = GridCells.Adaptive(minSize = 240.dp),
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 80.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            if (isCompact) {
                                item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(maxLineSpan) }) {
                                    MobileStudentsHeader(
                                        uiState = uiState,
                                        colors = colors,
                                        isCompact = true,
                                        onStateChange = { screenModel.updateState(it) }
                                    )
                                }
                            }

                            item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(maxLineSpan) }) {
                                Text(
                                    text = "Eleves trouves (${filteredStudents.size})",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = colors.textPrimary
                                )
                            }

                            items(visibleStudents, key = { it.id }) { student ->
                                StudentCard(
                                    student = student,
                                    colors = colors,
                                    selectionMode = uiState.selectionMode,
                                    isSelected = uiState.selectedStudentIds.contains(student.id),
                                    onToggleSelect = {
                                        val newSelection = uiState.selectedStudentIds.toMutableSet()
                                        if (newSelection.contains(student.id)) newSelection.remove(student.id)
                                        else newSelection.add(student.id)
                                        screenModel.updateState(uiState.copy(selectedStudentIds = newSelection))
                                    },
                                    onClick = { screenModel.updateState(uiState.copy(viewMode = StudentsViewMode.PROFILE, selectedStudentId = student.id)) },
                                    icon = Icons.Filled.Category,
                                )
                            }
                            
                            if (filteredStudents.size > uiState.loadedStudentsCount) {
                                item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(maxLineSpan) }) {
                                    Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                                        CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                                    }
                                }
                            }

                            if (filteredStudents.isEmpty()) {
                                item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(maxLineSpan) }) {
                                    Box(
                                        modifier = Modifier.fillMaxWidth().padding(48.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = "Aucun eleve trouve.", color = colors.textMuted)
                                    }
                                }
                            }
                        }
                    }
                }
                StudentsViewMode.PROFILE -> {
                    val student = uiState.students.find { it.id == uiState.selectedStudentId }
                    if (student != null) {
                        StudentProfileScreen(
                            student = student,
                            colors = colors,
                            isCompact = isCompact,
                            onBack = { screenModel.onViewModeChange(StudentsViewMode.STUDENTS) }
                        )
                    }
                }
                StudentsViewMode.CLASS_DETAILS -> {
                    val classroom = uiState.classrooms.find { it.id == uiState.selectedClassroom }
                    if (classroom != null) {
                        ClassDetailsScreen(
                            classroom = classroom,
                            students = uiState.students.filter { it.classroom == classroom.name },
                            colors = colors,
                            isCompact = isCompact,
                            onBack = { screenModel.onViewModeChange(StudentsViewMode.CLASSES) },
                            onStudentClick = { studentId ->
                                screenModel.updateState(uiState.copy(viewMode = StudentsViewMode.PROFILE, selectedStudentId = studentId))
                            }
                        )
                    }
                }
                StudentsViewMode.STUDENT_FORM -> {
                    val student = uiState.students.find { it.id == uiState.selectedStudentId }
                    StudentForm(
                        student = student,
                        classrooms = uiState.classrooms.map { it.name },
                        currentAcademicYear = uiState.currentYear,
                        colors = colors,
                        isCompact = isCompact,
                        onBack = { screenModel.onViewModeChange(StudentsViewMode.STUDENTS) },
                        onSave = { updatedStudent ->
                            // Update logic
                            screenModel.onViewModeChange(StudentsViewMode.STUDENTS)
                        }
                    )
                }
                StudentsViewMode.CLASS_FORM -> {
                    val classroom = uiState.classrooms.find { it.id == uiState.selectedClassroom }
                    ClassForm(
                        classroom = classroom,
                        colors = colors,
                        isCompact = isCompact,
                        onBack = { screenModel.onViewModeChange(StudentsViewMode.CLASSES) },
                        onSave = { updatedClass ->
                            val newList = uiState.classrooms.toMutableList()
                            val index = newList.indexOfFirst { it.id == updatedClass.id }
                            if (index != -1) newList[index] = updatedClass
                            else newList.add(updatedClass)
                            screenModel.updateState(uiState.copy(classrooms = newList, viewMode = StudentsViewMode.CLASSES))
                        }
                    )
                }
            }
        }

        // Floating Selection Bar
        AnimatedVisibility(
            visible = uiState.selectionMode && uiState.selectedStudentIds.isNotEmpty(),
            modifier = Modifier.align(Alignment.BottomCenter).padding(24.dp)
        ) {
            SelectionActionBar(
                selectedCount = uiState.selectedStudentIds.size,
                onClearSelection = { screenModel.updateState(uiState.copy(selectedStudentIds = emptySet())) },
                onDeleteSelected = { /* Logic for delete */ },
                colors = colors,
                isCompact = isCompact
            )
        }
    }
}

@Composable
private fun MobileStudentsHeader(
    uiState: StudentsUiState,
    colors: DashboardColors,
    isCompact: Boolean,
    onStateChange: (StudentsUiState) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Header Row
        Column {
            Text(
                text = if (uiState.viewMode == StudentsViewMode.CLASSES) "Groupes & Classes" else "Liste des Eleves",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = colors.textPrimary
            )
            Text(
                text = "Gerez vos effectifs pour l'annee ${uiState.currentYear}",
                style = MaterialTheme.typography.bodySmall,
                color = colors.textMuted
            )
        }
        
        ViewToggle(
            currentMode = uiState.viewMode,
            onModeChange = { onStateChange(uiState.copy(viewMode = it, selectionMode = false, selectedStudentIds = emptySet())) },
            colors = colors,
            modifier = Modifier.fillMaxWidth()
        )

        ActionBar(
            onAddStudentClick = { onStateChange(uiState.copy(viewMode = StudentsViewMode.STUDENT_FORM, selectedStudentId = null)) },
            onAddClassClick = { onStateChange(uiState.copy(viewMode = StudentsViewMode.CLASS_FORM, selectedClassroom = null)) },
            colors = colors,
            isCompact = true
        )
        
        // Charts Section
        if (uiState.viewMode == StudentsViewMode.CLASSES && uiState.searchQuery.isEmpty()) {
            DistributionChart(distribution = uiState.levelDistribution, colors = colors)
        }

        // Filters and Search
        if (uiState.viewMode != StudentsViewMode.STUDENT_FORM && uiState.viewMode != StudentsViewMode.PROFILE) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SearchBar(
                        query = uiState.searchQuery,
                        onQueryChange = { onStateChange(uiState.copy(searchQuery = it)) },
                        colors = colors,
                        modifier = Modifier.weight(1f)
                    )
                    if (uiState.viewMode == StudentsViewMode.STUDENTS) {
                        StudentViewToggle(
                            currentMode = uiState.studentDisplayMode,
                            onModeChange = { onStateChange(uiState.copy(studentDisplayMode = it)) },
                            colors = colors,
                            ListIcon = Icons.AutoMirrored.Filled.LibraryBooks,
                        )
                        IconButton(
                            onClick = { onStateChange(uiState.copy(selectionMode = !uiState.selectionMode, selectedStudentIds = emptySet())) },
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = if (uiState.selectionMode) MaterialTheme.colorScheme.primary else colors.card,
                                contentColor = if (uiState.selectionMode) Color.White else colors.textPrimary
                            )
                        ) {
                            Icon(Icons.Default.Checklist, contentDescription = null)
                        }
                    }
                }

                AdvancedFilters(
                    selectedLevel = uiState.selectedLevel,
                    onLevelChange = { onStateChange(uiState.copy(selectedLevel = it)) },
                    selectedGender = uiState.selectedGender,
                    onGenderChange = { onStateChange(uiState.copy(selectedGender = it)) },
                    visibility = uiState.visibilityFilter,
                    onVisibilityChange = { onStateChange(uiState.copy(visibilityFilter = it)) },
                    colors = colors,
                    isCompact = true
                )
            }
        }
    }
}
