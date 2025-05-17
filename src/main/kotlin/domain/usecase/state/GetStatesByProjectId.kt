package org.example.logic.usecase.state

import domain.model.WorkflowState
import org.example.logic.repository.WorkflowStateRepository
import java.util.*

class GetStatesByProjectId(
    private val workflowStateRepository: WorkflowStateRepository
) {
    suspend operator fun invoke(projectID: UUID): List<WorkflowState> {
        return workflowStateRepository.getByProjectId(projectID)
    }
}