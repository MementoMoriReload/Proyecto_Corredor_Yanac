package com.example.panalsuite.repository

import com.example.panalsuite.model.User
import com.example.panalsuite.model.UserRole
import com.example.panalsuite.network.ApiClient
import com.example.panalsuite.network.ApiService
import com.example.panalsuite.network.LoginRequest

class AuthRepository( private val apiService: ApiService = ApiClient.service) {
    private val users = mutableListOf(
        User("u1", "Victor Castro", "victor@panal.cl", null, UserRole.REQUESTER),
        User("u2", "Martin Contreras", "martin@panal.cl", null, UserRole.RESOLVER),
        User("u3", "Marta Hernandez", "marta@panal.cl", null, UserRole.SUPERVISOR),
        User("u4", "Helena Vera", "helena@panal.cl", null, UserRole.MANAGER),
        User("u5", "Alex Silva", "admin@panal.cl", null, UserRole.ADMIN)
    )

    private val passwords = mutableMapOf(
        "u1" to "1234",
        "u2" to "1234",
        "u3" to "1234",
        "u4" to "1234",
        "u5" to "1234"
    )

    suspend fun Apilogin(email: String, password: String): User? {
        val cleanEmail = email.trim()

        return try {
            val response = apiService.login(LoginRequest(cleanEmail, password))

            if (response.isSuccessful && response.body() != null) {
                response.body()
            } else {
                login(cleanEmail, password)
            }
        } catch (e: Exception) {
            login(cleanEmail, password)
        }
    }


    private fun login(email: String, password: String): User? {
        val user = findByEmail(email)?: return null
        return if (passwords[user.id] == password) user else null
    }
    fun register(name: String, email: String, password: String, role: UserRole): User? {
        if (findByEmail(email)!=null) return null

        val user = User(
            id = "u${users.size + 1}",
            name = name,
            email = email,
            department = null,
            role = role
        )
        users.add(user)
        passwords[user.id] = password
        return user
    }

    fun findByEmail(email: String): User? =
        users.firstOrNull { it.email == email.trim() }

    fun getUsers(): List<User> = users.toList()

    fun updateUserRole(userId: String, newRole: UserRole): Boolean {
        val index = users.indexOfFirst { it.id == userId }
        return if (index != -1) {
            users[index] = users[index].copy(role = newRole)
            true
        } else {
            false
        }
    }
}