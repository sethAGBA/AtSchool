package com.ecolix.presentation.screens.eleves

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecolix.data.models.Classroom
import com.ecolix.data.models.DashboardColors
import com.ecolix.data.models.Student
import com.ecolix.presentation.components.CardContainer
import com.ecolix.presentation.components.StudentRow
import com.ecolix.presentation.components.TagPill
import com.ecolix.presentation.components.SearchBar

@Composable
fun ClassDetailsScreen(
    classroom: Classroom,
    students: List<Student>,
    colors: DashboardColors,
    isCompact: Boolean = false,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onStudentClick: (String) -> Unit,
    onAddStudent: () -> Unit,
    onStudentDelete: (String) -> Unit = {},
    onStudentRestoreAction: (String) -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(0) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showInUseDialog by remember { mutableStateOf(false) }
    val tabs = listOf("Infos Générales", "Élèves (${students.size})")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = if (isCompact) 16.dp else 24.dp)
            .padding(top = if (isCompact) 16.dp else 24.dp, bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(if (isCompact) 16.dp else 20.dp)
    ) {
        // Header
        if (isCompact) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = colors.textPrimary)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = classroom.name,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = colors.textPrimary
                        )
                        Text(
                            text = "${classroom.level} • ${classroom.academicYear}",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.textMuted
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = null, tint = colors.textLink)
                    }
                    IconButton(onClick = { 
                        if (classroom.studentCount > 0) showInUseDialog = true 
                        else showDeleteDialog = true 
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFEF4444))
                    }
                }
            }
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = colors.textPrimary)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Classe: ${classroom.name}",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = colors.textPrimary
                    )
                    Text(
                        text = "${classroom.level} • ${classroom.academicYear}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.textMuted
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = onEdit,
                        colors = ButtonDefaults.buttonColors(containerColor = colors.textLink, contentColor = Color.White),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Modifier", fontSize = 13.sp)
                    }
                    
                    Button(
                        onClick = { 
                            if (classroom.studentCount > 0) showInUseDialog = true 
                            else showDeleteDialog = true 
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444).copy(alpha = 0.1f), contentColor = Color(0xFFEF4444)),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.5f))
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Supprimer", fontSize = 13.sp)
                    }
                }
            }
        }

        // Dialogs
        if (showDeleteDialog) {
            com.ecolix.presentation.components.ConfirmationDialog(
                title = "Supprimer la classe",
                message = "Êtes-vous sûr de vouloir supprimer la classe \"${classroom.name}\" ? Cette action est irréversible.",
                onConfirm = {
                    showDeleteDialog = false
                    onDelete()
                },
                onDismiss = { showDeleteDialog = false },
                colors = colors
            )
        }

        if (showInUseDialog) {
            AlertDialog(
                onDismissRequest = { showInUseDialog = false },
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
                    TextButton(onClick = { showInUseDialog = false }) {
                        Text("D'accord", color = colors.textLink)
                    }
                }
            )
        }

        // Tabs
        if (isCompact) {
            SecondaryScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.primary,
                edgePadding = 0.dp,
                divider = {}
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { 
                            Text(
                                title, 
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.sp
                            ) 
                        },
                        selectedContentColor = MaterialTheme.colorScheme.primary,
                        unselectedContentColor = colors.textMuted
                    )
                }
            }
        } else {
            SecondaryTabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.primary,
                indicator = { 
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(selectedTab),
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                divider = {}
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { 
                            Text(
                                title, 
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 14.sp
                            ) 
                        },
                        selectedContentColor = MaterialTheme.colorScheme.primary,
                        unselectedContentColor = colors.textMuted
                    )
                }
            }
        }

        // Content
        Box(modifier = Modifier.weight(1f)) {
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() }
            ) { targetTab ->
                when (targetTab) {
                    0 -> ClassOverviewTab(classroom, colors, isCompact)
                    1 -> ClassStudentsTab(
                        students = students,
                        colors = colors,
                        onStudentClick = onStudentClick,
                        onAddStudent = onAddStudent,
                        onStudentDelete = onStudentDelete,
                        onStudentRestoreAction = onStudentRestoreAction
                    )
                }
            }
        }
    }
}

