package com.example.windowauthandreg.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.windowauthandreg.data.dao.SessionDao
import com.example.windowauthandreg.data.dao.UserDao
import com.example.windowauthandreg.data.entities.UserEntity
import com.example.windowauthandreg.data.entities.UserSessionEntity

@Database(
    entities = [UserEntity::class, UserSessionEntity::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(com.example.windowauthandreg.data.database.Converters::class) // Исправленный import
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun sessionDao(): SessionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "auth_database.db"
                )
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}