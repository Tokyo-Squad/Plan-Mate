package org.example.data.repository

import org.example.data.Authentication
import org.example.data.RemoteDataSource
import org.example.data.remote.dto.UserDto
import org.example.data.util.exception.AuthenticationException
import org.example.data.util.exception.DatabaseException
import org.example.data.util.mapper.toUserDto
import org.example.data.util.mapper.toUserEntity
import org.example.entity.User
import org.example.logic.repository.AuthenticationRepository
import org.example.logic.repository.UserRepository
import org.example.utils.hasher.PasswordHasher

class AuthenticationRepositoryImpl(
    private val authProvider: Authentication,
    private val userRepository: UserRepository,
    private val remoteDataSource: RemoteDataSource<UserDto>,
    private val hasher: PasswordHasher
) : AuthenticationRepository {

    override suspend fun login(username: String, password: String) {
        val user = runCatching { userRepository.getUserByUsername(username) }
            .getOrElse { e ->
                if (e is DatabaseException.DatabaseItemNotFoundException) {
                    throw AuthenticationException.UserNotFound()
                }
                throw e
            }

        if (!isPasswordValid(user.password, password)) {
            throw AuthenticationException.InvalidCredentials()
        }

        authProvider.addCurrentUser(user.toUserDto())
    }

    override suspend fun register(newUser: User) {
        runCatching { userRepository.getUserByUsername(newUser.username) }
            .onSuccess {
                throw AuthenticationException.UserAlreadyExists()
            }.onFailure { e ->
                if (e !is DatabaseException.DatabaseItemNotFoundException) throw e
            }

        remoteDataSource.add(newUser.toUserDto())
    }

    override suspend fun logout() {
        authProvider.deleteCurrentUser()
    }

    override suspend fun getCurrentUser(): User =
        authProvider.getCurrentUser()?.toUserEntity() ?: throw AuthenticationException.NoCurrentUser()

    private fun isPasswordValid(storedHashed: String, rawInput: String): Boolean =
        storedHashed == hasher.hash(rawInput)
}