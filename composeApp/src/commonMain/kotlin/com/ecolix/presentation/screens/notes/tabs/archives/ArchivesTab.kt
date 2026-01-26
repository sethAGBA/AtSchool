package com.ecolix.presentation.screens.notes.tabs.archives

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.ecolix.presentation.screens.notes.GradesScreenModel
import com.ecolix.data.models.GradesUiState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ecolix.presentation.components.*

@Composable
fun ArchivesView(screenModel: GradesScreenModel, isCompact: Boolean) {
    val state by screenModel.state.collectAsState()
    val filteredYears = screenModel.getFilteredArchives()
    val paginatedYears = screenModel.getPaginatedArchives()
    val totalPages = (filteredYears.size + state.itemsPerPage - 1) / state.itemsPerPage
    
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        if (isCompact) {
            item {
                MobileHeaderItems(state, onStateChange = { screenModel.updateState(it) })
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
        
        items(paginatedYears) { year ->
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

        if (filteredYears.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Search, null, modifier = Modifier.size(48.dp), tint = state.colors.textMuted.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Aucune archive trouvée pour cette recherche.", color = state.colors.textMuted)
                    }
                }
            }
        }

        if (totalPages > 1) {
            item {
                PaginationControls(
                    currentPage = state.archivesPage,
                    totalPages = totalPages,
                    onPageChange = { page -> screenModel.onPageChange(com.ecolix.data.models.GradesViewMode.ARCHIVES, page) },
                    colors = state.colors
                )
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
