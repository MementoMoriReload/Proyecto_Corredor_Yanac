package com.example.panalsuite.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
fun TicketDetailScreen(
    ticket: Ticket?,
    user: User,
    ticketViewModel: TicketViewModel,
    onBackClick: () -> Unit,
    onDashboardClick: () -> Unit = {},
    onManagementClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    val tickets by ticketViewModel.tickets.collectAsState()
    val ticket = tickets.find { it.id == ticket?.id }

    if (ticket == null){
        Scaffold(
            topBar = {
                AppTopBar(
                    title = "Detalle del ticket",
                    onBackClick = onBackClick
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("El ticket no existe o fue eliminado")
            }
        }
    } else {
        TicketDetailScreenContent(
            ticket = ticket,
            user = user,
            onBackClick = onBackClick,
            onDashboardClick = onDashboardClick,
            onManagementClick = onManagementClick,
            onLogoutClick = onLogoutClick
        )
    }
}

@Composable
fun TicketDetailScreenContent(
    ticket: Ticket,
    user: User,
    onBackClick: () -> Unit,
    onDashboardClick: () -> Unit,
    onManagementClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    val menuItems = remember(user.role) {
        MenuBarViewModel.MenuItems(
            userRole = user.role,
            onDashboardClick = onDashboardClick,
            onManagementClick = onManagementClick
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
    ){ padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ){
            Card(
                modifier = Modifier.fillMaxWidth()
            ){
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)

                ){
                    Text(
                        text = ticket.title,
                        style = MaterialTheme.typography.titleLarge
                    )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            Arrangement.SpaceBetween,
                            Alignment.CenterVertically
                        ){
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ){
                                Text(
                                    text= "Estado: ",
                                    style = MaterialTheme.typography.labelLarge
                                )
                                StatusBadge(status = ticket.status)
                            }

                            PriorityBadge(priority = ticket.priority)
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth()
            ){
                Column(
                    modifier = Modifier.padding(16.dp),
                    Arrangement.spacedBy(8.dp)
                ){
                    Text(
                        text = "Descripción",
                        style = MaterialTheme.typography.titleMedium
                    )
                    HorizontalDivider()
                    Text(
                        text = ticket.description.ifEmpty{
                            "Sin descripción"
                        },
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth()
            ){
                Column(
                    modifier = Modifier.padding(16.dp),
                    Arrangement.spacedBy(10.dp)
                ){
                    Text(
                        text= "Información General",
                        style = MaterialTheme.typography.titleMedium
                    )
                    HorizontalDivider()

                    DetailRow(label = "Categoría", value = ticket.category.label)
                    DetailRow(label = "Ubicación", value = ticket.location)
                    DetailRow(label = "Solicitante ID", value = ticket.requesterId)
                    DetailRow(label = "Asignado a", value = ticket.assignedId ?: "Sin asignar")
                    DetailRow(label = "Fecha de creación", value = ticket.createdAt)
                    DetailRow(label = "Última actualización", value = ticket.updatedAt)
                }
            }
        }

    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        Arrangement.SpaceBetween
    ){
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TicketDetailScreenPreview() {
    val sampleUser = User(
        id = "u1",
        name = "Victor Castro",
        email = "victor@panal.cl",
        department = null,
        role = UserRole.REQUESTER
    )

    val sampleTicket = Ticket(
        id = 101,
        title = "POS sin conexión en caja 2",
        description = "La máquina no imprime boletas y marca error de red al intentar procesar cobros.",
        category = TicketCategory.EXAMPLE,
        priority = TicketPriority.HIGH,
        status = TicketStatus.IN_PROGRESS,
        location = "Sucursal Centro",
        requesterId = "u1",
        assignedId = "u2",
        createdAt = "2026-09-01",
        updatedAt = "2026-09-02"
    )

    TicketDetailScreenContent(
        ticket = sampleTicket,
        user = sampleUser,
        onBackClick = {},
        onDashboardClick = {},
        onManagementClick = {},
        onLogoutClick= {}
    )
}
