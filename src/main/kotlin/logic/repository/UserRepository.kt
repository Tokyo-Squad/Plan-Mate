package org.example.logic.repository

import org.example.entity.UserEntity
import java.util.UUID

interface UserRepository {
    fun getUserByUsername(username: String): Result<UserEntity>
    fun getUserById(id: UUID): Result<UserEntity>
    fun getUsers(): Result<List<UserEntity>>
    fun delete(id: UUID): Result<Unit>
    fun update(user: UserEntity): Result<Unit>
}