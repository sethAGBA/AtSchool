package com.ecolix.presentation.screens.signatures

import com.ecolix.data.models.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SignatureScreenModel {
    private val _state = MutableStateFlow(SignatureUiState())
    val state: StateFlow<SignatureUiState> = _state.asStateFlow()

    init {
        loadMockAssets()
    }

    fun onDarkModeChange(isDarkMode: Boolean) {
        _state.value = _state.value.copy(isDarkMode = isDarkMode)
    }

    fun onSearchQueryChange(query: String) {
        _state.value = _state.value.copy(searchQuery = query)
    }

    fun onTypeFilterChange(type: AssetType?) {
        _state.value = _state.value.copy(selectedType = type)
    }

    private fun loadMockAssets() {
        val mockAssets = listOf(
            DigitalAsset(
                id = "S1",
                ownerName = "M. Moussa Diop",
                ownerRole = "Directeur Général",
                type = AssetType.SIGNATURE,
                imageUri = null,
                isDefault = true,
                status = AssetStatus.ACTIVE,
                dateAdded = "15/01/2026"
            ),
            DigitalAsset(
                id = "S2",
                ownerName = "Mme Sophie Sarr",
                ownerRole = "Secrétaire Académique",
                type = AssetType.SIGNATURE,
                imageUri = null,
                isDefault = false,
                status = AssetStatus.ACTIVE,
                dateAdded = "18/01/2026"
            ),
            DigitalAsset(
                id = "C1",
                ownerName = "École AtSchool",
                ownerRole = "Administration",
                type = AssetType.SEAL,
                imageUri = null,
                isDefault = true,
                status = AssetStatus.ACTIVE,
                dateAdded = "10/01/2026"
            ),
            DigitalAsset(
                id = "C2",
                ownerName = "Service Comptabilité",
                ownerRole = "Finance",
                type = AssetType.SEAL,
                imageUri = null,
                isDefault = false,
                status = AssetStatus.ARCHIVED,
                dateAdded = "05/12/2025"
            )
        )

        _state.value = _state.value.copy(assets = mockAssets)
    }
}
