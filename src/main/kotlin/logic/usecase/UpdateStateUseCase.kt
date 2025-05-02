package org.example.logic.usecase

import org.example.entity.StateEntity
import org.example.logic.repository.StateRepository
import org.example.utils.PlanMatException

class UpdateStateUseCase(
    private val stateRepository: StateRepository
) {
    operator fun invoke(stateId: StateEntity, newState: StateEntity): Result<Result<StateEntity>> =
        try {
            Result.success(stateRepository.updateState(stateId, newState))
        } catch (e: PlanMatException.ItemNotFoundException) {
            Result.failure(e)
        }
}