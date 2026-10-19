package com.example.panalsuite.viewmodel

import com.example.panalsuite.model.UserRole

object MenuBarViewModel {
    fun MenuItems(
        userRole: UserRole,
        onDashboardClick: () -> Unit = {},
        onManagementClick: () -> Unit = {},
        onRegisterClick: () -> Unit = {},
        onProfileClick: () -> Unit = {},
        onLogoutClick: () -> Unit = {},
    ): List<Pair<String, () -> Unit>> {
        val items = mutableListOf<Pair<String, () -> Unit>>()

        when (userRole) {
            UserRole.ADMIN -> {
                items.add("Dashboard" to onDashboardClick)
                items.add("Gestión de Usuarios" to onManagementClick)
                items.add("Registrar Usuario" to onRegisterClick)
            }

            UserRole.RESOLVER, UserRole.SUPERVISOR, UserRole.MANAGER -> {
                items.add("Dashboard" to onDashboardClick)
                if (userRole == UserRole.MANAGER) items.add("Gestion de Usuario" to onManagementClick)
            }

            UserRole.REQUESTER -> { }
        }

        items.add("Perfil" to onProfileClick)
        items.add("Cerrar sesíon" to onLogoutClick)

        return items
    }
}