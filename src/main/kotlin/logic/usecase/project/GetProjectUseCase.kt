package org.example.logic.usecase.project

import logic.model.Project
import org.example.logic.repository.ProjectRepository
import java.util.*

class GetProjectUseCase(
    private val projectRepository: ProjectRepository
) {
    suspend operator fun invoke(projectId: UUID): Project {
        return projectRepository.getProjectById(projectId)
    }
}