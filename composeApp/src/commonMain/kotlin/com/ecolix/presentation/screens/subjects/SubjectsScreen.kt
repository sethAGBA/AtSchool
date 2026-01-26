package com.ecolix.presentation.screens.subjects

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import cafe.adriel.voyager.core.screen.Screen
import com.ecolix.data.models.*
import com.ecolix.data.models.SubjectsViewMode
import com.ecolix.data.models.SubjectsLayoutMode
import com.ecolix.presentation.components.*
import com.ecolix.presentation.screens.categories.CategoriesDialog
import com.ecolix.presentation.screens.categories.CategoriesScreenModel

@Composable
fun SubjectsScreenContent(isDarkMode: Boolean) {
    val screenModel = remember { SubjectsScreenModel() }
    val state by screenModel.state.collectAsState()
    
    // Theme Update
    LaunchedEffect(isDarkMode) {
        screenModel.onDarkModeChange(isDarkMode)
    }

    // Categories Dialog (Kept for other screens if they use it, but Subjects now has a tab)
    if (state.showCategoriesDialog) {
        val categoriesScreenModel = remember { CategoriesScreenModel() }
        CategoriesDialog(
            onDismiss = { screenModel.toggleCategoriesDialog() },
            screenModel = categoriesScreenModel
        )
    }

    val filteredSubjects = state.subjects.filter { subject ->
        val matchesSearch = subject.name.contains(state.searchQuery, ignoreCase = true) ||
                           subject.code.contains(state.searchQuery, ignoreCase = true)
        val matchesCategory = state.selectedCategoryId == null || subject.categoryId == state.selectedCategoryId
        matchesSearch && matchesCategory
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val screenWidth = maxWidth
        val isCompact = screenWidth < 600.dp
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(if (isCompact) 16.dp else 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Header Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Gestion des Matières",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = if (screenWidth < 1000.dp) 24.sp else 28.sp
                        ),
                        color = state.colors.textPrimary
                    )
                    Text(
                        text = "Définissez et gérez les disciplines enseignées dans l'établissement.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = state.colors.textMuted
                    )
                }

                if (!isCompact) {
                    SubjectsViewToggle(
                        currentMode = state.viewMode,
                        onModeChange = { screenModel.onViewModeChange(it) },
                        colors = state.colors
                    )
                }
            }

            if (isCompact) {
                SubjectsViewToggle(
                    currentMode = state.viewMode,
                    onModeChange = { screenModel.onViewModeChange(it) },
                    colors = state.colors,
                    modifier = Modifier.fillMaxWidth(),
                    isFullWidth = true
                )
            }

            // Main Content Area
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                AnimatedContent(
                    targetState = state.viewMode,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    modifier = Modifier.fillMaxSize()
                ) { mode ->
                    when (mode) {
                        SubjectsViewMode.SUBJECTS -> SubjectsListView(
                            state = state,
                            screenModel = screenModel,
                            filteredSubjects = filteredSubjects,
                            isCompact = isCompact
                        )
                        SubjectsViewMode.CATEGORIES -> CategoriesTabView(state.isDarkMode, screenModel)
                        SubjectsViewMode.PROFESSORS -> ProfessorsTabView(state, screenModel)
                        SubjectsViewMode.CONFIG -> ConfigTabView(state, screenModel)
                        SubjectsViewMode.FORM -> {
                             SubjectForm(
                                subject = state.selectedSubject,
                                categories = state.categories,
                                colors = state.colors,
                                isCompact = isCompact,
                                onBack = { 
                                    screenModel.onSelectSubject(null)
                                    screenModel.onViewModeChange(SubjectsViewMode.SUBJECTS) 
                                },
                                onSave = { 
                                    screenModel.saveSubject(it)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SubjectsListView(
    state: SubjectsUiState,
    screenModel: SubjectsScreenModel,
    filteredSubjects: List<Subject>,
    isCompact: Boolean
) {
    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        // ACTION BAR
        SubjectActionBar(
            onAddSubjectClick = {
                screenModel.onSelectSubject(null)
                screenModel.onViewModeChange(SubjectsViewMode.FORM)
            },
            colors = state.colors,
            isCompact = isCompact
        )

        // Stats row
        SubjectStats(subjects = state.subjects, colors = state.colors)

        // Search and Filters Section
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SearchBar(
                    query = state.searchQuery,
                    onQueryChange = { screenModel.onSearchQueryChange(it) },
                    colors = state.colors,
                    modifier = Modifier.weight(1f)
                )
                
                // Layout Mode Toggle (LIST vs GRID)
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(state.colors.card)
                        .border(1.dp, state.colors.divider, RoundedCornerShape(12.dp))
                        .padding(4.dp)
                ) {
                    ToggleItem(
                        selected = state.layoutMode == SubjectsLayoutMode.LIST,
                        onClick = { screenModel.onLayoutModeChange(SubjectsLayoutMode.LIST) },
                        label = "Liste",
                        icon = Icons.AutoMirrored.Filled.List,
                        colors = state.colors
                    )
                    ToggleItem(
                        selected = state.layoutMode == SubjectsLayoutMode.GRID,
                        onClick = { screenModel.onLayoutModeChange(SubjectsLayoutMode.GRID) },
                        label = "Grille",
                        icon = Icons.Default.GridView,
                        colors = state.colors
                    )
                }
            }

            CategoryFilterChips(
                categories = state.categories,
                selectedCategoryId = state.selectedCategoryId,
                onCategoryChange = { screenModel.onCategoryChange(it) },
                colors = state.colors
            )
        }

        if (filteredSubjects.isEmpty()) {
            EmptySubjectsState(colors = state.colors)
        } else {
            if (state.layoutMode == SubjectsLayoutMode.GRID) {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 250.dp),
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(filteredSubjects) { subject ->
                        SubjectCard(
                            subject = subject,
                            colors = state.colors,
                            onClick = { 
                                screenModel.onSelectSubject(subject)
                                screenModel.onViewModeChange(SubjectsViewMode.FORM)
                            }
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(filteredSubjects) { subject ->
                        SubjectRow(
                            subject = subject,
                            colors = state.colors,
                            onClick = { 
                                screenModel.onSelectSubject(subject)
                                screenModel.onViewModeChange(SubjectsViewMode.FORM)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ClassSelector(
    selectedClass: String,
    classrooms: List<String>,
    colors: DashboardColors,
    onClassSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    Box {
        OutlinedButton(
            onClick = { expanded = true },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.textPrimary),
            border = androidx.compose.foundation.BorderStroke(1.dp, colors.divider)
        ) {
            Icon(Icons.Default.School, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(selectedClass)
            Spacer(modifier = Modifier.width(4.dp))
            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
        }
        
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(colors.card)
        ) {
            classrooms.forEach { classroom ->
                DropdownMenuItem(
                    text = { Text(classroom, color = colors.textPrimary) },
                    onClick = {
                        onClassSelected(classroom)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun CategoriesTabView(isDarkMode: Boolean, subjectsScreenModel: SubjectsScreenModel) {
    val categoriesScreenModel = remember { com.ecolix.presentation.screens.categories.CategoriesScreenModel() }
    val state by categoriesScreenModel.state.collectAsState()
    var showForm by remember { mutableStateOf(false) }

    // Sync categories back if needed or just use the screen model
    // For now we just display the list
    if (showForm) {
        com.ecolix.presentation.screens.categories.CategoryForm(
            category = state.selectedCategory,
            colors = state.colors,
            onBack = { 
                showForm = false 
                categoriesScreenModel.onSelectCategory(null)
            },
            onSave = { 
                categoriesScreenModel.saveCategory(it)
                showForm = false
            },
            onDelete = { 
                if (state.selectedCategory != null) {
                    categoriesScreenModel.deleteCategory(state.selectedCategory!!.id)
                    showForm = false
                }
            }
        )
    } else {
        com.ecolix.presentation.screens.categories.CategoriesListContent(
            state = state,
            onQueryChange = { categoriesScreenModel.onSearchQueryChange(it) },
            onAddClick = { 
                categoriesScreenModel.onSelectCategory(null)
                showForm = true 
            },
            onCategoryClick = { 
                categoriesScreenModel.onSelectCategory(it)
                showForm = true
            }
        )
    }
}

@Composable
private fun ProfessorsTabView(state: SubjectsUiState, screenModel: SubjectsScreenModel) {
    var showProfessorDialog by remember { mutableStateOf<Pair<String, Subject>?>(null) }
    var showManageSubjectsDialog by remember { mutableStateOf(false) }
    
    val currentClass = state.selectedClass ?: state.classrooms.firstOrNull() ?: ""
    val classConfigs = state.classroomConfigs.filter { it.className == currentClass }
    val assignedSubjectIds = classConfigs.map { it.subjectId }.toSet()
    val assignedSubjects = state.subjects.filter { it.id in assignedSubjectIds }

    if (showProfessorDialog != null) {
        val (className, subject) = showProfessorDialog!!
        val config = state.classroomConfigs.find { it.className == className && it.subjectId == subject.id }
        
        ProfessorAssignmentDialog(
            subject = subject,
            allTeachers = screenModel.getAvailableTeachers(),
            colors = state.colors,
            onDismiss = { showProfessorDialog = null },
            onSave = { professors ->
                // For now, we take the first one since users want "SON prof"
                screenModel.updateClassSubjectProfessor(className, subject.id, professors.firstOrNull())
                showProfessorDialog = null
            }
        )
    }

    if (showManageSubjectsDialog) {
        ManageClassSubjectsDialog(
            className = currentClass,
            allSubjects = state.subjects,
            assignedSubjectIds = assignedSubjectIds,
            colors = state.colors,
            onDismiss = { showManageSubjectsDialog = false },
            onToggleSubject = { screenModel.toggleSubjectInClass(currentClass, it) }
        )
    }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "Affectation des Professeurs",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = state.colors.textPrimary
                )
                Text(
                    "Gérez les professeurs pour chaque matière de la classe sélectionnée.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = state.colors.textMuted
                )
            }
            
            ClassSelector(
                selectedClass = currentClass,
                classrooms = state.classrooms,
                colors = state.colors,
                onClassSelected = { screenModel.onSelectClass(it) }
            )
        }
        
        Button(
            onClick = { showManageSubjectsDialog = true },
            colors = ButtonDefaults.buttonColors(containerColor = state.colors.card, contentColor = state.colors.textPrimary),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            border = androidx.compose.foundation.BorderStroke(1.dp, state.colors.divider)
        ) {
            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Modifier les matières enseignées en $currentClass")
        }

        if (assignedSubjects.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Aucune matière configurée pour cette classe.", color = state.colors.textMuted)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(assignedSubjects) { subject ->
                    val config = classConfigs.find { it.subjectId == subject.id }
                    val professor = screenModel.getAvailableTeachers().find { it.id == config?.professorId }
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(state.colors.card)
                            .border(1.dp, state.colors.divider, RoundedCornerShape(12.dp))
                            .clickable { showProfessorDialog = currentClass to subject }
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(subject.name, fontWeight = FontWeight.Bold, color = state.colors.textPrimary)
                            Text(
                                professor?.let { "${it.firstName} ${it.lastName}" } ?: "Aucun professeur affecté",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (professor != null) state.colors.textPrimary else Color.Red
                            )
                        }
                        Icon(Icons.Default.PersonAdd, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}

@Composable
private fun ConfigTabView(state: SubjectsUiState, screenModel: SubjectsScreenModel) {
    var showConfigDialog by remember { mutableStateOf<Pair<String, Subject>?>(null) }
    
    val currentClass = state.selectedClass ?: state.classrooms.firstOrNull() ?: ""
    val classConfigs = state.classroomConfigs.filter { it.className == currentClass }
    val assignedSubjectIds = classConfigs.map { it.subjectId }.toSet()
    val assignedSubjects = state.subjects.filter { it.id in assignedSubjectIds }

    if (showConfigDialog != null) {
        val (className, subject) = showConfigDialog!!
        val config = classConfigs.find { it.subjectId == subject.id }
        
        SubjectConfigDialog(
            subject = subject,
            colors = state.colors,
            onDismiss = { showConfigDialog = null },
            onSave = { coef, hours ->
                screenModel.updateClassSubjectConfig(className, subject.id, coef, hours)
                showConfigDialog = null
            }
        )
    }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "Coefficients et Volumes Horaires",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = state.colors.textPrimary
                )
                Text(
                    "Définissez les coefficients et volumes horaires par classe.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = state.colors.textMuted
                )
            }
            
            ClassSelector(
                selectedClass = currentClass,
                classrooms = state.classrooms,
                colors = state.colors,
                onClassSelected = { screenModel.onSelectClass(it) }
            )
        }
        
        if (assignedSubjects.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Aucune matière configurée pour cette classe. Utilisez l'onglet Affectations pour en ajouter.", color = state.colors.textMuted)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(assignedSubjects) { subject ->
                    val config = classConfigs.find { it.subjectId == subject.id }
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(state.colors.card)
                            .border(1.dp, state.colors.divider, RoundedCornerShape(12.dp))
                            .clickable { showConfigDialog = currentClass to subject }
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(subject.name, fontWeight = FontWeight.Bold, color = state.colors.textPrimary)
                            Text(
                                "Coef: ${config?.coefficient ?: subject.defaultCoefficient} • ${config?.weeklyHours ?: subject.weeklyHours}h/semaine",
                                style = MaterialTheme.typography.bodySmall,
                                color = state.colors.textMuted
                            )
                        }
                        Icon(Icons.Default.Settings, contentDescription = null, tint = state.colors.textMuted)
                    }
                }
            }
        }
    }
}

@Composable
private fun ManageClassSubjectsDialog(
    className: String,
    allSubjects: List<Subject>,
    assignedSubjectIds: Set<String>,
    colors: DashboardColors,
    onDismiss: () -> Unit,
    onToggleSubject: (String) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        CardContainer(containerColor = colors.card) {
            Column(
                modifier = Modifier.fillMaxWidth().heightIn(max = 600.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Matières de la classe : $className",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = colors.textPrimary
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Fermer", tint = colors.textMuted)
                    }
                }
                
                HorizontalDivider(color = colors.divider)
                
                LazyColumn(
                   verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(allSubjects) { subject ->
                        val isAssigned = assignedSubjectIds.contains(subject.id)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onToggleSubject(subject.id) }
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = isAssigned,
                                onCheckedChange = { onToggleSubject(subject.id) },
                                colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(subject.name, color = colors.textPrimary)
                        }
                    }
                }
                
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Terminer")
                }
            }
        }
    }
}

