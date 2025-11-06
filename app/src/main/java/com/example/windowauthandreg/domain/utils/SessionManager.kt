package com.example.windowauthandreg.domain.utils

import android.provider.Settings
import com.example.windowauthandreg.data.dao.SessionDao
import com.example.windowauthandreg.data.entities.UserSessionEntity
import com.example.windowauthandreg.presentation.activities.App
import java.util.Date
import java.util.Calendar
import java.util.UUID

class SessionManager(
    private val sessionDao: SessionDao
) {
    fun generateSessionToken(): String {
        return UUID.randomUUID().toString().replace("-", "")
    }

    suspend fun createSession(
        userId: Long,
        isRememberMe: Boolean = false
    ): UserSessionEntity {
        val calendar = Calendar.getInstance()
        val loginTime = calendar.time

        calendar.apply {
            if (isRememberMe) {
                add(Calendar.DAY_OF_MONTH, 30)
            } else {
                add(Calendar.HOUR_OF_DAY, 24)
            }
        }

        val expiryTime = calendar.time

        val session = UserSessionEntity(
            sessionToken = generateSessionToken(),
            userId = userId,
            deviceId = getDeviceId(),
            loginTime = loginTime as java.sql.Date,
            expiryTime = expiryTime as java.sql.Date,
            isRememberMe = isRememberMe
        )

        sessionDao.insertSession(session)
        return session
    }

    suspend fun validateSession(token: String): Boolean {
        val session = sessionDao.getSessionByToken(token) ?: return false
        return session.expiryTime.after(Date())
    }

    suspend fun getCurrentUser(token: String): Long? {
        val session = sessionDao.getSessionByToken(token) ?: return null
        return if (session.expiryTime.after(Date())) {
            session.userId
        } else {
            sessionDao.deleteSession(token)
            null
        }
    }

    private fun getDeviceId(): String {
        return Settings.Secure.getString(
            App.context.contentResolver,
            Settings.Secure.ANDROID_ID
        )
    }
}

