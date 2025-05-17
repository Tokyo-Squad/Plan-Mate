package org.example.data.repository


import org.example.data.RemoteDataSource
import org.example.entity.StateEntity
import org.example.logic.repository.StateRepository
import org.example.utils.PlanMateException
import java.util.*

class StateRepositoryImpl(
    private val remoteDataSource: RemoteDataSource<StateEntity>
) : StateRepository {

    override suspend fun addState(state: StateEntity): String {
        remoteDataSource.add(state)
        return state.id.toString()
    }

    override suspend fun updateState(stateId: UUID, newState: StateEntity): StateEntity {
        val toSave = newState.copy(id = stateId)
        remoteDataSource.update(toSave)
        return toSave
    }

    override suspend fun deleteState(stateId: UUID): Boolean {
        remoteDataSource.delete(stateId)
        return true
    }

    override suspend fun getStateById(stateId: UUID): StateEntity {
        return remoteDataSource.getById(stateId)
            ?: throw PlanMateException.ItemNotFoundException("State with ID $stateId does not exist")
    }

    override suspend fun getByProjectId(projectId: UUID): List<StateEntity> {
        return remoteDataSource.get().filter { it.projectId == projectId }
    }
}