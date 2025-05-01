package org.example.logic.usecase.user

import org.example.entity.UserEntity
import org.example.logic.repository.UserRepository

class GetUsersUseCase(
    private val userRepository: UserRepository
) {
    operator fun invoke(): Result<List<UserEntity>> = runCatching { userRepository.getUsers().getOrThrow() }
}