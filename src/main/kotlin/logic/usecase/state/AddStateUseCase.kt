package org.example.logic.usecase.state

import org.example.entity.StateEntity
import org.example.logic.repository.StateRepository
import org.example.utils.PlanMateException

class AddStateUseCase(
    private val stateRepository: StateRepository
) {
    operator fun invoke(state: StateEntity): Result<Result<String>> =
        try {
            Result.success(stateRepository.addState(state))
        } catch (e: PlanMateException.FileWriteException) {
            Result.failure(e)
        }

}