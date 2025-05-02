package org.example.logic.repository

import org.example.entity.StateEntity
import java.util.UUID

interface StateRepository {
    fun addState(state: StateEntity): Result<String>
    fun updateState(stateId: StateEntity, newState: StateEntity): Result<StateEntity>
    fun deleteState(stateId: StateEntity): Result<Boolean>
    fun getStateById(stateId: UUID): Result<StateEntity>
}