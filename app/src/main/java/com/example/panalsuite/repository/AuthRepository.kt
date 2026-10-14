package com.example.panalsuite.repository

import com.example.panalsuite.model.User
import com.example.panalsuite.model.UserRole

class AuthRepository{
    private val users = listOf(
        User("u1", "Victor Castro", "victor@panal.cl",null, UserRole.REQUESTER),
        User("u2", "Martin Contreras", "martin@panal.cl",null, UserRole.RESOLVER),
        User("u3", "Marta Hernandez", "marta@panal.cl",null, UserRole.SUPERVISOR),
        User("u4", "Helena Vera", "helena@panal.cl",null , UserRole.MANAGER),
        User("u5", "Alex Silva", "admin@panal.cl", null,UserRole.ADMINISTRATOR)
    )

    fun login(email: String, password: String): User? =
        users.firstOrNull { it.email == email.trim() && password == "1234" }
}