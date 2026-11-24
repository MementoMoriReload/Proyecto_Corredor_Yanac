package com.example.panalsuite.repository

import com.example.panalsuite.model.User
import com.example.panalsuite.model.UserRole
import com.example.panalsuite.network.ApiClient
import com.example.panalsuite.network.ApiService
import com.example.panalsuite.network.LoginRequest
import com.example.panalsuite.network.RoleRequest
import com.example.panalsuite.network.UserRequestDTO
import com.example.panalsuite.network.bodyOrThrow
import com.example.panalsuite.network.toUser

class AuthRepository( private val apiService: ApiService = ApiClient.service) {
    suspend fun login(email: String, password: String): Result<User> = runCatching {
        apiService.login(LoginRequest(email.trim(), password)).bodyOrThrow().toUser()
    }

    suspend fun getUsers(): Result<List<User>> = runCatching {
        apiService.getUsers().map { it.toUser() }
    }
    suspend fun register(name: String, email: String, password: String, role: UserRole): Result<User> = runCatching {
       apiService.createUser(UserRequestDTO(name.trim(), email.trim(), password, role))
           .bodyOrThrow().toUser()
    }

    suspend fun updateUserRole(userId: String, newRole: UserRole): Result<User> = runCatching {
        val id = userId.toLongOrNull() ?: throw IllegalArgumentException("Invalid user ID")
        apiService.updateUserRole(id, RoleRequest(newRole))
            .bodyOrThrow().toUser()
    }
}