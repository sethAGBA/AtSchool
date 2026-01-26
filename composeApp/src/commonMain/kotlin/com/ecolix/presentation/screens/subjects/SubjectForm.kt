package com.ecolix.presentation.screens.subjects

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecolix.data.models.Subject
import com.ecolix.data.models.DashboardColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectForm(
    subject: Subject? = null,
    categories: List<com.ecolix.data.models.Category> = emptyList(),
    colors: DashboardColors,
    isCompact: Boolean = false,
    onBack: () -> Unit,
    onSave: (Subject) -> Unit
) {
    var name by remember { mutableStateOf(subject?.name ?: "") }
    var code by remember { mutableStateOf(subject?.code ?: "") }
    var selectedCategoryId by remember { mutableStateOf(subject?.categoryId) }
    var coefficient by remember { mutableStateOf(subject?.defaultCoefficient?.toString() ?: "1") }
    // Color is now derived from category if not explicitly set, but subjects can still have their own color
    // For now, we'll initialize with subject color, or fall back to category color if selected, or default
    var color by remember { 
        mutableStateOf(
            subject?.color ?: 
            (categories.find { it.id == selectedCategoryId }?.color ?: Color(0xFF6366F1))
        ) 
    }
    var description by remember { mutableStateOf(subject?.description ?: "") }

    // Update color when category changes if subject is new (optional UX choice)
    LaunchedEffect(selectedCategoryId) {
        if (subject == null) {
            val catColor = categories.find { it.id == selectedCategoryId }?.color
            if (catColor != null) {
                color = catColor
            }
        }
    }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = colors.textPrimary,
        unfocusedTextColor = colors.textPrimary,
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = colors.divider.copy(alpha = if (colors.textPrimary == Color(0xFF1E293B)) 0.8f else 0.5f),
        focusedLabelColor = MaterialTheme.colorScheme.primary,
        unfocusedLabelColor = colors.textMuted
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(if (isCompact) 16.dp else 24.dp),
        verticalArrangement = Arrangement.spacedBy(if (isCompact) 16.dp else 24.dp)
    ) {
        // Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = colors.textPrimary)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (subject == null) "Nouvelle Matiere" else "Modifier: ${subject.name}",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = colors.textPrimary
            )
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(28.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        "IDENTIFICATION",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Black, letterSpacing = 1.2.sp),
                        color = MaterialTheme.colorScheme.primary
                    )
                    HorizontalDivider(color = colors.divider.copy(alpha = 0.5f))
                    
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nom de la matiere *") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = fieldColors
                    )

                    OutlinedTextField(
                        value = code,
                        onValueChange = { code = it },
                        label = { Text("Code (ex: MATH, FRAN) *") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = fieldColors
                    )
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        "PARAMETRES ACADEMIQUES",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Black, letterSpacing = 1.2.sp),
                        color = MaterialTheme.colorScheme.primary
                    )
                    HorizontalDivider(color = colors.divider.copy(alpha = 0.5f))

                    // Category Selection
                    Text("Categorie", style = MaterialTheme.typography.labelMedium, color = colors.textMuted)
                    if (categories.isEmpty()) {
                        Text("Aucune catégorie disponible", style = MaterialTheme.typography.bodySmall, color = colors.textMuted)
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            categories.forEach { cat ->
                                FilterChip(
                                    selected = selectedCategoryId == cat.id,
                                    onClick = { selectedCategoryId = cat.id },
                                    label = { Text(cat.name) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = cat.color.copy(alpha = 0.2f),
                                        selectedLabelColor = MaterialTheme.colorScheme.primary
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        borderColor = if (selectedCategoryId == cat.id) cat.color else colors.divider,
                                        selected = selectedCategoryId == cat.id,
                                        enabled = true
                                    )
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = coefficient,
                        onValueChange = { if (it.all { char -> char.isDigit() || char == '.' }) coefficient = it },
                        label = { Text("Coefficient par defaut") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = fieldColors
                    )
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        "DESCRIPTION ET STYLE",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Black, letterSpacing = 1.2.sp),
                        color = MaterialTheme.colorScheme.primary
                    )
                    HorizontalDivider(color = colors.divider.copy(alpha = 0.5f))

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description (Optionnel)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        minLines = 3,
                        colors = fieldColors
                    )

                    // Simple Color Selection
                    Text("Couleur de la matiere", style = MaterialTheme.typography.labelMedium, color = colors.textMuted)
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        val colorOptions = listOf(
                            Color(0xFF3B82F6), // Blue
                            Color(0xFFEF4444), // Red
                            Color(0xFF10B981), // Green
                            Color(0xFFF59E0B), // Orange
                            Color(0xFF8B5CF6), // Purple
                            Color(0xFFEC4899), // Pink
                            Color(0xFF06B6D4)  // Cyan
                        )
                        colorOptions.forEach { col ->
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(col)
                                    .border(
                                        width = if (color == col) 3.dp else 0.dp,
                                        color = if (color == col) colors.textPrimary else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickable { color = col }
                            )
                        }
                    }
                }
            }
        }

        Button(
            onClick = {
                if (name.isNotBlank() && code.isNotBlank()) {
                    val selectedCat = categories.find { it.id == selectedCategoryId }
                    onSave(
                        Subject(
                            id = subject?.id ?: "SUBJ_${code.uppercase()}",
                            name = name,
                            code = code,
                            categoryId = selectedCategoryId,
                            categoryName = selectedCat?.name ?: "Non classée",
                            categoryColorHex = selectedCat?.colorHex ?: "#6366F1",
                            defaultCoefficient = coefficient.toFloatOrNull() ?: 1f,
                            description = if (description.isNotBlank()) description else null
                        )
                    )
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colors.textLink,
                contentColor = Color.White
            )
        ) {
            Icon(Icons.Filled.Save, contentDescription = null)
            Spacer(modifier = Modifier.width(12.dp))
            Text("Enregistrer la matière", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}
