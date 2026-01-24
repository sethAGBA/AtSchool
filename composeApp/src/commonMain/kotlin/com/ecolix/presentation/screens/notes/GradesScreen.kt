package com.ecolix.presentation.screens.notes

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import com.ecolix.data.models.EvaluationTemplate
import com.ecolix.data.models.GradesUiState
import com.ecolix.data.models.GradesViewMode
import com.ecolix.data.models.PeriodMode
import com.ecolix.data.models.EvaluationType
import com.ecolix.presentation.components.*

@Composable
fun GradesScreenContent(isDarkMode: Boolean) {
    var state by remember { mutableStateOf(GradesUiState.sample(isDarkMode)) }

    LaunchedEffect(isDarkMode) {
        state = state.copy(isDarkMode = isDarkMode)
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val screenWidth = maxWidth
        val isCompact = screenWidth < 700.dp
        
        if (isCompact) {
            // Mobile: Sub-views handle their own scrollable headers
            Box(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                AnimatedContent(
                    targetState = state.viewMode,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    modifier = Modifier.fillMaxSize()
                ) { mode ->
                    when (mode) {
                        GradesViewMode.NOTES -> GradesListView(
                            state = state,
                            isCompact = true,
                            onAddGrade = { state = state.copy(viewMode = GradesViewMode.GRADE_FORM) },
                            onOpenConfig = { state = state.copy(viewMode = GradesViewMode.CONFIG) },
                            onStateChange = { state = it }
                        )
                        GradesViewMode.BULLETINS -> BulletinsListView(
                            state = state,
                            isCompact = true,
                            onStateChange = { state = it }
                        )
                        GradesViewMode.ARCHIVES -> ArchivesView(
                            state = state,
                            isCompact = true,
                            onStateChange = { state = it }
                        )
                        GradesViewMode.GRADE_FORM -> GradeEntryForm(
                            state = state,
                            isCompact = true,
                            onBack = { state = state.copy(viewMode = GradesViewMode.NOTES) },
                            onSave = { state = state.copy(viewMode = GradesViewMode.NOTES) }
                        )
                        GradesViewMode.CONFIG -> EvaluationConfigView(
                            state = state,
                            onBack = { state = state.copy(viewMode = GradesViewMode.NOTES) },
                            onTemplatesUpdated = { updated -> state = state.copy(templates = updated) }
                        )
                    }
                }
            }
        } else {
            // Desktop: Fixed layout with scrollable content area
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header (Desktop)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Notes & Bulletins",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = if (screenWidth < 1000.dp) 24.sp else 28.sp
                            ),
                            color = state.colors.textPrimary
                        )
                        Text(
                            text = "Suivi des performances et gestion des résultats académiques",
                            style = MaterialTheme.typography.bodyMedium,
                            color = state.colors.textMuted
                        )
                    }
                    
                    GradesViewToggle(
                        currentMode = state.viewMode,
                        onModeChange = { state = state.copy(viewMode = it) },
                        colors = state.colors
                    )
                }

                // Summary (Desktop)
                AnimatedVisibility(visible = state.viewMode == GradesViewMode.NOTES && state.searchQuery.isEmpty()) {
                    AcademicSummaryCards(summary = state.summary, colors = state.colors, isCompact = false)
                }

                // Filters (Desktop)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SearchBar(
                        query = state.searchQuery,
                        onQueryChange = { state = state.copy(searchQuery = it) },
                        colors = state.colors,
                        modifier = Modifier.width(300.dp)
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PeriodModeToggle(
                            currentMode = state.periodMode,
                            onModeChange = { state = state.copy(periodMode = it, currentPeriod = if (it == PeriodMode.TRIMESTRE) "1er Trimestre" else "1er Semestre") },
                            colors = state.colors
                        )

                        SpecificPeriodSelector(
                            currentPeriod = state.currentPeriod,
                            onPeriodChange = { state = state.copy(currentPeriod = it) },
                            periodMode = state.periodMode,
                            colors = state.colors
                        )
                    }
                }

                // Content Area (Desktop)
                Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    AnimatedContent(
                        targetState = state.viewMode,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        modifier = Modifier.fillMaxSize()
                    ) { mode ->
                        when (mode) {
                            GradesViewMode.NOTES -> GradesListView(
                                state = state,
                                isCompact = false,
                                onAddGrade = { state = state.copy(viewMode = GradesViewMode.GRADE_FORM) },
                                onOpenConfig = { state = state.copy(viewMode = GradesViewMode.CONFIG) },
                                onStateChange = { state = it }
                            )
                            GradesViewMode.BULLETINS -> BulletinsListView(state, false, { state = it })
                            GradesViewMode.ARCHIVES -> ArchivesView(state, false, { state = it })
                            GradesViewMode.GRADE_FORM -> GradeEntryForm(
                                state = state,
                                isCompact = false,
                                onBack = { state = state.copy(viewMode = GradesViewMode.NOTES) },
                                onSave = { state = state.copy(viewMode = GradesViewMode.NOTES) }
                            )
                            GradesViewMode.CONFIG -> EvaluationConfigView(
                                state = state,
                                onBack = { state = state.copy(viewMode = GradesViewMode.NOTES) },
                                onTemplatesUpdated = { updated: List<EvaluationTemplate> -> state = state.copy(templates = updated) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MobileHeaderItems(
    state: GradesUiState,
    onStateChange: (GradesUiState) -> Unit,
    onAddGrade: () -> Unit = {},
    onOpenConfig: () -> Unit = {}
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Title & Description
        Column {
            Text(
                text = "Notes & Bulletins",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = state.colors.textPrimary
            )
            Text(
                text = "Suivi des performances académiques",
                style = MaterialTheme.typography.bodySmall,
                color = state.colors.textMuted
            )
        }
        
        GradesViewToggle(
            currentMode = state.viewMode,
            onModeChange = { onStateChange(state.copy(viewMode = it)) },
            colors = state.colors,
            modifier = Modifier.fillMaxWidth(),
            isFullWidth = true
        )
        
        // Actions Section (Only for NOTES mode)
        if (state.viewMode == GradesViewMode.NOTES) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                GradesActionBar(
                    onAddGradeClick = onAddGrade,
                    onExportClick = { /* TODO: Export logic */ },
                    onImportClick = { /* TODO: Import logic */ },
                    onFilterClick = { /* TODO: Filter logic */ },
                    colors = state.colors,
                    modifier = Modifier.fillMaxWidth(),
                    isCompact = true
                )
                
                OutlinedButton(
                    onClick = onOpenConfig,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, state.colors.divider)
                ) {
                    Icon(Icons.Default.Settings, null, tint = state.colors.textPrimary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Paramètres d'évaluation", color = state.colors.textPrimary, fontSize = 14.sp)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // Summary
        AnimatedVisibility(visible = state.viewMode == GradesViewMode.NOTES && state.searchQuery.isEmpty()) {
            AcademicSummaryCards(summary = state.summary, colors = state.colors, isCompact = true)
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // Search & Filters
        SearchBar(
            query = state.searchQuery,
            onQueryChange = { onStateChange(state.copy(searchQuery = it)) },
            colors = state.colors,
            modifier = Modifier.fillMaxWidth()
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(modifier = Modifier.weight(1f)) {
                PeriodModeToggle(
                    currentMode = state.periodMode,
                    onModeChange = { onStateChange(state.copy(periodMode = it, currentPeriod = if (it == PeriodMode.TRIMESTRE) "1er Trimestre" else "1er Semestre")) },
                    colors = state.colors,
                    modifier = Modifier.fillMaxWidth(),
                    isFullWidth = true
                )
            }
            
            var showPeriodMenu by remember { mutableStateOf(false) }
            val periods = if (state.periodMode == PeriodMode.TRIMESTRE) 
                listOf("1er Trimestre", "2e Trimestre", "3e Trimestre")
            else 
                listOf("1er Semestre", "2e Semestre")

            OutlinedButton(
                onClick = { showPeriodMenu = true },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f).height(48.dp),
                contentPadding = PaddingValues(horizontal = 8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, state.colors.divider)
            ) {
                Text(state.currentPeriod, color = state.colors.textPrimary, fontSize = 12.sp, maxLines = 1)
                Icon(Icons.Default.ArrowDropDown, null, tint = state.colors.textMuted, modifier = Modifier.size(16.dp))
                
                DropdownMenu(
                    expanded = showPeriodMenu,
                    onDismissRequest = { showPeriodMenu = false },
                    modifier = Modifier.background(state.colors.card)
                ) {
                    periods.forEach { period ->
                        DropdownMenuItem(
                            text = { Text(period, color = state.colors.textPrimary) },
                            onClick = { 
                                onStateChange(state.copy(currentPeriod = period))
                                showPeriodMenu = false 
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GradesListView(
    state: GradesUiState, 
    isCompact: Boolean,
    onAddGrade: () -> Unit,
    onOpenConfig: () -> Unit,
    onStateChange: (GradesUiState) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        if (isCompact) {
            item {
                MobileHeaderItems(
                    state = state, 
                    onStateChange = onStateChange,
                    onAddGrade = onAddGrade,
                    onOpenConfig = onOpenConfig
                )
            }
        } else {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GradesActionBar(
                        onAddGradeClick = onAddGrade,
                        onExportClick = { /* TODO: Export logic */ },
                        onImportClick = { /* TODO: Import logic */ },
                        onFilterClick = { /* TODO: Filter logic */ },
                        colors = state.colors,
                        modifier = Modifier.weight(1f)
                    )
                    
                    IconButton(
                        onClick = onOpenConfig,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(state.colors.card)
                            .border(1.dp, state.colors.divider, RoundedCornerShape(12.dp))
                    ) {
                        Icon(Icons.Default.Settings, null, tint = state.colors.textPrimary)
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Sessions d'Evaluation",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = state.colors.textPrimary
                )
                Text(
                    "${state.sessions.size} session(s) trouvée(s)",
                    style = MaterialTheme.typography.labelSmall,
                    color = state.colors.textMuted
                )
            }
        }

        items(state.sessions) { session ->
            EvaluationSessionCard(session, state.colors, isCompact = isCompact, onClick = onAddGrade)
        }
        
        if (state.sessions.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Inbox, null, modifier = Modifier.size(48.dp), tint = state.colors.textMuted.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Aucune session trouvée.", color = state.colors.textMuted)
                    }
                }
            }
        }
    }
}

@Composable
private fun BulletinsListView(state: GradesUiState, isCompact: Boolean, onStateChange: (GradesUiState) -> Unit) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        if (isCompact) {
            item {
                MobileHeaderItems(state, onStateChange)
            }
        }
        
        item {
            if (isCompact) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        "Bulletins du Trimestre",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = state.colors.textPrimary
                    )
                    Button(
                        onClick = { },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981), contentColor = Color.White),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.AutoAwesome, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Générer Tout", fontSize = 14.sp)
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Bulletins du Trimestre",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = state.colors.textPrimary
                    )
                    Button(
                        onClick = { },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981), contentColor = Color.White),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.AutoAwesome, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Générer Tout", fontSize = 12.sp)
                    }
                }
            }
        }

        items(state.bulletins) { bulletin ->
            BulletinItemCard(bulletin, state.colors, isCompact = isCompact)
        }
    }
}

