package com.ecolix.presentation.components

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecolix.data.models.Classroom
import com.ecolix.data.models.Student
import com.ecolix.data.models.StudentsViewMode
import com.ecolix.data.models.DashboardColors

@Composable
fun DistributionChart(distribution: Map<String, Int>, colors: DashboardColors) {
    CardContainer(containerColor = colors.card) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(
                text = "Repartition par niveau",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = colors.textPrimary
            )
            
            val total = distribution.values.sum().coerceAtLeast(1)
            val levelColors = listOf(Color(0xFF6366F1), Color(0xFF10B981), Color(0xFFF59E0B), Color(0xFFEF4444), Color(0xFF8B5CF6))
            
            Row(
                modifier = Modifier.fillMaxWidth().height(24.dp).clip(RoundedCornerShape(6.dp))
            ) {
                distribution.entries.forEachIndexed { index, entry ->
                    val weight = entry.value.toFloat() / total
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(weight.coerceAtLeast(0.01f))
                            .background(levelColors[index % levelColors.size])
                    )
                }
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                distribution.entries.forEachIndexed { index, entry ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(levelColors[index % levelColors.size]))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${entry.key} (${entry.value})",
                            style = MaterialTheme.typography.labelSmall,
                            color = colors.textMuted
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SelectionActionBar(
    selectedCount: Int,
    onClearSelection: () -> Unit,
    onDeleteSelected: () -> Unit,
    colors: DashboardColors,
    isCompact: Boolean = false
) {
    CardContainer(
        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.95f),
        modifier = Modifier.fillMaxWidth()
    ) {
        if (isCompact) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onClearSelection) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "$selectedCount eleve(s)",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.2f), contentColor = Color.White),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        Icon(Icons.Default.SwapHoriz, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Changer", fontSize = 12.sp)
                    }
                    Button(
                        onClick = onDeleteSelected,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444), contentColor = Color.White),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Supprimer", fontSize = 12.sp)
                    }
                }
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onClearSelection) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "$selectedCount eleve(s) selectionne(s)",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.2f), contentColor = Color.White),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.SwapHoriz, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Changer Classe")
                    }
                    Button(
                        onClick = onDeleteSelected,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444), contentColor = Color.White),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Supprimer")
                    }
                }
            }
        }
    }
}

@Composable
fun ViewToggle(
    currentMode: StudentsViewMode,
    onModeChange: (StudentsViewMode) -> Unit,
    colors: DashboardColors,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(colors.card)
            .border(1.dp, colors.divider, RoundedCornerShape(12.dp))
            .padding(4.dp)
    ) {
        ToggleItem(
            selected = currentMode == StudentsViewMode.CLASSES,
            onClick = { onModeChange(StudentsViewMode.CLASSES) },
            label = "Classes",
            icon = Icons.Default.Dashboard,
            colors = colors,
            modifier = if (modifier != Modifier) Modifier.weight(1f) else Modifier
        )
        ToggleItem(
            selected = currentMode == StudentsViewMode.STUDENTS,
            onClick = { onModeChange(StudentsViewMode.STUDENTS) },
            label = "Eleves",
            icon = Icons.Default.People,
            colors = colors,
            modifier = if (modifier != Modifier) Modifier.weight(1f) else Modifier
        )
    }
}

@Composable
private fun ToggleItem(
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
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp), tint = contentColor)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label, color = contentColor, style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
    }
}

