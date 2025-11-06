package com.example.windowauthandreg.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() { // Убрали @HiltViewModel и зависимости

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _validationErrors = MutableStateFlow<Map<String, String>>(emptyMap())
    val validationErrors: StateFlow<Map<String, String>> = _validationErrors.asStateFlow()

    fun login(identifier: String, password: String, isRememberMe: Boolean = false) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            // Имитация задержки сети
            kotlinx.coroutines.delay(1500)

            // Простая логика для демонстрации
            if (identifier.isNotBlank() && password == "12345678") {
                _authState.value = AuthState.LoginSuccess
            } else {
                _authState.value = AuthState.Error("Неверные учетные данные")
            }
        }
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

            // Имитация задержки сети
            kotlinx.coroutines.delay(1500)

            val errors = validateRegistration(
                username, email, password, confirmPassword, firstName, lastName
            )

            if (errors.isNotEmpty()) {
                _validationErrors.value = errors
                _authState.value = AuthState.Error("Пожалуйста, исправьте ошибки валидации")
                return@launch
            }

            _authState.value = AuthState.RegistrationSuccess
            _validationErrors.value = emptyMap()
        }
    }

    fun logout() {
        _authState.value = AuthState.LoggedOut
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
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object RegistrationSuccess : AuthState()
    object LoginSuccess : AuthState()
    object LoggedOut : AuthState()
    data class Error(val message: String) : AuthState()
}