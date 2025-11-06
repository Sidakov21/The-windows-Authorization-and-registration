package com.example.windowauthandreg.di

import android.content.Context
import com.example.windowauthandreg.data.dao.SessionDao
import com.example.windowauthandreg.data.dao.UserDao
import com.example.windowauthandreg.data.database.AppDatabase
import com.example.windowauthandreg.domain.repository.AuthRepository
import com.example.windowauthandreg.domain.repository.AuthRepositoryImpl
import com.example.windowauthandreg.domain.utils.PasswordManager
import com.example.windowauthandreg.domain.utils.PreferencesManager
import com.example.windowauthandreg.domain.utils.SessionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// di/AppModule.kt
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Provides
    fun provideUserDao(database: AppDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    fun provideSessionDao(database: AppDatabase): SessionDao {
        return database.sessionDao()
    }

    @Provides
    @Singleton
    fun providePasswordManager(): PasswordManager {
        return PasswordManager()
    }

    @Provides
    @Singleton
    fun provideSessionManager(sessionDao: SessionDao): SessionManager {
        return SessionManager(sessionDao)
    }

    @Provides
    @Singleton
    fun providePreferencesManager(@ApplicationContext context: Context): PreferencesManager {
        return PreferencesManager(context)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        userDao: UserDao,
        sessionDao: SessionDao,
        passwordManager: PasswordManager,
        sessionManager: SessionManager,
        preferencesManager: PreferencesManager
    ): AuthRepository {
        return AuthRepositoryImpl(
            userDao, sessionDao, passwordManager, sessionManager, preferencesManager
        )
    }
}