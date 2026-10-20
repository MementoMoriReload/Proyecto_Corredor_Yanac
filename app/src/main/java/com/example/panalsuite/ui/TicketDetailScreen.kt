package com.example.panalsuite.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.panalsuite.model.Ticket
import com.example.panalsuite.model.TicketComment
import com.example.panalsuite.model.TicketStatus
import com.example.panalsuite.model.User
import com.example.panalsuite.model.UserRole
import com.example.panalsuite.viewmodel.MenuBarViewModel
import com.example.panalsuite.viewmodel.TicketViewModel

@Composable
fun TicketDetailScreen(
    ticket: Ticket?,
    user: User,
    users: List<User>,
    ticketViewModel: TicketViewModel,
    onBackClick: () -> Unit,
    onDashboardClick: () -> Unit = {},
    onManagementClick: () -> Unit = {},
    onRegisterClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    val tickets by ticketViewModel.tickets.collectAsState()
    val currentTicket = tickets.find { it.id == ticket?.id }

    if (currentTicket == null) {
        Scaffold(
            topBar = {
                AppTopBar(
                    title = "Detalle del ticket",
                    onBackClick = onBackClick
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("El ticket no existe")
            }
        }
    } else {
        TicketDetailScreenContent(
            ticket = currentTicket,
            user = user,
            users = users,
            onBackClick = onBackClick,
            onDashboardClick = onDashboardClick,
            onManagementClick = onManagementClick,
            onRegisterClick = onRegisterClick,
            onProfileClick = onProfileClick,
            onLogoutClick = onLogoutClick,
            onCloseTicket = { ticketViewModel.closeTicket(currentTicket.id) },
            onAddComment = { commentText ->
                ticketViewModel.addComment(
                    currentTicket.id,
                    user.name,
                    commentText
                )
            },
            onAssignResolver = { resolverId ->
                ticketViewModel.assignTicket(currentTicket.id, resolverId)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketDetailScreenContent(
    ticket: Ticket,
    user: User,
    users: List<User>,
    onBackClick: () -> Unit,
    onDashboardClick: () -> Unit,
    onManagementClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onProfileClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onCloseTicket: () -> Unit,
    onAddComment: (String) -> Unit,
    onAssignResolver: (String?) -> Unit
) {
    var commentText by remember { mutableStateOf("") }
    val resolvers = remember(users) { users.filter { it.role == UserRole.RESOLVER } }
    var resolversExpanded by remember { mutableStateOf(false) }

    val currentResolver = remember(ticket.assignedId, users) {
        users.find { it.id == ticket.assignedId }
    }

    val menuItems = remember(user.role) {
        MenuBarViewModel.MenuItems(
            userRole = user.role,
            onDashboardClick = onDashboardClick,
            onManagementClick = onManagementClick,
            onRegisterClick = onRegisterClick,
            onProfileClick = onProfileClick
        )
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Ticket #${ticket.id}",
                subtitle = "${user.name} · ${user.role.label}",
                actions = {
                    TopBarMenu(items = menuItems)
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = ticket.title,
                        style = MaterialTheme.typography.titleLarge
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Estado: ",
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
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Descripción",
                        style = MaterialTheme.typography.titleMedium
                    )
                    HorizontalDivider()
                    Text(
                        text = ticket.description.ifEmpty { "Sin descripción" },
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Información General",
                        style = MaterialTheme.typography.titleMedium
                    )
                    HorizontalDivider()

                    DetailRow(label = "Categoría", value = ticket.category.label)
                    DetailRow(label = "Ubicación", value = ticket.location)
                    DetailRow(label = "Solicitante ID", value = ticket.requesterId)
                    DetailRow(
                        label = "Resolutor asignado",
                        value = currentResolver?.name ?: "Sin asignar"
                    )
                    DetailRow(label = "Fecha de creación", value = ticket.createdAt)
                    DetailRow(label = "Última actualización", value = ticket.updatedAt)
                }
            }

            // Selector de Resolutor (Solo visible para Resolutores o Managers)
            if (user.role == UserRole.RESOLVER || user.role == UserRole.MANAGER) {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Asignar a resolutor",
                            style = MaterialTheme.typography.titleMedium
                        )
                        HorizontalDivider()

                        if (resolvers.isEmpty()) {
                            Text(
                                text = "No hay resolutores disponibles.",
                                style = MaterialTheme.typography.bodySmall
                            )
                        } else {
                            ExposedDropdownMenuBox(
                                expanded = resolversExpanded,
                                onExpandedChange = { resolversExpanded = !resolversExpanded }
                            ) {
                                OutlinedTextField(
                                    value = currentResolver?.name ?: "Seleccionar resolutor",
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Resolutor") },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(
                                            expanded = resolversExpanded
                                        )
                                    },
                                    modifier = Modifier
                                        .menuAnchor()
                                        .fillMaxWidth()
                                )

                                ExposedDropdownMenu(
                                    expanded = resolversExpanded,
                                    onDismissRequest = { resolversExpanded = false }
                                ) {
                                    resolvers.forEach { option ->
                                        DropdownMenuItem(
                                            text = { Text(option.name) },
                                            onClick = {
                                                onAssignResolver(option.id)
                                                resolversExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Sección de Comentarios y Acciones
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Comentarios (${ticket.comments.size})",
                        style = MaterialTheme.typography.titleMedium
                    )
                    HorizontalDivider()

                    if (ticket.comments.isEmpty()) {
                        Text(
                            text = "Aún no hay comentarios.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        ticket.comments.forEach { comment ->
                            CommentItem(comment = comment)
                        }
                    }

                    if (ticket.status != TicketStatus.CLOSED) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = commentText,
                                onValueChange = { commentText = it },
                                label = { Text("Escribe comentario") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            AnimatedVisibility(visible = commentText.isNotBlank()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    Button(
                                        onClick = {
                                            onAddComment(commentText)
                                            commentText = ""
                                        }
                                    ) {
                                        Text("Agregar comentario")
                                    }
                                }
                            }
                        }
                    }

                    if (user.role == UserRole.RESOLVER && ticket.status != TicketStatus.CLOSED) {
                        Spacer(Modifier.height(8.dp))
                        Button(
                            onClick = onCloseTicket,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error,
                                contentColor = MaterialTheme.colorScheme.onError
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Text("Cerrar ticket")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CommentItem(comment: TicketComment) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = comment.author,
                    style = MaterialTheme.typography.labelMedium
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = comment.text, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
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