package com.example.panalsuite

import com.example.panalsuite.model.TicketCategory
import com.example.panalsuite.model.TicketPriority
import com.example.panalsuite.model.TicketStatus
import com.example.panalsuite.network.ApiService
import com.example.panalsuite.network.AssignRequest
import com.example.panalsuite.network.CommentRequestDTO
import com.example.panalsuite.network.StatusRequest
import com.example.panalsuite.network.TicketRequestDTO
import com.example.panalsuite.repository.TicketRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response
import java.io.IOException

class TicketRepositoryTest {

    // El "actor falso": una API que solo responde lo que nosotros le digamos
    private val api = mockk<ApiService>()
    private val repository = TicketRepository(api)

    @Test
    fun getTicketsMapsDtosToTickets() = runTest {
        coEvery { api.getTickets() } returns listOf(sampleTicketDto())

        val result = repository.getTickets()

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
        assertEquals("Impresora", result.getOrNull()?.first()?.title)
    }

    @Test
    fun getTicketsFailsWhenTheNetworkFails() = runTest {
        coEvery { api.getTickets() } throws IOException("sin red")

        val result = repository.getTickets()

        assertTrue(result.isFailure)
    }

    @Test
    fun createTicketTrimsTheFieldsAndSendsTheRequest() = runTest {
        val sent = slot<TicketRequestDTO>()
        coEvery { api.createTicket(capture(sent)) } returns Response.success(sampleTicketDto())

        val result = repository.createTicket(
            title = "  Impresora  ",
            description = "  No imprime ",
            location = " Piso 1 ",
            category = TicketCategory.INTERNAL_SUPPORT,
            priority = TicketPriority.HIGH,
            requesterId = 1L
        )

        assertTrue(result.isSuccess)
        assertEquals("Impresora", sent.captured.title)
        assertEquals("No imprime", sent.captured.description)
        assertEquals("Piso 1", sent.captured.location)
        assertEquals(1L, sent.captured.requesterId)
        coVerify(exactly = 1) { api.createTicket(any()) }
    }

    @Test
    fun createTicketFailsWithTheServerMessage() = runTest {
        coEvery { api.createTicket(any()) } returns
                Response.error(400, errorBody("La ubicación es obligatoria"))

        val result = repository.createTicket(
            "t", "d", "l", TicketCategory.INTERNAL_SUPPORT, TicketPriority.LOW, 1L
        )

        assertTrue(result.isFailure)
        assertEquals("La ubicación es obligatoria", result.exceptionOrNull()?.message)
    }

    @Test
    fun addCommentSendsAuthorAndText() = runTest {
        val sent = slot<CommentRequestDTO>()
        coEvery { api.createComment(5L, capture(sent)) } returns Response.success(sampleTicketDto())

        val result = repository.addComent(5, "Victor", "Hola")

        assertTrue(result.isSuccess)
        assertEquals("Victor", sent.captured.author)
        assertEquals("Hola", sent.captured.text)
    }

    @Test
    fun updateStatusSendsTheNewStatus() = runTest {
        val sent = slot<StatusRequest>()
        coEvery { api.updateTicketStatus(5L, capture(sent)) } returns Response.success(sampleTicketDto())

        val result = repository.updateStatus(5, TicketStatus.CLOSED)

        assertTrue(result.isSuccess)
        assertEquals(TicketStatus.CLOSED, sent.captured.status)
    }

    @Test
    fun assignTicketSendsTheResolverId() = runTest {
        val sent = slot<AssignRequest>()
        coEvery { api.assignTicket(5L, capture(sent)) } returns Response.success(sampleTicketDto())

        val result = repository.assignTicket(5, 7L)

        assertTrue(result.isSuccess)
        assertEquals(7L, sent.captured.assignedId)
    }

    @Test
    fun deleteTicketSucceedsOnEmptyResponse() = runTest {
        coEvery { api.deleteTicket(5L) } returns Response.success(Unit)

        val result = repository.deleteTicket(5)

        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { api.deleteTicket(5L) }
    }

    @Test
    fun deleteTicketFailsWithTheServerMessage() = runTest {
        coEvery { api.deleteTicket(5L) } returns
                Response.error(400, errorBody("Ticket 5 no encontrado"))

        val result = repository.deleteTicket(5)

        assertTrue(result.isFailure)
        assertEquals("Ticket 5 no encontrado", result.exceptionOrNull()?.message)
    }
}