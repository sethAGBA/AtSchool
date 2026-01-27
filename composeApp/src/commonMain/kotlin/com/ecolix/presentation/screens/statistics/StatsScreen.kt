package com.ecolix.presentation.screens.statistics

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecolix.data.models.*
import com.ecolix.presentation.components.DonutChart
import com.ecolix.presentation.components.EcobizBarChart
import com.ecolix.presentation.components.KpiCard

@Composable
fun StatsScreenContent(isDarkMode: Boolean) {
    var selectedTab by remember { mutableStateOf(StatsTab.OVERVIEW) }
    
    val colors = if (isDarkMode) {
        DashboardColors(
            background = Color(0xFF111827),
            card = Color(0xFF1F2937),
            textPrimary = Color.White,
            textMuted = Color(0xFF9CA3AF),
            divider = Color(0xFF374151),
            textLink = Color(0xFF3B82F6)
        )
    } else {
        DashboardColors(
            background = Color(0xFFF3F4F6),
            card = Color.White,
            textPrimary = Color(0xFF111827),
            textMuted = Color(0xFF6B7280),
            divider = Color(0xFFE5E7EB),
            textLink = Color(0xFF2563EB)
        )
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Header with Tab Toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "Statistiques",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = colors.textPrimary
                )
                Text(
                    "Vue d'ensemble et indicateurs clés",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textMuted
                )
            }
            
            StatsTabToggle(
                selectedTab = selectedTab,
                onTabChange = { selectedTab = it },
                colors = colors
            )
        }

        // Content based on selected tab
        when (selectedTab) {
            StatsTab.OVERVIEW -> OverviewTab(colors)
            StatsTab.SUCCESS_RATE -> SuccessRateTab(colors)
            StatsTab.ATTENDANCE -> AttendanceTab(colors)
            StatsTab.SUBJECT_PERFORMANCE -> SubjectPerformanceTab(colors)
            StatsTab.ENROLLMENT_EVOLUTION -> EnrollmentEvolutionTab(colors)
            StatsTab.PAYMENT_DETAILS -> PaymentDetailsTab(colors)
            StatsTab.STAFF_STATS -> StaffStatsTab(colors)
        }
    }
}

@Composable
private fun StatsTabToggle(
    selectedTab: StatsTab,
    onTabChange: (StatsTab) -> Unit,
    colors: DashboardColors
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(colors.card)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        val tabs = listOf(
            Triple(StatsTab.OVERVIEW, "Vue d'ensemble", Icons.Default.Dashboard),
            Triple(StatsTab.SUCCESS_RATE, "Réussite", Icons.AutoMirrored.Filled.TrendingUp),
            Triple(StatsTab.ATTENDANCE, "Présence", Icons.Default.EventAvailable),
            Triple(StatsTab.SUBJECT_PERFORMANCE, "Matières", Icons.Default.MenuBook),
            Triple(StatsTab.ENROLLMENT_EVOLUTION, "Effectifs", Icons.Default.Group),
            Triple(StatsTab.PAYMENT_DETAILS, "Paiements", Icons.Default.MonetizationOn),
            Triple(StatsTab.STAFF_STATS, "Personnel", Icons.Default.Badge)
        )
        
        tabs.forEach { (tab, label, icon) ->
            val isSelected = selectedTab == tab
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                    .clickable { onTabChange(tab) }
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = if (isSelected) Color.White else colors.textMuted
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = label,
                    color = if (isSelected) Color.White else colors.textMuted,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                )
            }
        }
    }
}

