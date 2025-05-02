package org.example.logic.usecase

import org.example.entity.StateEntity
import org.example.logic.repository.StateRepository
import org.example.utils.PlanMatException
import java.util.UUID

class GetStateByIdUseCase(
    private val stateRepository: StateRepository
) {
    operator fun invoke(stateId: UUID): Result<StateEntity> {
        return try {
            stateRepository.getStateById(stateId)
        } catch (e: PlanMatException.ItemNotFoundException) {
            Result.failure(e)
        }
    }
}