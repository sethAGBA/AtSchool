package com.ecolix.data.models

import androidx.compose.ui.graphics.Color

data class DataPoint(
    val label: String,
    val value: Float,
    val color: Color? = null
)

data class ChartSeries(
    val name: String,
    val points: List<DataPoint>,
    val color: Color
)

data class KpiMetric(
    val title: String,
    val value: String,
    val trend: Float, // Percentage change
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color,
    val sparkline: List<Float> = emptyList()
)

data class StatsUiState(
    val selectedTab: Int = 0,
    val globalKpis: List<KpiMetric> = emptyList(),
    val enrollmentSeries: List<ChartSeries> = emptyList(),
    val successRateSeries: List<DataPoint> = emptyList(),
    val revenueSeries: ChartSeries? = null,
    val isLoading: Boolean = false,
    val error: String? = null
) {
    companion object {
        fun mock(): StatsUiState = StatsUiState() // Will be populated in ScreenModel
    }
}
