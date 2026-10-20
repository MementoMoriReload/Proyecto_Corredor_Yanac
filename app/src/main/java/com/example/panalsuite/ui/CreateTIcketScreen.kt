package com.example.panalsuite.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.panalsuite.model.TicketCategory
import com.example.panalsuite.model.TicketPriority
import com.example.panalsuite.model.User
import com.example.panalsuite.viewmodel.TicketViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTicketScreen(
    user: User,
    ticketViewModel: TicketViewModel,
    onBackClick: () -> Unit,
    onTicketCreated: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(TicketPriority.MEDIUM) }
    var category by remember { mutableStateOf(TicketCategory.INVENTORY_STOCK) }

    var showError by remember { mutableStateOf(false) }
    var priorityExpanded by remember { mutableStateOf(false) }
    var categoryExpanded by remember { mutableStateOf(false) }


    Scaffold(
        topBar = {
            AppTopBar(
                title = "Nuevo Ticket",
                onBackClick = onBackClick
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
            Text(
                text = "Crear solicitud de soporte",
                style = MaterialTheme.typography.titleLarge
            )

            // Campo Título
            OutlinedTextField(
                value = title,
                onValueChange = {
                    title = it
                    showError = false
                },
                label = { Text("Título *") },
                singleLine = true,
                isError = showError && title.isBlank(),
                modifier = Modifier.fillMaxWidth()
            )

            // Campo Descripción
            OutlinedTextField(
                value = description,
                onValueChange = {
                    description = it
                    showError = false
                },
                label = { Text("Descripción *") },
                minLines = 3,
                isError = showError && description.isBlank(),
                modifier = Modifier.fillMaxWidth()
            )

            // Campo Ubicación
            OutlinedTextField(
                value = location,
                onValueChange = {
                    location = it
                    showError = false
                },
                label = { Text("Ubicacion (Sucursal / Area) *") },
                singleLine = true,
                isError = showError && location.isBlank(),
                modifier = Modifier.fillMaxWidth()
            )

            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = { categoryExpanded = !categoryExpanded }
            ) {
                OutlinedTextField(
                    value = category.label,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoria") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false }
                ) {
                    TicketCategory.entries.forEach { categoryOption ->
                        DropdownMenuItem(
                            text = { Text(categoryOption.label) },
                            onClick = {
                                category = categoryOption
                                categoryExpanded = false
                            }
                        )
                    }
                }
            }

            // Desplegable de Prioridad
            ExposedDropdownMenuBox(
                expanded = priorityExpanded,
                onExpandedChange = { priorityExpanded = !priorityExpanded }
            ) {
                OutlinedTextField(
                    value = priority.label,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Prioridad") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = priorityExpanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = priorityExpanded,
                    onDismissRequest = { priorityExpanded = false }
                ) {
                    TicketPriority.entries.forEach { priorityOption ->
                        DropdownMenuItem(
                            text = { Text(priorityOption.label) },
                            onClick = {
                                priority = priorityOption
                                priorityExpanded = false
                            }
                        )
                    }
                }
            }

            // Animación para alerta de error
            AnimatedVisibility(
                visible = showError,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Text(
                    text = "Por favor completa todos los campos obligatorios.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Botón de Envío
            Button(
                onClick = {
                    if (title.isNotBlank() && description.isNotBlank() && location.isNotBlank()) {
                        ticketViewModel.createTicket(
                            title = title,
                            description = description,
                            category = category,
                            priority = priority,
                            location = location,
                            requesterId = user.id
                        )
                        onTicketCreated()
                    } else {
                        showError = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Crear Ticket")
            }
        }
    }
}
