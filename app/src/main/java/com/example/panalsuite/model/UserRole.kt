package com.example.panalsuite.model

enum class UserRole(val label: String){
    REQUESTER("Solicitante"),
    RESOLVER("Resolutor"),
    SUPERVISOR("Supervisor"),
    MANAGER("Gerente"),
    ADMINISTRATOR("Administrador")
}