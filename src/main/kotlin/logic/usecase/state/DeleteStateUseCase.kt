package org.example.logic.usecase.state

import org.example.entity.StateEntity
import org.example.logic.repository.StateRepository

class DeleteStateUseCase(
    private val stateRepository: StateRepository
) {
    suspend operator fun invoke(stateEntity: StateEntity) {
        stateRepository.deleteState(stateEntity.id)
    }
}