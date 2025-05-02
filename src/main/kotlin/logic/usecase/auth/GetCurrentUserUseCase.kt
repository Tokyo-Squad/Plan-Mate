package org.example.logic.usecase.auth

import org.example.entity.UserEntity
import org.example.logic.repository.AuthenticationRepository

class GetCurrentUserUseCase(
    private val authRepository: AuthenticationRepository
) {

    operator fun invoke(): Result<UserEntity?> {
        return Result.failure(Exception())
    }
}