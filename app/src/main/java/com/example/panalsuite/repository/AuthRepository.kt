package com.example.panalsuite.repository

import com.example.panalsuite.model.User
import com.example.panalsuite.model.UserRole

class AuthRepository{
    private val users = mutableListOf(
        User("u1", "Victor Castro", "victor@panal.cl",null, UserRole.REQUESTER),
        User("u2", "Martin Contreras", "martin@panal.cl",null, UserRole.RESOLVER),
        User("u3", "Marta Hernandez", "marta@panal.cl",null, UserRole.SUPERVISOR),
        User("u4", "Helena Vera", "helena@panal.cl",null , UserRole.MANAGER),
        User("u5", "Alex Silva", "admin@panal.cl", null,UserRole.ADMIN)
    )

    fun login(email: String, password: String): User? =
        users.firstOrNull { it.email == email.trim() && password == "1234" }
    fun register(name: String, email: String, password: String, role: UserRole): User?
    {
        if (findByEmail(email)!=null) return null

        val user = User(
            id = "u${users.size + 1}",
            name = name,
            email = email,
            department = null,
            role = role
        )
        users.add(user)
        return user
    }

    fun findByEmail(email: String): User? =
        users.firstOrNull { it.email == email.trim() }
}