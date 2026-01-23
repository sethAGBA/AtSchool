package com.ecolix.atschool.ui.dashboard.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecolix.atschool.ui.dashboard.models.DashboardUiState

@Composable
fun EnrollmentChartCard(state: DashboardUiState) {
    CardContainer(containerColor = state.colors.card) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(
                text = "Evolution des inscriptions",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                color = state.colors.textPrimary
            )
            EnrollmentChart(
                values = state.enrollmentChartValues,
                labels = state.enrollmentChartLabels,
                lineColor = Color(0xFF3B82F6),
                gridColor = state.colors.divider
            )
        }
    }
}

@Composable
fun EnrollmentChart(
    values: List<Int>,
    labels: List<String>,
    lineColor: Color,
    gridColor: Color
) {
    val maxValue = (values.maxOrNull() ?: 1).coerceAtLeast(1)
    val minValue = (values.minOrNull() ?: 0)
    val range = (maxValue - minValue).coerceAtLeast(1)

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
        ) {
            val width = size.width
            val height = size.height
            val leftPadding = 20f
            val bottomPadding = 24f
            val topPadding = 12f
            val usableHeight = height - bottomPadding - topPadding
            val usableWidth = width - leftPadding

            val stepX = if (values.size > 1) usableWidth / (values.size - 1) else 0f

            val gridLines = 4
            repeat(gridLines + 1) { index ->
                val y = topPadding + usableHeight * (index / gridLines.toFloat())
                drawLine(
                    color = gridColor.copy(alpha = 0.4f),
                    start = androidx.compose.ui.geometry.Offset(leftPadding, y),
                    end = androidx.compose.ui.geometry.Offset(width, y),
                    strokeWidth = 1f
                )
            }

            val path = Path()
            values.forEachIndexed { index, value ->
                val x = leftPadding + stepX * index
                val normalized = (value - minValue).toFloat() / range
                val y = topPadding + usableHeight * (1f - normalized)
                if (index == 0) {
                    path.moveTo(x, y)
                } else {
                    path.lineTo(x, y)
                }
            }

            drawPath(
                path = path,
                color = lineColor,
                style = Stroke(width = 4f)
            )

            values.forEachIndexed { index, value ->
                val x = leftPadding + stepX * index
                val normalized = (value - minValue).toFloat() / range
                val y = topPadding + usableHeight * (1f - normalized)
                drawCircle(color = lineColor, radius = 5f, center = androidx.compose.ui.geometry.Offset(x, y))
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            labels.forEach { label ->
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF64748B)
                )
            }
        }
    }
}
