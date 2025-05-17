package org.example.data.repository

import org.example.data.AuthProvider
import org.example.data.RemoteDataSource
import org.example.entity.UserEntity
import org.example.logic.repository.AuthenticationRepository
import org.example.logic.repository.UserRepository
import org.example.utils.PlanMateException
import org.example.utils.hasher.PasswordHasher

class AuthenticationRepositoryImpl(
    private val authenticationProvider: AuthProvider,
    private val userRepository: UserRepository,
    private val remoteDataSource: RemoteDataSource<UserEntity>,
    private val passwordHasher: PasswordHasher
) : AuthenticationRepository {

    override suspend fun login(username: String, password: String) {
        val user = userRepository.getUserByUsername(username)

        if (!isPasswordValid(user, password)) {
            throw PlanMateException.ValidationException("Password is not correct.")
        }
        authenticationProvider.addCurrentUser(user)
    }

    override suspend fun register(newUser: UserEntity, currentUser: UserEntity) {
        try {
            userRepository.getUserByUsername(newUser.username)
            throw PlanMateException.ValidationException("A user with that username already exists.")
        } catch (e: PlanMateException.ItemNotFoundException) {
            remoteDataSource.add(newUser)
        }
    }

    override suspend fun logout() {
        authenticationProvider.deleteCurrentUser()
    }

    override suspend fun getCurrentUser(): UserEntity? {
        return try {
            authenticationProvider.getCurrentUser()
        } catch (e: PlanMateException.ItemNotFoundException) {
            null
        }
    }

    private fun isPasswordValid(user: UserEntity, inputPassword: String): Boolean {
        return try {
            val hashedInput = passwordHasher.hash(inputPassword)
            user.password == hashedInput
        } catch (e: Exception) {
            false
        }
    }

}