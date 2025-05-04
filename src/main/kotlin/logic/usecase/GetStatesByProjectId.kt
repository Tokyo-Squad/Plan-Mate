package org.example.logic.usecase

import org.example.entity.StateEntity
import org.example.logic.repository.StateRepository
import java.util.*

class GetStatesByProjectId(
    private val stateRepository: StateRepository
) {
    operator fun invoke(projectID: UUID): Result<List<StateEntity>> {
        return runCatching { stateRepository.getByProjectId(projectID).getOrThrow() }
    }
}