@Composable
private fun SubjectStats(subjects: List<Subject>, colors: DashboardColors) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StatCardMini(
            label = "Total Matières",
            value = subjects.size.toString(),
            icon = Icons.AutoMirrored.Filled.LibraryBooks,
            color = Color(0xFF6366F1),
            colors = colors,
            modifier = Modifier.weight(1f)
        )
        StatCardMini(
            label = "Catégories",
            value = subjects.map { it.categoryName }.distinct().size.toString(),
            icon = Icons.Default.Category,
            color = Color(0xFFEC4899),
            colors = colors,
            modifier = Modifier.weight(1f)
        )
        StatCardMini(
            label = "Coeff. Moyen",
            value = if (subjects.isEmpty()) "0" else String.format("%.1f", subjects.map { it.defaultCoefficient }.average()),
            icon = Icons.Default.Functions,
            color = Color(0xFF10B981),
            colors = colors,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StatCardMini(
    label: String,
    value: String,
    icon: ImageVector,
    color: Color,
    colors: DashboardColors,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(colors.card)
            .border(1.dp, colors.divider, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            }
            Column {
                Text(text = value, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = colors.textPrimary)
                Text(text = label, style = MaterialTheme.typography.labelSmall, color = colors.textMuted)
            }
        }
    }
}

