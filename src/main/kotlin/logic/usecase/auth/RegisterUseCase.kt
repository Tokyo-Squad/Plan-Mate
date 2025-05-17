package org.example.logic.usecase.auth

import org.example.entity.UserEntity
import org.example.entity.UserType
import org.example.logic.repository.AuthenticationRepository
import org.example.utils.PlanMateException

class RegisterUseCase(
    private val authRepository: AuthenticationRepository
) {
    suspend operator fun invoke(newUser: UserEntity, currentUser: UserEntity) {
        if (currentUser.type == UserType.MATE) {
            throw PlanMateException.UserActionNotAllowedException("MATE users cannot create new users.")
        }

        if (newUser.username.isBlank()) throw PlanMateException.ValidationException("Username cannot be empty")

        authRepository.register(newUser)
    }
}