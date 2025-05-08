package org.example.logic.usecase.user

import org.example.entity.UserEntity
import org.example.logic.repository.UserRepository
import java.util.*

class GetUserByIdUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(id: UUID): UserEntity {
        return userRepository.getUserById(id)
    }
}