@Composable
private fun CategoryFilterChips(
    categories: List<Category>,
    selectedCategoryId: String?,
    onCategoryChange: (String?) -> Unit,
    colors: DashboardColors
) {
    Row(
        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FilterChip(
            selected = selectedCategoryId == null,
            onClick = { onCategoryChange(null) },
            label = { Text("Toutes") },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                selectedLabelColor = MaterialTheme.colorScheme.primary,
                labelColor = colors.textMuted
            ),
            border = if (selectedCategoryId == null) 
                FilterChipDefaults.filterChipBorder(enabled = true, selected = true, borderColor = MaterialTheme.colorScheme.primary) 
            else 
                FilterChipDefaults.filterChipBorder(enabled = true, selected = false, borderColor = colors.divider)
        )
        
        categories.forEach { category ->
            FilterChip(
                selected = selectedCategoryId == category.id,
                onClick = { onCategoryChange(category.id) },
                label = { Text(category.name) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = category.color.copy(alpha = 0.1f),
                    selectedLabelColor = MaterialTheme.colorScheme.primary,
                    labelColor = colors.textMuted
                ),
                border = if (selectedCategoryId == category.id) 
                    FilterChipDefaults.filterChipBorder(enabled = true, selected = true, borderColor = category.color) 
                else 
                    FilterChipDefaults.filterChipBorder(enabled = true, selected = false, borderColor = colors.divider)
            )
        }
    }
}

// Removed redundant SubjectViewModeToggle

@Composable
private fun ToggleItem(
    selected: Boolean,
    onClick: () -> Unit,
    label: String,
    icon: ImageVector,
    colors: DashboardColors
) {
    val bg = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent
    val contentColor = if (selected) Color.White else colors.textMuted
    
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp), tint = contentColor)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label, color = contentColor, style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
    }
}

@Composable
private fun EmptySubjectsState(colors: DashboardColors) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(Icons.Default.SearchOff, contentDescription = null, modifier = Modifier.size(64.dp), tint = colors.textMuted)
            Text("Aucune matière trouvée", style = MaterialTheme.typography.titleMedium, color = colors.textPrimary)
            Text("Essayez de modifier votre recherche ou vos filtres.", color = colors.textMuted)
        }
    }
}
