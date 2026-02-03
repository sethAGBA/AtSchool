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
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import com.ecolix.atschool.api.*

@Composable
fun AnalyticsTabContent(state: SuperAdminState.Success, screenModel: SuperAdminScreenModel) {
    val metrics = state.growthMetrics
    
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
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
            }
            
            // Period Selector
            Row(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                AnalyticsPeriod.values().forEach { period ->
                    val isSelected = state.selectedPeriod == period
                    val text = when(period) {
                        AnalyticsPeriod.LAST_7_DAYS -> "7j"
                        AnalyticsPeriod.LAST_30_DAYS -> "30j"
                        AnalyticsPeriod.LAST_YEAR -> "1 an"
                    }
                    
                    Surface(
                        onClick = { screenModel.onPeriodChange(period) },
                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                        contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = text,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Dynamic Chart Wrapper
        Card(
            modifier = Modifier.fillMaxWidth().height(350.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        ) {
            Column(Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Croissance des revenus (FCFA)", fontWeight = FontWeight.Bold)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(10.dp).background(BluePrimary, CircleShape))
                        Spacer(Modifier.width(6.dp))
                        Text("Revenus", style = MaterialTheme.typography.labelSmall)
                    }
                }
                
                Spacer(Modifier.height(20.dp))
                
                if (metrics != null && metrics.dataPoints.isNotEmpty()) {
                    DynamicLineChart(
                        dataPoints = metrics.dataPoints,
                        modifier = Modifier.weight(1f).fillMaxWidth()
                    )
                } else {
                    Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("Aucune donnée disponible pour cette période", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                    }
                }
            }
        }
        
        Spacer(Modifier.height(24.dp))
        
        // Summary Cards
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            AnalyticsCard("Nouv. Écoles", metrics?.let { "${it.newSchools}" } ?: "0", Icons.Default.School, GreenAccent, Modifier.weight(1f)) 
            AnalyticsCard("Nouv. Élèves", metrics?.let { "${it.newStudents}" } ?: "0", Icons.Default.People, BluePrimary, Modifier.weight(1f)) 
            val totalRevenueFormatted = metrics?.totalRevenue?.toLong()?.toString() ?: "0"
            AnalyticsCard("Revenu Période", "$totalRevenueFormatted FCFA", Icons.Default.Payments, Color(0xFF9C27B0), Modifier.weight(1f)) 
        }

        Spacer(Modifier.height(32.dp))

        // School Activity Leaderboard
        Text("Concentration d'Activité par École", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
        ) {
            Column(Modifier.padding(16.dp)) {
                if (state.schoolActivity.isEmpty()) {
                    Text("Calcul des scores d'activité...", modifier = Modifier.padding(16.dp), color = Color.Gray)
                } else {
                    state.schoolActivity.sortedByDescending { it.activityScore }.forEach { activity ->
                        ActivityScoreRow(activity)
                        if (activity != state.schoolActivity.last()) {
                            Divider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
                        }
                    }
                }
            }
        }
        
        Spacer(Modifier.height(40.dp))
    }
}

