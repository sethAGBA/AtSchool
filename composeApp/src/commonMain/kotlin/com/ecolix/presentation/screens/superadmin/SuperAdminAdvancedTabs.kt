package com.ecolix.presentation.screens.superadmin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecolix.presentation.theme.BluePrimary
import com.ecolix.presentation.theme.GreenAccent
import com.ecolix.presentation.theme.OrangeSecondary

@Composable
fun AnalyticsTabContent(metrics: com.ecolix.atschool.api.GrowthMetricsDto?) {
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Text(
            "Analyse et Statistiques",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            "Vue détaillée de la croissance de la plateforme",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Mock Chart Placeholder
        Card(
            modifier = Modifier.fillMaxWidth().height(300.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.BarChart, null, modifier = Modifier.size(64.dp), tint = BluePrimary.copy(alpha = 0.5f))
                    Spacer(Modifier.height(16.dp))
                    Text("Graphique de Croissance (Écoles vs Élèves)", fontWeight = FontWeight.Bold)
                    Text("Données visualisées sur 6 mois", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
        
        Spacer(Modifier.height(24.dp))
        
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
           AnalyticsCard("Taux de Rétention", "98.5%", Icons.Default.ThumbUp, GreenAccent, Modifier.weight(1f)) 
           AnalyticsCard("Croissance", metrics?.let { "+${it.newSchools} écoles" } ?: "--", Icons.Default.TrendingUp, BluePrimary, Modifier.weight(1f)) 
           AnalyticsCard("Revenu Total", metrics?.let { "${it.totalRevenue} FCFA" } ?: "--", Icons.Default.Payments, Color(0xFF9C27B0), Modifier.weight(1f))         }
    }
}

@Composable
fun AnalyticsCard(title: String, value: String, icon: ImageVector, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(32.dp).background(color.copy(alpha = 0.1f), CircleShape), contentAlignment = Alignment.Center) {
                    Icon(icon, null, tint = color, modifier = Modifier.size(16.dp))
                }
                Spacer(Modifier.width(8.dp))
                Text(title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.height(12.dp))
            Text(value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
fun BillingTabContent(
    payments: List<com.ecolix.atschool.api.SubscriptionPaymentDto>,
    tenants: List<com.ecolix.atschool.api.TenantDto>,
    screenModel: SuperAdminScreenModel
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var editingPayment by remember { mutableStateOf<com.ecolix.atschool.api.SubscriptionPaymentDto?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
             verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Facturation et Abonnements", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text("Gestion des revenus et factures", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Button(
                onClick = { showCreateDialog = true },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
            ) {
                Icon(Icons.Default.Add, null)
                Spacer(Modifier.width(8.dp))
                Text("Nouveau Paiement")
            }
        }

        if (showCreateDialog || editingPayment != null) {
            CreatePaymentDialog(
                tenants = tenants,
                existingPayment = editingPayment,
                onDismiss = { 
                    showCreateDialog = false 
                    editingPayment = null
                },
                onConfirm = { tenantId, amount, method, notes, invoiceNumber ->
                    if (editingPayment != null) {
                        screenModel.updatePaymentStatus(editingPayment!!.id, "PAID", invoiceNumber) // Simplified for now
                        editingPayment = null
                    } else {
                        screenModel.recordPayment(tenantId, amount, method, notes) {
                            showCreateDialog = false
                        }
                    }
                }
            )
        }

        Spacer(Modifier.height(24.dp))

        // Plans Section
        Text("Plans d'abonnement", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            PricingPlanCard("Basique", "29 FCFA/mois", "Pour petites écoles", false, Modifier.weight(1f))
            PricingPlanCard("Business", "99 FCFA/mois", "Fonctionnalités complètes", true, Modifier.weight(1f))
            PricingPlanCard("Entreprise", "Sur devis", "Réseaux d'écoles", false, Modifier.weight(1f))
        }

        Spacer(Modifier.height(32.dp))
        
        Text("Dernières Factures", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        
        ScrollableColumn(Modifier.weight(1f)) {
            payments.forEach { payment ->
                PaymentItem(
                    payment = payment,
                    onStatusChange = { newStatus ->
                        screenModel.updatePaymentStatus(payment.id, newStatus)
                    },
                    onEdit = {
                        editingPayment = payment
                    }
                )
                Spacer(Modifier.height(8.dp))
            }
            if (payments.isEmpty()) {
                Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text("Aucune facture trouvée", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
fun ScrollableColumn(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier.verticalScroll(rememberScrollState())) {
        content()
    }
}

@Composable
fun PricingPlanCard(title: String, price: String, subtitle: String, isPopular: Boolean, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = if (isPopular) BorderStroke(2.dp, BluePrimary) else null
    ) {
        Column(Modifier.padding(16.dp)) {
            if (isPopular) {
                Text("POPULAIRE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BluePrimary)
                Spacer(Modifier.height(4.dp))
            }
            Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(price, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(16.dp))
            OutlinedButton(onClick = {}, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)) {
                Text("Modifier")
            }
        }
    }
}

@Composable
fun PaymentItem(
    payment: com.ecolix.atschool.api.SubscriptionPaymentDto,
    onStatusChange: (String) -> Unit,
    onEdit: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Icon(Icons.Default.Receipt, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(payment.invoiceNumber ?: "Facture #${payment.id}", fontWeight = FontWeight.Bold)
                    Text("${payment.tenantName} • ${payment.paymentDate.take(10)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("${payment.amount} ${payment.currency}", fontWeight = FontWeight.Bold)
                Spacer(Modifier.width(12.dp))
                val isPaid = payment.status == "PAID"
                
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, "Edit", tint = BluePrimary, modifier = Modifier.size(18.dp))
                }

                IconButton(onClick = { 
                    onStatusChange(if (isPaid) "PENDING" else "PAID") 
                }) {
                    Box(
                        modifier = Modifier
                            .background((if (isPaid) GreenAccent else OrangeSecondary).copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(payment.status, color = if (isPaid) GreenAccent else OrangeSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun CreatePaymentDialog(
    tenants: List<com.ecolix.atschool.api.TenantDto>,
    existingPayment: com.ecolix.atschool.api.SubscriptionPaymentDto? = null,
    onDismiss: () -> Unit,
    onConfirm: (Int, Double, String, String?, String?) -> Unit
) {
    var selectedTenantId by remember { mutableStateOf(existingPayment?.tenantId ?: tenants.firstOrNull()?.id ?: 0) }
    var amount by remember { mutableStateOf(existingPayment?.amount?.toString() ?: "") }
    var method by remember { mutableStateOf(existingPayment?.paymentMethod ?: "TRANSFER") }
    var notes by remember { mutableStateOf(existingPayment?.notes ?: "") }
    var invoiceNumber by remember { mutableStateOf(existingPayment?.invoiceNumber ?: "") }
    var expandedTenants by remember { mutableStateOf(false) }
    var expandedMethods by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (existingPayment != null) "Modifier le Paiement" else "Nouvel Enregistrement de Paiement") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                if (existingPayment == null) {
                    // Tenant Selection (Only for new)
                    Box {
                        OutlinedButton(
                            onClick = { expandedTenants = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(tenants.find { it.id == selectedTenantId }?.name ?: "Sélectionner École")
                        }
                        DropdownMenu(expanded = expandedTenants, onDismissRequest = { expandedTenants = false }) {
                            tenants.forEach { tenant ->
                                DropdownMenuItem(
                                    text = { Text(tenant.name) },
                                    onClick = {
                                        selectedTenantId = tenant.id
                                        expandedTenants = false
                                    }
                                )
                            }
                        }
                    }
                } else {
                    OutlinedTextField(
                        value = invoiceNumber,
                        onValueChange = { invoiceNumber = it },
                        label = { Text("Numéro de Facture") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Montant (FCFA)") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Method Selection
                Box {
                    OutlinedButton(
                        onClick = { expandedMethods = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Méthode: $method")
                    }
                    DropdownMenu(expanded = expandedMethods, onDismissRequest = { expandedMethods = false }) {
                        listOf("TRANSFER", "CASH", "CHECK", "CARD").forEach { m ->
                            DropdownMenuItem(
                                text = { Text(m) },
                                onClick = {
                                    method = m
                                    expandedMethods = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (Optionnel)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val amt = amount.toDoubleOrNull() ?: 0.0
                onConfirm(selectedTenantId, amt, method, notes.takeIf { it.isNotBlank() }, invoiceNumber.takeIf { it.isNotBlank() })
            }) {
                Text(if (existingPayment != null) "Mettre à jour" else "Enregistrer")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annuler") }
        }
    )
}

@Composable
fun SystemHealthContent() {
    Column(Modifier.fillMaxSize()) {
        Text("Santé du Système", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(24.dp))
        
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(Modifier.padding(24.dp)) {
                HealthIndicator("API Server", "Online", GreenAccent)
                Divider(Modifier.padding(vertical = 12.dp))
                HealthIndicator("Database (PostgreSQL)", "Online - 24ms latence", GreenAccent)
                Divider(Modifier.padding(vertical = 12.dp))
                HealthIndicator("Redis Cache", "Online", GreenAccent)
                Divider(Modifier.padding(vertical = 12.dp))
                HealthIndicator("Email Service (SMTP)", "Maintenance", OrangeSecondary)
            }
        }
        
        Spacer(Modifier.height(24.dp))
        
        Button(
            onClick = {}, 
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Warning, null)
            Spacer(Modifier.width(8.dp))
            Text("ACTIVER MODE MAINTENANCE")
        }
    }
}

@Composable
fun HealthIndicator(service: String, status: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(10.dp).background(color, CircleShape))
            Spacer(Modifier.width(12.dp))
            Text(service, fontWeight = FontWeight.Medium)
        }
        Text(status, color = color, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun SupportTabContent(tickets: List<com.ecolix.atschool.api.SupportTicketDto>) {
    Column(Modifier.fillMaxSize()) {
        Text("Centre de Support", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(24.dp))
        
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.weight(1f)) {
            items(tickets) { ticket ->
                TicketItem(ticket)
            }
            if (tickets.isEmpty()) {
                item {
                    Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text("Aucun ticket de support", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
fun TicketItem(ticket: com.ecolix.atschool.api.SupportTicketDto) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(ticket.subject, fontWeight = FontWeight.Bold)
                val statusColor = when(ticket.status) {
                    "OPEN" -> OrangeSecondary
                    "RESOLVED" -> GreenAccent
                    else -> Color.Gray
                }
                Box(
                    modifier = Modifier.background(statusColor.copy(alpha = 0.1f), RoundedCornerShape(4.dp)).padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(ticket.status, color = statusColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(8.dp))
            Text("${ticket.tenantName} • ${ticket.createdAt.take(10)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(12.dp))
            Text(ticket.description, maxLines = 2)
            Spacer(Modifier.height(12.dp))
            Row {
               Button(onClick = {}, modifier = Modifier.height(32.dp), contentPadding = PaddingValues(horizontal = 12.dp)) {
                   Text("Répondre", fontSize = 12.sp)
               }
            }
        }
    }
}
