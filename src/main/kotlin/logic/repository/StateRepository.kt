package org.example.logic.repository

import org.example.entity.StateEntity

interface StateRepository {
    fun createState(state: StateEntity): String
    fun updateState(stateId: StateEntity, newState: StateEntity)
    fun deleteState(stateId: StateEntity): Boolean
    fun ensureStateCsvExists(filePath: String) : Boolean
}