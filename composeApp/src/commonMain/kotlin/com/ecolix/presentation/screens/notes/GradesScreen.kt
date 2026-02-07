package com.ecolix.presentation.screens.notes

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecolix.data.models.EvaluationTemplate
import com.ecolix.data.models.GradesUiState
import com.ecolix.data.models.GradesViewMode
import com.ecolix.data.models.PeriodMode
import com.ecolix.presentation.components.*
import com.ecolix.presentation.screens.notes.tabs.grades.GradesListView
import com.ecolix.presentation.screens.notes.tabs.grades.GradeEntryForm
import com.ecolix.presentation.screens.notes.tabs.grades.EvaluationConfigView
import com.ecolix.presentation.screens.notes.tabs.bulletins.BulletinsListView
import com.ecolix.presentation.screens.notes.tabs.archives.ArchivesView

import org.koin.compose.koinInject

@Composable
fun GradesScreenContent(isDarkMode: Boolean) {
    val screenModel = koinInject<com.ecolix.presentation.screens.notes.GradesScreenModel>()
    val state by screenModel.state.collectAsState()

    LaunchedEffect(isDarkMode) {
        screenModel.onDarkModeChange(isDarkMode)
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val screenWidth = maxWidth
        val isCompact = screenWidth < 700.dp
        
        if (isCompact) {
            // Mobile: Sub-views handle their own scrollable headers
            Box(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                AnimatedContent(
                    targetState = state.viewMode,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    modifier = Modifier.fillMaxSize()
                ) { mode ->
                    when (mode) {
                        GradesViewMode.NOTES -> GradesListView(
                            screenModel = screenModel,
                            isCompact = true,
                            onAddGrade = { screenModel.onViewModeChange(GradesViewMode.GRADE_FORM) },
                            onOpenConfig = { screenModel.onViewModeChange(GradesViewMode.CONFIG) }
                        )
                        GradesViewMode.BULLETINS -> BulletinsListView(
                            screenModel = screenModel,
                            isCompact = true,
                            onSelectBulletin = { screenModel.onSelectBulletin(it) },
                            onBackFromPreview = { screenModel.clearSelectedReportCard() },
                            onExportPdf = { screenModel.exportReportCardToPdf() },
                            onGenerateAll = { screenModel.exportAllBulletinsToPdf() }
                        )
                        GradesViewMode.ARCHIVES -> ArchivesView(
                            screenModel = screenModel,
                            isCompact = true
                        )
                        GradesViewMode.GRADE_FORM -> GradeEntryForm(
                            state = state,
                            students = state.currentClassStudents,
                            isCompact = true,
                            onBack = { screenModel.onViewModeChange(GradesViewMode.NOTES) },
                            onSave = { template, date, grades -> screenModel.onSaveSession(template, date, grades) },
                            onClassSelected = { screenModel.onClassSelected(it) },
                            onScrollToEnd = { screenModel.loadMoreStudents() }
                        )
                        GradesViewMode.CONFIG -> EvaluationConfigView(
                            state = state,
                            onBack = { screenModel.onViewModeChange(GradesViewMode.NOTES) },
                            onTemplatesUpdated = { updated -> screenModel.onTemplateUpdated(updated) }
                        )
                    }
                }
            }
        } else {
            // Desktop: Fixed layout with scrollable content area
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header (Desktop)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Notes & Bulletins",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = if (screenWidth < 1000.dp) 24.sp else 28.sp
                            ),
                            color = state.colors.textPrimary
                        )
                        Text(
                            text = "Suivi des performances et gestion des résultats académiques",
                            style = MaterialTheme.typography.bodyMedium,
                            color = state.colors.textMuted
                        )
                    }
                    
                    GradesViewToggle(
                        currentMode = state.viewMode,
                        onModeChange = { screenModel.onViewModeChange(it) },
                        colors = state.colors
                    )
                }

                // Summary (Desktop)
                AnimatedVisibility(visible = state.viewMode == GradesViewMode.NOTES && state.searchQuery.isEmpty()) {
                    AcademicSummaryCards(summary = state.summary, colors = state.colors, isCompact = false)
                }

                // Filters (Desktop)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        SearchBar(
                            query = state.searchQuery,
                            onQueryChange = { screenModel.onSearchQueryChange(it) },
                            colors = state.colors,
                            modifier = Modifier.width(250.dp)
                        )
                        
                        SpecificClassSelector(
                            selectedClass = state.selectedClassroom ?: "Toutes les classes",
                            onClassChange = { selectedClass -> screenModel.onClassSelected(selectedClass) },
                            classrooms = state.classrooms,
                            colors = state.colors
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PeriodModeToggle(
                            currentMode = state.periodMode,
                            onModeChange = { screenModel.onPeriodModeChange(it) },
                            colors = state.colors
                        )

                        SpecificPeriodSelector(
                            currentPeriod = state.currentPeriod,
                            onPeriodChange = { screenModel.onCurrentPeriodChange(it) },
                            periodMode = state.periodMode,
                            colors = state.colors
                        )
                    }
                }

                // Content Area (Desktop)
                Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    AnimatedContent(
                        targetState = state.viewMode,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        modifier = Modifier.fillMaxSize()
                    ) { mode ->
                        when (mode) {
                            GradesViewMode.NOTES -> GradesListView(
                                screenModel = screenModel,
                                isCompact = false,
                                onAddGrade = { screenModel.onViewModeChange(GradesViewMode.GRADE_FORM) },
                                onOpenConfig = { screenModel.onViewModeChange(GradesViewMode.CONFIG) }
                            )
                            GradesViewMode.BULLETINS -> BulletinsListView(
                                screenModel = screenModel, 
                                isCompact = false, 
                                onSelectBulletin = { id -> screenModel.onSelectBulletin(id) },
                                onBackFromPreview = { screenModel.clearSelectedReportCard() },
                                onExportPdf = { screenModel.exportReportCardToPdf() },
                                onGenerateAll = { screenModel.exportAllBulletinsToPdf() }
                            )
                            GradesViewMode.ARCHIVES -> ArchivesView(screenModel, false)
                            GradesViewMode.GRADE_FORM -> GradeEntryForm(
                                state = state,
                                students = state.currentClassStudents,
                                isCompact = false,
                                onBack = { screenModel.onViewModeChange(GradesViewMode.NOTES) },
                                onSave = { template, date, grades -> screenModel.onSaveSession(template, date, grades) },
                                onClassSelected = { screenModel.onClassSelected(it) },
                                onScrollToEnd = { screenModel.loadMoreStudents() }
                            )
                            GradesViewMode.CONFIG -> EvaluationConfigView(
                                state = state,
                                onBack = { screenModel.onViewModeChange(GradesViewMode.NOTES) },
                                onTemplatesUpdated = { updated: List<EvaluationTemplate> -> screenModel.onTemplateUpdated(updated) }
                            )
                        }
                    }
                }
            }
        }
    }
}
