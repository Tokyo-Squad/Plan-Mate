package org.example.logic.usecase.state

import org.example.entity.StateEntity
import org.example.logic.repository.StateRepository
import org.example.utils.PlanMateException

class AddStateUseCase(
    private val stateRepository: StateRepository
) {
    suspend operator fun invoke(state: StateEntity) {
        try {
            stateRepository.addState(state)
        } catch (e: PlanMateException.FileWriteException) {
            throw e
        } catch (e: PlanMateException.DatabaseException) {
            throw e
        } catch (e: Exception) {
            throw PlanMateException.DatabaseException("An unexpected error occurred while adding the state: ${e.message}")
        }
    }
}