@Composable
private fun GradeEntryForm(
    state: GradesUiState,
    isCompact: Boolean,
    onBack: () -> Unit,
    onSave: () -> Unit
) {
    var selectedClass by remember { mutableStateOf(state.classrooms.first { it != "Toutes les classes" }) }
    
    // FILTERED TEMPLATES: Only show templates for the selected class
    val classTemplates = state.templates.filter { it.className == selectedClass }
    var selectedTemplate by remember(selectedClass) { 
        mutableStateOf(classTemplates.firstOrNull() ?: EvaluationTemplate("DEF", selectedClass, EvaluationType.DEVOIR, "Devoir", 20f, 1f))
    }
    
    var date by remember { mutableStateOf("24 Jan 2026") }

    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // COMPACT HEADER
        CardContainer(containerColor = state.colors.card) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = state.colors.textPrimary, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Nouvelle Session", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = state.colors.textPrimary)
                    Spacer(modifier = Modifier.weight(1f))
                    
                    TagPill(date, state.colors.textMuted)
                }

                if (isCompact) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        // Class Selector (Searchable)
                        var showClassMenu by remember { mutableStateOf(false) }
                        OutlinedButton(
                            onClick = { showClassMenu = true },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, state.colors.divider)
                        ) {
                            Icon(Icons.Default.School, null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(selectedClass, style = MaterialTheme.typography.bodyMedium, color = state.colors.textPrimary)
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(Icons.Default.ExpandMore, null, tint = state.colors.textMuted)
                        }
                        SearchableDropdownMenu(
                            expanded = showClassMenu,
                            onDismissRequest = { showClassMenu = false },
                            options = state.classrooms.filter { it != "Toutes les classes" },
                            onSelect = { 
                                selectedClass = it
                                showClassMenu = false 
                            },
                            colors = state.colors
                        )

                        // Template Selector
                        var showTemplateMenu by remember { mutableStateOf(false) }
                        OutlinedButton(
                            onClick = { showTemplateMenu = true },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, state.colors.divider)
                        ) {
                            Icon(Icons.Default.Tune, null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(selectedTemplate.label, style = MaterialTheme.typography.bodyMedium, color = state.colors.textPrimary)
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(Icons.Default.ExpandMore, null, tint = state.colors.textMuted)
                        }
                        DropdownMenu(
                            expanded = showTemplateMenu, 
                            onDismissRequest = { showTemplateMenu = false },
                            modifier = Modifier.background(state.colors.card).width(280.dp)
                        ) {
                            classTemplates.forEach { template ->
                                DropdownMenuItem(
                                    text = { Text(template.label, color = state.colors.textPrimary) },
                                    onClick = { 
                                        selectedTemplate = template
                                        showTemplateMenu = false 
                                    }
                                )
                            }
                        }

                        // Subject Selector (Searchable)
                        var showSubjectMenu by remember { mutableStateOf(false) }
                        var selectedSubject by remember { mutableStateOf(state.subjects.first { it != "Toutes les matières" }) }
                        OutlinedButton(
                            onClick = { showSubjectMenu = true }, 
                            modifier = Modifier.fillMaxWidth().height(48.dp), 
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, state.colors.divider)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.MenuBook, null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(selectedSubject, style = MaterialTheme.typography.bodyMedium, color = state.colors.textPrimary)
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(Icons.Default.ExpandMore, null, tint = state.colors.textMuted)
                        }
                        SearchableDropdownMenu(
                            expanded = showSubjectMenu,
                            onDismissRequest = { showSubjectMenu = false },
                            options = state.subjects.filter { it != "Toutes les matières" },
                            onSelect = { 
                                selectedSubject = it
                                showSubjectMenu = false 
                            },
                            colors = state.colors
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Class Selector (Searchable)
                        var showClassMenu by remember { mutableStateOf(false) }
                        Box(modifier = Modifier.weight(1f)) {
                            OutlinedButton(
                                onClick = { showClassMenu = true },
                                modifier = Modifier.fillMaxWidth().height(44.dp),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, state.colors.divider)
                            ) {
                                Icon(Icons.Default.School, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(selectedClass, style = MaterialTheme.typography.bodySmall, color = state.colors.textPrimary, maxLines = 1)
                            }
                            SearchableDropdownMenu(
                                expanded = showClassMenu,
                                onDismissRequest = { showClassMenu = false },
                                options = state.classrooms.filter { it != "Toutes les classes" },
                                onSelect = { 
                                    selectedClass = it
                                    showClassMenu = false 
                                },
                                colors = state.colors
                            )
                        }

                        // Template Selector
                        var showTemplateMenu by remember { mutableStateOf(false) }
                        Box(modifier = Modifier.weight(1.5f)) {
                            OutlinedButton(
                                onClick = { showTemplateMenu = true },
                                modifier = Modifier.fillMaxWidth().height(44.dp),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, state.colors.divider)
                            ) {
                                Icon(Icons.Default.Tune, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(selectedTemplate.label, style = MaterialTheme.typography.bodySmall, color = state.colors.textPrimary, maxLines = 1)
                                Spacer(modifier = Modifier.weight(1f))
                                Icon(Icons.Default.ExpandMore, null, tint = state.colors.textMuted, modifier = Modifier.size(16.dp))
                            }
                            
                            DropdownMenu(
                                expanded = showTemplateMenu, 
                                onDismissRequest = { showTemplateMenu = false },
                                modifier = Modifier.background(state.colors.card).width(240.dp)
                            ) {
                                classTemplates.forEach { template ->
                                    DropdownMenuItem(
                                        text = { 
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    if (template.type == EvaluationType.COMPOSITION) Icons.Default.Stars else Icons.Default.Edit,
                                                    null,
                                                    modifier = Modifier.size(14.dp),
                                                    tint = if (template.type == EvaluationType.COMPOSITION) Color(0xFFF59E0B) else state.colors.textMuted
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(template.label, color = state.colors.textPrimary)
                                            }
                                        },
                                        onClick = { 
                                            selectedTemplate = template
                                            showTemplateMenu = false 
                                        },
                                        colors = MenuDefaults.itemColors(textColor = state.colors.textPrimary)
                                    )
                                }
                            }
                        }

                        // Subject Selector (Searchable)
                        var showSubjectMenu by remember { mutableStateOf(false) }
                        var selectedSubject by remember { mutableStateOf(state.subjects.first { it != "Toutes les matières" }) }
                        Box(modifier = Modifier.weight(1.2f)) {
                            OutlinedButton(
                                onClick = { showSubjectMenu = true }, 
                                modifier = Modifier.fillMaxWidth().height(44.dp), 
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, state.colors.divider)
                            ) {
                                Text(selectedSubject, style = MaterialTheme.typography.bodySmall, color = state.colors.textPrimary, maxLines = 1)
                            }
                            SearchableDropdownMenu(
                                expanded = showSubjectMenu,
                                onDismissRequest = { showSubjectMenu = false },
                                options = state.subjects.filter { it != "Toutes les matières" },
                                onSelect = { 
                                    selectedSubject = it
                                    showSubjectMenu = false 
                                },
                                colors = state.colors
                            )
                        }
                    }
                }

                // Config info
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(state.colors.background)
                        .padding(horizontal = 8.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Coeff: ${selectedTemplate.coefficient.toInt()}", style = MaterialTheme.typography.labelSmall, color = state.colors.textMuted)
                }
            }
        }

        Text("Saisie des Notes", fontWeight = FontWeight.Bold, color = state.colors.textPrimary)

        // Bulk Entry List
        CardContainer(containerColor = state.colors.card, modifier = Modifier.weight(1f)) {
            val sampleStudents = listOf("Seth Kouamé", "Awa Diop", "Koffi Mensah", "Bamba Drissa")
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(sampleStudents) { student ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(student, color = state.colors.textPrimary, modifier = Modifier.weight(1f))
                        OutlinedTextField(
                            value = "",
                            onValueChange = {},
                            placeholder = { Text("0.0") },
                            modifier = Modifier.width(80.dp),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true,
                            textStyle = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = state.colors.divider
                            )
                        )
                        Text("/ ${selectedTemplate.maxValue.toInt()}", modifier = Modifier.padding(start = 8.dp).width(40.dp), color = state.colors.textMuted)
                    }
                    HorizontalDivider(color = state.colors.divider)
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBack) { Text("Annuler") }
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = onSave,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = Color.White),
                modifier = Modifier.height(48.dp).padding(horizontal = 24.dp)
            ) {
                Text("Enregistrer la Session")
            }
        }
    }
}

