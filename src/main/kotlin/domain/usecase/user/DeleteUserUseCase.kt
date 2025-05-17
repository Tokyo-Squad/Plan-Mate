package org.example.logic.usecase.user

import org.example.entity.User
import org.example.entity.UserType
import org.example.logic.repository.UserRepository
import org.example.utils.PlanMateException
import java.util.*

class DeleteUserUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(id: UUID, currentUser: User) {
        if (currentUser.type == UserType.MATE) {
            throw PlanMateException.UserActionNotAllowedException("MATE users are not allowed to delete users")
        }

        userRepository.delete(id)
    }
}