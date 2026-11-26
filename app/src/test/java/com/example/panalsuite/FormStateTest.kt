package com.example.panalsuite

import com.example.panalsuite.viewmodel.LoginUiState
import com.example.panalsuite.viewmodel.RegisterUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FormStateTest {

    // ---------- Login ----------

    @Test
    fun loginWithBlankEmailShowsRequiredError() {
        val state = LoginUiState(email = "", password = "1234")
        assertEquals("El correo es obligatorio", state.emailError)
        assertFalse(state.isValid)
    }

    @Test
    fun loginWithEmailOutsidePanalDomainIsInvalid() {
        val state = LoginUiState(email = "juan@gmail.com", password = "1234")
        assertEquals("Formato de correo inválido", state.emailError)
        assertFalse(state.isValid)
    }

    @Test
    fun loginWithBlankPasswordShowsRequiredError() {
        val state = LoginUiState(email = "victor@panal.cl", password = "")
        assertEquals("La contraseña es obligatoria", state.passwordError)
        assertFalse(state.isValid)
    }

    @Test
    fun loginWithValidDataIsValid() {
        val state = LoginUiState(email = "victor@panal.cl", password = "1234")
        assertNull(state.emailError)
        assertNull(state.passwordError)
        assertTrue(state.isValid)
    }

    // ---------- Registro ----------

    @Test
    fun registerWithShortNameIsInvalid() {
        val state = RegisterUiState(
            name = "Al",
            email = "alex@panal.cl",
            password = "1234",
            confirmPassword = "1234"
        )
        assertEquals("El nombre es obligatorio", state.nameError)
        assertFalse(state.isValid)
    }

    @Test
    fun registerWithDifferentPasswordsIsInvalid() {
        val state = RegisterUiState(
            name = "Victor Castro",
            email = "victor@panal.cl",
            password = "1234",
            confirmPassword = "9999"
        )
        assertEquals("Las contraseñas no coinciden", state.confirmPasswordError)
        assertFalse(state.isValid)
    }

    @Test
    fun registerWithValidDataIsValid() {
        val state = RegisterUiState(
            name = "Victor Castro",
            email = "victor@panal.cl",
            password = "1234",
            confirmPassword = "1234"
        )
        assertTrue(state.isValid)
    }
}