@Composable
private fun ArchivesView(state: GradesUiState, isCompact: Boolean, onStateChange: (GradesUiState) -> Unit) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        if (isCompact) {
            item {
                MobileHeaderItems(state, onStateChange)
            }
        }
        
        item {
            Text(
                "Archives des Bulletins",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = state.colors.textPrimary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        
        items(listOf("2024-2025", "2023-2024", "2022-2023")) { year ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(state.colors.card)
                    .clickable { }
                    .padding(if (isCompact) 16.dp else 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Folder, null, tint = state.colors.textMuted)
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("Année Scolaire $year", fontWeight = FontWeight.Bold, color = state.colors.textPrimary)
                        Text("Tous les bulletins archivés", style = MaterialTheme.typography.bodySmall, color = state.colors.textMuted)
                    }
                }
                Icon(Icons.Default.ChevronRight, null, tint = state.colors.textMuted)
            }
        }

        item {
            Box(modifier = Modifier.fillMaxWidth().padding(top = 48.dp), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.History, null, modifier = Modifier.size(48.dp), tint = state.colors.textMuted.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Plus d'archives disponibles via le serveur cloud", style = MaterialTheme.typography.bodySmall, color = state.colors.textMuted)
                }
            }
        }
    }
}
@Composable
private fun EvaluationConfigView(
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
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Barème", style = MaterialTheme.typography.labelSmall, color = state.colors.textMuted)
                                Text("/${template.maxValue.toInt()}", fontWeight = FontWeight.Bold, color = state.colors.textPrimary)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Coeff.", style = MaterialTheme.typography.labelSmall, color = state.colors.textMuted)
                                Text("x${template.coefficient.toInt()}", fontWeight = FontWeight.Bold, color = state.colors.textPrimary)
                            }
                            
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

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedTextField(
                        value = maxValue,
                        onValueChange = { if (it.all { char -> char.isDigit() }) maxValue = it },
                        label = { Text("Base / Barème") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
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
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = colors.textPrimary,
                            unfocusedTextColor = colors.textPrimary,
                            unfocusedBorderColor = colors.divider
                        )
                    )
                    OutlinedTextField(
                        value = coefficient,
                        onValueChange = { if (it.all { char -> char.isDigit() }) coefficient = it },
                        label = { Text("Coefficient") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
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
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = colors.textPrimary,
                            unfocusedTextColor = colors.textPrimary,
                            unfocusedBorderColor = colors.divider
                        )
                    )
                }
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
@Composable
private fun SearchableDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    options: List<String>,
    onSelect: (String) -> Unit,
    colors: com.ecolix.data.models.DashboardColors
) {
    var search by remember(expanded) { mutableStateOf("") }
    val filtered = options.filter { it.contains(search, ignoreCase = true) }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = Modifier.background(colors.card).width(200.dp)
    ) {
        OutlinedTextField(
            value = search,
            onValueChange = { search = it },
            placeholder = { Text("Rechercher...", fontSize = 12.sp) },
            modifier = Modifier.padding(8.dp).fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.Search, null, modifier = Modifier.size(16.dp)) },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = colors.textPrimary,
                unfocusedTextColor = colors.textPrimary,
                unfocusedBorderColor = colors.divider
            )
        )
        filtered.forEach { option ->
            DropdownMenuItem(
                text = { Text(option, color = colors.textPrimary, fontSize = 14.sp) },
                onClick = { onSelect(option) },
                colors = MenuDefaults.itemColors(textColor = colors.textPrimary)
            )
        }
        if (filtered.isEmpty()) {
            DropdownMenuItem(
                text = { Text("Aucun résultat", color = colors.textMuted, fontSize = 14.sp) },
                onClick = { }
            )
        }
    }
}

@Composable
private fun ConfirmationDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    colors: com.ecolix.data.models.DashboardColors
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = colors.card,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, color = colors.textPrimary, fontWeight = FontWeight.Bold)
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, null, tint = colors.textMuted)
                }
            }
        },
        text = { Text(message, color = colors.textPrimary) },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error, contentColor = Color.White),
                shape = RoundedCornerShape(8.dp),
            ) {
                Text("Supprimer")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler", color = colors.textMuted)
            }
        }
    )
}
