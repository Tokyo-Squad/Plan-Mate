package org.example.data.repository

import org.example.data.AuthProvider
import org.example.data.DataProvider
import org.example.entity.UserEntity
import org.example.logic.repository.AuthenticationRepository
import org.example.logic.repository.UserRepository
import org.example.utils.PlanMateException

class AuthenticationRepositoryImpl(
    private val authenticationProvider: AuthProvider,
    private val userRepository: UserRepository,
    private val dataProvider: DataProvider<UserEntity>
) : AuthenticationRepository {

    override suspend fun login(username: String, password: String) {
        val user = userRepository.getUserByUsername(username)

        if (user.password != password) {
            throw PlanMateException.ValidationException("Password is not correct.")
        }

        authenticationProvider.addCurrentUser(user)
    }

    override suspend fun register(newUser: UserEntity, currentUser: UserEntity) {
        try {
            userRepository.getUserByUsername(newUser.username)
            throw PlanMateException.ValidationException("A user with that username already exists.")
        } catch (e: PlanMateException.ItemNotFoundException) {
            // User doesn't exist, we can proceed
            dataProvider.add(newUser)
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
}


