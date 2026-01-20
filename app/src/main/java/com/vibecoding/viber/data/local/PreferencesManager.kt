package com.vibecoding.viber.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "viber_preferences")

@Singleton
class PreferencesManager @Inject constructor(
    private val context: Context
) {
    private val dataStore = context.dataStore

    companion object {
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        private val USER_LOGIN_KEY = stringPreferencesKey("user_login")
        private val USER_AVATAR_KEY = stringPreferencesKey("user_avatar")
        private val VIBE_MODE_KEY = stringPreferencesKey("vibe_mode")
        private val CAT_MODE_KEY = stringPreferencesKey("cat_mode")
        private val AUTH_TYPE_KEY = stringPreferencesKey("auth_type")
    }

    val accessToken: Flow<String?> = dataStore.data.map { preferences ->
        preferences[ACCESS_TOKEN_KEY]
    }

    val userLogin: Flow<String?> = dataStore.data.map { preferences ->
        preferences[USER_LOGIN_KEY]
    }

    val userAvatar: Flow<String?> = dataStore.data.map { preferences ->
        preferences[USER_AVATAR_KEY]
    }

    val vibeModeEnabled: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[VIBE_MODE_KEY]?.toBoolean() ?: false
    }

    val catModeEnabled: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[CAT_MODE_KEY]?.toBoolean() ?: false
    }

    suspend fun saveAccessToken(token: String) {
        dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN_KEY] = token
        }
    }

    suspend fun saveUserInfo(login: String, avatarUrl: String) {
        dataStore.edit { preferences ->
            preferences[USER_LOGIN_KEY] = login
            preferences[USER_AVATAR_KEY] = avatarUrl
        }
    }

    suspend fun setVibeMode(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[VIBE_MODE_KEY] = enabled.toString()
        }
    }

    suspend fun setCatMode(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[CAT_MODE_KEY] = enabled.toString()
        }
    }

    suspend fun getAuthType(): String? {
        return dataStore.data.first()[AUTH_TYPE_KEY]
    }

    suspend fun setAuthType(type: String) {
        dataStore.edit { preferences ->
            preferences[AUTH_TYPE_KEY] = type
        }
    }

    suspend fun clearAll() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
