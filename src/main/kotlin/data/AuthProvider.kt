package org.example.data

import org.example.entity.UserEntity

interface AuthProvider {
    suspend fun addCurrentUser(user: UserEntity)
    suspend fun deleteCurrentUser()
    suspend fun getCurrentUser(): UserEntity
}