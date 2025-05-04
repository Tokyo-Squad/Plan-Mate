package org.example.logic.usecase.auth

import org.example.entity.UserEntity
import org.example.logic.repository.AuthenticationRepository

class LoginUseCase(
    private val authRepository: AuthenticationRepository
) {
    operator fun invoke(username: String, password: String): Result<UserEntity> {
        return authRepository.login(username, password)
    }
}