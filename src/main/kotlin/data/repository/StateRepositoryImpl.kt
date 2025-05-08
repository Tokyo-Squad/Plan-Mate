package org.example.data.repository


import org.example.data.DataProvider
import org.example.entity.StateEntity
import org.example.logic.repository.StateRepository
import org.example.utils.PlanMateException
import java.util.*

class StateRepositoryImpl(
    private val dataProvider: DataProvider<StateEntity>
) : StateRepository {

    override suspend fun addState(state: StateEntity): String {
        dataProvider.add(state)
        return state.id.toString()
    }

    override suspend fun updateState(stateId: UUID, newState: StateEntity): StateEntity {
        val toSave = newState.copy(id = stateId)
        dataProvider.update(toSave)
        return toSave
    }

    override suspend fun deleteState(stateId: UUID): Boolean {
        dataProvider.delete(stateId)
        return true
    }

    override suspend fun getStateById(stateId: UUID): StateEntity {
        return dataProvider.getById(stateId)
            ?: throw PlanMateException.ItemNotFoundException("State with ID $stateId does not exist")
    }

    override suspend fun getByProjectId(projectId: UUID): List<StateEntity> {
        return dataProvider.get().filter { it.projectId == projectId }
    }
}