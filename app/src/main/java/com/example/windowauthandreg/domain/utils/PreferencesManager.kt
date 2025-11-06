package com.example.windowauthandreg.domain.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

val Context.authDataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_preferences")

class PreferencesManager  @Inject constructor(
    private val context: Context
) {
    private val dataStore = context.authDataStore

    private object PreferencesKeys {
        val SESSION_TOKEN = stringPreferencesKey("session_token")
        val USER_ID = longPreferencesKey("user_id")
    }

    suspend fun saveSessionToken(token: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.SESSION_TOKEN] = token
        }
    }

    suspend fun saveUserId(userId: Long) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_ID] = userId
        }
    }

    suspend fun getSessionToken(): String? {
        return dataStore.data.map { preferences ->
            preferences[PreferencesKeys.SESSION_TOKEN]
        }.first()
    }

    suspend fun getUserId(): Long? {
        return dataStore.data.map { preferences ->
            preferences[PreferencesKeys.USER_ID]
        }.first()
    }

    suspend fun clearSession() {
        dataStore.edit { preferences ->
            preferences.remove(PreferencesKeys.SESSION_TOKEN)
            preferences.remove(PreferencesKeys.USER_ID)
        }
    }
}