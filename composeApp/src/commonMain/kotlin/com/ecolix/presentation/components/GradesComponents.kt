package com.ecolix.presentation.components

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
import com.ecolix.data.models.DashboardColors
import com.ecolix.data.models.GradesViewMode
import com.ecolix.data.models.GradeEvaluation
import com.ecolix.data.models.BulletinPreview
import com.ecolix.data.models.EvaluationSession
import com.ecolix.data.models.AcademicSummary
import com.ecolix.data.models.PeriodMode
import com.ecolix.data.models.EvaluationType

@Composable
fun PeriodModeToggle(
    currentMode: PeriodMode,
    onModeChange: (PeriodMode) -> Unit,
    colors: DashboardColors,
    modifier: Modifier = Modifier,
    isFullWidth: Boolean = false
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(colors.card)
            .border(1.dp, colors.divider, RoundedCornerShape(8.dp))
            .padding(4.dp)
    ) {
        listOf(PeriodMode.TRIMESTRE, PeriodMode.SEMESTRE).forEach { mode ->
            val selected = currentMode == mode
            Box(
                modifier = Modifier
                    .then(if (isFullWidth) Modifier.weight(1f) else Modifier)
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent)
                    .clickable { onModeChange(mode) }
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (mode == PeriodMode.TRIMESTRE) "Trimestres" else "Semestres",
                    color = if (selected) MaterialTheme.colorScheme.primary else colors.textMuted,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

@Composable
fun SpecificPeriodSelector(
    currentPeriod: String,
    onPeriodChange: (String) -> Unit,
    periodMode: PeriodMode,
    colors: DashboardColors,
    modifier: Modifier = Modifier
) {
    val periods = if (periodMode == PeriodMode.TRIMESTRE) 
        listOf("1er", "2e", "3e")
    else 
        listOf("1er", "2e")
    
    val suffix = if (periodMode == PeriodMode.TRIMESTRE) "Trimestre" else "Semestre"
    val fullPeriods = periods.map { "$it $suffix" }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(colors.background)
            .border(1.dp, colors.divider, RoundedCornerShape(8.dp))
            .padding(2.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        fullPeriods.forEach { period ->
            val selected = currentPeriod == period
            val label = period.substringBefore(" ")
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (selected) MaterialTheme.colorScheme.primary else Color.Transparent)
                    .clickable { onPeriodChange(period) }
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    color = if (selected) Color.White else colors.textMuted,
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, fontSize = 13.sp)
                )
            }
        }
    }
}

@Composable
fun GradesViewToggle(
    currentMode: GradesViewMode,
    onModeChange: (GradesViewMode) -> Unit,
    colors: DashboardColors,
    modifier: Modifier = Modifier,
    isFullWidth: Boolean = false
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(colors.card)
            .border(1.dp, colors.divider, RoundedCornerShape(12.dp))
            .padding(4.dp)
    ) {
        listOf(
            Triple(GradesViewMode.NOTES, "Notes", Icons.AutoMirrored.Filled.MenuBook),
            Triple(GradesViewMode.BULLETINS, "Bulletins", Icons.Default.Description),
            Triple(GradesViewMode.ARCHIVES, "Archives", Icons.Default.History)
        ).forEach { (mode, label, icon) ->
            GradesToggleItem(
                selected = currentMode == mode,
                onClick = { onModeChange(mode) },
                label = label,
                icon = icon,
                colors = colors,
                modifier = if (isFullWidth) Modifier.weight(1f) else Modifier
            )
        }
    }
}

@Composable
private fun GradesToggleItem(
    selected: Boolean,
    onClick: () -> Unit,
    label: String,
    icon: ImageVector,
    colors: DashboardColors,
    modifier: Modifier = Modifier
) {
    val bg = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent
    val contentColor = if (selected) Color.White else colors.textMuted
    
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp), tint = contentColor)
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = label, color = contentColor, style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, fontSize = 12.sp), maxLines = 1)
    }
}

