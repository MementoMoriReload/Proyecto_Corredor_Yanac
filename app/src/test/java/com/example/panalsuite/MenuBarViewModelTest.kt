package com.example.panalsuite

import com.example.panalsuite.model.UserRole
import com.example.panalsuite.viewmodel.MenuBarViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MenuBarViewModelTest {

    private fun labels(role: UserRole) =
        MenuBarViewModel.MenuItems(role).map { it.first }

    @Test
    fun requesterOnlySeesProfile() {
        assertEquals(listOf("Perfil"), labels(UserRole.REQUESTER))
    }

    @Test
    fun resolverAndSupervisorSeeProfileAndDashboard() {
        assertEquals(listOf("Perfil", "Dashboard"), labels(UserRole.RESOLVER))
        assertEquals(listOf("Perfil", "Dashboard"), labels(UserRole.SUPERVISOR))
    }

    @Test
    fun managerAlsoSeesUserManagement() {
        assertEquals(
            listOf("Perfil", "Dashboard", "Gestion de Usuario"),
            labels(UserRole.MANAGER)
        )
    }

    @Test
    fun adminSeesEveryOption() {
        assertEquals(
            listOf("Perfil", "Dashboard", "Gestión de Usuarios", "Registrar Usuario"),
            labels(UserRole.ADMIN)
        )
    }

    @Test
    fun menuItemsRunTheirCallbacks() {
        var profileClicked = false
        val items = MenuBarViewModel.MenuItems(
            userRole = UserRole.ADMIN,
            onProfileClick = { profileClicked = true }
        )

        items.first { it.first == "Perfil" }.second.invoke()

        assertTrue(profileClicked)
    }
}