package com.kelompok.rebook.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_session")

class SessionManager(private val context: Context) {
    companion object {
        private val TOKEN_KEY = stringPreferencesKey("auth_token")
        private val USER_ID_KEY = stringPreferencesKey("user_id")
        private val USER_NAME_KEY = stringPreferencesKey("user_name")
        private val USER_EMAIL_KEY = stringPreferencesKey("user_email")
        private val USER_PHONE_KEY = stringPreferencesKey("user_phone")
        private val USER_STATUS_KEY = stringPreferencesKey("user_status")
    }

    val authToken: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[TOKEN_KEY]
    }

    val userData: Flow<UserProfile?> = context.dataStore.data.map { preferences ->
        val email = preferences[USER_EMAIL_KEY] ?: return@map null
        UserProfile(
            id = preferences[USER_ID_KEY] ?: "",
            name = preferences[USER_NAME_KEY] ?: "",
            email = email,
            username = email,
            status = preferences[USER_STATUS_KEY] ?: "Mahasiswa",
            phone = preferences[USER_PHONE_KEY] ?: ""
        )
    }

    suspend fun saveSession(user: UserProfile, token: String) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
            preferences[USER_ID_KEY] = user.id
            preferences[USER_NAME_KEY] = user.name
            preferences[USER_EMAIL_KEY] = user.email
            preferences[USER_PHONE_KEY] = user.phone
            preferences[USER_STATUS_KEY] = user.status
        }
    }

    suspend fun updateProfile(name: String, phone: String, status: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_NAME_KEY] = name
            preferences[USER_PHONE_KEY] = phone
            preferences[USER_STATUS_KEY] = status
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
