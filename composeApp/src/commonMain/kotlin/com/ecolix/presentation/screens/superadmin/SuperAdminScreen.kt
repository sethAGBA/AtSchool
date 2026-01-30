package com.ecolix.presentation.screens.superadmin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
        val navigator = LocalNavigator.currentOrThrow
        var showCreateDialog by remember { mutableStateOf(false) }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Administration Plateforme", fontWeight = FontWeight.Bold) },
                    actions = {
                        IconButton(onClick = { screenModel.refresh() }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Actualiser")
                        }
                        IconButton(onClick = { 
                            // Logout
                            com.ecolix.atschool.api.TokenProvider.token = null
                            navigator.replace(LoginScreen())
                        }) {
                            Icon(Icons.Default.Logout, contentDescription = "Déconnexion")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.primary
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showCreateDialog = true },
                    containerColor = BluePrimary,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Ajouter une école")
                }
            }
        ) { padding ->
            Box(modifier = Modifier.padding(padding).fillMaxSize()) {
                when (val currentState = state) {
                    is SuperAdminState.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                    is SuperAdminState.Error -> {
                        Text(
                            text = currentState.message,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    is SuperAdminState.Success -> {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Stats Summary
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                StatCard("Écoles", currentState.stats.totalSchools.toString(), Icons.Default.School, Modifier.weight(1f))
                                StatCard("Élèves Total", currentState.stats.totalStudents.toString(), Icons.Default.People, Modifier.weight(1f))
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            Text("Liste des Établissements", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(16.dp))

                            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                items(currentState.tenants) { tenant ->
                                    TenantItem(tenant = tenant)
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
    fun StatCard(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier = Modifier) {
        Card(
            modifier = modifier,
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Icon(icon, contentDescription = null, tint = BluePrimary)
                Spacer(modifier = Modifier.height(8.dp))
                Text(value, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text(label, style = MaterialTheme.typography.bodySmall)
            }
        }
    }

    @Composable
    fun TenantItem(tenant: com.ecolix.atschool.api.TenantDto) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
            // colors handled by default
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(48.dp).background(BluePrimary.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(tenant.code, fontWeight = FontWeight.Bold, color = BluePrimary)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(tenant.name, fontWeight = FontWeight.Bold)
                    Text(tenant.domain, style = MaterialTheme.typography.bodySmall)
                }
                Text("Depuis: ${tenant.createdAt}", style = MaterialTheme.typography.bodySmall)
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
            title = { Text("Nouvel Établissement") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nom de l'école") })
                    OutlinedTextField(value = code, onValueChange = { code = it }, label = { Text("Code (ex: EXCEL)") })
                    OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email Admin") })
                    OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Mot de passe") })
                }
            },
            confirmButton = {
                Button(onClick = { 
                    onConfirm(CreateTenantRequest(name, code, email, password))
                }) {
                    Text("Créer")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) { Text("Annuler") }
            }
        )
    }
}
