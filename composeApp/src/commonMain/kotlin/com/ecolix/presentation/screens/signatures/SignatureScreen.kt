package com.ecolix.presentation.screens.signatures

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecolix.data.models.*
import com.ecolix.presentation.components.SearchBar

@Composable
fun SignatureScreenContent(isDarkMode: Boolean) {
    val screenModel = remember { SignatureScreenModel() }
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
            verticalArrangement = Arrangement.spacedBy(if (isCompact) 16.dp else 24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Signatures & Cachets",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = if (isCompact) 24.sp else 32.sp
                        ),
                        color = state.colors.textPrimary
                    )
                    if (!isCompact) {
                        Text(
                            text = "Gestion des signatures numériques et cachets officiels",
                            style = MaterialTheme.typography.bodyMedium,
                            color = state.colors.textMuted
                        )
                    }
                }

                if (!isCompact) {
                    AssetTypeToggle(
                        selected = state.selectedType,
                        onTypeChange = { screenModel.onTypeFilterChange(it) },
                        colors = state.colors
                    )
                }
            }

            // Stats / Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                InfoStatCard("Signatures", "${state.assets.count { it.type == AssetType.SIGNATURE }}", Icons.Default.Draw, Color(0xFF3B82F6), state.colors, Modifier.weight(1f))
                InfoStatCard("Cachets", "${state.assets.count { it.type == AssetType.SEAL }}", Icons.Default.Verified, Color(0xFF10B981), state.colors, Modifier.weight(1f))
                if (!isCompact) {
                    InfoStatCard("Archivés", "${state.assets.count { it.status == AssetStatus.ARCHIVED }}", Icons.Default.Archive, Color(0xFF71717A), state.colors, Modifier.weight(1f))
                }
            }

            // Search and Tabs
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

                Button(
                    onClick = { /* Add new asset */ },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    if (!isCompact) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Ajouter")
                    }
                }
            }

            // Assets Grid
            Box(modifier = Modifier.weight(1f)) {
                val filteredAssets = remember(state.assets, state.searchQuery, state.selectedType) {
                    state.assets.filter { asset ->
                        (state.searchQuery.isEmpty() || asset.ownerName.contains(state.searchQuery, ignoreCase = true)) &&
                        (state.selectedType == null || asset.type == state.selectedType)
                    }
                }

                LazyVerticalGrid(
                    columns = if (isCompact) GridCells.Fixed(1) else GridCells.Adaptive(minSize = 300.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredAssets) { asset ->
                        AssetCard(asset, state.colors)
                    }
                }
            }
        }
    }
}

@Composable
private fun AssetCard(asset: DigitalAsset, colors: DashboardColors) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colors.card),
        border = if (asset.isDefault) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (asset.type == AssetType.SIGNATURE) Icons.Default.Draw else Icons.Default.Verified,
                        contentDescription = null,
                        tint = colors.textMuted,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (asset.type == AssetType.SIGNATURE) "Signature" else "Cachet",
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.textMuted
                    )
                }
                
                if (asset.isDefault) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("Par défaut", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Asset Preview Placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (asset.status == AssetStatus.ARCHIVED) colors.divider.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.05f))
                    .border(1.dp, colors.divider.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (asset.status == AssetStatus.ARCHIVED) {
                    Icon(Icons.Default.Archive, contentDescription = null, tint = colors.textMuted.copy(alpha = 0.3f), modifier = Modifier.size(48.dp))
                } else {
                    Text(
                        text = if (asset.type == AssetType.SIGNATURE) "[ Signature Placeholder ]" else "[ Cachet Officiel ]",
                        color = colors.textMuted.copy(alpha = 0.5f),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(asset.ownerName, fontWeight = FontWeight.Bold, color = colors.textPrimary)
            Text(asset.ownerRole, style = MaterialTheme.typography.bodySmall, color = colors.textMuted)

            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ajouté le ${asset.dateAdded}",
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textMuted
                )
                
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(onClick = { /* Archive/Active */ }, modifier = Modifier.size(32.dp)) {
                        Icon(if (asset.status == AssetStatus.ACTIVE) Icons.Default.Archive else Icons.Default.Unarchive, contentDescription = null, modifier = Modifier.size(18.dp), tint = colors.textMuted)
                    }
                    IconButton(onClick = { /* Delete */ }, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color(0xFFEF4444))
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoStatCard(title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, colors: DashboardColors, modifier: Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = colors.card)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(36.dp).clip(CircleShape).background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(title, style = MaterialTheme.typography.labelSmall, color = colors.textMuted)
                Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = colors.textPrimary)
            }
        }
    }
}

@Composable
private fun AssetTypeToggle(
    selected: AssetType?,
    onTypeChange: (AssetType?) -> Unit,
    colors: DashboardColors
) {
    Row(
        modifier = Modifier
            .height(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(colors.card)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        val options = listOf(null, AssetType.SIGNATURE, AssetType.SEAL)
        options.forEach { option ->
            val isSelected = selected == option
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                    .clickable { onTypeChange(option) }
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = when (option) {
                        AssetType.SIGNATURE -> "Signatures"
                        AssetType.SEAL -> "Cachets"
                        else -> "Tout"
                    },
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = if (isSelected) Color.White else colors.textMuted
                )
            }
        }
    }
}
