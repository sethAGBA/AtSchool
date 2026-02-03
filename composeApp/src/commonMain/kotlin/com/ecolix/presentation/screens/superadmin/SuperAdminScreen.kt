package com.ecolix.presentation.screens.superadmin

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.ecolix.atschool.api.CreateTenantRequest
import com.ecolix.presentation.screens.auth.LoginScreen
import com.ecolix.presentation.theme.BluePrimary
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset

class SuperAdminScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<SuperAdminScreenModel>()
        val state by screenModel.state.collectAsState()
        val searchQuery by screenModel.searchQuery.collectAsState()
        val navigator = LocalNavigator.currentOrThrow
        var showCreateDialog by remember { mutableStateOf(false) }
        var selectedTenant by remember { mutableStateOf<com.ecolix.atschool.api.TenantDto?>(null) }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text("Administration Plateforme", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text("Vue d'ensemble et gestion", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    },
                    actions = {
                        IconButton(onClick = { screenModel.refresh() }, colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.onSurface)) {
                            Icon(Icons.Default.Refresh, contentDescription = "Actualiser")
                        }
                        
                        val themeState = com.ecolix.presentation.theme.LocalThemeIsDark.current
                        IconButton(onClick = { themeState.value = !themeState.value }, colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.onSurface)) {
                            Icon(
                                if (themeState.value) Icons.Default.LightMode else Icons.Default.DarkMode,
                                contentDescription = "Changer Thème"
                            )
                        }
                        IconButton(onClick = {
                            com.ecolix.atschool.api.TokenProvider.token = null
                            com.ecolix.atschool.api.TokenProvider.role = null
                            navigator.replace(LoginScreen())
                        }, colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.onSurface)) {
                            Icon(Icons.Default.Logout, contentDescription = "Déconnexion")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.primary
                    )
                )
            },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    onClick = { showCreateDialog = true },
                    containerColor = BluePrimary,
                    contentColor = Color.White,
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text("Nouvel Établissement") }
                )
            }
        ) { padding ->
            Box(modifier = Modifier.padding(padding).fillMaxSize().background(MaterialTheme.colorScheme.background)) {
                when (val currentState = state) {
                    is SuperAdminState.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = BluePrimary)
                    }
                    is SuperAdminState.Error -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.align(Alignment.Center)) {
                            Icon(Icons.Default.ErrorOutline, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.Red)
                            Spacer(Modifier.height(8.dp))
                            Text(currentState.message, color = MaterialTheme.colorScheme.error)
                            Button(onClick = { screenModel.refresh() }, colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)) {
                                Text("Réessayer")
                            }
                        }
                    }
                    is SuperAdminState.Success -> {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // TOP NAVIGATION TABS
                            val selectedTab by screenModel.selectedTab.collectAsState()
                            TabRow(
                                selectedTabIndex = selectedTab.ordinal,
                                containerColor = Color.Transparent,
                                contentColor = BluePrimary,
                                indicator = { tabPositions ->
                                    TabRowDefaults.SecondaryIndicator(
                                        Modifier.tabIndicatorOffset(tabPositions[selectedTab.ordinal]),
                                        color = BluePrimary
                                    )
                                },
                                divider = {}
                            ) {
                                SuperAdminTab.values().forEach { tab ->
                                    Tab(
                                        selected = selectedTab == tab,
                                        onClick = { screenModel.onTabChange(tab) },
                                        text = {
                                            Text(
                                                when(tab) {
                                                    SuperAdminTab.SCHOOLS -> "Établissements"
                                                    SuperAdminTab.ANNOUNCEMENTS -> "Communications"
                                                    SuperAdminTab.LOGS -> "Historique"
                                                    SuperAdminTab.ANALYTICS -> "Dashboard"
                                                    SuperAdminTab.BILLING -> "Facturation"
                                                    SuperAdminTab.SYSTEM -> "Système"
                                                    SuperAdminTab.SUPPORT -> "Support"
                                                },
                                                fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Normal
                                            )
                                        }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            AnimatedContent(
                                targetState = selectedTab,
                                transitionSpec = {
                                    fadeIn() with fadeOut()
                                }
                            ) { tab ->
                                when(tab) {
                                    SuperAdminTab.SCHOOLS -> SchoolsTabContent(currentState, screenModel)
                                    SuperAdminTab.ANNOUNCEMENTS -> AnnouncementsTabContent(currentState, screenModel)
                                    SuperAdminTab.LOGS -> LogsTabContent(currentState)
                                    SuperAdminTab.ANALYTICS -> AnalyticsTabContent(currentState, screenModel)
                                    SuperAdminTab.BILLING -> BillingTabContent(currentState.payments, currentState.tenants, currentState.plans, screenModel)
                                    SuperAdminTab.SYSTEM -> SystemHealthContent(currentState.tenants, screenModel)
                                    SuperAdminTab.SUPPORT -> SupportTabContent(currentState.tickets)
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showCreateDialog) {
            CreateTenantDialog(
                onDismiss = { showCreateDialog = false },
                onConfirm = { request ->
                    screenModel.createTenant(request) { success ->
                        if (success) showCreateDialog = false
                    }
                }
            )
        }

        selectedTenant?.let { tenant ->
            TenantDetailsDialog(
                tenant = tenant,
                screenModel = screenModel,
                onDismiss = { selectedTenant = null },
                onToggleStatus = { isActive ->
                    screenModel.toggleTenantStatus(tenant.id, isActive)
                    selectedTenant = null
                },
                onResetPassword = { password ->
                    screenModel.resetAdminPassword(tenant.id, password) { success ->
                        if (success) selectedTenant = null
                    }
                },
                onUpdateSubscription = { date ->
                    screenModel.updateSubscription(tenant.id, date)
                    selectedTenant = null
                }
            )
        }
    }

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
    @Composable
    fun SchoolsTabContent(state: SuperAdminState.Success, screenModel: SuperAdminScreenModel) {
        val layoutMode by screenModel.layoutMode.collectAsState()
        val searchQuery by screenModel.searchQuery.collectAsState()

        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                StatCard(
                    title = "Écoles",
                    value = state.stats.totalSchools.toString(),
                    icon = Icons.Default.School,
                    color = BluePrimary,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Élèves",
                    value = state.stats.totalStudents.toString(),
                    icon = Icons.Default.Groups,
                    color = com.ecolix.presentation.theme.GreenAccent,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Revenus",
                    value = "${state.stats.totalRevenue} FCFA",
                    icon = Icons.Default.Payments,
                    color = Color(0xFF9C27B0),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Action Bar: Search + Export + Add + View Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { screenModel.onSearchQueryChange(it) },
                        placeholder = { Text("Rechercher une école...") },
                        modifier = Modifier.width(300.dp),
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                        ),
                        singleLine = true
                    )
                    
                    Spacer(Modifier.width(12.dp))
                    
                    Button(
                        onClick = { /* Export implementation */ },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(Modifier.width(8.dp))
                            Text("Exporter", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    var showCreateDialog by remember { mutableStateOf(false) }
                    Button(
                        onClick = { showCreateDialog = true },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Nouvel Établissement")
                    }

                    if (showCreateDialog) {
                        CreateTenantDialog(
                            onDismiss = { showCreateDialog = false },
                            onConfirm = { request ->
                                screenModel.createTenant(request) { success ->
                                    if (success) showCreateDialog = false
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    ViewToggle(layoutMode) { screenModel.onLayoutModeChange(it) }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Scrollable Schools List/Grid
            var selectedTenant by remember { mutableStateOf<com.ecolix.atschool.api.TenantDto?>(null) }
            
            AnimatedContent(targetState = layoutMode) { mode ->
                if (mode == SuperAdminLayoutMode.LIST) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.tenants) { tenant ->
                            TenantListItem(tenant) { selectedTenant = tenant }
                        }
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(300.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(state.tenants) { tenant ->
                            TenantCard(tenant) { selectedTenant = tenant }
                        }
                    }
                }
            }

            selectedTenant?.let { tenant ->
                TenantDetailsDialog(
                    tenant = tenant,
                    screenModel = screenModel,
                    onDismiss = { 
                        selectedTenant = null },
                    onToggleStatus = { isActive ->
                        screenModel.toggleTenantStatus(tenant.id, isActive)
                        selectedTenant = null
                    },
                    onResetPassword = { newPass ->
                        screenModel.resetAdminPassword(tenant.id, newPass) { success ->
                            if (success) selectedTenant = null
                        }
                    },
                    onUpdateSubscription = { date ->
                         screenModel.updateSubscription(tenant.id, date)
                         selectedTenant = null
                    }
                )
            }
        }
    }

    @Composable
    fun ViewToggle(layoutMode: SuperAdminLayoutMode, onLayoutModeChange: (SuperAdminLayoutMode) -> Unit) {
        Surface(
            modifier = Modifier.height(56.dp),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Row(
                modifier = Modifier.padding(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val isList = layoutMode == SuperAdminLayoutMode.LIST
                IconButton(
                    onClick = { onLayoutModeChange(SuperAdminLayoutMode.LIST) },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = if (isList) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent,
                        contentColor = if (isList) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Liste")
                }
                IconButton(
                    onClick = { onLayoutModeChange(SuperAdminLayoutMode.GRID) },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = if (!isList) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent,
                        contentColor = if (!isList) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Icon(Icons.Default.GridView, contentDescription = "Grille")
                }
            }
        }
    }

    @Composable
    fun TenantDetailsDialog(
        tenant: com.ecolix.atschool.api.TenantDto,
        screenModel: SuperAdminScreenModel,
        onDismiss: () -> Unit,
        onToggleStatus: (Boolean) -> Unit,
        onResetPassword: (String) -> Unit,
        onUpdateSubscription: (String?) -> Unit
    ) {
        var showPasswordReset by remember { mutableStateOf(false) }
        var showSubscriptionDialog by remember { mutableStateOf(false) }
        var showNotificationDialog by remember { mutableStateOf(false) }
        var newPassword by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(tenant.name, fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Status Badge in details
                    val statusColor = if (tenant.isActive) com.ecolix.presentation.theme.GreenAccent else Color.Red
                    Surface(
                        color = statusColor.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.size(8.dp).background(statusColor, CircleShape))
                            Spacer(Modifier.width(8.dp))
                            Text(
                                if (tenant.isActive) "Établissement Actif" else "Établissement Inactif",
                                color = statusColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        DetailItem("Code École", tenant.code, Icons.Default.QrCode)
                        DetailItem("Domaine", tenant.domain, Icons.Default.Language)
                        
                        tenant.adminEmail?.let {
                            DetailItem("Email Admin", it, Icons.Default.AdminPanelSettings)
                        }

                        tenant.contactEmail?.let {
                            DetailItem("Email Contact", it, Icons.Default.Email)
                        }
                        tenant.contactPhone?.let {
                            DetailItem("Téléphone", it, Icons.Default.Phone)
                        }
                        tenant.address?.let {
                            DetailItem("Adresse", it, Icons.Default.LocationOn)
                        }
                        
                        DetailItem("Date d'ajout", tenant.createdAt.take(10), Icons.Default.CalendarToday)
                        
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                        
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                             Column(modifier = Modifier.weight(1f)) {
                                 Text("Abonnement", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                 val expiry = tenant.subscriptionExpiresAt ?: "Non défini"
                                 Text(expiry, fontWeight = FontWeight.Bold, color = if (tenant.subscriptionExpiresAt == null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary)
                             }
                             TextButton(onClick = { showSubscriptionDialog = true }) {
                                 Text("Modifier")
                             }
                        }
                    }
                    
                    Divider(color = Color.LightGray.copy(alpha = 0.5f))
                    
                    if (!showPasswordReset) {
                        Button(
                            onClick = { onToggleStatus(!tenant.isActive) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (tenant.isActive) Color.Red.copy(alpha = 0.1f) else Color.Green.copy(alpha = 0.1f),
                                contentColor = if (tenant.isActive) Color.Red else com.ecolix.presentation.theme.GreenAccent
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                if (tenant.isActive) Icons.Default.Block else Icons.Default.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(if (tenant.isActive) "Désactiver l'école" else "Activer l'école")
                        }

                         OutlinedButton(
                            onClick = { showNotificationDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                             Icon(Icons.Default.NotificationsActive, contentDescription = null, modifier = Modifier.size(18.dp))
                             Spacer(Modifier.width(8.dp))
                             Text("Notifier l'école")
                        }

                        OutlinedButton(
                            onClick = { showPasswordReset = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                             Icon(Icons.Default.LockReset, contentDescription = null, modifier = Modifier.size(18.dp))
                             Spacer(Modifier.width(8.dp))
                             Text("Réinitialiser mot de passe admin")
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Nouveau mot de passe pour l'admin", style = MaterialTheme.typography.bodySmall)
                            OutlinedTextField(
                                value = newPassword,
                                onValueChange = { newPassword = it },
                                label = { Text("Mot de passe") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                TextButton(onClick = { showPasswordReset = false }, modifier = Modifier.weight(1f)) {
                                    Text("Annuler")
                                }
                                Button(
                                    onClick = { onResetPassword(newPassword) },
                                    enabled = newPassword.length >= 6,
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Valider")
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = onDismiss) { Text("Fermer") }
            },
            shape = RoundedCornerShape(24.dp)
        )

        if (showSubscriptionDialog) {
            var dateInput by remember { mutableStateOf(tenant.subscriptionExpiresAt ?: "2026-12-31") }
            AlertDialog(
                onDismissRequest = { showSubscriptionDialog = false },
                title = { Text("Fin d'abonnement") },
                text = {
                    OutlinedTextField(
                        value = dateInput,
                        onValueChange = { dateInput = it },
                        label = { Text("Format: YYYY-MM-DD") },
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    Button(onClick = { 
                        onUpdateSubscription(dateInput.ifBlank { null })
                        showSubscriptionDialog = false 
                    }) { Text("Mettre à jour") }
                },
                dismissButton = {
                    TextButton(onClick = { showSubscriptionDialog = false }) { Text("Annuler") }
                }
            )
        }
        if (showNotificationDialog) {
            SendNotificationDialog(
                tenants = emptyList(), // Not used for list when target is fixed
                targetTenantId = tenant.id,
                onDismiss = { showNotificationDialog = false },
                onConfirm = { tenantId, userId, title, msg, type, priority ->
                    screenModel.sendNotification(tenantId, userId, title, msg, type, priority) {
                        showNotificationDialog = false
                    }
                }
            )
        }
    }

    @Composable
    fun StatCard(title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, modifier: Modifier = Modifier) {
        Card(
            modifier = modifier,
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(color.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(value, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = color)
                Text(title, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }

    @Composable
    fun TenantCard(tenant: com.ecolix.atschool.api.TenantDto, onClick: () -> Unit) {
        Card(
            modifier = Modifier.fillMaxWidth().clickable { onClick() },
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = (if (tenant.isActive) BluePrimary else Color.Gray).copy(alpha = 0.1f),
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = tenant.code.take(2),
                                fontWeight = FontWeight.Bold,
                                color = if (tenant.isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 18.sp
                            )
                        }
                    }

                    val statusColor = if (tenant.isActive) com.ecolix.presentation.theme.GreenAccent else Color.Red
                    Box(
                        modifier = Modifier
                            .background(statusColor.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            if (tenant.isActive) "ACTIF" else "INACTIF",
                            color = statusColor,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = tenant.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = if (tenant.isActive) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Language, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.width(6.dp))
                    Text(tenant.domain, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                tenant.contactEmail?.let {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Email, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.width(6.dp))
                        Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                   modifier = Modifier.fillMaxWidth(),
                   horizontalArrangement = Arrangement.SpaceBetween,
                   verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Code École", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(tenant.code, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.primary)
                    }
                    Text(
                        text = tenant.createdAt.take(10),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }

    @Composable
    fun TenantListItem(tenant: com.ecolix.atschool.api.TenantDto, onClick: () -> Unit) {
        Card(
            modifier = Modifier.fillMaxWidth().clickable { onClick() },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = (if (tenant.isActive) BluePrimary else Color.Gray).copy(alpha = 0.1f),
                    modifier = Modifier.size(56.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = tenant.code.take(2),
                            fontWeight = FontWeight.Bold,
                            color = if (tenant.isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 20.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(tenant.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = if (tenant.isActive) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Language, contentDescription = null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.width(4.dp))
                        Text(tenant.domain, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        
                        tenant.contactEmail?.let {
                            Spacer(Modifier.width(12.dp))
                            Icon(Icons.Default.Email, contentDescription = null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(Modifier.width(4.dp))
                            Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    val statusColor = if (tenant.isActive) com.ecolix.presentation.theme.GreenAccent else Color.Red
                    Box(
                        modifier = Modifier
                            .background(statusColor.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            if (tenant.isActive) "Actif" else "Inactif",
                            color = statusColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    tenant.subscriptionExpiresAt?.let {
                        Text("Expire: $it", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Spacer(Modifier.width(8.dp))
                Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = Color.LightGray)
            }
        }
    }

    @Composable
    fun DetailItem(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.width(12.dp))
            Column {
                Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            }
        }
    }

    @Composable
    fun CreateTenantDialog(onDismiss: () -> Unit, onConfirm: (CreateTenantRequest) -> Unit) {
        var name by remember { mutableStateOf("") }
        var code by remember { mutableStateOf("") }
        var contactEmail by remember { mutableStateOf("") }
        var contactPhone by remember { mutableStateOf("") }
        var address by remember { mutableStateOf("") }
        var adminEmail by remember { mutableStateOf("") }
        var adminPassword by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Nouvel Établissement", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier.padding(top = 8.dp).verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nom de l'école") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = code,
                        onValueChange = { code = it },
                        label = { Text("Code unique (ex: DEMO)") },
                        placeholder = { Text("Générera automatique : demo.atschool.com") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = contactEmail,
                            onValueChange = { contactEmail = it },
                            label = { Text("Email Contact") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = contactPhone,
                            onValueChange = { contactPhone = it },
                            label = { Text("Téléphone") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                    
                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Adresse") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Divider()
                    Text("Premier Administrateur", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = adminEmail,
                        onValueChange = { adminEmail = it },
                        label = { Text("Email de l'admin") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) }
                    )
                    OutlinedTextField(
                        value = adminPassword,
                        onValueChange = { adminPassword = it },
                        label = { Text("Mot de passe") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { 
                        onConfirm(CreateTenantRequest(
                            name, code.uppercase(), adminEmail, adminPassword,
                            contactEmail.ifBlank { null },
                            contactPhone.ifBlank { null },
                            address.ifBlank { null }
                        )) 
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                    enabled = name.isNotBlank() && code.isNotBlank() && adminEmail.isNotBlank() && adminPassword.isNotBlank()
                ) {
                    Text("Créer l'accès")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) { Text("Annuler") }
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = MaterialTheme.colorScheme.surface
        )
    }

    @Composable
    fun AnnouncementsTabContent(state: SuperAdminState.Success, screenModel: SuperAdminScreenModel) {
        var showCreateAnnouncement by remember { mutableStateOf(false) }

        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Communications Globales", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                
                Button(
                    onClick = { showCreateAnnouncement = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
                ) {
                    Icon(Icons.Default.Campaign, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Nouvelle Annonce")
                }
            }

            Spacer(Modifier.height(24.dp))

            if (state.announcements.isEmpty()) {
                Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    Text("Aucune annonce publiée", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(state.announcements) { announcement ->
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                        ) {
                            Column(Modifier.padding(20.dp).fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            color = BluePrimary.copy(alpha = 0.1f),
                                            shape = CircleShape,
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(Icons.Default.Campaign, null, modifier = Modifier.size(16.dp), tint = BluePrimary)
                                            }
                                        }
                                        Spacer(Modifier.width(12.dp))
                                        Text(announcement.createdAt, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    
                                    val statusColor = if (announcement.isActive) com.ecolix.presentation.theme.GreenAccent else MaterialTheme.colorScheme.onSurfaceVariant
                                    Text(
                                        if (announcement.isActive) "ACTIVE" else "PASSÉE",
                                        color = statusColor,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                
                                Spacer(Modifier.height(16.dp))
                                Text(announcement.content, style = MaterialTheme.typography.bodyLarge)
                                
                                if (announcement.targetRole != null || announcement.expiresAt != null) {
                                    Spacer(Modifier.height(16.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                        announcement.targetRole?.let {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.Person, null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                                Spacer(Modifier.width(4.dp))
                                                Text("Cible: $it", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            }
                                        }
                                        announcement.expiresAt?.let {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.Event, null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                                Spacer(Modifier.width(4.dp))
                                                Text("Expire: $it", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showCreateAnnouncement) {
            var content by remember { mutableStateOf("") }
            var targetRole by remember { mutableStateOf<String?>(null) }
            var expiresAt by remember { mutableStateOf("2026-12-31") }

            AlertDialog(
                onDismissRequest = { showCreateAnnouncement = false },
                title = { Text("Diffuser une annonce") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        OutlinedTextField(
                            value = content,
                            onValueChange = { content = it },
                            label = { Text("Message") },
                            modifier = Modifier.fillMaxWidth().height(100.dp),
                            shape = RoundedCornerShape(12.dp)
                        )
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                value = targetRole ?: "",
                                onValueChange = { targetRole = it.ifBlank { null } },
                                label = { Text("Rôle (Optionnel)") },
                                placeholder = { Text("ex: ADMIN") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            OutlinedTextField(
                                value = expiresAt,
                                onValueChange = { expiresAt = it },
                                label = { Text("Expiration") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                        Text("L'annonce sera visible sur le dashboard des écoles ciblées.", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            screenModel.createAnnouncement(com.ecolix.atschool.api.CreateAnnouncementRequest(content, targetRole, expiresAt)) {
                                if (it) showCreateAnnouncement = false
                            }
                        },
                        enabled = content.isNotBlank(),
                        shape = RoundedCornerShape(12.dp)
                    ) { Text("Publier") }
                },
                dismissButton = {
                    TextButton(onClick = { showCreateAnnouncement = false }) { Text("Annuler") }
                }
            )
        }
    }

    @Composable
    fun LogsTabContent(state: SuperAdminState.Success) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text("Historique des Actions", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(24.dp))

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
            ) {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(state.logs) { log ->
                        Column(Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(log.actorEmail, fontWeight = FontWeight.Bold, color = BluePrimary)
                                        Spacer(Modifier.width(8.dp))
                                        Box(
                                            modifier = Modifier
                                                .background(Color.LightGray.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(log.action, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                                        }
                                    }
                                    Spacer(Modifier.height(4.dp))
                                    Text(log.details ?: "Pas de détails", style = MaterialTheme.typography.bodyMedium)
                                }
                                Text(
                                    log.timestamp.replace("T", " ").take(16),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Divider(modifier = Modifier.padding(top = 12.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
                        }
                    }
                }
            }
        }
    }
}
