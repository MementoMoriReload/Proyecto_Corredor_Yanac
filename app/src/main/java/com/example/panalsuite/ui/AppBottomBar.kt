// AppBottomBar.kt
package com.example.panalsuite.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Una opción del menú inferior.
 *
 * @param route          ruta a la que navega (o un identificador de acción, ej. "action_logout").
 * @param label          texto visible.
 * @param selectedRoutes rutas en las que esta opción se ve como seleccionada.
 *                       Por ejemplo, "Bandeja" también está activa en el detalle de un ticket.
 */
data class BottomBarItem(
    val route: String,
    val label: String,
    val selectedRoutes: List<String> = listOf(route)
)

/**
 * Menú inferior único de la app, solo texto (sin íconos).
 * No conoce el NavController: avisa el clic con onItemClick y alguien más navega.
 */
@Composable
fun AppBottomBar(
    items: List<BottomBarItem>,
    currentRoute: String?,
    onItemClick: (BottomBarItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding() // respeta la barra de gestos del teléfono
                .height(56.dp)
        ) {
            items.forEach { item ->
                BottomBarButton(
                    label = item.label,
                    selected = currentRoute in item.selectedRoutes,
                    onClick = { onItemClick(item) },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
            }
        }
    }
}

@Composable
private fun BottomBarButton(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val color = if (selected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Column(
        modifier = modifier.selectable(
            selected = selected,
            onClick = onClick,
            role = Role.Tab
        ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Barrita indicadora sobre la opción activa (reemplaza al ícono)
        Spacer(
            modifier = Modifier
                .width(32.dp)
                .height(3.dp)
                .background(
                    color = if (selected) color else Color.Transparent,
                    shape = RoundedCornerShape(50)
                )
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            color = color,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            maxLines = 1
        )
    }
}
