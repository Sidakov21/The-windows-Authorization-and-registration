package com.example.windowauthandreg.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.windowauthandreg.data.entities.UserSessionEntity
import java.util.Date

@Dao
interface SessionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: UserSessionEntity)

    @Query("SELECT * FROM user_sessions WHERE session_token = :token")
    suspend fun getSessionByToken(token: String): UserSessionEntity?

    @Query("SELECT * FROM user_sessions WHERE user_id = :userId AND device_id = :deviceId")
    suspend fun getSessionByUserAndDevice(userId: Long, deviceId: String): UserSessionEntity?

    @Query("DELETE FROM user_sessions WHERE session_token = :token")
    suspend fun deleteSession(token: String)

    @Query("DELETE FROM user_sessions WHERE user_id = :userId")
    suspend fun deleteAllUserSessions(userId: Long)

    @Query("DELETE FROM user_sessions WHERE expiry_time < :currentTime")
    suspend fun deleteExpiredSessions(currentTime: Date)

    @Query("SELECT * FROM user_sessions WHERE user_id = :userId ORDER BY login_time DESC LIMIT 5")
    suspend fun getRecentSessions(userId: Long): List<UserSessionEntity>
}
