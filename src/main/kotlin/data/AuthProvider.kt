package org.example.data

import org.example.entity.UserEntity

interface AuthProvider {
    fun addCurrentUser(user: UserEntity)
    fun deleteCurrentUser()
    fun getCurrentUser(): UserEntity
}