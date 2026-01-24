package com.ecolix.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecolix.data.models.*

@Composable
fun ActivitiesCard(state: DashboardUiState) {
    CardContainer(containerColor = state.colors.card) {
        Column(
            modifier = Modifier.height(320.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Activites recentes",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                color = state.colors.textPrimary
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (state.activities.isEmpty()) {
                    Text(
                        text = "Aucune activite recente.",
                        color = state.colors.textMuted
                    )
                } else {
                    state.activities.forEach { activity ->
                        ActivityRow(activity, state.colors)
                    }
                }
            }
        }
    }
}

@Composable
fun ActivityRow(activity: ActivityData, colors: DashboardColors) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Brush.linearGradient(listOf(activity.color.copy(alpha = 0.2f), activity.color.copy(alpha = 0.4f)))),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = activity.icon,
                contentDescription = null,
                tint = activity.color,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = activity.title,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                color = colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = activity.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = colors.textMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Text(
            text = activity.time,
            style = MaterialTheme.typography.labelSmall,
            color = colors.textMuted
        )
    }
}

@Composable
fun QuickActionsCard(state: DashboardUiState) {
    CardContainer(containerColor = state.colors.card) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(
                text = "Actions rapides",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                color = state.colors.textPrimary
            )
            val rows = state.quickActions.chunked(2)
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                rows.forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        row.forEach { action ->
                            QuickActionCard(
                                modifier = Modifier.weight(1f),
                                action = action
                            )
                        }
                        if (row.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuickActionCard(modifier: Modifier, action: QuickActionData) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Brush.linearGradient(listOf(action.color.copy(alpha = 0.2f), action.color.copy(alpha = 0.4f))))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = action.icon,
                contentDescription = null,
                tint = action.color,
                modifier = Modifier.size(32.dp)
            )
            Text(
                text = action.title,
                color = action.color,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun AlertsCard(state: DashboardUiState) {
    CardContainer(containerColor = state.colors.card) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Alertes et suivi",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ),
                    color = state.colors.textPrimary
                )
                Text(
                    text = "Actualiser",
                    color = Color(0xFF3B82F6),
                    fontWeight = FontWeight.SemiBold
                )
            }
            if (state.alerts.isEmpty()) {
                Text(
                    text = "Aucune alerte pour le moment.",
                    color = state.colors.textMuted
                )
            } else {
                state.alerts.forEach { alert ->
                    AlertRow(alert, state.colors)
                }
            }
        }
    }
}

@Composable
fun AlertRow(alert: AlertData, colors: DashboardColors) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(colors.background.copy(alpha = 0.55f))
            .border(1.dp, colors.divider.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(alert.color.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = alert.icon,
                contentDescription = null,
                tint = alert.color,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = alert.title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = colors.textPrimary
            )
            Text(
                text = alert.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = colors.textMuted,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
        Text(
            text = alert.actionLabel,
            color = Color(0xFF3B82F6),
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
        )
    }
}

@Composable
fun AgendaCard(state: DashboardUiState) {
    CardContainer(containerColor = state.colors.card) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Echeances (7 jours)",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ),
                    color = state.colors.textPrimary
                )
                Text(
                    text = "Details",
                    color = Color(0xFF3B82F6),
                    fontWeight = FontWeight.SemiBold
                )
            }
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(state.agendaDays) { day ->
                    AgendaChip(day = day, colors = state.colors)
                }
            }
            AnimatedVisibility(visible = state.dueItems.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    state.dueItems.take(5).forEach { item ->
                        DueItemRow(item)
                    }
                }
            }
            if (state.dueItems.isEmpty()) {
                Text(
                    text = "Aucune echeance detectee.",
                    color = state.colors.textMuted
                )
            }
        }
    }
}

@Composable
fun AgendaChip(day: AgendaDay, colors: DashboardColors) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (day.count > 0) Color(0xFF0EA5E9).copy(alpha = 0.12f)
                else colors.background.copy(alpha = 0.5f)
            )
            .border(1.dp, colors.divider.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = day.label,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
            color = colors.textPrimary
        )
        Spacer(modifier = Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(999.dp))
                .background(if (day.count > 0) Color(0xFF0EA5E9) else colors.divider)
                .padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
            Text(
                text = day.count.toString(),
                color = Color.White,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}

@Composable
fun DueItemRow(item: DueItem) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(item.color)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "${item.dateLabel} - ${item.title} - ${item.subtitle}",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF475569),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun TodosCard(state: DashboardUiState) {
    val total = state.todos.size
    val doneCount = state.todos.count { it.done }
    val progress = if (total == 0) 0f else doneCount / total.toFloat()

    CardContainer(containerColor = state.colors.card) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "A faire",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        ),
                        color = state.colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TagPill("${total - doneCount} en cours", Color(0xFF0EA5E9))
                        TagPill("$doneCount terminee(s)", Color(0xFF10B981))
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Ajouter",
                        color = Color(0xFF3B82F6),
                        fontWeight = FontWeight.SemiBold
                    )
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = null,
                        tint = Color(0xFF3B82F6),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            if (total > 0) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(999.dp)),
                    color = Color(0xFF10B981),
                    trackColor = state.colors.divider.copy(alpha = 0.4f)
                )
            }
            if (state.todos.isEmpty()) {
                Text(
                    text = "Aucune tache. Ajoutez-en une pour commencer.",
                    color = state.colors.textMuted
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    state.todos.take(6).forEach { todo ->
                        TodoRow(todo, state.colors)
                    }
                }
            }
        }
    }
}

@Composable
fun TodoRow(todo: TodoItem, colors: DashboardColors) {
    val accent = when {
        todo.done -> Color(0xFF10B981)
        todo.overdue -> Color(0xFFEF4444)
        todo.dueSoon -> Color(0xFFF59E0B)
        else -> Color(0xFF0EA5E9)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(colors.background.copy(alpha = 0.6f))
            .border(1.dp, colors.divider.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(18.dp)
                .clip(CircleShape)
                .background(if (todo.done) accent else Color.Transparent)
                .border(2.dp, accent, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (todo.done) {
                Icon(
                    imageVector = Icons.Filled.AssignmentTurnedIn,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = todo.title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = colors.textPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                TagPill(todo.dueLabel, accent)
                if (todo.overdue) TagPill("En retard", Color(0xFFEF4444))
                if (todo.done) TagPill("Termine", Color(0xFF10B981))
            }
        }
        Icon(
            imageVector = Icons.Filled.WarningAmber,
            contentDescription = null,
            tint = if (todo.overdue) Color(0xFFEF4444) else Color.Transparent
        )
    }
}
