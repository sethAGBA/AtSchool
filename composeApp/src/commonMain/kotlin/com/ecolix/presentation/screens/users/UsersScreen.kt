package com.ecolix.presentation.screens.users

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import cafe.adriel.voyager.core.screen.Screen
// import cafe.adriel.voyager.koin.getScreenModel
import com.ecolix.data.models.DashboardColors
import com.ecolix.data.models.User
import com.ecolix.data.models.UserRole
import com.ecolix.data.models.AppsUiState
import com.ecolix.presentation.components.CardContainer
import com.ecolix.presentation.components.TagPill
import com.ecolix.presentation.components.SearchBar
import com.ecolix.presentation.components.UserForm

data class UsersScreen(val isDarkMode: Boolean = false) : Screen {
    @Composable
    override fun Content() {
        UsersScreenContent(isDarkMode)
    }
}

@Composable
fun UsersScreenContent(isDarkMode: Boolean) {
    val screenModel = remember { UsersScreenModel() }
    val state by screenModel.state.collectAsState()
    
    LaunchedEffect(isDarkMode) {
        screenModel.onDarkModeChange(isDarkMode)
    }

    val filteredUsers = remember(state.users, state.viewMode, state.searchQuery, state.roleFilter) {
        state.users.filter { user ->
            val matchTab = when (state.viewMode) {
                com.ecolix.data.models.UsersViewMode.ADMINS -> user.role == UserRole.ADMIN || user.role == UserRole.MANAGER
                com.ecolix.data.models.UsersViewMode.TEACHERS -> user.role == UserRole.TEACHER
                com.ecolix.data.models.UsersViewMode.PARENTS -> user.role == UserRole.PARENT
            }
            val matchSearch = state.searchQuery.isEmpty() || 
                              user.username.contains(state.searchQuery, ignoreCase = true) || 
                              user.email.contains(state.searchQuery, ignoreCase = true)
            val matchRole = state.roleFilter == null || user.role == state.roleFilter
            val matchStatus = state.statusFilter == null || user.status == state.statusFilter
            matchTab && matchSearch && matchRole && matchStatus
        }
    }

    val visibleUsers = remember(filteredUsers, state.loadedUsersCount) {
        filteredUsers.take(state.loadedUsersCount)
    }

    val listState = androidx.compose.foundation.lazy.rememberLazyListState()
    
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
        if (endReached && filteredUsers.size > state.loadedUsersCount) {
            screenModel.loadMoreUsers()
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isCompact = maxWidth < 800.dp
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(if (isCompact) 16.dp else 24.dp),
            verticalArrangement = Arrangement.spacedBy(if (isCompact) 16.dp else 24.dp)
        ) {
            UsersHeader(
                state = state,
                isCompact = isCompact,
                onViewModeChange = screenModel::onViewModeChange,
                onAddUserClick = screenModel::onAddUserClick,
                onSearchChange = screenModel::onSearchQueryChange,
                onRoleChange = screenModel::onRoleFilterChange,
                onStatusChange = screenModel::onStatusFilterChange
            )

            // Content List
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, state.colors.divider.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .background(state.colors.card),
                verticalArrangement = Arrangement.Top,
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                // Table Header Row (Desktop only)
                if (!isCompact) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.05f))
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("UTILISATEUR", modifier = Modifier.weight(2f), style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = state.colors.textMuted)
                            Text("ROLE", modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = state.colors.textMuted)
                            Text("EMAIL", modifier = Modifier.weight(2f), style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = state.colors.textMuted)
                            Text("DERNIERE CONNEXION", modifier = Modifier.weight(1.5f), style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = state.colors.textMuted)
                            Text("STATUT", modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = state.colors.textMuted)
                            Spacer(modifier = Modifier.width(100.dp)) // Actions
                        }
                        HorizontalDivider(color = state.colors.divider, thickness = 1.dp)
                    }
                }
                
                if (filteredUsers.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) {
                            Text("Aucun utilisateur trouvé", color = state.colors.textMuted)
                        }
                    }
                }

                itemsIndexed(visibleUsers) { index, user ->
                    if (isCompact) {
                        UserCard(
                            user = user,
                            colors = state.colors,
                            onClick = { screenModel.onUserClick(user.id) }
                        )
                    } else {
                        UserRow(
                            user = user,
                            colors = state.colors,
                            isAlternate = index % 2 == 1,
                            onEdit = { screenModel.onUserClick(user.id) },
                            onDelete = { screenModel.onDeleteUser(user.id) },
                            onToggleStatus = { screenModel.onToggleUserStatus(user.id) }
                        )
                        HorizontalDivider(color = state.colors.divider.copy(alpha = 0.3f), thickness = 0.5.dp)
                    }
                }

                if (filteredUsers.size > state.loadedUsersCount) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
        
        if (state.showUserDialog) {
            val selectedUser = state.selectedUserId?.let { id -> state.users.find { it.id == id } }
            AlertDialog(
                onDismissRequest = screenModel::onDismissDialog,
                containerColor = state.colors.card,
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (state.isEditing) "Modifier l'Utilisateur" else "Nouvel Utilisateur",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = state.colors.textPrimary
                        )
                        IconButton(onClick = screenModel::onDismissDialog) {
                            Icon(Icons.Default.Close, null, tint = state.colors.textMuted)
                        }
                    }
                },
                text = {
                    UserForm(
                        user = selectedUser,
                        colors = state.colors,
                        isCompact = isCompact,
                        onSave = screenModel::onSaveUser
                    )
                },
                confirmButton = {} // Handled inside UserForm
            )
        }

        state.userToDelete?.let { user ->
            com.ecolix.presentation.components.ConfirmationDialog(
                title = "Supprimer l'Utilisateur",
                message = "Êtes-vous sûr de vouloir supprimer l'utilisateur ${user.fullName} (@${user.username}) ? Cette action est irréversible.",
                onConfirm = screenModel::confirmDeleteUser,
                onDismiss = screenModel::onDismissDeleteConfirmation,
                colors = state.colors
            )
        }
    }
}

