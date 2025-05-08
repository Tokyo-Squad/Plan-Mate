package org.example.logic.usecase.project

import org.example.logic.repository.ProjectRepository
import java.util.*

class DeleteProjectUseCase(
    private val projectRepository: ProjectRepository,
) {
    suspend operator fun invoke(
        projectId: UUID,
        currentUser: UUID
    ) {
        projectRepository.getProjectById(projectId.toString())
        projectRepository.deleteProject(projectId, currentUser)
    }
}