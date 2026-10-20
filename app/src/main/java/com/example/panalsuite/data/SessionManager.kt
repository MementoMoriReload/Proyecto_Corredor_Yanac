package com.example.panalsuite.data

import android.content.Context
import android.content.SharedPreferences
import com.example.panalsuite.model.Ticket
import com.google.gson.reflect.TypeToken
import com.google.gson.Gson

class SessionManager(context: Context) {
    // Crea un archivo local llamado "sesion_panal"
    private val prefs: SharedPreferences = context.getSharedPreferences("sesion_panal", Context.MODE_PRIVATE)
    private val gson = Gson()

    // Función para guardar el correo
    fun saveUserEmail(email: String) {
        prefs.edit().putString("EMAIL", email).apply()
    }

    // Función para obtener el correo (devuelve null si no hay ninguno)
    fun getUserEmail(): String {
        return prefs.getString("EMAIL", "")?: ""
    }

    // Función para borrar los datos cuando cierra sesión
    fun clearSession() {
        prefs.edit().clear().apply()
    }
}