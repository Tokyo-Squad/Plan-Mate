package org.example.logic.usecase.user

import org.example.entity.UserEntity
import org.example.logic.repository.UserRepository
import org.example.utils.PlanMateException

class GetUserByUsernameUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(username: String): UserEntity {
        // Input validation
        if (username.isBlank()) {
            throw PlanMateException.ValidationException("Username cannot be empty")
        }

        return userRepository.getUserByUsername(username)
    }
}