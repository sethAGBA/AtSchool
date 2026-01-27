package com.ecolix.presentation.screens.timetable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecolix.data.models.*

@Composable
fun TimetableGrid(
    state: TimetableUiState,
    sessions: List<TimetableSession>,
    modifier: Modifier = Modifier
) {
    val days = listOf(
        DayOfWeek.MONDAY,
        DayOfWeek.TUESDAY,
        DayOfWeek.WEDNESDAY,
        DayOfWeek.THURSDAY,
        DayOfWeek.FRIDAY,
        DayOfWeek.SATURDAY
    )

    Column(modifier = modifier.fillMaxSize()) {
        // Days Column header
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
            Spacer(modifier = Modifier.width(80.dp)) // Time column spacer
            days.forEach { day ->
                Text(
                    text = day.toFrench(),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = state.colors.textPrimary
                )
            }
        }

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(state.timeSlots) { slot ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .border(0.5.dp, state.colors.divider.copy(alpha = 0.5f))
                ) {
                    // Time label
                    Column(
                        modifier = Modifier
                            .width(80.dp)
                            .fillMaxHeight()
                            .padding(8.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = slot.startTime,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = state.colors.textPrimary
                        )
                        Text(
                            text = slot.endTime,
                            style = MaterialTheme.typography.labelSmall,
                            color = state.colors.textMuted
                        )
                    }

                    // Cells for each day
                    days.forEach { day ->
                        val session = sessions.find { it.dayOfWeek == day && it.timeSlot.id == slot.id }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .border(0.5.dp, state.colors.divider.copy(alpha = 0.3f))
                                .padding(4.dp)
                        ) {
                            if (session != null) {
                                SessionCard(session, state.colors)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SessionCard(session: TimetableSession, colors: DashboardColors) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(8.dp))
            .background(session.color.copy(alpha = 0.15f))
            .border(1.dp, session.color.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        Column {
            Text(
                text = session.subjectName,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = session.color,
                maxLines = 1
            )
            Text(
                text = session.teacherName,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                color = colors.textPrimary,
                maxLines = 1
            )
            Spacer(modifier = Modifier.weight(1f))
            if (session.roomName != null) {
                Text(
                    text = session.roomName,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp, fontWeight = FontWeight.SemiBold),
                    color = colors.textMuted,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}
