package org.example.logic.usecase.state

import org.example.entity.StateEntity
import org.example.logic.repository.StateRepository
import java.util.*

class GetStateByIdUseCase(
    private val stateRepository: StateRepository
) {
    suspend operator fun invoke(stateId: UUID): StateEntity {
        return stateRepository.getStateById(stateId)
    }
}