package org.example.logic.usecase.user

import org.example.entity.UserEntity
import org.example.logic.repository.UserRepository
import java.util.*

class GetUserByIdUseCase(
    private val userRepository: UserRepository
) {
    operator fun invoke(id: UUID): Result<UserEntity> = runCatching { userRepository.getUserById(id).getOrThrow() }
}