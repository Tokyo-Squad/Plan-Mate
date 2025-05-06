package org.example.logic.usecase.state

import org.example.entity.StateEntity
import org.example.logic.repository.StateRepository
import org.example.utils.PlanMateException
import java.util.UUID

class GetStateByIdUseCase(
    private val stateRepository: StateRepository
) {
    suspend operator fun invoke(stateId: UUID): StateEntity {
        try {
            return stateRepository.getStateById(stateId)
        } catch (e: PlanMateException.ItemNotFoundException) {
            throw e
        } catch (e: PlanMateException.DatabaseException) {
            throw e
        } catch (e: Exception) {
            throw PlanMateException.DatabaseException("An unexpected error occurred while retrieving the state: ${e.message}")
        }
    }
}