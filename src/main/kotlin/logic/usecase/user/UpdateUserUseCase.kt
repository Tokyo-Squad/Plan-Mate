package org.example.logic.usecase.user

import org.example.entity.User
import org.example.entity.UserType
import org.example.logic.repository.UserRepository
import org.example.utils.PlanMateException

class UpdateUserUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(user: User, currentUser: User): User {

        if (currentUser.type != UserType.ADMIN) {
            throw PlanMateException.UserActionNotAllowedException(
                "${currentUser.type} users are not allowed to update users"
            )
        }

        validationInput(user)
        return userRepository.update(user)
    }

    private fun validationInput(user: User) {
        if (user.username.isBlank()) throw PlanMateException.ValidationException("Username cannot be empty")

        if (user.password.isBlank()) throw PlanMateException.ValidationException("Password cannot be empty")
    }
}