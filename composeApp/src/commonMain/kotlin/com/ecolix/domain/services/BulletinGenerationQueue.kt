package com.ecolix.domain.services

import com.ecolix.data.models.ReportCard
import kotlinx.coroutines.flow.Flow

/**
 * Service de file d'attente pour la génération de bulletins en masse
 * Permet de générer plusieurs bulletins de manière asynchrone avec suivi de progression
 */
interface BulletinGenerationQueue {
    /**
     * Ajoute un bulletin à la file de génération
     * @param bulletinId Identifiant du bulletin à générer
     */
    suspend fun queueBulletin(bulletinId: String)
    
    /**
     * Ajoute plusieurs bulletins à la file de génération
     * @param bulletinIds Liste des identifiants de bulletins
     */
    suspend fun queueBulletins(bulletinIds: List<String>)
    
    /**
     * Observe la progression de la génération
     * @return Flow émettant les mises à jour de progression
     */
    fun observeProgress(): Flow<GenerationProgress>
    
    /**
     * Annule toutes les générations en cours
     */
    suspend fun cancelAll()
    
    /**
     * Vérifie si une génération est en cours
     */
    fun isGenerating(): Boolean
}

/**
 * État de progression de la génération
 */
data class GenerationProgress(
    val total: Int = 0,
    val completed: Int = 0,
    val failed: Int = 0,
    val currentBulletinId: String? = null,
    val currentStudentName: String? = null,
    val errors: List<GenerationError> = emptyList(),
    val isComplete: Boolean = false
) {
    val progress: Float
        get() = if (total > 0) (completed + failed).toFloat() / total else 0f
    
    val successRate: Float
        get() = if (total > 0) completed.toFloat() / total else 0f
    
    companion object {
        fun empty() = GenerationProgress()
    }
}

/**
 * Erreur de génération
 */
data class GenerationError(
    val bulletinId: String,
    val studentName: String,
    val error: String,
    val timestamp: Long = System.currentTimeMillis()
)
