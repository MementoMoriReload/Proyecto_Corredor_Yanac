package com.example.panalsuite.viewmodel

import androidx.lifecycle.ViewModel
import com.example.panalsuite.model.Ticket
import com.example.panalsuite.model.TicketComment
import com.example.panalsuite.model.TicketStatus
import com.example.panalsuite.model.TicketCategory
import com.example.panalsuite.model.TicketPriority
import com.example.panalsuite.model.User
import com.example.panalsuite.model.UserRole
import com.example.panalsuite.repository.TicketRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate

class TicketViewModel(
) : ViewModel() {
    private val repository: TicketRepository = TicketRepository()

    private val _tickets = MutableStateFlow(repository.getTickets())
    val tickets: StateFlow<List<Ticket>> = _tickets.asStateFlow()

    fun getVisibleTickets(user: User): List<Ticket> = when {
        user.role.canSeeAllTickets -> _tickets.value
        user.role == UserRole.RESOLVER -> _tickets.value.filter { it.assignedId == user.id }
        else -> _tickets.value.filter { it.requesterId == user.id }
    }

    fun getTicketById(id: Int): Ticket? = _tickets.value.firstOrNull { it.id == id }

    fun changeStatus(ticketId: Int, newStatus: TicketStatus) {
        _tickets.value = _tickets.value.map {
            if (it.id == ticketId) it.copy(status = newStatus) else it
        }
    }

    fun addComment(ticketId: Int, author: String, text: String) {
        _tickets.value = _tickets.value.map { ticket ->
            if (ticket.id != ticketId) ticket
            else ticket.copy(
                comments = ticket.comments + TicketComment(
                    id = ticket.comments.size + 1,
                    author = author,
                    text = text.trim(),
                    timestamp = "ahora"
                )
            )
        }
    }

    fun createTicket(
        title: String,
        description: String,
        category: TicketCategory,
        priority: TicketPriority,
        location: String,
        requesterId: String
    ) {
        val currentTickets = _tickets.value
        val newId = (currentTickets.maxOfOrNull { it.id } ?: 0) + 1
        val currentDate = LocalDate.now().toString() // Genera la fecha actual automáticamente (ej. "2026-10-15")

        val newTicket = Ticket(
            id = newId,
            title = title.trim(),
            description = description.trim(),
            category = category,
            priority = priority,
            status = TicketStatus.OPEN,
            location = location.trim(),
            requesterId = requesterId,
            assignedId = null,
            comments = emptyList(),
            createdAt = currentDate,
            updatedAt = currentDate
        )

        _tickets.value = currentTickets + newTicket
    }
}