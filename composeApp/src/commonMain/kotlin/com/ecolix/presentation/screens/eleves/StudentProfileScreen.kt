package com.ecolix.presentation.screens.eleves

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
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
import com.ecolix.presentation.components.CardContainer
import com.ecolix.presentation.components.TagPill
import com.ecolix.data.models.DashboardColors
import com.ecolix.data.models.Student
import com.ecolix.data.models.StudentDocument

@Composable
fun StudentProfileScreen(
    student: Student,
    colors: DashboardColors,
    isCompact: Boolean = false,
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Aperçu", "Académique", "Documents")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = if (isCompact) 16.dp else 24.dp)
            .padding(top = if (isCompact) 16.dp else 24.dp, bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(if (isCompact) 16.dp else 20.dp)
    ) {
        // Top Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = colors.textPrimary)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Profil Étudiant",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = colors.textPrimary
            )
        }

        // Student Brief Header
        CardContainer(containerColor = colors.card) {
            if (isCompact) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(colors.background),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(56.dp), tint = colors.textMuted)
                    }
                    
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${student.firstName} ${student.lastName}",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                            color = colors.textPrimary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Text(
                            text = "Matricule: ${student.matricule ?: "N/A"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.textMuted
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            TagPill(student.classroom, Color(0xFF6366F1))
                            TagPill(student.status, getStatusColor(student.status))
                        }
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(colors.background),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(48.dp), tint = colors.textMuted)
                    }
                    
                    Spacer(modifier = Modifier.width(20.dp))
                    
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${student.firstName} ${student.lastName}",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                            color = colors.textPrimary
                        )
                        Text(
                            text = "Matricule: ${student.matricule ?: "N/A"}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.textMuted
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            TagPill(student.classroom, Color(0xFF6366F1))
                            TagPill(student.status, getStatusColor(student.status))
                        }
                    }
                }
            }
        }

        // Tabs
        if (isCompact) {
            SecondaryScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.primary,
                edgePadding = 0.dp,
                divider = {}
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { 
                            Text(
                                title, 
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.sp
                            ) 
                        },
                        selectedContentColor = MaterialTheme.colorScheme.primary,
                        unselectedContentColor = colors.textMuted
                    )
                }
            }
        } else {
            SecondaryTabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.primary,
                indicator = { 
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(selectedTab),
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                divider = {}
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { 
                            Text(
                                title, 
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 14.sp
                            ) 
                        },
                        selectedContentColor = MaterialTheme.colorScheme.primary,
                        unselectedContentColor = colors.textMuted
                    )
                }
            }
        }

        // Tab Content
        Box(modifier = Modifier.weight(1f)) {
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() }
            ) { targetTab ->
                when (targetTab) {
                    0 -> OverviewTab(student, colors, isCompact)
                    1 -> AcademicTab(student, colors, isCompact)
                    2 -> DocumentsTab(student.documents, colors)
                }
            }
        }

        // Main Actions
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { },
                modifier = Modifier.weight(if (isCompact) 1f else 1.1f).height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.textLink,
                    contentColor = Color.White
                )
            ) {
                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(if (isCompact) 18.dp else 20.dp))
                if (!isCompact) Spacer(modifier = Modifier.width(8.dp))
                Text(if (isCompact) "Edit" else "Modifier", fontWeight = FontWeight.Bold, fontSize = if (isCompact) 13.sp else 14.sp)
            }
            OutlinedButton(
                onClick = { },
                modifier = Modifier.weight(if (isCompact) 1f else 0.9f).height(52.dp),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, colors.divider),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.textPrimary)
            ) {
                Icon(Icons.Default.Badge, contentDescription = null, modifier = Modifier.size(if (isCompact) 18.dp else 20.dp))
                if (!isCompact) Spacer(modifier = Modifier.width(8.dp))
                Text(if (isCompact) "Carte" else "Carte Scolaire", fontWeight = FontWeight.Bold, fontSize = if (isCompact) 13.sp else 14.sp, maxLines = 1)
            }
        }
    }
}

@Composable
private fun OverviewTab(student: Student, colors: DashboardColors, isCompact: Boolean) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(bottom = 20.dp)
    ) {
        item {
            InfoCard(
                title = "Identité & Contact",
                colors = colors,
                items = listOf(
                    Triple(Icons.Default.Wc, "Sexe", if (student.gender == "M") "Masculin" else "Féminin"),
                    Triple(Icons.Default.Cake, "Né(e) le", student.dateOfBirth ?: "N/A"),
                    Triple(Icons.Default.Place, "Lieu de Naissance", student.placeOfBirth ?: "N/A"),
                    Triple(Icons.Default.Flag, "Nationalité", student.nationality ?: "N/A"),
                    Triple(Icons.Default.Bloodtype, "Groupe Sanguin", student.bloodGroup ?: "N/A"),
                    Triple(Icons.Default.Home, "Adresse", student.address ?: "N/A"),
                    Triple(Icons.Default.Email, "Email", student.email ?: "N/A"),
                    Triple(Icons.Default.Phone, "Téléphone", student.contactNumber ?: "N/A")
                )
            )
        }

        item {
            InfoCard(
                title = "Parent / Tuteur",
                colors = colors,
                items = listOf(
                    Triple(Icons.Default.Person, "Nom complet", student.guardianName ?: "N/A"),
                    Triple(Icons.Default.Phone, "Contact", student.guardianContact ?: "N/A"),
                    Triple(Icons.Default.Emergency, "Urgence", student.emergencyContact ?: "N/A")
                )
            )
        }
    }
}

