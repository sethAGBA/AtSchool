package com.ecolix.presentation.screens.staff

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecolix.atschool.models.Staff
import com.ecolix.atschool.models.StaffRole
import com.ecolix.data.models.DashboardColors
import com.ecolix.data.models.StaffViewMode
import com.ecolix.presentation.components.*
import org.koin.compose.koinInject

@Composable
fun StaffScreenContent(isDarkMode: Boolean) {
    val screenModel: StaffScreenModel = koinInject()
    val state by screenModel.state.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            screenModel.clearError()
        }
    }

    LaunchedEffect(state.successMessage) {
        state.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            screenModel.clearSuccess()
        }
    }

    LaunchedEffect(isDarkMode) {
        screenModel.onDarkModeChange(isDarkMode)
    }
    
    val filteredStaff = remember(state.staffMembers, state.searchQuery, state.roleFilter, state.departmentFilter, state.selectedGender, state.statusFilter) {
        state.staffMembers.filter { member ->
            val matchRole = state.roleFilter == null || member.role == state.roleFilter
            val matchDept = state.departmentFilter == null || member.department == state.departmentFilter
            val matchGender = state.selectedGender == null || member.gender == state.selectedGender
            val matchStatus = state.statusFilter == null || member.status == state.statusFilter
            val matchSearch = state.searchQuery.isEmpty() || 
                             "${member.firstName} ${member.lastName}".contains(state.searchQuery, ignoreCase = true) ||
                             member.matricule?.contains(state.searchQuery, ignoreCase = true) == true
            
            matchRole && matchDept && matchGender && matchStatus && matchSearch
        }
    }

    val visibleStaff = remember(filteredStaff, state.loadedCount) {
        filteredStaff.take(state.loadedCount)
    }

    // Scroll Detection for Lazy Load
    val listState = rememberLazyListState()
    val gridState = rememberLazyGridState()

    if (state.viewMode == StaffViewMode.LIST) {
        val endReached by remember {
            derivedStateOf {
                val layoutInfo = listState.layoutInfo
                val visibleItemsInfo = layoutInfo.visibleItemsInfo
                if (layoutInfo.totalItemsCount == 0) false
                else {
                    val lastVisibleItem = visibleItemsInfo.lastOrNull()
                    lastVisibleItem != null && lastVisibleItem.index >= layoutInfo.totalItemsCount - 1
                }
            }
        }
        LaunchedEffect(endReached) {
            if (endReached && filteredStaff.size > state.loadedCount) {
                screenModel.loadMore()
            }
        }
    } else if (state.viewMode == StaffViewMode.GRID) {
        val endReached by remember {
            derivedStateOf {
                val layoutInfo = gridState.layoutInfo
                val visibleItemsInfo = layoutInfo.visibleItemsInfo
                if (layoutInfo.totalItemsCount == 0) false
                else {
                    val lastVisibleItem = visibleItemsInfo.lastOrNull()
                    lastVisibleItem != null && lastVisibleItem.index >= layoutInfo.totalItemsCount - 1
                }
            }
        }
        LaunchedEffect(endReached) {
            if (endReached && filteredStaff.size > state.loadedCount) {
                screenModel.loadMore()
            }
        }
    }

    var showDeleteStaffDialog by remember { mutableStateOf(false) }

    if (showDeleteStaffDialog) {
        ConfirmationDialog(
            title = "Supprimer la sélection ?",
            message = "Êtes-vous sûr de vouloir supprimer ${state.selectedStaffIds.size} membre(s) du personnel ? Cette action est irréversible.",
            onConfirm = {
                screenModel.deleteSelectedStaff()
                showDeleteStaffDialog = false
            },
            onDismiss = { showDeleteStaffDialog = false },
            colors = state.colors
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = state.colors.background,
        modifier = Modifier.fillMaxSize()
    ) { padding ->
        BoxWithConstraints(modifier = Modifier.fillMaxSize().padding(padding)) {
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
                        text = "Gestion du Personnel",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = if (isCompact) 24.sp else 32.sp
                        ),
                        color = state.colors.textPrimary
                    )
                    Text(
                        text = "Gerez les enseignants et le personnel administratif",
                        style = MaterialTheme.typography.bodyMedium,
                        color = state.colors.textMuted
                    )
                }
                
                if (!isCompact) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        StaffViewToggle(
                            currentMode = state.viewMode,
                            onModeChange = { screenModel.onViewModeChange(it) },
                            colors = state.colors
                        )
                    }
                }
            }

            // Stats Section (Only when listing)
            if (state.viewMode == StaffViewMode.LIST || state.viewMode == StaffViewMode.GRID) {
                StaffStatsSection(
                    total = state.staffMembers.size,
                    teachers = state.staffMembers.count { it.role == StaffRole.TEACHER },
                    admins = state.staffMembers.count { 
                        it.role == StaffRole.PRINCIPAL || 
                        it.role == StaffRole.ADMIN || 
                        it.role == StaffRole.DIRECTOR || 
                        it.role == StaffRole.CENSOR ||
                        it.role == StaffRole.SECRETARY ||
                        it.role == StaffRole.ACCOUNTANT
                    },
                    onLeave = state.staffMembers.count { it.status == "En congé" },
                    colors = state.colors,
                    isCompact = isCompact
                )
                
                if (state.searchQuery.isEmpty()) {
                    StaffDistributionChart(distribution = state.roleDistribution, colors = state.colors)
                }
            }

            // Action Bar & Filters
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SearchBar(
                        query = state.searchQuery,
                        onQueryChange = { screenModel.onSearchQueryChange(it) },
                        colors = state.colors,
                        modifier = Modifier.weight(1f)
                    )
                    
                    
                    if (state.viewMode == StaffViewMode.LIST || state.viewMode == StaffViewMode.GRID) {
                        IconButton(
                            onClick = { screenModel.onToggleSelectionMode() },
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = if (state.selectionMode) MaterialTheme.colorScheme.primary else state.colors.card,
                                contentColor = if (state.selectionMode) Color.White else state.colors.textPrimary
                            )
                        ) {
                            Icon(Icons.Default.Checklist, contentDescription = "Sélectionner")
                        }
                        
                        IconButton(
                            onClick = { 
                                screenModel.onViewModeChange(StaffViewMode.LIST)
                                screenModel.refreshStaff() 
                            },
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = state.colors.card,
                                contentColor = state.colors.textPrimary
                            )
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "Actualiser")
                        }
                    }

                    Button(
                        onClick = { screenModel.onViewModeChange(StaffViewMode.FORM) },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Default.Add, null)
                        if (!isCompact) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Ajouter", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                if (state.viewMode == StaffViewMode.LIST || state.viewMode == StaffViewMode.GRID) {
                    // Department & Role Chips
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        item {
                            FilterDropdown(
                                label = "Rôle",
                                selectedLabel = state.roleFilter?.label ?: "Tous",
                                onSelect = { 
                                    val role = StaffRole.entries.find { r -> r.label == it }
                                    screenModel.onRoleFilterChange(role) 
                                },
                                items = listOf("Tous") + StaffRole.entries.map { it.label },
                                colors = state.colors
                            )
                        }
                        item {
                            FilterDropdown(
                                label = "Dép.",
                                selectedLabel = state.departmentFilter ?: "Tous",
                                onSelect = { screenModel.onDepartmentFilterChange(it) },
                                items = state.departments,
                                colors = state.colors
                            )
                        }
                        item {
                            FilterDropdown(
                                label = "Statut",
                                selectedLabel = state.statusFilter ?: "Tous",
                                onSelect = { screenModel.onStatusFilterChange(it) },
                                items = listOf("Tous", "Actif", "En congé", "Inactif"),
                                colors = state.colors
                            )
                        }
                        item {
                            FilterDropdown(
                                label = "Sexe",
                                selectedLabel = if (state.selectedGender == "M") "Masculin" else if (state.selectedGender == "F") "Féminin" else "Tous",
                                onSelect = { 
                                    val gender = if (it == "Masculin") "M" else if (it == "Féminin") "F" else "Tous"
                                    screenModel.onGenderFilterChange(gender) 
                                },
                                items = listOf("Tous", "Masculin", "Féminin"),
                                colors = state.colors
                            )
                        }
                    }
                }
            }

            // Main List/Grid
            Box(modifier = Modifier.weight(1f)) {
                when (state.viewMode) {
                    StaffViewMode.LIST -> {
                        LazyColumn(
                            state = listState,
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 80.dp)
                        ) {
                            items(visibleStaff) { member ->
                                StaffRow(
                                    staff = member, 
                                    colors = state.colors, 
                                    selectionMode = state.selectionMode,
                                    isSelected = state.selectedStaffIds.contains(member.id),
                                    onToggleSelect = { screenModel.onToggleStaffSelection(member.id) },
                                    onClick = { screenModel.onSelectStaff(member.id) }
                                )
                            }
                            
                            if (filteredStaff.size > state.loadedCount) {
                                item {
                                    Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                                        CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                                    }
                                }
                            }

                            if (filteredStaff.isEmpty()) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 24.dp, vertical = 64.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.spacedBy(16.dp)
                                        ) {
                                            Text(
                                                text = "👔",
                                                style = MaterialTheme.typography.displayMedium.copy(fontSize = 48.sp)
                                            )
                                            Text(
                                                text = "Aucun membre du personnel trouvé",
                                                style = MaterialTheme.typography.titleMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 18.sp
                                                ),
                                                color = state.colors.textPrimary
                                            )
                                            Text(
                                                text = if (state.searchQuery.isNotEmpty())
                                                    "Aucun membre du personnel ne correspond à votre recherche."
                                                else
                                                    "Commencez par ajouter des membres du personnel via le bouton Ajouter.",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = state.colors.textMuted,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    StaffViewMode.GRID -> {
                        LazyVerticalGrid(
                            state = gridState,
                            columns = GridCells.Adaptive(minSize = 280.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 80.dp)
                        ) {
                            items(visibleStaff) { member ->
                                StaffCard(
                                    staff = member, 
                                    colors = state.colors, 
                                    selectionMode = state.selectionMode,
                                    isSelected = state.selectedStaffIds.contains(member.id),
                                    onToggleSelect = { screenModel.onToggleStaffSelection(member.id) },
                                    onClick = { screenModel.onSelectStaff(member.id) }
                                )
                            }
                            
                            if (filteredStaff.size > state.loadedCount) {
                                item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(maxLineSpan) }) {
                                    Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                                        CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                                    }
                                }
                            }

                            if (filteredStaff.isEmpty()) {
                                item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(maxLineSpan) }) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 24.dp, vertical = 64.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.spacedBy(16.dp)
                                        ) {
                                            Text(
                                                text = "👔",
                                                style = MaterialTheme.typography.displayMedium.copy(fontSize = 48.sp)
                                            )
                                            Text(
                                                text = "Aucun membre du personnel trouvé",
                                                style = MaterialTheme.typography.titleMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 18.sp
                                                ),
                                                color = state.colors.textPrimary
                                            )
                                            Text(
                                                text = if (state.searchQuery.isNotEmpty())
                                                    "Aucun membre du personnel ne correspond à votre recherche."
                                                else
                                                    "Commencez par ajouter des membres du personnel via le bouton Ajouter.",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = state.colors.textMuted,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    StaffViewMode.PROFILE -> {
                        val member = state.staffMembers.find { it.id == state.selectedStaffId }
                        if (member != null) {
                            StaffProfileScreen(
                                staff = member,
                                colors = state.colors,
                                isCompact = isCompact,
                                onBack = { screenModel.onViewModeChange(StaffViewMode.LIST) },
                                onEdit = { screenModel.onViewModeChange(StaffViewMode.FORM) }
                            )
                        }
                    }
                    StaffViewMode.FORM -> {
                        val member = state.staffMembers.find { it.id == state.selectedStaffId }
                        StaffForm(
                            staff = member,
                            colors = state.colors,
                            isCompact = isCompact,
                            onBack = { screenModel.onViewModeChange(StaffViewMode.LIST) },
                            onSave = { updatedStaff ->
                                screenModel.saveStaff(updatedStaff)
                            }
                        )
                    }
                    StaffViewMode.MANAGEMENT -> {
                        StaffManagementScreen(
                            currentDepartments = state.departments,
                            colors = state.colors,
                            onAddDepartment = { screenModel.addDepartment(it) },
                            onDeleteDepartment = { screenModel.deleteDepartment(it) }
                        )
                    }
                    StaffViewMode.TRASH -> {
                        StaffTrashScreen(
                            deletedStaff = state.staffMembers, // In TRASH mode, staffMembers contains deleted items
                            colors = state.colors,
                            onRestore = { screenModel.restoreStaff(it) },
                            onPermanentDelete = { screenModel.permanentDeleteStaff(it) }
                        )
                    }
                }
            }
        }

        // Floating Selection Bar
        AnimatedVisibility(
            visible = state.selectionMode && state.selectedStaffIds.isNotEmpty(),
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter).padding(24.dp)
        ) {
            StaffSelectionActionBar(
                selectedCount = state.selectedStaffIds.size,
                onClearSelection = { screenModel.onClearSelection() },
                onDeleteSelected = { showDeleteStaffDialog = true },
                onStatusChange = { screenModel.updateSelectedStaffStatus(it) },
                colors = state.colors,
                isCompact = isCompact
            )
        }
    }
}
}

@Composable
fun FilterDropdown(
    label: String,
    selectedLabel: String,
    onSelect: (String) -> Unit,
    items: List<String>,
    colors: DashboardColors
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Surface(
            modifier = Modifier
                .clickable { expanded = true }
                .border(1.dp, colors.divider, RoundedCornerShape(8.dp)),
            shape = RoundedCornerShape(8.dp),
            color = colors.card
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("$label: ", style = MaterialTheme.typography.labelSmall, color = colors.textMuted)
                Text(selectedLabel, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = colors.textPrimary)
                Spacer(modifier = Modifier.width(4.dp))
                Icon(Icons.Default.ArrowDropDown, null, modifier = Modifier.size(16.dp), tint = colors.textMuted)
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(colors.card)
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item, color = colors.textPrimary) },
                    onClick = {
                        onSelect(item)
                        expanded = false
                    }
                )
            }
        }
    }
}
