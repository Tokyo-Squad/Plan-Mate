package org.example.logic.usecase.state

import logic.model.WorkflowState
import org.example.logic.repository.WorkflowStateRepository

class AddStateUseCase(
    private val workflowStateRepository: WorkflowStateRepository
) {
    suspend operator fun invoke(workflowState: WorkflowState) {
        workflowStateRepository.addState(workflowState)
    }
}