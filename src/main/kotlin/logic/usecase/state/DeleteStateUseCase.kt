package org.example.logic.usecase.state

import org.example.entity.StateEntity
import org.example.logic.repository.StateRepository
import org.example.utils.PlanMateException

class DeleteStateUseCase(
    private val stateRepository: StateRepository
) {
    suspend operator fun invoke(stateEntity: StateEntity) {
        try {
            stateRepository.deleteState(stateEntity.id)
        } catch (e: PlanMateException.ItemNotFoundException) {
            throw e
        } catch (e: PlanMateException.DatabaseException) {
            throw e
        } catch (e: Exception) {
            throw PlanMateException.DatabaseException("An unexpected error occurred while deleting the state: ${e.message}")
        }
    }
}