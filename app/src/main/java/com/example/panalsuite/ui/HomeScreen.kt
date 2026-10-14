package com.example.panalsuite.ui

import android.R.attr.text
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.panalsuite.model.Ticket
import com.example.panalsuite.model.TicketCategory
import com.example.panalsuite.model.TicketPriority
import com.example.panalsuite.model.TicketStatus
import com.example.panalsuite.model.User
import com.example.panalsuite.viewmodel.TicketViewModel

@Composable
fun HomeScreen(
    user: User,
    ticketViewModel: TicketViewModel,
    onTicketClick: (Int) -> Unit,
    onCreateTicket: () -> Unit,
    onOpenDashboard: () -> Unit,
    onLogout: () -> Unit
) {
    val tickets by ticketViewModel.tickets.collectAsState()
    val visibleTickets = remember(tickets, user) { ticketViewModel.getVisibleTickets(user) }

    HomeScreenContent(
        userName = user.name,
        roleLabel = user.role.label,
        tickets = visibleTickets,
        canCreateTicket = user.role.canCreateTicket,
        canSeeDashboard = user.role.canSeeDashboard,
        onTicketClick = onTicketClick,
        onCreateTicket = onCreateTicket,
        onOpenDashboard = onOpenDashboard,
        onLogout = onLogout
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    userName: String,
    roleLabel: String,
    tickets: List<Ticket>,
    canCreateTicket: Boolean,
    canSeeDashboard: Boolean,
    onTicketClick: (Int) -> Unit,
    onCreateTicket: () -> Unit,
    onOpenDashboard: () -> Unit,
    onLogout: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PanelSuite") },
                actions = {
                    if (canSeeDashboard) {
                        TextButton(
                            onClick = onOpenDashboard,
                            colors = ButtonDefaults.textButtonColors(contentColor = Color.White)
                        ) {
                            Text("Resumen")
                        }
                    }
                    TextButton(
                        onClick = onLogout,
                        colors = ButtonDefaults.textButtonColors(contentColor = Color.White)
                    ) {
                        Text("Salir")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF16365D),
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            if (canCreateTicket) {
                ExtendedFloatingActionButton(
                    onClick = onCreateTicket
                )
                { Text("Nuevo ticket") }

            }
        }
    ) { padding ->
        if (tickets.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay tickets para mostrar")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(tickets, key = { it.id }) { ticket ->
                    TicketCard(
                        ticket = ticket,
                        onClick = { onTicketClick(ticket.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun TicketCard(
    ticket: Ticket,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = ticket.title,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "${ticket.category.label} · ${ticket.location}",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(2.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = "Estado: ",
                    style = MaterialTheme.typography.labelLarge
                )
                StatusBadge(status = ticket.status)
            }
            Text(
                text = "Actualizado: ${ticket.updatedAt}",
                style = MaterialTheme.typography.labelLarge
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                PriorityBadge(priority = ticket.priority)
            }
        }
    }
}

@Composable
fun PriorityBadge(priority: TicketPriority) {
    val pcolor = priorityColor(priority)
    Surface(
        color = pcolor.copy(alpha = 0.50f),
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(
            text = priority.label,
            color = Color.Black,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun StatusBadge(status: TicketStatus) {
    val scolor = statusColor(status)
    Surface(
        color = scolor.copy(alpha = 0.90f),
        shape = RoundedCornerShape(10)
    ) {
        Text(
            text = status.label,
            color = Color.Black,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

private fun priorityColor(priority: TicketPriority): Color = when (priority) {
    TicketPriority.LOW -> Color(0xFF2E7D32)
    TicketPriority.MEDIUM -> Color(0xFFF9A825)
    TicketPriority.HIGH -> Color(0xFFEF6C00)
    TicketPriority.CRITICAL -> Color(0xFFC62828)
}

private fun statusColor(status: TicketStatus): Color = when (status) {
    TicketStatus.OPEN -> Color(0xFF2E7D32)
    TicketStatus.IN_PROGRESS -> Color(0xFFF9A825)
    TicketStatus.PENDING -> Color(0xFFEF6C00)
    TicketStatus.RESOLVED -> Color(0xFFC62828)
    TicketStatus.CLOSED -> Color(0xFFC62828)

}

@Preview(showBackground = true)
@Composable
private fun HomeScreenContentPreview() {
    HomeScreenContent(
        userName = "Camila Rojas",
        roleLabel = "Solicitante",
        tickets = listOf(
            Ticket(
                id = 1,
                title = "POS sin conexión",
                description = "La caja 2 no imprime boletas",
                category = TicketCategory.EXAMPLE,
                priority = TicketPriority.HIGH,
                status = TicketStatus.OPEN,
                location = "ubicacion",
                requesterId = "u1",
                assignedId = "u2",
                createdAt = "2026-09-01",
                updatedAt = "2026-09-01"
            ),
            Ticket(
                id = 2,
                title = "Caída de red",
                description = "Sin internet en bodega",
                category = TicketCategory.EXAMPLE,
                priority = TicketPriority.CRITICAL,
                status = TicketStatus.PENDING,
                location = "ubicacion",
                requesterId = "u1",
                assignedId = null,
                createdAt = "2026-09-03",
                updatedAt = "2026-09-03"
            ),
            Ticket(
                id = 3,
                title = "Stock descuadrado",
                description = "Sin internet en bodega",
                category = TicketCategory.EXAMPLE,
                priority = TicketPriority.MEDIUM,
                status = TicketStatus.IN_PROGRESS,
                location = "ubicacion",
                requesterId = "u1",
                assignedId = "u2",
                createdAt = "2026-09-02",
                updatedAt = "2026-09-03"
            )

        ),
        canCreateTicket = true,
        canSeeDashboard = false,
        onTicketClick = {},
        onCreateTicket = {},
        onOpenDashboard = {},
        onLogout = {}
    )
}