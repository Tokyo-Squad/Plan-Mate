package org.example.logic.usecase.state

import org.example.entity.StateEntity
import org.example.logic.repository.StateRepository

class UpdateStateUseCase(
    private val stateRepository: StateRepository
) {
    suspend operator fun invoke(stateEntity: StateEntity, newState: StateEntity): StateEntity {
        return stateRepository.updateState(stateEntity.id, newState)
    }
}