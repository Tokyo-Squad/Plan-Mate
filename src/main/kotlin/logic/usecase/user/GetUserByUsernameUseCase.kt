package org.example.logic.usecase.user

import org.example.entity.UserEntity
import org.example.logic.repository.UserRepository

class GetUserByUsernameUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(username: String): UserEntity {
        return userRepository.getUserByUsername(username)
    }
}