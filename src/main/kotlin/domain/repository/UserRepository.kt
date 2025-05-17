package org.example.logic.repository

import org.example.entity.User
import java.util.*


interface UserRepository {
    suspend fun getUserByUsername(username: String): User
    suspend fun getUserById(id: UUID): User
    suspend fun getUsers(): List<User>
    suspend fun delete(id: UUID)
    suspend fun update(user: User):User
    suspend fun add(user: User)
}