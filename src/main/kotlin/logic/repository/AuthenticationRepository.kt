package org.example.logic.repository

import org.example.entity.UserEntity

interface AuthenticationRepository{
    fun login(username: String, password: String): Result<UserEntity>
    fun register(user: UserEntity, currentUser: UserEntity): Result<Unit>
    fun logout(): Result<Unit>
    fun getCurrentUser(): Result<UserEntity?>
}