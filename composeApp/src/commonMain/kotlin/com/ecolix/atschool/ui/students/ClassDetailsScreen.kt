package com.ecolix.atschool.ui.students

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
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import com.ecolix.atschool.ui.dashboard.components.TagPill
import com.ecolix.atschool.ui.dashboard.models.DashboardColors
import com.ecolix.atschool.ui.students.components.StudentRow
import com.ecolix.atschool.ui.students.models.Classroom
import com.ecolix.atschool.ui.students.models.Student

@Composable
fun ClassDetailsScreen(
    classroom: Classroom,
    students: List<Student>,
    colors: DashboardColors,
    onBack: () -> Unit,
    onStudentClick: (String) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Infos Générales", "Élèves (${students.size})")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .padding(top = 24.dp, bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = colors.textPrimary)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "Classe: ${classroom.name}",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = colors.textPrimary
                )
                Text(
                    text = "${classroom.level} • ${classroom.academicYear}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textMuted
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = {},
                colors = ButtonDefaults.buttonColors(containerColor = colors.textLink, contentColor = Color.White),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Modifier", fontSize = 13.sp)
            }
        }

        // Tabs
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

        // Content
        Box(modifier = Modifier.weight(1f)) {
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() }
            ) { targetTab ->
                when (targetTab) {
                    0 -> ClassOverviewTab(classroom, colors)
                    1 -> ClassStudentsTab(students, colors, onStudentClick)
                }
            }
        }
    }
}

@Composable
private fun ClassOverviewTab(classroom: Classroom, colors: DashboardColors) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(bottom = 20.dp)
    ) {
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                ClassStatCard(
                    modifier = Modifier.weight(1f),
                    label = "Total Élèves",
                    value = classroom.studentCount.toString(),
                    icon = Icons.Default.People,
                    color = colors.textLink,
                    colors = colors
                )
                ClassStatCard(
                    modifier = Modifier.weight(1f),
                    label = "Sexe Ratio",
                    value = "${classroom.boysCount}G / ${classroom.girlsCount}F",
                    icon = Icons.Default.Wc,
                    color = Color(0xFF10B981),
                    colors = colors,
                    smallValue = true
                )
            }
        }

        item {
            CardContainer(containerColor = colors.card) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Détails de la Classe", fontWeight = FontWeight.Bold, color = colors.textPrimary)
                    HorizontalDivider(color = colors.divider)
                    
                    ClassDetailRow(Icons.Default.Person, "Professeur Principal", classroom.mainTeacher ?: "Non assigné", colors)
                    ClassDetailRow(Icons.Default.MeetingRoom, "Salle de classe", classroom.roomNumber ?: "N/A", colors)
                    ClassDetailRow(Icons.Default.GroupAdd, "Capacité Maximale", classroom.capacity?.toString() ?: "Illimitée", colors)
                    ClassDetailRow(Icons.AutoMirrored.Filled.TrendingUp, "Moyenne Générale", "13.45 / 20", colors) // Mock
                }
            }
        }

        if (classroom.description != null) {
            item {
                CardContainer(containerColor = colors.card) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Observations / Notes", fontWeight = FontWeight.Bold, color = colors.textPrimary)
                        HorizontalDivider(color = colors.divider)
                        Text(
                            text = classroom.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.textPrimary,
                            lineHeight = 22.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ClassStudentsTab(students: List<Student>, colors: DashboardColors, onStudentClick: (String) -> Unit) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(bottom = 20.dp)
    ) {
        items(students) { student ->
            StudentRow(
                student = student,
                colors = colors,
                selectionMode = false,
                isSelected = false,
                onToggleSelect = {},
                onClick = { onStudentClick(student.id) }
            )
        }
    }
}

@Composable
private fun ClassDetailRow(icon: ImageVector, label: String, value: String, colors: DashboardColors) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Icon(icon, contentDescription = null, size(18.dp), tint = colors.textMuted)
        Spacer(modifier = Modifier.width(12.dp))
        Text(label, style = MaterialTheme.typography.bodyMedium, color = colors.textMuted, modifier = Modifier.weight(1f))
        Text(value, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = colors.textPrimary)
    }
}

@Composable
private fun ClassStatCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    icon: ImageVector,
    color: Color,
    colors: DashboardColors,
    smallValue: Boolean = false
) {
    CardContainer(containerColor = colors.card, modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = color)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = value,
                    style = if (smallValue) MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold) else MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black),
                    color = colors.textPrimary
                )
                Text(text = label, style = MaterialTheme.typography.labelSmall, color = colors.textMuted)
            }
        }
    }
}

private fun size(dp: androidx.compose.ui.unit.Dp) = Modifier.size(dp)
