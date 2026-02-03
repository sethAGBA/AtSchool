package com.ecolix.atschool.api

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object AuthEvents {
    private val _unauthorizedEvents = MutableSharedFlow<Unit>()
    val unauthorizedEvents = _unauthorizedEvents.asSharedFlow()

    suspend fun onUnauthorized() {
        TokenProvider.token = null
        _unauthorizedEvents.emit(Unit)
    }
}
