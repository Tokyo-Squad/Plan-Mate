package org.example.logic.usecase.user

import org.example.entity.UserEntity
import org.example.entity.UserType
import org.example.logic.repository.UserRepository
import org.example.utils.PlanMateException

class UpdateUserUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(user: UserEntity, currentUser: UserEntity) {
        if (currentUser.type == UserType.MATE) {
            throw PlanMateException.UserActionNotAllowedException("MATE users are not allowed to update users")
        }

        userRepository.update(user)
    }
}