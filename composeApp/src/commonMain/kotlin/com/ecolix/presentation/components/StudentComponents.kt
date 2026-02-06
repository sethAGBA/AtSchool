package com.ecolix.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Category as ClassIcon
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.LibraryBooks as ListIcon
import androidx.compose.material.icons.filled.HowToReg
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Male
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.material.icons.filled.AddHome
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecolix.data.models.Classroom
import com.ecolix.data.models.Student
import com.ecolix.data.models.StudentDisplayMode
import com.ecolix.data.models.StudentsViewMode
import com.ecolix.data.models.DashboardColors

@OptIn(ExperimentalLayoutApi::class)
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

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                distribution.entries.filter { it.key != "Niveaux" || it.value > 0 }.forEachIndexed { index, entry ->
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
    onRestoreSelected: (() -> Unit)? = null,
    onTransferSelected: (() -> Unit)? = null,
    isTrashView: Boolean = false,
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
                        Icon(Icons.Filled.Close, contentDescription = null, tint = Color.White)
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
                    if (isTrashView && onRestoreSelected != null) {
                        Button(
                            onClick = onRestoreSelected,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981), contentColor = Color.White),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp)
                        ) {
                            Icon(Icons.Filled.SwapHoriz, contentDescription = null, modifier = Modifier.size(16.dp)) // Use SwapHoriz as Restore if not available
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Restaurer", fontSize = 12.sp)
                        }
                    } else {
                        Button(
                            onClick = { onTransferSelected?.invoke() },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.2f), contentColor = Color.White),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp)
                        ) {
                            Icon(Icons.Filled.SwapHoriz, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Changer", fontSize = 12.sp)
                        }
                    }
                    Button(
                        onClick = onDeleteSelected,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444), contentColor = Color.White),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        Icon(Icons.Filled.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isTrashView) "Suppr. Definitive" else "Supprimer", fontSize = 12.sp)
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
                        Icon(Icons.Filled.Close, contentDescription = null, tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "$selectedCount eleve(s) selectionne(s)",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (isTrashView && onRestoreSelected != null) {
                        Button(
                            onClick = onRestoreSelected,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981), contentColor = Color.White),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Filled.SwapHoriz, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Restaurer")
                        }
                    } else {
                        Button(
                            onClick = { onTransferSelected?.invoke() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.2f), contentColor = Color.White),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Filled.SwapHoriz, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Changer Classe")
                        }
                    }
                    Button(
                        onClick = onDeleteSelected,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444), contentColor = Color.White),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Filled.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isTrashView) "Supprimer Definitivement" else "Supprimer")
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
            selected = currentMode == StudentsViewMode.STRUCTURE,
            onClick = { onModeChange(StudentsViewMode.STRUCTURE) },
            label = "Structure",
            icon = Icons.Filled.AccountTree,
            colors = colors,
            modifier = if (modifier != Modifier) Modifier.weight(1f) else Modifier
        )
        ToggleItem(
            selected = currentMode == StudentsViewMode.CLASSES,
            onClick = { onModeChange(StudentsViewMode.CLASSES) },
            label = "Classes",
            icon = Icons.Filled.Dashboard,
            colors = colors,
            modifier = if (modifier != Modifier) Modifier.weight(1f) else Modifier
        )
        ToggleItem(
            selected = currentMode == StudentsViewMode.STUDENTS,
            onClick = { onModeChange(StudentsViewMode.STUDENTS) },
            label = "Eleves",
            icon = Icons.Filled.People,
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
fun ClassCard(
    classroom: Classroom, 
    colors: DashboardColors, 
    onClick: () -> Unit,
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    var showMenu by remember { mutableStateOf(false) }

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
                Column(modifier = Modifier.weight(1f)) {
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
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TagPill(classroom.academicYear, Color(0xFF6366F1))
                    
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Filled.MoreVert, contentDescription = null, tint = colors.textMuted)
                        }
                        
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            modifier = Modifier.background(colors.card)
                        ) {
                            DropdownMenuItem(
                                text = { Text("Modifier", color = colors.textPrimary) },
                                onClick = {
                                    showMenu = false
                                    onEdit()
                                },
                                leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null, tint = colors.textPrimary) }
                            )
                            DropdownMenuItem(
                                text = { Text("Supprimer", color = Color(0xFFEF4444)) },
                                onClick = {
                                    showMenu = false
                                    onDelete()
                                },
                                leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFEF4444)) }
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatPill(Icons.Filled.People, classroom.studentCount.toString(), "élèves", Color(0xFF3B82F6))

                if (classroom.roomNumber != null) {
                    StatPill(Icons.Filled.Place, classroom.roomNumber, "Salle", Color(0xFF8B5CF6))
                }

                if (classroom.capacity != null) {
                    StatPill(Icons.Filled.GroupAdd, "${classroom.studentCount}/${classroom.capacity}", "Cap.", if (classroom.studentCount >= (classroom.capacity ?: 0)) Color(0xFFEF4444) else Color(0xFF10B981))
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatMicroPill(Icons.Filled.Male, classroom.boysCount.toString(), Color(0xFF10B981))
                StatMicroPill(Icons.Filled.Female, classroom.girlsCount.toString(), Color(0xFFF59E0B))
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
    onRefreshClick: () -> Unit = {},
    colors: DashboardColors,
    isCompact: Boolean = false
) {
    if (isCompact) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ActionButton(Icons.Filled.PersonAdd, "Eleve", colors.textLink, Color.White, modifier = Modifier.weight(1f), onClick = onAddStudentClick)
                ActionButton(Icons.Filled.AddHome, "Classe", colors.background, colors.textPrimary, border = true, colors = colors, modifier = Modifier.weight(1f), onClick = onAddClassClick)
                ActionButton(Icons.Default.Refresh, "", colors.background, colors.textPrimary, border = true, colors = colors, modifier = Modifier.width(48.dp), onClick = onRefreshClick)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ActionButton(Icons.Filled.FileUpload, "Importer", colors.background, colors.textPrimary, border = true, colors = colors, modifier = Modifier.weight(1f))
                ActionButton(Icons.Filled.FileDownload, "Exporter", colors.background, colors.textPrimary, border = true, colors = colors, modifier = Modifier.weight(1f))
            }
            ActionButton(Icons.Filled.HowToReg, "Reinscription", Color(0xFFF59E0B), Color.White, modifier = Modifier.fillMaxWidth())
        }
    } else {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ActionButton(Icons.Filled.PersonAdd, "Eleve", colors.textLink, Color.White, onClick = onAddStudentClick)
            ActionButton(Icons.Filled.AddHome, "Classe", colors.background, colors.textPrimary, border = true, colors = colors, onClick = onAddClassClick)
            ActionButton(Icons.Default.Refresh, "Actualiser", colors.background, colors.textPrimary, border = true, colors = colors, onClick = onRefreshClick)
            ActionButton(Icons.Filled.HowToReg, "Reinscription", Color(0xFFF59E0B), Color.White)
            Spacer(modifier = Modifier.weight(1f))
            ActionButton(Icons.Filled.FileUpload, "Importer", colors.background, colors.textPrimary, border = true, colors = colors)
            ActionButton(Icons.Filled.FileDownload, "Exporter", colors.background, colors.textPrimary, border = true, colors = colors)
        }
    }
}


