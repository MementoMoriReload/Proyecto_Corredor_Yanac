package com.example.panalsuite

import com.example.panalsuite.model.UserRole
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UserRoleTest {

    @Test
    fun onlyAdminCanCreateUsers() {
        assertTrue(UserRole.ADMIN.canCreateUser)
        assertFalse(UserRole.MANAGER.canCreateUser)
        assertFalse(UserRole.SUPERVISOR.canCreateUser)
        assertFalse(UserRole.RESOLVER.canCreateUser)
        assertFalse(UserRole.REQUESTER.canCreateUser)
    }

    @Test
    fun onlyRequesterAndAdminCanCreateTickets() {
        assertTrue(UserRole.REQUESTER.canCreateTicket)
        assertTrue(UserRole.ADMIN.canCreateTicket)
        assertFalse(UserRole.RESOLVER.canCreateTicket)
        assertFalse(UserRole.SUPERVISOR.canCreateTicket)
        assertFalse(UserRole.MANAGER.canCreateTicket)
    }

    @Test
    fun onlyAdminAndManagerCanManage() {
        assertTrue(UserRole.ADMIN.canManage)
        assertTrue(UserRole.MANAGER.canManage)
        assertFalse(UserRole.SUPERVISOR.canManage)
        assertFalse(UserRole.RESOLVER.canManage)
        assertFalse(UserRole.REQUESTER.canManage)
    }

    @Test
    fun dashboardIsForSupervisorManagerAndAdmin() {
        assertTrue(UserRole.SUPERVISOR.canSeeDashboard)
        assertTrue(UserRole.MANAGER.canSeeDashboard)
        assertTrue(UserRole.ADMIN.canSeeDashboard)
        assertFalse(UserRole.RESOLVER.canSeeDashboard)
        assertFalse(UserRole.REQUESTER.canSeeDashboard)
    }

    @Test
    fun requesterAndResolverCannotSeeAllTickets() {
        assertFalse(UserRole.REQUESTER.canSeeAllTickets)
        assertFalse(UserRole.RESOLVER.canSeeAllTickets)
        assertTrue(UserRole.SUPERVISOR.canSeeAllTickets)
        assertTrue(UserRole.MANAGER.canSeeAllTickets)
        assertTrue(UserRole.ADMIN.canSeeAllTickets)
    }
}