package com.ecolix.presentation.screens.superadmin

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.ecolix.atschool.api.CreateTenantRequest
import com.ecolix.atschool.api.GlobalStatsResponse
import com.ecolix.atschool.api.SuperAdminApiService
import com.ecolix.atschool.api.TenantDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class SuperAdminState {
    object Loading : SuperAdminState()
    data class Success(
        val tenants: List<TenantDto>,
        val stats: GlobalStatsResponse
    ) : SuperAdminState()
    data class Error(val message: String) : SuperAdminState()
}

class SuperAdminScreenModel(private val apiService: SuperAdminApiService) : ScreenModel {
    private val _state = MutableStateFlow<SuperAdminState>(SuperAdminState.Loading)
    val state = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        screenModelScope.launch {
            _state.value = SuperAdminState.Loading
            val tenantsResult = apiService.getTenants()
            val statsResult = apiService.getGlobalStats()

            if (tenantsResult.isSuccess && statsResult.isSuccess) {
                _state.value = SuperAdminState.Success(
                    tenantsResult.getOrThrow(),
                    statsResult.getOrThrow()
                )
            } else {
                _state.value = SuperAdminState.Error("Erreur de chargement des données")
            }
        }
    }

    fun createTenant(request: CreateTenantRequest, onComplete: (Boolean) -> Unit) {
        screenModelScope.launch {
            val result = apiService.createTenant(request)
            if (result.isSuccess) {
                refresh()
                onComplete(true)
            } else {
                onComplete(false)
            }
        }
    }
}
