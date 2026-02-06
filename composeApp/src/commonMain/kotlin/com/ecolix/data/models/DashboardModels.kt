package com.ecolix.data.models

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*

@Immutable
data class DashboardUiState(
    val colors: DashboardColors,
    val stats: List<StatCardData>,
    val enrollmentChartValues: List<Int>,
    val enrollmentChartLabels: List<String>,
    val activities: List<ActivityData>,
    val quickActions: List<QuickActionData>,
    val agendaDays: List<AgendaDay>,
    val dueItems: List<DueItem>,
    val todos: List<TodoItem>,
    val alerts: List<AlertData>
) {
    companion object {
        fun sample(isDarkMode: Boolean): DashboardUiState {
            val colors = if (isDarkMode) DashboardColors.dark() else DashboardColors.light()
            return DashboardUiState(
                colors = colors,
                stats = listOf(
                    StatCardData("Total Eleves", "1248", Icons.Filled.School, Color(0xFF3B82F6), ""),
                    StatCardData("Personnel", "68", Icons.Filled.Groups, Color(0xFF10B981), ""),
                    StatCardData("Classes", "38", Icons.Filled.School, Color(0xFFF59E0B), ""),
                    StatCardData("Revenus", "18.2M FCFA", Icons.Filled.Payments, Color(0xFFEF4444), "")
                ),
                enrollmentChartValues = listOf(120, 160, 180, 200, 230, 280, 310),
                enrollmentChartLabels = listOf("Jan", "Fev", "Mar", "Avr", "Mai", "Juin", "Juil"),
                activities = listOf(
                    ActivityData(
                        title = "Nouveau paiement recu",
                        subtitle = "Classe 3e B - 120 000 FCFA",
                        time = "09:10",
                        icon = Icons.Filled.Payments,
                        color = Color(0xFF3B82F6)
                    ),
                    ActivityData(
                        title = "Absence signalee",
                        subtitle = "Eleve: Binta D. (4e A)",
                        time = "08:45",
                        icon = Icons.Filled.WarningAmber,
                        color = Color(0xFFF59E0B)
                    ),
                    ActivityData(
                        title = "Nouvel enseignant",
                        subtitle = "Mme Diallo - Sciences",
                        time = "08:10",
                        icon = Icons.Filled.PersonAdd,
                        color = Color(0xFF10B981)
                    )
                ),
                quickActions = listOf(
                    QuickActionData("Nouvel Eleve", Icons.Filled.PersonAdd, Color(0xFF10B981)),
                    QuickActionData("Saisir Notes", Icons.Filled.Edit, Color(0xFF3B82F6)),
                    QuickActionData("Generer Bulletin", Icons.Filled.Description, Color(0xFFF59E0B)),
                    QuickActionData("Emploi du Temps", Icons.Filled.Schedule, Color(0xFF8B5CF6)),
                    QuickActionData("Paiements", Icons.Filled.Payments, Color(0xFF4CAF50)),
                    QuickActionData("Ajouter Personnel", Icons.Filled.PersonAddAlt, Color(0xFF60A5FA)),
                    QuickActionData("Annuler Paiement", Icons.Filled.Timeline, Color(0xFFEF4444)),
                    QuickActionData("Finance et Materiel", Icons.Filled.Inventory2, Color(0xFFF59E0B))
                ),
                agendaDays = listOf(
                    AgendaDay("Lun 18", 2),
                    AgendaDay("Mar 19", 0),
                    AgendaDay("Mer 20", 1),
                    AgendaDay("Jeu 21", 3),
                    AgendaDay("Ven 22", 0),
                    AgendaDay("Sam 23", 1),
                    AgendaDay("Dim 24", 0)
                ),
                dueItems = listOf(
                    DueItem("18/09", "Relance impayes", "3e B", Color(0xFF0EA5E9)),
                    DueItem("19/09", "Retour livres", "Bibliotheque", Color(0xFFEF4444)),
                    DueItem("21/09", "Conseil classe", "Terminale S", Color(0xFF8B5CF6))
                ),
                todos = listOf(
                    TodoItem("Relancer les impayes", "Echeance 20/09/2025", done = false, overdue = false, dueSoon = true),
                    TodoItem("Signer les bulletins", "Echeance 18/09/2025", done = false, overdue = true, dueSoon = false),
                    TodoItem("Exporter les statistiques", "Sans echeance", done = true, overdue = false, dueSoon = false)
                ),
                alerts = listOf(
                    AlertData(
                        title = "Impayes (estimation)",
                        subtitle = "Reste a encaisser: 8.5M FCFA / 26.7M FCFA",
                        icon = Icons.Filled.Payments,
                        color = Color(0xFFF59E0B),
                        actionLabel = "Voir paiements"
                    ),
                    AlertData(
                        title = "Bibliotheque",
                        subtitle = "2 emprunts en retard",
                        icon = Icons.AutoMirrored.Filled.LibraryBooks,
                        color = Color(0xFFEF4444),
                        actionLabel = "Voir bibliotheque"
                    ),
                    AlertData(
                        title = "Discipline",
                        subtitle = "3 sanctions ces 7 derniers jours",
                        icon = Icons.Filled.Gavel,
                        color = Color(0xFF8B5CF6),
                        actionLabel = "Voir discipline"
                    )
                )
            )
        }
    }
}

@Immutable
data class DashboardColors(
    val background: Color,
    val card: Color,
    val textPrimary: Color,
    val textMuted: Color,
    val divider: Color,
    val textLink: Color,
    val success: Color = Color(0xFF10B981),
    val error: Color = Color(0xFFEF4444)
) {
    companion object {
        fun light() = DashboardColors(
            background = Color(0xFFF5F7FB),
            card = Color(0xFFFFFFFF),
            textPrimary = Color(0xFF1E293B),
            textMuted = Color(0xFF64748B),
            divider = Color(0xFFE2E8F0),
            textLink = Color(0xFF3B82F6),
            success = Color(0xFF10B981),
            error = Color(0xFFEF4444)
        )

        fun dark() = DashboardColors(
            background = Color(0xFF0F172A),
            card = Color(0xFF111827),
            textPrimary = Color(0xFFF8FAFC),
            textMuted = Color(0xFF94A3B8),
            divider = Color(0xFF1F2937),
            textLink = Color(0xFF60A5FA),
            success = Color(0xFF10B981),
            error = Color(0xFFEF4444)
        )
    }
}

@Immutable
data class StatCardData(
    val title: String,
    val value: String,
    val icon: ImageVector,
    val color: Color,
    val subtitle: String
)

@Immutable
data class ActivityData(
    val title: String,
    val subtitle: String,
    val time: String,
    val icon: ImageVector,
    val color: Color
)

@Immutable
data class QuickActionData(
    val title: String,
    val icon: ImageVector,
    val color: Color
)

@Immutable
data class AgendaDay(
    val label: String,
    val count: Int
)

@Immutable
data class DueItem(
    val dateLabel: String,
    val title: String,
    val subtitle: String,
    val color: Color
)

@Immutable
data class TodoItem(
    val title: String,
    val dueLabel: String,
    val done: Boolean,
    val overdue: Boolean,
    val dueSoon: Boolean
)

@Immutable
data class AlertData(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val color: Color,
    val actionLabel: String
)
