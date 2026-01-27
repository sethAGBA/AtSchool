package com.ecolix.presentation.screens.exports

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecolix.data.models.*
import com.ecolix.presentation.components.SearchBar

@Composable
fun ExportScreenContent(isDarkMode: Boolean) {
    val screenModel = remember { ExportScreenModel() }
    val state by screenModel.state.collectAsState()

    LaunchedEffect(isDarkMode) {
        screenModel.onDarkModeChange(isDarkMode)
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isCompact = maxWidth < 800.dp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(if (isCompact) 16.dp else 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Header
            Column {
                Text(
                    text = "Centre d'Exports",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = if (isCompact) 24.sp else 32.sp
                    ),
                    color = state.colors.textPrimary
                )
                Text(
                    text = "Générez et gérez vos documents administratifs et académiques",
                    style = MaterialTheme.typography.bodyMedium,
                    color = state.colors.textMuted
                )
            }

            // Search Bar
            SearchBar(
                query = state.searchQuery,
                onQueryChange = { screenModel.onSearchQueryChange(it) },
                colors = state.colors,
                modifier = Modifier.fillMaxWidth()
            )

            // Available Templates Section
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = "Templates Disponibles",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = state.colors.textPrimary
                )
                
                val filteredTemplates = remember(state.templates, state.searchQuery) {
                    state.templates.filter { 
                        state.searchQuery.isEmpty() || it.name.contains(state.searchQuery, ignoreCase = true)
                    }
                }

                if (isCompact) {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.heightIn(max = 400.dp)
                    ) {
                        items(filteredTemplates) { template ->
                            TemplateCard(template, state.colors, isCompact = true)
                        }
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 300.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.heightIn(max = 400.dp)
                    ) {
                        items(filteredTemplates) { template ->
                            TemplateCard(template, state.colors, isCompact = false)
                        }
                    }
                }
            }

            Divider(color = state.colors.divider)

            // Recent Exports Section
            Column(verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.weight(1f)) {
                Text(
                    text = "Exports Récents",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = state.colors.textPrimary
                )

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(state.recentExports, key = { it.id }) { job ->
                        ExportJobCard(job, state.colors, isCompact)
                    }
                }
            }
        }
    }
}

@Composable
private fun TemplateCard(template: ExportTemplate, colors: DashboardColors, isCompact: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colors.card),
        border = androidx.compose.foundation.BorderStroke(1.dp, colors.divider.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = template.type.getIcon(),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = template.name,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = colors.textPrimary
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = template.description,
                style = MaterialTheme.typography.bodySmall,
                color = colors.textMuted,
                minLines = 2,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                template.supportedFormats.forEach { format ->
                    Button(
                        onClick = { /* Generate */ },
                        modifier = Modifier.weight(1f).height(36.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(format.name, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}

@Composable
private fun ExportJobCard(job: ExportJob, colors: DashboardColors, isCompact: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colors.card),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = when (job.format) {
                    ExportFormat.PDF -> Icons.Default.PictureAsPdf
                    ExportFormat.EXCEL -> Icons.Default.TableChart
                    else -> Icons.Default.Article
                },
                contentDescription = null,
                tint = when (job.status) {
                    ExportStatus.COMPLETED -> Color(0xFF10B981)
                    ExportStatus.FAILED -> Color(0xFFEF4444)
                    else -> colors.textMuted
                },
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = job.name,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    color = colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = job.generatedAt,
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.textMuted
                    )
                    if (job.fileSize != null) {
                        Text(" • ", color = colors.textMuted)
                        Text(job.fileSize, style = MaterialTheme.typography.labelSmall, color = colors.textMuted)
                    }
                }
            }

            if (!isCompact) {
                StatusBadge(job.status, colors)
                Spacer(modifier = Modifier.width(16.dp))
            }

            if (job.status == ExportStatus.COMPLETED) {
                IconButton(onClick = { /* Download */ }) {
                    Icon(Icons.Default.Download, contentDescription = "Download", tint = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = { /* Print */ }) {
                    Icon(Icons.Default.Print, contentDescription = "Print", tint = colors.textMuted)
                }
            } else if (job.status == ExportStatus.FAILED) {
                IconButton(onClick = { /* Retry */ }) {
                    Icon(Icons.Default.Refresh, contentDescription = "Retry", tint = Color(0xFFEF4444))
                }
            } else if (job.status == ExportStatus.GENERATING) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            }
        }
    }
}

@Composable
private fun StatusBadge(status: ExportStatus, colors: DashboardColors) {
    val bgColor = when (status) {
        ExportStatus.COMPLETED -> Color(0xFF10B981).copy(alpha = 0.1f)
        ExportStatus.FAILED -> Color(0xFFEF4444).copy(alpha = 0.1f)
        ExportStatus.GENERATING -> Color(0xFFF59E0B).copy(alpha = 0.1f)
        ExportStatus.PENDING -> colors.textMuted.copy(alpha = 0.1f)
    }
    
    val textColor = when (status) {
        ExportStatus.COMPLETED -> Color(0xFF10B981)
        ExportStatus.FAILED -> Color(0xFFEF4444)
        ExportStatus.GENERATING -> Color(0xFFF59E0B)
        ExportStatus.PENDING -> colors.textMuted
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = status.toFrench(),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = textColor
        )
    }
}
