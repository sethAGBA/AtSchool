package com.ecolix.presentation.screens.notes.tabs.bulletins

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.ecolix.presentation.screens.notes.GradesScreenModel
import com.ecolix.data.models.GradesUiState
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecolix.presentation.components.*

@Composable
fun BulletinsListView(
    screenModel: GradesScreenModel, 
    isCompact: Boolean,
    onSelectBulletin: (String) -> Unit,
    onBackFromPreview: () -> Unit,
    onExportPdf: () -> Unit,
    onGenerateAll: () -> Unit
) {
    val state by screenModel.state.collectAsState()
    val filteredBulletins = screenModel.getFilteredBulletins()
    val paginatedBulletins = screenModel.getPaginatedBulletins()
    val totalPages = (filteredBulletins.size + state.itemsPerPage - 1) / state.itemsPerPage
    
    val selectedReportCard = state.selectedReportCard
    if (selectedReportCard != null) {
        ReportCardView(
            reportCard = selectedReportCard,
            colors = state.colors,
            onBack = onBackFromPreview,
            onExportPdf = onExportPdf,
            isExporting = state.isExporting
        )
        return
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        if (isCompact) {
            item {
                MobileHeaderItems(state, onStateChange = { screenModel.updateState(it) })
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
                        onClick = onGenerateAll,
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
                        onClick = onGenerateAll,
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

        items(paginatedBulletins) { bulletin ->
            // Clickable card to open details
            Box(modifier = Modifier.clickable { onSelectBulletin(bulletin.id) }) {
                BulletinItemCard(bulletin, state.colors, isCompact = isCompact)
            }
        }

        if (filteredBulletins.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Info, null, modifier = Modifier.size(48.dp), tint = state.colors.textMuted.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Aucun bulletin trouvé pour cette classe ou période.", color = state.colors.textMuted, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    }
                }
            }
        }

        if (totalPages > 1) {
            item {
                PaginationControls(
                    currentPage = state.bulletinsPage,
                    totalPages = totalPages,
                    onPageChange = { page -> screenModel.onPageChange(com.ecolix.data.models.GradesViewMode.BULLETINS, page) },
                    colors = state.colors
                )
            }
        }
    }
}