@Composable
private fun ClassOverviewTab(classroom: Classroom, colors: DashboardColors, isCompact: Boolean) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(bottom = 20.dp)
    ) {
        item {
            if (isCompact) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    ClassStatCard(
                        modifier = Modifier.fillMaxWidth(),
                        label = "Total Élèves",
                        value = classroom.studentCount.toString(),
                        icon = Icons.Default.People,
                        color = colors.textLink,
                        colors = colors
                    )
                    ClassStatCard(
                        modifier = Modifier.fillMaxWidth(),
                        label = "Sexe Ratio",
                        value = "${classroom.boysCount}G / ${classroom.girlsCount}F",
                        icon = Icons.Default.Wc,
                        color = Color(0xFF10B981),
                        colors = colors,
                        smallValue = true
                    )
                }
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    ClassStatCard(
                        modifier = Modifier.weight(1f),
                        label = "Total Élèves",
                        value = classroom.studentCount.toString(),
                        icon = Icons.Default.People,
                        color = colors.textLink,
                        colors = colors
                    )
                    ClassStatCard(
                        modifier = Modifier.weight(1f),
                        label = "Sexe Ratio",
                        value = "${classroom.boysCount}G / ${classroom.girlsCount}F",
                        icon = Icons.Default.Wc,
                        color = Color(0xFF10B981),
                        colors = colors,
                        smallValue = true
                    )
                }
            }
        }

        item {
            CardContainer(containerColor = colors.card) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Détails de la Classe", fontWeight = FontWeight.Bold, color = colors.textPrimary)
                    HorizontalDivider(color = colors.divider)
                    
                    ClassDetailRow(Icons.Default.Person, "Professeur Principal", classroom.mainTeacher ?: "Non assigné", colors)
                    ClassDetailRow(Icons.Default.MeetingRoom, "Salle de classe", classroom.roomNumber ?: "N/A", colors)
                    ClassDetailRow(Icons.Default.GroupAdd, "Capacité Maximale", classroom.capacity?.toString() ?: "Illimitée", colors)
                    ClassDetailRow(Icons.AutoMirrored.Filled.TrendingUp, "Moyenne Générale", "13.45 / 20", colors) // Mock
                }
            }
        }

        if (classroom.description != null) {
            item {
                CardContainer(containerColor = colors.card) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Observations / Notes", fontWeight = FontWeight.Bold, color = colors.textPrimary)
                        HorizontalDivider(color = colors.divider)
                        Text(
                            text = classroom.description!!,
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.textPrimary,
                            lineHeight = 22.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ClassStudentsTab(
    students: List<Student>, 
    colors: DashboardColors, 
    onStudentClick: (String) -> Unit,
    onAddStudent: () -> Unit,
    onStudentDelete: (String) -> Unit = {},
    onStudentRestoreAction: (String) -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var loadedCount by remember { mutableStateOf(10) }
    val batchSize = 10
    
    val filteredStudents = remember(students, searchQuery) {
        if (searchQuery.isEmpty()) students
        else students.filter { 
            "${it.firstName} ${it.lastName}".contains(searchQuery, ignoreCase = true) ||
            it.matricule?.contains(searchQuery, ignoreCase = true) == true
        }
    }
    
    val visibleStudents = remember(filteredStudents, loadedCount) {
        filteredStudents.take(loadedCount)
    }
    
    val listState = rememberLazyListState()
    
    val endReached by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val visibleItemsInfo = layoutInfo.visibleItemsInfo
            if (layoutInfo.totalItemsCount == 0) false
            else {
                val lastVisibleItem = visibleItemsInfo.lastOrNull()
                lastVisibleItem != null && lastVisibleItem.index >= layoutInfo.totalItemsCount - 1
            }
        }
    }
    
    LaunchedEffect(endReached) {
        if (endReached && students.size > loadedCount) {
            loadedCount += batchSize
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SearchBar(
                query = searchQuery,
                onQueryChange = { 
                    searchQuery = it
                    loadedCount = 10 // Reset loading when searching
                },
                colors = colors,
                modifier = Modifier.weight(1f)
            )
            
            Button(
                onClick = onAddStudent,
                colors = ButtonDefaults.buttonColors(containerColor = colors.textLink, contentColor = Color.White),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                modifier = Modifier.height(48.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Inscrire", fontSize = 13.sp)
            }
                }
        }

        LazyColumn(
            state = listState,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 20.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            if (filteredStudents.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 64.dp),
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
                                text = if (searchQuery.isNotEmpty())
                                    "Aucun élève ne correspond à votre recherche."
                                else
                                    "Aucun élève dans cette classe. Ajoutez-en via le bouton d'inscription.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = colors.textMuted,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(visibleStudents) { student ->
                    StudentRow(
                        student = student,
                        colors = colors,
                        selectionMode = false,
                        isSelected = false,
                        onToggleSelect = {},
                        onDelete = { onStudentDelete(student.id) },
                        onRestore = { onStudentRestoreAction(student.id) },
                        onClick = { onStudentClick(student.id) }
                    )
                }
                
                if (filteredStudents.size > loadedCount) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }

@Composable
private fun ClassDetailRow(icon: ImageVector, label: String, value: String, colors: DashboardColors) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp), tint = colors.textMuted)
        Spacer(modifier = Modifier.width(12.dp))
        Text(label, style = MaterialTheme.typography.bodyMedium, color = colors.textMuted, modifier = Modifier.weight(1f))
        Text(value, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = colors.textPrimary)
    }
}

@Composable
private fun ClassStatCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    icon: ImageVector,
    color: Color,
    colors: DashboardColors,
    smallValue: Boolean = false
) {
    CardContainer(containerColor = colors.card, modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = color)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = value,
                    style = if (smallValue) MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold) else MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black),
                    color = colors.textPrimary
                )
                Text(text = label, style = MaterialTheme.typography.labelSmall, color = colors.textMuted)
            }
        }
    }
}
