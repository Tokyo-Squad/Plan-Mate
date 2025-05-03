package org.example.data.repository

import org.example.data.AuthProvider
import org.example.data.DataProvider
import org.example.entity.UserEntity
import org.example.logic.repository.AuthenticationRepository
import org.example.logic.repository.UserRepository
import org.example.utils.PlanMateException
import org.example.utils.hasher.PasswordHasher

class AuthenticationRepositoryImpl(
    private val authenticationProvider: AuthProvider,
    private val userRepository: UserRepository,
    private val dataProvider: DataProvider<UserEntity>,
    private val passwordHasher: PasswordHasher
) : AuthenticationRepository {

    override fun login(username: String, password: String): Result<Unit> {
        return try {
            val user = userRepository.getUserByUsername(username)
                .getOrElse { throw PlanMateException.ItemNotFoundException("User not found.") }

            if (!isPasswordValid(user, password)) {
                throw PlanMateException.AuthenticationException("Invalid credentials")
            }

            authenticationProvider.addCurrentUser(user)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun register(user: UserEntity, currentUser: UserEntity): Result<Unit> {
        return try {

            userRepository.getUserByUsername(user.username).onSuccess {
                throw PlanMateException.AuthenticationException("A user with that username already exists.")
            }
            val hashedUser = user.copy(password = passwordHasher.hash(user.password))
            dataProvider.add(hashedUser)

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

    private fun isPasswordValid(user: UserEntity, inputPassword: String): Boolean {
        return try {
            val hashedInput = passwordHasher.hash(inputPassword)
            user.password == hashedInput
        } catch (e: Exception) {
            false
        }
    }
}





