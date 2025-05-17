package org.example.logic.usecase.user

import org.example.entity.User
import org.example.logic.repository.UserRepository
import domain.utils.exception.PlanMateException

class GetUserByUsernameUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(username: String): User {
        if (username.isBlank()) {
            throw PlanMateException.ValidationException("Username cannot be empty")
        }
        return userRepository.getUserByUsername(username)
    }
}