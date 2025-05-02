package org.example.logic.usecase.auth

import org.example.logic.repository.AuthenticationRepository

class LogoutUseCase(
    private val authRepository: AuthenticationRepository
) {

    operator fun invoke(): Result<Unit> {
        return Result.failure(Exception())
    }
}