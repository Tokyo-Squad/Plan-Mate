package org.example.logic.usecase.state

import org.example.entity.StateEntity
import org.example.logic.repository.StateRepository
import org.example.utils.PlanMateException

class UpdateStateUseCase(
    private val stateRepository: StateRepository
) {
    operator fun invoke(stateId: StateEntity, newState: StateEntity): Result<Result<StateEntity>> =
        try {
            Result.success(stateRepository.updateState(stateId, newState))
        } catch (e: PlanMateException.ItemNotFoundException) {
            Result.failure(e)
        }
}