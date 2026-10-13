package com.example.panalsuite.model

data class TicketComment(
    val id: Int,
    val author: String,
    val text: String,
    val timestamp: String
)

data class Ticket(
    val id: Int,
    val title: String,
    val description: String,
    val category: TicketCategory,
    val priority: TicketPriority,
    val status: TicketStatus,
    val location: String,
    val requesterId: String,
    val assignedId: String?,
    val comments: List<TicketComment> = emptyList(),
    val createdAt: String,
    val updatedAt: String
)
