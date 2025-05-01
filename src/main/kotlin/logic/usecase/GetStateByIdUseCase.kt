package org.example.logic.usecase

import org.example.entity.StateEntity
import org.example.logic.repository.StateRepository
import java.util.UUID

class GetStateByIdUseCase(
    private val stateRepository: StateRepository
) {
    operator fun invoke(stateId: UUID): Result<StateEntity?> =
        stateRepository.getStateById(stateId)
            ?.let { Result.success(it) }
            ?: Result.failure(NoSuchElementException("State with ID $stateId not found"))
}