@Composable
private fun OverviewTab(colors: DashboardColors) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                KpiCard(
                    metric = KpiMetric(
                        "Total Élèves", "1,234", 5.2f, Icons.Default.School, Color(0xFF3B82F6),
                        listOf(40f, 30f, 50f, 60f, 80f, 70f, 90f)
                    ),
                    colors = colors,
                    modifier = Modifier.weight(1f)
                )
                KpiCard(
                    metric = KpiMetric(
                        "Total Staff", "86", 2.1f, Icons.Default.Group, Color(0xFF10B981),
                        listOf(20f, 25f, 30f, 28f, 35f, 40f, 42f)
                    ),
                    colors = colors,
                    modifier = Modifier.weight(1f)
                )
                KpiCard(
                    metric = KpiMetric(
                        "Revenu Mensuel", "12M CFA", 12.5f, Icons.Default.MonetizationOn, Color(0xFFF59E0B),
                        listOf(60f, 70f, 65f, 80f, 90f, 100f, 110f)
                    ),
                    colors = colors,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                EcobizBarChart(
                    series = listOf(
                        ChartSeries("Garçons", listOf(
                            DataPoint("6e", 120f), DataPoint("5e", 110f), DataPoint("4e", 130f),
                            DataPoint("3e", 100f), DataPoint("2nde", 90f), DataPoint("1ere", 80f), DataPoint("Tle", 70f)
                        ), Color(0xFF3B82F6)),
                        ChartSeries("Filles", listOf(
                            DataPoint("6e", 130f), DataPoint("5e", 115f), DataPoint("4e", 125f),
                            DataPoint("3e", 105f), DataPoint("2nde", 95f), DataPoint("1ere", 85f), DataPoint("Tle", 75f)
                        ), Color(0xFFEC4899))
                    ),
                    colors = colors,
                    modifier = Modifier.weight(2f).height(350.dp)
                )

                Card(
                    colors = CardDefaults.cardColors(containerColor = colors.card),
                    modifier = Modifier.weight(1f).height(350.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp).fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Statut des Paiements",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = colors.textPrimary,
                            modifier = Modifier.align(Alignment.Start)
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        DonutChart(
                            data = listOf(65f, 25f, 10f),
                            colors = listOf(Color(0xFF10B981), Color(0xFFF59E0B), Color(0xFFEF4444)),
                            modifier = Modifier.size(200.dp),
                            centerText = "85%",
                            centerSubText = "Recouvrés",
                            textColor = colors.textPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SuccessRateTab(colors: DashboardColors) {
    val data = listOf(
        SuccessRateRow("6ème", 250, 235, 15, 14.2f),
        SuccessRateRow("5ème", 225, 210, 15, 13.8f),
        SuccessRateRow("4ème", 255, 240, 15, 14.5f),
        SuccessRateRow("3ème", 205, 190, 15, 13.2f),
        SuccessRateRow("2nde", 185, 170, 15, 12.8f),
        SuccessRateRow("1ère", 165, 155, 10, 13.5f),
        SuccessRateRow("Terminale", 145, 140, 5, 14.1f)
    )
    
    StatsTable(
        title = "Taux de Réussite par Niveau",
        headers = listOf("Niveau", "Total", "Réussis", "Échoués", "Moyenne"),
        colors = colors
    ) {
        items(data) { row ->
            TableRow(colors) {
                TableCell(row.level, colors, weight = 1f)
                TableCell(row.totalStudents.toString(), colors, weight = 1f)
                TableCell(row.passed.toString(), colors, weight = 1f, textColor = Color(0xFF10B981))
                TableCell(row.failed.toString(), colors, weight = 1f, textColor = Color(0xFFEF4444))
                TableCell("${row.average}/20", colors, weight = 1f)
            }
        }
    }
}

@Composable
private fun AttendanceTab(colors: DashboardColors) {
    val data = listOf(
        AttendanceRow("Septembre", 20, 18, 2, 90f),
        AttendanceRow("Octobre", 22, 20, 2, 91f),
        AttendanceRow("Novembre", 21, 19, 2, 90.5f),
        AttendanceRow("Décembre", 18, 16, 2, 89f),
        AttendanceRow("Janvier", 20, 18, 2, 90f),
        AttendanceRow("Février", 19, 17, 2, 89.5f)
    )
    
    StatsTable(
        title = "Statistiques de Présence Mensuelle",
        headers = listOf("Mois", "Jours Total", "Présents", "Absents", "Taux"),
        colors = colors
    ) {
        items(data) { row ->
            TableRow(colors) {
                TableCell(row.month, colors, weight = 1.2f)
                TableCell(row.totalDays.toString(), colors, weight = 1f)
                TableCell(row.presentDays.toString(), colors, weight = 1f)
                TableCell(row.absentDays.toString(), colors, weight = 1f)
                TableCell("${row.rate}%", colors, weight = 1f, textColor = if (row.rate >= 90) Color(0xFF10B981) else Color(0xFFF59E0B))
            }
        }
    }
}

@Composable
private fun SubjectPerformanceTab(colors: DashboardColors) {
    val data = listOf(
        SubjectPerformanceRow("Mathématiques", "M. Diallo", 234, 13.5f, 85f),
        SubjectPerformanceRow("Français", "Mme Sow", 234, 12.8f, 78f),
        SubjectPerformanceRow("Anglais", "M. Kane", 234, 14.2f, 88f),
        SubjectPerformanceRow("Physique", "Mme Ndiaye", 180, 13.1f, 82f),
        SubjectPerformanceRow("SVT", "M. Fall", 180, 13.8f, 84f),
        SubjectPerformanceRow("Histoire-Géo", "Mme Sarr", 234, 12.5f, 76f)
    )
    
    StatsTable(
        title = "Performance par Matière",
        headers = listOf("Matière", "Enseignant", "Élèves", "Moyenne", "Taux Réussite"),
        colors = colors
    ) {
        items(data) { row ->
            TableRow(colors) {
                TableCell(row.subject, colors, weight = 1.5f)
                TableCell(row.teacher, colors, weight = 1.2f)
                TableCell(row.studentsCount.toString(), colors, weight = 0.8f)
                TableCell("${row.average}/20", colors, weight = 1f)
                TableCell("${row.passRate}%", colors, weight = 1f, textColor = if (row.passRate >= 80) Color(0xFF10B981) else Color(0xFFF59E0B))
            }
        }
    }
}

@Composable
private fun EnrollmentEvolutionTab(colors: DashboardColors) {
    val data = listOf(
        EnrollmentEvolutionRow("2019-2020", 980, 520, 460, 0f),
        EnrollmentEvolutionRow("2020-2021", 1050, 560, 490, 7.1f),
        EnrollmentEvolutionRow("2021-2022", 1120, 595, 525, 6.7f),
        EnrollmentEvolutionRow("2022-2023", 1180, 625, 555, 5.4f),
        EnrollmentEvolutionRow("2023-2024", 1210, 640, 570, 2.5f),
        EnrollmentEvolutionRow("2024-2025", 1234, 652, 582, 2.0f)
    )
    
    StatsTable(
        title = "Évolution des Effectifs",
        headers = listOf("Année", "Total", "Garçons", "Filles", "Variation"),
        colors = colors
    ) {
        items(data) { row ->
            TableRow(colors) {
                TableCell(row.year, colors, weight = 1.2f)
                TableCell(row.totalStudents.toString(), colors, weight = 1f)
                TableCell(row.boys.toString(), colors, weight = 1f)
                TableCell(row.girls.toString(), colors, weight = 1f)
                TableCell(
                    if (row.variation > 0) "+${row.variation}%" else if (row.variation < 0) "${row.variation}%" else "-",
                    colors,
                    weight = 1f,
                    textColor = if (row.variation > 0) Color(0xFF10B981) else if (row.variation < 0) Color(0xFFEF4444) else colors.textMuted
                )
            }
        }
    }
}

@Composable
private fun PaymentDetailsTab(colors: DashboardColors) {
    val data = listOf(
        PaymentDetailsRow("Payé intégralement", 856, "18.5M CFA", 69.4f),
        PaymentDetailsRow("Paiement partiel", 245, "3.2M CFA", 19.9f),
        PaymentDetailsRow("En attente", 98, "1.8M CFA", 7.9f),
        PaymentDetailsRow("En retard", 35, "680K CFA", 2.8f)
    )
    
    StatsTable(
        title = "Détails des Paiements",
        headers = listOf("Statut", "Nombre", "Montant", "Pourcentage"),
        colors = colors
    ) {
        items(data) { row ->
            TableRow(colors) {
                TableCell(row.status, colors, weight = 1.5f)
                TableCell(row.count.toString(), colors, weight = 1f)
                TableCell(row.amount, colors, weight = 1.2f)
                TableCell(
                    "${row.percentage}%",
                    colors,
                    weight = 1f,
                    textColor = when {
                        row.percentage >= 50 -> Color(0xFF10B981)
                        row.percentage >= 15 -> Color(0xFFF59E0B)
                        else -> Color(0xFFEF4444)
                    }
                )
            }
        }
    }
}

@Composable
private fun StaffStatsTab(colors: DashboardColors) {
    val data = listOf(
        StaffStatsRow("Enseignement", 52, 38, 14, "450K CFA"),
        StaffStatsRow("Administration", 12, 10, 2, "380K CFA"),
        StaffStatsRow("Surveillance", 8, 6, 2, "280K CFA"),
        StaffStatsRow("Entretien", 10, 8, 2, "220K CFA"),
        StaffStatsRow("Cantine", 4, 3, 1, "240K CFA")
    )
    
    StatsTable(
        title = "Statistiques du Personnel",
        headers = listOf("Département", "Total", "Permanents", "Contractuels", "Salaire Moyen"),
        colors = colors
    ) {
        items(data) { row ->
            TableRow(colors) {
                TableCell(row.department, colors, weight = 1.5f)
                TableCell(row.totalStaff.toString(), colors, weight = 0.8f)
                TableCell(row.permanent.toString(), colors, weight = 1f)
                TableCell(row.contract.toString(), colors, weight = 1.2f)
                TableCell(row.averageSalary, colors, weight = 1.2f)
            }
        }
    }
}

@Composable
private fun StatsTable(
    title: String,
    headers: List<String>,
    colors: DashboardColors,
    content: androidx.compose.foundation.lazy.LazyListScope.() -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = colors.card),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = colors.textPrimary,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // Header Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.divider.copy(alpha = 0.3f))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                headers.forEachIndexed { index, header ->
                    val weight = when (index) {
                        0 -> 1.5f
                        headers.size - 1 -> 1.2f
                        else -> 1f
                    }
                    Text(
                        text = header,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = colors.textPrimary,
                        modifier = Modifier.weight(weight)
                    )
                }
            }
            
            HorizontalDivider(color = colors.divider)
            
            LazyColumn(
                modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp),
                content = content
            )
        }
    }
}

@Composable
private fun TableRow(
    colors: DashboardColors,
    content: @Composable RowScope.() -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            content()
        }
        HorizontalDivider(color = colors.divider.copy(alpha = 0.5f))
    }
}

@Composable
private fun RowScope.TableCell(
    text: String,
    colors: DashboardColors,
    weight: Float = 1f,
    textColor: Color = colors.textPrimary
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = textColor,
        modifier = Modifier.weight(weight)
    )
}