@Composable
private fun AcademicTab(student: Student, colors: DashboardColors, isCompact: Boolean) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(bottom = 20.dp)
    ) {
        // Performance Stats
        item {
            if (isCompact) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    AcademicMetricCard(
                        modifier = Modifier.fillMaxWidth(),
                        label = "Moyenne Générale",
                        value = "${student.averageGrade}",
                        suffix = "/20",
                        progress = (student.averageGrade / 20.0).toFloat(),
                        color = Color(0xFFF59E0B),
                        colors = colors
                    )
                    AcademicMetricCard(
                        modifier = Modifier.fillMaxWidth(),
                        label = "Assiduité",
                        value = "96",
                        suffix = "%",
                        progress = 0.96f,
                        color = Color(0xFF10B981),
                        colors = colors
                    )
                }
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    AcademicMetricCard(
                        modifier = Modifier.weight(1f),
                        label = "Moyenne G.",
                        value = "${student.averageGrade}",
                        suffix = "/20",
                        progress = (student.averageGrade / 20.0).toFloat(),
                        color = Color(0xFFF59E0B),
                        colors = colors
                    )
                    AcademicMetricCard(
                        modifier = Modifier.weight(1f),
                        label = "Assiduité",
                        value = "96",
                        suffix = "%",
                        progress = 0.96f,
                        color = Color(0xFF10B981),
                        colors = colors
                    )
                }
            }
        }

        item {
            InfoCard(
                title = "Détails Académiques",
                colors = colors,
                items = listOf(
                    Triple(Icons.Default.School, "Année Scolaire", student.academicYear),
                    Triple(Icons.Default.Event, "Inscription", student.enrollmentDate),
                    Triple(Icons.Default.HistoryEdu, "Statut", student.status)
                )
            )
        }

        if (student.medicalInfo != null || student.remarks != null) {
            item {
                CardContainer(containerColor = colors.card) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        if (student.medicalInfo != null) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.MedicalServices, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text("Santé & Allergies", fontWeight = FontWeight.Bold, color = colors.textPrimary)
                                }
                                Text(
                                    text = student.medicalInfo!!,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = colors.textPrimary,
                                    lineHeight = 20.sp
                                )
                            }
                        }
                        
                        if (student.medicalInfo != null && student.remarks != null) {
                            HorizontalDivider(color = colors.divider)
                        }

                        if (student.remarks != null) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.AutoMirrored.Filled.Assignment, contentDescription = null, tint = colors.textLink, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text("Observations Générales", fontWeight = FontWeight.Bold, color = colors.textPrimary)
                                }
                                Text(
                                    text = student.remarks!!,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = colors.textPrimary,
                                    lineHeight = 20.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DocumentsTab(documents: List<StudentDocument>, colors: DashboardColors) {
    if (documents.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.FolderOpen, contentDescription = null, modifier = Modifier.size(64.dp), tint = colors.textMuted.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(16.dp))
                Text("Aucun document", color = colors.textMuted, fontWeight = FontWeight.Medium)
            }
        }
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(documents) { doc ->
                CardContainer(containerColor = colors.card) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(colors.background),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                when {
                                    doc.name.lowercase().endsWith(".pdf") -> Icons.Default.PictureAsPdf
                                    else -> Icons.Default.Description
                                },
                                contentDescription = null,
                                tint = if (doc.name.lowercase().endsWith(".pdf")) Color(0xFFEF4444) else colors.textLink,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(doc.name, fontWeight = FontWeight.Bold, color = colors.textPrimary, maxLines = 1)
                            Text("Ajouté le ${doc.addedAt}", style = MaterialTheme.typography.labelSmall, color = colors.textMuted)
                        }
                        IconButton(onClick = {}) {
                            Icon(Icons.Default.Download, contentDescription = null, tint = colors.textMuted, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
            
            item {
                Button(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colors.background, contentColor = colors.textLink),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Ajouter un document", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun AcademicMetricCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    suffix: String,
    progress: Float,
    color: Color,
    colors: DashboardColors
) {
    CardContainer(containerColor = colors.card, modifier = modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(label, style = MaterialTheme.typography.labelMedium, color = colors.textMuted)
            Row(verticalAlignment = Alignment.Bottom) {
                Text(value, style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black), color = colors.textPrimary)
                Text(suffix, style = MaterialTheme.typography.bodySmall, color = colors.textMuted, modifier = Modifier.padding(bottom = 6.dp, start = 2.dp))
            }
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                color = color,
                trackColor = color.copy(alpha = 0.1f)
            )
        }
    }
}

@Composable
private fun InfoCard(
    title: String,
    colors: DashboardColors,
    items: List<Triple<ImageVector, String, String>>
) {
    CardContainer(containerColor = colors.card) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = colors.textPrimary
            )
            HorizontalDivider(color = colors.divider)
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                items.forEach { (icon, label, value) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp), tint = colors.textMuted)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = colors.textMuted, modifier = Modifier.weight(0.4f))
                        Text(
                            text = value,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = colors.textPrimary,
                            modifier = Modifier.weight(0.6f),
                            textAlign = androidx.compose.ui.text.style.TextAlign.End
                        )
                    }
                }
            }
        }
    }
}

private fun getStatusColor(status: String): Color {
    return when (status.uppercase()) {
        "ACTIF", "ACTIVE" -> Color(0xFF10B981)
        "INACTIF", "INACTIVE" -> Color(0xFF64748B)
        "SUSPENDU", "SUSPENDED" -> Color(0xFFEF4444)
        "DIPLÔMÉ", "GRADUATED" -> Color(0xFFF59E0B)
        else -> Color(0xFF6366F1)
    }
}
