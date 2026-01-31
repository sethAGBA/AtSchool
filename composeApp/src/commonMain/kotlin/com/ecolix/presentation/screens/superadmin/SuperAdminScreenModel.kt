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

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _allTenants = MutableStateFlow<List<TenantDto>>(emptyList())
    private val _stats = MutableStateFlow<GlobalStatsResponse?>(null)

    init {
        refresh()
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        updateState()
    }

    fun refresh() {
        screenModelScope.launch {
            _state.value = SuperAdminState.Loading
            val tenantsResult = apiService.getTenants()
            val statsResult = apiService.getGlobalStats()

            if (tenantsResult.isSuccess && statsResult.isSuccess) {
                _allTenants.value = tenantsResult.getOrThrow()
                _stats.value = statsResult.getOrThrow()
                updateState()
            } else {
                _state.value = SuperAdminState.Error("Erreur de chargement des données")
            }
        }
    }

    private fun updateState() {
        val stats = _stats.value ?: return
        val filteredTenants = if (_searchQuery.value.isEmpty()) {
            _allTenants.value
        } else {
            _allTenants.value.filter {
                it.name.contains(_searchQuery.value, ignoreCase = true) ||
                        it.code.contains(_searchQuery.value, ignoreCase = true)
            }
        }
        _state.value = SuperAdminState.Success(filteredTenants, stats)
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
