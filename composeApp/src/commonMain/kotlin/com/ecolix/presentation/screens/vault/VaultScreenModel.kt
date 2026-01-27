package com.ecolix.presentation.screens.vault

import com.ecolix.data.models.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.Clock

class VaultScreenModel {
    private val _state = MutableStateFlow(VaultUiState())
    val state: StateFlow<VaultUiState> = _state.asStateFlow()

    private val correctPin = "1234"

    fun onDarkModeChange(isDarkMode: Boolean) {
        _state.value = _state.value.copy(isDarkMode = isDarkMode)
    }

    fun onPinInput(digit: String) {
        if (_state.value.pinBuffer.length < 4) {
            val newBuffer = _state.value.pinBuffer + digit
            _state.value = _state.value.copy(pinBuffer = newBuffer, errorMessage = null)
            
            if (newBuffer.length == 4) {
                verifyPin(newBuffer)
            }
        }
    }

    fun onBackspace() {
        if (_state.value.pinBuffer.isNotEmpty()) {
            _state.value = _state.value.copy(pinBuffer = _state.value.pinBuffer.dropLast(1))
        }
    }

    fun lockVault() {
        _state.value = _state.value.copy(isLocked = true, pinBuffer = "")
    }

    private fun verifyPin(pin: String) {
        if (pin == correctPin) {
            _state.value = _state.value.copy(isLocked = false, pinBuffer = "", errorMessage = null)
            loadVaultAssets()
        } else {
            _state.value = _state.value.copy(pinBuffer = "", errorMessage = "Code PIN incorrect")
        }
    }

    private fun loadVaultAssets() {
        val mockAssets = listOf(
            VaultItem(
                id = "V1",
                name = "Contrats Direction 2024",
                type = VaultItemType.FOLDER,
                sensitivity = VaultSensitivity.TOP_SECRET,
                size = "4.2 MB",
                lastAccessed = "Hier, 14:20",
                lockedAt = "20/01/2026"
            ),
            VaultItem(
                id = "V2",
                name = "Bilan Financier Annuel - Brouillon",
                type = VaultItemType.DOCUMENT,
                sensitivity = VaultSensitivity.SECRET,
                size = "1.5 MB",
                lastAccessed = "Aujourd'hui, 09:12",
                lockedAt = "24/01/2026"
            ),
            VaultItem(
                id = "V3",
                name = "Identifiants Plateforme Ministérielle",
                type = VaultItemType.KEY,
                sensitivity = VaultSensitivity.TOP_SECRET,
                size = "12 KB",
                lastAccessed = "Il y a 3 jours",
                lockedAt = "15/01/2026"
            ),
            VaultItem(
                id = "V4",
                name = "Listes Noires Discipline (Confidentiel)",
                type = VaultItemType.RECORD,
                sensitivity = VaultSensitivity.CONFIDENTIAL,
                size = "120 KB",
                lastAccessed = "22/01/2026",
                lockedAt = "25/01/2026"
            )
        )
        _state.value = _state.value.copy(assets = mockAssets)
    }
}
