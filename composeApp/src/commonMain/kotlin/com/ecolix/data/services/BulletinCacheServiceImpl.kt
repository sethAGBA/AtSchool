package com.ecolix.data.services

import com.ecolix.domain.services.BulletinCacheService
import com.ecolix.domain.services.CacheStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Implémentation du service de cache pour les bulletins
 * Utilise une stratégie LRU (Least Recently Used) pour l'éviction
 */
class BulletinCacheServiceImpl(
    private val maxBulletinCacheSize: Int = 100,
    private val maxTemplateCacheSize: Int = 10,
    private val bulletinTtlMinutes: Long = 60,
    private val templateTtlMinutes: Long = 1440 // 24h
) : BulletinCacheService {
    
    private val bulletinCache = LruCache<String, CacheEntry>(maxBulletinCacheSize)
    private val templateCache = LruCache<String, CacheEntry>(maxTemplateCacheSize)
    
    private val mutex = Mutex()
    private var totalHits = 0L
    private var totalMisses = 0L
    private var totalEvictions = 0L
    
    private val _statsFlow = MutableStateFlow(CacheStats.empty())
    
    override suspend fun getCachedBulletin(bulletinId: String): String? = mutex.withLock {
        val entry = bulletinCache.get(bulletinId)
        
        if (entry != null && !entry.isExpired(bulletinTtlMinutes)) {
            totalHits++
            updateStats()
            return entry.content
        }
        
        // Supprimer si expiré
        if (entry != null) {
            bulletinCache.remove(bulletinId)
        }
        
        totalMisses++
        updateStats()
        return null
    }
    
    override suspend fun cacheBulletin(bulletinId: String, html: String) = mutex.withLock {
        val evicted = bulletinCache.put(bulletinId, CacheEntry(html, System.currentTimeMillis()))
        if (evicted != null) {
            totalEvictions++
            updateStats()
        }
    }
    
    override suspend fun invalidateBulletin(bulletinId: String) = mutex.withLock {
        bulletinCache.remove(bulletinId)
        updateStats()
    }
    
    override suspend fun invalidateAll() = mutex.withLock {
        bulletinCache.clear()
        templateCache.clear()
        updateStats()
    }
    
    override fun getCachedTemplate(templateKey: String): String? {
        val entry = templateCache.get(templateKey)
        
        if (entry != null && !entry.isExpired(templateTtlMinutes)) {
            totalHits++
            updateStats()
            return entry.content
        }
        
        // Supprimer si expiré
        if (entry != null) {
            templateCache.remove(templateKey)
        }
        
        totalMisses++
        updateStats()
        return null
    }
    
    override fun cacheTemplate(templateKey: String, template: String) {
        val evicted = templateCache.put(templateKey, CacheEntry(template, System.currentTimeMillis()))
        if (evicted != null) {
            totalEvictions++
            updateStats()
        }
    }
    
    override fun getCacheStats(): CacheStats = _statsFlow.value
    
    override fun observeCacheStats(): Flow<CacheStats> = _statsFlow.asStateFlow()
    
    private fun updateStats() {
        val total = totalHits + totalMisses
        val hitRate = if (total > 0) totalHits.toFloat() / total else 0f
        val missRate = if (total > 0) totalMisses.toFloat() / total else 0f
        
        _statsFlow.value = CacheStats(
            bulletinCacheSize = bulletinCache.size(),
            templateCacheSize = templateCache.size(),
            totalHits = totalHits,
            totalMisses = totalMisses,
            hitRate = hitRate,
            missRate = missRate,
            evictions = totalEvictions
        )
    }
}

/**
 * Entrée de cache avec timestamp pour TTL
 */
private data class CacheEntry(
    val content: String,
    val timestamp: Long
) {
    fun isExpired(ttlMinutes: Long): Boolean {
        val now = System.currentTimeMillis()
        val ageMinutes = (now - timestamp) / (1000 * 60)
        return ageMinutes > ttlMinutes
    }
}

/**
 * Implémentation simple d'un cache LRU thread-safe
 */
private class LruCache<K, V>(private val maxSize: Int) {
    private val cache = LinkedHashMap<K, V>(maxSize, 0.75f, true)
    
    fun get(key: K): V? = synchronized(this) {
        cache[key]
    }
    
    fun put(key: K, value: V): V? = synchronized(this) {
        var evicted: V? = null
        
        if (cache.size >= maxSize && !cache.containsKey(key)) {
            val eldest = cache.entries.first()
            evicted = cache.remove(eldest.key)
        }
        
        cache[key] = value
        evicted
    }
    
    fun remove(key: K): V? = synchronized(this) {
        cache.remove(key)
    }
    
    fun clear() = synchronized(this) {
        cache.clear()
    }
    
    fun size(): Int = synchronized(this) {
        cache.size
    }
}
