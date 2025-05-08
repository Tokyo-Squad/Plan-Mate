package org.example.logic.usecase.auth

import org.example.logic.repository.AuthenticationRepository

class LogoutUseCase(
    private val authRepository: AuthenticationRepository
) {
    suspend operator fun invoke() {
        authRepository.logout()
    }
}