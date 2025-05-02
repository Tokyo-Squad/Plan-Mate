package org.example.logic.usecase.user

import org.example.entity.ProjectEntity
import org.example.logic.repository.ProjectRepository
import java.util.*

class GetProjectUseCase(
    private val projectRepository: ProjectRepository
) {
    operator fun invoke(projectId: UUID): Result<ProjectEntity> {
        return projectRepository.getProjectById(projectId.toString())
    }
}