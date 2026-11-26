package com.example.panalsuite.repository

import com.example.panalsuite.model.Ticket
import com.example.panalsuite.model.TicketCategory
import com.example.panalsuite.model.TicketComment
import com.example.panalsuite.model.TicketPriority
import com.example.panalsuite.model.TicketStatus
import com.example.panalsuite.network.ApiClient
import com.example.panalsuite.network.ApiService
import com.example.panalsuite.network.AssignRequest
import com.example.panalsuite.network.CommentRequestDTO
import com.example.panalsuite.network.StatusRequest
import com.example.panalsuite.network.TicketRequestDTO
import com.example.panalsuite.network.bodyOrThrow
import com.example.panalsuite.network.toTicket
import com.example.panalsuite.network.requireSuccess

class TicketRepository(private val apiService: ApiService = ApiClient.service) {


    suspend fun getTickets(): Result<List<Ticket>> = runCatching {
        apiService.getTickets().map { it.toTicket() }
    }

    suspend fun createTicket(
        title: String,
        description: String,
        location: String,
        category: TicketCategory,
        priority: TicketPriority,
        requesterId: Long
    ): Result<Ticket> = runCatching {
        apiService.createTicket(
            TicketRequestDTO(
                title.trim(), description.trim(), location.trim(), category,
                priority, requesterId
            )
        ).bodyOrThrow().toTicket()
    }

    suspend fun addComent(ticketId: Int, author: String, text: String): Result<Ticket> =
        runCatching {
            apiService.createComment(ticketId.toLong(), CommentRequestDTO(author, text))
                .bodyOrThrow().toTicket()
        }

    suspend fun updateStatus(ticketId: Int, status: TicketStatus): Result<Ticket> = runCatching {
        apiService.updateTicketStatus(ticketId.toLong(), StatusRequest(status))
            .bodyOrThrow().toTicket()
    }

    suspend fun assignTicket(ticketId: Int, resoverId: Long?): Result<Ticket> = runCatching {
        apiService.assignTicket(ticketId.toLong(), AssignRequest(resoverId))
            .bodyOrThrow().toTicket()
    }

    suspend fun deleteTicket(ticketId: Int): Result<Unit> = runCatching {
        apiService.deleteTicket(ticketId.toLong()).requireSuccess()
    }
}