package org.example.logic.usecase.auth

import org.example.entity.User
import org.example.logic.repository.AuthenticationRepository

class GetCurrentUserUseCase(
    private val authRepository: AuthenticationRepository
) {
    suspend operator fun invoke(): User = authRepository.getCurrentUser()
}