package org.example.data.repository


import org.example.data.RemoteDataSource
import org.example.data.remote.dto.StateDto
import org.example.data.util.exception.DatabaseException
import org.example.data.util.mapper.toStateDto
import org.example.data.util.mapper.toStateEntity
import org.example.entity.StateEntity
import org.example.logic.repository.StateRepository
import java.util.UUID

class StateRepositoryImpl(
    private val remoteDataSource: RemoteDataSource<StateDto>
) : StateRepository {

    override suspend fun addState(state: StateEntity) {
        remoteDataSource.add(state.toStateDto())
    }

    override suspend fun updateState(stateId: UUID, newState: StateEntity): StateEntity {
        val toSave = newState.copy(id = stateId)
        remoteDataSource.update(toSave.toStateDto())
        return toSave
    }

    override suspend fun deleteState(stateId: UUID) =
        remoteDataSource.delete(stateId)


    override suspend fun getStateById(stateId: UUID): StateEntity =
        remoteDataSource.getById(stateId)?.toStateEntity()
            ?: throw DatabaseException.DatabaseItemNotFoundException("State with ID $stateId does not exist")


    override suspend fun getByProjectId(projectId: UUID): List<StateEntity> =
        remoteDataSource.get().filter { it.projectId == projectId }.map { it.toStateEntity() }

}