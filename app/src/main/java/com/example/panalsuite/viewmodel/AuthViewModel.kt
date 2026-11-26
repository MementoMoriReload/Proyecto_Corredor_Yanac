package com.example.panalsuite.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.panalsuite.data.SessionManager
import com.example.panalsuite.model.User
import com.example.panalsuite.model.UserRole
import com.example.panalsuite.network.userMessage
import com.example.panalsuite.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

val EMAIL_REGEX = Regex("^[A-Za-z0-9._%+-]+@panal.cl$")

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
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

class AuthViewModel(
    application: Application,
    private val repository: AuthRepository = AuthRepository(),
    private val sessionManager: SessionManager=SessionManager(application)
) : AndroidViewModel(application) {

    private val _currentUser = MutableStateFlow<User?>(sessionManager.getSavedUser())
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _loginState = MutableStateFlow(
        LoginUiState(email = sessionManager.getUserEmail())
    )
    val loginState: StateFlow<LoginUiState> = _loginState.asStateFlow()

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users.asStateFlow()

    private val _actionError = MutableStateFlow<String?>(null)
    val actionError: StateFlow<String?> = _actionError.asStateFlow()

    init {
        if (_currentUser.value != null) loadUsers()
    }

    fun clearActionError() {
        _actionError.value = null
    }

    fun loadUsers() {
        viewModelScope.launch {
            repository.getUsers()
                .onSuccess { _users.value = it }
                .onFailure {
                    Log.e("AuthVM", "No se pudieron cargar los usuarios: ${it.userMessage()}")
                }
        }
    }

    fun onEmailChange(value: String) {
        _loginState.update { it.copy(email = value, error = null) }
    }

    fun onPasswordChange(value: String) {
        _loginState.update { it.copy(password = value, error = null) }
    }

    fun login(onSuccess: () -> Unit = {}) {
        _loginState.update { it.copy(showErrors = true) }
        val state = _loginState.value

        if (!state.isValid || state.isLoading) return

        viewModelScope.launch {
            _loginState.update { it.copy(isLoading = true, error = null) }

            repository.login(state.email.trim(), state.password)
                .onSuccess { user ->
                    _currentUser.value = user
                    sessionManager.saveUser(user)
                    _loginState.value = LoginUiState()
                    loadUsers()
                    onSuccess()
                }
                .onFailure { e ->
                    _loginState.update {
                        it.copy(isLoading = false, error = e.userMessage())
                    }
                }
        }
    }

    fun registerUser(
        name: String,
        email: String,
        password: String,
        role: UserRole,
        onResult: (Result<User>) -> Unit = {}
    ) {
        if (_currentUser.value?.role?.canCreateUser != true) {
            val message = "No tienes permisos para crear usuarios"
            _actionError.value = message
            onResult(Result.failure(IllegalStateException(message)))
            return
        }

        viewModelScope.launch {
            repository.register(name.trim(), email.trim(), password, role)
                .onSuccess { created ->
                    _users.update { it + created }
                    _actionError.value = null
                    onResult(Result.success(created))
                }
                .onFailure { e ->
                    val message = e.userMessage()
                    _actionError.value = message
                    Log.e("AuthVM", "Error al registrar usuario: $message")
                    onResult(Result.failure(e))
                }
        }
    }

    fun updateUserRole(userId: String, newRole: UserRole) {
        viewModelScope.launch {
            repository.updateUserRole(userId, newRole)
                .onSuccess { updated ->
                    _users.update { list ->
                        list.map { if (it.id == updated.id) updated else it }
                    }
                    if (_currentUser.value?.id == updated.id) {
                        _currentUser.value = updated
                        sessionManager.saveUser(updated)
                    }
                    _actionError.value = null
                }
                .onFailure { e ->
                    val message = e.userMessage()
                    _actionError.value = message
                    Log.e("AuthVM", "Error al actualizar rol: $message")
                }
        }
    }

    fun logout() {
        sessionManager.clearSession()
        _currentUser.value = null
        _users.value = emptyList()
        _actionError.value = null
        _loginState.value = LoginUiState()
    }
}