package org.example.logic.repository

import org.example.entity.UserEntity

interface AuthenticationRepository {
    suspend fun login(username: String, password: String)
    suspend fun register(newUser: UserEntity)
    suspend fun logout()
    suspend fun getCurrentUser(): UserEntity
}