package com.example.panalsuite.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.panalsuite.model.User
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// Crea el archivo local "sesion_panal" en el teléfono
private val Context.dataStore by preferencesDataStore(name = "sesion_panal")

class SessionManager(context: Context) {

    private val dataStore = context.applicationContext.dataStore
    private val gson = Gson()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private companion object {
        val USER_KEY = stringPreferencesKey("USER")
        val EMAIL_KEY = stringPreferencesKey("EMAIL")
    }

    // Guarda el usuario
    fun saveUser(user: User) {
        scope.launch {
            dataStore.edit { prefs ->
                prefs[USER_KEY] = gson.toJson(user)
                prefs[EMAIL_KEY] = user.email
            }
        }
    }

    // Recupera el usuario guardado
    fun getSavedUser(): User? {
        val json = runBlocking { dataStore.data.first()[USER_KEY] } ?: return null
        return try {
            gson.fromJson(json, User::class.java)
        } catch (e: Exception) {
            null
        }
    }

    // Guarda el correo
    fun saveUserEmail(email: String) {
        scope.launch {
            dataStore.edit { prefs -> prefs[EMAIL_KEY] = email }
        }
    }

    // Devuelve el correo guardado
    fun getUserEmail(): String {
        return runBlocking { dataStore.data.first()[EMAIL_KEY] } ?: ""
    }

    // Borra los datos cuando cierra sesión
    fun clearSession() {
        scope.launch {
            dataStore.edit { prefs -> prefs.clear() }
        }
    }
}