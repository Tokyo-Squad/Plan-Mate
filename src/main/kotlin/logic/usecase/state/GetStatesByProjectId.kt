package org.example.logic.usecase.state

import org.example.entity.StateEntity
import org.example.logic.repository.StateRepository
import java.util.*

class GetStatesByProjectId(
    private val stateRepository: StateRepository
) {
    suspend operator fun invoke(projectID: UUID): List<StateEntity> {
        return stateRepository.getByProjectId(projectID)
    }
}