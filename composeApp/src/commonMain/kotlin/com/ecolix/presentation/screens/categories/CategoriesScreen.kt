package com.ecolix.presentation.screens.categories

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.ecolix.data.models.Category
import com.ecolix.data.models.DashboardColors
import com.ecolix.presentation.components.CardContainer
import com.ecolix.presentation.components.ConfirmationDialog

@Composable
fun CategoriesDialog(
    isDarkMode: Boolean,
    onDismiss: () -> Unit,
    screenModel: CategoriesScreenModel
) {
    val state by screenModel.state.collectAsState()
    var showForm by remember { mutableStateOf(false) }

    LaunchedEffect(isDarkMode) {
        screenModel.onDarkModeChange(isDarkMode)
    }

    Dialog(onDismissRequest = onDismiss) {
        if (showForm) {
            CategoryForm(
                category = state.selectedCategory,
                colors = state.colors,
                onBack = { 
                    showForm = false 
                    screenModel.onSelectCategory(null)
                },
                onSave = { 
                    screenModel.saveCategory(it)
                    showForm = false
                },
                onDelete = { 
                    if (state.selectedCategory != null) {
                        screenModel.deleteCategory(state.selectedCategory!!.id)
                        showForm = false
                    }
                }
            )
        } else {
            CategoriesListContent(
                state = state,
                onDismiss = onDismiss,
                onQueryChange = { screenModel.onSearchQueryChange(it) },
                onAddClick = { 
                    screenModel.onSelectCategory(null)
                    showForm = true 
                },
                onCategoryClick = { 
                    screenModel.onSelectCategory(it)
                    showForm = true
                },
                onSeedClick = { screenModel.seedDefaultCategories() }
            )
        }
    }
}

@Composable
fun CategoriesListContent(
    state: com.ecolix.data.models.CategoriesUiState,
    onDismiss: (() -> Unit)? = null,
    onQueryChange: (String) -> Unit,
    onAddClick: () -> Unit,
    onCategoryClick: (Category) -> Unit,
    onSeedClick: () -> Unit
) {
    CardContainer(containerColor = state.colors.card) {
        Column(
            modifier = Modifier.fillMaxWidth().heightIn(max = 600.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primary)
                            .padding(8.dp)
                    ) {
                        Icon(Icons.Default.Category, contentDescription = null, tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Catégories", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = state.colors.textPrimary)
                        Text("Gestion des catégories", style = MaterialTheme.typography.bodySmall, color = state.colors.textMuted)
                    }
                }
                if (onDismiss != null) {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Fermer", tint = state.colors.textMuted)
                    }
                }
            }

            HorizontalDivider(color = state.colors.divider)

            // Actions & Search
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onAddClick,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Ajouter")
                }
                
                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = onQueryChange,
                    placeholder = { Text("Rechercher...", color = state.colors.textMuted) },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = state.colors.divider
                    ),
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = state.colors.textMuted) }
                )

                if (state.categories.isEmpty()) {
                    OutlinedButton(
                        onClick = onSeedClick,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Default.AutoMode, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Par défaut")
                    }
                }
            }

            // List
            val filtered = state.categories.filter { 
                it.name.contains(state.searchQuery, ignoreCase = true) 
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (filtered.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            Text("Aucune catégorie trouvée", color = state.colors.textMuted)
                        }
                    }
                } else {
                    items(filtered) { category ->
                        CategoryRow(category = category, colors = state.colors, onClick = { onCategoryClick(category) })
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryRow(
    category: Category,
    colors: DashboardColors,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, colors.divider, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(16.dp).clip(CircleShape).background(category.color)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(category.name, fontWeight = FontWeight.SemiBold, color = colors.textPrimary)
            if (!category.description.isNullOrBlank()) {
                Text(category.description, style = MaterialTheme.typography.bodySmall, color = colors.textMuted, maxLines = 1)
            }
        }
        Text("#${category.order}", style = MaterialTheme.typography.bodySmall, color = colors.textMuted)
        Spacer(modifier = Modifier.width(8.dp))
        Icon(Icons.Default.Edit, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
    }
}

@Composable
fun CategoryForm(
    category: Category?,
    colors: DashboardColors,
    onBack: () -> Unit,
    onSave: (Category) -> Unit,
    onDelete: () -> Unit
) {
    var name by remember { mutableStateOf(category?.name ?: "") }
    var description by remember { mutableStateOf(category?.description ?: "") }
    var order by remember { mutableStateOf(category?.order?.toString() ?: "0") }
    var colorHex by remember { mutableStateOf(category?.colorHex ?: "#6366F1") }
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        ConfirmationDialog(
            title = "Confirmer la suppression",
            message = "Voulez-vous vraiment supprimer la catégorie \"$name\" ? Cette action peut affecter les matières associées.",
            onConfirm = {
                showDeleteDialog = false
                onDelete()
            },
            onDismiss = { showDeleteDialog = false },
            colors = colors
        )
    }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = colors.textPrimary,
        unfocusedTextColor = colors.textPrimary,
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = colors.divider.copy(alpha = if (colors.textPrimary == Color(0xFF1E293B)) 0.8f else 0.5f),
        focusedLabelColor = MaterialTheme.colorScheme.primary,
        unfocusedLabelColor = colors.textMuted
    )

    CardContainer(containerColor = colors.card) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (category == null) "Nouvelle Catégorie" else "Modifier Catégorie",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = colors.textPrimary
                )
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.Close, contentDescription = "Annuler", tint = colors.textMuted)
                }
            }
            
            HorizontalDivider(color = colors.divider)

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nom") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = fieldColors
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3,
                colors = fieldColors
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = order,
                    onValueChange = { if (it.all { c -> c.isDigit() }) order = it },
                    label = { Text("Ordre") },
                    modifier = Modifier.width(100.dp),
                    singleLine = true,
                    colors = fieldColors
                )

                Column(modifier = Modifier.weight(1f)) {
                    Text("Couleur de la catégorie", style = MaterialTheme.typography.labelSmall, color = colors.textMuted)
                    Spacer(modifier = Modifier.height(8.dp))
                    val colorOptions = listOf(
                        "#6366F1", // Indigo (Default)
                        "#4CAF50", // Green
                        "#2196F3", // Blue
                        "#F44336", // Red
                        "#FF9800", // Orange
                        "#9C27B0", // Purple
                        "#795548", // Brown
                        "#607D8B"  // Blue Grey
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        colorOptions.forEach { hex ->
                            val isSelected = colorHex.uppercase() == hex.uppercase()
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(parseHexColor(hex))
                                    .border(
                                        width = if (isSelected) 2.dp else 0.dp,
                                        color = if (isSelected) colors.textPrimary else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickable { colorHex = hex }
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (category != null) {
                    TextButton(onClick = { showDeleteDialog = true }, colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)) {
                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Supprimer")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }
                
                Button(
                    onClick = {
                        if (name.isNotBlank()) {
                            onSave(Category(
                                id = category?.id ?: "",
                                name = name,
                                description = description.ifBlank { null },
                                colorHex = colorHex,
                                order = order.toIntOrNull() ?: 0
                            ))
                        }
                    }
                ) {
                    Text("Enregistrer")
                }
            }
        }
    }
}

private fun parseHexColor(hex: String): Color {
    return try {
        Color(hex.removePrefix("#").toLong(16) or 0xFF00000000)
    } catch (e: Exception) {
        Color.Gray
    }
}
