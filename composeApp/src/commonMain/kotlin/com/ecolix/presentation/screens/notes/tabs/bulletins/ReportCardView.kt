package com.ecolix.presentation.screens.notes.tabs.bulletins

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecolix.data.models.DashboardColors
import com.ecolix.data.models.ReportCard
import com.ecolix.data.models.ReportCardSubject
import com.ecolix.presentation.components.CardContainer

@Composable
fun ReportCardView(
    reportCard: ReportCard,
    colors: DashboardColors,
    onBack: () -> Unit,
    onExportPdf: () -> Unit,
    isExporting: Boolean = false,
    steps: Int = 1
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Header Actions
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = colors.textPrimary)
            }
            Text(
                "Bulletin - ${reportCard.studentName}",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = colors.textPrimary
            )
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = onExportPdf,
                enabled = !isExporting,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isExporting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(Icons.Default.Print, null, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (isExporting) "Export..." else "Imprimer", fontWeight = FontWeight.Bold)
            }
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // School Header
            item {
                CardContainer(containerColor = colors.card) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            // Column 1: Ministry, DRE, Inspection
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                                val ministry = reportCard.schoolInfo?.ministry ?: "MINISTERE DES ENSEIGNEMENTS\nPRIMAIRE, SECONDAIRE, TECHNIQUE"
                                val dre = reportCard.schoolInfo?.educationDirection ?: "DRE-MARITIME"
                                val inspection = reportCard.schoolInfo?.inspection ?: "IESG-VOGAN"

                                Text(ministry, style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.sp, fontWeight = FontWeight.Bold), textAlign = TextAlign.Center, color = colors.textPrimary)
                                Text("-----------------------------", style = MaterialTheme.typography.labelSmall.copy(fontSize = 6.sp), color = colors.textMuted)
                                Text(dre, style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, fontWeight = FontWeight.Bold), textAlign = TextAlign.Center, color = colors.textPrimary)
                                Text("-----------------------------", style = MaterialTheme.typography.labelSmall.copy(fontSize = 6.sp), color = colors.textMuted)
                                Text(inspection, style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, fontWeight = FontWeight.Bold), textAlign = TextAlign.Center, color = colors.textPrimary)
                                Text("-----------------------------", style = MaterialTheme.typography.labelSmall.copy(fontSize = 6.sp), color = colors.textMuted)
                            }
                            
                            // Column 2: Logo & School Info
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                                Box(
                                    modifier = Modifier
                                        .size(50.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.School, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(30.dp))
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                val schoolName = reportCard.schoolInfo?.schoolName ?: "GROUPE SCOLAIRE ECOLIX"
                                val bp = reportCard.schoolInfo?.bp ?: "BP : 1234 Lomé"
                                val phone = reportCard.schoolInfo?.phone?.let { "Tel: $it" } ?: "Tel: 22 22 22 22"
                                val slogan = reportCard.schoolInfo?.schoolSlogan ?: "Discipline - Travail - Succès"

                                Text(schoolName, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp), color = colors.textPrimary, textAlign = TextAlign.Center)
                                Text(bp, style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.sp), color = colors.textPrimary)
                                Text(phone, style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.sp), color = colors.textPrimary)
                                Text("\"$slogan\"", style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.sp, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic), color = colors.textMuted, textAlign = TextAlign.Center)
                            }

                            // Column 3: Republic Info
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                                val republic = reportCard.schoolInfo?.republicName ?: "RÉPUBLIQUE TOGOLAISE"
                                val motto = reportCard.schoolInfo?.republicMotto ?: "Travail - Liberté - Patrie"
                                
                                Text(republic, style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, fontWeight = FontWeight.Bold), color = colors.textPrimary, textAlign = TextAlign.Center)
                                Text(motto, style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.sp), color = colors.textPrimary, textAlign = TextAlign.Center)
                                Spacer(modifier = Modifier.weight(1f))
                                Text("Année Scolaire\n${reportCard.academicYear}", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), textAlign = TextAlign.Center, color = colors.textPrimary)
                            }
                        }
                    }
                }
            }

            // Student Info Header
            item {
                CardContainer(containerColor = colors.card) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            InfoItem("Classe:", reportCard.className, colors)
                            InfoItem("Série:", reportCard.serie, colors)
                            InfoItem("Effectif:", "${reportCard.totalStudents}", colors)
                            InfoItem("Période:", reportCard.period, colors)
                        }
                        HorizontalDivider(color = colors.divider)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            InfoItem("Elève:", reportCard.studentName, colors)
                            InfoItem("Matricule:", reportCard.matricule, colors)
                            InfoItem("Statut:", if (reportCard.isRepeater) "Redoublant" else "Nouveau", colors)
                            InfoItem("Né(e) le/à:", reportCard.dateOfBirth, colors)
                            InfoItem("Sexe:", reportCard.sex, colors)
                        }
                    }
                }
            }

            // Grades Table
            item {
                CardContainer(containerColor = colors.card) {
                   Column {
                       // Table Header
                       Row(
                           modifier = Modifier
                               .fillMaxWidth()
                               .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                               .padding(vertical = 12.dp, horizontal = 4.dp),
                           verticalAlignment = Alignment.CenterVertically
                       ) {
                           Text("Matières", modifier = Modifier.weight(2.2f), fontWeight = FontWeight.Bold, fontSize = 11.sp, color = colors.textPrimary)
                           Text("Dev", modifier = Modifier.weight(0.7f), fontWeight = FontWeight.Bold, fontSize = 10.sp, textAlign = TextAlign.Center, color = colors.textPrimary)
                           Text("Comp", modifier = Modifier.weight(0.7f), fontWeight = FontWeight.Bold, fontSize = 10.sp, textAlign = TextAlign.Center, color = colors.textPrimary)
                           Text("Moy", modifier = Modifier.weight(0.8f), fontWeight = FontWeight.Bold, fontSize = 10.sp, textAlign = TextAlign.Center, color = colors.textPrimary)
                           Text("Coef", modifier = Modifier.weight(0.5f), fontWeight = FontWeight.Bold, fontSize = 10.sp, textAlign = TextAlign.Center, color = colors.textPrimary)
                           Text("Total", modifier = Modifier.weight(0.8f), fontWeight = FontWeight.Bold, fontSize = 10.sp, textAlign = TextAlign.Center, color = colors.textPrimary)
                           Text("Min", modifier = Modifier.weight(0.6f), fontWeight = FontWeight.Bold, fontSize = 10.sp, textAlign = TextAlign.Center, color = colors.textMuted)
                           Text("Max", modifier = Modifier.weight(0.6f), fontWeight = FontWeight.Bold, fontSize = 10.sp, textAlign = TextAlign.Center, color = colors.textMuted)
                           Text("Rng", modifier = Modifier.weight(0.5f), fontWeight = FontWeight.Bold, fontSize = 10.sp, textAlign = TextAlign.Center, color = colors.textPrimary)
                           Text("Appréciations", modifier = Modifier.weight(1.5f), fontWeight = FontWeight.Bold, fontSize = 11.sp, color = colors.textPrimary)
                           Text("Professeurs", modifier = Modifier.weight(1.5f), fontWeight = FontWeight.Bold, fontSize = 11.sp, color = colors.textPrimary)
                       }
                       
                       // Group subjects by category
                       val groupedSubjects = reportCard.subjects.groupBy { it.category }
                       
                       groupedSubjects.forEach { (category, subjects) ->
                           // Category Header
                           if (category.isNotEmpty() && category != "Général") {
                               Row(
                                   modifier = Modifier
                                       .fillMaxWidth()
                                       .background(colors.background)
                                       .border(0.5.dp, colors.divider)
                                       .padding(vertical = 4.dp, horizontal = 8.dp),
                                   verticalAlignment = Alignment.CenterVertically
                               ) {
                                   Text(
                                       text = category.uppercase(),
                                       style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                       color = colors.textPrimary,
                                       fontSize = 10.sp
                                   )
                               }
                           }

                           // Table Rows
                           subjects.forEachIndexed { index, subject ->
                               Row(
                                   modifier = Modifier
                                       .fillMaxWidth()
                                       .background(if (index % 2 == 1) colors.background.copy(alpha = 0.5f) else Color.Transparent)
                                       .padding(vertical = 8.dp, horizontal = 4.dp),
                                   verticalAlignment = Alignment.CenterVertically
                               ) {
                                    val devoirs = subject.evaluations.filter { it.typeName == "Devoir" }.joinToString(", ") { it.mark.toString() }
                                    val composition = subject.evaluations.find { it.typeName == "Composition" }?.mark?.toString() ?: "-"

                                   Text(subject.name, modifier = Modifier.weight(2.2f), fontWeight = FontWeight.Medium, fontSize = 12.sp, color = colors.textPrimary)
                                   
                                    Text(if (devoirs.isNotEmpty()) devoirs else "-", modifier = Modifier.weight(0.7f), fontSize = 11.sp, textAlign = TextAlign.Center, color = colors.textPrimary)
                                    Text(composition, modifier = Modifier.weight(0.7f), fontSize = 11.sp, textAlign = TextAlign.Center, color = colors.textPrimary)
                                   Text(subject.average.toString(), modifier = Modifier.weight(0.8f), fontSize = 11.sp, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, color = colors.textPrimary)
                                   Text(subject.coefficient.toInt().toString(), modifier = Modifier.weight(0.5f), fontSize = 11.sp, textAlign = TextAlign.Center, color = colors.textPrimary)
                                   Text(subject.total.toString(), modifier = Modifier.weight(0.8f), fontSize = 11.sp, textAlign = TextAlign.Center, color = colors.textPrimary)
                                   Text(subject.minAverage.toString(), modifier = Modifier.weight(0.6f), fontSize = 11.sp, textAlign = TextAlign.Center, color = colors.textMuted)
                                   Text(subject.maxAverage.toString(), modifier = Modifier.weight(0.6f), fontSize = 11.sp, textAlign = TextAlign.Center, color = colors.textMuted)
                                   Text(subject.rank.toString(), modifier = Modifier.weight(0.5f), fontSize = 11.sp, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, color = colors.textPrimary)
                                   Text(subject.appreciation, modifier = Modifier.weight(1.5f), fontSize = 10.sp, color = colors.textPrimary, maxLines = 1)
                                   Text(subject.professor, modifier = Modifier.weight(1.5f), fontSize = 10.sp, color = colors.textMuted, maxLines = 1)
                               }
                               HorizontalDivider(color = colors.divider, thickness = 0.5.dp)
                           }
                       }
                       
                       // Totals Row
                       val totalCoef = reportCard.subjects.sumOf { it.coefficient.toDouble() }
                       val totalPoints = reportCard.subjects.sumOf { it.total.toDouble() }
                       
                       HorizontalDivider(color = colors.divider, thickness = 1.dp)
                       Row(
                           modifier = Modifier
                               .fillMaxWidth()
                               .background(colors.background.copy(alpha = 0.8f))
                               .padding(vertical = 12.dp, horizontal = 4.dp),
                           verticalAlignment = Alignment.CenterVertically
                       ) {
                           Text("TOTAUX", modifier = Modifier.weight(2.2f + 0.7f + 0.7f + 0.8f), fontWeight = FontWeight.Bold, fontSize = 12.sp, color = colors.textPrimary)
                           Text(totalCoef.toInt().toString(), modifier = Modifier.weight(0.5f), fontSize = 12.sp, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, color = colors.textPrimary)
                           Text(totalPoints.toString(), modifier = Modifier.weight(0.8f), fontSize = 12.sp, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, color = colors.textPrimary)
                           Spacer(modifier = Modifier.weight(0.6f + 0.6f + 0.5f + 1.5f + 1.5f))
                       }
                   }
                }
            }
            
            // Footer (Averages & Decision)
            item {
                Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    
                    // Left Column: History & Attendance
                    CardContainer(containerColor = colors.card, modifier = Modifier.weight(3f)) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("I-Moyennes", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = colors.textPrimary)
                            
                            // History Table
                            Column(modifier = Modifier.border(0.5.dp, colors.divider)) {
                                val periods = listOf("1er Trimestre", "2e Trimestre", "3e Trimestre")
                                periods.forEachIndexed { index, label ->
                                    val avg = reportCard.historyAverages.getOrNull(index)?.toString() ?: "-"
                                    Row(modifier = Modifier.fillMaxWidth().border(0.5.dp, colors.divider)) {
                                        Text(label, modifier = Modifier.weight(1f).padding(4.dp), style = MaterialTheme.typography.bodySmall, fontSize = 10.sp, color = colors.textPrimary)
                                        Text(avg, modifier = Modifier.weight(1f).padding(4.dp), style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), fontSize = 10.sp, color = colors.textPrimary)
                                    }
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Présence: 98%  |  Abs. J: ${reportCard.absJustifiees}  |  Abs. I: ${reportCard.absInjustifiees}  |  Ret: ${reportCard.retards}", style = MaterialTheme.typography.bodySmall, fontSize = 9.sp, color = colors.textPrimary)
                        }
                    }

                    // Right Column: Class Stats
                    CardContainer(containerColor = colors.card, modifier = Modifier.weight(4f)) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                             Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                 BadgeItem("Mention", reportCard.appreciationGenerale, colors)
                                 BadgeItem("Rang", "${reportCard.rank}/${reportCard.totalStudents}", colors)
                             }
                             
                             // Stats Table
                             Column(modifier = Modifier.border(0.5.dp, colors.divider)) {
                                 Row(modifier = Modifier.fillMaxWidth()) {
                                     MiniInfoCell("Moy. forte", "${reportCard.maxAverage}", colors, true)
                                     MiniInfoCell("Moy. faible", "${reportCard.minAverage}", colors, true)
                                 }
                                 HorizontalDivider(color = colors.divider, thickness = 0.5.dp)
                                 Row(modifier = Modifier.fillMaxWidth()) {
                                     MiniInfoCell("Moy. classe", "${reportCard.classAverage}", colors, true)
                                     MiniInfoCell("Moy. Gen", "${reportCard.generalAverage}", colors, true)
                                 }
                                 
                                 if (reportCard.annualAverage != null) {
                                     HorizontalDivider(color = colors.divider, thickness = 0.5.dp)
                                     Row(modifier = Modifier.fillMaxWidth()) {
                                         MiniInfoCell("Moy. Annuelle", "${reportCard.annualAverage}", colors, true)
                                         MiniInfoCell("Rang Annuel", "${reportCard.rank}/${reportCard.totalStudents}", colors, true)
                                     }
                                 }
                             }
                        }
                    }
                }
            }

            // Council Section
            item {
                CardContainer(containerColor = colors.card) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("II-Appréciations du conseil des professeurs", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = colors.textPrimary)
                        
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Travail: ${reportCard.travail}", style = MaterialTheme.typography.bodySmall, fontSize = 11.sp, color = colors.textPrimary)
                            Text("Conduite: ${reportCard.conduite}", style = MaterialTheme.typography.bodySmall, fontSize = 11.sp, color = colors.textPrimary)
                        }
                        
                        Text(
                            "Félicitations: ${if(reportCard.tableauFelicitations) "OUI" else "NON"}  |  Encouragements: ${if(reportCard.tableauEncouragement) "OUI" else "NON"}  |  Tableau d'honneur: ${if(reportCard.tableauHonneur) "OUI" else "NON"}",
                            style = MaterialTheme.typography.bodySmall, fontSize = 10.sp, color = colors.textPrimary
                        )
                        
                        HorizontalDivider(color = colors.divider, thickness = 0.5.dp)
                        
                        Text("III- Décision du conseil: ${reportCard.decision}", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = colors.textPrimary)
                    }
                }
            }

            // Signatures
            item {
                CardContainer(containerColor = colors.card) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        val location = reportCard.schoolInfo?.address?.split(",")?.lastOrNull()?.trim() ?: "Lomé"
                        val date = reportCard.generatedDate ?: "24/01/2026"
                        Text("Fait à $location, le $date", style = MaterialTheme.typography.bodySmall, color = colors.textPrimary)
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth().height(80.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                                Text("Le Titulaire", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = colors.textPrimary)
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(modifier = Modifier.width(100.dp).height(40.dp).border(0.5.dp, colors.divider)) {
                                    Box(modifier = Modifier.align(Alignment.Center)) {
                                        Text(reportCard.teacherName, fontSize = 8.sp, color = colors.textMuted)
                                    }
                                }
                            }
                            
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                                Text("Le Chef d'Etablissement", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = colors.textPrimary)
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(modifier = Modifier.width(100.dp).height(40.dp).border(0.5.dp, colors.divider)) {
                                    Box(modifier = Modifier.align(Alignment.Center)) {
                                        Text(reportCard.directorName, fontSize = 8.sp, color = colors.textMuted)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            // Footer NB
            if (reportCard.nb.isNotEmpty()) {
                item {
                    Text(
                        reportCard.nb, 
                        style = MaterialTheme.typography.bodySmall.copy(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic), 
                        color = colors.textMuted,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoItem(label: String, value: String, colors: DashboardColors) {
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall, color = colors.textMuted)
        Text(value, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = colors.textPrimary)
    }
}

@Composable
private fun BadgeItem(label: String, value: String, colors: DashboardColors) {
    Row(
        modifier = Modifier
            .background(colors.background, RoundedCornerShape(4.dp))
            .border(0.5.dp, colors.divider, RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("$label: ", style = MaterialTheme.typography.bodySmall, fontSize = 11.sp, color = colors.textMuted)
        Text(value, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), fontSize = 11.sp, color = colors.textPrimary)
    }
}

@Composable
private fun RowScope.MiniInfoCell(label: String, value: String, colors: DashboardColors, isBold: Boolean = false) {
    Row(modifier = Modifier.weight(1f).border(0.2.dp, colors.divider).padding(4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontSize = 9.sp, color = colors.textMuted)
        Text(value, fontSize = 9.sp, fontWeight = if(isBold) FontWeight.Bold else FontWeight.Normal, color = colors.textPrimary)
    }
}
