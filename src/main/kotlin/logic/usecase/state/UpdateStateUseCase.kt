package org.example.logic.usecase.state

import logic.model.WorkflowState
import org.example.logic.repository.WorkflowStateRepository

class UpdateStateUseCase(
    private val workflowStateRepository: WorkflowStateRepository
) {
    suspend operator fun invoke(workflowState: WorkflowState, newWorkflowState: WorkflowState): WorkflowState {
        return workflowStateRepository.updateState(workflowState.id, newWorkflowState)
    }
}