package com.ecolix.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecolix.data.models.ChartSeries
import com.ecolix.data.models.DashboardColors
import com.ecolix.data.models.KpiMetric

@Composable
fun KpiCard(
    metric: KpiMetric,
    colors: DashboardColors,
    modifier: Modifier = Modifier
) {
    CardContainer(
        containerColor = colors.card,
        modifier = modifier
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(metric.color.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(metric.icon, null, tint = metric.color, modifier = Modifier.size(22.dp))
                }
                
                val trendColor = if (metric.trend >= 0) Color(0xFF10B981) else Color(0xFFEF4444)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        if (metric.trend >= 0) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                        null,
                        tint = trendColor,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "${if (metric.trend >= 0) "+" else ""}${metric.trend}%",
                        color = trendColor,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
            
            Column {
                Text(metric.title, style = MaterialTheme.typography.labelMedium, color = colors.textMuted)
                Text(
                    metric.value, 
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = colors.textPrimary
                )
            }
            
            if (metric.sparkline.isNotEmpty()) {
                Sparkline(
                    data = metric.sparkline,
                    color = metric.color,
                    modifier = Modifier.fillMaxWidth().height(30.dp)
                )
            }
        }
    }
}

@Composable
fun Sparkline(
    data: List<Float>,
    color: Color,
    modifier: Modifier = Modifier
) {
    val animateProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
    )

    Canvas(modifier = modifier) {
        if (data.size < 2) return@Canvas
        
        val width = size.width
        val height = size.height
        val max = data.maxOf { it }
        val min = data.minOf { it }
        val range = if (max == min) 1f else max - min
        
        val path = Path()
        val stepX = width / (data.size - 1)
        
        data.forEachIndexed { index, value ->
            val x = index * stepX
            val y = height - ((value - min) / range * height)
            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        
        drawPath(
            path = path,
            color = color,
            style = Stroke(width = 2.dp.toPx()),
            alpha = animateProgress
        )
    }
}

@Composable
fun EcobizBarChart(
    series: List<ChartSeries>,
    colors: DashboardColors,
    modifier: Modifier = Modifier
) {
    val animateHeight by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(1200, easing = FastOutSlowInEasing)
    )

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(colors.card)
            .padding(24.dp)
    ) {
        Text(
            "Analyse Comparative des Effectifs",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = colors.textPrimary
        )
        Spacer(modifier = Modifier.height(24.dp))
        
        Canvas(modifier = Modifier.fillMaxWidth().height(200.dp)) {
            val width = size.width
            val height = size.height
            val labelsCount = series.firstOrNull()?.points?.size ?: 0
            if (labelsCount == 0) return@Canvas
            
            val barWidth = (width / labelsCount) * 0.6f
            val spacing = (width / labelsCount) * 0.4f
            val maxValue = series.flatMap { it.points }.maxOf { it.value } * 1.2f
            
            series.forEachIndexed { sIndex, s ->
                s.points.forEachIndexed { pIndex, p ->
                    val x = (pIndex * (barWidth + spacing)) + (sIndex * (barWidth / series.size))
                    val bHeight = (p.value / maxValue) * height * animateHeight
                    
                    drawRoundRect(
                        color = s.color,
                        topLeft = Offset(x, height - bHeight),
                        size = Size(barWidth / series.size, bHeight),
                        cornerRadius = CornerRadius(4.dp.toPx())
                    )
                }
            }
            
            // Draw x-axis labels (Simplified)
            // In a real app, use native Canvas text or LayoutCoordinates
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Legend
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            series.forEach { s ->
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 8.dp)) {
                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(s.color))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(s.name, style = MaterialTheme.typography.labelSmall, color = colors.textMuted)
                }
            }
        }
    }
}

@Composable
fun DonutChart(
    data: List<Float>,
    colors: List<Color>,
    modifier: Modifier = Modifier,
    centerText: String = "",
    centerSubText: String = "",
    textColor: Color = Color.Black
) {
    var animationPlayed by remember { mutableStateOf(false) }
    val animateRotation by animateFloatAsState(
        targetValue = if (animationPlayed) 360f else 0f,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
    )

    LaunchedEffect(key1 = true) {
        animationPlayed = true
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val total = data.sum()
            var startAngle = -90f + animateRotation - 360f // Rotate with animation
            
            val strokeWidth = 30.dp.toPx()
            val radius = (size.minDimension - strokeWidth) / 2
            
            data.forEachIndexed { index, value ->
                val sweepAngle = (value / total) * 360f
                val color = colors.getOrElse(index) { Color.Gray }
                
                drawArc(
                    color = color,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2, radius * 2),
                    style = Stroke(width = strokeWidth)
                )
                
                startAngle += sweepAngle
            }
        }
        
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = centerText,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = textColor
            )
            if (centerSubText.isNotEmpty()) {
                Text(
                    text = centerSubText,
                    style = MaterialTheme.typography.bodySmall,
                    color = textColor.copy(alpha = 0.7f)
                )
            }
        }
    }
}
