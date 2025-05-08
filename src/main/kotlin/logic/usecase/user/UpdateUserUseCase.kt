package org.example.logic.usecase.user

import org.example.entity.UserEntity
import org.example.entity.UserType
import org.example.logic.repository.UserRepository
import org.example.utils.PlanMateException

class UpdateUserUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(user: UserEntity, currentUser: UserEntity) {
        // Authorization check
        if (currentUser.type != UserType.ADMIN) {
            throw PlanMateException.UserActionNotAllowedException(
                "${currentUser.type} users are not allowed to update users"
            )
        }

        // Data validation
        if (user.username.isBlank()) {
            throw PlanMateException.ValidationException("Username cannot be empty")
        }

        // Additional validations as needed
        if (user.password.isBlank()) {
            throw PlanMateException.ValidationException("Password cannot be empty")
        }

        userRepository.update(user)
    }
}