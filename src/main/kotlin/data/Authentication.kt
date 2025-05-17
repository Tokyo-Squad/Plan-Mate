package org.example.data

import org.example.data.remote.dto.UserDto

interface Authentication {
    suspend fun addCurrentUser(user: UserDto)
    suspend fun deleteCurrentUser()
    suspend fun getCurrentUser(): UserDto?
}