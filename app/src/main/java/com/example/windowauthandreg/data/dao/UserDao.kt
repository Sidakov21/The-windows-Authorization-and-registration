package com.example.windowauthandreg.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.windowauthandreg.data.entities.UserEntity
import java.util.Date

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: UserEntity): Long

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("SELECT * FROM users WHERE user_id = :userId")
    suspend fun getUserById(userId: Long): UserEntity?

    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE username = :username")
    suspend fun getUserByUsername(username: String): UserEntity?

    @Query("UPDATE users SET last_login = :lastLogin WHERE user_id = :userId")
    suspend fun updateLastLogin(userId: Long, lastLogin: Date)

    @Query("UPDATE users SET is_active = :isActive WHERE user_id = :userId")
    suspend fun updateUserStatus(userId: Long, isActive: Boolean)

    @Query("SELECT COUNT(*) FROM users WHERE email = :email")
    suspend fun checkEmailExists(email: String): Int

    @Query("SELECT COUNT(*) FROM users WHERE username = :username")
    suspend fun checkUsernameExists(username: String): Int

    @Query("UPDATE users SET password_hash = :newPasswordHash, salt = :newSalt WHERE user_id = :userId")
    suspend fun updatePassword(userId: Long, newPasswordHash: String, newSalt: String)
}

