package org.example.data.repository

import org.example.data.DataProvider
import org.example.entity.StateEntity
import org.example.logic.repository.StateRepository
import java.util.UUID

class StateRepositoryImpl(
    private val dataProvider: DataProvider<StateEntity>
): StateRepository {
    override fun addState(state: StateEntity): String {
        dataProvider.add(state)
        return state.id.toString()
    }

    override fun updateState(stateId: StateEntity, newState: StateEntity) : StateEntity {
        val existingState = dataProvider.getById(stateId.id)
            ?: throw IllegalArgumentException("State with ID ${stateId.id} does not exist")
        val toSave = newState.copy(id = existingState.id)
        dataProvider.update(toSave)
        return toSave
    }

    override fun deleteState(stateId: StateEntity): Boolean {
        val existingState = dataProvider.getById(stateId.id)
        return if (existingState != null) {
            dataProvider.delete(stateId.id)
            true
        }else {
            throw IllegalArgumentException("State with ID ${stateId.id} does not exist")
            false
        }
    }

    override fun getStateById(stateId: UUID): StateEntity? {
        val uuid = UUID.nameUUIDFromBytes(stateId.toString().toByteArray())
        val existingState = dataProvider.getById(uuid)
        return if (existingState != null) {
            existingState
        }else {
            throw IllegalArgumentException("State with ID $stateId does not exist")
            null
        }
    }
}