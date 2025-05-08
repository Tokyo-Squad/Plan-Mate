package org.example.data.repository

import org.example.data.DataProvider
import org.example.entity.UserEntity
import org.example.logic.repository.UserRepository
import org.example.utils.PlanMateException
import java.util.*


class UserRepositoryImpl(
    private val dataProvider: DataProvider<UserEntity>
) : UserRepository {

    override suspend fun getUserByUsername(username: String): UserEntity {
        return dataProvider.get()
            .firstOrNull { it.username == username }
            ?: throw PlanMateException.ItemNotFoundException("User with username '$username' not found")
    }

    override suspend fun getUserById(id: UUID): UserEntity {
        return dataProvider.getById(id)
            ?: throw PlanMateException.ItemNotFoundException("User with id $id not found")
    }

    override suspend fun getUsers(): List<UserEntity> {
        return dataProvider.get()
    }

    override suspend fun delete(id: UUID) {
        dataProvider.delete(id)
    }

    override suspend fun update(user: UserEntity) {
        dataProvider.update(user)
    }

    override suspend fun add(user: UserEntity) {
        dataProvider.add(user)
    }
}