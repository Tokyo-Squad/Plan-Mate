package org.example.logic.usecase.user

import org.example.entity.UserEntity
import org.example.logic.repository.UserRepository
import org.example.utils.PlanMateException

class GetUserByUsernameUseCase(
    private val userRepository: UserRepository
) {
    operator fun invoke(username: String): Result<UserEntity> {
        if (username.isBlank()) {
            return Result.failure(PlanMateException.ValidationException("Username cannot be empty"))
        }
        return runCatching {
            userRepository.getUserByUsername(username).getOrThrow()
        }

    }
}