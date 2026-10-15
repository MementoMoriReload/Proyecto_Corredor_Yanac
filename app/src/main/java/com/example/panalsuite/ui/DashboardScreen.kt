package com.example.panalsuite.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.panalsuite.model.TicketPriority
import com.example.panalsuite.model.TicketStatus
import com.example.panalsuite.model.User
import com.example.panalsuite.viewmodel.TicketViewModel

@Composable
fun DashboardScreen(
    user: User,
    ticketViewModel: TicketViewModel,
    onBackClick: (() -> Unit)? = null
) {
    val tickets by ticketViewModel.tickets.collectAsState()

    // Cálculo dinámico de métricas e indicadores
    val totalTickets = tickets.size
    val openTickets = tickets.count { it.status == TicketStatus.OPEN }
    val inProgressTickets = tickets.count { it.status == TicketStatus.IN_PROGRESS }
    val pendingTickets = tickets.count { it.status == TicketStatus.PENDING }
    val resolvedTickets = tickets.count { it.status == TicketStatus.RESOLVED || it.status == TicketStatus.CLOSED }
    val criticalTickets = tickets.count { it.priority == TicketPriority.CRITICAL }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Resumen de Gestión",
                subtitle = "${user.name} · ${user.role.label}",
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
                text = "Indicadores Principales (KPIs)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            // Tarjetas de métricas superiores
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    title = "Total Tickets",
                    value = totalTickets.toString(),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "En Curso",
                    value = (openTickets + inProgressTickets + pendingTickets).toString(),
                    color = Color(0xFFFFF3E0),
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    title = "Resueltos",
                    value = resolvedTickets.toString(),
                    color = Color(0xFFE8F5E9),
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Críticos",
                    value = criticalTickets.toString(),
                    color = Color(0xFFFFEBEE),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Sección: Distribución por Estado (Barras de progreso)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Estado de Solicitudes",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    HorizontalDivider()

                    StatusProgressRow(label = "Abiertos", count = openTickets, total = totalTickets)
                    StatusProgressRow(label = "En Progreso", count = inProgressTickets, total = totalTickets)
                    StatusProgressRow(label = "Pendientes", count = pendingTickets, total = totalTickets)
                    StatusProgressRow(label = "Resueltos / Cerrados", count = resolvedTickets, total = totalTickets)
                }
            }

            // Sección: Distribución por Prioridad
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Distribución por Prioridad",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    HorizontalDivider()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        PriorityBadgeCount(priority = TicketPriority.LOW, count = tickets.count { it.priority == TicketPriority.LOW })
                        PriorityBadgeCount(priority = TicketPriority.MEDIUM, count = tickets.count { it.priority == TicketPriority.MEDIUM })
                        PriorityBadgeCount(priority = TicketPriority.HIGH, count = tickets.count { it.priority == TicketPriority.HIGH })
                        PriorityBadgeCount(priority = TicketPriority.CRITICAL, count = criticalTickets)
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = color,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun StatusProgressRow(
    label: String,
    count: Int,
    total: Int
) {
    val progress = if (total > 0) count.toFloat() / total.toFloat() else 0f

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, style = MaterialTheme.typography.bodyMedium)
            Text(text = "$count (${(progress * 100).toInt()}%)", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        }
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surface
        )
    }
}

@Composable
private fun PriorityBadgeCount(
    priority: TicketPriority,
    count: Int
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        PriorityBadge(priority = priority)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "$count",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}