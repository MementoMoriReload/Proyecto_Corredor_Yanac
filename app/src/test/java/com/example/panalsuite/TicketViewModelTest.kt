package com.example.panalsuite

import com.example.panalsuite.model.Ticket
import com.example.panalsuite.model.TicketCategory
import com.example.panalsuite.model.TicketPriority
import com.example.panalsuite.model.TicketStatus
import com.example.panalsuite.model.User
import com.example.panalsuite.model.UserRole
import com.example.panalsuite.repository.TicketRepository
import com.example.panalsuite.viewmodel.TicketViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class TicketViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // Repositorio falso: no toca la red
    private val repository = mockk<TicketRepository>()

    private fun user(id: String, role: UserRole) = User(
        id = id,
        name = "Usuario",
        email = "usuario@panal.cl",
        department = null,
        role = role
    )

    // Crea el ViewModel con una lista inicial de tickets ya cargada
    private fun viewModelWith(vararg tickets: Ticket): TicketViewModel {
        coEvery { repository.getTickets() } returns Result.success(tickets.toList())
        val viewModel = TicketViewModel(repository)
        viewModel.loadTickets()
        return viewModel
    }

    // ---------- Cargar ----------

    @Test
    fun loadTicketsFillsTheList() {
        val viewModel = viewModelWith(sampleTicket(1), sampleTicket(2))

        assertEquals(2, viewModel.tickets.value.size)
    }

    @Test
    fun loadTicketsKeepsTheListEmptyWhenItFails() {
        coEvery { repository.getTickets() } returns Result.failure(Exception("sin red"))
        val viewModel = TicketViewModel(repository)

        viewModel.loadTickets()

        assertTrue(viewModel.tickets.value.isEmpty())
    }

    // ---------- Qué tickets ve cada rol ----------

    @Test
    fun requesterSeesOnlyHisOwnTickets() {
        val viewModel = viewModelWith(
            sampleTicket(1, requesterId = "1"),
            sampleTicket(2, requesterId = "2")
        )

        val visible = viewModel.getVisibleTickets(user("1", UserRole.REQUESTER))

        assertEquals(listOf(1), visible.map { it.id })
    }

    @Test
    fun resolverSeesOnlyTicketsAssignedToHim() {
        val viewModel = viewModelWith(
            sampleTicket(1, assignedId = "7"),
            sampleTicket(2, assignedId = null),
            sampleTicket(3, assignedId = "8")
        )

        val visible = viewModel.getVisibleTickets(user("7", UserRole.RESOLVER))

        assertEquals(listOf(1), visible.map { it.id })
    }

    @Test
    fun supervisorSeesAllTickets() {
        val viewModel = viewModelWith(sampleTicket(1), sampleTicket(2), sampleTicket(3))

        val visible = viewModel.getVisibleTickets(user("9", UserRole.SUPERVISOR))

        assertEquals(3, visible.size)
    }

    // ---------- Buscar y actualizar en la lista ----------

    @Test
    fun getTicketByIdFindsTheTicketOrReturnsNull() {
        val viewModel = viewModelWith(sampleTicket(1))

        assertNotNull(viewModel.getTicketById(1))
        assertNull(viewModel.getTicketById(99))
    }

    @Test
    fun updateTicketReplacesAnExistingTicket() {
        val viewModel = viewModelWith(sampleTicket(1))

        viewModel.updateTicket(sampleTicket(1).copy(title = "Nuevo título"))

        assertEquals(1, viewModel.tickets.value.size)
        assertEquals("Nuevo título", viewModel.getTicketById(1)?.title)
    }

    @Test
    fun updateTicketAddsATicketThatIsNotInTheList() {
        val viewModel = viewModelWith(sampleTicket(1))

        viewModel.updateTicket(sampleTicket(2))

        assertEquals(2, viewModel.tickets.value.size)
    }

    // ---------- Crear ----------

    @Test
    fun createTicketAddsTheCreatedTicketToTheList() {
        coEvery {
            repository.createTicket(any(), any(), any(), any(), any(), any())
        } returns Result.success(sampleTicket(10))
        val viewModel = TicketViewModel(repository)

        viewModel.createTicket(
            title = "Impresora",
            description = "No imprime",
            category = TicketCategory.INTERNAL_SUPPORT,
            priority = TicketPriority.HIGH,
            location = "Piso 1",
            requesterId = "1"
        )

        assertNotNull(viewModel.getTicketById(10))
    }

    @Test
    fun createTicketWithInvalidRequesterIdDoesNotCallTheRepository() {
        val viewModel = TicketViewModel(repository)

        viewModel.createTicket(
            title = "Impresora",
            description = "No imprime",
            category = TicketCategory.INTERNAL_SUPPORT,
            priority = TicketPriority.HIGH,
            location = "Piso 1",
            requesterId = "abc"
        )

        coVerify(exactly = 0) {
            repository.createTicket(any(), any(), any(), any(), any(), any())
        }
    }

    // ---------- Cerrar, asignar y comentar ----------

    @Test
    fun closeTicketUpdatesTheStatusInTheList() {
        val viewModel = viewModelWith(sampleTicket(1))
        coEvery { repository.updateStatus(1, TicketStatus.CLOSED) } returns
                Result.success(sampleTicket(1).copy(status = TicketStatus.CLOSED))

        viewModel.closeTicket(1)

        assertEquals(TicketStatus.CLOSED, viewModel.getTicketById(1)?.status)
    }

    @Test
    fun assignTicketUpdatesTheResolverInTheList() {
        val viewModel = viewModelWith(sampleTicket(1))
        coEvery { repository.assignTicket(1, 7L) } returns
                Result.success(sampleTicket(1, assignedId = "7"))

        viewModel.assignTicket(1, "7")

        assertEquals("7", viewModel.getTicketById(1)?.assignedId)
    }

    @Test
    fun addCommentWithBlankTextDoesNotCallTheRepository() {
        val viewModel = viewModelWith(sampleTicket(1))

        viewModel.addComment(1, "Victor", "   ")

        coVerify(exactly = 0) { repository.addComent(any(), any(), any()) }
    }

    @Test
    fun addCommentUpdatesTheTicketInTheList() {
        val viewModel = viewModelWith(sampleTicket(1))
        coEvery { repository.addComent(1, "Victor", "Hola") } returns
                Result.success(sampleTicket(1).copy(updatedAt = "2026-11-26 15:00"))

        viewModel.addComment(1, "Victor", "Hola")

        assertEquals("2026-11-26 15:00", viewModel.getTicketById(1)?.updatedAt)
    }

    // ---------- Eliminar ----------

    @Test
    fun deleteTicketRemovesItFromTheListAndNotifies() {
        val viewModel = viewModelWith(sampleTicket(1), sampleTicket(2))
        coEvery { repository.deleteTicket(1) } returns Result.success(Unit)
        var notified = false

        viewModel.deleteTicket(1) { notified = true }

        assertTrue(notified)
        assertNull(viewModel.getTicketById(1))
        assertEquals(1, viewModel.tickets.value.size)
    }

    @Test
    fun deleteTicketKeepsTheTicketWhenTheServerFails() {
        val viewModel = viewModelWith(sampleTicket(1))
        coEvery { repository.deleteTicket(1) } returns Result.failure(Exception("error"))
        var notified = false

        viewModel.deleteTicket(1) { notified = true }

        assertFalse(notified)
        assertNotNull(viewModel.getTicketById(1))
    }
}