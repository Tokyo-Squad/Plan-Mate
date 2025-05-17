package org.example.logic.usecase.user

import org.example.entity.User
import org.example.logic.repository.UserRepository

class GetUsersUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): List<User> {
        return userRepository.getUsers()
    }
}