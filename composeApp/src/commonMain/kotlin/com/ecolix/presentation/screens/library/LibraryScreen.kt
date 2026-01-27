package com.ecolix.presentation.screens.library

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecolix.data.models.*
import com.ecolix.presentation.components.SearchBar

@Composable
fun LibraryScreenContent(isDarkMode: Boolean) {
    val screenModel = remember { LibraryScreenModel() }
    val state by screenModel.state.collectAsState()

    LaunchedEffect(isDarkMode) {
        screenModel.onDarkModeChange(isDarkMode)
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isCompact = maxWidth < 800.dp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(if (isCompact) 16.dp else 24.dp),
            verticalArrangement = Arrangement.spacedBy(if (isCompact) 16.dp else 24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Bibliothèque",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = if (isCompact) 24.sp else 32.sp
                        ),
                        color = state.colors.textPrimary
                    )
                    if (!isCompact) {
                        Text(
                            text = "Gestion du catalogue, des emprunts et des retours",
                            style = MaterialTheme.typography.bodyMedium,
                            color = state.colors.textMuted
                        )
                    }
                }

                LibraryViewToggle(
                    currentMode = state.viewMode,
                    onModeChange = { screenModel.onViewModeChange(it) },
                    colors = state.colors
                )
            }

            // Search & Tools
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SearchBar(
                    query = state.searchQuery,
                    onQueryChange = { screenModel.onSearchQueryChange(it) },
                    colors = state.colors,
                    modifier = Modifier.weight(1f)
                )

                if (!isCompact) {
                    Button(
                        onClick = { /* Add book */ },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Ajouter un livre")
                    }
                }
            }

            // Content
            Box(modifier = Modifier.weight(1f)) {
                AnimatedContent(
                    targetState = state.viewMode,
                    transitionSpec = { fadeIn() togetherWith fadeOut() }
                ) { mode ->
                    when (mode) {
                        LibraryViewMode.CATALOG -> CatalogView(state, isCompact)
                        LibraryViewMode.BORROWINGS -> BorrowingsView(state, isCompact)
                        else -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("En cours de développement", color = state.colors.textMuted)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CatalogView(state: LibraryUiState, isCompact: Boolean) {
    val filteredBooks = remember(state.books, state.searchQuery) {
        state.books.filter { 
            it.title.contains(state.searchQuery, ignoreCase = true) || 
            it.author.contains(state.searchQuery, ignoreCase = true)
        }
    }

    LazyVerticalGrid(
        columns = if (isCompact) GridCells.Fixed(2) else GridCells.Adaptive(minSize = 200.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(filteredBooks) { book ->
            BookCard(book, state.colors)
        }
    }
}

@Composable
private fun BookCard(book: Book, colors: DashboardColors) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colors.card),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Placeholder for book cover
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.7f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(colors.divider.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Book,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = colors.textMuted.copy(alpha = 0.5f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = book.title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = colors.textPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = book.author,
                style = MaterialTheme.typography.bodySmall,
                color = colors.textMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            BookStatusBadge(book.status)
        }
    }
}

@Composable
private fun BookStatusBadge(status: BookStatus) {
    val (label, color) = when (status) {
        BookStatus.AVAILABLE -> "Disponible" to Color(0xFF10B981)
        BookStatus.BORROWED -> "Emprunté" to Color(0xFF3B82F6)
        BookStatus.RESERVED -> "Réservé" to Color(0xFFF59E0B)
        BookStatus.LOST -> "Perdu" to Color(0xFFEF4444)
        BookStatus.DAMAGED -> "Endommagé" to Color(0xFF71717A)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(color.copy(alpha = 0.1f))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = color
        )
    }
}

@Composable
private fun BorrowingsView(state: LibraryUiState, isCompact: Boolean) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Table Header
        if (!isCompact) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(state.colors.divider.copy(alpha = 0.3f))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("Livre", modifier = Modifier.weight(2f), fontWeight = FontWeight.Bold)
                    Text("Élève", modifier = Modifier.weight(1.5f), fontWeight = FontWeight.Bold)
                    Text("Date d'emprunt", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
                    Text("Date de retour", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
                    Text("Statut", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
                }
            }
        }

        items(state.borrowings) { borrowing ->
            BorrowingRow(borrowing, state.colors, isCompact)
        }
    }
}

@Composable
private fun BorrowingRow(borrowing: Borrowing, colors: DashboardColors, isCompact: Boolean) {
    if (isCompact) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = colors.card)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(borrowing.bookTitle, fontWeight = FontWeight.Bold, color = colors.textPrimary)
                Text(borrowing.studentName, style = MaterialTheme.typography.bodySmall, color = colors.textMuted)
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Échéance: ${borrowing.dueDate}", style = MaterialTheme.typography.labelSmall, color = colors.textMuted)
                    BorrowingStatusBadge(borrowing.status)
                }
            }
        }
    } else {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(colors.card)
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(borrowing.bookTitle, modifier = Modifier.weight(2f), color = colors.textPrimary)
            Column(modifier = Modifier.weight(1.5f)) {
                Text(borrowing.studentName, color = colors.textPrimary)
                Text(borrowing.classroom, style = MaterialTheme.typography.bodySmall, color = colors.textMuted)
            }
            Text(borrowing.borrowDate, modifier = Modifier.weight(1f), color = colors.textMuted)
            Text(borrowing.dueDate, modifier = Modifier.weight(1f), color = if (borrowing.status == BorrowingStatus.OVERDUE) Color(0xFFEF4444) else colors.textMuted)
            Box(modifier = Modifier.weight(1f)) {
                BorrowingStatusBadge(borrowing.status)
            }
        }
    }
}

@Composable
private fun BorrowingStatusBadge(status: BorrowingStatus) {
    val (label, color) = when (status) {
        BorrowingStatus.ONGOING -> "En cours" to Color(0xFF3B82F6)
        BorrowingStatus.RETURNED -> "Rendu" to Color(0xFF10B981)
        BorrowingStatus.OVERDUE -> "Retard" to Color(0xFFEF4444)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(color.copy(alpha = 0.1f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = color
        )
    }
}

@Composable
private fun LibraryViewToggle(
    currentMode: LibraryViewMode,
    onModeChange: (LibraryViewMode) -> Unit,
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
            Triple(LibraryViewMode.CATALOG, "Catalogue", Icons.Default.MenuBook),
            Triple(LibraryViewMode.BORROWINGS, "Emprunts", Icons.Default.AssignmentReturn)
        )

        tabs.forEach { (mode, label, icon) ->
            val isSelected = currentMode == mode
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                    .clickable { onModeChange(mode) }
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
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
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                )
            }
        }
    }
}