@Composable
fun UsersHeader(
    state: AppsUiState,
    isCompact: Boolean,
    onViewModeChange: (com.ecolix.data.models.UsersViewMode) -> Unit,
    onAddUserClick: () -> Unit,
    onSearchChange: (String) -> Unit,
    onRoleChange: (UserRole?) -> Unit,
    onStatusChange: (String?) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(if (isCompact) 16.dp else 24.dp)) {
        if (isCompact) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Gestion Utilisateurs",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = state.colors.textPrimary
                )
                Text(
                    text = "Gérez les comptes d'accès et profils",
                    style = MaterialTheme.typography.bodySmall,
                    color = state.colors.textMuted
                )
                
                com.ecolix.presentation.components.UsersViewToggle(
                    currentMode = state.viewMode,
                    onModeChange = onViewModeChange,
                    colors = state.colors,
                    modifier = Modifier.fillMaxWidth(),
                    isFullWidth = true
                )
                
                com.ecolix.presentation.components.UsersActionBar(
                    onAddUserClick = onAddUserClick,
                    colors = state.colors,
                    isCompact = true
                )
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Gestion des Utilisateurs",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 32.sp
                        ),
                        color = state.colors.textPrimary
                    )
                    Text(
                        text = "Gérez les comptes d'accès et les profils parents de l'établissement",
                        style = MaterialTheme.typography.bodyMedium,
                        color = state.colors.textMuted
                    )
                }
                
                com.ecolix.presentation.components.UsersViewToggle(
                    currentMode = state.viewMode,
                    onModeChange = onViewModeChange,
                    colors = state.colors
                )
            }

            com.ecolix.presentation.components.UsersActionBar(
                onAddUserClick = onAddUserClick,
                colors = state.colors,
                isCompact = false
            )
        }
        
        HorizontalDivider(color = state.colors.divider.copy(alpha = 0.5f))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            com.ecolix.presentation.components.SearchBar(
                query = state.searchQuery,
                onQueryChange = onSearchChange,
                colors = state.colors,
                modifier = Modifier.weight(1f)
            )
        }

        com.ecolix.presentation.components.UsersAdvancedFilters(
            selectedRole = state.roleFilter,
            onRoleChange = onRoleChange,
            selectedStatus = state.statusFilter,
            onStatusChange = onStatusChange,
            colors = state.colors,
            isCompact = isCompact
        )
    }
}

