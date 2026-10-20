package com.example.panalsuite.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.panalsuite.data.SessionManager
import com.example.panalsuite.model.User
import com.example.panalsuite.model.UserRole
import com.example.panalsuite.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

val EMAIL_REGEX = Regex("^[A-Za-z0-9._%+-]+@panal.cl$")

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val showErrors: Boolean = false,
    val error: String? = null
) {
    val emailError: String?
        get() = when {
            email.isBlank() -> "El correo es obligatorio"
            !EMAIL_REGEX.matches(email.trim()) -> "Formato de correo inválido"
            else -> null
        }

    val passwordError: String?
        get() = if (password.isBlank()) "La contraseña es obligatoria" else null

    val isValid: Boolean
        get() = emailError == null && passwordError == null
}

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AuthRepository()
    private val sessionManager = SessionManager(application)

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _loginState = MutableStateFlow(LoginUiState())
    val loginState: StateFlow<LoginUiState> = _loginState.asStateFlow()

    private val _users = MutableStateFlow<List<User>>(repository.getUsers())
    val users: StateFlow<List<User>> = _users.asStateFlow()

    init {
        sessionManager.getUserEmail()?.let { savedEmail ->
            _currentUser.value = repository.findByEmail(savedEmail)
            if (_currentUser.value == null) sessionManager.clearSession()
        }
    }

    fun onEmailChange(value: String) {
        _loginState.update { it.copy(email = value, error = null) }
    }

    fun onPasswordChange(value: String) {
        _loginState.update { it.copy(password = value, error = null) }
    }

    fun login(): Boolean {
        _loginState.update { it.copy(showErrors = true) }
        val state = _loginState.value

        if (!state.isValid) return false

        val user = repository.login(state.email, state.password)
        if (user == null) {
            _loginState.update { it.copy(error = "Correo o contraseña incorrectos") }
            return false
        }

        _currentUser.value = user
        sessionManager.saveUserEmail(user.email)
        _loginState.value = LoginUiState()
        return true
    }

    fun registerUser(
        name: String,
        email: String,
        password: String,
        role: UserRole
    ): String? {
        if (_currentUser.value?.role?.canCreateUser != true) {
            return "No tienes permisos para crear usuarios"
        }
        if (repository.findByEmail(email) != null) {
            return "Ya existe un usuario registrado con este correo"
        }

        repository.register(name, email, password, role)
            ?: return "Error al crear el usuario"

        _users.update { repository.getUsers() }

        return null

    }


    fun updateUserRole(userId: String, newRole: UserRole) {
        val success = repository.updateUserRole(userId, newRole)
        if (success){
            _users.update { repository.getUsers() }

            if (_currentUser.value?.id == userId){
                _currentUser.update {it?.copy(role = newRole)}
            }
        }
    }

    fun logout() {
        sessionManager.clearSession()
        _currentUser.value = null
        _loginState.value = LoginUiState()
    }
}

