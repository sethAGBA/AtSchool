package com.ecolix.presentation.screens.communication

import androidx.compose.animation.*
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecolix.data.models.*
import com.ecolix.presentation.components.SearchBar

@Composable
fun CommunicationScreenContent(isDarkMode: Boolean) {
    val screenModel = remember { CommunicationScreenModel() }
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
                        text = "Communication",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = if (isCompact) 24.sp else 32.sp
                        ),
                        color = state.colors.textPrimary
                    )
                    if (!isCompact) {
                        Text(
                            text = "Gérez les annonces, SMS et emails",
                            style = MaterialTheme.typography.bodyMedium,
                            color = state.colors.textMuted
                        )
                    }
                }

                if (!isCompact) {
                    CommunicationChannelToggle(
                        selected = state.selectedChannel,
                        onChannelChange = { screenModel.onChannelFilterChange(it) },
                        colors = state.colors
                    )
                }
            }


            // View Switcher (Messages / Templates)
            TabRow(
                selectedTabIndex = state.selectedTab,
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.primary,
                divider = {}
            ) {
                Tab(
                    selected = state.selectedTab == 0,
                    onClick = { screenModel.onTabChange(0) },
                    text = { Text("Historique des messages") }
                )
                Tab(
                    selected = state.selectedTab == 1,
                    onClick = { screenModel.onTabChange(1) },
                    text = { Text("Modèles") }
                )
                Tab(
                    selected = state.selectedTab == 2,
                    onClick = { screenModel.onTabChange(2) },
                    text = { Text("Rappels & Automates") }
                )
            }

            // Search and Filters common to all tabs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (state.selectedTab == 0) {
                    Button(
                        onClick = { /* New message dialog */ },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        if (!isCompact) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Nouveau Message")
                        }
                    }
                }

                SearchBar(
                    query = state.searchQuery,
                    onQueryChange = { screenModel.onSearchQueryChange(it) },
                    colors = state.colors,
                    modifier = Modifier.weight(1f)
                )

                if (!isCompact && state.selectedTab == 0) {
                    StatusFilterDropdown(
                        selected = state.selectedStatus,
                        onStatusChange = { screenModel.onStatusFilterChange(it) },
                        colors = state.colors
                    )
                }
            }

            when (state.selectedTab) {
                0 -> {
                    // Messages List
                    Box(modifier = Modifier.weight(1f)) {
                        val filteredMessages = remember(state.messages, state.searchQuery, state.selectedChannel, state.selectedStatus) {
                            state.messages.filter { msg ->
                                (state.searchQuery.isEmpty() || 
                                 msg.title.contains(state.searchQuery, ignoreCase = true) ||
                                 msg.content.contains(state.searchQuery, ignoreCase = true) ||
                                 msg.recipients.contains(state.searchQuery, ignoreCase = true)) &&
                                (state.selectedChannel == null || msg.channel == state.selectedChannel) &&
                                (state.selectedStatus == null || msg.status == state.selectedStatus)
                            }
                        }

                        if (filteredMessages.isEmpty()) {
                            EmptyMessagesView(state.colors)
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                contentPadding = PaddingValues(bottom = 16.dp)
                            ) {
                                items(filteredMessages, key = { it.id }) { message ->
                                    MessageCard(message, state.colors, isCompact)
                                }
                            }
                        }
                    }
                }
                1 -> {
                    // Templates View
                    TemplatesGrid(
                        templates = state.templates,
                        searchQuery = state.searchQuery,
                        selectedChannel = state.selectedChannel,
                        colors = state.colors,
                        isCompact = isCompact
                    )
                }
                2 -> {
                    // Reminders & Automation View
                    RemindersView(
                        rules = state.reminderRules,
                        searchQuery = state.searchQuery,
                        colors = state.colors
                    )
                }
            }
        }
    }
}

@Composable
private fun TemplatesGrid(
    templates: List<CommunicationTemplate>,
    searchQuery: String,
    selectedChannel: CommunicationType?,
    colors: DashboardColors,
    isCompact: Boolean
) {
    val filteredTemplates = remember(templates, searchQuery, selectedChannel) {
        templates.filter { 
            (searchQuery.isEmpty() || it.name.contains(searchQuery, ignoreCase = true) || it.content.contains(searchQuery, ignoreCase = true)) &&
            (selectedChannel == null || it.type == selectedChannel)
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Modèles disponibles (${filteredTemplates.size})",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = colors.textPrimary
            )
            Button(
                onClick = { /* New template dialog */ },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Créer un modèle")
            }
        }

        if (filteredTemplates.isEmpty()) {
            EmptyMessagesView(colors)
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredTemplates) { template ->
                    TemplateCard(template, colors)
                }
            }
        }
    }
}

