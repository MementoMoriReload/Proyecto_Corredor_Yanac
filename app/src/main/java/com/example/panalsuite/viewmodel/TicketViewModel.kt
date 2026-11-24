package com.example.panalsuite.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.panalsuite.model.Ticket
import com.example.panalsuite.model.TicketComment
import com.example.panalsuite.model.TicketStatus
import com.example.panalsuite.model.TicketCategory
import com.example.panalsuite.model.TicketPriority
import com.example.panalsuite.model.User
import com.example.panalsuite.model.UserRole
import com.example.panalsuite.network.userMessage
import com.example.panalsuite.repository.TicketRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

class TicketViewModel : ViewModel() {
    private val repository: TicketRepository = TicketRepository()

    private val _tickets = MutableStateFlow<List<Ticket>>(emptyList())
    val tickets: StateFlow<List<Ticket>> = _tickets.asStateFlow()

    fun loadTickets(){
        viewModelScope.launch{
            repository.getTickets()
                .onSuccess { _tickets.value = it }
                .onFailure { Log.e("TicketVM","Error al cargar tickets: ${it.message}") }
        }
    }

    fun getVisibleTickets(user: User): List<Ticket> = when {
        user.role.canSeeAllTickets -> _tickets.value
        user.role == UserRole.RESOLVER -> _tickets.value.filter { it.assignedId == user.id }
        else -> _tickets.value.filter { it.requesterId == user.id }
    }

    fun getTicketById(id: Int): Ticket? = _tickets.value.firstOrNull { it.id == id }

    fun updateTicket(ticket: Ticket) {
        _tickets.update { list ->
            if (list.any { it.id == ticket.id }) {
            list.map { if (it.id == ticket.id) ticket else it }
             } else {
                 list + ticket
            }
        }
    }

    fun closeTicket(ticketId: Int){
        viewModelScope.launch {
            repository.updateStatus(ticketId, TicketStatus.CLOSED)
                .onSuccess { updated ->
                    updateTicket(updated)
                }
                .onFailure { Log.e("TicketVM","No se pudo cerrar: ${ it.userMessage()}") }
        }
    }

    fun assignTicket(ticketId: Int, resolverId: String?) {
        viewModelScope.launch {
            repository.assignTicket(ticketId, resolverId?.toLong())
                .onSuccess { updated ->
                    updateTicket(updated)
                }
                .onFailure { Log.e("TicketVM","No se pudo asignar: ${ it.userMessage()}") }
        }
    }

    fun addComment(ticketId: Int, author: String, text: String) {
        if (text.isBlank()) return

        viewModelScope.launch {
            repository.addComent(ticketId, author, text)
                .onSuccess { updated ->
                    updateTicket(updated)
                }
                .onFailure { Log.e("TicketVM","No se pudo comentar: ${it.userMessage()}") }
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
        val requester = requesterId.toLongOrNull() ?: return

        viewModelScope.launch {
            repository.createTicket(title, description, location, category, priority, requester)
                .onSuccess { created ->
                    updateTicket(created)
                }
                .onFailure { Log.e("TicketVM","No se pudo crear: ${it.userMessage()}") }
        }
    }
}