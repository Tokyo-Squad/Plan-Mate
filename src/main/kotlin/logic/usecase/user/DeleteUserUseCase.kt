package org.example.logic.usecase.user

import org.example.entity.UserEntity
import org.example.entity.UserType
import org.example.logic.repository.UserRepository
import org.example.utils.PlanMatException
import java.util.UUID

class DeleteUserUseCase(
    private val userRepository: UserRepository
) {

    operator fun invoke(
        id: UUID,
        currentUser: UserEntity
    ): Result<Unit> {
        if (currentUser.type == UserType.MATE) {
            return Result.failure(PlanMatException.UserActionNotAllowedException("MATE users are not allowed to delete users"))
        }
        return runCatching {
            userRepository.delete(id).getOrThrow()
        }
    }
}
