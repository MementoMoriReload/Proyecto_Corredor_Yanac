package com.example.panalsuite.viewmodel

import androidx.lifecycle.ViewModel
import com.example.panalsuite.model.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class RegisterUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val role: UserRole = UserRole.REQUESTER,
    val showErrors: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null

){
    val nameError: String?
        get() = if (name.isBlank() || name.trim().length < 3) "El nombre es obligatorio" else null

    val emailError: String?
        get() = when {
            email.isBlank() -> "El correo es obligatorio"
            !EMAIL_REGEX.matches(email.trim()) -> "Formato de correo inválido"
            else -> null
        }

    val passwordError: String?
        get() = if (password.isBlank()) "La contraseña es obligatoria" else null

    val confirmPasswordError: String?
        get() = if (password != confirmPassword) "Las contraseñas no coinciden" else null

    val isValid: Boolean
        get() = nameError == null && emailError == null && passwordError == null && confirmPasswordError == null

}

class RegisterViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onNameChange(value: String) =
        _uiState.update { it.copy(name = value, error = null, successMessage = null)
        }

    fun onEmailChange(value: String) =
        _uiState.update { it.copy(email = value, error = null, successMessage = null)
        }

    fun onPasswordChange(value: String) =
        _uiState.update { it.copy(password = value, error = null, successMessage = null)
        }

    fun onConfirmPasswordChange(value: String) =
        _uiState.update { it.copy(confirmPassword = value, error = null, successMessage = null)
        }

    fun onRoleChange(value: UserRole) =
        _uiState.update { it.copy(role = value, error = null, successMessage = null)
        }

    fun submit(): RegisterUiState?{
        _uiState.update { it.copy(showErrors = true) }
        val state = _uiState.value
        return if (state.isValid) state else null
    }

    fun onError(message: String){
        _uiState.update { it.copy(error = message) }
    }

    fun onSuccess(name: String){
        _uiState.value = RegisterUiState(
            successMessage = "Usuario ${name} registrado correctamente")
    }
}
