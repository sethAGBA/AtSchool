package com.ecolix.data.models

enum class VaultItemType {
    DOCUMENT,
    RECORD,
    KEY,
    FOLDER
}

enum class VaultSensitivity {
    CONFIDENTIAL,
    SECRET,
    TOP_SECRET
}

data class VaultItem(
    val id: String,
    val name: String,
    val type: VaultItemType,
    val sensitivity: VaultSensitivity,
    val size: String? = null,
    val lastAccessed: String? = null,
    val lockedAt: String
)

data class VaultUiState(
    val isLocked: Boolean = true,
    val assets: List<VaultItem> = emptyList(),
    val pinBuffer: String = "",
    val errorMessage: String? = null,
    val isDarkMode: Boolean = false
) {
    val colors: DashboardColors
        get() = if (isDarkMode) DashboardColors.dark() else DashboardColors.light()
}
