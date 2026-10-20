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
import kotlinx.coroutines.flow.update
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
        _tickets.update { list ->
            list.map { if (it.id == ticketId) it.copy(status = newStatus) else it }
        }
    }

    fun closeTicket(ticketId: Int){
        _tickets.update{ list ->
            list.map { ticket ->
                if (ticket.id == ticketId) {
                    ticket.copy(
                        status = TicketStatus.CLOSED,
                        updatedAt = LocalDate.now().toString()
                    )
                } else {
                    ticket
                }
            }
        }
    }

    fun addComment(ticketId: Int, author: String, text: String) {
        if (text.isBlank()) return

        val comment = TicketComment(
            id = _tickets.value.maxOfOrNull { it.id }?.plus(1) ?: 1,
            author = author,
            text = text.trim(),
            timestamp = LocalDate.now().toString()
        )

        _tickets.update { list ->
            list.map{ ticket ->
                if (ticket.id == ticketId) {
                    ticket.copy(
                        comments = ticket.comments + comment,
                        updatedAt = LocalDate.now().toString()
                    )
                } else ticket
            }
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
        val currentDate = LocalDate.now().toString()
        _tickets.update { currentList ->
            val newId = (currentList.maxOfOrNull { it.id } ?: 0) + 1
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
            currentList + newTicket
        }
    }
}