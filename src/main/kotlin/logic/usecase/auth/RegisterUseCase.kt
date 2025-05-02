package org.example.logic.usecase.auth

import org.example.entity.UserEntity
import org.example.logic.repository.AuthenticationRepository

class RegisterUseCase(
    private val authRepository: AuthenticationRepository
) {

    operator fun invoke(newUser: UserEntity, currentUser: UserEntity): Result<Unit> {
        return Result.failure(Exception())
    }
}