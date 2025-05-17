package org.example.data.repository

import org.example.data.RemoteDataSource
import org.example.entity.UserEntity
import org.example.logic.repository.UserRepository
import org.example.utils.PlanMateException
import java.util.*


class UserRepositoryImpl(
    private val remoteDataSource: RemoteDataSource<UserEntity>
) : UserRepository {

    override suspend fun getUserByUsername(username: String): UserEntity {
        return remoteDataSource.get()
            .firstOrNull { it.username == username }
            ?: throw PlanMateException.ItemNotFoundException("User with username '$username' not found")
    }

    override suspend fun getUserById(id: UUID): UserEntity {
        return remoteDataSource.getById(id)
            ?: throw PlanMateException.ItemNotFoundException("User with id $id not found")
    }

    override suspend fun getUsers(): List<UserEntity> {
        return remoteDataSource.get()
    }

    override suspend fun delete(id: UUID) {
        remoteDataSource.delete(id)
    }

    override suspend fun update(user: UserEntity) {
        remoteDataSource.update(user)
    }

    override suspend fun add(user: UserEntity) {
        remoteDataSource.add(user)
    }
}