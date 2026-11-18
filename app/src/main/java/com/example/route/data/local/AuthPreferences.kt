package com.example.route.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.authDataStore by preferencesDataStore(name = "route_auth_prefs")

class AuthPreferences(private val context: Context) {

    private object Keys {
        val TOKEN = stringPreferencesKey("auth_token")
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_EMAIL = stringPreferencesKey("user_email")
    }

    val tokenFlow: Flow<String?> = context.authDataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { prefs -> prefs[Keys.TOKEN] }

    val userNameFlow: Flow<String?> = context.authDataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { prefs -> prefs[Keys.USER_NAME] }

    val userEmailFlow: Flow<String?> = context.authDataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { prefs -> prefs[Keys.USER_EMAIL] }

    suspend fun saveSession(token: String, name: String, email: String) {
        context.authDataStore.edit { prefs ->
            prefs[Keys.TOKEN] = token
            prefs[Keys.USER_NAME] = name
            prefs[Keys.USER_EMAIL] = email
        }
    }

    suspend fun clearSession() {
        context.authDataStore.edit { prefs ->
            prefs.remove(Keys.TOKEN)
            prefs.remove(Keys.USER_NAME)
            prefs.remove(Keys.USER_EMAIL)
        }
    }
}
