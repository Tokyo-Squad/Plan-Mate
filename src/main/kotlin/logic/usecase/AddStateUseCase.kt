package org.example.logic.usecase

import org.example.entity.StateEntity
import org.example.logic.repository.StateRepository

class AddStateUseCase(
    private val stateRepository: StateRepository
) {
    operator fun invoke(state: StateEntity): Result<String> =
        try {
            Result.success(stateRepository.addState(state))
        } catch (e: Exception) {
            Result.failure(e)
        }

}