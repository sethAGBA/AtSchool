package com.ecolix.domain.services

import kotlinx.coroutines.flow.Flow

/**
 * Service de cache générique pour les données de l'application
 * Supporte LRU eviction, TTL, et invalidation sélective
 */
interface DataCacheService<T> {
    /**
     * Récupère une valeur depuis le cache
     * @param key Clé unique de la donnée
     * @return Valeur si présente et non expirée, null sinon
     */
    suspend fun get(key: String): T?
    
    /**
     * Met en cache une valeur
     * @param key Clé unique de la donnée
     * @param value Valeur à mettre en cache
     */
    suspend fun put(key: String, value: T)
    
    /**
     * Invalide une entrée spécifique du cache
     * @param key Clé de l'entrée à invalider
     */
    suspend fun invalidate(key: String)
    
    /**
     * Invalide toutes les entrées du cache
     */
    suspend fun invalidateAll()
    
    /**
     * Invalide les entrées correspondant à un prédicat
     * @param predicate Fonction de test pour chaque clé
     */
    suspend fun invalidateWhere(predicate: (String) -> Boolean)
    
    /**
     * Récupère les statistiques du cache
     */
    fun getStats(): CacheStats
    
    /**
     * Observe les changements des statistiques
     */
    fun observeStats(): Flow<CacheStats>
}

/**
 * Politique de cache définissant le comportement
 */
data class CachePolicy(
    val maxSize: Int = 100,
    val ttlMinutes: Long = 10,
    val name: String = "GenericCache"
)

/**
 * Statistiques du cache (réutilise CacheStats existant)
 */
// CacheStats déjà défini dans BulletinCacheService.kt
