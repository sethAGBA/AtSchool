package com.ecolix.data.services

import com.ecolix.domain.services.DataCacheService
import com.ecolix.domain.services.CachePolicy
import com.ecolix.domain.services.CacheStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Implémentation générique du cache de données
 * Thread-safe avec LRU eviction et TTL
 */
open class DataCacheServiceImpl<T>(
    private val policy: CachePolicy
) : DataCacheService<T> {
    
    private val cache = mutableMapOf<String, CacheEntry<T>>()
    private val accessOrder = mutableListOf<String>()
    private val mutex = Mutex()
    
    private var totalHits = 0L
    private var totalMisses = 0L
    private var totalEvictions = 0L
    
    private val _statsFlow = MutableStateFlow(CacheStats.empty())
    
    override suspend fun get(key: String): T? = mutex.withLock {
        val entry = cache[key]
        
        if (entry != null && !entry.isExpired(policy.ttlMinutes)) {
            // Update access order for LRU
            accessOrder.remove(key)
            accessOrder.add(key)
            
            totalHits++
            updateStats()
            return entry.value
        }
        
        // Supprimer si expiré
        if (entry != null) {
            cache.remove(key)
            accessOrder.remove(key)
        }
        
        totalMisses++
        updateStats()
        return null
    }
    
    override suspend fun put(key: String, value: T) = mutex.withLock {
        // Éviction LRU si nécessaire
        if (cache.size >= policy.maxSize && !cache.containsKey(key)) {
            val eldest = accessOrder.firstOrNull()
            if (eldest != null) {
                cache.remove(eldest)
                accessOrder.remove(eldest)
                totalEvictions++
            }
        }
        
        cache[key] = CacheEntry(value, System.currentTimeMillis())
        accessOrder.remove(key)
        accessOrder.add(key)
        updateStats()
    }
    
    override suspend fun invalidate(key: String) = mutex.withLock {
        cache.remove(key)
        accessOrder.remove(key)
        updateStats()
    }
    
    override suspend fun invalidateAll() = mutex.withLock {
        cache.clear()
        accessOrder.clear()
        updateStats()
    }
    
    override suspend fun invalidateWhere(predicate: (String) -> Boolean) = mutex.withLock {
        val keysToRemove = cache.keys.filter(predicate)
        keysToRemove.forEach { 
            cache.remove(it)
            accessOrder.remove(it)
        }
        updateStats()
    }
    
    override fun getStats(): CacheStats = _statsFlow.value
    
    override fun observeStats(): Flow<CacheStats> = _statsFlow.asStateFlow()
    
    private fun updateStats() {
        val total = totalHits + totalMisses
        val hitRate = if (total > 0) totalHits.toFloat() / total else 0f
        val missRate = if (total > 0) totalMisses.toFloat() / total else 0f
        
        _statsFlow.value = CacheStats(
            bulletinCacheSize = cache.size,
            templateCacheSize = 0, // N/A pour cache générique
            totalHits = totalHits,
            totalMisses = totalMisses,
            hitRate = hitRate,
            missRate = missRate,
            evictions = totalEvictions
        )
    }
    
    /**
     * Entrée de cache avec timestamp pour TTL
     */
    private data class CacheEntry<T>(
        val value: T,
        val timestamp: Long
    ) {
        fun isExpired(ttlMinutes: Long): Boolean {
            val now = System.currentTimeMillis()
            val ageMinutes = (now - timestamp) / (1000 * 60)
            return ageMinutes > ttlMinutes
        }
    }
}
