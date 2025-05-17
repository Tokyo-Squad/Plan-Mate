package org.example.logic.usecase.state

import domain.model.WorkflowState
import org.example.logic.repository.WorkflowStateRepository
import java.util.*

class GetStateByIdUseCase(
    private val workflowStateRepository: WorkflowStateRepository
) {
    suspend operator fun invoke(stateId: UUID): WorkflowState {
        return workflowStateRepository.getStateById(stateId)
    }
}