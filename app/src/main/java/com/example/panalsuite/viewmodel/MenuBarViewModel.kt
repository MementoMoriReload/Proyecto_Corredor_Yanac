package com.example.panalsuite.viewmodel

import com.example.panalsuite.model.UserRole

object MenuBarViewModel {
    fun MenuItems(
        userRole: UserRole,
        onProfileClick: () -> Unit = {},
        onDashboardClick: () -> Unit = {},
        onManagementClick: () -> Unit = {},
        onRegisterClick: () -> Unit = {},
        onLogoutClick: () -> Unit = {}
    ): List<Pair<String, () -> Unit>> {
        val items = mutableListOf<Pair<String, () -> Unit>>()

        items.add("Perfil" to onProfileClick)

        when (userRole) {
            UserRole.ADMIN -> {
                items.add("Dashboard" to onDashboardClick)
                items.add("Gestión de Usuarios" to onManagementClick)
                items.add("Registrar Usuario" to onRegisterClick)
            }

            UserRole.RESOLVER, UserRole.SUPERVISOR, UserRole.MANAGER -> {
                items.add("Dashboard" to onDashboardClick)
            }

            UserRole.REQUESTER -> {
                // El solicitante solo mantiene las opciones por defecto
            }
        }

        items.add("Cerrar sesión" to onLogoutClick)

        return items
    }
}