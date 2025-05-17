package org.example.logic.usecase.auth

import org.example.logic.repository.AuthenticationRepository
import domain.utils.exception.PlanMateException

class LoginUseCase(
    private val authRepository: AuthenticationRepository
) {
    suspend operator fun invoke(username: String, password: String) {
        if (username.isBlank()) throw PlanMateException.ValidationException("Username cannot be empty")

        authRepository.login(username, password)
    }
}