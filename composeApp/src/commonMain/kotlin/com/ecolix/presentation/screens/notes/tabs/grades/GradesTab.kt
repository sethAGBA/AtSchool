package com.ecolix.presentation.screens.notes.tabs.grades

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.ecolix.presentation.screens.notes.GradesScreenModel
import com.ecolix.data.models.GradesUiState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ecolix.presentation.components.*

@Composable
fun GradesListView(
    screenModel: GradesScreenModel,
    isCompact: Boolean,
    onAddGrade: () -> Unit,
    onOpenConfig: () -> Unit
) {
    val state by screenModel.state.collectAsState()
    val filteredSessions = screenModel.getFilteredSessions()
    val paginatedSessions = screenModel.getPaginatedSessions()
    val totalPages = (filteredSessions.size + state.itemsPerPage - 1) / state.itemsPerPage
    
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        if (isCompact) {
            item {
                MobileHeaderItems(
                    state = state, 
                    onStateChange = { screenModel.updateState(it) },
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
                    "${filteredSessions.size} session(s) trouvée(s)",
                    style = MaterialTheme.typography.labelSmall,
                    color = state.colors.textMuted
                )
            }
        }

        items(paginatedSessions) { session ->
            EvaluationSessionCard(session, state.colors, isCompact = isCompact, onClick = onAddGrade)
        }
        
        if (filteredSessions.isEmpty()) {
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

        if (totalPages > 1) {
            item {
                PaginationControls(
                    currentPage = state.notesPage,
                    totalPages = totalPages,
                    onPageChange = { screenModel.onPageChange(com.ecolix.data.models.GradesViewMode.NOTES, it) },
                    colors = state.colors
                )
            }
        }
    }
}
