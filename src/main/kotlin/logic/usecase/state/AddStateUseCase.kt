package org.example.logic.usecase.state

import org.example.entity.StateEntity
import org.example.logic.repository.StateRepository

class AddStateUseCase(
    private val stateRepository: StateRepository
) {
    suspend operator fun invoke(state: StateEntity) {
        stateRepository.addState(state)
    }
}