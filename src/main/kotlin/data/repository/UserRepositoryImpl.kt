package org.example.data.repository

import org.example.data.DataProvider
import org.example.entity.UserEntity
import org.example.logic.repository.UserRepository
import java.util.UUID

class UserRepositoryImpl(
    private val dataProvider: DataProvider<UserEntity>
) : UserRepository {

    override fun getUserByUsername(username: String): Result<UserEntity> = runCatching {
        dataProvider.get()
            .firstOrNull { it.username == username }
            ?: throw NoSuchElementException("User with username '$username' not found")
    }

    override fun getUserById(id: UUID): Result<UserEntity> = runCatching {
        dataProvider.getById(id)
            ?: throw NoSuchElementException("User with id $id not found")
    }

    override fun getUsers(): Result<List<UserEntity>> = runCatching { dataProvider.get() }

    override fun delete(id: UUID): Result<Unit> = runCatching { dataProvider.delete(id) }

    override fun update(user: UserEntity): Result<Unit> = runCatching { dataProvider.update(user) }
}