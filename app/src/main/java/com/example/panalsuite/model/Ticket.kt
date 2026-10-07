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
    val requester: String,
    val assigned: String,
    val comment: List<TicketComment> = emptyList()
)
