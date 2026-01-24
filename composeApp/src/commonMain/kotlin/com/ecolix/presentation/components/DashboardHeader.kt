package com.ecolix.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecolix.data.models.DashboardUiState

@Composable
fun DashboardHeader(state: DashboardUiState, isWide: Boolean) {
    CardContainer(containerColor = state.colors.card) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    GradientIconBox(
                        colors = listOf(Color(0xFF6366F1), Color(0xFF8B5CF6)),
                        icon = Icons.Filled.BarChart,
                        size = if (isWide) 64.dp else 48.dp
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Tableau de bord",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 32.sp
                            ),
                            color = state.colors.textPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Gerez votre ecole avec style et efficacite",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
                            color = state.colors.textMuted
                        )
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatusPill(
                        text = "Licence active - 23j restants",
                        colors = listOf(Color(0xFF3B82F6), Color(0xFF60A5FA))
                    )
                    StatusPill(
                        text = "Annee 2024-2025",
                        colors = listOf(Color(0xFF10B981), Color(0xFF34D399))
                    )
                    IconButtonCard(icon = Icons.Filled.NotificationsNone)
                }
            }
        }
    }
}
