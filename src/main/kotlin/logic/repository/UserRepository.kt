package org.example.logic.repository

import org.example.entity.UserEntity
import java.util.*


interface UserRepository {
    suspend fun getUserByUsername(username: String): UserEntity
    suspend fun getUserById(id: UUID): UserEntity
    suspend fun getUsers(): List<UserEntity>
    suspend fun delete(id: UUID)
    suspend fun update(user: UserEntity):UserEntity
    suspend fun add(user: UserEntity)
}