package com.ecolix.domain.services

import kotlinx.coroutines.flow.Flow

/**
 * Service de cache pour les bulletins générés et les templates HTML
 * Améliore les performances en évitant la régénération de bulletins identiques
 */
interface BulletinCacheService {
    /**
     * Récupère un bulletin depuis le cache
     * @param bulletinId Identifiant unique du bulletin
     * @return HTML du bulletin si présent en cache, null sinon
     */
    suspend fun getCachedBulletin(bulletinId: String): String?
    
    /**
     * Met en cache un bulletin généré
     * @param bulletinId Identifiant unique du bulletin
     * @param html Contenu HTML du bulletin
     */
    suspend fun cacheBulletin(bulletinId: String, html: String)
    
    /**
     * Invalide un bulletin spécifique du cache
     * @param bulletinId Identifiant du bulletin à invalider
     */
    suspend fun invalidateBulletin(bulletinId: String)
    
    /**
     * Invalide tous les bulletins du cache
     */
    suspend fun invalidateAll()
    
    /**
     * Récupère un template depuis le cache
     * @param templateKey Clé unique du template
     * @return Template HTML si présent en cache, null sinon
     */
    fun getCachedTemplate(templateKey: String): String?
    
    /**
     * Met en cache un template
     * @param templateKey Clé unique du template
     * @param template Contenu du template
     */
    fun cacheTemplate(templateKey: String, template: String)
    
    /**
     * Récupère les statistiques du cache
     * @return Statistiques actuelles du cache
     */
    fun getCacheStats(): CacheStats
    
    /**
     * Observe les changements des statistiques du cache
     */
    fun observeCacheStats(): Flow<CacheStats>
}

/**
 * Statistiques du cache
 */
data class CacheStats(
    val bulletinCacheSize: Int = 0,
    val templateCacheSize: Int = 0,
    val totalHits: Long = 0,
    val totalMisses: Long = 0,
    val hitRate: Float = 0f,
    val missRate: Float = 0f,
    val evictions: Long = 0
) {
    companion object {
        fun empty() = CacheStats()
    }
}
