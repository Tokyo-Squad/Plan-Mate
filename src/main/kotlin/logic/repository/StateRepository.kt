package org.example.logic.repository

import org.example.entity.StateEntity
import java.util.*

interface StateRepository{
    fun getStateById(stateId: UUID): Result<StateEntity>
}