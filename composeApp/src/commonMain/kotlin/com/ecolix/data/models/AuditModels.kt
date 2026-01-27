package com.ecolix.data.models

enum class LogSeverity {
    INFO,
    WARNING,
    CRITICAL,
    SECURITY;

    fun toFrench(): String = when (this) {
        INFO -> "Information"
        WARNING -> "Avertissement"
        CRITICAL -> "Critique"
        SECURITY -> "Sécurité"
    }
}

data class AuditLog(
    val id: String,
    val userId: String,
    val userName: String,
    val userRole: String,
    val action: String,
    val module: String,
    val timestamp: String,
    val severity: LogSeverity,
    val ipAddress: String? = null,
    val details: String? = null
)

data class AuditUiState(
    val logs: List<AuditLog> = emptyList(),
    val searchQuery: String = "",
    val selectedModule: String? = null,
    val selectedSeverity: LogSeverity? = null,
    val isLoading: Boolean = false,
    val isDarkMode: Boolean = false
) {
    val colors: DashboardColors
        get() = if (isDarkMode) DashboardColors.dark() else DashboardColors.light()
    
    val modules: List<String>
        get() = logs.map { it.module }.distinct().sorted()
}
