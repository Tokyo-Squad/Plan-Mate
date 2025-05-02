package org.example.logic.usecase.auth

import org.example.entity.UserEntity
import org.example.entity.UserType
import org.example.logic.repository.AuthenticationRepository
import org.example.utils.PlanMatException

class RegisterUseCase(
    private val authRepository: AuthenticationRepository
) {

    operator fun invoke(newUser: UserEntity, currentUser: UserEntity): Result<Unit> {
        if (currentUser.type == UserType.MATE) {
            return Result.failure(
                PlanMatException.UserActionNotAllowedException("MATE users cannot create new users.")
            )
        }
        return authRepository.register(newUser, currentUser)
    }
}