package com.ecolix.atschool.ui.grades

import androidx.compose.animation.*
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecolix.atschool.ui.grades.components.*
import com.ecolix.atschool.ui.grades.models.GradesUiState
import com.ecolix.atschool.ui.grades.models.GradesViewMode
import com.ecolix.atschool.ui.students.components.SearchBar

@Composable
fun GradesScreenContent(isDarkMode: Boolean) {
    var state by remember(isDarkMode) { mutableStateOf(GradesUiState.sample(isDarkMode)) }

    LaunchedEffect(isDarkMode) {
        state = state.copy(isDarkMode = isDarkMode)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Header Row
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
                        fontSize = 32.sp
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

        GradesActionBar(
            onAddGradeClick = { /* TODO */ },
            onExportClick = { /* TODO */ },
            colors = state.colors
        )

        // Search and Global Filters
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
        }

        // Main Content Area
        AnimatedContent(
            targetState = state.viewMode,
            transitionSpec = {
                fadeIn() togetherWith fadeOut()
            },
            modifier = Modifier.weight(1f).fillMaxWidth()
        ) { mode ->
            when (mode) {
                GradesViewMode.NOTES -> GradesListView(
                    state = state,
                    onAddGrade = { state = state.copy(viewMode = GradesViewMode.GRADE_FORM) }
                )
                GradesViewMode.BULLETINS -> BulletinsListView(state)
                GradesViewMode.ARCHIVES -> ArchivesView(state)
                GradesViewMode.GRADE_FORM -> GradeEntryForm(
                    state = state,
                    onBack = { state = state.copy(viewMode = GradesViewMode.NOTES) },
                    onSave = { 
                        // Logic to save grade
                        state = state.copy(viewMode = GradesViewMode.NOTES)
                    }
                )
            }
        }
    }
}

@Composable
private fun GradesListView(state: GradesUiState, onAddGrade: () -> Unit) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Dernières Evaluations",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = state.colors.textPrimary
                )
                TextButton(onClick = onAddGrade) {
                    Text("Tout voir", color = MaterialTheme.colorScheme.primary)
                }
            }
        }
        items(state.evaluations) { evaluation ->
            GradeItemCard(evaluation, state.colors)
        }
        
        if (state.evaluations.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) {
                    Text("Aucune évaluation trouvée.", color = state.colors.textMuted)
                }
            }
        }
    }
}

@Composable
private fun BulletinsListView(state: GradesUiState) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
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

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(state.bulletins) { bulletin ->
                BulletinItemCard(bulletin, state.colors)
            }
        }
    }
}

@Composable
private fun GradeEntryForm(
    state: GradesUiState,
    onBack: () -> Unit,
    onSave: () -> Unit
) {
    var studentName by remember { mutableStateOf("") }
    var gradeValue by remember { mutableStateOf("") }
    var selectedSubject by remember { mutableStateOf(state.subjects.firstOrNull() ?: "") }
    var selectedClass by remember { mutableStateOf(state.classrooms.firstOrNull() ?: "") }

    com.ecolix.atschool.ui.dashboard.components.CardContainer(containerColor = state.colors.card) {
        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = state.colors.textPrimary)
                }
                Text("Saisie de Note", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = state.colors.textPrimary)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Classe", style = MaterialTheme.typography.labelMedium, color = state.colors.textMuted)
                    Spacer(modifier = Modifier.height(8.dp))
                    // Simple Dropdown placeholder logic would go here
                    OutlinedButton(onClick = {}, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)) {
                        Text(selectedClass, color = state.colors.textPrimary)
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Matière", style = MaterialTheme.typography.labelMedium, color = state.colors.textMuted)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(onClick = {}, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)) {
                        Text(selectedSubject, color = state.colors.textPrimary)
                    }
                }
            }

            Column {
                Text("Nom de l'élève", style = MaterialTheme.typography.labelMedium, color = state.colors.textMuted)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = studentName,
                    onValueChange = { studentName = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = state.colors.divider,
                        focusedBorderColor = MaterialTheme.colorScheme.primary
                    )
                )
            }

            Column {
                Text("Note (sur 20)", style = MaterialTheme.typography.labelMedium, color = state.colors.textMuted)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = gradeValue,
                    onValueChange = { gradeValue = it },
                    modifier = Modifier.width(120.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = state.colors.divider,
                        focusedBorderColor = MaterialTheme.colorScheme.primary
                    )
                )
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
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = Color.White)
                ) {
                    Text("Enregistrer")
                }
            }
        }
    }
}

@Composable
private fun ArchivesView(state: GradesUiState) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text(
                "Archives des Années Précédentes",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = state.colors.textPrimary
            )
        }
        
        val years = listOf("2023-2024", "2022-2023", "2021-2022")
        items(years) { year ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(state.colors.card)
                    .clickable { }
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Folder, null, tint = Color(0xFFF59E0B))
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Année Scolaire $year", fontWeight = FontWeight.SemiBold, color = state.colors.textPrimary)
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