@Composable
fun ClassCard(classroom: Classroom, colors: DashboardColors, onClick: () -> Unit) {
    CardContainer(
        containerColor = colors.card,
        modifier = Modifier.clickable { onClick() }
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = classroom.name,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = colors.textPrimary
                    )
                    Text(
                        text = classroom.level,
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textMuted
                    )
                }
                TagPill(classroom.academicYear, Color(0xFF6366F1))
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatPill(Icons.Default.People, classroom.studentCount.toString(), "élèves", Color(0xFF3B82F6))
                
                if (classroom.roomNumber != null) {
                    StatPill(Icons.Default.MeetingRoom, classroom.roomNumber, "Salle", Color(0xFF8B5CF6))
                }
                
                if (classroom.capacity != null) {
                    StatPill(Icons.Default.GroupAdd, "${classroom.studentCount}/${classroom.capacity}", "Cap.", if (classroom.studentCount >= (classroom.capacity ?: 0)) Color(0xFFEF4444) else Color(0xFF10B981))
                }
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatMicroPill(Icons.Default.Male, classroom.boysCount.toString(), Color(0xFF10B981))
                StatMicroPill(Icons.Default.Female, classroom.girlsCount.toString(), Color(0xFFF59E0B))
                if (classroom.mainTeacher != null) {
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "Prof: ${classroom.mainTeacher}",
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.textMuted,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
private fun StatMicroPill(icon: ImageVector, value: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(12.dp), tint = color)
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = value, color = color, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
    }
}

@Composable
private fun StatPill(icon: ImageVector, value: String, label: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.1f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(14.dp), tint = color)
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = "$value $label", color = color, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
    }
}

@Composable
fun ActionBar(
    onAddStudentClick: () -> Unit,
    onAddClassClick: () -> Unit,
    colors: DashboardColors,
    isCompact: Boolean = false
) {
    if (isCompact) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ActionButton(Icons.Default.PersonAdd, "Eleve", colors.textLink, Color.White, modifier = Modifier.weight(1f), onClick = onAddStudentClick)
                ActionButton(Icons.Default.AddHome, "Classe", colors.background, colors.textPrimary, border = true, colors = colors, modifier = Modifier.weight(1f), onClick = onAddClassClick)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ActionButton(Icons.Default.FileUpload, "Importer", colors.background, colors.textPrimary, border = true, colors = colors, modifier = Modifier.weight(1f))
                ActionButton(Icons.Default.FileDownload, "Exporter", colors.background, colors.textPrimary, border = true, colors = colors, modifier = Modifier.weight(1f))
            }
            ActionButton(Icons.Default.HowToReg, "Reinscription", Color(0xFFF59E0B), Color.White, modifier = Modifier.fillMaxWidth())
        }
    } else {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ActionButton(Icons.Default.PersonAdd, "Eleve", colors.textLink, Color.White, onClick = onAddStudentClick)
            ActionButton(Icons.Default.AddHome, "Classe", colors.background, colors.textPrimary, border = true, colors = colors, onClick = onAddClassClick)
            ActionButton(Icons.Default.HowToReg, "Reinscription", Color(0xFFF59E0B), Color.White)
            Spacer(modifier = Modifier.weight(1f))
            ActionButton(Icons.Default.FileUpload, "Importer", colors.background, colors.textPrimary, border = true, colors = colors)
            ActionButton(Icons.Default.FileDownload, "Exporter", colors.background, colors.textPrimary, border = true, colors = colors)
        }
    }
}

@Composable
private fun ActionButton(
    icon: ImageVector,
    label: String,
    bg: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
    border: Boolean = false,
    colors: DashboardColors? = null,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .then(if (border && colors != null) Modifier.border(1.dp, colors.divider, RoundedCornerShape(12.dp)) else Modifier)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp), tint = contentColor)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label, color = contentColor, style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold), maxLines = 1)
    }
}

