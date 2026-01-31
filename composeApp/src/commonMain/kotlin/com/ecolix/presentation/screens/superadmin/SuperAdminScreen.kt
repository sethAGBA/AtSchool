package com.ecolix.presentation.screens.superadmin

import androidx.compose.foundation.background
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

class SuperAdminScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<SuperAdminScreenModel>()
        val state by screenModel.state.collectAsState()
        val searchQuery by screenModel.searchQuery.collectAsState()
        val navigator = LocalNavigator.currentOrThrow
        var showCreateDialog by remember { mutableStateOf(false) }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text("Administration Plateforme", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text("Vue d'ensemble et gestion", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        }
                    },
                    actions = {
                        IconButton(onClick = { screenModel.refresh() }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Actualiser")
                        }
                        IconButton(onClick = {
                            com.ecolix.atschool.api.TokenProvider.token = null
                            com.ecolix.atschool.api.TokenProvider.role = null
                            navigator.replace(LoginScreen())
                        }) {
                            Icon(Icons.Default.Logout, contentDescription = "Déconnexion")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = BluePrimary
                    )
                )
            },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    onClick = { showCreateDialog = true },
                    containerColor = BluePrimary,
                    contentColor = Color.White,
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text("Nouvelle École") }
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
                            Icon(Icons.Default.ErrorOutline, contentDescription = null, size(64.dp), Color.Red)
                            Spacer(Modifier.height(8.dp))
                            Text(currentState.message, color = MaterialTheme.colorScheme.error)
                            Button(onClick = { screenModel.refresh() }, colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)) {
                                Text("Réessayer")
                            }
                        }
                    }
                    is SuperAdminState.Success -> {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // High Level Stats
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                StatCard(
                                    label = "Écoles actives",
                                    value = currentState.stats.totalSchools.toString(),
                                    icon = Icons.Default.School,
                                    color = BluePrimary,
                                    modifier = Modifier.weight(1f)
                                )
                                StatCard(
                                    label = "Utilisateurs total",
                                    value = (currentState.stats.totalStudents + currentState.stats.totalSchools).toString(), // Demo math
                                    icon = Icons.Default.Group,
                                    color = com.ecolix.presentation.theme.PinkAccent,
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            // Search & Filter
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { screenModel.onSearchQueryChange(it) },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("Rechercher une école...") },
                                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                                trailingIcon = {
                                    if (searchQuery.isNotEmpty()) {
                                        IconButton(onClick = { screenModel.onSearchQueryChange("") }) {
                                            Icon(Icons.Default.Close, contentDescription = "Effacer")
                                        }
                                    }
                                },
                                shape = RoundedCornerShape(16.dp),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = BluePrimary,
                                    unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f)
                                )
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            Text(
                                text = "Établissements (${currentState.tenants.size})",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            if (currentState.tenants.isEmpty()) {
                                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                                    Text("Aucun établissement trouvé", color = Color.Gray)
                                }
                            } else {
                                LazyColumn(
                                    verticalArrangement = Arrangement.spacedBy(16.dp),
                                    contentPadding = PaddingValues(bottom = 80.dp)
                                ) {
                                    items(currentState.tenants) { tenant ->
                                        TenantListItem(tenant = tenant)
                                    }
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
    }

    @Composable
    fun StatCard(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, modifier: Modifier = Modifier) {
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
                Text(label, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
        }
    }

    @Composable
    fun TenantListItem(tenant: com.ecolix.atschool.api.TenantDto) {
        Card(
            modifier = Modifier.fillMaxWidth(),
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
                    color = BluePrimary.copy(alpha = 0.1f),
                    modifier = Modifier.size(56.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = tenant.code.take(2),
                            fontWeight = FontWeight.Bold,
                            color = BluePrimary,
                            fontSize = 20.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(tenant.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Language, contentDescription = null, size(12.dp), Color.Gray)
                        Spacer(Modifier.width(4.dp))
                        Text(tenant.domain, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Box(
                        modifier = Modifier
                            .background(com.ecolix.presentation.theme.GreenAccent.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("Actif", color = com.ecolix.presentation.theme.GreenAccent, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(tenant.createdAt.take(10), style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
                Spacer(Modifier.width(8.dp))
                Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = Color.LightGray)
            }
        }
    }

    @Composable
    fun CreateTenantDialog(onDismiss: () -> Unit, onConfirm: (CreateTenantRequest) -> Unit) {
        var name by remember { mutableStateOf("") }
        var code by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Nouvel Établissement", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.padding(top = 8.dp)) {
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
                        label = { Text("Code unique (ex: EXCEL)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Divider()
                    Text("Premier Administrateur", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email de l'admin") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) }
                    )
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Mot de passe") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { onConfirm(CreateTenantRequest(name, code, email, password)) },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                    enabled = name.isNotBlank() && code.isNotBlank() && email.isNotBlank() && password.isNotBlank()
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

    // Helper for easier size declaration
    private fun size(dp: androidx.compose.ui.unit.Dp) = Modifier.size(dp)
}
