package com.example.windowauthandreg.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "user_sessions",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["user_id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["user_id"])]
)
data class UserSessionEntity(
    @PrimaryKey
    @ColumnInfo(name = "session_token")
    val sessionToken: String,

    @ColumnInfo(name = "user_id")
    val userId: Long,

    @ColumnInfo(name = "device_id")
    val deviceId: String,

    @ColumnInfo(name = "login_time")
    val loginTime: Date,

    @ColumnInfo(name = "expiry_time")
    val expiryTime: Date,

    @ColumnInfo(name = "is_remember_me")
    val isRememberMe: Boolean = false
)

