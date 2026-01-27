package com.ecolix.data.models

enum class AssetType {
    SIGNATURE,
    SEAL
}

enum class AssetStatus {
    ACTIVE,
    ARCHIVED,
    PENDING_APPROVAL;

    fun toFrench(): String = when (this) {
        ACTIVE -> "Actif"
        ARCHIVED -> "Archivé"
        PENDING_APPROVAL -> "En attente"
    }
}

data class DigitalAsset(
    val id: String,
    val ownerName: String,
    val ownerRole: String,
    val type: AssetType,
    val imageUri: String?, // Image resource path or URL
    val isDefault: Boolean = false,
    val status: AssetStatus = AssetStatus.ACTIVE,
    val dateAdded: String
)

data class SignatureUiState(
    val assets: List<DigitalAsset> = emptyList(),
    val searchQuery: String = "",
    val selectedType: AssetType? = null,
    val isLoading: Boolean = false,
    val isDarkMode: Boolean = false
) {
    val colors: DashboardColors
        get() = if (isDarkMode) DashboardColors.dark() else DashboardColors.light()
}
