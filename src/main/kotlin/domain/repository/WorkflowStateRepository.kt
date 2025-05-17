package org.example.logic.repository

import domain.model.WorkflowState
import java.util.*

interface WorkflowStateRepository {
    suspend fun addState(workflowState: WorkflowState)
    suspend fun updateState(stateId: UUID, newWorkflowState: WorkflowState): WorkflowState
    suspend fun deleteState(stateId: UUID)
    suspend fun getStateById(stateId: UUID): WorkflowState
    suspend fun getByProjectId(projectId: UUID): List<WorkflowState>
}