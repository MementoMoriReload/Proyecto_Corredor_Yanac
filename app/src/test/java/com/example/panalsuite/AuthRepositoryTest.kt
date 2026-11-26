package com.example.panalsuite

import com.example.panalsuite.model.UserRole
import com.example.panalsuite.network.ApiService
import com.example.panalsuite.network.LoginRequest
import com.example.panalsuite.network.RoleRequest
import com.example.panalsuite.network.UserDTO
import com.example.panalsuite.network.UserRequestDTO
import com.example.panalsuite.repository.AuthRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response

class AuthRepositoryTest {

    private val api = mockk<ApiService>()
    private val repository = AuthRepository(api)

    @Test
    fun loginTrimsTheEmailAndReturnsTheUser() = runTest {
        coEvery { api.login(LoginRequest("victor@panal.cl", "1234")) } returns
                Response.success(sampleUserDto())

        val result = repository.login("  victor@panal.cl ", "1234")

        assertTrue(result.isSuccess)
        assertEquals("1", result.getOrNull()?.id)
        assertEquals("victor@panal.cl", result.getOrNull()?.email)
    }

    @Test
    fun loginFailsWithServerMessageOnWrongCredentials() = runTest {
        coEvery { api.login(any()) } returns
                Response.error<UserDTO>(401, errorBody("Correo o contraseña incorrectas"))

        val result = repository.login("victor@panal.cl", "mala")

        assertTrue(result.isFailure)
        assertEquals("Correo o contraseña incorrectas", result.exceptionOrNull()?.message)
    }

    @Test
    fun getUsersMapsAllDtos() = runTest {
        coEvery { api.getUsers() } returns listOf(sampleUserDto(1L), sampleUserDto(2L))

        val result = repository.getUsers()

        assertEquals(listOf("1", "2"), result.getOrNull()?.map { it.id })
    }

    @Test
    fun registerTrimsNameAndEmail() = runTest {
        val sent = slot<UserRequestDTO>()
        coEvery { api.createUser(capture(sent)) } returns Response.success(sampleUserDto())

        val result = repository.register(" Victor Castro ", " victor@panal.cl ", "1234", UserRole.REQUESTER)

        assertTrue(result.isSuccess)
        assertEquals("Victor Castro", sent.captured.name)
        assertEquals("victor@panal.cl", sent.captured.email)
        assertEquals(UserRole.REQUESTER, sent.captured.role)
    }

    @Test
    fun updateUserRoleSendsTheNewRole() = runTest {
        val sent = slot<RoleRequest>()
        coEvery { api.updateUserRole(3L, capture(sent)) } returns
                Response.success(sampleUserDto(3L, UserRole.ADMIN))

        val result = repository.updateUserRole("3", UserRole.ADMIN)

        assertTrue(result.isSuccess)
        assertEquals(UserRole.ADMIN, sent.captured.role)
    }

    @Test
    fun updateUserRoleFailsWithInvalidIdAndNeverCallsTheApi() = runTest {
        val result = repository.updateUserRole("abc", UserRole.ADMIN)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
        coVerify(exactly = 0) { api.updateUserRole(any(), any()) }
    }
}