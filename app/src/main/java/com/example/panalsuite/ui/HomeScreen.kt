package com.example.panalsuite.ui

import android.annotation.SuppressLint
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
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.example.panalsuite.model.UserRole
import com.example.panalsuite.viewmodel.MenuBarViewModel
import com.example.panalsuite.viewmodel.TicketViewModel

@Composable
fun HomeScreen(
    user: User,
    ticketViewModel: TicketViewModel,
    onTicketClick: (Int) -> Unit,
    onDashboardClick: () -> Unit = {},
    onManagementClick: () -> Unit = {},
    onRegisterClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
    ) {
    val tickets by ticketViewModel.tickets.collectAsState()
    val visibleTickets = remember(tickets, user) { ticketViewModel.getVisibleTickets(user) }

    HomeScreenContent(
        user = user,
        tickets = visibleTickets,
        onTicketClick = onTicketClick,
        onDashboardClick = onDashboardClick,
        onManagementClick = onManagementClick,
        onRegisterClick = onRegisterClick,
        onProfileClick = onProfileClick,
        onLogoutClick = onLogoutClick
    )
}

@SuppressLint("RememberReturnType")
@Composable
fun HomeScreenContent(
    user: User,
    tickets: List<Ticket>,
    onTicketClick: (Int) -> Unit,
    onDashboardClick: () -> Unit = {},
    onManagementClick: () -> Unit = {},
    onRegisterClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    val menuItems = remember(user.role) {
        MenuBarViewModel.MenuItems(
            userRole = user.role,
            onDashboardClick = onDashboardClick,
            onManagementClick = onManagementClick,
            onRegisterClick = onRegisterClick,
            onProfileClick = onProfileClick,
            onLogoutClick = onLogoutClick
        )
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Bandeja de tickets",
                subtitle = "${user.name} · ${user.role.label}",
                actions = {
                    TopBarMenu(items = menuItems)
                }
            )
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
                modifier = Modifier.padding(padding),
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
            modifier = Modifier.padding(16.dp)
        ) {
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
    TicketStatus.OPEN -> Color(0xFFC62828)
    TicketStatus.IN_PROGRESS -> Color(0xFFEF6C00)
    TicketStatus.PENDING -> Color(0xFFF9A825)
    TicketStatus.RESOLVED -> Color(0xFF2E7D32)
    TicketStatus.CLOSED -> Color(0xFF2E7D32)
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenContentPreview() {
    val sampleUser = User(
        id = "u1",
        name = "Victor Castro",
        email = "victor@panal.cl",
        department = null,
        role = UserRole.REQUESTER
    )

    HomeScreenContent(
        user = sampleUser,
        tickets = listOf(
            Ticket(
                id = 1,
                title = "POS sin conexión",
                description = "La caja 2 no imprime boletas",
                category = TicketCategory.EXAMPLE,
                priority = TicketPriority.HIGH,
                status = TicketStatus.OPEN,
                location = "Sucursal Centro",
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
                location = "Bodega Sur",
                requesterId = "u1",
                assignedId = null,
                createdAt = "2026-09-03",
                updatedAt = "2026-09-03"
            )
        ),
        onTicketClick = {}
    )
}