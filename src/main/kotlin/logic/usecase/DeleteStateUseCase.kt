package org.example.logic.usecase

import org.example.entity.StateEntity
import org.example.logic.repository.StateRepository
import org.example.utils.PlanMatException

class DeleteStateUseCase(
    private val stateRepository: StateRepository
) {
    operator fun invoke(stateId: StateEntity): Result<Result<Boolean>> =
        try {
            Result.success(stateRepository.deleteState(stateId))
        } catch (e: PlanMatException.ItemNotFoundException) {
            Result.failure(e)
        }
}