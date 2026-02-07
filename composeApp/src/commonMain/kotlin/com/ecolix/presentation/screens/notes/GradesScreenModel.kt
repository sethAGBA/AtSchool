package com.ecolix.presentation.screens.notes

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.ecolix.data.models.*
import com.ecolix.domain.services.PdfExportService
import com.ecolix.domain.services.BulletinGenerationQueue
import com.ecolix.domain.services.GenerationProgress
import com.ecolix.data.services.PdfExportServiceImpl
import com.ecolix.data.services.SettingsDataCache
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GradesScreenModel(
    private val pdfExportService: PdfExportService = PdfExportServiceImpl(),
    private val generationQueue: BulletinGenerationQueue? = null,
    private val settingsCache: SettingsDataCache? = null
) : StateScreenModel<GradesUiState>(GradesUiState.sample(false)) {
    
    // Observe generation progress
    val generationProgress: StateFlow<GenerationProgress> = generationQueue?.observeProgress()
        ?.stateIn(screenModelScope, SharingStarted.Lazily, GenerationProgress.empty())
        ?: kotlinx.coroutines.flow.MutableStateFlow(GenerationProgress.empty())

    init {
        if (settingsCache != null) {
            screenModelScope.launch {
                val settings = settingsCache.get(SettingsDataCache.KEY_SETTINGS)
                if (settings != null) {
                    mutableState.update { it.copy(schoolInfo = settings) }
                }
            }
        }
    }


    fun onViewModeChange(mode: GradesViewMode) {
        mutableState.update { it.copy(viewMode = mode) }
    }

    fun loadMoreStudents() {
        mutableState.update { 
            val newCount = it.studentsLoadedCount + it.studentsBatchSize
            it.copy(studentsLoadedCount = minOf(newCount, it.currentClassStudents.size))
        }
    }

    fun onPageChange(tab: GradesViewMode, newPage: Int) {
        mutableState.update { 
            when (tab) {
                GradesViewMode.NOTES -> it.copy(notesPage = newPage)
                GradesViewMode.BULLETINS -> it.copy(bulletinsPage = newPage)
                GradesViewMode.ARCHIVES -> it.copy(archivesPage = newPage)
                else -> it
            }
        }
    }

    fun onDarkModeChange(isDark: Boolean) {
        mutableState.update { it.copy(isDarkMode = isDark) }
    }

    fun onClassSelected(className: String) {
        // In a real app, we would fetch students from a repository here.
        // For now, we'll filter from the sample data in StudentsUiState
        val allStudents = StudentsUiState.sample(false).students
        val classStudents = allStudents.filter { it.classroom == className }
        
        mutableState.update { 
            it.copy(
                selectedClassroom = className,
                currentClassStudents = classStudents,
                notesPage = 0,
                bulletinsPage = 0,
                archivesPage = 0,
                studentsLoadedCount = it.studentsBatchSize
            ) 
        }
    }
    
    fun onTemplateUpdated(updatedTemplates: List<EvaluationTemplate>) {
        mutableState.update { it.copy(templates = updatedTemplates) }
    }
    
    fun onSearchQueryChange(query: String) {
        mutableState.update { 
            it.copy(
                searchQuery = query,
                notesPage = 0,
                bulletinsPage = 0,
                archivesPage = 0
            ) 
        }
    }
    
    fun onPeriodModeChange(mode: PeriodMode) {
        val newPeriod = if (mode == PeriodMode.TRIMESTRE) "1er Trimestre" else "1er Semestre"
        mutableState.update { it.copy(periodMode = mode, currentPeriod = newPeriod) }
    }
    
    fun onCurrentPeriodChange(period: String) {
        mutableState.update { it.copy(currentPeriod = period) }
    }

    fun onSaveSession(
        template: EvaluationTemplate,
        date: String,
        grades: Map<String, String>
    ) {
        val gradeValues = grades.values.mapNotNull { it.toFloatOrNull() }
        val average = if (gradeValues.isNotEmpty()) gradeValues.average().toFloat() else 0f
        val successCount = gradeValues.count { it >= (template.maxValue / 2) }
        val successRate = if (gradeValues.isNotEmpty()) (successCount * 100 / gradeValues.size) else 0

        val session = EvaluationSession(
            id = "S_${kotlin.random.Random.nextInt(1000)}",
            title = template.label,
            type = template.type,
            subject = "Mathématiques", // TODO: Use selected subject from state
            classroom = template.className,
            date = date,
            period = mutableState.value.currentPeriod,
            average = average,
            successRate = successRate,
            maxGrade = template.maxValue,
            coefficient = template.coefficient,
            gradeCount = grades.size
        )

        mutableState.update { 
            it.copy(
                sessions = it.sessions + session,
                viewMode = GradesViewMode.NOTES
            )
        }
    }
    
    private fun getAppreciation(grade: Float): String {
        return when {
            grade >= 18 -> "Excellent"
            grade >= 16 -> "Très Bien"
            grade >= 14 -> "Bien"
            grade >= 12 -> "Assez Bien"
            grade >= 10 -> "Passable"
            else -> "Insuffisant"
        }
    }

    private fun getGeneralAppreciation(average: Float): String {
        return getAppreciation(average)
    }

    fun onSelectBulletin(bulletinId: String, isDuplicate: Boolean = false) {
        val mockReport = createReportCardForBulletin(bulletinId, isDuplicate)
        if (mockReport != null) {
            mutableState.update { it.copy(selectedReportCard = mockReport) }
        }
    }

    // Helper to get filtered lists for the UI
    fun getFilteredSessions(): List<EvaluationSession> {
        val state = mutableState.value
        return state.sessions.filter { session ->
            val matchesClass = state.selectedClassroom == "Toutes les classes" || session.classroom == state.selectedClassroom
            val matchesPeriod = session.period == state.currentPeriod
            val matchesSearch = state.searchQuery.isEmpty() || 
                               session.title.contains(state.searchQuery, ignoreCase = true) ||
                               session.subject.contains(state.searchQuery, ignoreCase = true)
            matchesClass && matchesPeriod && matchesSearch
        }
    }

    fun getFilteredEvaluations(): List<GradeEvaluation> {
        val state = mutableState.value
        return state.evaluations.filter { evaluation ->
            // Individual evaluations might not have classroom directly, but we can filter by student if needed
            // For now, let's filter by subject and period
            val matchesPeriod = evaluation.period == state.currentPeriod
            val matchesSubject = state.selectedSubject == "Toutes les matières" || evaluation.subject == state.selectedSubject
            val matchesSearch = state.searchQuery.isEmpty() || 
                               evaluation.studentName.contains(state.searchQuery, ignoreCase = true) ||
                               evaluation.subject.contains(state.searchQuery, ignoreCase = true)
            matchesPeriod && matchesSubject && matchesSearch
        }
    }

    fun getFilteredBulletins(): List<BulletinPreview> {
        val state = mutableState.value
        return state.bulletins.filter { bulletin ->
            val matchesClass = state.selectedClassroom == "Toutes les classes" || bulletin.classroom == state.selectedClassroom
            val matchesSearch = state.searchQuery.isEmpty() || 
                               bulletin.studentName.contains(state.searchQuery, ignoreCase = true)
            matchesClass && matchesSearch
        }
    }

    fun getPaginatedSessions(): List<EvaluationSession> {
        val filtered = getFilteredSessions()
        val fromIndex = mutableState.value.notesPage * mutableState.value.itemsPerPage
        if (fromIndex >= filtered.size) return emptyList()
        return filtered.subList(fromIndex, minOf(fromIndex + mutableState.value.itemsPerPage, filtered.size))
    }

    fun getPaginatedBulletins(): List<BulletinPreview> {
        val filtered = getFilteredBulletins()
        val fromIndex = mutableState.value.bulletinsPage * mutableState.value.itemsPerPage
        if (fromIndex >= filtered.size) return emptyList()
        return filtered.subList(fromIndex, minOf(fromIndex + mutableState.value.itemsPerPage, filtered.size))
    }

    fun getPaginatedArchives(): List<String> {
        val filtered = getFilteredArchives()
        val fromIndex = mutableState.value.archivesPage * mutableState.value.itemsPerPage
        if (fromIndex >= filtered.size) return emptyList()
        return filtered.subList(fromIndex, minOf(fromIndex + mutableState.value.itemsPerPage, filtered.size))
    }

    fun getFilteredArchives(): List<String> {
        val state = mutableState.value
        val allYears = listOf("2024-2025", "2023-2024", "2022-2023")
        return allYears.filter { year ->
            state.searchQuery.isEmpty() || year.contains(state.searchQuery)
        }
    }
    
    private fun createReportCardForBulletin(bulletinId: String, isDuplicate: Boolean = false): ReportCard? {
        // MOCK DATA GENERATION - Should be in Repository
        // In a real app, fetchReportCard(bulletinId)
        val bulletin = mutableState.value.bulletins.find { it.id == bulletinId } ?: return null
        
        val subjectsData = listOf(
            Triple("Français", "Mme. Dubois", "Matières Littéraires"),
            Triple("Anglais", "Mr. Smith", "Matières Littéraires"),
            Triple("Histoire-Géo", "M. Kouassi", "Matières Littéraires"),
            Triple("Allemand/Espagnol", "Mme. Muller", "Matières Littéraires"),
            Triple("Philosophie", "M. Diallo", "Matières Littéraires"),
            Triple("Mathématiques", "M. Koffi", "Matières Scientifiques"),
            Triple("Physique-Chimie", "M. Konan", "Matières Scientifiques"),
            Triple("SVT", "Mme. Yace", "Matières Scientifiques"),
            Triple("Informatique", "M. Trabi", "Matières Scientifiques"),
            Triple("ECM", "Mme. Kone", "Matières Générales"),
            Triple("EPS", "M. Zadi", "Autres"),
            Triple("Musique", "M. Loukou", "Autres"),
            Triple("Arts Plastiques", "Mme. Bamba", "Autres"),
            Triple("Technique", "M. Soro", "Matières Techniques"),
            Triple("Comptabilité", "Mme. Ouattara", "Matières Techniques")
        )

        return ReportCard(
            id = bulletin.id,
            studentName = bulletin.studentName,
            studentId = "ST_${bulletin.id}",
            matricule = "MAT-${202300 + bulletin.id.toInt()}",
            dateOfBirth = "12/04/2014 à Lomé",
            sex = "M",
            isRepeater = false,
            className = bulletin.classroom,
            period = mutableState.value.currentPeriod,
            academicYear = "2025-2026",
            subjects = subjectsData.mapIndexed { index, (name, prof, cat) ->
                val avg = 10f + index // Just randomizing slightly based on index
                ReportCardSubject(
                    name = name,
                    professor = prof,
                    evaluations = listOf(
                        EvaluationSummary("Devoir", avg + 0.5f),
                        EvaluationSummary("Composition", avg - 0.5f, 2f)
                    ),
                    average = avg,
                    coefficient = (4 - (index % 3)).toFloat(), 
                    total = avg * (4 - (index % 3)),
                    totalCoefficient = (4 - (index % 3)).toFloat(),
                    classAverage = 12f,
                    minAverage = 8f,
                    maxAverage = 18f,
                    rank = index + 1,
                    appreciation = getAppreciation(avg),
                    category = cat
                )
            },
            generalAverage = bulletin.average,
            annualAverage = if(bulletin.average > 14) 15.2f else null,
            rank = bulletin.rank,
            totalStudents = bulletin.totalStudents,
            classAverage = 12.5f,
            minAverage = 8.5f,
            maxAverage = 18.5f,
            appreciationGenerale = getGeneralAppreciation(bulletin.average),
            decision = if (bulletin.average >= 10) "Passe en classe supérieure (Admis)" else "Redouble",
            conduite = "Bonne",
            travail = if (bulletin.average >= 12) "Satisfaisant" else "Moyen",
            tableauHonneur = bulletin.average >= 12 && bulletin.average < 14,
            tableauEncouragement = bulletin.average >= 14 && bulletin.average < 16,
            tableauFelicitations = bulletin.average >= 16,
            historyAverages = listOf(14.5f, 15.2f, bulletin.average),
            teacherName = "M. KOUASSI",
            directorName = "M. LE DIRECTEUR",
            isDuplicate = isDuplicate,
            absInjustifiees = 2,
            absJustifiees = 0,
            retards = 1,
            pointsADevelopper = "NON",
            serie = "D",
            nb = "NB: Il n'est délivré qu'un seul Bulletin. Delai de reclamation : 30 jours date de reception.",
            schoolInfo = mutableState.value.schoolInfo,
            generatedDate = com.ecolix.presentation.utils.DateUtils.getCurrentDateFormatted()
        )
    }
    
    fun clearSelectedReportCard() {
        mutableState.update { it.copy(selectedReportCard = null) }
    }
    
    /**
     * Exporte le bulletin sélectionné en PDF
     * @param customDestination Chemin optionnel du dossier de destination. Si null, demande à l'utilisateur
     */
    /**
     * Exporte le bulletin sélectionné en PDF
     * @param customDestination Chemin optionnel du dossier de destination. Si null, demande à l'utilisateur
     */
    fun exportReportCardToPdf(customDestination: String? = null) {
        screenModelScope.launch {
            val reportCard = mutableState.value.selectedReportCard
            if (reportCard == null) {
                println("⚠️ No report card selected for export")
                return@launch
            }
            
            try {
                // Mettre à jour l'état pour indiquer l'export en cours
                mutableState.update { it.copy(isExporting = true) }
                
                // Demander à l'utilisateur de choisir le dossier de destination
                val destinationPath = customDestination ?: com.ecolix.utils.FolderPicker.selectFolder()
                
                if (destinationPath == null) {
                    println("ℹ️ PDF export cancelled by user")
                    mutableState.update { it.copy(isExporting = false) }
                    return@launch
                }
                
                // Générer le PDF
                val pdfData = pdfExportService.generateReportCardPdf(reportCard)
                
                // Exporter vers un fichier
                val fileName = "Bulletin_${reportCard.studentName.replace(" ", "_")}_${reportCard.period}"
                val result = pdfExportService.exportToFile(pdfData, fileName, destinationPath)
                
                result.onSuccess { filePath ->
                    println("✅ PDF exported successfully to: $filePath")
                }.onFailure { error ->
                    println("❌ PDF export failed: ${error.message}")
                }
            } catch (e: Exception) {
                println("❌ Unexpected error during PDF export: ${e.message}")
            } finally {
                mutableState.update { it.copy(isExporting = false) }
            }
        }
    }

    /**
     * Exporte tous les bulletins de la classe en PDF (en fonction des filtres actuels)
     * Utilise la file de génération asynchrone pour de meilleures performances
     */
    fun exportAllBulletinsToPdf() {
        screenModelScope.launch {
            val bulletins = getFilteredBulletins()
            if (bulletins.isEmpty()) {
                println("⚠️ No bulletins to export")
                return@launch
            }
            
            // Utiliser la file de génération si disponible
            if (generationQueue != null) {
                val bulletinIds = bulletins.map { it.id }
                generationQueue.queueBulletins(bulletinIds)
            } else {
                // Fallback vers l'ancienne méthode synchrone
                exportAllBulletinsSynchronously(bulletins)
            }
        }
    }
    
    /**
     * Méthode de fallback pour l'export synchrone (sans file d'attente)
     */
    private suspend fun exportAllBulletinsSynchronously(bulletins: List<BulletinPreview>) {
        try {
            // Demander à l'utilisateur de choisir le dossier de destination
            val destinationPath = com.ecolix.utils.FolderPicker.selectFolder()
            if (destinationPath == null) {
                println("ℹ️ Batch export cancelled by user")
                return
            }

            // Mettre à jour l'état pour indiquer l'export en cours
            mutableState.update { 
                it.copy(
                    isExporting = true, 
                    exportProgress = 0f,
                    batchExportCount = bulletins.size
                ) 
            }
            
            bulletins.forEachIndexed { index, bulletinPreview ->
                val reportCard = createReportCardForBulletin(bulletinPreview.id)
                if (reportCard != null) {
                    println("📄 Batch generating PDF for: ${reportCard.studentName} (${index + 1}/${bulletins.size})")
                    
                    // Générer le PDF
                    val pdfData = pdfExportService.generateReportCardPdf(reportCard)
                    
                    // Exporter vers un fichier
                    val fileName = "Bulletin_${reportCard.studentName.replace(" ", "_")}_${reportCard.period}"
                    pdfExportService.exportToFile(pdfData, fileName, destinationPath)
                }
                
                // Mettre à jour le progrès
                mutableState.update { it.copy(exportProgress = (index + 1).toFloat() / bulletins.size) }
            }
            
            println("✅ Batch export completed successfully")
            
        } catch (e: Exception) {
            println("❌ Unexpected error during batch export: ${e.message}")
        } finally {
            mutableState.update { it.copy(isExporting = false, exportProgress = 0f) }
        }
    }
    
    /**
     * Annule la génération en cours
     */
    fun cancelBulletinGeneration() {
        screenModelScope.launch {
            generationQueue?.cancelAll()
        }
    }



    fun updateState(newState: GradesUiState) {
        mutableState.value = newState
    }
}