@Composable
private fun TemplateCard(template: CommunicationTemplate, colors: DashboardColors) {
    val channelIcon = when (template.type) {
        CommunicationType.ANNOUNCEMENT -> Icons.Default.Campaign
        CommunicationType.SMS -> Icons.Default.Sms
        CommunicationType.EMAIL -> Icons.Default.Email
        CommunicationType.PUSH -> Icons.Default.NotificationsActive
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colors.card),
        border = androidx.compose.foundation.BorderStroke(1.dp, colors.divider.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(channelIcon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(template.name, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = colors.textPrimary)
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { /* Edit */ }, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp), tint = colors.textMuted)
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Content with variable highlighting
            val annotatedContent = remember(template.content) {
                androidx.compose.ui.text.buildAnnotatedString {
                    val parts = template.content.split(Regex("(?=\\{)|(?<=\\})"))
                    parts.forEach { part ->
                        if (part.startsWith("{") && part.endsWith("}")) {
                            pushStyle(androidx.compose.ui.text.SpanStyle(
                                color = Color(0xFF10B981), // Emerald/Green for variables
                                fontWeight = FontWeight.Bold,
                                background = Color(0xFF10B981).copy(alpha = 0.1f)
                            ))
                            append(part)
                            pop()
                        } else {
                            append(part)
                        }
                    }
                }
            }

            Text(
                text = annotatedContent,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textPrimary
            )
            
            if (template.variables.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Code, contentDescription = null, modifier = Modifier.size(14.dp), tint = colors.textMuted)
                    Text(
                        text = "Variables: ${template.variables.joinToString(", ")}",
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.textMuted
                    )
                }
            }
        }
    }
}

@Composable
private fun MessageCard(message: SchoolMessage, colors: DashboardColors, isCompact: Boolean) {
    val channelIcon = when (message.channel) {
        CommunicationType.ANNOUNCEMENT -> Icons.Default.Campaign
        CommunicationType.SMS -> Icons.Default.Sms
        CommunicationType.EMAIL -> Icons.Default.Email
        CommunicationType.PUSH -> Icons.Default.NotificationsActive
    }
    
    val channelColor = when (message.channel) {
        CommunicationType.ANNOUNCEMENT -> Color(0xFF3B82F6) // Blue
        CommunicationType.SMS -> Color(0xFF10B981) // Green
        CommunicationType.EMAIL -> Color(0xFFF59E0B) // Amber
        CommunicationType.PUSH -> Color(0xFF8B5CF6) // Purple
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colors.card),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(channelColor.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(channelIcon, contentDescription = null, tint = channelColor, modifier = Modifier.size(20.dp))
                    }
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = message.title,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = colors.textPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (message.isAutomated) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(colors.textMuted.copy(alpha = 0.1f))
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    Text("AUTO", style = MaterialTheme.typography.labelSmall, color = colors.textMuted)
                                }
                            }
                        }
                        Text(
                            text = "Destinataires : ${message.recipients}",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.textMuted
                        )
                    }
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    StatusBadge(message.status, colors)
                    if (message.scheduledAt != null) {
                        Text(
                            text = "Prévu : ${message.scheduledAt}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = message.content,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textPrimary,
                maxLines = if (isCompact) 2 else 3,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = colors.divider.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Envoyé par : ${message.sender}",
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textMuted
                )
                Text(
                    text = message.timestamp,
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textMuted
                )
            }
        }
    }
}