@Composable
fun AcademicSummaryCards(summary: AcademicSummary, colors: DashboardColors, isCompact: Boolean = false) {
    if (isCompact) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Moyenne G.",
                    value = "${summary.averageGrade}/20",
                    icon = Icons.AutoMirrored.Filled.ShowChart,
                    color = Color(0xFF6366F1),
                    colors = colors,
                    modifier = Modifier.weight(1f),
                    isCompact = true
                )
                StatCard(
                    title = "Taux Réussite",
                    value = "${summary.successRate}%",
                    icon = Icons.Default.CheckCircle,
                    color = Color(0xFF10B981),
                    colors = colors,
                    modifier = Modifier.weight(1f),
                    isCompact = true
                )
            }
            StatCard(
                title = "Meilleure Performance: ${summary.topStudent}",
                value = summary.topStudent,
                icon = Icons.Default.EmojiEvents,
                color = Color(0xFFF59E0B),
                colors = colors,
                modifier = Modifier.fillMaxWidth(),
                isCompact = true,
                showValueOnly = false
            )
        }
    } else {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatCard(
                title = "Moyenne Générale",
                value = "${summary.averageGrade}/20",
                icon = Icons.AutoMirrored.Filled.ShowChart,
                color = Color(0xFF6366F1),
                colors = colors,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Taux de Réussite",
                value = "${summary.successRate}%",
                icon = Icons.Default.CheckCircle,
                color = Color(0xFF10B981),
                colors = colors,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Top Elève",
                value = summary.topStudent,
                icon = Icons.Default.EmojiEvents,
                color = Color(0xFFF59E0B),
                colors = colors,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    colors: DashboardColors,
    modifier: Modifier = Modifier,
    isCompact: Boolean = false,
    showValueOnly: Boolean = true
) {
    CardContainer(containerColor = colors.card, modifier = modifier) {
        if (isCompact && !showValueOnly) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(color.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, null, tint = color, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(title, style = MaterialTheme.typography.labelSmall, color = colors.textMuted)
                    Text(value, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold), color = colors.textPrimary)
                }
            }
        } else {
            Column {
                Box(
                    modifier = Modifier
                        .size(if (isCompact) 32.dp else 40.dp)
                        .clip(RoundedCornerShape(if (isCompact) 8.dp else 10.dp))
                        .background(color.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, null, tint = color, modifier = Modifier.size(if (isCompact) 16.dp else 20.dp))
                }
                Spacer(modifier = Modifier.height(if (isCompact) 12.dp else 16.dp))
                Text(value, style = if (isCompact) MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold) else MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold), color = colors.textPrimary)
                Text(title, style = if (isCompact) MaterialTheme.typography.labelSmall else MaterialTheme.typography.labelMedium, color = colors.textMuted, maxLines = 1)
            }
        }
    }
}

@Composable
fun GradesActionBar(
    onAddGradeClick: () -> Unit,
    onExportClick: () -> Unit,
    onImportClick: () -> Unit,
    onFilterClick: () -> Unit,
    colors: DashboardColors,
    modifier: Modifier = Modifier,
    isCompact: Boolean = false
) {
    if (isCompact) {
        Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(
                onClick = { onAddGradeClick() },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = Color.White),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Icon(Icons.Default.Add, null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Nouvelle Evaluation", fontWeight = FontWeight.Bold)
            }
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                // Import Button
                OutlinedButton(
                    onClick = onImportClick,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f).height(48.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, colors.divider),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    Icon(Icons.Default.FileUpload, null, tint = colors.textPrimary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Importer", color = colors.textPrimary, fontSize = 12.sp, maxLines = 1)
                }
                
                // Export Button
                OutlinedButton(
                    onClick = onExportClick,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f).height(48.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, colors.divider),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    Icon(Icons.Default.FileDownload, null, tint = colors.textPrimary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Exporter", color = colors.textPrimary, fontSize = 12.sp, maxLines = 1)
                }

                // Filter Button (New for Mobile)
                OutlinedButton(
                    onClick = onFilterClick,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f).height(48.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, colors.divider),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    Icon(Icons.Default.FilterList, null, tint = colors.textPrimary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Filtrer", color = colors.textPrimary, fontSize = 12.sp, maxLines = 1)
                }
            }
        }
    } else {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onAddGradeClick,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ),
                modifier = Modifier.height(48.dp)
            ) {
                Icon(Icons.Default.Add, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Nouvelle Evaluation", fontWeight = FontWeight.Bold)
            }
            
            OutlinedButton(
                onClick = onImportClick,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.height(48.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, colors.divider)
            ) {
                Icon(Icons.Default.FileUpload, null, tint = colors.textPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Importer Excel", color = colors.textPrimary)
            }

            Spacer(modifier = Modifier.weight(1f))

            IconButton(
                onClick = onFilterClick,
                modifier = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(colors.card).border(1.dp, colors.divider, RoundedCornerShape(12.dp))
            ) {
                Icon(Icons.Default.FilterList, null, tint = colors.textPrimary)
            }
            
            IconButton(
                onClick = onExportClick,
                modifier = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(colors.card).border(1.dp, colors.divider, RoundedCornerShape(12.dp))
            ) {
                Icon(Icons.Default.FileDownload, null, tint = colors.textPrimary)
            }
        }
    }
}

