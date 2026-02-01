package com.ecolix.presentation.screens.superadmin

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.ecolix.atschool.api.CreateTenantRequest
import com.ecolix.atschool.api.GlobalStatsResponse
import com.ecolix.atschool.api.SuperAdminApiService
import com.ecolix.atschool.api.TenantDto
import com.ecolix.atschool.api.AnnouncementDto
import com.ecolix.atschool.api.AuditLogDto
import com.ecolix.atschool.api.SubscriptionPaymentDto
import com.ecolix.atschool.api.SupportTicketDto
import com.ecolix.atschool.api.GrowthMetricsDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class SuperAdminLayoutMode {
    LIST, GRID
}

enum class SuperAdminTab {
    SCHOOLS, ANNOUNCEMENTS, LOGS, ANALYTICS, BILLING, SYSTEM, SUPPORT
}

sealed class SuperAdminState {
    object Loading : SuperAdminState()
    data class Success(
        val tenants: List<TenantDto>,
        val stats: GlobalStatsResponse,
        val announcements: List<AnnouncementDto> = emptyList(),
        val logs: List<AuditLogDto> = emptyList(),
        val payments: List<SubscriptionPaymentDto> = emptyList(),
        val tickets: List<SupportTicketDto> = emptyList(),
        val growthMetrics: GrowthMetricsDto? = null,
        val selectedTab: SuperAdminTab = SuperAdminTab.SCHOOLS,
        val layoutMode: SuperAdminLayoutMode = SuperAdminLayoutMode.LIST
    ) : SuperAdminState()
    data class Error(val message: String) : SuperAdminState()
}

class SuperAdminScreenModel(private val apiService: SuperAdminApiService) : ScreenModel {
    private val _state = MutableStateFlow<SuperAdminState>(SuperAdminState.Loading)
    val state = _state.asStateFlow()

    private val _layoutMode = MutableStateFlow(SuperAdminLayoutMode.LIST)
    val layoutMode = _layoutMode.asStateFlow()

    private val _selectedTab = MutableStateFlow(SuperAdminTab.SCHOOLS)
    val selectedTab = _selectedTab.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _allTenants = MutableStateFlow<List<TenantDto>>(emptyList())
    private val _announcements = MutableStateFlow<List<AnnouncementDto>>(emptyList())
    private val _logs = MutableStateFlow<List<AuditLogDto>>(emptyList())
    private val _payments = MutableStateFlow<List<SubscriptionPaymentDto>>(emptyList())
    private val _tickets = MutableStateFlow<List<SupportTicketDto>>(emptyList())
    private val _growthMetrics = MutableStateFlow<GrowthMetricsDto?>(null)
    private val _stats = MutableStateFlow<GlobalStatsResponse?>(null)

    init {
        refresh()
    }
    
    fun onTabChange(tab: SuperAdminTab) {
        _selectedTab.value = tab
        updateState()
    }

    fun onLayoutModeChange(mode: SuperAdminLayoutMode) {
        _layoutMode.value = mode
        updateState()
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
            val announcementsResult = apiService.getAnnouncements()
            val logsResult = apiService.getAuditLogs()
            val paymentsResult = apiService.getPayments()
            val ticketsResult = apiService.getTickets()
            val growthResult = apiService.getGrowthMetrics()

            if (tenantsResult.isSuccess && statsResult.isSuccess) {
                _allTenants.value = tenantsResult.getOrThrow()
                _stats.value = statsResult.getOrThrow()
                _announcements.value = announcementsResult.getOrDefault(emptyList())
                _logs.value = logsResult.getOrDefault(emptyList())
                _payments.value = paymentsResult.getOrDefault(emptyList())
                _tickets.value = ticketsResult.getOrDefault(emptyList())
                _growthMetrics.value = growthResult.getOrNull()
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
        _state.value = SuperAdminState.Success(
            tenants = filteredTenants,
            stats = stats,
            announcements = _announcements.value,
            logs = _logs.value,
            payments = _payments.value,
            tickets = _tickets.value,
            growthMetrics = _growthMetrics.value,
            selectedTab = _selectedTab.value,
            layoutMode = _layoutMode.value
        )
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

    fun toggleTenantStatus(tenantId: Int, isActive: Boolean) {
        screenModelScope.launch {
            val result = apiService.updateTenantStatus(tenantId, isActive)
            if (result.isSuccess) {
                refresh()
            }
        }
    }

    fun resetAdminPassword(tenantId: Int, newPassword: String, onComplete: (Boolean) -> Unit) {
        screenModelScope.launch {
            val result = apiService.resetAdminPassword(tenantId, newPassword)
            if (result.isSuccess) {
                onComplete(true)
            } else {
                onComplete(false)
            }
        }
    }

    fun updateSubscription(tenantId: Int, expiresAt: String?) {
        screenModelScope.launch {
            val result = apiService.updateSubscription(tenantId, expiresAt)
            if (result.isSuccess) {
                refresh()
            }
        }
    }

    fun createAnnouncement(request: com.ecolix.atschool.api.CreateAnnouncementRequest, onComplete: (Boolean) -> Unit) {
        screenModelScope.launch {
            val result = apiService.createAnnouncement(request)
            if (result.isSuccess) {
                refresh()
                onComplete(true)
            } else {
                onComplete(false)
            }
        }
    }
}
