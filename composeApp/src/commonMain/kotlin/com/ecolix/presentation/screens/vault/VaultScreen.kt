package com.ecolix.presentation.screens.vault

import androidx.compose.animation.*
import androidx.compose.foundation.background
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

@Composable
fun VaultScreenContent(isDarkMode: Boolean) {
    val screenModel = remember { VaultScreenModel() }
    val state by screenModel.state.collectAsState()

    LaunchedEffect(isDarkMode) {
        screenModel.onDarkModeChange(isDarkMode)
    }

    AnimatedContent(
        targetState = state.isLocked,
        transitionSpec = { fadeIn() togetherWith fadeOut() }
    ) { isLocked ->
        if (isLocked) {
            VaultLockScreen(state, screenModel)
        } else {
            VaultDashboard(state, screenModel)
        }
    }
}

@Composable
private fun VaultLockScreen(state: VaultUiState, screenModel: VaultScreenModel) {
    Box(
        modifier = Modifier.fillMaxSize().background(state.colors.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.width(IntrinsicSize.Min)
        ) {
            Icon(
                Icons.Default.Lock,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "Mode Coffre-fort",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = state.colors.textPrimary
                )
                Text(
                    "Veuillez entrer votre code PIN de sécurité",
                    style = MaterialTheme.typography.bodyMedium,
                    color = state.colors.textMuted
                )
            }

            // PIN Dots
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                repeat(4) { index ->
                    val isFilled = state.pinBuffer.length > index
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(if (isFilled) MaterialTheme.colorScheme.primary else state.colors.divider)
                    )
                }
            }

            if (state.errorMessage != null) {
                Text(state.errorMessage, color = Color(0xFFEF4444), style = MaterialTheme.typography.bodySmall)
            }

            // PIN Pad
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                val digits = listOf(
                    listOf("1", "2", "3"),
                    listOf("4", "5", "6"),
                    listOf("7", "8", "9"),
                    listOf("", "0", "DEL")
                )

                digits.forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        row.forEach { digit ->
                            if (digit.isEmpty()) {
                                Spacer(modifier = Modifier.size(64.dp))
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(CircleShape)
                                        .background(state.colors.card)
                                        .clickable { 
                                            if (digit == "DEL") screenModel.onBackspace() else screenModel.onPinInput(digit) 
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (digit == "DEL") {
                                        Icon(Icons.Default.Backspace, contentDescription = null, tint = state.colors.textPrimary)
                                    } else {
                                        Text(digit, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = state.colors.textPrimary)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun VaultDashboard(state: VaultUiState, screenModel: VaultScreenModel) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Shield, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(28.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "Espace Sécurisé",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = state.colors.textPrimary
                    )
                }
                Text(
                    "Contenu confidentiel et sensible",
                    style = MaterialTheme.typography.bodyMedium,
                    color = state.colors.textMuted
                )
            }

            Button(
                onClick = { screenModel.lockVault() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE11D48))
            ) {
                Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Verrouiller")
            }
        }

        // Search and Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = state.colors.card)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Search, contentDescription = null, tint = state.colors.textMuted)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Rechercher dans le coffre...", color = state.colors.textMuted)
                }
            }
            
            IconButton(
                onClick = {},
                colors = IconButtonDefaults.iconButtonColors(containerColor = state.colors.card)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = state.colors.textPrimary)
            }
        }

        // Items Grid
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 240.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(state.assets) { item ->
                VaultItemCard(item, state.colors)
            }
        }
    }
}

@Composable
private fun VaultItemCard(item: VaultItem, colors: DashboardColors) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { /* Action */ },
        colors = CardDefaults.cardColors(containerColor = colors.card)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Box(
                    modifier = Modifier.size(44.dp).clip(RoundedCornerShape(8.dp)).background(colors.background),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (item.type) {
                            VaultItemType.FOLDER -> Icons.Default.Folder
                            VaultItemType.DOCUMENT -> Icons.Default.Description
                            VaultItemType.KEY -> Icons.Default.VpnKey
                            VaultItemType.RECORD -> Icons.Default.BarChart
                        },
                        contentDescription = null,
                        tint = when (item.sensitivity) {
                            VaultSensitivity.TOP_SECRET -> Color(0xFFE11D48)
                            VaultSensitivity.SECRET -> Color(0xFFF59E0B)
                            VaultSensitivity.CONFIDENTIAL -> Color(0xFF3B82F6)
                        }
                    )
                }
                
                Icon(Icons.Default.MoreVert, contentDescription = null, tint = colors.textMuted)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(item.name, fontWeight = FontWeight.Bold, color = colors.textPrimary, maxLines = 1)
            Text(
                when (item.sensitivity) {
                    VaultSensitivity.TOP_SECRET -> "Très Secret"
                    VaultSensitivity.SECRET -> "Secret"
                    VaultSensitivity.CONFIDENTIAL -> "Confidentiel"
                },
                style = MaterialTheme.typography.labelSmall,
                color = when (item.sensitivity) {
                    VaultSensitivity.TOP_SECRET -> Color(0xFFE11D48)
                    VaultSensitivity.SECRET -> Color(0xFFF59E0B)
                    VaultSensitivity.CONFIDENTIAL -> Color(0xFF3B82F6)
                }
            )

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = colors.divider.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(item.size ?: "", style = MaterialTheme.typography.labelSmall, color = colors.textMuted)
                Text(item.lastAccessed ?: "", style = MaterialTheme.typography.labelSmall, color = colors.textMuted)
            }
        }
    }
}
