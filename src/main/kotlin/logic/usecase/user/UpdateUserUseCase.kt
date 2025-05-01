package org.example.logic.usecase.user

import org.example.entity.UserEntity
import org.example.entity.UserType
import org.example.logic.repository.UserRepository
import org.example.utils.PlanMatException

class UpdateUserUseCase(
    private val userRepository: UserRepository
) {
    operator fun invoke(user: UserEntity, currentUser: UserEntity): Result<Unit> {
        if (currentUser.type == UserType.MATE) {
            return Result.failure(PlanMatException.UserActionNotAllowedException("MATE users are not allowed to update users"))
        }
        return runCatching {
            userRepository.update(user).getOrThrow()
        }
    }
}