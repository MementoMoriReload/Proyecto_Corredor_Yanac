package com.example.panalsuite.viewmodel

import com.example.panalsuite.model.UserRole

object MenuBarViewModel {
    fun MenuItems(
        userRole: UserRole,
        onProfileClick: () -> Unit = {},
        onDashboardClick: () -> Unit = {},
        onManagementClick: () -> Unit = {},
        onLogoutClick: () -> Unit = {}
    ): List<Pair<String, () -> Unit>> {
        val items = mutableListOf<Pair<String, () -> Unit>>()

        items.add("Perfil" to onProfileClick)

        when (userRole) {
            UserRole.ADMIN -> {
                items.add("Dashboard" to onDashboardClick)
                items.add("Gestión de Usuarios" to onManagementClick)
            }

            UserRole.RESOLVER -> {
                items.add("Dashboard" to onDashboardClick)

            }

            //Placeholder
            UserRole.REQUESTER -> TODO()
            UserRole.SUPERVISOR -> TODO()
            UserRole.MANAGER -> TODO()
        }

            items.add("Cerrar sesión" to onLogoutClick)

                return items

        }
    }

