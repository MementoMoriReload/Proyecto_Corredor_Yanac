package com.example.panalsuite.repository

import com.example.panalsuite.model.Ticket
import com.example.panalsuite.model.TicketCategory
import com.example.panalsuite.model.TicketComment
import com.example.panalsuite.model.TicketPriority
import com.example.panalsuite.model.TicketStatus

class TicketRepository {
    private val tickets = mutableListOf(
        Ticket(
            id = 1,
            title = "Devolución de productos.",
            description = "Cliente necesita devolver producto en tienda.",
            category = TicketCategory.CUSTOMER_SERVICE,
            priority = TicketPriority.HIGH,
            status = TicketStatus.OPEN,
            location = "ejemplo",
            requesterId = "u1",
            assignedId = "u2",
            comments = listOf(
                TicketComment(
                    id = 1,
                    author = "Victor Castro",
                    text = "Urgente, favor de resolver el problema.",
                    timestamp = "2026-09-01 10:15"
                )
            ),
            createdAt = "2026-09-01 10:00",
            updatedAt = "2026-09-01 10:15"
        ),
        Ticket(
            id = 2,
            title = "Stock descuadrado",
            description = "Diferencia entre las unidades del inventario físico con el stock del sistema.",
            category = TicketCategory.INVENTORY_STOCK,
            priority = TicketPriority.MEDIUM,
            status = TicketStatus.IN_PROGRESS,
            location = "ejemplo",
            requesterId = "u1",
            assignedId = "u2",
            createdAt = "2026-09-02 14:20",
            updatedAt = "2026-09-03 09:00"
        ),
        Ticket(
            id = 3,
            title = "Falla de conexión",
            description = "Sin internet ni comunicación en la caja 2.",
            category = TicketCategory.STORE_INFRASTRUCTURE,
            priority = TicketPriority.CRITICAL,
            status = TicketStatus.PENDING,
            location = "ejemplo",
            requesterId = "u1",
            assignedId = null,
            createdAt = "2026-09-03 11:45",
            updatedAt = "2026-09-03 11:45"
        )
    )

    fun getTickets(): List<Ticket> = tickets.toList()
}