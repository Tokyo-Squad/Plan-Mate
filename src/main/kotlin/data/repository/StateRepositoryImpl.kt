package org.example.data.repository

import org.example.data.DataProvider
import org.example.entity.StateEntity
import org.example.logic.repository.StateRepository
import org.example.utils.PlanMatException
import java.util.UUID

class StateRepositoryImpl(
    private val dataProvider: DataProvider<StateEntity>
): StateRepository {

    override fun addState(state: StateEntity): Result<String> {
        return try {
            dataProvider.add(state)
            Result.success(state.id.toString())
        } catch (e: PlanMatException.FileWriteException) {
            Result.failure(e)
        }
    }

    override fun updateState(stateId: StateEntity, newState: StateEntity): Result<StateEntity> {
        return try {
            val toSave = newState.copy(id = stateId.id)
            dataProvider.update(toSave)
            Result.success(toSave)
        } catch (e: PlanMatException.ItemNotFoundException) {
            Result.failure(e)
        }
    }

    override fun deleteState(stateId: StateEntity): Result<Boolean> {
        return try {
            dataProvider.delete(stateId.id)
            Result.success(true)
        } catch (e: PlanMatException.ItemNotFoundException) {
            Result.failure(e)
        }
    }

    override fun getStateById(stateId: UUID): Result<StateEntity> {
        return try {
            val existing = dataProvider.getById(stateId)
                ?: throw PlanMatException.ItemNotFoundException("State with ID $stateId does not exist")
            Result.success(existing)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}