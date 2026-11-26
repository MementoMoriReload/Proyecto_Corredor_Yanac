package com.example.panalsuite

import com.example.panalsuite.viewmodel.RegisterViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RegisterViewModelTest {

    private lateinit var viewModel: RegisterViewModel

    @Before
    fun setUp() {
        viewModel = RegisterViewModel()
    }

    @Test
    fun submitWithEmptyFormReturnsNullAndShowsErrors() {
        val result = viewModel.submit()

        assertNull(result)
        assertTrue(viewModel.uiState.value.showErrors)
    }

    @Test
    fun submitWithValidFormReturnsTheState() {
        viewModel.onNameChange("Victor Castro")
        viewModel.onEmailChange("victor@panal.cl")
        viewModel.onPasswordChange("1234")
        viewModel.onConfirmPasswordChange("1234")

        val result = viewModel.submit()

        assertNotNull(result)
        assertEquals("victor@panal.cl", result?.email)
    }

    @Test
    fun onSuccessClearsTheFormAndShowsMessage() {
        viewModel.onNameChange("Victor Castro")

        viewModel.onSuccess("Victor Castro")

        val state = viewModel.uiState.value
        assertEquals("", state.name)
        assertEquals("Usuario Victor Castro registrado correctamente", state.successMessage)
    }

    @Test
    fun onErrorStoresTheMessage() {
        viewModel.onError("El correo ya está registrado")

        assertEquals("El correo ya está registrado", viewModel.uiState.value.error)
    }

    @Test
    fun changingAFieldClearsThePreviousError() {
        viewModel.onError("Algo falló")

        viewModel.onNameChange("Victor")

        assertNull(viewModel.uiState.value.error)
    }
}