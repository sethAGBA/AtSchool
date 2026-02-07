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
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.heightIn
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

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it, duration = SnackbarDuration.Short)
            screenModel.clearError()
        }
    }

    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it, duration = SnackbarDuration.Short)
            screenModel.clearSuccess()
        }
    }

    LaunchedEffect(isDarkMode) {
        screenModel.onDarkModeChange(isDarkMode)
    }

    val cycleNames = remember(uiState.cycles) { uiState.cycles.map { it.name }.distinct() }
    
    val filteredStudents = remember<List<Student>>(uiState.students, uiState.classrooms, uiState.searchQuery, uiState.selectedClassroom, uiState.selectedLevel, uiState.selectedGender, uiState.visibilityFilter) {
        uiState.students.filter { student ->
            val matchClass = uiState.selectedClassroom == null || student.classroom == uiState.classrooms.find { it.id == uiState.selectedClassroom }?.name
            val matchCycle = uiState.selectedLevel == null || uiState.classrooms.any { it.name == student.classroom && it.cycle == uiState.selectedLevel }
            val matchGender = uiState.selectedGender == null || student.gender == uiState.selectedGender
            val matchVisibility = when (uiState.visibilityFilter) {
                "active" -> !student.isDeleted
                "deleted" -> student.isDeleted
                else -> true
            }
            val matchSearch = uiState.searchQuery.isEmpty() || 
                             "${student.firstName} ${student.lastName}".contains(uiState.searchQuery, ignoreCase = true) ||
                             student.matricule?.contains(uiState.searchQuery, ignoreCase = true) == true
            
            matchClass && matchCycle && matchGender && matchVisibility && matchSearch
        }
    }

    val filteredClasses = remember<List<Classroom>>(uiState.classrooms, uiState.searchQuery, uiState.selectedLevel) {
        uiState.classrooms.filter { classroom ->
            val matchCycle = uiState.selectedLevel == null || classroom.cycle == uiState.selectedLevel
            val matchSearch = uiState.searchQuery.isEmpty() || classroom.name.contains(uiState.searchQuery, ignoreCase = true)
            matchCycle && matchSearch
        }.sortedWith(compareBy({ it.cycle ?: "Zzz" }, { it.level }, { it.name })) // Sort by cycle, then level, then name
    }

    // Group classes by cycle for broad categorization
    val groupedClasses = remember<Map<String, List<Classroom>>>(filteredClasses) {
        filteredClasses.groupBy { classroom ->
            classroom.cycle ?: "Autres"
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
            val totalVisibleItems = layoutInfo.totalItemsCount
            // Trigger when we're within 3 items of the end to ensure smooth loading
            lastVisibleItemIndex >= totalVisibleItems - 3
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
            val totalVisibleItems = layoutInfo.totalItemsCount
            // Trigger when we're within 3 items of the end to ensure smooth loading
            lastVisibleItemIndex >= totalVisibleItems - 3
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
            val totalVisibleItems = layoutInfo.totalItemsCount
            // Trigger when we're within 3 items of the end to ensure smooth loading
            lastVisibleItemIndex >= totalVisibleItems - 3
        }
    }
    LaunchedEffect(classesEndReached) {
        if (classesEndReached && filteredClasses.size > uiState.loadedClassesCount) {
            screenModel.loadMoreClasses()
        }
    }

    val isCompact = false // Placeholder for screen size check

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = colors.background,
        modifier = Modifier.fillMaxSize()
    ) { padding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
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
                            text = when(uiState.viewMode) {
                                StudentsViewMode.STRUCTURE -> "Structure Scolaire"
                                StudentsViewMode.CLASSES -> "Groupes & Classes"
                                else -> "Liste des Eleves"
                            },
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
                    onAddStudentClick = { screenModel.updateState(uiState.copy(viewMode = StudentsViewMode.STUDENT_FORM, selectedStudentId = null)) },
                    onAddClassClick = { screenModel.updateState(uiState.copy(viewMode = StudentsViewMode.CLASS_FORM, selectedClassroom = null)) },
                    onRefreshClick = { screenModel.refreshData() },
                    colors = colors,
                    isCompact = false
                )
                
                // Main content handles its own header/filters in lazy containers
            }

            // Main Content View
            when (uiState.viewMode) {
                StudentsViewMode.STRUCTURE -> {
                    SchoolStructureView(
                        uiState = uiState,
                        screenModel = screenModel,
                        colors = colors,
                        isCompact = isCompact
                    )
                }
                StudentsViewMode.CLASSES -> {
                    LazyVerticalGrid(
                        state = classesGridState,
                        columns = GridCells.Adaptive(minSize = 300.dp),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        if (!isCompact) {
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
                                    if (uiState.viewMode == StudentsViewMode.CLASSES && uiState.searchQuery.isEmpty()) {
                                        DistributionChart(distribution = uiState.levelDistribution, colors = colors)
                                    }

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

                                        YearSelector(
                                            selectedYear = uiState.currentYear,
                                            years = uiState.schoolYears,
                                            onYearChange = { screenModel.onYearChange(it) },
                                            colors = colors
                                        )
                                    }

                                    AdvancedFilters(
                                        levels = cycleNames,
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
                        }

                        if (isCompact) {
                            item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(maxLineSpan) }) {
                                MobileStudentsHeader(
                                    uiState = uiState,
                                    colors = colors,
                                    isCompact = true,
                                    onStateChange = { screenModel.updateState(it) },
                                    onViewModeChange = { screenModel.onViewModeChange(it) },
                                    onYearChange = { screenModel.onYearChange(it) },
                                    onRefreshClick = { screenModel.refreshData() }
                                )
                            }
                        }

                        // Display classes grouped by level
                        if (groupedClasses.isEmpty()) {
                            item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(maxLineSpan) }) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 24.dp, vertical = 64.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        Text(
                                            text = "🏫",
                                            style = MaterialTheme.typography.displayMedium.copy(fontSize = 48.sp)
                                        )
                                        Text(
                                            text = "Aucune classe trouvée",
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 18.sp
                                            ),
                                            color = colors.textPrimary
                                        )
                                        Text(
                                            text = if (uiState.searchQuery.isNotEmpty())
                                                "Aucune classe ne correspond à votre recherche."
                                            else
                                                "Commencez par créer des classes dans l'onglet Structure.",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = colors.textMuted,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }

                        var itemsDisplayed = 0
                        groupedClasses.forEach { (levelName, classesInLevel) ->
                            if (itemsDisplayed >= uiState.loadedClassesCount) return@forEach
                            
                            // Section header for this level
                            item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(maxLineSpan) }) {
                                Text(
                                    text = levelName,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    ),
                                    color = colors.textPrimary,
                                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                                )
                            }
                            
                            // Classes for this level
                            val visibleClassesForLevel = classesInLevel.take(
                                (uiState.loadedClassesCount - itemsDisplayed).coerceAtLeast(0)
                            )
                            items(visibleClassesForLevel) { classroom ->
                                ClassCard(
                                    classroom = classroom, 
                                    colors = colors, 
                                    onClick = {
                                        screenModel.updateState(uiState.copy(viewMode = StudentsViewMode.CLASS_DETAILS, selectedClassroom = classroom.id))
                                    },
                                    onEdit = {
                                        screenModel.updateState(uiState.copy(viewMode = StudentsViewMode.CLASS_FORM, selectedClassroom = classroom.id))
                                    },
                                    onDelete = {
                                        screenModel.onDeleteClassAttempt(classroom.id)
                                    }
                                )
                            }
                            itemsDisplayed += visibleClassesForLevel.size
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
                        if (!isCompact) {
                            item {
                                Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
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

                                    AdvancedFilters(
                                        levels = cycleNames,
                                        selectedLevel = uiState.selectedLevel,
                                        onLevelChange = { screenModel.onLevelChange(it) },
                                        selectedGender = uiState.selectedGender,
                                        onGenderChange = { screenModel.onGenderChange(it) },
                                        visibility = uiState.visibilityFilter,
                                        onVisibilityChange = { screenModel.onVisibilityChange(it) },
                                        colors = colors,
                                        isCompact = false
                                    )

                                    Text(
                                        text = "Élèves trouvés (${filteredStudents.size})",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = colors.textPrimary
                                    )
                                }
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
                                    onRestore = { screenModel.restoreStudent(student.id) },
                                    onDelete = { screenModel.onDeleteAttempt(setOf(student.id)) },
                                    onTransfer = { screenModel.showTransferDialog(setOf(student.id)) },
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
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 24.dp, vertical = 64.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.spacedBy(16.dp)
                                        ) {
                                            Text(
                                                text = "🎓",
                                                style = MaterialTheme.typography.displayMedium.copy(fontSize = 48.sp)
                                            )
                                            Text(
                                                text = "Aucun élève trouvé",
                                                style = MaterialTheme.typography.titleMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 18.sp
                                                ),
                                                color = colors.textPrimary
                                            )
                                            Text(
                                                text = if (uiState.searchQuery.isNotEmpty())
                                                    "Aucun élève ne correspond à votre recherche."
                                                else
                                                    "Commencez par ajouter des élèves via le formulaire d'inscription.",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = colors.textMuted,
                                                textAlign = TextAlign.Center
                                            )
                                        }
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
                            if (!isCompact) {
                                item(span = { GridItemSpan(maxLineSpan) }) {
                                    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
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
                                            YearSelector(
                                                selectedYear = uiState.currentYear,
                                                years = uiState.schoolYears,
                                                onYearChange = { screenModel.onYearChange(it) },
                                                colors = colors
                                            )
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

                                        AdvancedFilters(
                                            levels = cycleNames,
                                            selectedLevel = uiState.selectedLevel,
                                            onLevelChange = { screenModel.onLevelChange(it) },
                                            selectedGender = uiState.selectedGender,
                                            onGenderChange = { screenModel.onGenderChange(it) },
                                            visibility = uiState.visibilityFilter,
                                            onVisibilityChange = { screenModel.onVisibilityChange(it) },
                                            colors = colors,
                                            isCompact = false
                                        )

                                        Text(
                                            text = "Élèves trouvés (${filteredStudents.size})",
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = colors.textPrimary
                                        )
                                    }
                                }
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
                                    onRestore = { screenModel.restoreStudent(student.id) },
                                    onDelete = { screenModel.onDeleteAttempt(setOf(student.id)) },
                                    onTransfer = { screenModel.showTransferDialog(setOf(student.id)) },
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
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 24.dp, vertical = 64.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.spacedBy(16.dp)
                                        ) {
                                            Text(
                                                text = "🎓",
                                                style = MaterialTheme.typography.displayMedium.copy(fontSize = 48.sp)
                                            )
                                            Text(
                                                text = "Aucun élève trouvé",
                                                style = MaterialTheme.typography.titleMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 18.sp
                                                ),
                                                color = colors.textPrimary
                                            )
                                            Text(
                                                text = if (uiState.searchQuery.isNotEmpty())
                                                    "Aucun élève ne correspond à votre recherche."
                                                else
                                                    "Commencez par ajouter des élèves via le formulaire d'inscription.",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = colors.textMuted,
                                                textAlign = TextAlign.Center
                                            )
                                        }
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
                            onBack = { screenModel.onViewModeChange(StudentsViewMode.STUDENTS) },
                            onEdit = { 
                                screenModel.onViewModeChange(StudentsViewMode.STUDENT_FORM)
                            }
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
                            onEdit = {
                                screenModel.updateState(uiState.copy(viewMode = StudentsViewMode.CLASS_FORM))
                            },
                            onDelete = {
                                screenModel.onDeleteClassAttempt(uiState.selectedClassroom!!)
                            },
                            onStudentClick = { studentId ->
                                screenModel.updateState(uiState.copy(viewMode = StudentsViewMode.PROFILE, selectedStudentId = studentId))
                            },
                            onAddStudent = {
                                screenModel.onQuickAddStudent(classroom.id)
                            },
                            onStudentDelete = { studentId ->
                                screenModel.onDeleteAttempt(setOf(studentId))
                            },
                            onStudentRestoreAction = { studentId ->
                                screenModel.restoreStudent(studentId)
                            },
                            onStudentTransfer = { studentId ->
                                screenModel.showTransferDialog(setOf(studentId))
                            }
                        )
                    }
                }
                StudentsViewMode.STUDENT_FORM -> {
                    val student = uiState.students.find { it.id == uiState.selectedStudentId }
                    key(uiState.lastSavedTimestamp) {
                        StudentForm(
                            student = student,
                            classrooms = uiState.classrooms,
                            currentAcademicYear = uiState.currentYear,
                            colors = colors,
                            isCompact = isCompact,
                            isClassroomFixed = uiState.isClassroomFixed,
                            preSelectedClassroom = uiState.selectedClassroom,
                            onBack = { 
                                if (uiState.isClassroomFixed) {
                                    screenModel.onViewModeChange(StudentsViewMode.CLASS_DETAILS)
                                } else {
                                    screenModel.onViewModeChange(StudentsViewMode.STUDENTS)
                                }
                            },
                            onPickPhoto = { callback ->
                                screenModel.pickAndUploadPhoto { url ->
                                    callback(url)
                                }
                            },
                            isUploadingPhoto = uiState.isUploadingPhoto,
                            onSave = { updatedStudent ->
                                screenModel.saveStudent(updatedStudent)
                            }
                        )
                    }
                }
                StudentsViewMode.CLASS_FORM -> {
                    val selectedClass = uiState.classrooms.find { it.id == uiState.selectedClassroom }
                    ClassForm(
                        classroom = selectedClass,
                        levels = uiState.levels,
                        cycles = uiState.cycles,
                        colors = colors,
                        staffMembers = uiState.staffMembers,
                        isCompact = isCompact,
                        currentAcademicYear = uiState.currentYear,
                        onBack = { screenModel.onViewModeChange(StudentsViewMode.CLASSES) },
                        onSave = { classroom -> screenModel.saveClassroom(classroom) }
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
                onDeleteSelected = { 
                    screenModel.onDeleteAttempt(uiState.selectedStudentIds)
                },
                onRestoreSelected = {
                    screenModel.restoreSelectedStudents()
                },
                onTransferSelected = {
                    screenModel.showTransferDialog(uiState.selectedStudentIds)
                },
                isTrashView = uiState.visibilityFilter == "deleted",
                colors = colors,
                isCompact = isCompact
            )
        }

        if (uiState.showDeleteConfirmation) {
            val isPermanent = uiState.visibilityFilter == "deleted"
            AlertDialog(
                onDismissRequest = { screenModel.onDismissDeleteConfirmation() },
                containerColor = colors.card,
                title = { Text(if (isPermanent) "Suppression DEFINITIVE" else "Confirmer la suppression", color = colors.textPrimary, fontWeight = FontWeight.Bold) },
                text = { 
                    val count = uiState.studentToDeleteIds.size
                    val studentLabel = if (count > 1) "$count élèves" else "cet élève"
                    val message = if (isPermanent) {
                        "Voulez-vous vraiment supprimer DEFINITIVEMENT $studentLabel ? Cette action supprimera également toutes leurs inscriptions et données associées."
                    } else {
                        "Voulez-vous vraiment mettre $studentLabel à la corbeille ?"
                    }
                    Text(text = message, color = colors.textPrimary) 
                },
                confirmButton = {
                    TextButton(
                        onClick = { 
                            if (isPermanent) {
                                screenModel.deleteSelectedStudentsPermanently()
                            } else {
                                screenModel.deleteSelectedStudents()
                            }
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFEF4444))
                    ) {
                        Text(if (isPermanent) "Supprimer Définitivement" else "Supprimer", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { screenModel.onDismissDeleteConfirmation() }) {
                        Text("Annuler", color = colors.textMuted)
                    }
                }
            )
        }

        if (uiState.showTransferDialog) {
            TransferStudentDialog(
                classrooms = uiState.classrooms,
                onDismiss = { screenModel.hideTransferDialog() },
                onConfirm = { classId -> screenModel.transferStudents(classId) },
                colors = colors
            )
        }

        if (uiState.showClassDeleteConfirmation) {
            AlertDialog(
                onDismissRequest = { screenModel.dismissClassDeleteConfirmation() },
                containerColor = colors.card,
                title = { Text("Supprimer la classe", color = colors.textPrimary, fontWeight = FontWeight.Bold) },
                text = { Text("Voulez-vous vraiment supprimer cette classe ? Cette action est irréversible et retirera également les liens avec les élèves.", color = colors.textPrimary) },
                confirmButton = {
                    Button(
                        onClick = { screenModel.confirmClassDeletion() },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error, contentColor = Color.White),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Supprimer")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { screenModel.dismissClassDeleteConfirmation() }) {
                        Text("Annuler", color = colors.textMuted)
                    }
                }
            )
        }

        if (uiState.showClassInUseDialog) {
            val classroom = uiState.classrooms.find { it.id == uiState.classToDeleteId }
            if (classroom != null) {
                AlertDialog(
                    onDismissRequest = { screenModel.dismissClassInUseDialog() },
                    containerColor = colors.card,
                    title = { 
                        Text("Action impossible", color = colors.textPrimary, fontWeight = FontWeight.Bold) 
                    },
                    text = { 
                        Text(
                            "Impossible de supprimer cette classe car elle contient encore ${classroom.studentCount} élève(s). Veuillez d'abord retirer ou transférer tous les élèves.",
                            color = colors.textPrimary
                        ) 
                    },
                    confirmButton = {
                        TextButton(onClick = { screenModel.dismissClassInUseDialog() }) {
                            Text("D'accord", color = colors.textLink)
                        }
                    }
                )
            }
        }
    }
}
}

