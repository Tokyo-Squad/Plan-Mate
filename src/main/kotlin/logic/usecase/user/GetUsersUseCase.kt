package org.example.logic.usecase.user

import org.example.entity.UserEntity
import org.example.logic.repository.UserRepository

class GetUsersUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): List<UserEntity> {
        return userRepository.getUsers()
    }
}