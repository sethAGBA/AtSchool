package com.ecolix.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.PersonPin
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecolix.data.models.Subject
import com.ecolix.data.models.DashboardColors
import com.ecolix.data.models.SubjectsViewMode
import com.ecolix.data.models.SubjectsLayoutMode
import com.ecolix.presentation.components.CardContainer

@Composable
fun SubjectCard(
    subject: Subject,
    colors: DashboardColors,
    onClick: () -> Unit
) {
    CardContainer(
        containerColor = colors.card,
        modifier = Modifier.clickable { onClick() }
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(subject.color.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        getCategoryIcon(subject.categoryName),
                        contentDescription = null,
                        tint = subject.color,
                        modifier = Modifier.size(24.dp)
                    )
                }

                TagPill(
                    label = "Coeff: ${subject.defaultCoefficient}",
                    color = subject.color,
                    isSmall = true
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = subject.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = colors.textPrimary,
                    maxLines = 1
                )
                Text(
                    text = subject.code,
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                    color = colors.textMuted
                )
            }

            HorizontalDivider(color = colors.divider, thickness = 0.5.dp)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = subject.categoryName,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textMuted
                )

                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = colors.textMuted
                )
            }
        }
    }
}

@Composable
fun SubjectRow(
    subject: Subject,
    colors: DashboardColors,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(colors.card)
            .border(1.dp, colors.divider, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(subject.color.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                getCategoryIcon(subject.categoryName),
                contentDescription = null,
                tint = subject.color,
                modifier = Modifier.size(20.dp)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = subject.name,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = colors.textPrimary
            )
            Text(
                text = "${subject.code} • ${subject.categoryName}",
                style = MaterialTheme.typography.bodySmall,
                color = colors.textMuted
            )
        }

        TagPill(
            label = "Coeff: ${subject.defaultCoefficient}",
            color = subject.color,
            isSmall = true
        )

        Icon(
            Icons.Default.ChevronRight,
            contentDescription = null,
            tint = colors.textMuted
        )
    }
}

@Composable
fun SubjectsViewToggle(
    currentMode: SubjectsViewMode,
    onModeChange: (SubjectsViewMode) -> Unit,
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
        listOf<Triple<SubjectsViewMode, String, ImageVector>>(
            Triple(SubjectsViewMode.SUBJECTS, "Matières", Icons.AutoMirrored.Filled.LibraryBooks),
            Triple(SubjectsViewMode.CATEGORIES, "Catégories", Icons.Filled.Category),
            Triple(SubjectsViewMode.PROFESSORS, "Affectations", Icons.Filled.PersonPin),
            Triple(SubjectsViewMode.CONFIG, "Config", Icons.Filled.Tune)
        ).forEach { (mode, label, icon) ->
            SubjectsToggleItem(
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
private fun SubjectsToggleItem(
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
fun SubjectActionBar(
    onAddSubjectClick: () -> Unit,
    colors: DashboardColors,
    isCompact: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(if (isCompact) 8.dp else 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ActionButton(
            icon = Icons.Filled.Add,
            label = if (isCompact) "Ajouter" else "Ajouter une Matière",
            bg = MaterialTheme.colorScheme.primary,
            contentColor = Color.White,
            onClick = onAddSubjectClick,
            modifier = if (isCompact) Modifier.weight(1f) else Modifier
        )

        if (!isCompact) {
            ActionButton(
                icon = Icons.Filled.FileUpload,
                label = "Importer",
                bg = colors.card,
                contentColor = colors.textPrimary,
                modifier = Modifier,
                colors = colors,
                onClick = {}
            )

            ActionButton(
                icon = Icons.Filled.FileDownload,
                label = "Exporter",
                bg = colors.card,
                contentColor = colors.textPrimary,
                modifier = Modifier,
                colors = colors,
                onClick = {}
            )
        }

        Spacer(modifier = Modifier.weight(1f))
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
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp), tint = contentColor)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            color = contentColor,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

private fun getCategoryIcon(categoryName: String): ImageVector {
    return when (categoryName) {
        "Scientifique" -> Icons.Filled.Science
        "Littéraire" -> Icons.Filled.HistoryEdu
        "Arts & Sports", "Sports" -> Icons.Filled.Palette
        "Technique" -> Icons.Filled.Memory
        else -> Icons.AutoMirrored.Filled.MenuBook
    }
}
