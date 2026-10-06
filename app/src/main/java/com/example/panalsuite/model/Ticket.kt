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
    val category: TicketCategory,
    val priority: TicketPriority,
    val status: TicketStatus,
    val location: String,
    val requester: Int,
    val assigned: Int,
    val comment: TicketComment
)