@Composable
fun AdvancedFilters(
    levels: List<String>,
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
                    title = "Cycles",
                    items = listOf(null) + levels,
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
    onDelete: () -> Unit = {},
    onRestore: () -> Unit = {},
    onTransfer: () -> Unit = {},
    onClick: () -> Unit = {}
) {
    var showMenu by remember { mutableStateOf(false) }

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
                Icon(Icons.Filled.Person, contentDescription = null, tint = colors.textMuted)
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
                StatusIndicator(if (student.isDeleted) "Supprimé" else student.status)
            }

            Spacer(modifier = Modifier.width(8.dp))

            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Filled.MoreVert, contentDescription = null, tint = colors.textMuted)
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier.background(colors.card)
                ) {
                    if (student.isDeleted) {
                        DropdownMenuItem(
                            text = { Text("Restaurer", color = colors.textPrimary) },
                            onClick = {
                                showMenu = false
                                onRestore()
                            },
                            leadingIcon = { Icon(Icons.Default.Restore, contentDescription = null, tint = Color(0xFF10B981)) }
                        )
                        DropdownMenuItem(
                            text = { Text("Supprimer définitivement", color = Color(0xFFEF4444)) },
                            onClick = {
                                showMenu = false
                                onDelete()
                            },
                            leadingIcon = { Icon(Icons.Default.DeleteForever, contentDescription = null, tint = Color(0xFFEF4444)) }
                        )
                    } else {
                        DropdownMenuItem(
                            text = { Text("Changer de classe", color = colors.textPrimary) },
                            onClick = {
                                showMenu = false
                                onTransfer()
                            },
                            leadingIcon = { Icon(Icons.Default.SwapHoriz, contentDescription = null, tint = colors.textPrimary) }
                        )
                        DropdownMenuItem(
                            text = { Text("Supprimer", color = Color(0xFFEF4444)) },
                            onClick = {
                                showMenu = false
                                onDelete()
                            },
                            leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFEF4444)) }
                        )
                    }
                }
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
        "SUPPRIMÉ", "SUPPRIME", "DELETED" -> Color(0xFFEF4444)
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
fun StudentCard(
    student: Student,
    colors: DashboardColors,
    selectionMode: Boolean = false,
    isSelected: Boolean = false,
    onToggleSelect: () -> Unit = {},
    onDelete: () -> Unit = {},
    onRestore: () -> Unit = {},
    onTransfer: () -> Unit = {},
    onClick: () -> Unit,
    icon: ImageVector
) {
    var showMenu by remember { mutableStateOf(false) }


    CardContainer(
        containerColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.05f) else colors.card,
        modifier = Modifier.clickable { if (selectionMode) onToggleSelect() else onClick() }
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (selectionMode) {
                        Checkbox(
                            checked = isSelected,
                            onCheckedChange = { onToggleSelect() },
                            colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(colors.background),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Person, contentDescription = null, tint = colors.textMuted)
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    StatusIndicator(if (student.isDeleted) "Supprimé" else student.status)
                    Spacer(modifier = Modifier.width(8.dp))
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Filled.MoreVert, contentDescription = null, tint = colors.textMuted)
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            modifier = Modifier.background(colors.card)
                        ) {
                            if (student.isDeleted) {
                                DropdownMenuItem(
                                    text = { Text("Restaurer", color = colors.textPrimary) },
                                    onClick = {
                                        showMenu = false
                                        onRestore()
                                    },
                                    leadingIcon = { Icon(Icons.Default.Restore, contentDescription = null, tint = Color(0xFF10B981)) }
                                )
                                DropdownMenuItem(
                                    text = { Text("Supprimer définitivement", color = Color(0xFFEF4444)) },
                                    onClick = {
                                        showMenu = false
                                        onDelete()
                                    },
                                    leadingIcon = { Icon(Icons.Default.DeleteForever, contentDescription = null, tint = Color(0xFFEF4444)) }
                                )
                            } else {
                                DropdownMenuItem(
                                    text = { Text("Changer de classe", color = colors.textPrimary) },
                                    onClick = {
                                        showMenu = false
                                        onTransfer()
                                    },
                                    leadingIcon = { Icon(Icons.Default.SwapHoriz, contentDescription = null, tint = colors.textPrimary) }
                                )
                                DropdownMenuItem(
                                    text = { Text("Supprimer", color = Color(0xFFEF4444)) },
                                    onClick = {
                                        showMenu = false
                                        onDelete()
                                    },
                                    leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFEF4444)) }
                                )
                            }
                        }
                    }
                }
            }

            Column {
                Text(
                    text = "${student.firstName} ${student.lastName}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = colors.textPrimary,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
                Text(
                    text = "Matr: ${student.matricule ?: "N/A"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textMuted
                )
            }

            HorizontalDivider(color = colors.divider, thickness = 0.5.dp)

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                StudentInfoMini(icon, student.classroom, colors)
                if (student.dateOfBirth != null) {
                    StudentInfoMini(Icons.Filled.Cake, student.dateOfBirth, colors)
                }
                if (student.averageGrade > 0) {
                    StudentInfoMini(Icons.Filled.Star, "Moy: ${student.averageGrade}/20", colors)
                }
            }
        }
    }
}

