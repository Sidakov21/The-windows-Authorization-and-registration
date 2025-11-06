package com.example.windowauthandreg.domain.repository

import com.example.windowauthandreg.data.dao.SessionDao
import com.example.windowauthandreg.data.dao.UserDao
import com.example.windowauthandreg.data.entities.UserEntity
import com.example.windowauthandreg.domain.utils.PasswordManager
import com.example.windowauthandreg.domain.utils.PasswordStrength
import com.example.windowauthandreg.domain.utils.PreferencesManager
import com.example.windowauthandreg.domain.utils.SessionManager
import java.util.Date
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val sessionDao: SessionDao,
    private val passwordManager: PasswordManager,
    private val sessionManager: SessionManager,
    private val preferencesManager: PreferencesManager
) : AuthRepository {

    override suspend fun register(
        username: String,
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        phoneNumber: String?
    ): AuthResult {
        return try {
            if (userDao.checkEmailExists(email) > 0) {
                return AuthResult.Error("Email уже зарегистрирован")
            }

            if (userDao.checkUsernameExists(username) > 0) {
                return AuthResult.Error("Имя пользователя уже занято")
            }

            val passwordStrength = passwordManager.isPasswordStrong(password)
            if (passwordStrength == PasswordStrength.WEAK) {
                return AuthResult.Error("Пароль слишком слабый")
            }

            val salt = passwordManager.generateSalt()
            val passwordHash = passwordManager.hashPassword(password, salt)

            val user = UserEntity(
                username = username,
                email = email,
                passwordHash = passwordHash,
                salt = salt,
                firstName = firstName,
                lastName = lastName,
                phoneNumber = phoneNumber,
                avatarUrl = null,
                lastLogin = null,
                createdAt = Date(),
                updatedAt = Date()
            )

            val userId = userDao.insertUser(user)
            AuthResult.Success(userId)

        } catch (e: Exception) {
            AuthResult.Error("Ошибка регистрации: ${e.message}")
        }
    }

    override suspend fun login(
        identifier: String,
        password: String,
        isRememberMe: Boolean
    ): AuthResult {
        return try {
            val user = userDao.getUserByEmail(identifier)
                ?: userDao.getUserByUsername(identifier)
                ?: return AuthResult.Error("Пользователь не найден")

            if (!passwordManager.verifyPassword(password, user.salt, user.passwordHash)) {
                return AuthResult.Error("Неверный пароль")
            }

            if (!user.isActive) {
                return AuthResult.Error("Аккаунт деактивирован")
            }

            userDao.updateLastLogin(user.id, Date())

            val session = sessionManager.createSession(user.id, isRememberMe)

            preferencesManager.saveSessionToken(session.sessionToken)
            preferencesManager.saveUserId(user.id)

            AuthResult.Success(user.id)

        } catch (e: Exception) {
            AuthResult.Error("Ошибка входа: ${e.message}")
        }
    }

    override suspend fun logout() {
        try {
            val token = preferencesManager.getSessionToken()
            token?.let { sessionDao.deleteSession(it) }
            preferencesManager.clearSession() // без suspend
        } catch (e: Exception) {
        }
    }

    override suspend fun changePassword(
        userId: Long,
        currentPassword: String,
        newPassword: String
    ): AuthResult {
        return try {
            val user = userDao.getUserById(userId) ?: return AuthResult.Error("Пользователь не найден")

            if (!passwordManager.verifyPassword(currentPassword, user.salt, user.passwordHash)) {
                return AuthResult.Error("Неверный текущий пароль")
            }

            val passwordStrength = passwordManager.isPasswordStrong(newPassword)
            if (passwordStrength == PasswordStrength.WEAK) {
                return AuthResult.Error("Новый пароль слишком слабый")
            }

            val newSalt = passwordManager.generateSalt()
            val newPasswordHash = passwordManager.hashPassword(newPassword, newSalt)

            userDao.updatePassword(userId, newPasswordHash, newSalt)

            AuthResult.Success(userId)

        } catch (e: Exception) {
            AuthResult.Error("Ошибка смены пароля: ${e.message}")
        }
    }

    override suspend fun isUserLoggedIn(): Boolean {
        return try {
            val token = preferencesManager.getSessionToken()
            token != null && sessionManager.validateSession(token)
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun getCurrentUser(): UserEntity? {
        return try {
            val token = preferencesManager.getSessionToken()
            val userId = token?.let { sessionManager.getCurrentUser(it) }
            userId?.let { userDao.getUserById(it) }
        } catch (e: Exception) {
            null
        }
    }
}

