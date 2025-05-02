package org.example.data.repository

import org.example.entity.StateEntity
import org.example.logic.repository.StateRepository
import java.util.*

class StateRepositoryImpl: StateRepository {
    override fun getStateById(stateId: UUID): Result<StateEntity> {
     return Result.failure(Exception())
    }
}