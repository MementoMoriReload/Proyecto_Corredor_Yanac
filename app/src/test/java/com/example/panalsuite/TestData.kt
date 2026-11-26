package com.example.panalsuite

import com.example.panalsuite.model.Ticket
import com.example.panalsuite.model.TicketCategory
import com.example.panalsuite.model.TicketPriority
import com.example.panalsuite.model.TicketStatus
import com.example.panalsuite.model.UserRole
import com.example.panalsuite.network.CommentDTO
import com.example.panalsuite.network.TicketDTO
import com.example.panalsuite.network.UserDTO
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody

fun sampleUserDto(id: Long = 1L, role: UserRole = UserRole.REQUESTER) = UserDTO(
    id = id,
    name = "Victor Castro",
    email = "victor@panal.cl",
    department = null,
    role = role
)

fun sampleTicketDto(assignedId: Long = 2L) = TicketDTO(
    id = 5L,
    title = "Impresora",
    description = "No imprime",
    category = TicketCategory.INTERNAL_SUPPORT,
    priority = TicketPriority.HIGH,
    status = TicketStatus.OPEN,
    location = "Piso 1",
    requesterId = 1L,
    assignedId = assignedId,
    comments = listOf(
        CommentDTO(
            id = 9L,
            author = "Victor",
            text = "Hola",
            timestamp = "2026-11-26T14:30:15.123"
        )
    ),
    createdAt = "2026-11-26T14:26:03.456",
    updatedAt = "2026-11-26T14:29:59.000"
)

fun sampleTicket(id: Int = 1, requesterId: String = "1", assignedId: String? = null) = Ticket(
    id = id,
    title = "Impresora",
    description = "No imprime",
    category = TicketCategory.INTERNAL_SUPPORT,
    priority = TicketPriority.MEDIUM,
    status = TicketStatus.OPEN,
    location = "Piso 1",
    requesterId = requesterId,
    assignedId = assignedId,
    comments = emptyList(),
    createdAt = "2026-11-26 14:26",
    updatedAt = "2026-11-26 14:26"
)

// Cuerpo de error igual al que manda el backend: {"error": "mensaje"}
fun errorBody(message: String): ResponseBody =
    """{"error":"$message"}""".toResponseBody("application/json".toMediaType())