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
import com.ecolix.atschool.api.CreatePaymentRequest
import com.ecolix.atschool.api.UpdatePaymentStatusRequest
import com.ecolix.atschool.api.CreateAnnouncementRequest
import com.ecolix.atschool.api.CreateNotificationRequest
import com.ecolix.atschool.api.CreatePlanRequest
import com.ecolix.atschool.api.SubscriptionPlanDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.async
import kotlinx.datetime.*
import com.ecolix.atschool.api.SchoolActivityDto

enum class SuperAdminLayoutMode {
    LIST, GRID
}

enum class SuperAdminTab {
    SCHOOLS, ANNOUNCEMENTS, LOGS, ANALYTICS, BILLING, SYSTEM, SUPPORT
}

enum class AnalyticsPeriod {
    LAST_7_DAYS, LAST_30_DAYS, LAST_YEAR
}

sealed class SuperAdminState {
    object Loading : SuperAdminState()
    data class Success(
        val tenants: List<TenantDto>,
        val stats: GlobalStatsResponse,
        val announcements: List<AnnouncementDto> = emptyList(),
        val logs: List<AuditLogDto> = emptyList(),
        val payments: List<SubscriptionPaymentDto> = emptyList(),
        val plans: List<SubscriptionPlanDto> = emptyList(),
        val tickets: List<SupportTicketDto> = emptyList(),
        val growthMetrics: GrowthMetricsDto? = null,
        val schoolActivity: List<SchoolActivityDto> = emptyList(),
        val selectedPeriod: AnalyticsPeriod = AnalyticsPeriod.LAST_30_DAYS,
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
    private val _plans = MutableStateFlow<List<SubscriptionPlanDto>>(emptyList())
    private val _tickets = MutableStateFlow<List<SupportTicketDto>>(emptyList())
    private val _growthMetrics = MutableStateFlow<GrowthMetricsDto?>(null)
    private val _schoolActivity = MutableStateFlow<List<SchoolActivityDto>>(emptyList())
    private val _selectedPeriod = MutableStateFlow(AnalyticsPeriod.LAST_30_DAYS)
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
            try {
                val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
                val startDate = when (_selectedPeriod.value) {
                    AnalyticsPeriod.LAST_7_DAYS -> now.minus(7, DateTimeUnit.DAY)
                    AnalyticsPeriod.LAST_30_DAYS -> now.minus(30, DateTimeUnit.DAY)
                    AnalyticsPeriod.LAST_YEAR -> now.minus(1, DateTimeUnit.YEAR)
                }

                // Fire all requests in parallel to avoid sequential bottlenecks
                val tenantsDeferred = async { apiService.getTenants() }
                val statsDeferred = async { apiService.getGlobalStats() }
                val announcementsDeferred = async { apiService.getAnnouncements() }
                val logsDeferred = async { apiService.getAuditLogs() }
                val paymentsDeferred = async { apiService.getPayments() }
                val plansDeferred = async { apiService.getPlans() }
                val ticketsDeferred = async { apiService.getTickets() }
                val growthDeferred = async { apiService.getGrowthMetrics(startDate.toString(), now.toString()) }
                val activityDeferred = async { apiService.getSchoolActivity() }

                // Wait for core results, using defaults for others
                _allTenants.value = tenantsDeferred.await().getOrDefault(emptyList())
                _stats.value = statsDeferred.await().getOrNull()
                _announcements.value = announcementsDeferred.await().getOrDefault(emptyList())
                _logs.value = logsDeferred.await().getOrDefault(emptyList())
                _payments.value = paymentsDeferred.await().getOrDefault(emptyList())
                _plans.value = plansDeferred.await().getOrDefault(emptyList())
                _tickets.value = ticketsDeferred.await().getOrDefault(emptyList())
                _growthMetrics.value = growthDeferred.await().getOrNull()
                _schoolActivity.value = activityDeferred.await().getOrDefault(emptyList())

                updateState()
            } catch (e: Exception) {
                _state.value = SuperAdminState.Error(e.message ?: "Erreur inconnue")
            }
        }
    }

    private fun updateState() {
        val stats = _stats.value ?: GlobalStatsResponse(0,0,0.0) // Provide default if null to avoid blocking UI
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
            plans = _plans.value,
            tickets = _tickets.value,
            growthMetrics = _growthMetrics.value,
            schoolActivity = _schoolActivity.value,
            selectedPeriod = _selectedPeriod.value,
            selectedTab = _selectedTab.value,
            layoutMode = _layoutMode.value
        )
    }

    fun onPeriodChange(period: AnalyticsPeriod) {
        _selectedPeriod.value = period
        refresh()
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

    fun createAnnouncement(request: CreateAnnouncementRequest, onComplete: (Boolean) -> Unit) {
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

    fun recordPayment(tenantId: Int, amount: Double, paymentMethod: String, notes: String?, onComplete: (Boolean) -> Unit) {
        screenModelScope.launch {
            val request = CreatePaymentRequest(tenantId, amount, paymentMethod, notes)
            apiService.createPayment(request).onSuccess {
                refresh()
                onComplete(true)
            }.onFailure {
                onComplete(false)
            }
        }
    }

    fun updatePaymentStatus(paymentId: Long, status: String, invoiceNumber: String? = null) {
        screenModelScope.launch {
            apiService.updatePaymentStatus(paymentId, status, invoiceNumber).onSuccess {
                refresh()
            }
        }
    }

    fun sendNotification(tenantId: Int?, userId: Long?, title: String, message: String, type: String, priority: String, onComplete: (Boolean) -> Unit) {
        screenModelScope.launch {
            val request = CreateNotificationRequest(tenantId, userId, title, message, type, priority)
            apiService.sendNotification(request).onSuccess {
                refresh()
                onComplete(true)
            }.onFailure {
                onComplete(false)
            }
        }
    }

    fun createPlan(name: String, price: Double, description: String, isPopular: Boolean, onComplete: (Boolean) -> Unit) {
        screenModelScope.launch {
            val request = CreatePlanRequest(name, price, description, isPopular)
            apiService.createPlan(request).onSuccess {
                refresh()
                onComplete(true)
            }.onFailure {
                onComplete(false)
            }
        }
    }

    fun updatePlan(id: Int, name: String, price: Double, description: String, isPopular: Boolean, onComplete: (Boolean) -> Unit) {
        screenModelScope.launch {
            val request = CreatePlanRequest(name, price, description, isPopular)
            apiService.updatePlan(id, request).onSuccess {
                refresh()
                onComplete(true)
            }.onFailure {
                onComplete(false)
            }
        }
    }

    fun deletePlan(id: Int, onComplete: (Boolean) -> Unit) {
        screenModelScope.launch {
            apiService.deletePlan(id).onSuccess {
                refresh()
                onComplete(true)
            }.onFailure {
                onComplete(false)
            }
        }
    }
}
