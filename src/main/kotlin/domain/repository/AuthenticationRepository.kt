package org.example.logic.repository

import org.example.entity.User

interface AuthenticationRepository {
    suspend fun login(username: String, password: String)
    suspend fun register(newUser: User)
    suspend fun logout()
    suspend fun getCurrentUser(): User
}