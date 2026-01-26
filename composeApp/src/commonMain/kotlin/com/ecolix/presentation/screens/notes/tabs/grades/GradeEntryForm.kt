package com.ecolix.presentation.screens.notes.tabs.grades

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.ecolix.data.models.EvaluationTemplate
import com.ecolix.data.models.EvaluationType
import com.ecolix.data.models.GradesUiState
import com.ecolix.data.models.Student
import com.ecolix.presentation.components.CardContainer
import androidx.compose.material3.HorizontalDivider
import com.ecolix.presentation.components.TagPill
import com.ecolix.presentation.components.SearchableDropdownMenu

@Composable
fun GradeEntryForm(
    state: GradesUiState,
    students: List<Student>,
    isCompact: Boolean,
    onBack: () -> Unit,
    onSave: (EvaluationTemplate, String, Map<String, String>) -> Unit,
    onClassSelected: (String) -> Unit,
    onScrollToEnd: () -> Unit
) {
    val listState = rememberLazyListState()
    
    // Detect scroll to end
    val endOfListReached by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val visibleItemsInfo = layoutInfo.visibleItemsInfo
            if (layoutInfo.totalItemsCount == 0) {
                false
            } else {
                val lastVisibleItem = visibleItemsInfo.lastOrNull()
                lastVisibleItem != null && lastVisibleItem.index >= layoutInfo.totalItemsCount - 2
            }
        }
    }

    LaunchedEffect(endOfListReached) {
        if (endOfListReached && students.size > state.studentsLoadedCount) {
            onScrollToEnd()
        }
    }
    
    var selectedClass by remember { mutableStateOf(state.classrooms.first { it != "Toutes les classes" }) }
    
    // Trigger onClassSelected when selectedClass changes
    LaunchedEffect(selectedClass) {
        onClassSelected(selectedClass)
    }

    // SLICE STUDENTS FOR LAZY LOADING
    val visibleStudents = remember(students, state.studentsLoadedCount) {
        students.take(state.studentsLoadedCount)
    }
    
    // FILTERED TEMPLATES: Only show templates for the selected class
    val classTemplates = state.templates.filter { it.className == selectedClass }
    var selectedTemplate by remember(selectedClass) { 
        mutableStateOf(classTemplates.firstOrNull() ?: EvaluationTemplate("DEF", selectedClass, EvaluationType.DEVOIR, "Devoir", 20f, 1f))
    }
    
    var date by remember { mutableStateOf("24 Jan 2026") }
    
    // Grade storage state
    val grades = remember { mutableStateMapOf<String, String>() }

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
            // Using sliced visibleStudents list
            LazyColumn(
                state = listState,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(visibleStudents) { student ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("${student.firstName} ${student.lastName}", color = state.colors.textPrimary, modifier = Modifier.weight(1f))
                        OutlinedTextField(
                            value = grades[student.id] ?: "",
                            onValueChange = { input -> 
                                if (input.isEmpty()) {
                                    grades[student.id] = ""
                                } else {
                                    // Only allow digits and decimal point
                                    val cleaned = input.filter { it.isDigit() || it == '.' }
                                    val value = cleaned.toFloatOrNull()
                                    if (value != null && value <= selectedTemplate.maxValue) {
                                        grades[student.id] = cleaned
                                    } else if (cleaned.isEmpty()) {
                                        grades[student.id] = ""
                                    }
                                }
                            },
                            placeholder = { Text("0.0") },
                            modifier = Modifier.width(80.dp),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true,
                            textStyle = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = state.colors.divider
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        Text("/ ${selectedTemplate.maxValue.toInt()}", modifier = Modifier.padding(start = 8.dp).width(40.dp), color = state.colors.textMuted)
                    }
                    HorizontalDivider(color = state.colors.divider)
                }
                
                if (students.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(48.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                Icons.Default.Info, 
                                null, 
                                modifier = Modifier.size(48.dp), 
                                tint = state.colors.textMuted.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "Aucun élève trouvé dans cette classe.",
                                color = state.colors.textMuted,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                } else if (students.size > state.studentsLoadedCount) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
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
                onClick = {
                    onSave(selectedTemplate, date, grades)
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = Color.White),
                modifier = Modifier.height(48.dp).padding(horizontal = 24.dp)
            ) {
                Text("Enregistrer la Session")
            }
        }
    }
}
