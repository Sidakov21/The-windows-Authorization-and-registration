package com.example.windowauthandreg.domain.repository

import com.example.windowauthandreg.data.entities.UserEntity
import com.example.windowauthandreg.presentation.activities.App
import com.google.android.gms.auth.api.Auth
import javax.inject.Inject

interface AuthRepository {
    suspend fun register(
        username: String,
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        phoneNumber: String?
    ): AuthResult

    suspend fun login(
        identifier: String,
        password: String,
        isRememberMe: Boolean
    ): AuthResult

    suspend fun logout()
    suspend fun changePassword(
        userId: Long,
        currentPassword: String,
        newPassword: String
    ): AuthResult

    suspend fun isUserLoggedIn(): Boolean
    suspend fun getCurrentUser(): UserEntity?
}

sealed class AuthResult {
    data class Success(val userId: Long) : AuthResult()
    data class Error(val message: String) : AuthResult()
}



