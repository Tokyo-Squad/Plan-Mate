package org.example.logic.usecase

import org.example.entity.StateEntity
import org.example.logic.repository.StateRepository

class DeleteStateUseCase(
    private val stateRepository: StateRepository
) {
    operator fun invoke(stateId: StateEntity): Result<Boolean> =
        try {
            Result.success(stateRepository.deleteState(stateId))
        } catch (e: Exception) {
            Result.failure(e)
        }
}