package com.ecolix.data.services

import com.ecolix.domain.services.BulletinGenerationQueue
import com.ecolix.domain.services.GenerationProgress
import com.ecolix.domain.services.GenerationError
import com.ecolix.domain.services.PdfExportService
import com.ecolix.data.models.ReportCard
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin

/**
 * Implémentation de la file de génération de bulletins
 * Génère les bulletins de manière asynchrone avec gestion de la concurrence
 */
class BulletinGenerationQueueImpl(
    private val pdfService: PdfExportService,
    private val getReportCard: suspend (String) -> ReportCard
) : BulletinGenerationQueue {
    
    private val _progressFlow = MutableStateFlow(GenerationProgress.empty())
    private var currentJob: Job? = null
    
    override suspend fun queueBulletin(bulletinId: String) {
        queueBulletins(listOf(bulletinId))
    }
    
    override suspend fun queueBulletins(bulletinIds: List<String>) = coroutineScope {
        // Annuler toute génération en cours
        currentJob?.cancelAndJoin()
        
        // Initialiser la progression
        _progressFlow.value = GenerationProgress(
            total = bulletinIds.size,
            completed = 0,
            failed = 0,
            currentBulletinId = null,
            errors = emptyList(),
            isComplete = false
        )
        
        // Créer un nouveau job pour la génération
        currentJob = launch {
            bulletinIds.forEach { bulletinId ->
                try {
                    // Récupérer les données du bulletin
                    val reportCard = getReportCard(bulletinId)
                    
                    // Mettre à jour le bulletin en cours
                    _progressFlow.update { 
                        it.copy(
                            currentBulletinId = bulletinId,
                            currentStudentName = reportCard.studentName
                        )
                    }
                    
                    // Générer le PDF
                    pdfService.generateReportCardPdf(reportCard)
                    
                    // Marquer comme complété
                    _progressFlow.update { 
                        it.copy(
                            completed = it.completed + 1,
                            currentBulletinId = null,
                            currentStudentName = null
                        )
                    }
                } catch (e: Exception) {
                    // Enregistrer l'erreur
                    val studentName = try {
                        getReportCard(bulletinId).studentName
                    } catch (_: Exception) {
                        "Inconnu"
                    }
                    
                    _progressFlow.update {
                        it.copy(
                            failed = it.failed + 1,
                            errors = it.errors + GenerationError(
                                bulletinId = bulletinId,
                                studentName = studentName,
                                error = e.message ?: "Erreur inconnue"
                            ),
                            currentBulletinId = null,
                            currentStudentName = null
                        )
                    }
                }
            }
            
            // Marquer comme terminé
            _progressFlow.update { it.copy(isComplete = true) }
        }
    }
    
    override fun observeProgress(): Flow<GenerationProgress> = _progressFlow.asStateFlow()
    
    override suspend fun cancelAll() {
        currentJob?.cancelAndJoin()
        _progressFlow.value = GenerationProgress.empty()
    }
    
    override fun isGenerating(): Boolean {
        val progress = _progressFlow.value
        return progress.total > 0 && !progress.isComplete
    }
}
