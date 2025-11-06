package com.example.windowauthandreg.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.windowauthandreg.data.entities.UserEntity
import com.example.windowauthandreg.domain.repository.AuthRepository
import com.example.windowauthandreg.domain.repository.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    private val _validationErrors = MutableStateFlow<Map<String, String>>(emptyMap())
    val validationErrors: StateFlow<Map<String, String>> = _validationErrors.asStateFlow()

    init {
        checkCurrentUser()
    }

    fun register(
        username: String,
        email: String,
        password: String,
        confirmPassword: String,
        firstName: String,
        lastName: String,
        phoneNumber: String?
    ) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            val errors = validateRegistration(
                username, email, password, confirmPassword, firstName, lastName
            )

            if (errors.isNotEmpty()) {
                _validationErrors.value = errors
                _authState.value = AuthState.Error("Пожалуйста, исправьте ошибки валидации")
                return@launch
            }

            when (val result = authRepository.register(
                username, email, password, firstName, lastName, phoneNumber
            )) {
                is AuthResult.Success -> {
                    _authState.value = AuthState.RegistrationSuccess
                    _validationErrors.value = emptyMap()
                }
                is AuthResult.Error -> {
                    _authState.value = AuthState.Error(result.message)
                }
            }
        }
    }

    fun login(identifier: String, password: String, isRememberMe: Boolean = false) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            val errors = validateLogin(identifier, password)
            if (errors.isNotEmpty()) {
                _validationErrors.value = errors
                _authState.value = AuthState.Error("Пожалуйста, исправьте ошибки валидации")
                return@launch
            }

            when (val result = authRepository.login(identifier, password, isRememberMe)) {
                is AuthResult.Success -> {
                    _authState.value = AuthState.LoginSuccess
                    _validationErrors.value = emptyMap()
                    loadCurrentUser()
                }
                is AuthResult.Error -> {
                    _authState.value = AuthState.Error(result.message)
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _authState.value = AuthState.LoggedOut
            _currentUser.value = null
        }
    }

    private fun checkCurrentUser() {
        viewModelScope.launch {
            if (authRepository.isUserLoggedIn()) {
                loadCurrentUser()
            }
        }
    }

    private suspend fun loadCurrentUser() {
        _currentUser.value = authRepository.getCurrentUser()
    }

    private fun validateRegistration(
        username: String,
        email: String,
        password: String,
        confirmPassword: String,
        firstName: String,
        lastName: String
    ): Map<String, String> {
        val errors = mutableMapOf<String, String>()

        if (username.length < 3) {
            errors["username"] = "Имя пользователя должно содержать минимум 3 символа"
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            errors["email"] = "Введите корректный email адрес"
        }

        if (password.length < 8) {
            errors["password"] = "Пароль должен содержать минимум 8 символов"
        }

        if (password != confirmPassword) {
            errors["confirmPassword"] = "Пароли не совпадают"
        }

        if (firstName.isBlank()) {
            errors["firstName"] = "Введите имя"
        }

        if (lastName.isBlank()) {
            errors["lastName"] = "Введите фамилию"
        }

        return errors
    }

    private fun validateLogin(identifier: String, password: String): Map<String, String> {
        val errors = mutableMapOf<String, String>()

        if (identifier.isBlank()) {
            errors["identifier"] = "Введите email или имя пользователя"
        }

        if (password.isBlank()) {
            errors["password"] = "Введите пароль"
        }

        return errors
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object RegistrationSuccess : AuthState()
    object LoginSuccess : AuthState()
    object LoggedOut : AuthState()
    data class Error(val message: String) : AuthState()
}

