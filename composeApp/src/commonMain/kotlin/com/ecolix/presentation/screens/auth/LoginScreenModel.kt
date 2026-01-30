package com.ecolix.presentation.screens.auth

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.ecolix.atschool.api.AuthApiService
import com.ecolix.atschool.api.LoginRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    object Success : LoginState()
    data class Error(val message: String) : LoginState()
}

class LoginScreenModel(private val authService: AuthApiService) : ScreenModel {
    private val _state = MutableStateFlow<LoginState>(LoginState.Idle)
    val state = _state.asStateFlow()

    fun login(email: String, password: String, schoolCode: String, rememberMe: Boolean = false) {
        screenModelScope.launch {
            _state.value = LoginState.Loading
            val result = authService.login(LoginRequest(email, password, schoolCode), rememberMe)
            result.onSuccess {
                _state.value = LoginState.Success
            }.onFailure {
                it.printStackTrace() // Log the full error to console
                _state.value = LoginState.Error(it.message ?: "Authentication failed")
            }
        }
    }

    fun reset() {
        _state.value = LoginState.Idle
    }
}
