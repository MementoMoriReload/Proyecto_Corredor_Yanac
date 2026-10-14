package com.example.panalsuite.viewmodel

import androidx.lifecycle.ViewModel
import com.example.panalsuite.model.User
import com.example.panalsuite.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class LoginUi(
    val email: String = "",
    val password: String = "",
    val error: String? = null
)

class AuthViewModel(
) : ViewModel() {
    private val repository: AuthRepository = AuthRepository()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _loginState = MutableStateFlow(LoginUi())
    val loginState: StateFlow<LoginUi> = _loginState.asStateFlow()

    fun onEmailChange(value: String) {
        _loginState.update{ it.copy(email = value, error = null)}
    }

    fun onPasswordChange(value: String){
        _loginState.update { it.copy(password = value, error = null) }
    }

    fun login(): Boolean {
        val state = _loginState.value

        if (state.email.isBlank() || state.password.isBlank()) {
            _loginState.update { it.copy(error = "Ingresa correo y contraseña") }
            return false
        }

        val user = repository.login(state.email, state.password)
        if (user == null) {
            _loginState.update { it.copy(error = "Correo o contraseña incorrectos") }
            return false
        }

        _currentUser.value= user
        _loginState.value = LoginUi()

        return true
    }


    fun logout() {
        _currentUser.value = null
        _loginState.value = LoginUi()
    }

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError.asStateFlow()

}