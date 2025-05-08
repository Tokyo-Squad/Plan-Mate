package org.example.logic.repository

import org.example.entity.StateEntity
import java.util.*

interface StateRepository {
    suspend fun addState(state: StateEntity): String
    suspend fun updateState(stateId: UUID, newState: StateEntity): StateEntity
    suspend fun deleteState(stateId: UUID): Boolean
    suspend fun getStateById(stateId: UUID): StateEntity
    suspend fun getByProjectId(projectId: UUID): List<StateEntity>
}