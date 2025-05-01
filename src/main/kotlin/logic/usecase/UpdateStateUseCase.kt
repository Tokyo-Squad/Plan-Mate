package org.example.logic.usecase

import org.example.entity.StateEntity
import org.example.logic.repository.StateRepository

class UpdateStateUseCase(
    private val stateRepository: StateRepository
) {
    operator fun invoke(stateId: StateEntity, newState: StateEntity): Result<StateEntity> =
        try {
            Result.success(stateRepository.updateState(stateId, newState))
        } catch (e: Exception) {
            Result.failure(e)
        }
}