@Composable
private fun StudentInfoMini(icon: ImageVector, text: String, colors: DashboardColors) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(14.dp), tint = colors.textMuted)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, style = MaterialTheme.typography.labelSmall, color = colors.textPrimary, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
    }
}

@Composable
fun StudentViewToggle(
    currentMode: StudentDisplayMode,
    onModeChange: (StudentDisplayMode) -> Unit,
    colors: DashboardColors,
    modifier: Modifier = Modifier,
    ListIcon: ImageVector
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(colors.card)
            .border(1.dp, colors.divider, RoundedCornerShape(12.dp))
            .padding(4.dp)
    ) {
        ToggleItemGeneric(
            selected = currentMode == StudentDisplayMode.LIST,
            onClick = { onModeChange(StudentDisplayMode.LIST) },
            label = "Liste",
            icon = ListIcon,
            colors = colors,
            modifier = if (modifier != Modifier) Modifier.weight(1f) else Modifier
        )
        ToggleItemGeneric(
            selected = currentMode == StudentDisplayMode.GRID,
            onClick = { onModeChange(StudentDisplayMode.GRID) },
            label = "Grille",
            icon = Icons.Filled.GridView,
            colors = colors,
            modifier = if (modifier != Modifier) Modifier.weight(1f) else Modifier
        )
    }
}

@Composable
private fun ToggleItemGeneric(
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
        if (label.isNotEmpty()) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = label, color = contentColor, style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
        }
    }
}

@Composable
fun YearSelector(
    selectedYear: String,
    years: List<com.ecolix.atschool.api.SchoolYearDto>,
    onYearChange: (String) -> Unit,
    colors: DashboardColors,
    modifier: Modifier = Modifier
) {
    var expanded by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }

    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(colors.card)
                .border(1.dp, colors.divider, RoundedCornerShape(12.dp))
                .clickable { expanded = true }
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CalendarToday,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = colors.textLink
            )
            Text(
                text = selectedYear,
                color = colors.textPrimary,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = colors.textMuted
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(colors.card).width(IntrinsicSize.Min)
        ) {
            years.forEach { year ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = year.libelle,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (year.libelle == selectedYear) MaterialTheme.colorScheme.primary else colors.textPrimary
                        )
                    },
                    onClick = {
                        onYearChange(year.libelle)
                        expanded = false
                    },
                    leadingIcon = {
                        if (year.isDefault) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = "Active",
                                modifier = Modifier.size(16.dp),
                                tint = Color(0xFFF59E0B)
                            )
                        }
                    }
                )
            }
        }
    }
}
