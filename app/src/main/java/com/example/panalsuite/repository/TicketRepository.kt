package com.example.panalsuite.repository

import com.example.panalsuite.model.Ticket
import com.example.panalsuite.model.TicketCategory
import com.example.panalsuite.model.TicketComment
import com.example.panalsuite.model.TicketPriority
import com.example.panalsuite.model.TicketStatus

class TicketRepository {
    fun getTickets(): List<Ticket> = listOf(
        Ticket(
            id = 1, title = "Sin conexión", description = "La caja 2 no imprime boletas",
            category = TicketCategory.EXAMPLE, priority = TicketPriority.HIGH,
            status = TicketStatus.OPEN, location = "ubicacion",
            requesterId = "u1", assignedId = "u2",
            comments = listOf(
                TicketComment(
                    1,
                    "Camila Rojas",
                    "Urgente, hay fila",
                    "2026-09-01 10:15"
                )
            ),
            createdAt = "2026-09-01", updatedAt = "2026-09-01"
        ),
        Ticket(
            id = 2, title = "Stock descuadrado", description = "Diferencia en inventario",
            category = TicketCategory.EXAMPLE, priority = TicketPriority.MEDIUM,
            status = TicketStatus.IN_PROGRESS, location = "ubicacion",
            requesterId = "u1", assignedId = "u2",
            createdAt = "2026-09-02", updatedAt = "2026-09-03"
        ),
        Ticket(
            id = 3, title = "Caída de red", description = "Sin internet en bodega",
            category = TicketCategory.EXAMPLE, priority = TicketPriority.CRITICAL,
            status = TicketStatus.PENDING, location = "ubicacion",
            requesterId = "u1", assignedId = null,
            createdAt = "2026-09-03", updatedAt = "2026-09-03"
        )
    )
}