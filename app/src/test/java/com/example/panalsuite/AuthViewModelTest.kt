package com.example.panalsuite

import android.app.Application
import com.example.panalsuite.data.SessionManager
import com.example.panalsuite.model.User
import com.example.panalsuite.model.UserRole
import com.example.panalsuite.network.ApiException
import com.example.panalsuite.repository.AuthRepository
import com.example.panalsuite.viewmodel.AuthViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AuthViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = mockk<AuthRepository>()
    private val sessionManager = mockk<SessionManager>(relaxed = true)
    private val application = mockk<Application>(relaxed = true)

    private fun user(id: String, role: UserRole = UserRole.REQUESTER) = User(
        id = id,
        name = "Usuario $id",
        email = "usuario$id@panal.cl",
        department = null,
        role = role
    )

    // Crea el ViewModel simulando que en el teléfono había (o no) una sesión guardada
    private fun createViewModel(savedUser: User? = null, savedEmail: String = ""): AuthViewModel {
        every { sessionManager.getSavedUser() } returns savedUser
        every { sessionManager.getUserEmail() } returns savedEmail
        return AuthViewModel(application, repository, sessionManager)
    }

    @Before
    fun setUp() {
        coEvery { repository.getUsers() } returns Result.success(emptyList())
    }

    // ---------- Estado inicial y sesión guardada ----------

    @Test
    fun startsWithoutUserWhenThereIsNoSavedSession() {
        val viewModel = createViewModel()

        assertNull(viewModel.currentUser.value)
        assertEquals("", viewModel.loginState.value.email)
    }

    @Test
    fun restoresTheSavedUserAndLoadsTheUserList() {
        val saved = user("1", UserRole.ADMIN)
        coEvery { repository.getUsers() } returns Result.success(listOf(saved))

        val viewModel = createViewModel(savedUser = saved)

        assertEquals(saved, viewModel.currentUser.value)
        assertEquals(listOf(saved), viewModel.users.value)
    }

    @Test
    fun showsTheRememberedEmailInTheLoginForm() {
        val viewModel = createViewModel(savedEmail = "victor@panal.cl")

        assertEquals("victor@panal.cl", viewModel.loginState.value.email)
    }

    // ---------- Login ----------

    @Test
    fun loginWithInvalidFormDoesNotCallTheRepository() {
        val viewModel = createViewModel()
        viewModel.onEmailChange("correo-malo")

        viewModel.login()

        assertTrue(viewModel.loginState.value.showErrors)
        coVerify(exactly = 0) { repository.login(any(), any()) }
    }

    @Test
    fun successfulLoginStoresTheUserAndRunsTheCallback() {
        val logged = user("1")
        coEvery { repository.login("victor@panal.cl", "1234") } returns Result.success(logged)
        coEvery { repository.getUsers() } returns Result.success(listOf(logged))
        val viewModel = createViewModel()
        viewModel.onEmailChange("victor@panal.cl")
        viewModel.onPasswordChange("1234")
        var succeeded = false

        viewModel.login { succeeded = true }

        assertTrue(succeeded)
        assertEquals(logged, viewModel.currentUser.value)
        assertEquals(listOf(logged), viewModel.users.value)
        verify { sessionManager.saveUser(logged) }
    }

    @Test
    fun failedLoginShowsTheServerMessageAndKeepsTheUserNull() {
        coEvery { repository.login(any(), any()) } returns
                Result.failure(ApiException("Correo o contraseña incorrectas"))
        val viewModel = createViewModel()
        viewModel.onEmailChange("victor@panal.cl")
        viewModel.onPasswordChange("mala")

        viewModel.login()

        assertEquals("Correo o contraseña incorrectas", viewModel.loginState.value.error)
        assertFalse(viewModel.loginState.value.isLoading)
        assertNull(viewModel.currentUser.value)
    }

    @Test
    fun editingTheLoginFieldsClearsThePreviousError() {
        coEvery { repository.login(any(), any()) } returns Result.failure(ApiException("error"))
        val viewModel = createViewModel()
        viewModel.onEmailChange("victor@panal.cl")
        viewModel.onPasswordChange("mala")
        viewModel.login()

        viewModel.onPasswordChange("otra")

        assertNull(viewModel.loginState.value.error)
    }

    // ---------- Registro de usuarios ----------

    @Test
    fun registerWithoutAdminRoleIsRejectedAndNeverCallsTheRepository() {
        val viewModel = createViewModel(savedUser = user("1", UserRole.REQUESTER))
        var result: Result<User>? = null

        viewModel.registerUser("Nuevo", "nuevo@panal.cl", "1234", UserRole.REQUESTER) { result = it }

        assertTrue(result?.isFailure == true)
        assertEquals("No tienes permisos para crear usuarios", viewModel.actionError.value)
        coVerify(exactly = 0) { repository.register(any(), any(), any(), any()) }
    }

    @Test
    fun adminCanRegisterAUserAndItIsAddedToTheList() {
        val admin = user("1", UserRole.ADMIN)
        val created = user("2")
        coEvery { repository.getUsers() } returns Result.success(listOf(admin))
        coEvery { repository.register("Nuevo", "nuevo@panal.cl", "1234", UserRole.REQUESTER) } returns
                Result.success(created)
        val viewModel = createViewModel(savedUser = admin)
        var result: Result<User>? = null

        viewModel.registerUser("Nuevo", "nuevo@panal.cl", "1234", UserRole.REQUESTER) { result = it }

        assertTrue(result?.isSuccess == true)
        assertTrue(viewModel.users.value.contains(created))
        assertNull(viewModel.actionError.value)
    }

    @Test
    fun failedRegisterStoresTheServerMessage() {
        val admin = user("1", UserRole.ADMIN)
        coEvery { repository.register(any(), any(), any(), any()) } returns
                Result.failure(ApiException("El correo electrónico ya está registrado"))
        val viewModel = createViewModel(savedUser = admin)

        viewModel.registerUser("Nuevo", "nuevo@panal.cl", "1234", UserRole.REQUESTER)

        assertEquals("El correo electrónico ya está registrado", viewModel.actionError.value)
    }

    @Test
    fun clearActionErrorRemovesTheMessage() {
        val viewModel = createViewModel(savedUser = user("1", UserRole.REQUESTER))
        viewModel.registerUser("Nuevo", "nuevo@panal.cl", "1234", UserRole.REQUESTER)
        assertNotNull(viewModel.actionError.value)

        viewModel.clearActionError()

        assertNull(viewModel.actionError.value)
    }

    // ---------- Cambio de rol ----------

    @Test
    fun updatingTheRoleOfTheLoggedUserUpdatesTheSessionToo() {
        val admin = user("1", UserRole.ADMIN)
        val other = user("2")
        val updated = admin.copy(role = UserRole.MANAGER)
        coEvery { repository.getUsers() } returns Result.success(listOf(admin, other))
        coEvery { repository.updateUserRole("1", UserRole.MANAGER) } returns Result.success(updated)
        val viewModel = createViewModel(savedUser = admin)

        viewModel.updateUserRole("1", UserRole.MANAGER)

        assertEquals(UserRole.MANAGER, viewModel.currentUser.value?.role)
        assertEquals(UserRole.MANAGER, viewModel.users.value.first { it.id == "1" }.role)
        verify { sessionManager.saveUser(updated) }
    }

    @Test
    fun updatingTheRoleOfAnotherUserKeepsTheSessionUntouched() {
        val admin = user("1", UserRole.ADMIN)
        val other = user("2")
        val updatedOther = other.copy(role = UserRole.RESOLVER)
        coEvery { repository.getUsers() } returns Result.success(listOf(admin, other))
        coEvery { repository.updateUserRole("2", UserRole.RESOLVER) } returns Result.success(updatedOther)
        val viewModel = createViewModel(savedUser = admin)

        viewModel.updateUserRole("2", UserRole.RESOLVER)

        assertEquals(UserRole.ADMIN, viewModel.currentUser.value?.role)
        assertEquals(UserRole.RESOLVER, viewModel.users.value.first { it.id == "2" }.role)
        verify(exactly = 0) { sessionManager.saveUser(any()) }
    }

    @Test
    fun failedRoleUpdateStoresTheErrorMessage() {
        val admin = user("1", UserRole.ADMIN)
        coEvery { repository.updateUserRole(any(), any()) } returns
                Result.failure(ApiException("Usuario 2 no encontrado"))
        val viewModel = createViewModel(savedUser = admin)

        viewModel.updateUserRole("2", UserRole.RESOLVER)

        assertEquals("Usuario 2 no encontrado", viewModel.actionError.value)
    }

    // ---------- Cerrar sesión ----------

    @Test
    fun logoutClearsTheSessionTheUserAndTheList() {
        val admin = user("1", UserRole.ADMIN)
        coEvery { repository.getUsers() } returns Result.success(listOf(admin))
        val viewModel = createViewModel(savedUser = admin)

        viewModel.logout()

        assertNull(viewModel.currentUser.value)
        assertTrue(viewModel.users.value.isEmpty())
        verify { sessionManager.clearSession() }
    }
}