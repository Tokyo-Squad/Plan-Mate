package org.example.data.repository

import org.example.data.AuthProvider
import org.example.data.DataProvider
import org.example.entity.UserEntity
import org.example.logic.repository.AuthenticationRepository
import org.example.logic.repository.UserRepository
import org.example.utils.PlanMatException

class AuthenticationRepositoryImpl(
    private val authenticationProvider: AuthProvider,
    private val userRepository: UserRepository,
    private val dataProvider: DataProvider<UserEntity>
) : AuthenticationRepository {

    override fun login(username: String, password: String): Result<Unit> {
        return try {
            val user = userRepository.getUserByUsername(username)
                .getOrElse { throw PlanMatException.ItemNotFoundException("User not found.") }

            if (user.password != password) {
                throw PlanMatException.ValidationException("Password is not correct.")
            }

            authenticationProvider.addCurrentUser(user)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun register(newUser: UserEntity, currentUser: UserEntity): Result<Unit> {
        return try {

            userRepository.getUserByUsername(newUser.username).onSuccess {
                throw PlanMatException.ValidationException("A user with that username already exists.")
            }

            dataProvider.add(newUser)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun logout(): Result<Unit> {
        return try {
            authenticationProvider.deleteCurrentUser()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getCurrentUser(): Result<UserEntity?> {
        return try {
            Result.success(authenticationProvider.getCurrentUser())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}





