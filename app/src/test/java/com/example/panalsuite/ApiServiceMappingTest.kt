package com.example.panalsuite

import com.example.panalsuite.model.TicketStatus
import com.example.panalsuite.model.UserRole
import com.example.panalsuite.network.ApiException
import com.example.panalsuite.network.UserDTO
import com.example.panalsuite.network.bodyOrThrow
import com.example.panalsuite.network.requireSuccess
import com.example.panalsuite.network.toTicket
import com.example.panalsuite.network.toUser
import com.example.panalsuite.network.userMessage
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Test
import retrofit2.Response
import java.io.IOException

class ApiServiceMappingTest {

    // ---------- Conversión DTO -> modelo ----------

    @Test
    fun userDtoIsMappedToUserWithStringId() {
        val user = sampleUserDto(id = 7L, role = UserRole.MANAGER).toUser()

        assertEquals("7", user.id)
        assertEquals("Victor Castro", user.name)
        assertEquals("victor@panal.cl", user.email)
        assertNull(user.department)
        assertEquals(UserRole.MANAGER, user.role)
    }

    @Test
    fun ticketDtoIsMappedToTicket() {
        val ticket = sampleTicketDto(assignedId = 2L).toTicket()

        assertEquals(5, ticket.id)
        assertEquals("Impresora", ticket.title)
        assertEquals(TicketStatus.OPEN, ticket.status)
        assertEquals("1", ticket.requesterId)
        assertEquals("2", ticket.assignedId)
    }

    @Test
    fun ticketDatesAreShortenedToMinutes() {
        val ticket = sampleTicketDto().toTicket()

        assertEquals("2026-11-26 14:26", ticket.createdAt)
        assertEquals("2026-11-26 14:29", ticket.updatedAt)
    }

    @Test
    fun ticketCommentsAreMapped() {
        val ticket = sampleTicketDto().toTicket()

        assertEquals(1, ticket.comments.size)
        assertEquals("9", ticket.comments[0].id)
        assertEquals("Victor", ticket.comments[0].author)
        assertEquals("2026-11-26 14:30", ticket.comments[0].timestamp)
    }

    // ---------- bodyOrThrow ----------

    @Test
    fun bodyOrThrowReturnsTheBodyWhenSuccessful() {
        val dto = sampleUserDto()

        val body = Response.success(dto).bodyOrThrow()

        assertEquals(dto, body)
    }

    @Test
    fun bodyOrThrowUsesTheServerErrorMessage() {
        val response = Response.error<UserDTO>(400, errorBody("Correo inválido"))

        val error = assertThrows(ApiException::class.java) { response.bodyOrThrow() }

        assertEquals("Correo inválido", error.message)
    }

    @Test
    fun bodyOrThrowUsesGenericMessageWhenBodyIsNotJson() {
        val body = "oops".toResponseBody("text/plain".toMediaType())
        val response = Response.error<UserDTO>(500, body)

        val error = assertThrows(ApiException::class.java) { response.bodyOrThrow() }

        assertEquals("Error del servidor (500)", error.message)
    }

    @Test
    fun bodyOrThrowFailsWhenSuccessfulResponseHasNoBody() {
        val response = Response.success<UserDTO>(null)

        val error = assertThrows(ApiException::class.java) { response.bodyOrThrow() }

        assertEquals("El servidor devolvio una respuesta vacia", error.message)
    }

    // ---------- requireSuccess (usado al eliminar) ----------

    @Test
    fun requireSuccessDoesNothingWhenSuccessful() {
        Response.success(Unit).requireSuccess()
    }

    @Test
    fun requireSuccessThrowsServerMessageOnError() {
        val response = Response.error<Unit>(404, errorBody("Ticket 1 no encontrado"))

        val error = assertThrows(ApiException::class.java) { response.requireSuccess() }

        assertEquals("Ticket 1 no encontrado", error.message)
    }

    // ---------- userMessage ----------

    @Test
    fun userMessageForApiExceptionIsItsMessage() {
        assertEquals("Algo falló", ApiException("Algo falló").userMessage())
    }

    @Test
    fun userMessageForNetworkErrorIsNetworkMessage() {
        assertEquals("Error de red", IOException("sin conexión").userMessage())
    }

    @Test
    fun userMessageForUnknownErrorWithoutMessageIsGeneric() {
        assertEquals("Error inesperado", RuntimeException().userMessage())
    }
}