@Composable
fun ActivityScoreRow(activity: SchoolActivityDto) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Surface(
            modifier = Modifier.size(40.dp),
            shape = CircleShape,
            color = BluePrimary.copy(alpha = 0.1f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(activity.tenantName.take(1).uppercase(), color = BluePrimary, fontWeight = FontWeight.Bold)
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(activity.tenantName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                Text("${activity.totalStudents} élèves", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = (activity.activityScore / 100).toFloat(),
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                color = when {
                    activity.activityScore > 75 -> GreenAccent
                    activity.activityScore > 40 -> BluePrimary
                    else -> OrangeSecondary
                },
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }

        Surface(
            color = when {
                activity.activityScore > 75 -> GreenAccent.copy(alpha = 0.1f)
                activity.activityScore > 40 -> BluePrimary.copy(alpha = 0.1f)
                else -> OrangeSecondary.copy(alpha = 0.1f)
            },
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                "${activity.activityScore.toInt()}%",
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                color = when {
                    activity.activityScore > 75 -> GreenAccent
                    activity.activityScore > 40 -> BluePrimary
                    else -> OrangeSecondary
                },
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun DynamicLineChart(dataPoints: List<GrowthDataPoint>, modifier: Modifier = Modifier) {
    val chartColor = BluePrimary
    val maxRevenue = dataPoints.maxOfOrNull { it.revenue } ?: 1.0
    val displayMax = if (maxRevenue == 0.0) 1.0 else maxRevenue * 1.2

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val spacing = width / (dataPoints.size - 1).coerceAtLeast(1)

        val path = Path()
        dataPoints.forEachIndexed { index, point ->
            val x = index * spacing
            val y = height - (point.revenue / displayMax * height).toFloat()
            
            if (index == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }

        drawPath(
            path = path,
            color = chartColor,
            style = Stroke(width = 3.dp.toPx())
        )
        
        // Draw Area Under Path
        val fillPath = Path().apply {
            addPath(path)
            lineTo(width, height)
            lineTo(0f, height)
            close()
        }
        drawPath(
            path = fillPath,
            color = chartColor.copy(alpha = 0.1f)
        )
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
    plans: List<com.ecolix.atschool.api.SubscriptionPlanDto>,
    screenModel: SuperAdminScreenModel
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var showCreatePlanDialog by remember { mutableStateOf(false) }
    var editingPayment by remember { mutableStateOf<com.ecolix.atschool.api.SubscriptionPaymentDto?>(null) }
    var editingPlan by remember { mutableStateOf<com.ecolix.atschool.api.SubscriptionPlanDto?>(null) }
    var planToDelete by remember { mutableStateOf<com.ecolix.atschool.api.SubscriptionPlanDto?>(null) }

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
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { showCreatePlanDialog = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Icon(Icons.Default.AddCircle, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Nouveau Plan")
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

        if (showCreatePlanDialog || editingPlan != null) {
            CreatePlanDialog(
                existingPlan = editingPlan,
                onDismiss = { 
                    showCreatePlanDialog = false 
                    editingPlan = null
                },
                onConfirm = { name, price, desc, popular ->
                    if (editingPlan != null) {
                        screenModel.updatePlan(editingPlan!!.id, name, price, desc, popular) {
                            editingPlan = null
                        }
                    } else {
                        screenModel.createPlan(name, price, desc, popular) {
                            showCreatePlanDialog = false
                        }
                    }
                }
            )
        }

        if (planToDelete != null) {
            AlertDialog(
                onDismissRequest = { planToDelete = null },
                title = { Text("Confirmer la suppression") },
                text = { Text("Êtes-vous sûr de vouloir supprimer le plan '${planToDelete?.name}' ? Cette action est irréversible.") },
                confirmButton = {
                    Button(
                        onClick = {
                            planToDelete?.let { plan ->
                                screenModel.deletePlan(plan.id) {
                                    planToDelete = null
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Supprimer")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { planToDelete = null }) {
                        Text("Annuler")
                    }
                }
            )
        }

        Spacer(Modifier.height(24.dp))

        // Plans Section
        Text("Plans d'abonnement", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            plans.forEach { plan ->
                PricingPlanCard(
                    title = plan.name,
                    price = "${plan.price} ${plan.currency}/mois",
                    subtitle = plan.description,
                    isPopular = plan.isPopular,
                    onEdit = { editingPlan = plan },
                    onDelete = { planToDelete = plan },
                    modifier = Modifier.weight(1f)
                )
            }
            if (plans.isEmpty()) {
                PricingPlanCard("Plan Standard", "À venir", "Configuration requise", false, {}, {}, Modifier.weight(1f))
            }
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
fun PricingPlanCard(
    title: String,
    price: String,
    subtitle: String,
    isPopular: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = if (isPopular) BorderStroke(2.dp, BluePrimary) else null
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    if (isPopular) {
                        Text("POPULAIRE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BluePrimary)
                    }
                    Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                }
            }
            Text(price, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
            Spacer(Modifier.height(16.dp))
            OutlinedButton(
                onClick = onEdit,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text("Modifier", fontSize = 12.sp)
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
fun SystemHealthContent(tenants: List<com.ecolix.atschool.api.TenantDto>, screenModel: SuperAdminScreenModel) {
    var showNotifDialog by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Santé du Système", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Button(
                onClick = { showNotifDialog = true },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
            ) {
                Icon(Icons.Default.NotificationsActive, null)
                Spacer(Modifier.width(8.dp))
                Text("Notifier")
            }
        }
        
        if (showNotifDialog) {
            SendNotificationDialog(
                tenants = tenants,
                onDismiss = { showNotifDialog = false },
                onConfirm = { tenantId, userId, title, msg, type, priority ->
                    screenModel.sendNotification(tenantId, userId, title, msg, type, priority) {
                        showNotifDialog = false
                    }
                }
            )
        }

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
@Composable
fun CreatePlanDialog(
    existingPlan: com.ecolix.atschool.api.SubscriptionPlanDto? = null,
    onDismiss: () -> Unit,
    onConfirm: (String, Double, String, Boolean) -> Unit
) {
    var name by remember { mutableStateOf(existingPlan?.name ?: "") }
    var price by remember { mutableStateOf(existingPlan?.price?.toString() ?: "") }
    var description by remember { mutableStateOf(existingPlan?.description ?: "") }
    var isPopular by remember { mutableStateOf(existingPlan?.isPopular ?: false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (existingPlan != null) "Modifier l'Offre" else "Nouvelle Offre d'Abonnement") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nom du Plan (ex: Business)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Prix par mois (FCFA)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description courte") }, modifier = Modifier.fillMaxWidth())
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isPopular, onCheckedChange = { isPopular = it })
                    Text("Plan Populaire (Mise en avant)")
                }
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(name, price.toDoubleOrNull() ?: 0.0, description, isPopular) }) { 
                Text(if (existingPlan != null) "Mettre à jour" else "Créer l'offre") 
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annuler") }
        }
    )
}

@Composable
fun SendNotificationDialog(
    tenants: List<com.ecolix.atschool.api.TenantDto>,
    targetTenantId: Int? = null,
    onDismiss: () -> Unit,
    onConfirm: (Int?, Long?, String, String, String, String) -> Unit
) {
    var selectedTenantId by remember { mutableStateOf(targetTenantId) }
    var title by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("INFO") }
    var priority by remember { mutableStateOf("NORMAL") }
    var expandedTenants by remember { mutableStateOf(false) }
    var expandedTypes by remember { mutableStateOf(false) }
    var expandedPriorities by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Envoyer une Notification") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Target Selection
                Box {
                    OutlinedButton(
                        onClick = { expandedTenants = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (selectedTenantId == null) "🌍 Tous les Établissements" else "🏢 ${tenants.find { it.id == selectedTenantId }?.name}")
                    }
                    DropdownMenu(expanded = expandedTenants, onDismissRequest = { expandedTenants = false }) {
                        DropdownMenuItem(text = { Text("🌍 Tous les Établissements") }, onClick = { selectedTenantId = null; expandedTenants = false })
                        tenants.forEach { tenant ->
                            DropdownMenuItem(text = { Text("🏢 ${tenant.name}") }, onClick = { selectedTenantId = tenant.id; expandedTenants = false })
                        }
                    }
                }

                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Titre") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = message, onValueChange = { message = it }, label = { Text("Message") }, modifier = Modifier.fillMaxWidth(), minLines = 3)

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Type
                    Box(Modifier.weight(1f)) {
                        OutlinedButton(onClick = { expandedTypes = true }, modifier = Modifier.fillMaxWidth()) { Text("Type: $type") }
                        DropdownMenu(expanded = expandedTypes, onDismissRequest = { expandedTypes = false }) {
                            listOf("INFO", "WARNING", "ALERT", "SUCCESS").forEach { t ->
                                DropdownMenuItem(text = { Text(t) }, onClick = { type = t; expandedTypes = false })
                            }
                        }
                    }
                    // Priority
                    Box(Modifier.weight(1f)) {
                        OutlinedButton(onClick = { expandedPriorities = true }, modifier = Modifier.fillMaxWidth()) { Text("Prio: $priority") }
                        DropdownMenu(expanded = expandedPriorities, onDismissRequest = { expandedPriorities = false }) {
                            listOf("LOW", "NORMAL", "HIGH", "URGENT").forEach { p ->
                                DropdownMenuItem(text = { Text(p) }, onClick = { priority = p; expandedPriorities = false })
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(selectedTenantId, null, title, message, type, priority) }) { Text("Envoyer") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annuler") }
        }
    )
}
