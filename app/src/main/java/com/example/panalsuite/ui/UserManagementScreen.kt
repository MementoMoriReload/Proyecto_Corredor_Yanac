package com.example.panalsuite.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.panalsuite.model.User
import com.example.panalsuite.model.UserRole
import com.example.panalsuite.ui.AppTopBar

@Composable
fun UserManagementScreen(
    user: User,
    userList: List<User>,
    onBackClick: () -> Unit,
    onRoleChange: (User, UserRole) -> Unit = { _, _ -> }
    ) {
        UserManagementScreenContent(
        canManage = user.role.canManage,
        userList = userList,
        onBackClick = onBackClick,
        onRoleChange = onRoleChange,
    )
}

@Composable

fun UserManagementScreenContent(
    canManage: Boolean,
    userList: List<User>,
    onBackClick: () -> Unit,
    onRoleChange: (User, UserRole) -> Unit = { _, _ -> }
){
    Scaffold(
        topBar = { AppTopBar( title = "Gestión de Usuarios",
                subtitle = "${userList.size} usuarios registrados",
                onBackClick = onBackClick) })
    { padding ->
        if (!canManage) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No tienes permisos para gestionar usuarios")
            }
        } else if (userList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay usuarios registrados")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(userList, key = { it.id }) { item ->
                    UserCard(
                        user = item,
                        onRoleChanged = { new -> onRoleChange(item, new) }
                    )
                }
            }
        }
    }
}

@Composable
fun UserCard(
    user: User,
    onRoleChanged: (UserRole) -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }
    var pendingRoleChange by remember { mutableStateOf<UserRole?>(null) }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = user.email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (!user.department.isNullOrBlank()) {
                    Text(
                        text = "Depto: ${user.department}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                RoleBadge(role = user.role)
            }

            Box {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Opciones de usuario"
                    )
                }

                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false }
                ) {
                    Text(
                        text = " Cambiar rol:",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                    UserRole.entries.forEach { roleOption ->
                        DropdownMenuItem(
                            text = { Text(roleOption.label) },
                            onClick = {
                                menuExpanded = false
                                pendingRoleChange = roleOption
                            },
                            enabled = roleOption != user.role
                        )
                    }
                }
            }
        }
    }

    pendingRoleChange?.let { newRole ->
        AlertDialog(
            onDismissRequest = { pendingRoleChange = null },
            title = { Text("Confirmar cambio de rol") },
            text = { Text("¿Estás seguro de que deseas cambiar el rol de ${user.name} a ${newRole.label}?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onRoleChanged(newRole)
                        pendingRoleChange = null
                    }
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { pendingRoleChange = null }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun RoleBadge(role: UserRole){
    val targetColor = when (role) {
        UserRole.REQUESTER -> Color(0xFF757575)
        UserRole.RESOLVER -> Color(0xFF388E3C)
        UserRole.SUPERVISOR -> Color(0xFFFDBE02)
        UserRole.MANAGER -> Color(0xFF3737E7)
        UserRole.ADMIN -> Color(0xFF000080)
    }

    val animatedBackgroundColor by animateColorAsState(
        targetValue = targetColor,
        animationSpec = tween(durationMillis = 350),
        label = "RoleBadgeColor"
    )

    Surface(
        color = animatedBackgroundColor,
        shape = RoundedCornerShape(21.dp)
    ) {
        Text(
            text = role.label,
            color = Color.White,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun UserManagementPreview(){
     val users = listOf(
        User("u1", "Victor Castro", "victor@panal.cl",null, UserRole.REQUESTER),
        User("u2", "Martin Contreras", "martin@panal.cl",null, UserRole.RESOLVER),
        User("u3", "Marta Hernandez", "marta@panal.cl",null, UserRole.SUPERVISOR),
        User("u4", "Helena Vera", "helena@panal.cl",null , UserRole.MANAGER),
        User("u5", "Alex Silva", "admin@panal.cl", null,UserRole.ADMIN)
    )

    UserManagementScreenContent(
        canManage = true,
        userList = users,
        onBackClick = {},
        onRoleChange = {_, _ ->}
    )
}

