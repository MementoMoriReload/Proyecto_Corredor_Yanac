package com.example.panalsuite.viewmodel

import com.example.panalsuite.model.UserRole

object MenuBarViewModel {
    fun MenuItems(
        userRole: UserRole,
        onDashboardClick: () -> Unit = {},
        onManagementClick: () -> Unit = {},
        onRegisterClick: () -> Unit = {},
    ): List<Pair<String, () -> Unit>> {
        val items = mutableListOf<Pair<String, () -> Unit>>()

        when (userRole) {
            UserRole.ADMIN -> {
                items.add("Dashboard" to onDashboardClick)
                items.add("Gestión de Usuarios" to onManagementClick)
                items.add("Registrar Usuario" to onRegisterClick)
            }

            UserRole.RESOLVER, UserRole.SUPERVISOR -> {
                items.add("Dashboard" to onDashboardClick)
            }

            UserRole.MANAGER -> {
                items.add("Dashboard" to onDashboardClick)
                items.add("Gestión de Usuarios" to onManagementClick)

            }

            UserRole.REQUESTER -> {
                // El solicitante solo mantiene las opciones por defecto
            }
        }

        return items
    }
}