@Composable
fun UserRow(
    user: User,
    colors: DashboardColors,
    isAlternate: Boolean = false,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggleStatus: () -> Unit
) {
    val bg = if (isAlternate) colors.background.copy(alpha = 0.5f) else Color.Transparent
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(bg)
            .clickable { onEdit() }
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // User Info
            Row(modifier = Modifier.weight(2f), verticalAlignment = Alignment.CenterVertically) {
                 Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(user.role.color.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(user.role.icon, null, tint = user.role.color, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(user.fullName, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = colors.textPrimary)
                    Text("@${user.username}", style = MaterialTheme.typography.bodySmall, color = colors.textMuted)
                }
            }

            // Role
            Box(modifier = Modifier.weight(1f)) {
                 TagPill(user.role.label, user.role.color, isSmall = true)
            }

            // Email
            Text(user.email, modifier = Modifier.weight(2f), style = MaterialTheme.typography.bodyMedium, color = colors.textPrimary)

            // Last Login
            Text(
                user.lastLogin ?: "Jamais", 
                modifier = Modifier.weight(1.5f), 
                style = MaterialTheme.typography.bodyMedium, 
                color = if (user.lastLogin == null) colors.textMuted else colors.textPrimary
            )

            // Status
            Box(modifier = Modifier.weight(1f)) {
                val statusColor = when(user.status) {
                    "Actif" -> Color(0xFF10B981)
                    "Inactif" -> Color(0xFF64748B)
                    else -> Color(0xFFEF4444)
                }
                 TagPill(user.status, statusColor, isSmall = true)
            }

            // Actions
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(onClick = onToggleStatus) {
                    Icon(
                        if (user.status == "Actif") Icons.Default.Block else Icons.Default.CheckCircle, 
                        null, 
                        tint = if (user.status == "Actif") Color(0xFFEF4444).copy(alpha = 0.7f) else Color(0xFF10B981).copy(alpha = 0.7f),
                        modifier = Modifier.size(20.dp)
                    )
                }
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, null, tint = colors.textMuted, modifier = Modifier.size(20.dp))
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f), modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

@Composable
fun UserCard(
    user: User,
    colors: DashboardColors,
    onClick: () -> Unit
) {
    CardContainer(
        containerColor = colors.card,
        modifier = Modifier.clickable { onClick() }
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(user.role.color.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(user.role.icon, null, tint = user.role.color, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(user.fullName, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = colors.textPrimary)
                    Text("@${user.username}", style = MaterialTheme.typography.bodySmall, color = colors.textMuted)
                }
                TagPill(user.status, if (user.status == "Actif") Color(0xFF10B981) else Color(0xFF64748B), isSmall = true)
            }
            
            HorizontalDivider(color = colors.divider.copy(alpha = 0.5f))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Rôle", style = MaterialTheme.typography.labelSmall, color = colors.textMuted)
                    Text(user.role.label, style = MaterialTheme.typography.bodySmall, color = colors.textPrimary)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Dernière connexion", style = MaterialTheme.typography.labelSmall, color = colors.textMuted)
                    Text(user.lastLogin ?: "Jamais", style = MaterialTheme.typography.bodySmall, color = colors.textPrimary)
                }
            }
            
            Text(user.email, style = MaterialTheme.typography.bodySmall, color = colors.textLink)
        }
    }
}
