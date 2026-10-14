package com.example.panalsuite.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.panalsuite.data.SessionManager
import com.example.panalsuite.model.User
import com.example.panalsuite.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError.asStateFlow()

    fun login(email: String, password: String, context: Context): Boolean {
        val user = repository.login(email, password)
        _currentUser.value = user
        _loginError.value = if (user == null) "Correo o contraseña incorrectos" else null

        if (user != null){
            val sessionManager = SessionManager(context)
            sessionManager.saveUserEmail(user.email)
        }
        return user != null
    }

    fun logout(context: Context) {
        val sessionManager = SessionManager(context)
        sessionManager.clearSession()
        _currentUser.value = null
    }
}