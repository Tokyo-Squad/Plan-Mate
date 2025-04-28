package org.example.data.repository

import org.example.data.DataProvider
import org.example.entity.StateEntity
import org.example.logic.repository.StateRepository

class StateRepositoryImpl(
    private val dataProvider: DataProvider<StateEntity>
): StateRepository {
    override fun createState(state: StateEntity): String {
        TODO("Not yet implemented")
    }

    override fun updateState(stateId: StateEntity, newState: StateEntity) {
        TODO("Not yet implemented")
    }

    override fun deleteState(stateId: StateEntity): Boolean {
        TODO("Not yet implemented")
    }

    override fun ensureStateCsvExists(filePath: String): Boolean {
        TODO("Not yet implemented")
    }
}