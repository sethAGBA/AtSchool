package com.ecolix.presentation.screens.dashboard

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.ecolix.atschool.api.DashboardApiService
import com.ecolix.atschool.api.DashboardStatsResponse
import com.ecolix.data.models.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.School
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DashboardScreenModel(private val dashboardApiService: DashboardApiService) : 
    StateScreenModel<DashboardUiState>(DashboardUiState.sample(false)) {

    fun refreshStats() {
        screenModelScope.launch {
            dashboardApiService.getStats()
                .onSuccess { response ->
                    mutableState.update { state ->
                        state.copy(
                            stats = listOf(
                                StatCardData("Total Eleves", response.totalStudents.toString(), Icons.Filled.School, Color(0xFF3B82F6), ""),
                                StatCardData("Personnel", response.totalStaff.toString(), Icons.Filled.Groups, Color(0xFF10B981), ""),
                                StatCardData("Classes", response.totalClasses.toString(), Icons.Filled.School, Color(0xFFF59E0B), ""),
                                StatCardData("Revenus", "${response.totalRevenue} FCFA", Icons.Filled.Payments, Color(0xFFEF4444), "")
                            ),
                            activities = response.recentActivities.map { dto ->
                                ActivityData(
                                    title = dto.title,
                                    subtitle = dto.subtitle,
                                    time = dto.time,
                                    icon = if (dto.type == "PAYMENT") Icons.Filled.Payments else Icons.Filled.School,
                                    color = if (dto.type == "PAYMENT") Color(0xFF3B82F6) else Color(0xFF10B981)
                                )
                            }
                        )
                    }
                }
                .onFailure { e ->
                    println("Dashboard stats load failed: ${e.message}")
                    // Keep sample data or show error state if needed
                }
        }
    }

    fun onDarkModeChange(isDark: Boolean) {
        mutableState.update { it.copy(colors = if (isDark) DashboardColors.dark() else DashboardColors.light()) }
    }
}
