package org.example.data.repository

import org.example.data.RemoteDataSource
import org.example.data.remote.dto.UserDto
import org.example.data.util.exception.DatabaseException
import org.example.data.util.mapper.toUserDto
import org.example.data.util.mapper.toUserEntity
import org.example.entity.User
import org.example.logic.repository.UserRepository
import java.util.UUID

class UserRepositoryImpl(
    private val remoteDataSource: RemoteDataSource<UserDto>
) : UserRepository {

    override suspend fun getUserByUsername(username: String): User =
        remoteDataSource.get()
            .firstOrNull { it.username == username }?.toUserEntity()
            ?: throw DatabaseException.DatabaseItemNotFoundException("User with username '$username' not found")


    override suspend fun getUserById(id: UUID): User =
        remoteDataSource.getById(id)?.toUserEntity()
            ?: throw DatabaseException.DatabaseItemNotFoundException("User with id $id not found")


    override suspend fun getUsers(): List<User> = remoteDataSource.get().map { it.toUserEntity() }

    override suspend fun delete(id: UUID) {
        remoteDataSource.delete(id)
    }

    override suspend fun update(user: User): User {
        remoteDataSource.update(user.toUserDto())
        return user
    }

    override suspend fun add(user: User) {
        remoteDataSource.add(user.toUserDto())
    }
}