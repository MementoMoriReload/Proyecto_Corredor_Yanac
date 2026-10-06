package com.example.panalsuite.model

sealed class User (
    val id: String,
    val name: String,
    val email: String,
    val role: UserRole
)

data class RequesterUser(
    val requesterId: String,
    val requesterName: String,
    val requesterEmail: String,
    val department: String,
    val branchOffice: String
): User(requesterId, requesterName, requesterEmail,UserRole.REQUESTER){
}

data class ResolverUser(
    val resolverId: String,
    val resolverName: String,
    val resolverEmail: String
): User(resolverId, resolverName, resolverEmail,UserRole.RESOLVER){
}

data class SupervisorUser(
    val supervisorId: String,
    val supervisorName: String,
    val supervisorEmail: String
): User(supervisorId, supervisorName, supervisorEmail,UserRole.SUPERVISOR){
}

data class AdminUser(
    val adminId: String,
    val adminName: String,
    val adminEmail: String
): User(adminId, adminName, adminEmail,UserRole.ADMINISTRATOR){
}