@Composable
private fun StatusBadge(status: MessageStatus, colors: DashboardColors) {
    val (label, color) = when (status) {
        MessageStatus.SENT -> "Envoyé" to Color(0xFF10B981)
        MessageStatus.SCHEDULED -> "Programmé" to Color(0xFF3B82F6)
        MessageStatus.DRAFT -> "Brouillon" to Color(0xFF71717A)
        MessageStatus.FAILED -> "Échec" to Color(0xFFEF4444)
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
private fun StatusFilterDropdown(
    selected: MessageStatus?,
    onStatusChange: (MessageStatus?) -> Unit,
    colors: DashboardColors
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Surface(
            modifier = Modifier
                .height(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .clickable { expanded = true },
            color = colors.card,
            border = androidx.compose.foundation.BorderStroke(1.dp, colors.divider)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selected?.toFrench() ?: "Tous les statuts",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (selected == null) colors.textMuted else colors.textPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = colors.textMuted)
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(colors.card)
        ) {
            DropdownMenuItem(
                text = { Text("Tous les statuts", color = colors.textPrimary) },
                onClick = {
                    onStatusChange(null)
                    expanded = false
                }
            )
            MessageStatus.entries.forEach { status ->
                DropdownMenuItem(
                    text = { Text(status.toFrench(), color = colors.textPrimary) },
                    onClick = {
                        onStatusChange(status)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun EmptyMessagesView(colors: DashboardColors) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.ChatBubbleOutline,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = colors.textMuted.copy(alpha = 0.3f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Aucun message trouvé",
            style = MaterialTheme.typography.bodyLarge,
            color = colors.textMuted
        )
    }
}

@Composable
private fun CommunicationChannelToggle(
    selected: CommunicationType?,
    onChannelChange: (CommunicationType?) -> Unit,
    colors: DashboardColors
) {
    Row(
        modifier = Modifier
            .height(44.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(colors.card)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        val options = listOf(null, CommunicationType.ANNOUNCEMENT, CommunicationType.SMS, CommunicationType.EMAIL, CommunicationType.PUSH)
        options.forEach { option ->
            val isSelected = selected == option
            val icon = when (option) {
                CommunicationType.ANNOUNCEMENT -> Icons.Default.Campaign
                CommunicationType.SMS -> Icons.Default.Sms
                CommunicationType.EMAIL -> Icons.Default.Email
                CommunicationType.PUSH -> Icons.Default.NotificationsActive
                else -> Icons.Default.GridView
            }
            
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                    .clickable { onChannelChange(option) }
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = if (isSelected) Color.White else colors.textMuted
                    )
                    Text(
                        text = when (option) {
                            CommunicationType.ANNOUNCEMENT -> "Annonces"
                            CommunicationType.SMS -> "SMS"
                            CommunicationType.EMAIL -> "Emails"
                            CommunicationType.PUSH -> "Push"
                            else -> "Tout"
                        },
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (isSelected) Color.White else colors.textMuted
                    )
                }
            }
        }
    }
}
@Composable
private fun RemindersView(
    rules: List<ReminderRule>,
    searchQuery: String,
    colors: DashboardColors
) {
    val filteredRules = remember(rules, searchQuery) {
        rules.filter { 
            searchQuery.isEmpty() || it.name.contains(searchQuery, ignoreCase = true) || it.trigger.contains(searchQuery, ignoreCase = true)
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxSize().padding(top = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Automates de rappel (${filteredRules.size})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = colors.textPrimary
                )
                Text(
                    text = "Configurez les messages envoyés automatiquement par le système",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textMuted
                )
            }
            Button(
                onClick = { /* New rule dialog */ },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Nouvel automate")
            }
        }

        if (filteredRules.isEmpty()) {
            EmptyMessagesView(colors)
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredRules) { rule ->
                    ReminderRuleCard(rule, colors)
                }
            }
        }
    }
}

@Composable
private fun ReminderRuleCard(rule: ReminderRule, colors: DashboardColors) {
    val channelIcon = when (rule.channel) {
        CommunicationType.ANNOUNCEMENT -> Icons.Default.Campaign
        CommunicationType.SMS -> Icons.Default.Sms
        CommunicationType.EMAIL -> Icons.Default.Email
        CommunicationType.PUSH -> Icons.Default.NotificationsActive
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colors.card),
        border = androidx.compose.foundation.BorderStroke(1.dp, colors.divider.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(channelIcon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = rule.name,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = colors.textPrimary
                )
                Text(
                    text = "Déclencheur : ${rule.trigger}",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textMuted
                )
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Switch(
                    checked = rule.isActive,
                    onCheckedChange = { /* Toggle */ },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = MaterialTheme.colorScheme.primary
                    )
                )
                Text(
                    text = if (rule.isActive) "Actif" else "Inactif",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (rule.isActive) MaterialTheme.colorScheme.primary else colors.textMuted
                )
            }
        }
    }
}
