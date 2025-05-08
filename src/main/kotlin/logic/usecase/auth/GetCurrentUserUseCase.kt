package org.example.logic.usecase.auth

import org.example.entity.UserEntity
import org.example.logic.repository.AuthenticationRepository

class GetCurrentUserUseCase(
    private val authRepository: AuthenticationRepository
) {
    suspend operator fun invoke(): UserEntity? {
        return authRepository.getCurrentUser()
    }
}