package com.example.panalsuite.model

enum class UserRole(val label: String){
    REQUESTER("Solicitante"),
    RESOLVER("Resolutor"),
    SUPERVISOR("Supervisor"),
    MANAGER("Gerente"),
    ADMIN("Administrador");

    val canCreateUser: Boolean get() = this == ADMIN
    val canCreateTicket: Boolean get() = this == REQUESTER || this == ADMIN
    val canManage: Boolean get() = this == ADMIN || this == MANAGER
    val canSeeDashboard: Boolean get() = this == SUPERVISOR || this == MANAGER
    val canSeeAllTickets: Boolean get() = this != REQUESTER && this != RESOLVER
}