@Composable
fun EvaluationSessionCard(
    session: EvaluationSession,
    colors: DashboardColors,
    isCompact: Boolean = false,
    onClick: () -> Unit
) {
    CardContainer(
        containerColor = colors.card,
        modifier = Modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(if (isCompact) 44.dp else 52.dp)
                    .clip(RoundedCornerShape(if (isCompact) 10.dp else 12.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.EventNote,
                    null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(if (isCompact) 22.dp else 26.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(if (isCompact) 12.dp else 16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(session.title, fontWeight = FontWeight.Bold, color = colors.textPrimary, fontSize = if (isCompact) 14.sp else 16.sp, maxLines = 1)
                    if (!isCompact) {
                        Spacer(modifier = Modifier.width(8.dp))
                        TagPill(
                            label = session.type.label,
                            color = if (session.type == EvaluationType.COMPOSITION) Color(0xFFEF4444) else MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Text("${session.subject} • ${session.classroom}", style = if (isCompact) MaterialTheme.typography.labelSmall else MaterialTheme.typography.bodySmall, color = colors.textMuted, maxLines = 1)
                if (isCompact) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(session.date, style = MaterialTheme.typography.labelSmall, color = colors.textMuted.copy(alpha = 0.7f))
                        Spacer(modifier = Modifier.width(6.dp))
                        TagPill(
                            label = session.type.label,
                            color = if (session.type == EvaluationType.COMPOSITION) Color(0xFFEF4444) else MaterialTheme.colorScheme.primary,
                            isSmall = true
                        )
                    }
                } else {
                    Text(session.date, style = MaterialTheme.typography.labelSmall, color = colors.textMuted.copy(alpha = 0.7f))
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "${session.average}/${session.maxGrade}",
                    style = if (isCompact) MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = colors.textPrimary) else MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = colors.textPrimary)
                )
                Text(
                    "Coeff: ${session.coefficient.toInt()}",
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textMuted
                )
            }
            
            if (!isCompact) {
                Spacer(modifier = Modifier.width(16.dp))
                Icon(Icons.Default.ChevronRight, null, tint = colors.textMuted)
            }
        }
    }
}

@Composable
fun BulletinItemCard(
    bulletin: BulletinPreview,
    colors: DashboardColors,
    isCompact: Boolean = false
) {
    CardContainer(containerColor = colors.card) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(if (isCompact) 40.dp else 48.dp)
                        .clip(RoundedCornerShape(if (isCompact) 10.dp else 12.dp))
                        .background(Color(0xFF6366F1).copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = Color(0xFF6366F1),
                        modifier = Modifier.size(if (isCompact) 20.dp else 24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(if (isCompact) 12.dp else 16.dp))
                Column {
                    Text(
                        bulletin.studentName,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary,
                        fontSize = if (isCompact) 14.sp else 16.sp,
                        maxLines = 1
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "Rang: ${bulletin.rank}/${bulletin.totalStudents}",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.textMuted,
                            fontSize = if (isCompact) 11.sp else 12.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = when(bulletin.trend) {
                                "up" -> Icons.AutoMirrored.Filled.TrendingUp
                                "down" -> Icons.AutoMirrored.Filled.TrendingDown
                                else -> Icons.AutoMirrored.Filled.TrendingFlat
                            },
                            contentDescription = null,
                            tint = when(bulletin.trend) {
                                "up" -> Color(0xFF10B981)
                                "down" -> Color(0xFFEF4444)
                                else -> colors.textMuted
                            },
                            modifier = Modifier.size(if (isCompact) 12.dp else 14.dp)
                        )
                    }
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "Moy: ${bulletin.average}",
                    style = if (isCompact) MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = colors.textPrimary) else MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = colors.textPrimary)
                )
                TagPill(
                    label = bulletin.status,
                    color = when (bulletin.status) {
                        "Validé" -> Color(0xFF10B981)
                        "Généré" -> Color(0xFF3B82F6)
                        else -> Color(0xFFF59E0B)
                    },
                    isSmall = isCompact
                )
            }
        }
    }
}
