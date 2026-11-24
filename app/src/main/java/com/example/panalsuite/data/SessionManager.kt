package com.example.panalsuite.data

import android.content.Context
import android.content.SharedPreferences
import com.example.panalsuite.model.Ticket
import com.example.panalsuite.model.User
import com.google.gson.reflect.TypeToken
import com.google.gson.Gson

class SessionManager(context: Context) {
    // Crea un archivo local llamado "sesion_panal"
    private val prefs: SharedPreferences = context.getSharedPreferences("sesion_panal", Context.MODE_PRIVATE)
    private val gson = Gson()


    // Guarda el usuario(sesion)
    fun saveUser(user: User){
        prefs.edit()
            .putString("USER", gson.toJson(user))
            .putString("EMAIL", user.email)
            .apply()
    }

    // Recupera el usuario(sesion) guardado
    fun getSavedUser(): User? {
        val json = prefs.getString("USER", null)?: return null
        return try {
            gson.fromJson(json, User::class.java)

        }
        catch (e: Exception){
            null
        }
    }

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