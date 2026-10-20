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

    fun saveToken(token: String) {
        prefs.edit().putString("AUTH_TOKEN", token).apply()
    }

    fun getToken(): String? {
        return prefs.getString("AUTH_TOKEN", null)
    }

    fun saveTicket(tickets: List<Ticket>) {
        val json = gson.toJson(tickets)
        prefs.edit().putString("TICKETS", json).apply()
    }

    fun getTickets(): List<Ticket> {
        val json = prefs.getString("TICKETS", null)
        val type = object : TypeToken<List<Ticket>>() {}.type
        return gson.fromJson(json, type)
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