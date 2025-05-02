package org.example.logic.usecase

import org.example.entity.StateEntity
import org.example.logic.repository.StateRepository
import org.example.utils.PlanMatException

class AddStateUseCase(
    private val stateRepository: StateRepository
) {
    operator fun invoke(state: StateEntity): Result<Result<String>> =
        try {
            Result.success(stateRepository.addState(state))
        } catch (e: PlanMatException.FileWriteException) {
            Result.failure(e)
        }

}