package org.example.data.repository


import org.example.data.RemoteDataSource
import org.example.data.remote.dto.WorkflowStateDto
import org.example.data.util.exception.DatabaseException
import org.example.data.util.mapper.toStateDto
import org.example.data.util.mapper.toStateEntity
import domain.model.WorkflowState
import org.example.logic.repository.WorkflowStateRepository
import java.util.UUID

class WorkflowStateRepositoryImpl(
    private val remoteDataSource: RemoteDataSource<WorkflowStateDto>
) : WorkflowStateRepository {

    override suspend fun addState(workflowState: WorkflowState) {
        remoteDataSource.add(workflowState.toStateDto())
    }

    override suspend fun updateState(stateId: UUID, newWorkflowState: WorkflowState): WorkflowState {
        val toSave = newWorkflowState.copy(id = stateId)
        remoteDataSource.update(toSave.toStateDto())
        return toSave
    }

    override suspend fun deleteState(stateId: UUID) =
        remoteDataSource.delete(stateId)


    override suspend fun getStateById(stateId: UUID): WorkflowState =
        remoteDataSource.getById(stateId)?.toStateEntity()
            ?: throw DatabaseException.DatabaseItemNotFoundException("State with ID $stateId does not exist")


    override suspend fun getByProjectId(projectId: UUID): List<WorkflowState> =
        remoteDataSource.get().filter { it.projectId == projectId }.map { it.toStateEntity() }

}