@Composable
private fun MobileStudentsHeader(
    uiState: StudentsUiState,
    colors: DashboardColors,
    isCompact: Boolean,
    onStateChange: (StudentsUiState) -> Unit,
    onViewModeChange: (StudentsViewMode) -> Unit,
    onYearChange: (String) -> Unit,
    onRefreshClick: () -> Unit
) {
    val cycleNames = remember(uiState.cycles) { uiState.cycles.map { it.name }.distinct() }
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Header Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
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

            YearSelector(
                selectedYear = uiState.currentYear,
                years = uiState.schoolYears,
                onYearChange = onYearChange,
                colors = colors
            )
        }
        
        ViewToggle(
            currentMode = uiState.viewMode,
            onModeChange = { onViewModeChange(it) },
            colors = colors,
            modifier = Modifier.fillMaxWidth()
        )

        ActionBar(
            onAddStudentClick = { onStateChange(uiState.copy(viewMode = StudentsViewMode.STUDENT_FORM, selectedStudentId = null)) },
            onAddClassClick = { onStateChange(uiState.copy(viewMode = StudentsViewMode.CLASS_FORM, selectedClassroom = null)) },
            onRefreshClick = onRefreshClick,
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
                    levels = cycleNames,
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

@Composable
private fun TransferStudentDialog(
    classrooms: List<Classroom>,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
    colors: DashboardColors
) {
    var selectedClassId by remember { mutableStateOf<String?>(null) }
    
    val sortedClassrooms = remember(classrooms) {
        classrooms.sortedBy { it.name }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Changer de classe", color = colors.textPrimary) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = "Sélectionnez la nouvelle classe de destination pour les élèves sélectionnés.",
                    color = colors.textMuted
                )
                
                LazyColumn(
                    modifier = Modifier.heightIn(max = 300.dp).fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(sortedClassrooms) { classroom ->
                        val isSelected = classroom.id == selectedClassId
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent)
                                .clickable { selectedClassId = classroom.id }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { selectedClassId = classroom.id },
                                colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = classroom.name,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = colors.textPrimary
                                )
                                Text(
                                    text = "${classroom.level} • ${classroom.studentCount} élèves",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = colors.textMuted
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { selectedClassId?.let(onConfirm) },
                enabled = selectedClassId != null,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Transférer")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler", color = colors.textMuted)
            }
        },
        containerColor = colors.card
    )
}
