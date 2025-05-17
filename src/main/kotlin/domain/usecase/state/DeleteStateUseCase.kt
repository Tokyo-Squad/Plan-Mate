package org.example.logic.usecase.state

import domain.model.WorkflowState
import org.example.logic.repository.WorkflowStateRepository

class DeleteStateUseCase(
    private val workflowStateRepository: WorkflowStateRepository
) {
    suspend operator fun invoke(workflowState: WorkflowState)= workflowStateRepository.deleteState(workflowState.id)
}