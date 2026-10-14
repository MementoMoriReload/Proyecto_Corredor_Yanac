package com.example.panalsuite.model

enum class UserRole(val label: String){
    REQUESTER("Solicitante"),
    RESOLVER("Resolutor"),
    SUPERVISOR("Supervisor"),
    MANAGER("Gerente"),
    ADMIN("Administrador");

    val canCreateTicket: Boolean get() = this == REQUESTER || this == ADMIN
    val canChangeStatus: Boolean get() = this == RESOLVER || this == SUPERVISOR
    val canComment: Boolean get() = this != MANAGER
    val canSeeDashboard: Boolean get() = this == SUPERVISOR || this == MANAGER
    val canSeeAllTickets: Boolean get() = this != REQUESTER && this != RESOLVER
}