@Composable
fun AdvancedFilters(
    selectedLevel: String?,
    onLevelChange: (String?) -> Unit,
    selectedGender: String?,
    onGenderChange: (String?) -> Unit,
    visibility: String,
    onVisibilityChange: (String) -> Unit,
    colors: DashboardColors,
    isCompact: Boolean = false
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        LazyRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            item {
                FilterChipGroup(
                    title = "Niveaux",
                    items = listOf(null, "Primaire", "College", "Lycee"),
                    selectedItem = selectedLevel,
                    onSelect = onLevelChange,
                    colors = colors
                )
            }
        }
        
        if (isCompact) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                FilterChipGroup(
                    title = "Sexe",
                    items = listOf(null, "M", "F"),
                    selectedItem = selectedGender,
                    onSelect = onGenderChange,
                    colors = colors,
                    itemLabels = mapOf("M" to "Garçons", "F" to "Filles")
                )
                FilterChipGroup(
                    title = "Statut",
                    items = listOf("active", "deleted", "all"),
                    selectedItem = visibility,
                    onSelect = onVisibilityChange,
                    colors = colors,
                    itemLabels = mapOf("active" to "Actifs", "deleted" to "Corbeille", "all" to "Tous")
                )
            }
        } else {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                FilterChipGroup(
                    title = "Sexe",
                    items = listOf(null, "M", "F"),
                    selectedItem = selectedGender,
                    onSelect = onGenderChange,
                    colors = colors,
                    itemLabels = mapOf("M" to "Garçons", "F" to "Filles")
                )
                FilterChipGroup(
                    title = "Statut",
                    items = listOf("active", "deleted", "all"),
                    selectedItem = visibility,
                    onSelect = onVisibilityChange,
                    colors = colors,
                    itemLabels = mapOf("active" to "Actifs", "deleted" to "Corbeille", "all" to "Tous")
                )
            }
        }
    }
}

@Composable
private fun <T> FilterChipGroup(
    title: String,
    items: List<T>,
    selectedItem: T,
    onSelect: (T) -> Unit,
    colors: DashboardColors,
    itemLabels: Map<T, String> = emptyMap()
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(colors.card)
            .border(1.dp, colors.divider, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text = title, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = colors.textMuted)
        Spacer(modifier = Modifier.width(8.dp))
        items.forEach { item ->
            val isSelected = item == selectedItem
            val bg = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent
            val textColor = if (isSelected) MaterialTheme.colorScheme.primary else colors.textPrimary
            
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(bg)
                    .clickable { onSelect(item) }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = itemLabels[item] ?: item?.toString() ?: "Tous",
                    color = textColor,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold)
                )
            }
        }
    }
}

@Composable
fun StudentRow(
    student: Student,
    colors: DashboardColors,
    selectionMode: Boolean,
    isSelected: Boolean,
    onToggleSelect: () -> Unit,
    onClick: () -> Unit = {}
) {
    CardContainer(
        containerColor = colors.card,
        modifier = Modifier.padding(vertical = 4.dp).clickable { 
            if (selectionMode) onToggleSelect() else onClick()
        }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (selectionMode) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = { onToggleSelect() },
                    colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(colors.background),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = colors.textMuted)
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${student.firstName} ${student.lastName}",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    color = colors.textPrimary
                )
                Text(
                    text = "${student.classroom} • Matr: ${student.matricule ?: "N/A"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textMuted
                )
            }
            
            Column(horizontalAlignment = Alignment.End) {
                StatusIndicator(student.status)
                if (student.isDeleted) {
                    TagPill("Supprime", Color(0xFFEF4444))
                }
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            IconButton(onClick = { }) {
                Icon(Icons.Default.MoreVert, contentDescription = null, tint = colors.textMuted)
            }
        }
    }
}

@Composable
private fun StatusIndicator(status: String) {
    val color = when (status.uppercase()) {
        "ACTIF", "ACTIVE" -> Color(0xFF10B981)
        "INACTIF", "INACTIVE" -> Color(0xFF64748B)
        "SUSPENDU", "SUSPENDED" -> Color(0xFFEF4444)
        "DIPLOME", "GRADUATED" -> Color(0xFFF59E0B)
        else -> Color(0xFF6366F1) // Default for new statuses like "Nouveau", "Transfert"
    }
    
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(color))
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = status.lowercase().replaceFirstChar { it.uppercase() },
            color = color,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold)
        )
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    colors: DashboardColors,
    modifier: Modifier = Modifier
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(colors.card),
        placeholder = { Text("Rechercher...", color = colors.textMuted) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = colors.textMuted) },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = colors.card,
            unfocusedContainerColor = colors.card,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        ),
        singleLine = true
    )
}
