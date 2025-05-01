package org.example.logic.repository

import org.example.entity.StateEntity
import java.util.UUID

interface StateRepository {
    fun addState(state: StateEntity): String
    fun updateState(stateId: StateEntity, newState: StateEntity): StateEntity
    fun deleteState(stateId: StateEntity): Boolean
    fun getStateById(stateId: UUID): StateEntity?
}