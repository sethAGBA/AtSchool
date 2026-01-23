package com.ecolix.atschool.ui.grades.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecolix.atschool.ui.dashboard.components.CardContainer
import com.ecolix.atschool.ui.dashboard.models.DashboardColors
import com.ecolix.atschool.ui.grades.models.GradesViewMode

@Composable
fun GradesViewToggle(
    currentMode: GradesViewMode,
    onModeChange: (GradesViewMode) -> Unit,
    colors: DashboardColors
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(colors.card)
            .border(1.dp, colors.divider, RoundedCornerShape(12.dp))
            .padding(4.dp)
    ) {
        GradesToggleItem(
            selected = currentMode == GradesViewMode.NOTES,
            onClick = { onModeChange(GradesViewMode.NOTES) },
            label = "Notes",
            icon = Icons.AutoMirrored.Filled.MenuBook,
            colors = colors
        )
        GradesToggleItem(
            selected = currentMode == GradesViewMode.BULLETINS,
            onClick = { onModeChange(GradesViewMode.BULLETINS) },
            label = "Bulletins",
            icon = Icons.Default.Description,
            colors = colors
        )
        GradesToggleItem(
            selected = currentMode == GradesViewMode.ARCHIVES,
            onClick = { onModeChange(GradesViewMode.ARCHIVES) },
            label = "Archives",
            icon = Icons.Default.History,
            colors = colors
        )
    }
}

@Composable
private fun GradesToggleItem(
    selected: Boolean,
    onClick: () -> Unit,
    label: String,
    icon: ImageVector,
    colors: DashboardColors
) {
    val bg = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent
    val contentColor = if (selected) Color.White else colors.textMuted
    
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp), tint = contentColor)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label, color = contentColor, style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
    }
}

@Composable
fun GradesActionBar(
    onAddGradeClick: () -> Unit,
    onExportClick: () -> Unit,
    colors: DashboardColors
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            onClick = onAddGradeClick,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            )
        ) {
            Icon(Icons.Default.Add, null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Saisir des Notes")
        }
        
        OutlinedButton(
            onClick = onExportClick,
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, colors.divider)
        ) {
            Icon(Icons.Default.FileUpload, null, tint = colors.textPrimary)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Importer", color = colors.textPrimary)
        }

        Spacer(modifier = Modifier.weight(1f))

        OutlinedButton(
            onClick = { },
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, colors.divider)
        ) {
            Icon(Icons.Default.FileDownload, null, tint = colors.textPrimary)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Exporter", color = colors.textPrimary)
        }
    }
}

@Composable
fun GradeItemCard(
    evaluation: com.ecolix.atschool.ui.grades.models.GradeEvaluation,
    colors: DashboardColors
) {
    CardContainer(containerColor = colors.card) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.MenuBook,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        evaluation.studentName,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary,
                        fontSize = 16.sp
                    )
                    Text(
                        "${evaluation.subject} • ${evaluation.date}",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textMuted
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "${evaluation.grade}/${evaluation.base}",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
                Text(
                    evaluation.period,
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textMuted
                )
            }
        }
    }
}

@Composable
fun BulletinItemCard(
    bulletin: com.ecolix.atschool.ui.grades.models.BulletinPreview,
    colors: DashboardColors
) {
    CardContainer(containerColor = colors.card) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF6366F1).copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Description,
                        contentDescription = null,
                        tint = Color(0xFF6366F1),
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        bulletin.studentName,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary,
                        fontSize = 16.sp
                    )
                    Text(
                        "${bulletin.classroom} • Rang: ${bulletin.rank}/${bulletin.totalStudents}",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textMuted
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "Moy: ${bulletin.average}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                )
                Surface(
                    color = when (bulletin.status) {
                        "Validé" -> Color(0xFF10B981).copy(alpha = 0.1f)
                        "Généré" -> Color(0xFF3B82F6).copy(alpha = 0.1f)
                        else -> Color(0xFFF59E0B).copy(alpha = 0.1f)
                    },
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        bulletin.status,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = when (bulletin.status) {
                            "Validé" -> Color(0xFF059669)
                            "Généré" -> Color(0xFF2563EB)
                            else -> Color(0xFFD97706)
                        }
                    )
                }
            }
        }
    }
}
