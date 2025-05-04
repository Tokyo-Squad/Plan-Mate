package org.example.logic.usecase.state

import org.example.entity.StateEntity
import org.example.logic.repository.StateRepository
import org.example.utils.PlanMateException
import java.util.UUID

class GetStateByIdUseCase(
    private val stateRepository: StateRepository
) {
    operator fun invoke(stateId: UUID): Result<StateEntity> {
        return try {
            stateRepository.getStateById(stateId)
        } catch (e: PlanMateException.ItemNotFoundException) {
            Result.failure(e)
        }
    }
}