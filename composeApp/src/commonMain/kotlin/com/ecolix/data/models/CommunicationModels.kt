package com.ecolix.data.models

enum class CommunicationType {
    ANNOUNCEMENT,
    SMS,
    EMAIL,
    PUSH
}

enum class MessageStatus {
    SENT,
    SCHEDULED,
    DRAFT,
    FAILED;

    fun toFrench(): String = when (this) {
        SENT -> "Envoyé"
        SCHEDULED -> "Programmé"
        DRAFT -> "Brouillon"
        FAILED -> "Échec"
    }
}

data class CommunicationTemplate(
    val id: String,
    val name: String,
    val content: String,
    val type: CommunicationType,
    val variables: List<String> = emptyList()
)

data class SchoolMessage(
    val id: String,
    val title: String,
    val content: String,
    val sender: String,
    val recipients: String, // e.g., "Tous les parents", "Classe 6ème A"
    val channel: CommunicationType,
    val status: MessageStatus,
    val timestamp: String,
    val scheduledAt: String? = null,
    val isAutomated: Boolean = false,
    val attachments: List<String> = emptyList()
)

data class ReminderRule(
    val id: String,
    val name: String,
    val trigger: String, // e.g., "Retard > 15min"
    val channel: CommunicationType,
    val isActive: Boolean = true,
    val templateId: String? = null
)

data class CommunicationUiState(
    val messages: List<SchoolMessage> = emptyList(),
    val templates: List<CommunicationTemplate> = emptyList(),
    val reminderRules: List<ReminderRule> = emptyList(),
    val searchQuery: String = "",
    val selectedChannel: CommunicationType? = null,
    val selectedStatus: MessageStatus? = null,
    val selectedTab: Int = 1, // 0: Messages, 1: Templates, 2: Reminders/Scheduled
    val isLoading: Boolean = false,
    val isDarkMode: Boolean = false
) {
    val colors: DashboardColors
        get() = if (isDarkMode) DashboardColors.dark() else DashboardColors.light()
}
