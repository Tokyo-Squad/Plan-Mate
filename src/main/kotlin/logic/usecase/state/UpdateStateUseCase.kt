package org.example.logic.usecase.state

import org.example.entity.StateEntity
import org.example.logic.repository.StateRepository
import org.example.utils.PlanMateException

class UpdateStateUseCase(
    private val stateRepository: StateRepository
) {
    suspend operator fun invoke(stateEntity: StateEntity, newState: StateEntity): StateEntity {
        try {
            return stateRepository.updateState(stateEntity.id, newState)
        } catch (e: PlanMateException.ItemNotFoundException) {
            throw e
        } catch (e: PlanMateException.DatabaseException) {
            throw e
        } catch (e: Exception) {
            throw PlanMateException.DatabaseException("An unexpected error occurred while updating the state: ${e.message}")
        }
    }
}