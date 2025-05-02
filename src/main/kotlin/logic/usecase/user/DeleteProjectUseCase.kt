package org.example.logic.usecase.user

import org.example.logic.repository.ProjectRepository
import java.util.*

class DeleteProjectUseCase(
    private val projectRepository: ProjectRepository,
) {
    operator fun invoke(
        projectId: UUID,
        currentUser: UUID
    ): Result<Unit> {
        return projectRepository.getProjectById(projectId.toString())
            .mapCatching {
                projectRepository.deleteProject(projectId, currentUser)
            }
    }
}